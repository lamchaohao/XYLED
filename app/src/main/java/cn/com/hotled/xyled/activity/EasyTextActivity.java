package cn.com.hotled.xyled.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
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
import cn.com.hotled.xyled.App;
import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.adapter.TypefaceAdapter;
import cn.com.hotled.xyled.bean.Program;
import cn.com.hotled.xyled.bean.TextButton;
import cn.com.hotled.xyled.bean.TypefaceFile;
import cn.com.hotled.xyled.dao.ProgramDao;
import cn.com.hotled.xyled.dao.TextButtonDao;
import cn.com.hotled.xyled.decoration.WifiItemDecoration;
import cn.com.hotled.xyled.util.DensityUtil;
import cn.com.hotled.xyled.util.MoveTextCompressUtil;
import cn.com.hotled.xyled.view.PhotoView;
import cn.com.hotled.xyled.view.RulerView;
import cn.com.hotled.xyled.view.WheelView;

public class EasyTextActivity extends BaseActivity implements View.OnClickListener{

    private static final int SELECT_FLOW_CODE = 202;
    @BindView(R.id.pv_fgText_photo)
    PhotoView mPhotoView;
    @BindView(R.id.et_fgText_input)
    EditText mEt_input;
    @BindView(R.id.bt_fgText_font)
    Button mBt_setFont;
    @BindView(R.id.bt_fgText_textSize)
    Button mBt_textSize;
    @BindView(R.id.ib_fgText_setBold)
    ImageButton ib_setBold;
    @BindView(R.id.ib_fgText_setItalic)
    ImageButton ib_setItalic;
    @BindView(R.id.ib_fgText_setUnderLine)
    ImageButton ib_setUnderLine;
    @BindView(R.id.ib_fgText_textColorRed)
    ImageButton ib_RedText;
    @BindView(R.id.ib_fgText_textColorGreen)
    ImageButton ib_GreenText;
    @BindView(R.id.ib_fgText_textColorBlue)
    ImageButton ib_BlueText;
    @BindView(R.id.ib_fgText_textColorMore)
    ImageButton ib_moreColor;
    @BindView(R.id.ib_fgText_textBgRed)
    ImageButton ib_RedTextBg;
    @BindView(R.id.ib_fgText_textBgGreen)
    ImageButton ib_GreenTextBg;
    @BindView(R.id.ib_fgText_textBgBlue)
    ImageButton ib_BlueTextBg;
    @BindView(R.id.ib_fgText_textBgMore)
    ImageButton ib_MoreTextBg;
    @BindView(R.id.ib_fgText_trainX)
    ImageButton ib_trainX;
    @BindView(R.id.ib_fgText_trainY)
    ImageButton ib_trainY;
    @BindView(R.id.iv_fgText_setTime)
    ImageView iv_setTime;
    @BindView(R.id.rl_fgText_flowLayout)
    RelativeLayout rl_setFlowStyle;
    @BindView(R.id.iv_fgText_flowStyle)
    ImageView iv_setFlowStyle;
    @BindView(R.id.bt_fgText_flowShowEffect)
    Button bt_setFlowEffect;
    @BindView(R.id.bt_fgText_flowShowSpeed)
    Button bt_setFlowSpeed;
    @BindView(R.id.sw_openFlow)
    Switch swOpenFlow;
    @BindView(R.id.ll_fgText_setFlow)
    LinearLayout llSetFlow;

    private Canvas canvas;
    private Bitmap targetBitmap;
    private Paint paint;
    private int mWidth;
    private int mHeight;
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
    private TypefaceAdapter typefaceAdapter;
    private File mTypeFile;
    private float mFrameTime;
    private float mStayTime;
    private long mProgramId;
    private TextButtonDao mTextButtonDao;
    private boolean isTextChanged;
    private boolean isLoadData;
    private List<TextButton> mOldTextButtonList;
    private ProgramDao mProgramDao;
    private Program mProgram;
    private Bitmap mFlowBitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_easy_text);
        ButterKnife.bind(this);
        initPhotoView();
        initOnclickEven();
        initEditText();
        loadData();
    }

    private void loadData() {
        mProgramId = getIntent().getLongExtra("programId", -1);
        mTextButtonDao = ((App) getApplication()).getDaoSession().getTextButtonDao();
        mProgramDao = ((App) getApplication()).getDaoSession().getProgramDao();
        if (mProgramId!=-1){
            isLoadData=true;
            mTextButtonList = mTextButtonDao.queryBuilder().where(TextButtonDao.Properties.ProgramId.eq(mProgramId)).list();
            mOldTextButtonList = mTextButtonDao.queryBuilder().where(TextButtonDao.Properties.ProgramId.eq(mProgramId)).list();

            mProgram = mProgramDao.queryBuilder().where(ProgramDao.Properties.Id.eq(mProgramId)).list().get(0);
            if (mProgram.getBaseX()!=0)
                mBaseX = mProgram.getBaseX();
            if (mProgram.getBaseY()!=0)
                mBaseY =mProgram.getBaseY();
            mFrameTime=mProgram.getFrameTime();
            mStayTime = mProgram.getStayTime();
            if (mTextButtonList==null||mTextButtonList.size()==0){
                mTextButtonList = new ArrayList<>();
            }
            StringBuilder sb=new StringBuilder();
            for (int i = 0; i < mTextButtonList.size(); i++) {
                if (i==mTextButtonList.size()-1){
                    TextButton textButton = mTextButtonList.get(0);
                    mTextBgColor=textButton.getTextBackgroudColor();
                    mTextColor=textButton.getTextColor();
                    mTextSize = textButton.getTextSize();
                    isUnderLine = textButton.getIsUnderline();
                    isItalic = textButton.getIsIlatic();
                    isBold = textButton.getIsbold();
                    mTypeFile = textButton.getTypeface();
                }
                sb.append(mTextButtonList.get(i).getText());
            }

            mEt_input.setText(sb.toString());
            if (mProgram.getFlowBoundFile()!=null){
                String absolutePath = mProgram.getFlowBoundFile().getAbsolutePath();
                mFlowBitmap=BitmapFactory.decodeFile(absolutePath);
                setFlowBound(absolutePath);
            }
            swOpenFlow.setChecked(mProgram.getUseFlowBound());
            if (swOpenFlow.isChecked()) {
                llSetFlow.setVisibility(View.VISIBLE);
            }else {
                llSetFlow.setVisibility(View.GONE);
            }
            drawText();
        }
        isLoadData=false;
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
                if (count!=0&&!isLoadData){
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
                if (!isLoadData){
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

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isLoadData) {
                    drawText();
                }
            }
        });
    }


    private void initOnclickEven() {
        iv_setTime.setOnClickListener(this);
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
        rl_setFlowStyle.setOnClickListener(this);
        bt_setFlowEffect.setOnClickListener(this);
        bt_setFlowSpeed.setOnClickListener(this);
        swOpenFlow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    llSetFlow.setVisibility(View.VISIBLE);
                    mProgram.setUseFlowBound(true);
                    drawText();
                }else {
                    mProgram.setUseFlowBound(false);
                    llSetFlow.setVisibility(View.GONE);
                    drawText();
                }
            }
        });
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
        mHeight = 32;

        targetBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_4444);

        if (targetBitmap!=null)
            canvas.setBitmap(targetBitmap);
        mPhotoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        mPhotoView.setImageBitmap(targetBitmap);
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EasyTextActivity.this, BrowsePhotoActivity.class);
                intent.putExtra("bitmap",targetBitmap);
                ActivityOptionsCompat options =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(EasyTextActivity.this,
                                mPhotoView, getString(R.string.shareNames));
                ActivityCompat.startActivity(EasyTextActivity.this, intent, options.toBundle());
            }
        });

    }
    private void drawText() {
        StringBuilder sb=new StringBuilder();
        for (TextButton textButton : mTextButtonList) {
            sb.append(textButton.getText());
        }
        if (!isLoadData){
            isTextChanged=true;//改变了需要提醒保存
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
        //需要加上流水边框的宽度,左右都需要加上
        int tempBaseX=mBaseX;
        int tempBaseY=mBaseY;
        if (mFlowBitmap!=null&&mProgram.getUseFlowBound()){
            drawWidth+=mFlowBitmap.getHeight()*2;
            //加上流水边框后，文字需要偏移流水边框的一个宽度
            tempBaseX+=mFlowBitmap.getHeight();
            tempBaseY+=mFlowBitmap.getHeight();
        }
        if (drawWidth>64) {
            mWidth = (int) drawWidth;
            targetBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_4444);
            if (targetBitmap != null)
                canvas.setBitmap(targetBitmap);
            mPhotoView.setImageBitmap(targetBitmap);
        }
        //背景
        drawBgColor(drawWidth);
        if (mFlowBitmap!=null&&mProgram.getUseFlowBound()){
        //流水边框
            drawFlowBound();
        }
        //文本
        canvas.drawText(sb.toString(),tempBaseX,tempBaseY,paint);
    }

    private float computeWidth(String text) {
        float drawWidth=mBaseX;
        float[] widths=new float[text.length()];
        paint.getTextWidths(text,widths);
        for (int i = 0; i < widths.length; i++) {
            drawWidth+=widths[i];
        }
        Log.i("advance", "computeWidth:drawWidth "+drawWidth);
        //为了避免OOM，bitmap的最大宽度设置为2048,
        // TODO: 2016/12/8 如果文字太长，需要重新绘制一个bitmap，bitmap的最大尺寸为4096*4096
        // TODO: 2017/1/4 文字太长省略后，提示用户不影响屏幕显示
        if (drawWidth>=2048)
            return 2048;
        return drawWidth;
    }

    private void drawBgColor(float drawWidth) {
        Paint bgPaint=new Paint();
        bgPaint.setColor(mTextBgColor);
        canvas.drawRect(mBaseX,0,drawWidth,mHeight,bgPaint);
    }
    private void drawFlowBound() {

        int[] flowbound = new int[mFlowBitmap.getWidth() * mFlowBitmap.getHeight()];
        for (int x1 = 0, i = 0; x1 < mFlowBitmap.getWidth(); x1++) {
            for (int y1 = 0; y1 < mFlowBitmap.getHeight(); y1++) {
                int pixel = mFlowBitmap.getPixel(x1, y1);
                flowbound[i] = pixel;
                i++;
            }
        }

        int screenHeight = mHeight;
        int screenWidth = getTargetBitmap().getWidth();
        Paint flowPaint=new Paint();

        //up part1
        for (int k = 0; k < screenWidth; k++) {
            int colorIndex = (k) * mFlowBitmap.getHeight();//this is right
            while (colorIndex >= flowbound.length) {
                colorIndex -= flowbound.length;
            }
            for (int q = 0; q < mFlowBitmap.getHeight(); q++) {
                flowPaint.setColor(flowbound[colorIndex + q]);
                int i = (screenWidth - k - 1) * screenHeight + q;
                float x=i/screenHeight;
                float y=i%screenHeight;
                canvas.drawPoint(x,y,flowPaint);
            }
        }
        //right part
        for (int j = 0; j < screenHeight; j++) {
            int colorIndex = j * mFlowBitmap.getHeight();
            while (colorIndex>=flowbound.length){
                colorIndex-=flowbound.length;
            }
            for (int q = 0; q < mFlowBitmap.getHeight(); q++) {
                flowPaint.setColor(flowbound[colorIndex + q]);
                int i = getTargetBitmap().getHeight()*getTargetBitmap().getWidth() - screenHeight * (q + 1) + j ;
                float x=i/screenHeight;
                float y=i%screenHeight;
                canvas.drawPoint(x,y,flowPaint);
            }
        }

        //down part1
        for (int k = screenWidth; k >= 2; k--) {
            int colorIndex = k * mFlowBitmap.getHeight();
            while (colorIndex>=flowbound.length){
                colorIndex-=flowbound.length;
            }

            for (int q = mFlowBitmap.getHeight(); q > 0; q--){
                flowPaint.setColor(flowbound[colorIndex + q - 1]);
                int i = (k - 1) * screenHeight - q ;
                float x=i/screenHeight;
                float y=i%screenHeight;
                canvas.drawPoint(x,y,flowPaint);
            }
        }

        //left part1
        for (int j = 0; j < screenHeight; j++) {
            int colorIndex = (j) * mFlowBitmap.getHeight();
            while (colorIndex>=flowbound.length){
                colorIndex-=flowbound.length;
            }
            for (int q = 0; q < mFlowBitmap.getHeight(); q++) {
                flowPaint.setColor(flowbound[colorIndex + q]);
                int i = screenHeight * q + j ;
                float x=i/screenHeight;
                float y=i%screenHeight;
                canvas.drawPoint(x,y,flowPaint);
            }
        }
    }


    private void setTrainY() {
        View outerView = LayoutInflater.from(this).inflate(R.layout.alert_train_y, null);
        RulerView ruler = (RulerView) outerView.findViewById(R.id.ruler_view_vertical);
        ruler.setValue(mBaseY);
        ruler.setOnValueChangeListener(new RulerView.OnValueChangeListener() {
            @Override
            public void onValueChange(int value) {
                mBaseY=value;
                drawText();
            }
        });
        new AlertDialog.Builder(this)
                .setTitle("设置垂直方向偏移量")
                .setView(outerView)
                .setPositiveButton("OK", null)
                .show();
    }

    private void setTrainX() {
        View outerView = LayoutInflater.from(this).inflate(R.layout.alert_train_x, null);
        RulerView ruler = (RulerView) outerView.findViewById(R.id.ruler_view_horizon);
        ruler.setValue(mBaseX);
        ruler.setOnValueChangeListener(new RulerView.OnValueChangeListener() {
            @Override
            public void onValueChange(int value) {
                mBaseX=value;
                drawText();
            }
        });
        new AlertDialog.Builder(this)
                .setTitle("设置水平方向偏移量")
                .setView(outerView)
                .setPositiveButton("OK", null)
                .show();
    }

    private void setTextBgMore() {
        ColorPickerDialogBuilder.with(this)
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
        ColorPickerDialogBuilder.with(this)
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
        View view = LayoutInflater.from(this).inflate(R.layout.content_setframetime, null);
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
                    isTextChanged=true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                    isTextChanged=true;
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
                isTextChanged=true;
            }
        });
        sb_speed.setProgress((int) (getFrameTime()/2.56));
        sb_stay.setProgress((int) (getStayTime()/2.56));

        new AlertDialog.Builder(this)
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
            tb.setIsUnderline(isUnderLine);
        }
        drawText();
    }

    private void setItalic() {
        isItalic=!isItalic;
        for (TextButton tb:mTextButtonList){
            tb.setIsIlatic(isItalic);
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
        if(downloadFonts!=null)
            for (File downloadFont : downloadFonts) {
                fileList.add(new TypefaceFile(downloadFont,false));
            }
        for (int i=0;i<files.length;i++){
            String name = files[i].getName();
            if(name.contains("-Regular")&&!name.contains("MiuiEx")){
                fileList.add(new TypefaceFile(files[i],false));
            }
        }
        View view = LayoutInflater.from(this).inflate(R.layout.typeface_list, null);
        RecyclerView typefaceRecycler = (RecyclerView) view.findViewById(R.id.typeFaceListView);
        typefaceRecycler.setLayoutManager(new LinearLayoutManager(this));
        typefaceAdapter = new TypefaceAdapter(fileList, this);
        typefaceRecycler.setAdapter(typefaceAdapter);
        typefaceRecycler.addItemDecoration(new WifiItemDecoration(this,WifiItemDecoration.VERTICAL_LIST));
        typefaceAdapter.setOnItemClickListener(new TypefaceAdapter.OnItemOnClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //1.先获取选择了哪个字体
                mTypeFile = fileList.get(position).getFile();
            }
        });


        new AlertDialog.Builder(this)
                .setTitle("Choose Typeface")
                .setView(view)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mTypeFile!=null){
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

        View outerView = LayoutInflater.from(this).inflate(R.layout.wheel_view, null);
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

        new AlertDialog.Builder(this)
                .setTitle("设置文字大小")
                .setView(outerView)
                .setPositiveButton("OK", null)
                .show();

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
            case R.id.rl_fgText_flowLayout:
                startActivityForResult(new Intent(this,SelectFlowActivity.class),SELECT_FLOW_CODE);
                break;
            case R.id.bt_fgText_flowShowEffect:

                break;
            case R.id.bt_fgText_flowShowSpeed:

                break;
        }
    }
    public float getFrameTime() {
        if (mFrameTime==0)
            return 20;
        return mFrameTime;
    }

    public float getStayTime() {
        if (mStayTime==0)
            return mFrameTime;//如果staytime没有设置，则与帧速一致
        return mStayTime;
    }

    public Bitmap getTargetBitmap() {
        return targetBitmap;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode==RESULT_OK&&requestCode==SELECT_FLOW_CODE){
            String fileName = data.getStringExtra("fileName");
            isTextChanged=true;
            mProgram.setFlowBoundFile(new File(fileName));
            setFlowBound(fileName);

        }
    }

    private void setFlowBound(String fileName) {
        mFlowBitmap = BitmapFactory.decodeFile(fileName);
        int width = mFlowBitmap.getWidth();
        int height = mFlowBitmap.getHeight();
        // 设置想要的大小
        int newWidth = width*5;
        int newHeight = height*5;
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(mFlowBitmap, 0, 0, width, height, matrix,
                true);
        iv_setFlowStyle.setImageBitmap(newbm);

        drawText();

    }



    @Override
    public void onBackPressed() {
        if (isTextChanged){
            new AlertDialog.Builder(this)
                    .setTitle("节目内容已改变，是否保存")
                    .setNegativeButton("不保存", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EasyTextActivity.super.onBackPressed();
                        }
                    })
                    .setPositiveButton("保存", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int index = 0;
                            for (TextButton textButton : mTextButtonList) {
                                if (textButton.getId()==0)
                                    textButton.setId(System.currentTimeMillis()*2+index);
                                textButton.setSortNumber(index);
                                textButton.setProgramId(mProgramId);
                                index++;
                            }
                            mTextButtonDao.deleteInTx(mOldTextButtonList);
                            mTextButtonDao.insertOrReplaceInTx(mTextButtonList);
                            mProgram.setBaseY(mBaseY);
                            mProgram.setBaseX(mBaseX);
                            mProgram.setStayTime(getStayTime());
                            mProgram.setFrameTime(getFrameTime());
                            mProgramDao.insertOrReplace(mProgram);

                            EasyTextActivity.super.onBackPressed();
                        }
                    })
                    .show();
        }else {
            EasyTextActivity.super.onBackPressed();
        }

    }

    @Override
    public void onCreateCustomToolBar(final Toolbar toolbar) {
        super.onCreateCustomToolBar(toolbar);
        ImageView imageView=new ImageView(this);
        ImageView ivEdit=new ImageView(this);
        Toolbar.LayoutParams prams =new Toolbar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        prams.gravity= Gravity.RIGHT;
        prams.rightMargin= DensityUtil.dp2px(this,10);

        imageView.setImageResource(R.drawable.ic_attach_file_white_24dp);
        ivEdit.setImageResource(R.drawable.ic_mode_edit_white_24dp);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoveTextCompressUtil moveUtil =new MoveTextCompressUtil(EasyTextActivity.this,getTargetBitmap(),64,32,getFrameTime(),getStayTime());
//                MoveTextUtil moveUtil =new MoveTextUtil(EasyTextActivity.this,getTargetBitmap(),64,32,getFrameTime(),getStayTime());
                moveUtil.startGenFile();
            }
        });
        //更改节目名称
        ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDialog.Builder builder = new MaterialDialog.Builder(EasyTextActivity.this);
                builder.title("修改节目名称")
                        .inputType(InputType.TYPE_CLASS_TEXT)
                        .input("请输入节目名称", mProgram.getProgramName(), new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                mProgram.setProgramName(input.toString());
                                isTextChanged=true;
                                toolbar.setTitle(input.toString());
                                setSupportActionBar(toolbar);
                                dialog.dismiss();
                                Intent intent = new Intent();
                                intent.putExtra("newProgramName",input.toString());
                                setResult(RESULT_OK,intent);
                            }
                        })
                        .positiveText("确定")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            }
                        })
                        .negativeText("取消")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        }).show();

            }
        });
        toolbar.addView(imageView,prams);
        toolbar.addView(ivEdit,prams);
        String programName = getIntent().getStringExtra("programName");
        toolbar.setTitle(programName);
    }


}
