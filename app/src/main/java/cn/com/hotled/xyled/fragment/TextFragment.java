package cn.com.hotled.xyled.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import org.w3c.dom.Text;

import java.io.File;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.adapter.TypefaceListAdapter;
import cn.com.hotled.xyled.view.WheelView;
import jp.wasabeef.richeditor.RichEditor;


public class TextFragment extends Fragment {

    @BindView(R.id.rich_fgText_editor)
    RichEditor mEditor;
    @BindView(R.id.bt_fgText_TextSize)
    Button bt_TextSize;
    @BindView(R.id.bt_fgText_Font)
    Button bt_setFont;
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
    @BindView(R.id.sb_fgText_X)
    SeekBar sb_X;
    @BindView(R.id.sb_fgText_Y)
    SeekBar sb_Y;
    @BindView(R.id.tv_fgText_Xprogress)
    TextView tv_Xprogress;
    @BindView(R.id.tv_fgText_Yprogress)
    TextView tv_Yprogress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_text, container, false);
        ButterKnife.bind(this,inflate);
        mEditor.setEditorHeight(100);
        mEditor.setEditorFontSize(18);
        mEditor.setEditorFontColor(Color.RED);
        mEditor.setPadding(10, 10, 10, 10);
        mEditor.setPadding(10, 10, 10, 10);
        mEditor.setPlaceholder("请在此输入文字");
        initSeekBar();
        return inflate;
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ButterKnife.bind(getActivity());
    }

    @OnClick(R.id.ib_fgText_setBold)
    public void setBold(){
        mEditor.setBold();
        String html = mEditor.getHtml();
        Log.i("textFragm",html);
    }
    @OnClick(R.id.ib_fgText_setItalic)
    public void setItalic(){
        mEditor.setItalic();
    }
    @OnClick(R.id.ib_fgText_setUnderLine)
    public void setUnderLine(){
        mEditor.setUnderline();
    }
    @OnClick(R.id.ib_fgText_textColorRed)
    public void setTextColorRed(){
        mEditor.setTextColor(Color.RED);
    }
    @OnClick(R.id.ib_fgText_textColorGreen)
    public void setTextColorGreen(){
        mEditor.setTextColor(Color.GREEN);
    }
    @OnClick(R.id.ib_fgText_textColorBlue)
    public void setTextColorBlue(){
        mEditor.setTextColor(Color.BLUE);
    }
    @OnClick(R.id.ib_fgText_textColorMore)
    public void setTextColorMore(){
        // TODO: 2016/10/26
        ColorPickerDialogBuilder.with(getContext())
                .setTitle("选择字体颜色")
                .initialColor(Color.RED)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int i) {
                        Toast.makeText(getContext(), "ColorSelected:"+Integer.toHexString(i), Toast.LENGTH_SHORT).show();
                    }
                })
                .setPositiveButton("确定", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, Integer[] integers) {
                        mEditor.setTextColor(i);

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
    @OnClick(R.id.ib_fgText_textBgRed)
    public void setTextBgRed(){
        mEditor.setTextBackgroundColor(Color.RED);
    }
    @OnClick(R.id.ib_fgText_textBgGreen)
    public void setTextBgGreen(){
        mEditor.setTextBackgroundColor(Color.GREEN);
    }
    @OnClick(R.id.ib_fgText_textBgBlue)
    public void setTextBgBlue(){
        mEditor.setTextBackgroundColor(Color.BLUE);
    }
    @OnClick(R.id.ib_fgText_textBgMore)
    public void setTextBgMore(){
        // TODO: 2016/10/26
        ColorPickerDialogBuilder.with(getContext())
                .setTitle("选择字体背景颜色")
                .initialColor(Color.RED)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int i) {
                        Toast.makeText(getContext(), "ColorSelected:"+Integer.toHexString(i), Toast.LENGTH_SHORT).show();
                    }
                })
                .setPositiveButton("确定", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, Integer[] integers) {
                        mEditor.setTextBackgroundColor(i);
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

    @OnClick(R.id.bt_fgText_TextSize)
    public void setTextSize(){
        String[] textSizePxList=new String[100];
        for (int i=0;i<100;i++){
            textSizePxList[i]=i+1+"";
        }

        View outerView = LayoutInflater.from(getContext()).inflate(R.layout.wheel_view, null);
        WheelView wv = (WheelView) outerView.findViewById(R.id.wheelview);
        wv.setOffset(2);
        wv.setItems(Arrays.asList(textSizePxList));
        wv.setSeletion(3);
        wv.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                    bt_TextSize.setText("size:"+item);
                    mEditor.setFontSize(selectedIndex);
                Log.i("textFragm",selectedIndex+"");
            }
        });

        new AlertDialog.Builder(getContext())
                .setTitle("WheelView in Dialog")
                .setView(outerView)
                .setPositiveButton("OK", null)
                .show();

    }

    @OnClick(R.id.bt_fgText_Font)
    public void setFont(){
        File file =new File("/system/fonts");
        if (file.exists()){
            Toast.makeText(getContext(), "exists", Toast.LENGTH_SHORT).show();
        }
        File[] files = file.listFiles();
        for (int i=0;i<files.length;i++){
            Log.i("textFragm",files[i].getName().toString());
        }
        View view = LayoutInflater.from(getContext()).inflate(R.layout.typeface_list, null);
        ListView typeFaceListView = (ListView) view.findViewById(R.id.typeFaceListView);
        typeFaceListView.setAdapter(new TypefaceListAdapter(files,getContext()));
        new AlertDialog.Builder(getContext())
                .setTitle("Choose Typeface")
                .setView(view)
                .setPositiveButton("OK", null)
                .show();

    }



    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
