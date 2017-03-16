package cn.com.hotled.xyled.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.activity.MainActivity;
import cn.com.hotled.xyled.activity.RemoteActivity;
import cn.com.hotled.xyled.activity.SelectLanguageActivity;
import cn.com.hotled.xyled.activity.SettingActivity;
import cn.com.hotled.xyled.activity.SocketActivity;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Lam on 2016/12/1.
 */

public class SettingFragment extends Fragment {

    private static final int SELECT_LANGUAGE_CODE = 201;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, null);
        initView(view);
        return view;
    }

    private void initView(View view) {

        View ll_enterWifi = view.findViewById(R.id.tv_setfrgm_enterWifi);
        ll_enterWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            }
        });

        view.findViewById(R.id.tv_setfrgm_testConnnet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), SocketActivity.class));
            }
        });

        view.findViewById(R.id.tv_setfrgm_screenSet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), SettingActivity.class));
            }
        });


        view.findViewById(R.id.tv_setfrgm_remote).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), RemoteActivity.class));
            }
        });
        view.findViewById(R.id.tv_setfrgm_language).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getContext(), SelectLanguageActivity.class);
                startActivityForResult(intent,SELECT_LANGUAGE_CODE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode==RESULT_OK&&requestCode==SELECT_LANGUAGE_CODE){
            Intent intent =new Intent(getContext(), MainActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
    }
}
