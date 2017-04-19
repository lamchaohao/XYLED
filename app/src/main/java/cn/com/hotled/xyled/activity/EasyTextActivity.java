package cn.com.hotled.xyled.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

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
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.hotled.xyled.App;
import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.bean.Program;
import cn.com.hotled.xyled.bean.TextContent;
import cn.com.hotled.xyled.dao.ProgramDao;
import cn.com.hotled.xyled.dao.TextContentDao;
import cn.com.hotled.xyled.global.Common;
import cn.com.hotled.xyled.global.Global;
import cn.com.hotled.xyled.util.android.DensityUtil;
import cn.com.hotled.xyled.util.android.DrawTextUtil;
import cn.com.hotled.xyled.view.numberpicker.NumberPicker;
import cn.com.hotled.xyled.view.photoview.PhotoView;

import static cn.com.hotled.xyled.global.Global.TEXT_CONTENT_CHANGE_CODE;

public class EasyTextActivity extends BaseActivity implements View.OnClickListener,AdapterView.OnItemSelectedListener,
        SeekBar.OnSeekBarChangeListener,CompoundButton.OnCheckedChangeListener {

    private static final int SELECT_FLOW_CODE = 202;
    private static final int SELECT_FONT_CODE = 303;
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
    @BindView(R.id.spn_fgText_textEffect)
    Spinner spnTextEffect;
    @BindView(R.id.rl_fgText_flowLayout)
    RelativeLayout rlSelectFlow;
    @BindView(R.id.iv_fgText_flowStyle)
    ImageView iv_setFlowStyle;
    @BindView(R.id.spn_fgText_flowShowEffect)
    Spinner spn_setFlowEffect;
    @BindView(R.id.spn_fgText_flowShowSpeed)
    Spinner spn_setFlowSpeed;
    @BindView(R.id.sw_openFlow)
    Switch swOpenFlow;
    @BindView(R.id.sw_reverse)
    Switch swReverse;
    @BindView(R.id.ll_fgText_setFlow)
    LinearLayout llSetFlow;
    @BindView(R.id.sb_fgText_stayTime)
    SeekBar sb_stayTime;
    @BindView(R.id.sb_fgText_speed)
    SeekBar sb_speed;
    @BindView(R.id.tv_fgText_showSpeed)
    TextView tv_showSpeed;
    @BindView(R.id.tv_fgText_showStaytime)
    TextView tv_showStaytime;

    private Canvas canvas;
    private Bitmap targetBitmap;
    private Paint paint;
    private int mScreenWidth;
    private int mScreenHeight;
    private int mBaseX;
    private int mBaseY;
    private TextContent mTextContent;
    private long mProgramId;
    private TextContentDao mTextContentDao;
    private boolean isLoadData;
    private boolean isWarnShowed;
    private ProgramDao mProgramDao;
    private Program mProgram;
    private Bitmap mFlowBitmap;
    private DrawTextUtil mDrawUtil;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_easy_text);
        ButterKnife.bind(this);
        initPhotoView();
        initEditText();
        loadData();
        initOnclickEven();
    }

    private void loadData() {
        int recomandSize = mScreenHeight;
        mProgramId = getIntent().getLongExtra(Common.EX_programId, -1);
        mBaseY=mScreenHeight/2;
        mTextContent =new TextContent();
        mTextContent.setProgramId(mProgramId);
        mTextContent.setTextSize(recomandSize);
        mTextContent.setTextColor(Color.RED);
        mTextContent.setTextBackgroudColor(Color.BLACK);
        int orient = getSharedPreferences(Global.SP_SCREEN_CONFIG, MODE_PRIVATE).getInt(Global.KEY_DATA_ORIENTATION, 0);
        mDrawUtil = new DrawTextUtil(orient);
        mTextContentDao = ((App) getApplication()).getDaoSession().getTextContentDao();
        mProgramDao = ((App) getApplication()).getDaoSession().getProgramDao();
        List<Program> programList=null;
        List<TextContent> textContentsList = null;
        if (mProgramId!=-1) {
            programList = mProgramDao.queryBuilder().where(ProgramDao.Properties.Id.eq(mProgramId)).list();
            textContentsList = mTextContentDao.queryBuilder().where(TextContentDao.Properties.ProgramId.eq(mProgramId)).list();
        }
        if (programList!=null&&programList.size()==1){
            isLoadData=true;
            mProgram = programList.get(0);

            //新增的节目的话就是0，所以让其居中
            if (mProgram.getBaseY()==0) {
                if (mScreenHeight==32) {
                    mBaseY=28;
                }else if (mScreenHeight==64){
                    mBaseY=57;
                }else {
                    mBaseY=mScreenHeight-7;
                }
            }else {
                mBaseY=mProgram.getBaseY();
            }
            mBaseX = mProgram.getBaseX();

            float frameTime=mProgram.getFrameTime();
            float stayTime = mProgram.getStayTime();
            tv_showSpeed.setText(String.valueOf((int) frameTime));
            tv_showStaytime.setText(String.valueOf((int)stayTime));
            stayTime *= 10;
            sb_speed.setProgress((int) frameTime);
            sb_stayTime.setProgress((int) stayTime);

            spn_setFlowEffect.setSelection(mProgram.getFlowEffect());
            spn_setFlowSpeed.setSelection(mProgram.getFlowSpeed());
            if (textContentsList!=null&&textContentsList.size()==1&&textContentsList.get(0)!=null){

                mTextContent = textContentsList.get(0);
                mEt_input.setText(mTextContent.getText());
                if (mProgram.getFlowBoundFile()!=null){
                    String absolutePath = mProgram.getFlowBoundFile().getAbsolutePath();
                    mFlowBitmap= BitmapFactory.decodeFile(absolutePath);
                    setFlowBound(absolutePath,false,false);
                }

                mBt_textSize.setText(String.valueOf(mTextContent.getTextSize()));
                if (mTextContent.getTypeface()!=null) {
                    String easyName = dealFileName(mTextContent.getTypeface().getName());
                    mBt_setFont.setText(easyName);
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
                spnTextEffect.setSelection(mTextContent.getTextEffect());//自带绘图
            }else {
                sb_speed.setProgress(50);
                sb_stayTime.setProgress(50);
                tv_showSpeed.setText(String.valueOf(13));
                tv_showStaytime.setText(String.valueOf(5));
                mProgram.setFrameTime(13);
                mProgram.setStayTime(5);
            }
            swOpenFlow.setChecked(mProgram.getUseFlowBound());
            if (swOpenFlow.isChecked()) {
                llSetFlow.setVisibility(View.VISIBLE);
            }else {
                llSetFlow.setVisibility(View.GONE);
            }
            swReverse.setChecked(mTextContent.getIsTextReverse());
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
        spn_setFlowSpeed.setOnItemSelectedListener(this);
        swOpenFlow.setOnClickListener(this);
        swReverse.setOnCheckedChangeListener(this);
        sb_stayTime.setOnSeekBarChangeListener(this);
        sb_speed.setOnSeekBarChangeListener(this);
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
            fos = new FileOutputStream(getCacheDir()+Common.FL_PREVIEW);
            mDrawUtil.getTargetBitmap().compress(Bitmap.CompressFormat.PNG,100,fos);
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


    private void drawText(){
        mDrawUtil.setTextContent(mTextContent)
                .setProgram(mProgram)
                .setBaseX(mBaseX)
                .setBaseY(mBaseY)
                .setCanvas(canvas)
                .setPaint(paint)
                .setContext(this)
                .setPhotoView(mPhotoView)
                .setScreenWidth(mScreenWidth)
                .setScreenHeight(mScreenHeight)
                .setTargetBitmap(getTargetBitmap())
                .setFlowBitmap(mFlowBitmap)
                .setWarnShowed(isWarnShowed)
                .drawText();
        isWarnShowed=mDrawUtil.isWarnShowed();

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
                .setTitle(R.string.msg_set_vertical)
                .setView(outerView)
                .setPositiveButton(R.string.msg_confirm, null)
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
                .setTitle(R.string.msg_set_horizon)
                .setView(outerView)
                .setPositiveButton(R.string.msg_confirm, null)
                .show();
    }

    private void setTextBgMore() {
        ColorPickerDialogBuilder.with(this)
                .setTitle(R.string.msg_set_backgroud)
                .initialColor(Color.RED)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int i) {

                    }
                })
                .setPositiveButton(R.string.msg_confirm, new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, Integer[] integers) {
                        setTextBgColor(i);

                    }
                })
                .setNegativeButton(R.string.msg_cancle, new DialogInterface.OnClickListener() {
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
                .setTitle(R.string.msg_set_fontColor)
                .initialColor(Color.RED)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int i) {

                    }
                })
                .setPositiveButton(R.string.msg_confirm, new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, Integer[] integers) {
                        setTextColor(i);

                    }
                })
                .setNegativeButton(R.string.msg_cancle, new DialogInterface.OnClickListener() {
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


    private void setTextSize(){
        View outerView = LayoutInflater.from(this).inflate(R.layout.wheel_view, null);
        NumberPicker sizePicker = (NumberPicker) outerView.findViewById(R.id.number_picker_textSize);
        sizePicker.setValue(mTextContent.getTextSize());
        sizePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mBt_textSize.setText(String.valueOf(newVal));
                mTextContent.setTextSize(newVal);
                drawText();
            }
        });

        new AlertDialog.Builder(this)
                .setTitle(R.string.msg_set_text_size)
                .setView(outerView)
                .setPositiveButton(R.string.msg_confirm, null)
                .show();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_fgText_textSize:
                setTextSize();
                break;
            case R.id.bt_fgText_font: //字体
                Intent intent = new Intent(this,SelectFontActivity.class);
                startActivityForResult(intent,SELECT_FONT_CODE);
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
                setTextBgColor(Color.BLACK);
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
                    File file = new File(getFilesDir() + Common.FL_FLOW_DEFAULT);
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


    public Bitmap getTargetBitmap() {
        return targetBitmap;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode==RESULT_OK&&requestCode==SELECT_FLOW_CODE){
            String fileName = data.getStringExtra(Common.EX_setlectFlow);
            mProgram.setFlowBoundFile(new File(fileName));
            setFlowBound(fileName,true,true);
        }else if (resultCode==RESULT_OK&&requestCode==SELECT_FONT_CODE){
            String fileName = data.getStringExtra(Common.EX_setelctFont);
            File fontFile = new File(fileName);
            if (fontFile.exists()){
                mTextContent.setTypeface(fontFile);
                String easyName = dealFileName(fontFile.getName());
                mBt_setFont.setText(easyName);
                drawText();
            }
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
        mTextContent.setProgramId(mProgram.getId());
        mTextContentDao.insertOrReplace(mTextContent);
        mProgram.setBaseY(mBaseY);
        mProgram.setBaseX(mBaseX);
        mProgram.setTextContent(mTextContent);
        mProgramDao.insertOrReplace(mProgram);
        Intent intent = new Intent();
        intent.putExtra(Common.EX_textContent,mTextContent.getText());
        setResult(TEXT_CONTENT_CHANGE_CODE,intent);
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
                builder.title(R.string.msg_edit_programName)
                        .inputType(InputType.TYPE_CLASS_TEXT)
                        .input(getResources().getString(R.string.msg_hint_programName), mProgram.getProgramName(), new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                mProgram.setProgramName(input.toString());
                                toolbar.setTitle(input.toString());
                                setSupportActionBar(toolbar);
                                dialog.dismiss();
                                Intent intent = new Intent();
                                intent.putExtra(Common.EX_newProGramName,input.toString());
                                setResult(RESULT_OK,intent);
                            }
                        })
                        .positiveText(getResources().getString(R.string.msg_confirm))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            }
                        })
                        .negativeText(getResources().getString(R.string.msg_cancle))
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        }).show();

            }
        });
        toolbar.addView(ivEdit,prams);
        String programName = getIntent().getStringExtra(Common.EX_programName);
        toolbar.setTitle(programName);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spn_fgText_flowShowSpeed:
                mProgram.setFlowSpeed(position);
                break;
            case R.id.spn_fgText_textEffect:
                mTextContent.setTextEffect(position);
                drawText();
                break;
            case R.id.spn_fgText_flowShowEffect:
                mProgram.setFlowEffect(position);
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        switch (seekBar.getId()) {
            case R.id.sb_fgText_speed:
                int speed = progress;
                tv_showSpeed.setText(String.valueOf(speed));
                mProgram.setFrameTime(speed);
                break;
            case R.id.sb_fgText_stayTime:
                int second = progress/10;
                tv_showStaytime.setText(String.valueOf(second));
                mProgram.setStayTime(second);
                break;

        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mTextContent.setIsTextReverse(isChecked);
        drawText();
    }

    private String dealFileName(String fileName) {
        if (fileName.contains("-Regular")){
            //2.get lastIndexof -Regular
            int lastIndexOf = fileName.lastIndexOf("-Regular");
            //3.substring
            String substring=null;
            if (lastIndexOf!=-1){
                substring = fileName.substring(0, lastIndexOf);
                return substring;
            }else {
                return fileName;
            }
        }else {
            int lastIndexOf = fileName.lastIndexOf(".");
            //3.substring
            String substring=null;
            if (lastIndexOf!=-1){
                substring = fileName.substring(0, lastIndexOf);
                return substring;
            }else {
                return fileName;
            }
        }

    }
}
