package cn.com.hotled.xyled.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.hotled.xyled.App;
import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.adapter.TraceSelectAdapter;
import cn.com.hotled.xyled.bean.Trace;
import cn.com.hotled.xyled.bean.TraceFile;
import cn.com.hotled.xyled.dao.TraceFileDao;
import cn.com.hotled.xyled.global.Common;
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

    private int pixel;
    private int scan;
    private int size;
    private int hub;
    private TraceFileDao mTraceFileDao;
    private List<Trace> mTraceList;
    private TraceSelectAdapter mTraceAdapter;
    private TraceFile mTraceFile;
    private boolean isInitView = true;
    private int initListenerCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trace_set);
        ButterKnife.bind(this);
        loadData();
        initView();
    }

    private void loadData() {
        long longExtra = getIntent().getLongExtra(Common.EX_id, -1);
        mTraceFileDao = ((App) getApplication()).getDaoSession().getTraceFileDao();
        List<TraceFile> list = mTraceFileDao.queryBuilder().list();
        mTraceList = new ArrayList<>();
        for (TraceFile traceFile : list) {
            Trace trace =new Trace(false,traceFile);
            if (longExtra==traceFile.getId()){
                trace.setSelected(true);
            }
            mTraceList.add(trace);
        }

    }

    private void initView() {
        mTraceFile = new TraceFile();
        mRvSelectFile.setLayoutManager(new LinearLayoutManager(this));
        int langInt = getSharedPreferences(Global.SP_SYSTEM_CONFIG, MODE_PRIVATE).getInt(Global.KEY_LANGUAGE, 0);
        Locale locale = getResources().getConfiguration().locale;
        mTraceAdapter = new TraceSelectAdapter(mTraceList, this,langInt==1||locale.equals(Locale.SIMPLIFIED_CHINESE)||locale.equals(Locale.CHINESE));
        mRvSelectFile.setAdapter(mTraceAdapter);
        mTraceAdapter.setOnItemClickListener(new TraceSelectAdapter.OnItemOnClickListener() {
            @Override
            public void onItemClick(View view, int position) {
               mTraceFile = mTraceList.get(position).getTraceFile();
                int scanCount = mTraceFile.getScanCount();
                getSharedPreferences(Global.SP_SCREEN_CONFIG,MODE_PRIVATE).edit().putInt(Global.KEY_SCREEN_SCAN,scanCount).apply();
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
                    intent.putExtra(Common.EX_selectTrace,mTraceFile.getId());
                    setResult(RESULT_OK,intent);
                    TraceSetActivity.this.finish();
                }
                break;
            case R.id.bt_traceset_showAll:
                mSpnTracePixel.setSelection(0);
                mSpnTraceScan.setSelection(0);
                mSpnTraceSize.setSelection(0);
                mSpnTraceHub.setSelection(0);
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
        if (!isInitView) {
            updateData();
        }else {
            initListenerCount++;
            if (initListenerCount==4)
                isInitView=false;
        }
    }

    private void updateData() {

        List<TraceFile> list = mTraceFileDao.queryBuilder().where(pixel==0?TraceFileDao.Properties.Pixel.isNotNull():TraceFileDao.Properties.Pixel.eq(pixel),
                scan==0?TraceFileDao.Properties.Scan.isNotNull():TraceFileDao.Properties.Scan.eq(scan),
                size==0?TraceFileDao.Properties.Size.isNotNull():TraceFileDao.Properties.Size.eq(size),
                hub==0?TraceFileDao.Properties.Hub.isNotNull():TraceFileDao.Properties.Hub.eq(hub))
                .list();
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
