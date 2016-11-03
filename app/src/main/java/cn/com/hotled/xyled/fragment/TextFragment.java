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
import cn.com.hotled.xyled.util.DensityUtil;
import cn.com.hotled.xyled.view.TextToolPopupWindow;
import cn.com.hotled.xyled.view.WheelView;


public class TextFragment extends Fragment {

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
    public List<TextButton> mSelectedTextList=new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_text, container, false);
        ll_fgText = (LinearLayout) inflate.findViewById(R.id.ll_fgText);
        ButterKnife.bind(this,inflate);
        initRecyclerView();
        setEditorListener();
        return inflate;
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),10, LinearLayoutManager.VERTICAL,false));
        textButtonAdapter = new TextButtonAdapter(getContext(),mTextButtonList);
        textButtonAdapter.setItemOnClickListener(new TextButtonAdapter.OnItemOnClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (TextButtonAdapter.SELECT_MODE){
                    //多选模式
                    if (mTextButtonList.get(position).isSelected()){
                        //如果是已选，则改为未选
                        mTextButtonList.get(position).setSelected(false);
                        mSelectedTextList.remove(mTextButtonList.get(position));
                    }else {
                        mTextButtonList.get(position).setSelected(true);
                        mSelectedTextList.add(mTextButtonList.get(position));
                    }
                }else {
                    //单选模式
                    if (mTextButton!=null)
                        mTextButton.setSelected(false);//把之前选中的取消选中
                    mButton= (Button) view;
                    mTextButton = mTextButtonList.get(position);
                    mTextButton.setSelected(true);
                }
            }
        });
        textButtonAdapter.setItemOnLongClickListener(new TextButtonAdapter.OnItemLongClickListener() {
            @Override
            public void onLongClick(View view, int position) {
                if (TextButtonAdapter.SELECT_MODE){

                }else {
                    mSelectedTextList.clear();
                    for (int i=0;i<mTextButtonList.size();i++){
                        mTextButtonList.get(i).setSelected(false);
                        textButtonAdapter.notifyItemChanged(i);//设置回背景
                    }

                }
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



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ButterKnife.bind(getActivity());
    }



    @OnClick(R.id.ib_fgText_settool)
    public void setToolBox(){

        if (mPopupWindow==null||!mPopupWindow.isShowing()){

            mPopupWindow=new TextToolPopupWindow(getContext());
            //上滑一段距离
            int popWinHeight = DensityUtil.dp2px(getContext(), 250);
            final int top = ll_fgText.getTop();
            ll_fgText.scrollTo(0,popWinHeight-top);
            mPopupWindow.showAtLocation(ll_fgText, Gravity.BOTTOM,0,0);
            mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    //消失后下滑
                    ll_fgText.scrollTo(0,top);
                }
            });
        }else if (mPopupWindow!=null&&mPopupWindow.isShowing()){
            mPopupWindow.dismiss();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
