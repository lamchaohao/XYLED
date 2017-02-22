package cn.com.hotled.xyled.activity;

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
import android.support.v7.app.AlertDialog;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.hotled.xyled.App;
import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.adapter.TypefaceAdapter;
import cn.com.hotled.xyled.bean.Program;
import cn.com.hotled.xyled.bean.TextContent;
import cn.com.hotled.xyled.bean.TypefaceFile;
import cn.com.hotled.xyled.dao.ProgramDao;
import cn.com.hotled.xyled.dao.TextContentDao;
import cn.com.hotled.xyled.decoration.WifiItemDecoration;
import cn.com.hotled.xyled.global.Global;
import cn.com.hotled.xyled.util.DensityUtil;
import cn.com.hotled.xyled.view.PhotoView;
import cn.com.hotled.xyled.view.numberpicker.NumberPicker;

public class EasyTextActivity extends BaseActivity implements View.OnClickListener,AdapterView.OnItemSelectedListener {

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
    @BindView(R.id.ib_fgText_inCenter)
    ImageButton ib_inCenter;
    @BindView(R.id.ib_fgText_trainY)
    ImageButton ib_trainY;
    @BindView(R.id.iv_fgText_setTime)
    ImageView iv_setTime;
    @BindView(R.id.spn_fgText_textEffect)
    Spinner spnTextEffect;
    @BindView(R.id.rl_fgText_flowLayout)
    RelativeLayout rlSelectFlow;
    @BindView(R.id.iv_fgText_flowStyle)
    ImageView iv_setFlowStyle;
    @BindView(R.id.spn_fgText_flowShowEffect)
    Spinner spn_setFlowEffect;
    @BindView(R.id.bt_fgText_flowShowSpeed)
    Button bt_setFlowSpeed;
    @BindView(R.id.sw_openFlow)
    Switch swOpenFlow;
    @BindView(R.id.ll_fgText_setFlow)
    LinearLayout llSetFlow;

    private Canvas canvas;
    private Bitmap targetBitmap;
    private Paint paint;
    private int mScreenWidth;
    private int mScreenHeight;
    private int RECOMAND_SIZE;
    private int mBaseX;
    private int mBaseY;
    private TextContent mTextContent;
    private TypefaceAdapter typefaceAdapter;
    private float mFrameTime;
    private float mStayTime;
    private long mProgramId;
    private TextContentDao mTextButtonDao;
    private boolean isLoadData;
    private boolean isWarnShowed;
    private ProgramDao mProgramDao;
    private Program mProgram;
    private Bitmap mFlowBitmap;
    private File mSelectedFontFile;


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
//
    private void loadData() {
        RECOMAND_SIZE = mScreenHeight;
        mBaseY=mScreenHeight/2;
        mTextContent =new TextContent();
        mTextContent.setProgramId(mProgramId);
        mTextContent.setTextSize(RECOMAND_SIZE);
        mTextContent.setTextColor(Color.RED);
        mTextContent.setTextBackgroudColor(Color.BLACK);

        mProgramId = getIntent().getLongExtra("programId", -1);
        mTextButtonDao = ((App) getApplication()).getDaoSession().getTextContentDao();
        mProgramDao = ((App) getApplication()).getDaoSession().getProgramDao();
        List<Program> programList=null;
        List<TextContent> textContentsList = null;
        if (mProgramId!=-1) {
            programList = mProgramDao.queryBuilder().where(ProgramDao.Properties.Id.eq(mProgramId)).list();
            textContentsList = mTextButtonDao.queryBuilder().where(TextContentDao.Properties.ProgramId.eq(mProgramId)).list();
        }

        if (programList!=null&&programList.size()==1){
            isLoadData=true;
            mProgram = programList.get(0);
            //新增的节目的话就是0，所以让其居中
            if (mProgram.getBaseY()==0) {
                if (mScreenHeight==32) {
                    mBaseY=24;
                }else if (mScreenHeight==64){
                    mBaseY=57;
                }else {
                    mBaseY=mScreenHeight-7;
                }
            }else {
                mBaseY=mProgram.getBaseY();
            }
            mBaseX = mProgram.getBaseX();

            mFrameTime=mProgram.getFrameTime();
            mStayTime = mProgram.getStayTime();
            spn_setFlowEffect.setSelection(mProgram.getFlowEffect());
            if (textContentsList!=null&&textContentsList.size()==1&&textContentsList.get(0)!=null){

                mTextContent = textContentsList.get(0);
                mEt_input.setText(mTextContent.getText());
                if (mProgram.getFlowBoundFile()!=null){
                    String absolutePath = mProgram.getFlowBoundFile().getAbsolutePath();
                    mFlowBitmap= BitmapFactory.decodeFile(absolutePath);
                    setFlowBound(absolutePath,false,false);
                }
                spnTextEffect.setSelection(mTextContent.getTextEffect());
                mBt_textSize.setText("size:"+mTextContent.getTextSize());
                if (mTextContent.getTypeface()!=null) {
                    int lastIndexOf = mTextContent.getTypeface().getName().lastIndexOf(".");
                    String fontFile = mTextContent.getTypeface().getName().substring(0, lastIndexOf);
                    mBt_setFont.setText(fontFile);
                }
                if (mTextContent.getIsbold()) {
                    ib_setBold.setBackgroundResource(R.drawable.recycler_bg_select);
                }
                if (mTextContent.getIsIlatic()) {
                    ib_setItalic.setBackgroundResource(R.drawable.recycler_bg_select);
                }
                if (mTextContent.getIsUnderline()) {
                    ib_setUnderLine.setBackgroundResource(R.drawable.recycler_bg_select);
                }
                //读取完后绘图
                drawText();
            }
            swOpenFlow.setChecked(mProgram.getUseFlowBound());
            if (swOpenFlow.isChecked()) {
                llSetFlow.setVisibility(View.VISIBLE);
            }else {
                llSetFlow.setVisibility(View.GONE);
            }

        }
        isLoadData=false;
    }

    private void initEditText(){
        mEt_input.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isLoadData) {
                    mTextContent.setText(s.toString());
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
        ib_inCenter.setOnClickListener(this);
        ib_trainY.setOnClickListener(this);
        spnTextEffect.setOnItemSelectedListener(this);
        rlSelectFlow.setOnClickListener(this);
        spn_setFlowEffect.setOnItemSelectedListener(this);
        bt_setFlowSpeed.setOnClickListener(this);
        swOpenFlow.setOnClickListener(this);
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
        mScreenWidth = getSharedPreferences(Global.SP_SCREEN_CONFIG,MODE_PRIVATE).getInt(Global.KEY_SCREEN_W,64);
        mScreenHeight = getSharedPreferences(Global.SP_SCREEN_CONFIG,MODE_PRIVATE).getInt(Global.KEY_SCREEN_H,32);

        targetBitmap = Bitmap.createBitmap(mScreenWidth, mScreenHeight, Bitmap.Config.ARGB_4444);

        if (targetBitmap!=null)
            canvas.setBitmap(targetBitmap);
        mPhotoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        mPhotoView.setImageBitmap(targetBitmap);
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImageToDisk();
                Intent intent = new Intent(EasyTextActivity.this, BrowsePhotoActivity.class);
                ActivityOptionsCompat options =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(EasyTextActivity.this,
                                mPhotoView, getString(R.string.shareNames));
                ActivityCompat.startActivity(EasyTextActivity.this, intent, options.toBundle());
            }
        });

    }

    private void saveImageToDisk() {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(getCacheDir()+"/preview.png");
            targetBitmap.compress(Bitmap.CompressFormat.PNG,100,fos);
            fos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void drawText() {
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawPaint(paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));

        //如果图片比所设置的宽，则需加长

        //先设置好画笔，才进行计算
        paint.setColor(mTextContent.getTextColor());
        paint.setTextSize(mTextContent.getTextSize());

        if (mTextContent.getTypeface()!=null) {
            Typeface typeface = Typeface.createFromFile(mTextContent.getTypeface());
            paint.setTypeface(typeface);
            if (mTextContent.getIsbold()) {//粗体
                paint.setTypeface(Typeface.create(typeface,Typeface.BOLD));
            }
            if (mTextContent.getIsIlatic()) {//斜体
                paint.setTypeface(Typeface.create(typeface,Typeface.ITALIC));
            }
            if (mTextContent.getIsbold()&& mTextContent.getIsIlatic()){//粗斜体
                paint.setTypeface(Typeface.create(typeface,Typeface.BOLD_ITALIC));
            }
        }
        else {
            paint.setTypeface(Typeface.DEFAULT);

            if (mTextContent.getIsbold()) {//粗体
                paint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
            }
            if (mTextContent.getIsIlatic()) {//斜体
                paint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.ITALIC));
            }
            if (mTextContent.getIsbold()&& mTextContent.getIsIlatic()){//粗斜体
                paint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD_ITALIC));
            }


        }
        if (mTextContent.getIsUnderline()) {//下划线
            paint.setUnderlineText(true);
        }else {
            paint.setUnderlineText(false);
        }
        paint.setTextAlign(Paint.Align.LEFT);

        //设置好画笔，开始计算
        float drawWidth = computeWidth(mTextContent.getText());
        //需要加上流水边框的宽度,左右都需要加上
        int tempBaseX=mBaseX;
        int tempBaseY=mBaseY;
        if (mFlowBitmap!=null&&mProgram.getUseFlowBound()){
            drawWidth+=mFlowBitmap.getHeight()*2;
            //加上流水边框后，文字需要偏移流水边框的一个宽度
            tempBaseX+=mFlowBitmap.getHeight();
            tempBaseY+=mFlowBitmap.getHeight();
        }
        if (drawWidth>mScreenWidth) {
            int width = (int) drawWidth;
            targetBitmap = Bitmap.createBitmap(width, mScreenHeight, Bitmap.Config.ARGB_4444);
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
        if (mTextContent.getText()!=null){
            canvas.drawText(mTextContent.getText(),tempBaseX,tempBaseY,paint);
        }

    }

    private float computeWidth(String text) {
        float drawWidth=mBaseX;
        if(text!=null){
            float[] widths=new float[text.length()];
            paint.getTextWidths(text,widths);
            for (int i = 0; i < widths.length; i++) {
                drawWidth+=widths[i];
            }
            Log.i("advance", "computeWidth:drawWidth "+drawWidth);
        }

        //为了避免OOM，bitmap的最大宽度设置为2048,bitmap的最大尺寸为4096*4096
        if (drawWidth>=2048){
            if (!isWarnShowed) {
                Toast.makeText(this, "文字过长，预览图后部分省略，但不会影响屏幕实际显示", Toast.LENGTH_LONG).show();
                isWarnShowed=true;
            }
            return 2048;
        }
        return drawWidth;
    }

    private void drawBgColor(float drawWidth) {
        Paint bgPaint=new Paint();
        bgPaint.setColor(mTextContent.getTextBackgroudColor());
        canvas.drawRect(mBaseX,0,drawWidth,mScreenHeight,bgPaint);
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

        int screenHeight = mScreenHeight;
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

        //down part
        for (int k = screenWidth; k >= 0; k--) {
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

        //left part
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
        NumberPicker picker = (NumberPicker) outerView.findViewById(R.id.number_picker_y);
        picker.setValue(mBaseY);
        picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mBaseY=newVal;
                drawText();
            }
        });
        new AlertDialog.Builder(this)
                .setTitle("设置垂直方向偏移量")
                .setView(outerView)
                .setPositiveButton("OK", null)
                .show();
    }
    //垂直居中
    private void setCenterHoriz(boolean needDrawtext){
        Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
        if (swOpenFlow.isChecked()&&mFlowBitmap!=null) {
            mBaseY = (targetBitmap.getHeight() - mFlowBitmap.getHeight()*2  - fontMetrics.bottom - fontMetrics.top) / 2;
        }else {
            mBaseY = (targetBitmap.getHeight() - fontMetrics.bottom - fontMetrics.top) / 2;
        }
        if (needDrawtext) {
            drawText();
        }
    }

    private void setTrainX() {

        View outerView = LayoutInflater.from(this).inflate(R.layout.alert_train_x, null);
        NumberPicker picker = (NumberPicker) outerView.findViewById(R.id.number_picker_x);
        picker.setValue(mBaseX);
        picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mBaseX=newVal;
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
        mTextContent.setTextBackgroudColor(color);
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
        mTextContent.setTextColor(textColor);
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
        mTextContent.setIsUnderline(!mTextContent.getIsUnderline());
        if (mTextContent.getIsUnderline()) {
            ib_setUnderLine.setBackgroundResource(R.drawable.recycler_bg_select);
        }else {
            ib_setUnderLine.setBackgroundResource(R.drawable.recycler_bg);
        }
        drawText();
    }

    private void setItalic() {
        mTextContent.setIsIlatic(!mTextContent.getIsIlatic());
        if (mTextContent.getIsIlatic()) {
            ib_setItalic.setBackgroundResource(R.drawable.recycler_bg_select);
        }else {
            ib_setItalic.setBackgroundResource(R.drawable.recycler_bg);
        }
        drawText();
    }

    private void setBold() {
        mTextContent.setIsbold(!mTextContent.getIsbold());
        if (mTextContent.getIsbold()) {
            ib_setBold.setBackgroundResource(R.drawable.recycler_bg_select);
        }else {
            ib_setBold.setBackgroundResource(R.drawable.recycler_bg);
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
        mSelectedFontFile = null;
        typefaceAdapter.setOnItemClickListener(new TypefaceAdapter.OnItemOnClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //1.先获取选择了哪个字体
                mSelectedFontFile = fileList.get(position).getFile();
                mTextContent.setTypeface(fileList.get(position).getFile());
            }
        });


        new AlertDialog.Builder(this)
                .setTitle("Choose Typeface")
                .setView(view)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mSelectedFontFile!=null){
                            mTextContent.setTypeface(mSelectedFontFile);
                            int lastIndexOf = mSelectedFontFile.getName().lastIndexOf(".");
                            String fontFile = mSelectedFontFile.getName().substring(0, lastIndexOf);
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
        View outerView = LayoutInflater.from(this).inflate(R.layout.wheel_view, null);
        NumberPicker sizePicker = (NumberPicker) outerView.findViewById(R.id.number_picker_textSize);
        sizePicker.setValue(mTextContent.getTextSize());
        sizePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mBt_textSize.setText("size:"+newVal);
                mTextContent.setTextSize(newVal);
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
            case R.id.ib_fgText_inCenter:
                setCenterHoriz(true);
                break;
            case R.id.ib_fgText_trainY:
                setTrainY();
                break;
            case R.id.sw_openFlow:
                setUseFlowToggle();
                break;
            case R.id.rl_fgText_flowLayout:
                startActivityForResult(new Intent(this,SelectFlowActivity.class),SELECT_FLOW_CODE);
                break;
            case R.id.bt_fgText_flowShowSpeed:

                break;
        }
    }

    private void setUseFlowToggle() {
        if (swOpenFlow.isChecked()) {
            setCenterHoriz(false);
            llSetFlow.setVisibility(View.VISIBLE);
            mProgram.setUseFlowBound(true);
            if (mFlowBitmap==null){
                boolean isFirstIn = getSharedPreferences(Global.SP_SYSTEM_CONFIG, MODE_PRIVATE).getBoolean(Global.KEY_IS_FIRSTIN, false);
                if (!isFirstIn) {
                    File file = new File(getFilesDir() + "/flow/bounds_1_01.bmp");
                    if (file.exists()) {
                        mProgram.setFlowBoundFile(file);
                        setFlowBound(file.toString(),false,false);
                    }
                }

            }
            if (mTextContent.getText()!=null){
                drawText();
            }

        }else {
            mProgram.setUseFlowBound(false);
            llSetFlow.setVisibility(View.GONE);
            drawText();
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
            mProgram.setFlowBoundFile(new File(fileName));
            setFlowBound(fileName,true,true);

        }
    }

    private void setFlowBound(String fileName,boolean needDrawText,boolean needCenterHozi) {
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
        if(needDrawText)
            drawText();
        if (needCenterHozi) {
            setCenterHoriz(false);
        }
    }



    @Override
    public void onBackPressed() {
        if (mTextContent.getId()==0)
            mTextContent.setId(System.currentTimeMillis()*2);
        mTextContent.setProgramId(mProgramId);
        mTextButtonDao.insertOrReplaceInTx(mTextContent);
        mProgram.setBaseY(mBaseY);
        mProgram.setBaseX(mBaseX);
        mProgram.setStayTime(getStayTime());
        mProgram.setFrameTime(getFrameTime());
        mProgramDao.insertOrReplace(mProgram);

        EasyTextActivity.super.onBackPressed();
    }

    @Override
    public void onCreateCustomToolBar(final Toolbar toolbar) {
        super.onCreateCustomToolBar(toolbar);
        ImageView ivEdit=new ImageView(this);
        Toolbar.LayoutParams prams =new Toolbar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        prams.gravity= Gravity.RIGHT;
        prams.rightMargin= DensityUtil.dp2px(this,10);

        ivEdit.setImageResource(R.drawable.ic_mode_edit_white_24dp);
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
        toolbar.addView(ivEdit,prams);
        String programName = getIntent().getStringExtra("programName");
        toolbar.setTitle(programName);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId()==R.id.spn_fgText_textEffect){
            mTextContent.setTextEffect(position);
        }else if (parent.getId()==R.id.spn_fgText_flowShowEffect){
            mProgram.setFlowEffect(position);
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
