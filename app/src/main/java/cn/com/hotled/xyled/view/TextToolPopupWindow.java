package cn.com.hotled.xyled.view;


import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.adapter.TextButtonAdapter;
import cn.com.hotled.xyled.adapter.TypefaceAdapter;
import cn.com.hotled.xyled.bean.TextButton;
import cn.com.hotled.xyled.bean.TypefaceFile;
import cn.com.hotled.xyled.decoration.WifiItemDecoration;
import cn.com.hotled.xyled.fragment.TextFragment;
import cn.com.hotled.xyled.ui.TypefaceListActivity;

/**
 * Created by Lam on 2016/11/3.
 */

public class TextToolPopupWindow extends PopupWindow implements View.OnClickListener{
    Button bt_TextSize;
    Button bt_setFont;
    ImageButton ib_setBold;
    ImageButton ib_setItalic;
    ImageButton ib_setUnderLine;
    ImageButton ib_RedText;
    ImageButton ib_GreenText;
    ImageButton ib_BlueText;
    ImageButton ib_moreColor;
    ImageButton ib_RedTextBg;
    ImageButton ib_GreenTextBg;
    ImageButton ib_BlueTextBg;
    ImageButton ib_MoreTextBg;

    Context mContext;
    private ImageButton ib_trainX;
    private ImageButton ib_trainY;
    List<TextButton> mTextButtonList;
    TextFragment mTextFragment;
    private TypefaceAdapter typefaceAdapter;
    private File typeFile;

    public TextToolPopupWindow(Context context, List<TextButton> textButtons, TextFragment textFragment) {
        super(context);
        mContext=context;
        mTextButtonList=textButtons;
        mTextFragment=textFragment;
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View inflate = layoutInflater.inflate(R.layout.popupwin_textfg_horizon_double, null);
        initView(inflate);
        setScrollBar(inflate);
        setContentView(inflate);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FAFAFA")));
        setAnimationStyle(R.style.popupwin_anim);
    }

    @TargetApi(16)
    private void setScrollBar(View inflate) {
        inflate.setScrollBarSize(5);
    }

    private void initView(View inflate) {
        bt_TextSize= (Button) inflate.findViewById(R.id.bt_fgText_textSize);
        bt_setFont= (Button) inflate.findViewById(R.id.bt_fgText_font);
        ib_setBold= (ImageButton) inflate.findViewById(R.id.ib_fgText_setBold);
        ib_setItalic= (ImageButton) inflate.findViewById(R.id.ib_fgText_setItalic);
        ib_setUnderLine= (ImageButton) inflate.findViewById(R.id.ib_fgText_setUnderLine);
        ib_RedText= (ImageButton) inflate.findViewById(R.id.ib_fgText_textColorRed);
        ib_GreenText= (ImageButton) inflate.findViewById(R.id.ib_fgText_textColorGreen);
        ib_BlueText= (ImageButton) inflate.findViewById(R.id.ib_fgText_textColorBlue);
        ib_moreColor= (ImageButton) inflate.findViewById(R.id.ib_fgText_textColorMore);
        ib_RedTextBg= (ImageButton) inflate.findViewById(R.id.ib_fgText_textBgRed);
        ib_GreenTextBg= (ImageButton) inflate.findViewById(R.id.ib_fgText_textBgGreen);
        ib_BlueTextBg= (ImageButton) inflate.findViewById(R.id.ib_fgText_textBgBlue);
        ib_MoreTextBg= (ImageButton) inflate.findViewById(R.id.ib_fgText_textBgMore);
        ib_trainX = (ImageButton) inflate.findViewById(R.id.ib_fgText_trainX);
        ib_trainY = (ImageButton) inflate.findViewById(R.id.ib_fgText_trainY);

        bt_TextSize.setOnClickListener(this);
        bt_setFont.setOnClickListener(this);
        ib_setBold.setOnClickListener(this);
        ib_setItalic.setOnClickListener(this);
        ib_RedText.setOnClickListener(this);
        ib_GreenText.setOnClickListener(this);
        ib_BlueText.setOnClickListener(this);
        ib_moreColor.setOnClickListener(this);
        ib_RedTextBg.setOnClickListener(this);
        ib_GreenTextBg.setOnClickListener(this);
        ib_BlueTextBg.setOnClickListener(this);
        ib_MoreTextBg.setOnClickListener(this);
        ib_trainX.setOnClickListener(this);
        ib_trainY.setOnClickListener(this);
    }




    private void setBold(){
        mContext.startActivity(new Intent(mContext, TypefaceListActivity.class));
    }
    private void setItalic(){

    }
    private void setUnderLine(){

    }

    private void setTextColor(int color){
        if (TextButtonAdapter.SELECT_MODE){
            //多选
            for (TextButton tb:mTextButtonList){
                if (tb.isSelected())
                    tb.setTextColor(color);
            }
            mTextFragment.textButtonAdapter.notifyDataSetChanged();
        }else {
            //单选
            if (mTextFragment.getmTextButton()!=null){
                mTextFragment.getmTextButton().setTextColor(color);
                mTextFragment.textButtonAdapter.notifyItemChanged(mTextFragment.getPosition());
            }

        }
        mTextFragment.drawText();

    }

    private void setTextBgColor(int color){
        if (TextButtonAdapter.SELECT_MODE){
            //多选
            for (TextButton tb:mTextButtonList){
                if (tb.isSelected())
                    tb.setTextBackgroudColor(color);
                mTextFragment.textButtonAdapter.notifyDataSetChanged();
            }
        }else {
            //单选
            if (mTextFragment.getmTextButton()!=null){
                mTextFragment.getmTextButton().setTextBackgroudColor(color);
                mTextFragment.textButtonAdapter.notifyItemChanged(mTextFragment.getPosition());
            }
        }
        mTextFragment.drawText();

    }

        /**
     * 设置文本字体
     */
    public void setFont(){
        File file =new File("/system/fonts");
        File[] files = file.listFiles();
        final List<TypefaceFile> fileList=new ArrayList<>();
        for (int i=0;i<files.length;i++){
            String name = files[i].getName();
            if(name.contains("-Regular")&&!name.contains("MiuiEx")){
                fileList.add(new TypefaceFile(files[i],false));
            }
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.typeface_list, null);
        RecyclerView typefaceRecycler = (RecyclerView) view.findViewById(R.id.typeFaceListView);
        typefaceRecycler.setLayoutManager(new LinearLayoutManager(mContext));
        typefaceAdapter = new TypefaceAdapter(fileList, mContext);
        typefaceRecycler.setAdapter(typefaceAdapter);
        typefaceRecycler.addItemDecoration(new WifiItemDecoration(mContext,WifiItemDecoration.VERTICAL_LIST));
        typefaceAdapter.setOnItemClickListener(new TypefaceAdapter.OnItemOnClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //1.先获取选择了哪个字体
                typeFile = fileList.get(position).getFile();
            }
        });


        new AlertDialog.Builder(mContext)
                .setTitle("Choose Typeface")
                .setView(view)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (TextButtonAdapter.SELECT_MODE){
                            //多选
                                for (TextButton tb:mTextButtonList){
                                    //2.将字体文件设置进入
                                    if (tb.isSelected())
                                        tb.setTypeface(typeFile);
                                    mTextFragment.textButtonAdapter.notifyDataSetChanged();
                                }
                        }else {
                            //单选
                            if (mTextFragment.getmTextButton()!=null)
                                mTextFragment.getmTextButton().setTypeface(typeFile);
                            mTextFragment.textButtonAdapter.notifyItemChanged(mTextFragment.getPosition());//更新

                        }
                        dialog.dismiss();
                        mTextFragment.drawText();
                    }
                })
                .setNegativeButton("cancle",null)
                .show();

    }

    public void setTextSize(){
        String[] textSizePxList=new String[200];
        for (int i=1;i<=200;i++){
            textSizePxList[i-1]=i+"";
        }

        View outerView = LayoutInflater.from(mContext).inflate(R.layout.wheel_view, null);
        WheelView wv = (WheelView) outerView.findViewById(R.id.wheelview);
        wv.setOffset(2);//偏移量
        wv.setItems(Arrays.asList(textSizePxList));
        if (TextButtonAdapter.SELECT_MODE){
            wv.setSeletion(mTextFragment.getRECOMAND_SIZE()-1);
        }else {
            //取之前设置的字体大小的值
            if (mTextFragment.getmTextButton()!=null)
                wv.setSeletion(mTextFragment.getmTextButton().getTextSize());
            else //如果没有点击就设置，则取推荐字体大小
                wv.setSeletion(mTextFragment.getRECOMAND_SIZE()-1);
        }
        wv.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                    bt_TextSize.setText("size:"+item);
                if (TextButtonAdapter.SELECT_MODE){
                    //多选
                    for (TextButton tb:mTextButtonList){
                        if (tb.isSelected())
                            tb.setTextSize(selectedIndex);
                    }
                }else {
                    //单选
                    if (mTextFragment.getmTextButton()!=null)
                        mTextFragment.getmTextButton().setTextSize(selectedIndex);

                }
                mTextFragment.drawText();
            }
        });

        new AlertDialog.Builder(mContext)
                .setTitle("设置文字大小")
                .setView(outerView)
                .setPositiveButton("OK", null)
                .show();

    }
    public void setTextColorMore(){
        ColorPickerDialogBuilder.with(mContext)
                .setTitle("选择字体颜色")
                .initialColor(Color.RED)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int i) {
                        Toast.makeText(mContext, "ColorSelected:"+Integer.toHexString(i), Toast.LENGTH_SHORT).show();

                    }
                })
                .setPositiveButton("确定", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, Integer[] integers) {
                        setTextColor(i);

                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .build().show();

    }

    public void setTextBgMore(){
        // TODO: 2016/10/26
        ColorPickerDialogBuilder.with(mContext)
                .setTitle("选择字体背景颜色")
                .initialColor(Color.RED)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int i) {
                        Toast.makeText(mContext, "ColorSelected:"+Integer.toHexString(i), Toast.LENGTH_SHORT).show();

                    }
                })
                .setPositiveButton("确定", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, Integer[] integers) {
                        setTextBgColor(i);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .build().show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_fgText_textSize:
                setTextSize();
                break;
            case R.id.bt_fgText_font: //字体
                setFont();
                break;
            case R.id.ib_fgText_setBold:
                setBold();
                break;
            case R.id.ib_fgText_setItalic:
                Toast.makeText(mContext, "test ib_fgText_setItalic", Toast.LENGTH_SHORT).show();
                break;
            case R.id.ib_fgText_setUnderLine:
                break;
            case R.id.ib_fgText_textColorRed:
                setTextColor(Color.RED);
                break;
            case R.id.ib_fgText_textColorGreen:
                setTextColor(Color.GREEN);
                break;
            case R.id.ib_fgText_textColorBlue:
                setTextColor(Color.BLUE);
                break;
            case R.id.ib_fgText_textColorMore:
                setTextColorMore();
                break;
            case R.id.ib_fgText_textBgRed:
                setTextBgColor(Color.RED);
                break;
            case R.id.ib_fgText_textBgGreen:
                setTextBgColor(Color.GREEN);
                break;
            case R.id.ib_fgText_textBgBlue:
                setTextBgColor(Color.BLUE);
                break;
            case R.id.ib_fgText_textBgMore:
                setTextBgMore();
                break;
            case R.id.ib_fgText_trainX:
                break;
            case R.id.ib_fgText_trainY:
                break;
        }
    }
}
