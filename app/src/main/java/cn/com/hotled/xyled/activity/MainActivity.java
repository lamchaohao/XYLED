package cn.com.hotled.xyled.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.adapter.MainFragmentAdapter;
import cn.com.hotled.xyled.fragment.ConnectFragment;
import cn.com.hotled.xyled.fragment.MoreFragment;
import cn.com.hotled.xyled.fragment.ScreenFragment;
import cn.com.hotled.xyled.fragment.SettingFragment;
import cn.com.hotled.xyled.global.Global;

public class MainActivity extends BaseAppCompatAtivity implements View.OnClickListener {

    @BindView(R.id.iv_main_screen)
    ImageView ivScreen;
    @BindView(R.id.iv_main_connect)
    ImageView ivConnect;
    @BindView(R.id.iv_main_setting)
    ImageView ivSetting;
    @BindView(R.id.iv_main_more)
    ImageView ivMore;
    @BindView(R.id.tv_main_screen)
    TextView tvScreen;
    @BindView(R.id.tv_main_connect)
    TextView tvConnect;
    @BindView(R.id.tv_main_setting)
    TextView tvSetting;
    @BindView(R.id.tv_main_more)
    TextView tvMore;
    @BindView(R.id.ll_main_screen)
    LinearLayout llScreen;
    @BindView(R.id.ll_main_connect)
    LinearLayout llConnect;
    @BindView(R.id.ll_main_setting)
    LinearLayout llSetting;
    @BindView(R.id.ll_main_more)
    LinearLayout llMore;

    private ViewPager mViewPager;
    private ArrayList<Fragment> fragmentList = new ArrayList<>();
    private FragmentManager mFragmentManager;
    private TextView mToolbarTitle;
    private ScreenFragment mScreenFragment;
    private ConnectFragment mConnectFragment;
    private SettingFragment mSettingFragment;
    private MoreFragment mMoreFragment;
    private boolean backFlag;
    private long firstTime;
    private long lastTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initToolbar();
        initView();
    }

    private void initToolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        Button sendButton = (Button) mToolbar.findViewById(R.id.bt_toolbar_send);
        mToolbarTitle = (TextView) mToolbar.findViewById(R.id.tv_toolbar_title);
        mToolbarTitle.setText(R.string.program);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SendActivity.class);
                startActivity(intent);
            }
        });
        setSupportActionBar(mToolbar);
    }

    private void initView() {
        initBottom();

        mViewPager = (ViewPager) findViewById(R.id.vp_main);
        mFragmentManager = getSupportFragmentManager();
        mScreenFragment = new ScreenFragment();
        mConnectFragment = new ConnectFragment();
        mSettingFragment = new SettingFragment();
        mMoreFragment = new MoreFragment();
        fragmentList.add(mScreenFragment);
        fragmentList.add(mConnectFragment);
        fragmentList.add(mSettingFragment);
        fragmentList.add(mMoreFragment);
        MainFragmentAdapter adapter = new MainFragmentAdapter(mFragmentManager, fragmentList);
        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                resetImg();
                switch (position) {
                    case 0:
                        ivScreen.setImageResource(R.drawable.ic_live_tv_primary_dark_700_36dp);
                        tvScreen.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                        mToolbarTitle.setText(R.string.program);
                        break;
                    case 1:
                        ivConnect.setImageResource(R.drawable.ic_search_primary_dark_36dp);
                        tvConnect.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                        mToolbarTitle.setText(R.string.connect);
                        break;
                    case 2:
                        ivSetting.setImageResource(R.drawable.ic_settings_primary_dark_36dp);
                        tvSetting.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                        mToolbarTitle.setText(R.string.setting);
                        break;
                    case 3:
                        ivMore.setImageResource(R.drawable.ic_more_primary_dark_700_36dp);
                        tvMore.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                        mToolbarTitle.setText(R.string.more);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mViewPager.setCurrentItem(0);//先设置一个
        ivScreen.setImageResource(R.drawable.ic_live_tv_primary_dark_700_36dp);
        tvScreen.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        boolean isFirstProgram = getSharedPreferences(Global.SP_SYSTEM_CONFIG, MODE_PRIVATE).getBoolean(Global.KEY_FIRST_IN_PROGRAM, true);
        if (isFirstProgram) {
            startActivity(new Intent(this,SetParamsHelpDiagActivity.class));
            startActivity(new Intent(this,UseGuideActivity.class));
            getSharedPreferences(Global.SP_SYSTEM_CONFIG, MODE_PRIVATE).edit().putBoolean(Global.KEY_FIRST_IN_PROGRAM, false).apply();
        }
    }

    private void initBottom() {
        llScreen.setOnClickListener(this);
        llConnect.setOnClickListener(this);
        llSetting.setOnClickListener(this);
        llMore.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        //resetImg();//点击哪个tab,对应的颜色要变亮，因此，在点击具体的tab之前先将所有的图片都重置为未点击的状态，即暗色图片
        switch (v.getId()) {
            case R.id.ll_main_screen:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.ll_main_connect:
                mViewPager.setCurrentItem(1);
                break;
            case R.id.ll_main_setting:
                mViewPager.setCurrentItem(2);
                break;
            case R.id.ll_main_more:
                mViewPager.setCurrentItem(3);
                break;
        }
    }

    private void resetImg() {
        ivScreen.setImageResource(R.drawable.ic_live_tv_grey_700_36dp);
        tvScreen.setTextColor(getResources().getColor(R.color.textSecondary));
        ivConnect.setImageResource(R.drawable.ic_search_grey_700_36dp);
        tvConnect.setTextColor(getResources().getColor(R.color.textSecondary));
        ivSetting.setImageResource(R.drawable.ic_settings_grey_700_36dp);
        tvSetting.setTextColor(getResources().getColor(R.color.textSecondary));
        ivMore.setImageResource(R.drawable.ic_more_grey_700_36dp);
        tvMore.setTextColor(getResources().getColor(R.color.textSecondary));
    }

    @Override
    public void onBackPressed() {
        if(mViewPager.getCurrentItem()==0&&mScreenFragment.isMenuClose()) {
            mScreenFragment.closeFabMenu();
        }else if (!backFlag){//第一次点击
            Toast.makeText(this, R.string.tos_press_again, Toast.LENGTH_SHORT).show();
            firstTime = System.currentTimeMillis();
            backFlag=true;
        }else{
            //第二次点击
            lastTime = System.currentTimeMillis();
            long gapTime=lastTime-firstTime;

            if (gapTime<2000){
               finish();
            }else{
                //防止时间过长再次点击没反应
                Toast.makeText(this, R.string.tos_press_again, Toast.LENGTH_SHORT).show();
                firstTime = System.currentTimeMillis();

            }
        }
    }
}
