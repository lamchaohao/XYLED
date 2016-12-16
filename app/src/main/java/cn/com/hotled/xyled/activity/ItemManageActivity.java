package cn.com.hotled.xyled.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import java.util.List;

import cn.com.hotled.xyled.App;
import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.bean.Program;
import cn.com.hotled.xyled.dao.ProgramDao;
import cn.com.hotled.xyled.util.MutilMoveCompressUtil;

public class ItemManageActivity extends BaseActivity {

    private List<Program> mProgramList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_manage);
        initView();
    }

    private void initView() {
        long screenId = getIntent().getLongExtra("screenId", -1);
        mProgramList = ((App) getApplication()).getDaoSession().getProgramDao().queryBuilder().where(ProgramDao.Properties.ScreenId.eq(screenId)).list();

        Button btSend = (Button) findViewById(R.id.bt_itemMan_send);
        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MutilMoveCompressUtil compressUtil = new MutilMoveCompressUtil(ItemManageActivity.this, mProgramList,64,32,60,60);
                compressUtil.startGenFile();
            }
        });
    }

    @Override
    public void onCreateCustomToolBar(Toolbar toolbar) {
        super.onCreateCustomToolBar(toolbar);
        toolbar.setTitle("节目管理");
    }
}
