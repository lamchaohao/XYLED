package cn.com.hotled.xyled.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.adapter.TextButtonAdapter;
import cn.com.hotled.xyled.bean.TextButton;
import cn.com.hotled.xyled.view.PhotoView;
import cn.com.hotled.xyled.view.TextToolPopupWindow;


public class TextFragment extends Fragment {

    @BindView(R.id.pv_fgText_photo)
    PhotoView photoView;
    @BindView(R.id.et_fgText_input)
    EditText et_input;
    @BindView(R.id.rv_fgText_showResult)
    RecyclerView recyclerView;
    @BindView(R.id.ib_fgText_settool)
    ImageButton ib_fgText_settool;
    Button mButton;
    List<TextButton> mTextButtonList=new ArrayList<>();
    public TextButtonAdapter textButtonAdapter;
    private TextButton mTextButton;
    private PopupWindow mPopupWindow;
    private LinearLayout ll_fgText;
    private Canvas canvas;
    private Bitmap targetBitmap;
    private Paint paint;
    private int RECOMAND_SIZE=26;
    private int mPosition;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_text, container, false);
        ll_fgText = (LinearLayout) inflate.findViewById(R.id.ll_fgText);
        ButterKnife.bind(this,inflate);
        initPhotoView();
        initRecyclerView();
        setEditorListener();
        return inflate;
    }

    private void initPhotoView() {
        //启用图片缩放功能
        photoView.enable();

        //设置缩放倍数
        photoView.setMaxScale(4);
        //获取图片信息
//        Info info = photoView.getInfo();
//        photoView.animaFrom(info);

        Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.monitor);
        canvas = new Canvas();

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        targetBitmap = bitmap.copy(bitmap.getConfig(),true);
        canvas.setBitmap(targetBitmap);
        photoView.setScaleType(ImageView.ScaleType.FIT_XY);
        photoView.setImageBitmap(targetBitmap);
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
                    }else {
                        mTextButtonList.get(position).setSelected(true);
                    }
                }else {
                    //单选模式
                    if (mTextButton!=null)
                        mTextButton.setSelected(false);//把之前选中的取消选中
                    mButton= (Button) view;
                    mPosition = position;
                    mTextButton = mTextButtonList.get(position);
                    mTextButton.setSelected(true);
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
                    // TODO: 2016/10/31 buttonTextList 展示出来是倒叙的，最后一个放在最前面 2016/11/1 已解决
                    TextButton tb=new TextButton(subStr,28,Color.RED,Color.WHITE,false,false,false);
                    mTextButtonList.add(start,tb);

                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                textButtonAdapter.notifyDataSetChanged();
                drawText();
            }
        });

    }

    public void drawText() {
        //先擦除原来的文字
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawPaint(paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        //画图
        paint.setColor(Color.RED);
        paint.setTypeface(Typeface.MONOSPACE);
        Rect rect=null;
        int x=0;
        int y=100;
        for(TextButton tb : mTextButtonList){
            x+=tb.getTextSize();
            paint.setColor(tb.getTextColor());
            paint.setTextSize(tb.getTextSize());
            if (tb.getTypeface()!=null)
                paint.setTypeface(Typeface.createFromFile(tb.getTypeface()));
            else
                paint.setTypeface(Typeface.DEFAULT);
            rect=new Rect(x,y-tb.getTextSize(),x+tb.getTextSize(),y);
            Paint bgPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
            bgPaint.setColor(tb.getTextBackgroudColor());
            canvas.drawRect(rect,bgPaint);
            canvas.drawText(tb.getText(),x,100,paint);

        }
//        canvas.drawText(text,0,100,paint);
        photoView.setImageBitmap(targetBitmap);

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ButterKnife.bind(getActivity());
    }



    @OnClick(R.id.ib_fgText_settool)
    public void setToolBox(){

        if (mPopupWindow==null||!mPopupWindow.isShowing()){

            mPopupWindow=new TextToolPopupWindow(getContext(),mTextButtonList,this);
            mPopupWindow.showAtLocation(ll_fgText, Gravity.TOP,0,0);
        }else if (mPopupWindow!=null&&mPopupWindow.isShowing()){
            mPopupWindow.dismiss();
        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    public TextButton getmTextButton() {
        return mTextButton;
    }

    public int getRECOMAND_SIZE() {
        return RECOMAND_SIZE;
    }

    public int getPosition() {
        return mPosition;
    }
}
