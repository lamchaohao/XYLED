package cn.com.hotled.xyled.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.activity.ConnectWifiActivity;
import cn.com.hotled.xyled.activity.HomeActivity;

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
        View ll_enterHome = view.findViewById(R.id.ll_enterHome);
        ll_enterHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContext().startActivity(new Intent(getContext(), HomeActivity.class));
            }
        });
        View ll_enterWifi = view.findViewById(R.id.ll_enterWifi);
        ll_enterWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContext().startActivity(new Intent(getContext(), ConnectWifiActivity.class));
            }
        });
    }
}
