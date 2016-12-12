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

import java.util.ArrayList;
import java.util.List;

import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.activity.EasyTextActivity;
import cn.com.hotled.xyled.activity.ItemManageActivity;
import cn.com.hotled.xyled.adapter.ScreenAdapter;
import cn.com.hotled.xyled.bean.LedScreen;
import cn.com.hotled.xyled.bean.Program;

/**
 * Created by Lam on 2016/12/1.
 */

public class ScreenFragment extends Fragment{
    private static final String TAG = "ScreenFragment";
    private RecyclerView mRecyclerView;
    private List<LedScreen> mScreenList;
    private ScreenAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sreen, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_fragmScreen);
        //1.加节目
        // TODO: 2016/12/7 当转到第三个view时候，第一个view会被viewpager销毁，而中间加入的屏幕会不见，所以需要建立数据持久化 
        mScreenList = new ArrayList<>();
        for (int i=0;i<6;i++){
            List<Program> programs =  new ArrayList<>() ;
            for (int j=0;j<3;j++){
                Program program = new Program("节目" + j, Program.ProgramType.Text);
                programs.add(program);
            }
            LedScreen screen=new LedScreen("屏幕"+i,64,32,"HC-1",programs);
            mScreenList.add(screen);
        }
        mAdapter = new ScreenAdapter(getContext(), mScreenList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new ScreenAdapter.OnItemClickListener() {
            @Override
            public void onClick(View v, int position, boolean isScreenParent) {
                if (!isScreenParent){
                    startActivity(new Intent(getContext(), EasyTextActivity.class));
                }else {
                    startActivity(new Intent(getContext(), ItemManageActivity.class));
                }
            }
        });
        mAdapter.setOnItemLongClickListener(new ScreenAdapter.OnItemLongClickListener() {
            @Override
            public void onLongClick(View v, final int position, final boolean isScreenParent, final int screenPosition, final int programPosition) {
                final String titlePart=isScreenParent?mScreenList.get(screenPosition).getScreenName():mScreenList.get(screenPosition).getProgramList().get(programPosition).getProgramName();
                new AlertDialog.Builder(getContext())
                        .setTitle("删除"+(isScreenParent?"屏幕":"节目"))
                        .setMessage("确定删除"+titlePart+"?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                if (isScreenParent) {
                                    mScreenList.remove(screenPosition);
                                    mAdapter.updateDataSet();
                                    mAdapter.notifyDataSetChanged();
                                }else{
                                    mScreenList.get(screenPosition).getProgramList().remove(programPosition);
                                    mAdapter.updateDataSet();
                                    mAdapter.notifyItemRemoved(position);
                                }
                                System.out.println(mScreenList.toString());
                                Snackbar.make(mRecyclerView,"已删除"+(isScreenParent?"屏幕":"节目")+titlePart,Snackbar.LENGTH_LONG).show();
                            }

                        })
                        .setNegativeButton("取消",null)
                        .show();
            }
        });
    }

   public void addScreen(LedScreen screen){
       if (screen!=null){
           mScreenList.add(screen);
           mAdapter.notifyDataSetChanged();
           Log.i(TAG,screen.toString());
       }
   }

}
