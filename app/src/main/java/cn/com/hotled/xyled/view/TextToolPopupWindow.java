package cn.com.hotled.xyled.view;


import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
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
import cn.com.hotled.xyled.adapter.TextButtonAdapter;
import cn.com.hotled.xyled.adapter.TypefaceAdapter;
import cn.com.hotled.xyled.bean.TextButton;
import cn.com.hotled.xyled.bean.TypefaceFile;
import cn.com.hotled.xyled.decoration.WifiItemDecoration;
import cn.com.hotled.xyled.fragment.TextFragment;

/**
 * Created by Lam on 2016/11/3.
 */

public class TextToolPopupWindow extends PopupWindow implements View.OnClickListener{
    private Button bt_TextSize;
    private Button bt_setFont;
    private ImageButton ib_setBold;
    private ImageButton ib_setItalic;
    private ImageButton ib_setUnderLine;
    private ImageButton ib_setFrameTime;
    private ImageButton ib_RedText;
    private ImageButton ib_GreenText;
    private ImageButton ib_BlueText;
    private ImageButton ib_moreColor;
    private ImageButton ib_RedTextBg;
    private ImageButton ib_GreenTextBg;
    private ImageButton ib_BlueTextBg;
    private ImageButton ib_MoreTextBg;
    private ImageButton ib_trainX;
    private ImageButton ib_trainY;
    private ImageButton mIb_selectAll;

    private Context mContext;

    private List<TextButton> mTextButtonList;
    private TextFragment mTextFragment;
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
        ib_setFrameTime= (ImageButton) inflate.findViewById(R.id.ib_fgText_setFrameTime);
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
        mIb_selectAll = (ImageButton) inflate.findViewById(R.id.ib_fgText_selectAll);

        bt_TextSize.setOnClickListener(this);
        bt_setFont.setOnClickListener(this);
        ib_setBold.setOnClickListener(this);
        ib_setItalic.setOnClickListener(this);
        ib_setUnderLine.setOnClickListener(this);
        ib_setFrameTime.setOnClickListener(this);
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
        mIb_selectAll.setOnClickListener(this);
    }




    private void setBold(){
        if (TextButtonAdapter.SELECT_MODE){
            //多选
            for (TextButton tb:mTextButtonList){
                if (tb.isSelected())
                    tb.setIsbold(!tb.isbold());
            }
            mTextFragment.textButtonAdapter.notifyDataSetChanged();
        }else {
            //单选
            if (mTextFragment.getmTextButton()!=null){
                mTextFragment.getmTextButton().setIsbold(!mTextFragment.getmTextButton().isbold());
                mTextFragment.textButtonAdapter.notifyItemChanged(mTextFragment.getPosition());
            }

        }
        mTextFragment.drawText();
    }
    private void setItalic(){
        if (TextButtonAdapter.SELECT_MODE){
            //多选
            for (TextButton tb:mTextButtonList){
                if (tb.isSelected())
                    tb.setIsIlatic(!tb.isIlatic());
            }
            mTextFragment.textButtonAdapter.notifyDataSetChanged();
        }else {
            //单选
            if (mTextFragment.getmTextButton()!=null){
                mTextFragment.getmTextButton().setIsIlatic(!mTextFragment.getmTextButton().isIlatic());
                mTextFragment.textButtonAdapter.notifyItemChanged(mTextFragment.getPosition());
            }

        }
        mTextFragment.drawText();

    }
    private void setUnderLine(){
        if (TextButtonAdapter.SELECT_MODE){
            //多选
            for (TextButton tb:mTextButtonList){
                if (tb.isSelected())
                    tb.setIsUnderline(!tb.isUnderline());
            }
            mTextFragment.textButtonAdapter.notifyDataSetChanged();
        }else {
            //单选
            if (mTextFragment.getmTextButton()!=null){
                mTextFragment.getmTextButton().setIsUnderline(!mTextFragment.getmTextButton().isUnderline());
                mTextFragment.textButtonAdapter.notifyItemChanged(mTextFragment.getPosition());
            }

        }
        mTextFragment.drawText();
    }

    private void setFrameTime() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.content_setframetime, null);
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
        sb_speed.setProgress((int) (mTextFragment.getFrameTime()/2.56));
        sb_stay.setProgress((int) (mTextFragment.getStayTime()/2.56));

        new AlertDialog.Builder(mContext)
                .setTitle("设置时间")
                .setView(view)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mTextFragment.setFrameTime(frameTime[0]);
                        mTextFragment.setStayTime(stayTime[0]);
                    }
                })
                .show();

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
        for (TextButton tb:mTextButtonList){

            tb.setTextBackgroudColor(color);
            mTextFragment.textButtonAdapter.notifyDataSetChanged();

        }
        mTextFragment.drawText();

    }

        /**
     * 设置文本字体
     */
    public void setFont(){
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
            wv.setSeletion(mTextFragment.getRecomandSize()-1);
        }else {
            //取之前设置的字体大小的值
            if (mTextFragment.getmTextButton()!=null)
                wv.setSeletion(mTextFragment.getmTextButton().getTextSize());
            else //如果没有点击就设置，则取推荐字体大小
                wv.setSeletion(mTextFragment.getRecomandSize()-1);
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


    private void selectAll() {
        if (TextButtonAdapter.SELECT_MODE) {
            int count=-1;
            int selectedItemCount=0;
            for (TextButton textButton : mTextButtonList) {
                count++;
                if (!textButton.isSelected()) {
                    textButton.setIsSelected(true);
                    mTextFragment.textButtonAdapter.notifyItemChanged(count);
                }else {
                    //计算已经选择的个数
                    selectedItemCount++;
                }
            }
            if (selectedItemCount==mTextButtonList.size()){
                //如果已经全选，则全不选
                for (TextButton tb : mTextButtonList) {
                    tb.setIsSelected(false);
                }
                mTextFragment.textButtonAdapter.notifyDataSetChanged();
            }
        }else {
            TextButtonAdapter.SELECT_MODE=true;
            Snackbar.make(mTextFragment.ib_fgText_settool,"进入多选模式",Snackbar.LENGTH_SHORT).show();
            selectAll();
        }
    }

    private void setTrainY() {
        this.dismiss();//先消失，才能实时看到变化
        View outerView = LayoutInflater.from(mContext).inflate(R.layout.alert_train_y, null);
        RulerView ruler = (RulerView) outerView.findViewById(R.id.ruler_view_vertical);
        ruler.setValue(mTextFragment.getBaseY());
        ruler.setOnValueChangeListener(new RulerView.OnValueChangeListener() {
            @Override
            public void onValueChange(int value) {
                mTextFragment.setBaseY(value);
                mTextFragment.drawText();
            }
        });
        new AlertDialog.Builder(mContext)
                .setTitle("设置垂直方向偏移量")
                .setView(outerView)
                .setPositiveButton("OK", null)
                .show();
    }

    private void setTrainX() {
        this.dismiss();//先消失，才能实时看到变化
        View outerView = LayoutInflater.from(mContext).inflate(R.layout.alert_train_x, null);
        RulerView ruler = (RulerView) outerView.findViewById(R.id.ruler_view_horizon);
        ruler.setValue(mTextFragment.getBaseX());
        ruler.setOnValueChangeListener(new RulerView.OnValueChangeListener() {
            @Override
            public void onValueChange(int value) {
                mTextFragment.setBaseX(value);
                mTextFragment.drawText();
            }
        });
        new AlertDialog.Builder(mContext)
                .setTitle("设置水平方向偏移量")
                .setView(outerView)
                .setPositiveButton("OK", null)
                .show();
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
                setItalic();
                break;
            case R.id.ib_fgText_setUnderLine:
                setUnderLine();
                break;
            case R.id.ib_fgText_setFrameTime:
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
            case R.id.ib_fgText_selectAll:
                selectAll();
                break;
        }
    }



}
