package cn.com.hotled.xyled.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.adapter.LanguageAdapter;
import cn.com.hotled.xyled.global.Global;
import cn.com.hotled.xyled.util.android.DensityUtil;

public class SelectLanguageActivity extends BaseActivity {


    private SharedPreferences.Editor mEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_language);
        intiView();
    }

    private void intiView() {
        SharedPreferences sp = getSharedPreferences(Global.SP_SYSTEM_CONFIG, MODE_PRIVATE);
        int langPos = sp.getInt(Global.KEY_LANGUAGE, 0);

        mEdit = sp.edit();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rcv_selectLanguage);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        List<String> languages = new ArrayList<>();
        languages.add(getString(R.string.auto));
        languages.add("简体中文");
        languages.add("English");
        LanguageAdapter adapter = new LanguageAdapter(languages, this,langPos);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new LanguageAdapter.OnItemOnClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mEdit.putInt(Global.KEY_LANGUAGE,position);
            }
        });
    }

    @Override
    public void onCreateCustomToolBar(Toolbar toolbar) {
        Button btSave=new Button(this);
        Toolbar.LayoutParams prams =new Toolbar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        prams.gravity= Gravity.RIGHT;
        prams.rightMargin= DensityUtil.dp2px(this,10);
        btSave.setBackgroundResource(R.drawable.sendbutton_bg);
        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEdit.apply();
                setResult(RESULT_OK);
                finish();
            }
        });
        btSave.setText(R.string.save);
        toolbar.addView(btSave,prams);
    }
}
