package cn.com.hotled.xyled.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
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
import cn.com.hotled.xyled.activity.BrowsePhotoActivity;
import cn.com.hotled.xyled.view.PhotoView;
import cn.com.hotled.xyled.view.TextToolPopupWindow;


public class TextFragment extends BaseFragment {

    @BindView(R.id.pv_fgText_photo)
    PhotoView photoView;
    @BindView(R.id.et_fgText_input)
    EditText et_input;
    @BindView(R.id.rv_fgText_showResult)
    RecyclerView recyclerView;
    @BindView(R.id.ib_fgText_settool)
    public ImageButton ib_fgText_settool;
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
    private int mWidth;
    private int mHeight;
    private int mBaseX = 15;
    private int mBaseY = 25;
    private float mFrameTime;
    private float mStayTime;

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
        photoView.setMaxScale(8);
        //获取图片信息
//        Info info = photoView.getInfo();
//        photoView.animaFrom(info);

        //使用decodeResource解析bitmap，会生成自适应手机屏幕尺寸的bitmap，mi4亲测会放大三倍
        canvas=new Canvas();
        //启用抗锯齿效果
        paint = new Paint();
        mWidth = 64;
        // TODO: 2016/11/29 临时改为16高
        mHeight = 32;

        targetBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);

        if (targetBitmap!=null)
            canvas.setBitmap(targetBitmap);
        photoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        photoView.setImageBitmap(targetBitmap);
        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), BrowsePhotoActivity.class);
                intent.putExtra("bitmap",targetBitmap);
                ActivityOptionsCompat options =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                                photoView, getString(R.string.shareNames));
                ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
            }
        });

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
                        mTextButtonList.get(position).setIsSelected(false);
                    }else {
                        mTextButtonList.get(position).setIsSelected(true);
                    }
                }else {
                    //单选模式
                    if (mTextButton!=null)
                        mTextButton.setIsSelected(false);//把之前选中的取消选中
                    mButton= (Button) view;
                    mPosition = position;
                    mTextButton = mTextButtonList.get(position);
                    mTextButton.setIsSelected(true);
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
                    TextButton tb=new TextButton(subStr,RECOMAND_SIZE,Color.RED,Color.BLACK,false,false,false);
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

    public void drawText(){
        //先计算宽度
        float drawWidth = computeWidth();
        //如果图片比所设置的宽，则需加长
        if (drawWidth>64) {
            mWidth = (int) drawWidth;
            targetBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
            if (targetBitmap != null)
                canvas.setBitmap(targetBitmap);
            photoView.setImageBitmap(targetBitmap);
        }
        startDrawText(drawWidth);
    }

    public void startDrawText(float computeWidth) {
        //先擦除原来的文字
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawPaint(paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        //画图
        paint.setColor(Color.RED);
        paint.setTypeface(Typeface.DEFAULT);

        int index=0;
        //背景
        drawBgColor(computeWidth);

        int drawWidth=mBaseX;
        float oldWidth=0;
        for(TextButton tb : mTextButtonList){
            //字体颜色
            paint.setColor(tb.getTextColor());
            //字体大小
            paint.setTextSize(tb.getTextSize());
            if (tb.getTypeface()!=null) {
                Typeface typeface = Typeface.createFromFile(tb.getTypeface());
                paint.setTypeface(typeface);
                if (tb.isbold()) {//粗体
                    paint.setTypeface(Typeface.create(typeface,Typeface.BOLD));
                }
                if (tb.isIlatic()) {//斜体
                    paint.setTypeface(Typeface.create(typeface,Typeface.ITALIC));
                }
                if (tb.isbold()&&tb.isIlatic()){//粗斜体
                    paint.setTypeface(Typeface.create(typeface,Typeface.BOLD_ITALIC));
                }
            }
            else {
                paint.setTypeface(Typeface.DEFAULT);

                if (tb.isbold()) {//粗体
                    paint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
                }
                if (tb.isIlatic()) {//斜体
                    paint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.ITALIC));
                }
                if (tb.isbold()&&tb.isIlatic()){//粗斜体
                    paint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD_ITALIC));
                }


            }
            if (tb.isUnderline()) {//下划线
                paint.setUnderlineText(true);
            }else {
                paint.setUnderlineText(false);
            }

            paint.setTextAlign(Paint.Align.LEFT);
            float[] widths=new float[1];
            paint.getTextWidths(tb.getText(),widths);
            //不再使用textSize作为间隔宽度，那样会使得英文之间的间隔太大

            if (oldWidth<widths[0]&&index!=0){
                drawWidth+=(widths[0]-oldWidth)/2;
            }
            oldWidth=widths[0];
            canvas.drawText(tb.getText(), drawWidth, mBaseY,paint);
            drawWidth+=widths[0];
//            Log.i("textfragment", "startDrawText: index="+index+", width="+widths[0]+", mbaseX="+mBaseX+", drawWidth="+drawWidth+", oldWidth= "+oldWidth);
            index++;

        }
    }

    private void drawBgColor(float width) {
        for (TextButton tb : mTextButtonList) {
            // 背景颜色      left  top  right  bottom
            Paint bgPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
            bgPaint.setColor(tb.getTextBackgroudColor());
            canvas.drawRect(0,0,width,mHeight,bgPaint);

        }

    }

    private float computeWidth(){
        int index=0;
        float drawWidth=mBaseX;
        float oldWidth=0;
        for (TextButton tb : mTextButtonList) {
            paint.setColor(tb.getTextColor());
            //字体大小
            paint.setTextSize(tb.getTextSize());
            if (tb.getTypeface()!=null) {
                Typeface typeface = Typeface.createFromFile(tb.getTypeface());
                paint.setTypeface(typeface);
                if (tb.isbold()) {//粗体
                    paint.setTypeface(Typeface.create(typeface,Typeface.BOLD));
                }
                if (tb.isIlatic()) {//斜体
                    paint.setTypeface(Typeface.create(typeface,Typeface.ITALIC));
                }
                if (tb.isbold()&&tb.isIlatic()){//粗斜体
                    paint.setTypeface(Typeface.create(typeface,Typeface.BOLD_ITALIC));
                }


            }
            else {
                if (tb.isbold()) {//粗体
                    paint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
                }
                if (tb.isIlatic()) {//斜体
                    paint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.ITALIC));
                }
                if (tb.isbold()&&tb.isIlatic()){//粗斜体
                    paint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD_ITALIC));
                }
                paint.setUnderlineText(false);

            }
            if (tb.isUnderline()) {//下划线
                paint.setUnderlineText(true);
            }else {
                paint.setUnderlineText(false);
            }

            paint.setTextAlign(Paint.Align.CENTER);
            float[] widths=new float[1];
            paint.getTextWidths(tb.getText(),widths);
            //不再使用textSize作为间隔宽度，那样会使得英文之间的间隔太大

            drawWidth+=widths[0];
            if (oldWidth<widths[0]&&index!=0){
                drawWidth+=(widths[0]-oldWidth)/2;
            }
            oldWidth=widths[0];
//            Log.i("textfragment compute", "startDrawText: index="+index+", width="+widths[0]+", mbaseX="+mBaseX+", drawWidth="+drawWidth+", oldWidth= "+oldWidth);
            index++;
        }
        return drawWidth;
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

    public int getRecomandSize() {
        return RECOMAND_SIZE;
    }

    public int getPosition() {
        return mPosition;
    }

    public void setBaseX(int baseX) {
        mBaseX = baseX;
    }

    public void setBaseY(int baseY) {
        mBaseY = baseY;
    }

    public int getBaseX() {
        return mBaseX;
    }

    public int getBaseY() {
        return mBaseY;
    }



    public void setFrameTime(float frameTime) {
        mFrameTime = frameTime;
    }


    public void setStayTime(float stayTime) {
        mStayTime = stayTime;
    }

    @Override
    public Bitmap getBitmap() {
        return targetBitmap;
    }

    @Override
    public float getFrameTime() {
        return mFrameTime;
    }

    @Override
    public float getStayTime() {
        return mStayTime;
    }
}
