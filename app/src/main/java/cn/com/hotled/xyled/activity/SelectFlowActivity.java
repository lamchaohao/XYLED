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
import cn.com.hotled.xyled.adapter.FlowAdapter;

public class SelectFlowActivity extends BaseActivity {

    private RecyclerView mRvSelectFlow;
    private List<File> mFileList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_flow);
        initView();
    }

    private void initView() {
        mRvSelectFlow = (RecyclerView) findViewById(R.id.rv_selectflow);
        mRvSelectFlow.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        String filePath= Environment.getExternalStorageDirectory()+"/xyled.flow";
        File file=new File(filePath);
        File[] files = file.listFiles();
        mFileList = new ArrayList<>();
        for (File file1 : files) {
            mFileList.add(file1);
        }
        FlowAdapter adapter =new FlowAdapter(mFileList,this);
        mRvSelectFlow.setAdapter(adapter);
        adapter.setOnItemClickListener(new FlowAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                File selectFile = mFileList.get(position);
                Intent intent = new Intent();
                intent.putExtra("fileName",selectFile.getAbsolutePath());
                setResult(RESULT_OK,intent);
                SelectFlowActivity.this.finish();
            }
        });
    }

    @Override
    public void onCreateCustomToolBar(Toolbar toolbar) {
        toolbar.setTitle("选择边框");
    }
}
