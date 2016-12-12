package cn.com.hotled.xyled.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.adapter.MainFragmentAdapter;
import cn.com.hotled.xyled.bean.LedScreen;
import cn.com.hotled.xyled.fragment.MoreFragment;
import cn.com.hotled.xyled.fragment.ScreenFragment;
import cn.com.hotled.xyled.fragment.SettingFragment;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int ADD_SCREEN_CODE = 366;
    @BindView(R.id.iv_main_screen)
    ImageView ivScreen;
    @BindView(R.id.iv_main_setting)
    ImageView ivSetting;
    @BindView(R.id.iv_main_more)
    ImageView ivMore;
    @BindView(R.id.tv_main_screen)
    TextView tvScreen;
    @BindView(R.id.tv_main_setting)
    TextView tvSetting;
    @BindView(R.id.tv_main_more)
    TextView tvMore;
    @BindView(R.id.ll_main_screen)
    LinearLayout llScreen;
    @BindView(R.id.ll_main_setting)
    LinearLayout llSetting;
    @BindView(R.id.ll_main_more)
    LinearLayout llMore;

    private ViewPager mViewPager;
    private ArrayList<Fragment> fragmentList = new ArrayList<>();
    private android.support.v4.app.FragmentManager mFragmentManager;
    private TextView mToolbarTitle;
    private ScreenFragment mScreenFragment;
    private SettingFragment mSettingFragment;
    private MoreFragment mMoreFragment;

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
        final ImageView ivAdd = (ImageView) mToolbar.findViewById(R.id.iv_toolbar_add);
        mToolbarTitle = (TextView) mToolbar.findViewById(R.id.tv_toolbar_title);
        ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MainActivity.this,AddScreenActivity.class),ADD_SCREEN_CODE);
            }
        });
        mToolbarTitle.setText("显示屏");
        setSupportActionBar(mToolbar);
    }

    private void initView() {
        initBottom();

        mViewPager = (ViewPager) findViewById(R.id.vp_main);
        mFragmentManager = getSupportFragmentManager();
        mScreenFragment = new ScreenFragment();
        mSettingFragment = new SettingFragment();
        mMoreFragment = new MoreFragment();
        fragmentList.add(mScreenFragment);
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
                        mToolbarTitle.setText("显示屏");
                        break;
                    case 1:
                        ivSetting.setImageResource(R.drawable.ic_settings_primary_dark_36dp);
                        tvSetting.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                        mToolbarTitle.setText("设置");
                        break;
                    case 2:
                        ivMore.setImageResource(R.drawable.ic_more_primary_dark_700_36dp);
                        tvMore.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                        mToolbarTitle.setText("更多");
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
    }

    private void initBottom() {
        llScreen.setOnClickListener(this);
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
            case R.id.ll_main_setting:
                mViewPager.setCurrentItem(1);
                break;
            case R.id.ll_main_more:
                mViewPager.setCurrentItem(2);
                break;
        }
    }

    private void resetImg() {
        ivScreen.setImageResource(R.drawable.ic_live_tv_grey_700_36dp);
        tvScreen.setTextColor(getResources().getColor(R.color.textSecondary));
        ivSetting.setImageResource(R.drawable.ic_settings_grey_700_36dp);
        tvSetting.setTextColor(getResources().getColor(R.color.textSecondary));
        ivMore.setImageResource(R.drawable.ic_more_grey_700_36dp);
        tvMore.setTextColor(getResources().getColor(R.color.textSecondary));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("result","requestCode"+requestCode+",resultCode=="+resultCode);
        if (resultCode==RESULT_OK&&requestCode==ADD_SCREEN_CODE){
            LedScreen screen = data.getParcelableExtra("screen");
            Log.i("result",screen.toString());
            mScreenFragment.addScreen(screen);
        }
    }
}
