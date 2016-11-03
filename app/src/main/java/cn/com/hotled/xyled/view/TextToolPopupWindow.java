package cn.com.hotled.xyled.view;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import java.io.File;
import java.util.Arrays;

import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.adapter.TypefaceListAdapter;
import cn.com.hotled.xyled.util.DensityUtil;

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
    SeekBar sb_X;
    SeekBar sb_Y;
    TextView tv_Xprogress;
    TextView tv_Yprogress;
    Context mContext;
    public TextToolPopupWindow(Context context) {
        super(context);
        mContext=context;
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View inflate = layoutInflater.inflate(R.layout.popupwind_textfragment, null);
        initView(inflate);

        setContentView(inflate);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(DensityUtil.dp2px(mContext,250));
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FAFAFA")));

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
        sb_X= (SeekBar) inflate.findViewById(R.id.sb_fgText_X);
        sb_Y= (SeekBar) inflate.findViewById(R.id.sb_fgText_Y);
        tv_Xprogress= (TextView) inflate.findViewById(R.id.tv_fgText_Xprogress);
        tv_Yprogress= (TextView) inflate.findViewById(R.id.tv_fgText_Yprogress);

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
        initSeekBar();
    }

    private void initSeekBar() {
        sb_X.setProgress(50);
        sb_Y.setProgress(50);
        tv_Xprogress.setText(0+"");
        tv_Yprogress.setText(0+"");
        sb_X.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_Xprogress.setText(progress-50+"");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sb_Y.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_Yprogress.setText(progress-50+"");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    public void setBold(){
    }
    public void setItalic(){
    }
    public void setUnderLine(){
    }
    public void setTextColorRed(){
    }
    public void setTextColorGreen(){
    }
    public void setTextColorBlue(){
    }
        /**
     * 设置文本字体
     */
    public void setFont(){
        File file =new File("/system/fonts");
        File[] files = file.listFiles();
        View view = LayoutInflater.from(mContext).inflate(R.layout.typeface_list, null);
        ListView typeFaceListView = (ListView) view.findViewById(R.id.typeFaceListView);
        typeFaceListView.setAdapter(new TypefaceListAdapter(files,mContext));
        new AlertDialog.Builder(mContext)
                .setTitle("Choose Typeface")
                .setView(view)
                .setPositiveButton("OK", null)
                .show();

    }

    public void setTextSize(){
        String[] textSizePxList=new String[100];
        for (int i=0;i<100;i++){
            textSizePxList[i]=i+1+"";
        }

        View outerView = LayoutInflater.from(mContext).inflate(R.layout.wheel_view, null);
        WheelView wv = (WheelView) outerView.findViewById(R.id.wheelview);
        wv.setOffset(2);
        wv.setItems(Arrays.asList(textSizePxList));
        wv.setSeletion(3);
        wv.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                    bt_TextSize.setText("size:"+item);
                // TODO: 2016/11/3 还没正式设置字体大小

            }
        });

        new AlertDialog.Builder(mContext)
                .setTitle("WheelView in Dialog")
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
                        // TODO: 2016/11/3 还没正式设置字体颜色
                    }
                })
                .setPositiveButton("确定", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, Integer[] integers) {


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
                        // TODO: 2016/11/3 还没正式设置字体颜色 //设置字体背景颜色
                    }
                })
                .setPositiveButton("确定", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, Integer[] integers) {

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
                Toast.makeText(mContext, "test 加粗", Toast.LENGTH_SHORT).show();
                break;
            case R.id.ib_fgText_setItalic:
                Toast.makeText(mContext, "test ib_fgText_setItalic", Toast.LENGTH_SHORT).show();
                break;
            case R.id.ib_fgText_setUnderLine:
                break;
            case R.id.ib_fgText_textColorRed:
                break;
            case R.id.ib_fgText_textColorGreen:
                break;
            case R.id.ib_fgText_textColorBlue:
                break;
            case R.id.ib_fgText_textColorMore:
                setTextBgMore();
                break;
            case R.id.ib_fgText_textBgRed:
                break;
            case R.id.ib_fgText_textBgGreen:
                break;
            case R.id.ib_fgText_textBgBlue:
                break;
            case R.id.ib_fgText_textBgMore:
                setTextBgMore();
                break;

        }
    }
}
