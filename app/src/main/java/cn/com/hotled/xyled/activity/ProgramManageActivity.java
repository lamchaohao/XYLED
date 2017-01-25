package cn.com.hotled.xyled.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;

import com.mobeta.android.dslv.DragSortListView;

import java.util.Arrays;
import java.util.List;

import cn.com.hotled.xyled.App;
import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.adapter.ItemSortAdapter;
import cn.com.hotled.xyled.bean.Program;
import cn.com.hotled.xyled.dao.ProgramDao;
import cn.com.hotled.xyled.util.PicCompressUtil;

import static cn.com.hotled.xyled.bean.ProgramType.Pic;

public class ProgramManageActivity extends BaseActivity {
    private static final int EASY_TEXT_REQUEST_CODE = 0x23;

    private List<Program> mProgramList;
    private ItemSortAdapter mAdapter;
    private int mPosition;
    private long mScreenId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_manage);
        loadData();
        initView();
    }

    private void loadData() {
        mScreenId = getIntent().getLongExtra("screenId", -1);
        mProgramList = ((App) getApplication()).getDaoSession().getProgramDao().queryBuilder().where(ProgramDao.Properties.ScreenId.eq(mScreenId)).list();

        Program[] sortProgramList = new Program[mProgramList.size()];
        for (int i = 0; i < mProgramList.size(); i++) {
            sortProgramList[mProgramList.get(i).getSortNumber()]=mProgramList.get(i);
        }
        mProgramList.clear();
        List<Program> programs = Arrays.asList(sortProgramList);
        mProgramList.addAll(programs);
    }

    private void initView() {

        Button btSend = (Button) findViewById(R.id.bt_itemMan_sendbyPhone);
        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PicCompressUtil compressUtil = new PicCompressUtil(ProgramManageActivity.this, mProgramList,64,32,60,60);
                compressUtil.startGenFile();
            }
        });

        findViewById(R.id.bt_itemMan_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProgramManageActivity.this, SendActivity.class);
                intent.putExtra("screenId",mScreenId);
                startActivity(intent);
//                PicCompressUtil compressUtil = new PicCompressUtil(ProgramManageActivity.this, mProgramList,64,32,60,60);
//                compressUtil.setNeedSend(true);
//                compressUtil.startGenFile();
            }
        });

//        findViewById(R.id.bt_itemMan_sentoCompu).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                WiFiToComputerUtil wifiToComputer = new WiFiToComputerUtil(ProgramManageActivity.this,mProgramList,64,32,60,60);
//                wifiToComputer.startGen();
//            }
//        });

        DragSortListView dslv_manage = (DragSortListView) findViewById(R.id.dslv_manage);

        mAdapter = new ItemSortAdapter(this,mProgramList);
        dslv_manage.setDragEnabled(true);
        dslv_manage.setAdapter(mAdapter);
        dslv_manage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mPosition = position;
                if (mProgramList.get(position).getProgramType()== Pic) {
                    Intent intent = new Intent(ProgramManageActivity.this, PhotoEditActivity.class);
                    intent.putExtra("programId",mProgramList.get(position).getId());
                    intent.putExtra("programName",mProgramList.get(position).getProgramName());
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(ProgramManageActivity.this, EasyTextActivity.class);
                    intent.putExtra("programId",mProgramList.get(position).getId());
                    intent.putExtra("programName",mProgramList.get(position).getProgramName());
                    startActivityForResult(intent,EASY_TEXT_REQUEST_CODE);
                }

            }
        });
        dslv_manage.setDropListener(new DragSortListView.DropListener() {
            @Override
            public void drop(int from, int to) {
                Log.i("itemsort","drop from="+from+",to="+to);
                ((App) getApplication()).getDaoSession().getProgramDao().deleteInTx(mProgramList);
                Program program = mProgramList.get(from);
                mProgramList.remove(from);
                mProgramList.add(to,program);
                mAdapter.notifyDataSetChanged();

                for (int i = 0;i<mProgramList.size();i++){
                    mProgramList.get(i).setSortNumber(i);

                }
                ((App) getApplication()).getDaoSession().getProgramDao().insertInTx(mProgramList);
                setResult(RESULT_OK);
            }
        });


    }

    @Override
    public void onCreateCustomToolBar(Toolbar toolbar) {
        super.onCreateCustomToolBar(toolbar);
        toolbar.setTitle("节目管理");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode==RESULT_OK&&requestCode==EASY_TEXT_REQUEST_CODE){
            String newProgramName = data.getStringExtra("newProgramName");
            mProgramList.get(mPosition).setProgramName(newProgramName);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }
}
