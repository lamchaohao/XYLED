package cn.com.hotled.xyled.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.adapter.FlowAdapter;
import cn.com.hotled.xyled.global.Common;

import static cn.com.hotled.xyled.global.Common.FL_FLOW_DIR;

public class SelectFlowActivity extends BaseActivity {

    private RecyclerView mRvSelectFlow;
    private List<File> mFileList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_flow);
        prepareToInit();

    }

    private void prepareToInit() {
        mRvSelectFlow = (RecyclerView) findViewById(R.id.rv_selectflow);
        mRvSelectFlow.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        initView();
    }

    private void initView() {

        File fileDir=new File(getFilesDir()+FL_FLOW_DIR);
        File[] files = fileDir.listFiles();

        mFileList = new ArrayList<>();
        if (files!=null)
        for (File file : files) {
            mFileList.add(file);
        }
        FlowAdapter adapter =new FlowAdapter(mFileList,this);
        mRvSelectFlow.setAdapter(adapter);
        adapter.setOnItemClickListener(new FlowAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                File selectFile = mFileList.get(position);
                Intent intent = new Intent();
                intent.putExtra(Common.EX_setlectFlow,selectFile.getAbsolutePath());
                setResult(RESULT_OK,intent);
                SelectFlowActivity.this.finish();
            }
        });
    }


    @Override
    public void onCreateCustomToolBar(Toolbar toolbar) {
        toolbar.setTitle(R.string.choose_bound);
    }
}
