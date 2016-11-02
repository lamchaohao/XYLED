package cn.com.hotled.xyled.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.adapter.TextButtonAdapter;
import cn.com.hotled.xyled.adapter.TypefaceListAdapter;
import cn.com.hotled.xyled.bean.TextButton;
import cn.com.hotled.xyled.view.WheelView;


public class TextFragment extends Fragment {

//    @BindView(R.id.bt_fgText_TextSize)
//    Button bt_TextSize;
//    @BindView(R.id.bt_fgText_Font)
//    Button bt_setFont;
//    @BindView(R.id.ib_fgText_setBold)
//    ImageButton ib_setBold;
//    @BindView(R.id.ib_fgText_setItalic)
//    ImageButton ib_setItalic;
//    @BindView(R.id.ib_fgText_setUnderLine)
//    ImageButton ib_setUnderLine;
//    @BindView(R.id.ib_fgText_textColorRed)
//    ImageButton ib_RedText;
//    @BindView(R.id.ib_fgText_textColorGreen)
//    ImageButton ib_GreenText;
//    @BindView(R.id.ib_fgText_textColorBlue)
//    ImageButton ib_BlueText;
//    @BindView(R.id.ib_fgText_textColorMore)
//    ImageButton ib_moreColor;
//    @BindView(R.id.ib_fgText_textBgRed)
//    ImageButton ib_RedTextBg;
//    @BindView(R.id.ib_fgText_textBgGreen)
//    ImageButton ib_GreenTextBg;
//    @BindView(R.id.ib_fgText_textBgBlue)
//    ImageButton ib_BlueTextBg;
//    @BindView(R.id.ib_fgText_textBgMore)
//    ImageButton ib_MoreTextBg;
//    @BindView(R.id.sb_fgText_X)
//    SeekBar sb_X;
//    @BindView(R.id.sb_fgText_Y)
//    SeekBar sb_Y;
//    @BindView(R.id.tv_fgText_Xprogress)
//    TextView tv_Xprogress;
//    @BindView(R.id.tv_fgText_Yprogress)
//    TextView tv_Yprogress;
    @BindView(R.id.et_fgText_input)
    EditText et_input;
    @BindView(R.id.rv_fgText_showResult)
    RecyclerView recyclerView;
    @BindView(R.id.ib_fgText_settool)
    ImageButton ib_fgText_settool;
    Button mButton;
    List<TextButton> mTextButtonList=new ArrayList<>();
    private TextButtonAdapter textButtonAdapter;
    private TextButton mTextButton;
    private PopupWindow mPopupWindow;
    private LinearLayout ll_fgText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_text, container, false);
        ll_fgText = (LinearLayout) inflate.findViewById(R.id.ll_fgText);
        ButterKnife.bind(this,inflate);
        initRecyclerView();
        setEditorListener();
//        initSeekBar();
        return inflate;
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),10, LinearLayoutManager.VERTICAL,false));
        textButtonAdapter = new TextButtonAdapter(getContext(),mTextButtonList);
        textButtonAdapter.setItemOnClickListener(new TextButtonAdapter.OnItemOnClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (mTextButton!=null)
                    mTextButton.setSelected(false);//把之前选中的取消选中
                mButton= (Button) view;
                mTextButton = mTextButtonList.get(position);
                mTextButton.setSelected(true);
            }
        });
        recyclerView.setAdapter(textButtonAdapter);
    }

    private void setEditorListener() {

        et_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //start 在哪个位置开始输入,开始的位置
                //after 在被改变的旧内容的基础上，新内容的数量，增加的数量
                //count 被改变的旧内容数
                //这里的s表示改变之前的内容，通常start和count组合，可以在s中读取本次改变字段中被改变的内容。而after表示改变后新的内容的数量。
                //先移除buttonlist中的button
            if (count!=0){
                    for (int i=count;i>0;i--){
                        mTextButtonList.remove(start+i-1);
                    }
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //start  在哪个位置开始输入 开始的位置
                //count  新增数
                //before 改变前的内容数量，by lam 也可称为被改变的数量
                //这里的s 表示改变之后的内容，通常start和count组合，可以在s中读取本次改变字段中新的内容。而before表示被改变的内容的数量。
                //获取到新增的字符串
                String substring = s.toString().substring(start, start + count);
                for (int i=substring.length();i>0;i--){
                    //将其分割，取出每个字符
                    String subStr = substring.substring(i-1, i);
                    //每个字符，对应一个按钮，加入buttonlist
                    //// TODO: 2016/10/31 buttonTextList 展示出来是倒叙的，最后一个放在最前面 2016/11/1 已解决
                    TextButton tb=new TextButton(subStr,16,Color.BLACK,Color.WHITE,false,false,false);
                    mTextButtonList.add(start,tb);

                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                textButtonAdapter.notifyDataSetChanged();
            }
        });



    }

//    private void initSeekBar() {
//        sb_X.setProgress(50);
//        sb_Y.setProgress(50);
//        tv_Xprogress.setText(0+"");
//        tv_Yprogress.setText(0+"");
//        sb_X.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                tv_Xprogress.setText(progress-50+"");
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });
//        sb_Y.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                tv_Yprogress.setText(progress-50+"");
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });
//    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ButterKnife.bind(getActivity());
    }

//    @OnClick(R.id.ib_fgText_setBold)
//    public void setBold(){
//        ImageFragment imageFragment = new ImageFragment();
//        getFragmentManager().beginTransaction().replace(R.id.ll_test,imageFragment,"imageFragment").show(imageFragment).commit();
//    }
//    @OnClick(R.id.ib_fgText_setItalic)
//    public void setItalic(){
//
//    }
//    @OnClick(R.id.ib_fgText_setUnderLine)
//    public void setUnderLine(){
//
//    }
//    @OnClick(R.id.ib_fgText_textColorRed)
//    public void setTextColorRed(){
//        setTextColor(Color.RED);
//    }
//    @OnClick(R.id.ib_fgText_textColorGreen)
//    public void setTextColorGreen(){
//        setTextColor(Color.GREEN);
//    }
//    @OnClick(R.id.ib_fgText_textColorBlue)
//    public void setTextColorBlue(){
//        setTextColor(Color.BLUE);
//    }
//    @OnClick(R.id.ib_fgText_textColorMore)
//    public void setTextColorMore(){
//        // TODO: 2016/10/26
//        ColorPickerDialogBuilder.with(getContext())
//                .setTitle("选择字体颜色")
//                .initialColor(Color.RED)
//                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
//                .density(12)
//                .setOnColorSelectedListener(new OnColorSelectedListener() {
//                    @Override
//                    public void onColorSelected(int i) {
//                        Toast.makeText(getContext(), "ColorSelected:"+Integer.toHexString(i), Toast.LENGTH_SHORT).show();
//                        setTextColor(i);
//                    }
//                })
//                .setPositiveButton("确定", new ColorPickerClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i, Integer[] integers) {
//
//
//                    }
//                })
//                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                })
//                .build().show();
//
//    }
//    @OnClick(R.id.ib_fgText_textBgRed)
//    public void setTextBgRed(){
//        setTextBackgroud(Color.RED);
//    }
//    @OnClick(R.id.ib_fgText_textBgGreen)
//    public void setTextBgGreen(){
//        setTextBackgroud(Color.GREEN);
//    }
//    @OnClick(R.id.ib_fgText_textBgBlue)
//    public void setTextBgBlue(){
//        setTextBackgroud(Color.BLUE);
//    }
//    @OnClick(R.id.ib_fgText_textBgMore)
//    public void setTextBgMore(){
//        // TODO: 2016/10/26
//        ColorPickerDialogBuilder.with(getContext())
//                .setTitle("选择字体背景颜色")
//                .initialColor(Color.RED)
//                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
//                .density(12)
//                .setOnColorSelectedListener(new OnColorSelectedListener() {
//                    @Override
//                    public void onColorSelected(int i) {
//                        Toast.makeText(getContext(), "ColorSelected:"+Integer.toHexString(i), Toast.LENGTH_SHORT).show();
//                        setTextBackgroud(i);//设置字体背景颜色
//                    }
//                })
//                .setPositiveButton("确定", new ColorPickerClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i, Integer[] integers) {
//
//                    }
//                })
//                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                })
//                .build().show();
//    }
//
//    @OnClick(R.id.bt_fgText_TextSize)
//    public void setTextSize(){
//        String[] textSizePxList=new String[100];
//        for (int i=0;i<100;i++){
//            textSizePxList[i]=i+1+"";
//        }
//
//        View outerView = LayoutInflater.from(getContext()).inflate(R.layout.wheel_view, null);
//        WheelView wv = (WheelView) outerView.findViewById(R.id.wheelview);
//        wv.setOffset(2);
//        wv.setItems(Arrays.asList(textSizePxList));
//        wv.setSeletion(3);
//        wv.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
//            @Override
//            public void onSelected(int selectedIndex, String item) {
//                    bt_TextSize.setText("size:"+item);
//                    mButton.setTextSize(selectedIndex+1);
//                    mTextButton.setTextSize(selectedIndex+1);
//                Log.i("textFragm",selectedIndex+"");
//            }
//        });
//
//        new AlertDialog.Builder(getContext())
//                .setTitle("WheelView in Dialog")
//                .setView(outerView)
//                .setPositiveButton("OK", null)
//                .show();
//
//    }
//
//    /**
//     * 设置文本字体
//     */
//    @OnClick(R.id.bt_fgText_Font)
//    public void setFont(){
//        File file =new File("/system/fonts");
//        if (file.exists()){
//            Toast.makeText(getContext(), "exists", Toast.LENGTH_SHORT).show();
//        }
//        File[] files = file.listFiles();
//        for (int i=0;i<files.length;i++){
//            Log.i("textFragm",files[i].getName().toString());
//        }
//        View view = LayoutInflater.from(getContext()).inflate(R.layout.typeface_list, null);
//        ListView typeFaceListView = (ListView) view.findViewById(R.id.typeFaceListView);
//        typeFaceListView.setAdapter(new TypefaceListAdapter(files,getContext()));
//        new AlertDialog.Builder(getContext())
//                .setTitle("Choose Typeface")
//                .setView(view)
//                .setPositiveButton("OK", null)
//                .show();
//
//    }
//
//    /**
//     * 设置字体背景颜色
//     * @param color
//     */
//    private void setTextBackgroud(int color){
//        mButton.setBackgroundColor(color);
//        mTextButton.setTextBackgroudColor(color);
//    }
//
//    /**
//     * 设置字体颜色
//     * @param color
//     */
//    private void setTextColor(int color){
//        mButton.setTextColor(color);
//        mTextButton.setTextColor(color);
//    }


    @OnClick(R.id.ib_fgText_settool)
    public void setToolBox(){
//        int visibility = sv_fgText_tool.getVisibility();
//        if (visibility==View.VISIBLE){
//            sv_fgText_tool.setVisibility(View.GONE);
//        }else{
//            sv_fgText_tool.setVisibility(View.VISIBLE);
//        }
        if (mPopupWindow==null||mPopupWindow.isShowing()){
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View inflate = layoutInflater.inflate(R.layout.popupwind_textfragment, null);
            mPopupWindow=new PopupWindow(inflate, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            mPopupWindow.showAtLocation(ll_fgText, Gravity.BOTTOM,0,0);
        }else if (mPopupWindow!=null&&mPopupWindow.isShowing()){
            mPopupWindow.dismiss();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
