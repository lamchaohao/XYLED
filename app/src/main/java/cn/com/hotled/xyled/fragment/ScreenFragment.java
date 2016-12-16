package cn.com.hotled.xyled.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;
import java.util.List;

import cn.com.hotled.xyled.App;
import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.activity.AddScreenActivity;
import cn.com.hotled.xyled.activity.EasyTextActivity;
import cn.com.hotled.xyled.activity.ItemManageActivity;
import cn.com.hotled.xyled.adapter.ScreenAdapter;
import cn.com.hotled.xyled.bean.LedScreen;
import cn.com.hotled.xyled.bean.Program;
import cn.com.hotled.xyled.bean.ProgramType;
import cn.com.hotled.xyled.bean.TextButton;
import cn.com.hotled.xyled.dao.ProgramDao;
import cn.com.hotled.xyled.dao.TextButtonDao;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Lam on 2016/12/1.
 */

public class ScreenFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "ScreenFragment";
    private static final int ADD_SCREEN_CODE = 366;
    private static final int EASY_TEXT_REQUEST_CODE = 0x23;
    private RecyclerView mRecyclerView;
    private List<LedScreen> mScreenList;
    private ScreenAdapter mAdapter;
    private int selectItem;
    private FloatingActionMenu mMFaMenu;
    private String mTitlePart;
    private int mScreenPosition;
    private int mProgramPosition;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sreen, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        initActionButton(view);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_fragmScreen);
        //1.加节目
        mScreenList = new ArrayList<>();
        loadData();
        mAdapter = new ScreenAdapter(getContext(), mScreenList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new ScreenAdapter.OnItemClickListener() {
            @Override
            public void onClick(View v, final int position, final boolean isScreenParent, final int screenPosition, final int programPosition) {
                if (!isScreenParent){
                    Intent intent = new Intent(getContext(), EasyTextActivity.class);
                    intent.putExtra("programId",mScreenList.get(screenPosition).getProgramList().get(programPosition).getId());
                    intent.putExtra("programName",mScreenList.get(screenPosition).getProgramList().get(programPosition).getProgramName());
                    mScreenPosition = screenPosition;
                    mProgramPosition = programPosition;
                    startActivityForResult(intent,EASY_TEXT_REQUEST_CODE);
                }else {
                    Intent intent = new Intent(getContext(), ItemManageActivity.class);
                    intent.putExtra("screenId",mScreenList.get(screenPosition).getId());
                    startActivity(intent);
                }
            }
        });
        mAdapter.setOnItemLongClickListener(new ScreenAdapter.OnItemLongClickListener() {
            @Override
            public void onLongClick(View v, final int position, final boolean isScreenParent, final int screenPosition, final int programPosition) {
                mTitlePart = "";
                if (isScreenParent){
                   mTitlePart = mScreenList.get(screenPosition).getScreenName();
                }else{
                    mTitlePart = mScreenList.get(screenPosition).getProgramList().get(programPosition).getProgramName();
                }
                new AlertDialog.Builder(getContext())
                        .setTitle("删除"+(isScreenParent?"屏幕":"节目"))
                        .setMessage("确定删除"+ mTitlePart +"?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                if (isScreenParent) {
                                    List<Program> list = ((App) getActivity().getApplication()).getDaoSession().getProgramDao().queryBuilder().where(ProgramDao.Properties.ScreenId.eq(mScreenList.get(screenPosition).getId())).list();
                                    ((App) getActivity().getApplication()).getDaoSession().getProgramDao().deleteInTx(list);
                                    ((App) getActivity().getApplication()).getDaoSession().getLedScreenDao().delete(mScreenList.get(screenPosition));
                                    for (Program program:list){
                                        long id = program.getId();
                                        List<TextButton> programs = ((App) getActivity().getApplication()).getDaoSession().getTextButtonDao().queryBuilder().where(TextButtonDao.Properties.ProgramId.eq(id)).list();
                                        ((App) getActivity().getApplication()).getDaoSession().getTextButtonDao().deleteInTx(programs);
                                    }

                                    mScreenList.remove(screenPosition);
                                    mAdapter.updateDataSet();
                                    mAdapter.notifyDataSetChanged();
                                }else{
                                    Program program = mScreenList.get(screenPosition).getProgramList().get(programPosition);
                                    ((App) getActivity().getApplication()).getDaoSession().getProgramDao().delete(program);
                                    List<TextButton> programs = ((App) getActivity().getApplication()).getDaoSession().getTextButtonDao().queryBuilder().where(TextButtonDao.Properties.ProgramId.eq(program.getId())).list();
                                    ((App) getActivity().getApplication()).getDaoSession().getTextButtonDao().deleteInTx(programs);
                                    mScreenList.get(screenPosition).getProgramList().remove(programPosition);
                                    mAdapter.updateDataSet();
                                    mAdapter.notifyItemRemoved(position);
                                    mAdapter.notifyDataSetChanged();
                                }
                                System.out.println(mScreenList.toString());
                                Snackbar.make(mRecyclerView,"已删除"+(isScreenParent?"屏幕":"节目")+ mTitlePart,Snackbar.LENGTH_LONG).show();
                            }

                        })
                        .setNegativeButton("取消",null)
                        .show();
            }
        });

    }

    private void loadData() {
        mScreenList = ((App) (getActivity().getApplication())).getDaoSession().getLedScreenDao().queryBuilder().list();

    }

    private void initActionButton(View view) {
        mMFaMenu = (FloatingActionMenu) view.findViewById(R.id.fam_screen_add);
        mMFaMenu.setClosedOnTouchOutside(true);
        FloatingActionButton abAddScreen =  (FloatingActionButton) view.findViewById(R.id.fab_addScreen);
        FloatingActionButton abAddProgram = (FloatingActionButton) view.findViewById(R.id.fab_addProgramText);
        abAddScreen.setOnClickListener(this);
        abAddProgram.setOnClickListener(this);
    }

    public void addScreen(LedScreen screen){
       if (screen!=null){
           mScreenList.add(screen);
           mAdapter.notifyDataSetChanged();
           Log.i(TAG,screen.toString());
       }
   }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab_addScreen:
                startActivityForResult(new Intent(getContext(),AddScreenActivity.class),ADD_SCREEN_CODE);
                break;
            case R.id.fab_addProgramText:
                mMFaMenu.close(true);
                final String[] screensStrs =new String[mScreenList.size()];
                if (screensStrs.length==0){
                    Toast.makeText(getContext(), "请先创建屏幕", Toast.LENGTH_SHORT).show();
                    return;
                }
                for (int i = 0; i < mScreenList.size(); i++) {
                    screensStrs[i]=mScreenList.get(i).getScreenName();
                }
                new AlertDialog.Builder(getContext()).setTitle("请选择屏幕")
                        .setSingleChoiceItems(screensStrs, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                selectItem=which;
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                selectItem=0;
                            }
                        })
                       .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               int size = mScreenList.get(selectItem).getProgramList().size();
                               size++;
                               Program program = new Program();
                               program.setProgramName("new Text"+size);
                               program.setScreenId(mScreenList.get(selectItem).getId());
                               program.setSortNumber(mScreenList.get(selectItem).getProgramList().size()-1);
                               program.setProgramType(ProgramType.Text);
                               ((App) getActivity().getApplication()).getDaoSession().getProgramDao().insert(program);
                               mScreenList.get(selectItem).getProgramList().add(program);
                               int position = 0;
                               for (int i = 0; i < mScreenList.size(); i++) {
                                   position++;
                                   position+=mScreenList.get(i).getProgramList().size();
                               }
                               mAdapter.notifyItemInserted(position);
                               selectItem=0;
                           }
                       })
                        .show();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode==RESULT_OK&&requestCode==ADD_SCREEN_CODE){
            LedScreen screen = data.getParcelableExtra("screen");
            addScreen(screen);
        }else if (resultCode==RESULT_OK&&requestCode==EASY_TEXT_REQUEST_CODE){
            String newProgramName = data.getStringExtra("newProgramName");
            mScreenList.get(mScreenPosition).getProgramList().get(mProgramPosition).setProgramName(newProgramName);
            mAdapter.notifyDataSetChanged();
        }
    }

}
