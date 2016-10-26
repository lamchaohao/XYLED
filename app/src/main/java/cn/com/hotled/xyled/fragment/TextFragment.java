package cn.com.hotled.xyled.fragment;

import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
        return inflate;
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
    @OnClick(R.id.ib_fgText_textBgRed)
    public void setTextBgMore(){
        // TODO: 2016/10/26

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
