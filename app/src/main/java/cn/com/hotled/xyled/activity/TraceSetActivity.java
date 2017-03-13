package cn.com.hotled.xyled.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.hotled.xyled.App;
import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.adapter.TraceSelectAdapter;
import cn.com.hotled.xyled.bean.Trace;
import cn.com.hotled.xyled.bean.TraceFile;
import cn.com.hotled.xyled.dao.TraceFileDao;
import cn.com.hotled.xyled.global.Global;

public class TraceSetActivity extends BaseActivity implements AdapterView.OnItemSelectedListener{

    @BindView(R.id.spn_trace_pixel)
    Spinner mSpnTracePixel;
    @BindView(R.id.spn_trace_scan)
    Spinner mSpnTraceScan;
    @BindView(R.id.spn_trace_size)
    Spinner mSpnTraceSize;
    @BindView(R.id.spn_trace_hub)
    Spinner mSpnTraceHub;
    @BindView(R.id.rv_selectFile)
    RecyclerView mRvSelectFile;
    @BindView(R.id.bt_traceset_commit)
    Button mBtTracesetCommit;
    @BindView(R.id.bt_traceset_showAll)
    Button mBtTracesetShowAll;

    private File fileName;
    private int pixel;
    private int scan;
    private int size;
    private int hub;
    private TraceFileDao mTraceFileDao;
    private List<Trace> mTraceList;
    private TraceSelectAdapter mTraceAdapter;
    private TraceFile mTraceFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trace_set);
        ButterKnife.bind(this);
        loadData();
        initView();
    }

    private void loadData() {
        mTraceFileDao = ((App) getApplication()).getDaoSession().getTraceFileDao();
        List<TraceFile> list = mTraceFileDao.queryBuilder().list();
        mTraceList = new ArrayList<>();
        for (TraceFile traceFile : list) {
            Trace trace =new Trace(false,traceFile);
            mTraceList.add(trace);
        }

    }

    private void initView() {
        mTraceFile = new TraceFile();
        mRvSelectFile.setLayoutManager(new LinearLayoutManager(this));
        mTraceAdapter = new TraceSelectAdapter(mTraceList, this);
        mRvSelectFile.setAdapter(mTraceAdapter);
        mTraceAdapter.setOnItemClickListener(new TraceSelectAdapter.OnItemOnClickListener() {
            @Override
            public void onItemClick(View view, int position) {
               mTraceFile = mTraceList.get(position).getTraceFile();
            }
        });
        mSpnTracePixel.setOnItemSelectedListener(this);
        mSpnTraceScan.setOnItemSelectedListener(this);
        mSpnTraceSize.setOnItemSelectedListener(this);
        mSpnTraceHub.setOnItemSelectedListener(this);
    }


    @OnClick({R.id.bt_traceset_commit, R.id.bt_traceset_showAll})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_traceset_commit:
                if (mTraceFile.getId()!=0) {
                    Intent intent=new Intent();
                    intent.putExtra(Global.EXTRA_SELECT_TRACE,mTraceFile.getId());
                    setResult(RESULT_OK,intent);
                    TraceSetActivity.this.finish();
                }
                break;
            case R.id.bt_traceset_showAll:
                updateData(true);
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spn_trace_pixel:
                pixel = position;
                break;
            case R.id.spn_trace_scan:
                scan = position;
                break;
            case R.id.spn_trace_size:
                size = position;
                break;
            case R.id.spn_trace_hub:
                hub = position;
                break;
        }
        updateData(false);
    }

    private void updateData(boolean isShowAll) {
        List<TraceFile> list = null;
        if (isShowAll){
            list = mTraceFileDao.queryBuilder().list();
        }else {
            list = mTraceFileDao.queryBuilder().where(pixel==0?TraceFileDao.Properties.Pixel.isNotNull():TraceFileDao.Properties.Pixel.eq(pixel),
                    scan==0?TraceFileDao.Properties.Scan.isNotNull():TraceFileDao.Properties.Scan.eq(scan),
                    size==0?TraceFileDao.Properties.Size.isNotNull():TraceFileDao.Properties.Size.eq(size),
                    hub==0?TraceFileDao.Properties.Hub.isNotNull():TraceFileDao.Properties.Hub.eq(hub))
                    .list();
        }
        mTraceList.clear();
        for (TraceFile traceFile : list) {
            Trace trace =new Trace(false,traceFile);
            mTraceList.add(trace);
        }
        mTraceAdapter.notifyDataSetChanged();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
