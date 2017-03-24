package cn.com.hotled.xyled.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.mobeta.android.dslv.DragSortListView;

import java.util.Arrays;
import java.util.List;

import cn.com.hotled.xyled.App;
import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.adapter.ItemSortAdapter;
import cn.com.hotled.xyled.bean.Program;

public class ProgramManageActivity extends BaseActivity {
    private List<Program> mProgramList;
    private ItemSortAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_manage);
        loadData();
        initView();
    }

    private void loadData() {
        mProgramList = ((App) getApplication()).getDaoSession().getProgramDao().queryBuilder().list();

        Program[] sortProgramList = new Program[mProgramList.size()];
        for (int i = 0; i < mProgramList.size(); i++) {
            sortProgramList[mProgramList.get(i).getSortNumber()]=mProgramList.get(i);
        }
        mProgramList.clear();
        List<Program> programs = Arrays.asList(sortProgramList);
        mProgramList.addAll(programs);
    }

    private void initView() {


        DragSortListView dslv_manage = (DragSortListView) findViewById(R.id.dslv_manage);

        mAdapter = new ItemSortAdapter(this,mProgramList);
        dslv_manage.setDragEnabled(true);
        dslv_manage.setAdapter(mAdapter);

        dslv_manage.setDropListener(new DragSortListView.DropListener() {
            @Override
            public void drop(int from, int to) {
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
        toolbar.setTitle(getString(R.string.program_manage));

    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }
}
