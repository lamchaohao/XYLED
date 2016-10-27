package cn.com.hotled.xyled.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.Unbinder;
import cn.com.hotled.xyled.R;
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
    @BindView(R.id.et_fgText_X)
    EditText et_X;
    @BindView(R.id.et_fgText_Y)
    EditText et_Y;
    @BindView(R.id.sb_fgText_X)
    SeekBar sb_X;
    @BindView(R.id.sb_fgText_Y)
    SeekBar sb_Y;


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
        sb_X.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int showProgress=progress-50;
                et_X.setText(showProgress+"");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.i("initSeekbar", "onStartTrackingTouch");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.i("initSeekbar", "onStopTrackingTouch");
            }
        });

        sb_Y.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int showProgress=progress-50;
                et_Y.setText(showProgress+"");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        et_X.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //// TODO: 2016/10/27 会产生只有“-”的结果，还有setProgress后，会调用onProgressChanged，在这里也会减去50，所以进度条一直都是-50
                String substring="";
                if (s.toString().contains("-")){
                    substring = s.toString().substring(s.toString().lastIndexOf("-"));
                }else {
                    substring=s.toString();
                }
                int i = Integer.parseInt(substring);
                sb_X.setProgress(i);
            }
        });

        et_Y.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String substring="";
                if (s.toString().contains("-")){
                    substring = s.toString().substring(s.toString().lastIndexOf("-"));
                }else {
                    substring=s.toString();
                }
                int i = Integer.parseInt(substring);
                sb_Y.setProgress(i);
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





    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
