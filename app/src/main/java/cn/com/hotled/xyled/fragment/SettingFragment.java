package cn.com.hotled.xyled.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.activity.MainActivity;
import cn.com.hotled.xyled.activity.RemoteActivity;
import cn.com.hotled.xyled.activity.SelectLanguageActivity;
import cn.com.hotled.xyled.activity.SettingActivity;
import cn.com.hotled.xyled.activity.SocketActivity;

import static android.app.Activity.RESULT_OK;

/**
 * 设置页面
 * Created by Lam on 2016/12/1.
 */

public class SettingFragment extends Fragment {

    private static final int SELECT_LANGUAGE_CODE = 201;
    @BindView(R.id.tv_setfrgm_enterWifi)
    TextView mTvSetfrgmEnterWifi;
    @BindView(R.id.tv_setfrgm_testConnnet)
    TextView mTvSetfrgmTestConnnet;
    @BindView(R.id.tv_setfrgm_screenSet)
    TextView mTvSetfrgmScreenSet;
    @BindView(R.id.tv_setfrgm_remote)
    TextView mTvSetfrgmRemote;
    @BindView(R.id.tv_setfrgm_language)
    TextView mTvSetfrgmLanguage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick({R.id.tv_setfrgm_enterWifi, R.id.tv_setfrgm_testConnnet, R.id.tv_setfrgm_screenSet, R.id.tv_setfrgm_remote, R.id.tv_setfrgm_language})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_setfrgm_enterWifi:
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                break;
            case R.id.tv_setfrgm_testConnnet:
                startActivity(new Intent(getContext(), SocketActivity.class));
                break;
            case R.id.tv_setfrgm_screenSet:
                startActivity(new Intent(getContext(), SettingActivity.class));
                break;
            case R.id.tv_setfrgm_remote:
                startActivity(new Intent(getContext(), RemoteActivity.class));
                break;
            case R.id.tv_setfrgm_language:
                Intent intent = new Intent(getContext(), SelectLanguageActivity.class);
                startActivityForResult(intent, SELECT_LANGUAGE_CODE);
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == SELECT_LANGUAGE_CODE) {
            Intent intent = new Intent(getContext(), MainActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
    }

}
