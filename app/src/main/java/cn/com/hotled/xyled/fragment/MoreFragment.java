package cn.com.hotled.xyled.fragment;

import android.content.Intent;
import android.os.Bundle;
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
import cn.com.hotled.xyled.activity.AboutUsActivity;
import cn.com.hotled.xyled.activity.CheckScreenActivity;
import cn.com.hotled.xyled.activity.ConnectedSendSettingActivity;
import cn.com.hotled.xyled.activity.ForwardDataActivity;
import cn.com.hotled.xyled.activity.UseGuideActivity;

/**
 * 更多页面
 * Created by Lam on 2016/12/1.
 */

public class MoreFragment extends Fragment {
    @BindView(R.id.tv_more_search)
    TextView mTvMoreSearch;
    @BindView(R.id.tv_more_sendFromComputer)
    TextView mTvMoreSendFromComputer;
    @BindView(R.id.tv_more_help)
    TextView mTvMoreHelp;
    @BindView(R.id.tv_more_about)
    TextView mTvMoreAbout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_more, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick({R.id.tv_more_search,R.id.tv_more_autoSend, R.id.tv_more_sendFromComputer, R.id.tv_more_help, R.id.tv_more_about})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_more_search:
                startActivity(new Intent(getContext(), CheckScreenActivity.class));
                break;
            case R.id.tv_more_autoSend:
                startActivity(new Intent(getContext(), ConnectedSendSettingActivity.class));
                break;
            case R.id.tv_more_sendFromComputer:
                startActivity(new Intent(getContext(), ForwardDataActivity.class));
                break;
            case R.id.tv_more_help:
                startActivity(new Intent(getContext(), UseGuideActivity.class));
                break;
            case R.id.tv_more_about:
                startActivity(new Intent(getContext(), AboutUsActivity.class));
                break;
        }
    }
}
