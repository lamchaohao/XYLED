package cn.com.hotled.xyled.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.activity.ScreenSettingsActivity;
import cn.com.hotled.xyled.activity.SocketActivity;

/**
 * Created by Lam on 2016/12/1.
 */

public class SettingFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        view.findViewById(R.id.ll_setting_socket).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), SocketActivity.class));
            }
        });

        View ll_enterWifi = view.findViewById(R.id.ll_setting_enterWifi);
        ll_enterWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            }
        });
        view.findViewById(R.id.ll_setting_screenConfig).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ScreenSettingsActivity.class));
            }
        });
        view.findViewById(R.id.ll_setting_language).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setTitle("coming soon")
                        .setMessage("正在开发")
                        .setPositiveButton("好的",null)
                        .show();
            }
        });
    }


}
