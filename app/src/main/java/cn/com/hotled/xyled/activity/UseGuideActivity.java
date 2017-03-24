package cn.com.hotled.xyled.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.global.Common;

public class UseGuideActivity extends AppCompatActivity {

    @BindView(R.id.tv_open_fontfolder)
    TextView mTvOpenFontfolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_use_guide);
        ButterKnife.bind(this);
        initView();

    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.tv_open_fontfolder)
    public void onClick() {
        Intent intent=new Intent();
        File fontFolder = new File(Environment.getExternalStorageDirectory() + Common.FL_FONTS_FOLDER);
        if (!fontFolder.exists()) {
            fontFolder.mkdirs();
        }
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setDataAndType(Uri.parse(fontFolder.toString()), "file/*");
        startActivity(intent);
    }
}
