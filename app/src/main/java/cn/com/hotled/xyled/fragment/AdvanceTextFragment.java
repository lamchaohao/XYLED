package cn.com.hotled.xyled.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.adapter.TypefaceAdapter;
import cn.com.hotled.xyled.bean.TextButton;
import cn.com.hotled.xyled.bean.TypefaceFile;
import cn.com.hotled.xyled.decoration.WifiItemDecoration;
import cn.com.hotled.xyled.activity.BrowsePhotoActivity;
import cn.com.hotled.xyled.view.PhotoView;
import cn.com.hotled.xyled.view.RulerView;
import cn.com.hotled.xyled.view.WheelView;

/**
 * Created by Lam on 2016/11/30.
 * 为了数据保存以及恢复，故采用Textbutton保存数据
 */

public class AdvanceTextFragment extends BaseFragment implements View.OnClickListener{


    private PhotoView mPhotoView;
    private Canvas canvas;
    private Bitmap targetBitmap;
    private Paint paint;
    private int mWidth;
    private int mHeight;
    private EditText mEt_input;
    private int RECOMAND_SIZE=26;
    private int mBaseX = 0;
    private int mBaseY = 25;
    private List<TextButton> mTextButtonList = new ArrayList<>();
    private int mTextSize = RECOMAND_SIZE;
    private int mTextColor = Color.RED;
    private int mTextBgColor = Color.BLACK;
    private boolean isBold;
    private boolean isItalic;
    private boolean isUnderLine;
    private Button mBt_textSize;
    private Button mBt_setFont;
    private TypefaceAdapter typefaceAdapter;
    private File mTypeFile;
    private float mFrameTime;
    private float mStayTime;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_advance_text, container, false);
        initView(view);
        initPhotoView();
        return view;
    }

    private void initPhotoView() {
        //启用图片缩放功能
        mPhotoView.enable();

        //设置缩放倍数
        mPhotoView.setMaxScale(8);

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
        mPhotoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        mPhotoView.setImageBitmap(targetBitmap);
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), BrowsePhotoActivity.class);
                intent.putExtra("bitmap",targetBitmap);
                ActivityOptionsCompat options =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                                mPhotoView, getString(R.string.shareNames));
                ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
            }
        });

    }

    private void initView(View inflate) {
        mPhotoView = (PhotoView) inflate.findViewById(R.id.pv_fgText_photo);
        mEt_input = (EditText) inflate.findViewById(R.id.et_fgText_input);
        ImageView mIv_setTime = (ImageView) inflate.findViewById(R.id.iv_fgText_setTime);
        mBt_textSize = (Button) inflate.findViewById(R.id.bt_fgText_textSize);
        mBt_setFont = (Button) inflate.findViewById(R.id.bt_fgText_font);
        ImageButton ib_setBold= (ImageButton) inflate.findViewById(R.id.ib_fgText_setBold);
        ImageButton ib_setItalic= (ImageButton) inflate.findViewById(R.id.ib_fgText_setItalic);
        ImageButton ib_setUnderLine= (ImageButton) inflate.findViewById(R.id.ib_fgText_setUnderLine);
        ImageButton ib_RedText= (ImageButton) inflate.findViewById(R.id.ib_fgText_textColorRed);
        ImageButton ib_GreenText= (ImageButton) inflate.findViewById(R.id.ib_fgText_textColorGreen);
        ImageButton ib_BlueText= (ImageButton) inflate.findViewById(R.id.ib_fgText_textColorBlue);
        ImageButton  ib_moreColor= (ImageButton) inflate.findViewById(R.id.ib_fgText_textColorMore);
        ImageButton ib_RedTextBg= (ImageButton) inflate.findViewById(R.id.ib_fgText_textBgRed);
        ImageButton ib_GreenTextBg= (ImageButton) inflate.findViewById(R.id.ib_fgText_textBgGreen);
        ImageButton ib_BlueTextBg= (ImageButton) inflate.findViewById(R.id.ib_fgText_textBgBlue);
        ImageButton ib_MoreTextBg= (ImageButton) inflate.findViewById(R.id.ib_fgText_textBgMore);
        ImageButton ib_trainX = (ImageButton) inflate.findViewById(R.id.ib_fgText_trainX);
        ImageButton ib_trainY = (ImageButton) inflate.findViewById(R.id.ib_fgText_trainY);

        mIv_setTime.setOnClickListener(this);
        mBt_textSize.setOnClickListener(this);
        mBt_setFont.setOnClickListener(this);
        ib_setBold.setOnClickListener(this);
        ib_setItalic.setOnClickListener(this);
        ib_setUnderLine.setOnClickListener(this);
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
        initEditText();
    }
    private void initEditText(){
        mEt_input.addTextChangedListener(new TextWatcher() {

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
                    TextButton tb=new TextButton(subStr,mTextSize,mTextColor,mTextBgColor,false,false,false);
                    mTextButtonList.add(start,tb);

                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                drawText();
            }
        });
    }

    private void drawText() {
        StringBuilder sb=new StringBuilder();
        for (TextButton textButton : mTextButtonList) {
            sb.append(textButton.getText());
        }
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawPaint(paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));

        //如果图片比所设置的宽，则需加长

       //先设置好画笔，才进行计算
        paint.setColor(mTextColor);
        paint.setTextSize(mTextSize);

        if (mTypeFile!=null) {
            Typeface typeface = Typeface.createFromFile(mTypeFile);
            paint.setTypeface(typeface);
            if (isBold) {//粗体
                paint.setTypeface(Typeface.create(typeface,Typeface.BOLD));
            }
            if (isItalic) {//斜体
                paint.setTypeface(Typeface.create(typeface,Typeface.ITALIC));
            }
            if (isBold&&isItalic){//粗斜体
                paint.setTypeface(Typeface.create(typeface,Typeface.BOLD_ITALIC));
            }
        }
        else {
            paint.setTypeface(Typeface.DEFAULT);

            if (isBold) {//粗体
                paint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
            }
            if (isItalic) {//斜体
                paint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.ITALIC));
            }
            if (isBold&&isItalic){//粗斜体
                paint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD_ITALIC));
            }


        }
        if (isUnderLine) {//下划线
            paint.setUnderlineText(true);
        }else {
            paint.setUnderlineText(false);
        }
        paint.setTextAlign(Paint.Align.LEFT);
        //设置好画笔，开始计算
        float drawWidth = computeWidth(sb.toString());
        if (drawWidth>64) {
            mWidth = (int) drawWidth;
            targetBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
            if (targetBitmap != null)
                canvas.setBitmap(targetBitmap);
            mPhotoView.setImageBitmap(targetBitmap);
        }
        //背景
        drawBgColor(drawWidth);
        //文本
        canvas.drawText(sb.toString(),mBaseX,mBaseY,paint);
    }

    private float computeWidth(String text) {
        float drawWidth=mBaseX;
        float[] widths=new float[text.length()];
        paint.getTextWidths(text,widths);
        for (int i = 0; i < widths.length; i++) {
            drawWidth+=widths[i];
        }
        Log.i("advance", "computeWidth:drawWidth "+drawWidth);
        return drawWidth;
    }

    private void drawBgColor(float drawWidth) {
        Paint bgPaint=new Paint();
        bgPaint.setColor(mTextBgColor);
        canvas.drawRect(mBaseX,0,drawWidth,mHeight,bgPaint);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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
                setItalic();
                break;
            case R.id.ib_fgText_setUnderLine:
                setUnderLine();
                break;
            case R.id.iv_fgText_setTime:
                setFrameTime();
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
                setTrainX();
                break;
            case R.id.ib_fgText_trainY:
                setTrainY();
                break;
        }
    }

    private void setTrainY() {
        View outerView = LayoutInflater.from(getContext()).inflate(R.layout.alert_train_y, null);
        RulerView ruler = (RulerView) outerView.findViewById(R.id.ruler_view_vertical);
        ruler.setValue(mBaseY);
        ruler.setOnValueChangeListener(new RulerView.OnValueChangeListener() {
            @Override
            public void onValueChange(int value) {
                mBaseY=value;
                drawText();
            }
        });
        new AlertDialog.Builder(getContext())
                .setTitle("设置垂直方向偏移量")
                .setView(outerView)
                .setPositiveButton("OK", null)
                .show();
    }

    private void setTrainX() {
        View outerView = LayoutInflater.from(getContext()).inflate(R.layout.alert_train_x, null);
        RulerView ruler = (RulerView) outerView.findViewById(R.id.ruler_view_horizon);
        ruler.setValue(mBaseX);
        ruler.setOnValueChangeListener(new RulerView.OnValueChangeListener() {
            @Override
            public void onValueChange(int value) {
                mBaseX=value;
                drawText();
            }
        });
        new AlertDialog.Builder(getContext())
                .setTitle("设置水平方向偏移量")
                .setView(outerView)
                .setPositiveButton("OK", null)
                .show();
    }

    private void setTextBgMore() {
        ColorPickerDialogBuilder.with(getContext())
                .setTitle("选择字体背景颜色")
                .initialColor(Color.RED)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int i) {

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

    private void setTextBgColor(int color) {
        mTextBgColor=color;
        for (TextButton tb:mTextButtonList){
            tb.setTextBackgroudColor(color);
        }
        drawText();
    }

    private void setTextColorMore() {
        ColorPickerDialogBuilder.with(getContext())
                .setTitle("选择字体颜色")
                .initialColor(Color.RED)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int i) {

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

    public void setTextColor(int textColor) {
        mTextColor=textColor;
        for (TextButton tb:mTextButtonList){
            tb.setTextColor(textColor);
        }
        drawText();
    }

    private void setFrameTime() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.content_setframetime, null);
        SeekBar sb_speed = (SeekBar) view.findViewById(R.id.sb_frameTime_speed);
        final TextView tv_speed = (TextView) view.findViewById(R.id.tv_frameTime_speed);
        SeekBar sb_stay = (SeekBar) view.findViewById(R.id.sb_frameTime_stayTime);
        final TextView tv_stayTime = (TextView) view.findViewById(R.id.tv_frameTime_stayTime);
        final float[] frameTime = {0};
        final float[] stayTime = {0};

        sb_speed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int i = (progress * 20);
                tv_speed.setText("速度:"+i+"ms/帧");
                frameTime[0] = (float) (progress*2.56);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sb_stay.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int i = progress*20;
                tv_stayTime.setText("停留时间:"+i+"ms");
                stayTime[0] = (float) (progress*2.56);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sb_speed.setProgress((int) (getFrameTime()/2.56));
        sb_stay.setProgress((int) (getStayTime()/2.56));

        new AlertDialog.Builder(getContext())
                .setTitle("设置时间")
                .setView(view)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mFrameTime = frameTime[0];
                        mStayTime = stayTime[0];
                    }
                })
                .show();
    }

    private void setUnderLine() {
        isUnderLine=!isUnderLine;
        for (TextButton tb:mTextButtonList){
            tb.setUnderline(isUnderLine);
        }
        drawText();
    }

    private void setItalic() {
        isItalic=!isItalic;
        for (TextButton tb:mTextButtonList){
            tb.setIlatic(isItalic);
        }
        drawText();
    }

    private void setBold() {
        isBold=!isBold;//取反
        for (TextButton tb:mTextButtonList){
            tb.setIsbold(isBold);
        }
        drawText();
    }

    private void setFont() {
        File file =new File("/system/fonts");
        File[] files = file.listFiles();
        File downloadFontDir = new File(Environment.getExternalStorageDirectory()+"/fonts/xyledfonts");
        if (!downloadFontDir.exists()){
            downloadFontDir.mkdir();
        }
        Log.i("popup", "setFont: "+downloadFontDir.getAbsolutePath());
        File[] downloadFonts = downloadFontDir.listFiles();
        final List<TypefaceFile> fileList=new ArrayList<>();
        for (File downloadFont : downloadFonts) {
            fileList.add(new TypefaceFile(downloadFont,false));
        }
        for (int i=0;i<files.length;i++){
            String name = files[i].getName();
            if(name.contains("-Regular")&&!name.contains("MiuiEx")){
                fileList.add(new TypefaceFile(files[i],false));
            }
        }
        View view = LayoutInflater.from(getContext()).inflate(R.layout.typeface_list, null);
        RecyclerView typefaceRecycler = (RecyclerView) view.findViewById(R.id.typeFaceListView);
        typefaceRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        typefaceAdapter = new TypefaceAdapter(fileList, getContext());
        typefaceRecycler.setAdapter(typefaceAdapter);
        typefaceRecycler.addItemDecoration(new WifiItemDecoration(getContext(),WifiItemDecoration.VERTICAL_LIST));
        typefaceAdapter.setOnItemClickListener(new TypefaceAdapter.OnItemOnClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //1.先获取选择了哪个字体
                mTypeFile = fileList.get(position).getFile();
            }
        });


        new AlertDialog.Builder(getContext())
                .setTitle("Choose Typeface")
                .setView(view)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    for (TextButton tb:mTextButtonList){
                        //2.将字体文件设置进入
                        tb.setTypeface(mTypeFile);
                    }
                    int lastIndexOf = mTypeFile.getName().lastIndexOf(".");
                    String fontFile = mTypeFile.getName().substring(0, lastIndexOf);
                    mBt_setFont.setText(fontFile);
                    dialog.dismiss();
                    drawText();
                    }
                })
                .setNegativeButton("cancle",null)
                .show();
    }

    private void setTextSize(){
        String[] textSizePxList=new String[200];
        for (int i=1;i<=200;i++){
            textSizePxList[i-1]=i+"";
        }

        View outerView = LayoutInflater.from(getContext()).inflate(R.layout.wheel_view, null);
        WheelView wv = (WheelView) outerView.findViewById(R.id.wheelview);
        wv.setOffset(2);//偏移量
        wv.setItems(Arrays.asList(textSizePxList));
        wv.setSeletion(mTextSize-1);

        wv.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                mBt_textSize.setText("size:"+item);
                for (TextButton tb:mTextButtonList){
                    mTextSize=selectedIndex;
                    tb.setTextSize(selectedIndex);
                }
                drawText();
            }
        });

        new AlertDialog.Builder(getContext())
                .setTitle("设置文字大小")
                .setView(outerView)
                .setPositiveButton("OK", null)
                .show();

    }


    @Override
    public Bitmap getBitmap() {
        return targetBitmap;
    }

    @Override
    public float getFrameTime() {
        if (mFrameTime==0)
            return 20;
        return mFrameTime;
    }

    @Override
    public float getStayTime() {
        if (mStayTime==0)
            return mFrameTime;//如果staytime没有设置，则与帧速一致
        return mStayTime;
    }



}
