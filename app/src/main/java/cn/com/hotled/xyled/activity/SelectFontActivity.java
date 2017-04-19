package cn.com.hotled.xyled.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.adapter.TypefaceAdapter;
import cn.com.hotled.xyled.bean.TypefaceFile;
import cn.com.hotled.xyled.decoration.WifiItemDecoration;
import cn.com.hotled.xyled.global.Common;

public class SelectFontActivity extends BaseActivity {

    private List<TypefaceFile> mFileList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_font);
        loadData();
        initView();
    }

    private void loadData() {
        File file =new File(Common.FL_SYSTEMFONT);
        File[] files = file.listFiles();
        File downloadFontDir = new File(Environment.getExternalStorageDirectory()+Common.FL_FONTS_FOLDER);
        if (!downloadFontDir.exists()){
            downloadFontDir.mkdirs();
        }
        File[] downloadFonts = downloadFontDir.listFiles();
        mFileList = new ArrayList<>();
        if(downloadFonts!=null)
            for (File downloadFont : downloadFonts) {
                mFileList.add(new TypefaceFile(downloadFont,false));
            }
        for (int i=0;i<files.length;i++){
            String name = files[i].getName();
            if(name.contains("-Regular")&&!name.contains("MiuiEx")){
                mFileList.add(new TypefaceFile(files[i],false));
            }
        }
    }

    private void initView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rcv_selectFont);
        TypefaceAdapter adapter = new TypefaceAdapter(mFileList, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new WifiItemDecoration(this,WifiItemDecoration.VERTICAL_LIST));

        adapter.setOnItemClickListener(new TypefaceAdapter.OnItemOnClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //1.先获取选择了哪个字体
                File file = mFileList.get(position).getFile();
                Intent intent = new Intent();
                intent.putExtra(Common.EX_setelctFont,file.getAbsolutePath());
                setResult(RESULT_OK,intent);
                SelectFontActivity.this.finish();
            }
        });
    }

    @Override
    public void onCreateCustomToolBar(Toolbar toolbar) {
        toolbar.setTitle(R.string.choose_font);
    }
}
