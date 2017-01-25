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

import java.util.Arrays;
import java.util.List;

import cn.com.hotled.xyled.App;
import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.activity.AddScreenActivity;
import cn.com.hotled.xyled.activity.EasyTextActivity;
import cn.com.hotled.xyled.activity.PhotoEditActivity;
import cn.com.hotled.xyled.activity.ProgramManageActivity;
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
    private static final int PHOTO_REQUEST_CODE = 0x24;
    private static final int ITEM_MANAGE_REQUEST_CODE = 0x25;
    private RecyclerView mRecyclerView;
    private List<LedScreen> mScreenList;
    private ScreenAdapter mAdapter;
    private int selectItem;
    private FloatingActionMenu mMFaMenu;
    private String mTitlePart;
    private int mScreenPosition;
    private int mProgramPosition;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //1.加节目
        loadData();
    }

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
        mAdapter = new ScreenAdapter(getContext(), mScreenList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new ScreenAdapter.OnItemClickListener() {
            @Override
            public void onClick(View v, final int position, final boolean isScreenParent, final int screenPosition, final int programPosition) {
                if (!isScreenParent){
                    ProgramType programType = mScreenList.get(screenPosition).getProgramList().get(programPosition).getProgramType();
                    if (programType== ProgramType.Pic) {
                        Intent intent = new Intent(getContext(),PhotoEditActivity.class);
                        intent.putExtra("programId",mScreenList.get(screenPosition).getProgramList().get(programPosition).getId());
                        intent.putExtra("programName",mScreenList.get(screenPosition).getProgramList().get(programPosition).getProgramName());
                        mScreenPosition = screenPosition;
                        mProgramPosition = programPosition;
                        startActivityForResult(intent,PHOTO_REQUEST_CODE);
                    } else if (programType== ProgramType.Text){
                        Intent intent = new Intent(getContext(), EasyTextActivity.class);
                        intent.putExtra("programId",mScreenList.get(screenPosition).getProgramList().get(programPosition).getId());
                        intent.putExtra("programName",mScreenList.get(screenPosition).getProgramList().get(programPosition).getProgramName());
                        mScreenPosition = screenPosition;
                        mProgramPosition = programPosition;
                        startActivityForResult(intent,EASY_TEXT_REQUEST_CODE);
                    }

                }else {
                    mScreenPosition = screenPosition;
                    Intent intent = new Intent(getContext(), ProgramManageActivity.class);
                    intent.putExtra("screenId",mScreenList.get(screenPosition).getId());
                    startActivityForResult(intent,ITEM_MANAGE_REQUEST_CODE);
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
                                    //删除一个节目后，要重新排序sortNum，不然会引发indexoutofbound,app就无法启动
                                    //2017/1/14 修复，由于之前重新排序的时候是所有屏幕的节目都重新排序，导致不该改变的屏幕的节目的sortnum都改变了，所以app无法启动
                                    List<Program> programList = ((App) getActivity().getApplication()).getDaoSession().getProgramDao().queryBuilder().where(ProgramDao.Properties.ScreenId.eq(mScreenList.get(screenPosition).getId())).list();
                                    int sortNumber = program.getSortNumber();
                                    for (Program program1 : programList) {//在被删除的节目的sortnum后的，sortNUm都减一，保持队列
                                        if (program1.getSortNumber()>sortNumber) {
                                            int currentSortNum = program1.getSortNumber();
                                            currentSortNum--;
                                            program1.setSortNumber(currentSortNum);
                                        }
                                    }
                                    ((App) getActivity().getApplication()).getDaoSession().getProgramDao().insertOrReplaceInTx(programList);

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
        for (LedScreen screen : mScreenList) {
            List<Program> programList = screen.getProgramList();
            Program[] sortProgramList = new Program[programList.size()];
            for (int i = 0; i < programList.size(); i++) {
                sortProgramList[programList.get(i).getSortNumber()]=programList.get(i);
            }
            screen.getProgramList().clear();
            List<Program> programs = Arrays.asList(sortProgramList);
            screen.getProgramList().addAll(programs);
        }
    }

    private void initActionButton(View view) {
        mMFaMenu = (FloatingActionMenu) view.findViewById(R.id.fam_screen_add);
        mMFaMenu.setClosedOnTouchOutside(true);
        FloatingActionButton abAddScreen =  (FloatingActionButton) view.findViewById(R.id.fab_addScreen);
        FloatingActionButton abAddProgram = (FloatingActionButton) view.findViewById(R.id.fab_addProgramText);
        FloatingActionButton abAddPic = (FloatingActionButton) view.findViewById(R.id.fab_addProgramPic);
        abAddScreen.setOnClickListener(this);
        abAddProgram.setOnClickListener(this);
        abAddPic.setOnClickListener(this);
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
                               program.setId(System.currentTimeMillis());
                               program.setProgramName("new Text"+size);
                               program.setScreenId(mScreenList.get(selectItem).getId());
                               program.setSortNumber(mScreenList.get(selectItem).getProgramList().size());
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
            case R.id.fab_addProgramPic:
                mMFaMenu.close(true);
                final String[] oScreensStrs =new String[mScreenList.size()];
                if (oScreensStrs.length==0){
                    Toast.makeText(getContext(), "请先创建屏幕", Toast.LENGTH_SHORT).show();
                    return;
                }
                for (int i = 0; i < mScreenList.size(); i++) {
                    oScreensStrs[i]=mScreenList.get(i).getScreenName();
                }
                new AlertDialog.Builder(getContext()).setTitle("请选择屏幕")
                        .setSingleChoiceItems(oScreensStrs, 0, new DialogInterface.OnClickListener() {
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
                                program.setId(System.currentTimeMillis());
                                program.setProgramName("new Pic"+size);
                                program.setScreenId(mScreenList.get(selectItem).getId());
                                program.setSortNumber(mScreenList.get(selectItem).getProgramList().size());
                                program.setProgramType(ProgramType.Pic);
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
        }else if(resultCode==RESULT_OK&&requestCode==ITEM_MANAGE_REQUEST_CODE){

            List<Program> list = ((App) getActivity().getApplication()).getDaoSession().getProgramDao().queryBuilder().where(ProgramDao.Properties.ScreenId.eq(mScreenList.get(mScreenPosition).getId())).list();

            Program[] sortProgramList = new Program[list.size()];
            for (int i = 0; i < list.size(); i++) {
                sortProgramList[list.get(i).getSortNumber()]=list.get(i);
            }
            mScreenList.get(mScreenPosition).getProgramList().clear();
            List<Program> programs = Arrays.asList(sortProgramList);

            mScreenList.get(mScreenPosition).getProgramList().addAll(programs);
            mAdapter.notifyDataSetChanged();
        }
    }

}
