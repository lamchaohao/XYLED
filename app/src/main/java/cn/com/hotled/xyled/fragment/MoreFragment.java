package cn.com.hotled.xyled.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.activity.AboutUsActivity;
import cn.com.hotled.xyled.activity.ConnectWifiActivity;

/**
 * Created by Lam on 2016/12/1.
 */

public class MoreFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_more, null);
        initView(view);
        return view;
    }

    private void initView(View view ) {

        view.findViewById(R.id.ll_more_checkscreen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ConnectWifiActivity.class));
            }
        });
        view.findViewById(R.id.ll_more_about_us).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AboutUsActivity.class));
            }
        });

        view.findViewById(R.id.ll_more_update).setOnClickListener(new View.OnClickListener() {
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
