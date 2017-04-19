package cn.com.hotled.xyled.fragment;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.hotled.xyled.App;
import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.activity.EasyTextActivity;
import cn.com.hotled.xyled.activity.PhotoEditActivity;
import cn.com.hotled.xyled.activity.ProgramManageActivity;
import cn.com.hotled.xyled.adapter.ProgramAdapter;
import cn.com.hotled.xyled.bean.Program;
import cn.com.hotled.xyled.bean.ProgramType;
import cn.com.hotled.xyled.bean.TextContent;
import cn.com.hotled.xyled.dao.ProgramDao;
import cn.com.hotled.xyled.dao.TextContentDao;
import cn.com.hotled.xyled.global.Common;
import cn.com.hotled.xyled.global.Global;
import cn.com.hotled.xyled.util.android.DensityUtil;
import cn.com.hotled.xyled.util.communicate.ReadScreenDataUtil;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static cn.com.hotled.xyled.global.Global.READ_FAILE;
import static cn.com.hotled.xyled.global.Global.READ_SUCCESS;
import static cn.com.hotled.xyled.global.Global.TEXT_CONTENT_CHANGE_CODE;
import static cn.com.hotled.xyled.global.Global.WIFI_ERRO;

/**
 * 节目页面
 * Created by Lam on 2016/12/1.
 */

public class ScreenFragment extends Fragment implements ProgramAdapter.OnItemClickListener, ProgramAdapter.OnItemLongClickListener,Animator.AnimatorListener {
    private static final int EASY_TEXT_REQUEST_CODE = 0x23;
    private static final int PHOTO_REQUEST_CODE = 0x24;
    private static final int ITEM_MANAGE_REQUEST_CODE = 0x25;
    @BindView(R.id.iv_screenCatag_screen)
    ImageView mIvScreen;
    @BindView(R.id.tv_screenCatag_card)
    TextView mTvCard;
    @BindView(R.id.tv_screenCatag_size)
    TextView mTvSize;
    @BindView(R.id.tv_screenCatag_scanCount)
    TextView mTvScanCount;
    @BindView(R.id.iv_screenCatag_refresh)
    ImageView mIvRefresh;
    @BindView(R.id.cv_contentScreen_screen)
    CardView mCvScreen;
    @BindView(R.id.rv_fragmScreen)
    RecyclerView mRecyclerView;
    @BindView(R.id.fab_screen_add_text)
    RelativeLayout mFabAddText;
    @BindView(R.id.fab_screen_add_pic)
    RelativeLayout mFabAddPic;
    @BindView(R.id.fab_screen_add_menu)
    FloatingActionButton mFabMenu;
    @BindView(R.id.tv_lable_text)
    TextView mTextLable;
    @BindView(R.id.tv_lable_pic)
    TextView mPicLable;

    private ProgramAdapter mAdapter;
    private String mTitlePart;
    private int mProgramPosition;
    private List<Program> mProgramList;
    private Animation mRefreshAnim;
    private boolean isMenuOpen;
    private SharedPreferences.OnSharedPreferenceChangeListener mListener = new SharedPreferences.OnSharedPreferenceChangeListener() {

        @Override
        public void onSharedPreferenceChanged(
                SharedPreferences sharedPreferences, String key) {
            if (key.equals(Global.KEY_CARD_SERIES) || key.equals(Global.KEY_SCREEN_H) || key.equals(Global.KEY_SCREEN_W)) {
                updateScreenView();
            }
        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WIFI_ERRO:
                    mRefreshAnim.cancel();
                    Toast.makeText(getContext(), R.string.tos_disConnect_screen, Toast.LENGTH_SHORT).show();
                    break;
                case READ_SUCCESS:
                    mRefreshAnim.cancel();
                    Toast.makeText(getContext(), R.string.tos_refresh_success, Toast.LENGTH_SHORT).show();
                    updateScreenView();
                    break;
                case READ_FAILE:
                    mRefreshAnim.cancel();
                    Toast.makeText(getContext(), R.string.tos_screen_noresponse, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    private ProgramDao mProgramDao;
    private Animation mFirstAddAnim;
    private ObjectAnimator mTextAnim;
    private ObjectAnimator mPicAnim;
    private Animation mInAnim;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getSharedPreferences(Global.SP_SCREEN_CONFIG, MODE_PRIVATE).registerOnSharedPreferenceChangeListener(mListener);
        //1.加节目
        loadData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sreen, null);
        ButterKnife.bind(this, view);
        initView();
        updateScreenView();
        return view;
    }

    private void initView() {
        TextContentDao textContentDao = ((App) (getActivity().getApplication())).getDaoSession().getTextContentDao();
        mAdapter = new ProgramAdapter(getContext(), mProgramList, textContentDao);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnItemLongClickListener(this);
        mFirstAddAnim = AnimationUtils.loadAnimation(getContext(), R.anim.fab_scale_up);
        initAnim();


    }

    private void initAnim() {
        mTextAnim = ObjectAnimator
                .ofFloat(mFabAddText, "translationY",0)
                .setDuration(300);
        mPicAnim = ObjectAnimator
                .ofFloat(mFabAddPic, "translationY",0)
                .setDuration(300);
        mTextAnim.addListener(this);
        mPicAnim.addListener(this);
        mInAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_slide_in_from_right);
    }


    private void loadData() {
        mProgramList = new ArrayList<>();
        mProgramDao = ((App) (getActivity().getApplication())).getDaoSession().getProgramDao();
        List<Program> tempPrograms = mProgramDao.queryBuilder().list();
        Program[] sortProgramList = new Program[tempPrograms.size()];
        for (Program program : tempPrograms) {
            sortProgramList[program.getSortNumber()] = program;
        }
        for (Program program : sortProgramList) {
            mProgramList.add(program);
        }
    }


    private void updateScreenView() {
        SharedPreferences sharedPre = getContext().getSharedPreferences(Global.SP_SCREEN_CONFIG, MODE_PRIVATE);
        int screenWidth = sharedPre.getInt(Global.KEY_SCREEN_W, -64);
        int screenHight = sharedPre.getInt(Global.KEY_SCREEN_H, -32);
        int screenScan = sharedPre.getInt(Global.KEY_SCREEN_SCAN, -1);
        String cardName = sharedPre.getString(Global.KEY_CARD_SERIES, getString(R.string.msg_none));
        mTvSize.setText(screenWidth + getString(R.string.pic_plus) + screenHight);
        mTvScanCount.setText(screenScan + getString(R.string.screen_scan_symbol));
        mTvCard.setText(cardName);
    }


    private void readData() {
        mRefreshAnim = AnimationUtils.loadAnimation(getContext(), R.anim.search_round);
        mIvRefresh.startAnimation(mRefreshAnim);
        ReadScreenDataUtil readUtil = new ReadScreenDataUtil(getActivity(), mHandler);
        readUtil.startReadData();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK && requestCode == EASY_TEXT_REQUEST_CODE) {
            String newProgramName = data.getStringExtra(Common.EX_newProGramName);
            mProgramList.get(mProgramPosition).setProgramName(newProgramName);
            mAdapter.notifyItemChanged(mProgramPosition);
        }
        if (resultCode == TEXT_CONTENT_CHANGE_CODE && requestCode == EASY_TEXT_REQUEST_CODE) {
            mAdapter.notifyItemChanged(mProgramPosition);
        } else if (resultCode == RESULT_OK && requestCode == ITEM_MANAGE_REQUEST_CODE) {

            List<Program> list = ((App) getActivity().getApplication()).getDaoSession().getProgramDao().queryBuilder().list();

            Program[] sortProgramList = new Program[list.size()];
            for (int i = 0; i < list.size(); i++) {
                sortProgramList[list.get(i).getSortNumber()] = list.get(i);
            }
            mProgramList.clear();
            List<Program> programs = Arrays.asList(sortProgramList);

            mProgramList.addAll(programs);
            mAdapter.notifyDataSetChanged();
        }
    }

    public void closeFabMenu() {
       openOrCloseMenu();
    }

    public boolean isMenuClose() {
        return isMenuOpen;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().getSharedPreferences(Global.SP_SCREEN_CONFIG, MODE_PRIVATE).unregisterOnSharedPreferenceChangeListener(mListener);
    }

    @Override
    public void onClick(View v, int position) {
        ProgramType programType = mProgramList.get(position).getProgramType();
        if (programType == ProgramType.Pic) {
            Intent intent = new Intent(getContext(), PhotoEditActivity.class);
            intent.putExtra(Common.EX_programId, mProgramList.get(position).getId());
            intent.putExtra(Common.EX_programName, mProgramList.get(position).getProgramName());
            mProgramPosition = position;
            startActivityForResult(intent, PHOTO_REQUEST_CODE);
        } else if (programType == ProgramType.Text) {
            Intent intent = new Intent(getContext(), EasyTextActivity.class);
            intent.putExtra(Common.EX_programId, mProgramList.get(position).getId());
            intent.putExtra(Common.EX_programName, mProgramList.get(position).getProgramName());
            mProgramPosition = position;
            startActivityForResult(intent, EASY_TEXT_REQUEST_CODE);
        }
    }

    @Override
    public void onLongClick(View v, final int position) {
        mTitlePart = mProgramList.get(position).getProgramName();
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.msg_delete_program)
                .setMessage(getString(R.string.msg_confirm_delete) + mTitlePart)
                .setPositiveButton(R.string.msg_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Program program = mProgramList.get(position);
                        ((App) getActivity().getApplication()).getDaoSession().getProgramDao().delete(program);
                        //删除一个节目后，要重新排序sortNum，不然会引发indexoutofbound,app就无法启动
                        //2017/1/14 修复，由于之前重新排序的时候是所有屏幕的节目都重新排序，导致不该改变的屏幕的节目的sortnum都改变了，所以app无法启动
                        int sortNumber = program.getSortNumber();
                        for (Program program1 : mProgramList) {//在被删除的节目的sortnum后的，sortNUm都减一，保持队列
                            if (program1.getSortNumber() > sortNumber) {
                                int currentSortNum = program1.getSortNumber();
                                currentSortNum--;
                                program1.setSortNumber(currentSortNum);
                            }
                        }
                        mProgramList.remove(position);
                        ((App) getActivity().getApplication()).getDaoSession().getProgramDao().insertOrReplaceInTx(mProgramList);
                        //删除文字
                        List<TextContent> textContents = ((App) getActivity().getApplication()).getDaoSession().getTextContentDao().queryBuilder().where(TextContentDao.Properties.ProgramId.eq(program.getId())).list();
                        ((App) getActivity().getApplication()).getDaoSession().getTextContentDao().deleteInTx(textContents);
                        mAdapter.notifyItemRemoved(position);
                        mAdapter.notifyDataSetChanged();
                        Snackbar.make(mRecyclerView, getString(R.string.msg_deleted) + mTitlePart, Snackbar.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton(R.string.msg_cancle, null)
                .show();
    }

    @OnClick({R.id.iv_screenCatag_refresh, R.id.cv_contentScreen_screen, R.id.fab_screen_add_text, R.id.fab_screen_add_pic, R.id.fab_screen_add_menu})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_screen_add_menu:
                openOrCloseMenu();
                break;
            case R.id.fab_screen_add_text:
                int size = mProgramList.size();
                size++;
                Program program = new Program();
                long programId = System.currentTimeMillis();
                program.setId(programId);
                program.setProgramName(getString(R.string.new_text_program) + size);
                program.setSortNumber(mProgramList.size());
                program.setProgramType(ProgramType.Text);
                mProgramDao.insert(program);
                mProgramList.add(program);
                mAdapter.notifyItemInserted(mProgramList.size());
                mRecyclerView.smoothScrollToPosition(mProgramList.size());
                break;
            case R.id.cv_contentScreen_screen:
                Intent intent = new Intent(getContext(), ProgramManageActivity.class);
                startActivityForResult(intent, ITEM_MANAGE_REQUEST_CODE);
                break;
            case R.id.iv_screenCatag_refresh:
                readData();
                break;
            case R.id.fab_screen_add_pic:
                int proSize = mProgramList.size();
                proSize++;
                Program picProgram = new Program();
                picProgram.setId(System.currentTimeMillis());
                picProgram.setProgramName(getString(R.string.new_pic_program) + proSize);
                picProgram.setSortNumber(mProgramList.size());
                picProgram.setProgramType(ProgramType.Pic);
                mProgramDao.insert(picProgram);
                mProgramList.add(picProgram);
                mAdapter.notifyItemInserted(mProgramList.size());
                mRecyclerView.smoothScrollToPosition(mProgramList.size());
                break;
        }
    }

    private void openOrCloseMenu() {
        float textCurrent = mFabAddText.getTranslationY();
        float picCurrent = mFabAddPic.getTranslationY();
        mFabMenu.startAnimation(mFirstAddAnim);
        int transY = DensityUtil.dp2px(getContext(), 60);
        if (isMenuOpen){
            isMenuOpen=false;

            mTextAnim.setFloatValues(textCurrent,transY*2);
            mTextAnim.start();

            mPicAnim.setFloatValues(picCurrent,transY);
            mPicAnim.start();
        }else {
            isMenuOpen=true;
            mTextAnim.setFloatValues(textCurrent,-transY*2);
            mTextAnim.start();

            mPicAnim.setFloatValues(picCurrent,-transY);
            mPicAnim.start();
        }
    }

    @Override
    public void onAnimationStart(Animator animation) {
        if (animation==mTextAnim&&!isMenuOpen){
            mTextLable.setVisibility(View.GONE);
        }else if (animation==mPicAnim&&!isMenuOpen){
            mPicLable.setVisibility(View.GONE);
        }else if (animation==mTextAnim&&isMenuOpen){
            mFabAddText.setVisibility(View.VISIBLE);
        }else if (animation==mPicAnim&&isMenuOpen){
            mFabAddPic.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        if (animation==mTextAnim&&isMenuOpen){
            mTextLable.setVisibility(View.VISIBLE);
            mTextLable.startAnimation(mInAnim);
        }else if (animation==mPicAnim&&isMenuOpen){
            mPicLable.setVisibility(View.VISIBLE);
            mPicLable.startAnimation(mInAnim);
        }else if (animation==mTextAnim&&!isMenuOpen){
            mFabAddText.setVisibility(View.GONE);
        }else if (animation==mPicAnim&&!isMenuOpen){
            mFabAddPic.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }
}