package cn.com.hotled.xyled.fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.activity.ConnectHelpDiagActivity;
import cn.com.hotled.xyled.adapter.ConnectAdapter;
import cn.com.hotled.xyled.global.Global;
import cn.com.hotled.xyled.receiver.WiFiReceiver;
import cn.com.hotled.xyled.util.android.WifiAdmin;
import cn.com.hotled.xyled.util.communicate.SendCmdUtil;

import static cn.com.hotled.xyled.global.Global.CONNECT_TIMEOUT;
import static cn.com.hotled.xyled.global.Global.JUST_STOP_ANIM;
import static cn.com.hotled.xyled.global.Global.SEND_TEST;
import static cn.com.hotled.xyled.global.Global.UPDATE_NETWORK_INFO;
import static cn.com.hotled.xyled.global.Global.WIFI_AVAILABLE_ACTION;
import static cn.com.hotled.xyled.global.Global.WIFI_DISABLE;
import static cn.com.hotled.xyled.global.Global.WIFI_ENABLED;
import static cn.com.hotled.xyled.global.Global.WIFI_ERRO;

public class ConnectFragment extends Fragment implements View.OnClickListener, ConnectAdapter.OnItemOnClickListener {

    private static final int REQUST_LOCATION_PERMISSION_CODE = 301;
    @BindView(R.id.iv_connect_round)
    ImageView mIvRound;
    @BindView(R.id.iv_connect_wifiLogo)
    ImageView mIvLogo;
    @BindView(R.id.tv_connect_tip)
    TextView mTvTip;
    @BindView(R.id.tv_connect_state)
    TextView mTvState;
    @BindView(R.id.tv_connect_checkResult)
    TextView mTvCheckResult;
    @BindView(R.id.iv_connect_help)
    ImageView mIvHelp;
    @BindView(R.id.rv_connect_wifi)
    RecyclerView mRvWifi;

    private WifiAdmin mWifiAdmin;
    private List<ScanResult> mWifiList;
    private ConnectAdapter mConnectAdapter;
    private Animation mInsideAnim;
    private long mOldTime;
    private WiFiReceiver mWiFiReceiver;

    private Handler connHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case JUST_STOP_ANIM:
                    mInsideAnim.cancel();
                    break;
                case SEND_TEST:
                    SendCmdUtil sendCmdUtil = new SendCmdUtil(getActivity(),connHandler);
                    sendCmdUtil.sendCmd(SendCmdUtil.Cmd.Test);
                    break;
                case WIFI_ERRO:
                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    Toast.makeText(getActivity(), R.string.tos_wifi_switch, Toast.LENGTH_LONG).show();
                    break;
                case CONNECT_TIMEOUT:
                    Toast.makeText(getActivity(), R.string.tos_wifi_timeout, Toast.LENGTH_LONG).show();
                    break;
                case WIFI_AVAILABLE_ACTION:
                    refreshWifiAndState();
                    break;
                case WIFI_DISABLE:
                    refreshWifiAndState();
                    mTvState.setText(R.string.msg_wifi_disable);
                    mConnectAdapter.notifyDataSetChanged();
                    break;
                case WIFI_ENABLED:
                    refreshWifiAndState();
                    break;
                case UPDATE_NETWORK_INFO:
                    NetworkInfo networkInfo = msg.getData().getParcelable(Global.EXTRA_NETWORKSTATE);
                    if (networkInfo != null) {
                        if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                            mTvState.setText(getString(R.string.connected) + mWifiAdmin.getWifiInfo().getSSID());
                            mTvTip.setText(R.string.connected);
                            sendTestCmd();
                            connHandler.sendEmptyMessageDelayed(JUST_STOP_ANIM,3000);
                        } else if (networkInfo.getState().equals(NetworkInfo.State.CONNECTING)) {
                            mTvState.setText(R.string.connecting);
                            mTvTip.setText(R.string.connecting);
                        } else if (networkInfo.getState().equals(NetworkInfo.State.DISCONNECTING)) {
                            mTvState.setText(R.string.disconnecting);
                            mTvTip.setText(R.string.disconnecting);
                        } else if (networkInfo.getState().equals(NetworkInfo.State.DISCONNECTED)) {
                            mTvState.setText(R.string.disconnect);
                            mTvTip.setText(R.string.disconnect);
                        }
                        // 刷新状态显示
                        refreshWifiAndState();
                    }
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_connect, null);
        ButterKnife.bind(this, view);
        RequestPermission();
        loadData();
        initView();
        registerWifiReciver();//监听WiFi状态广播
        return view;
    }


    private void initView() {

        mInsideAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.search_round);
        mRvWifi.setLayoutManager(new LinearLayoutManager(getActivity()));
        mConnectAdapter = new ConnectAdapter(getActivity(), mWifiList, mWifiAdmin);
        mRvWifi.setAdapter(mConnectAdapter);
        mConnectAdapter.setItemOnClickListener(this);
        refreshWifiAndState();
    }

    private void loadData() {
        mWifiAdmin = new WifiAdmin(getActivity());
        mWifiList = new ArrayList<>();
        List<ScanResult> scanResults = mWifiAdmin.startScan();
        for (ScanResult scanResult : scanResults) {
            boolean startFlag = scanResult.SSID.startsWith(Global.SSID_START);
            boolean endFlag = scanResult.SSID.endsWith(Global.SSID_END);
            if (startFlag && endFlag) {
                mWifiList.add(scanResult);
            }
        }

    }

    private void connectWifi(int position) {

        //检查是否已连接过
        WifiConfiguration exsitsConfig = mWifiAdmin.isExsits(mWifiList.get(position).SSID);//这里的SSID 打印出来没有双引号包括
        if (exsitsConfig != null) {
            // 1.已连接过，直接使用该配置进行连接
            mWifiAdmin.setMaxPriority(exsitsConfig);//已经连接过的，需要设置优先级为最大的才能连上
            mWifiAdmin.connectWifi(exsitsConfig.networkId);
        } else {
            WifiConfiguration wifiInfo2 = mWifiAdmin.createWifiInfo2(mWifiList.get(position), Global.CARD_PASSWORD);
            mWifiAdmin.addNetWork(wifiInfo2);
        }
    }

    private void roundInsideOnclick() {
        if (mWifiList.size()==1){
            refreshWifiAndState();
            if (mWifiAdmin.getWifiInfo().getSSID().equals("\""+mWifiList.get(0).SSID+"\"")){
                return;
            }
            mIvRound.startAnimation(mInsideAnim);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    connectWifi(0);
                }
            }).start();
        }else {
            if (mWifiAdmin.checkWifiState()) {
                mIvRound.startAnimation(mInsideAnim);
                refreshWifiAndState();
                connHandler.sendEmptyMessageDelayed(JUST_STOP_ANIM,3000);
            }else {
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                Toast.makeText(getActivity(), R.string.tos_set_wifi_enable, Toast.LENGTH_SHORT).show();
            }
        }


    }
    private void registerWifiReciver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION); // ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.setPriority(Integer.MAX_VALUE); // 设置优先级，最高为1000
        mWiFiReceiver = new WiFiReceiver(connHandler);
        getContext().registerReceiver(mWiFiReceiver, intentFilter);
    }


    private void refreshWifiAndState() {
        long currentTime = System.currentTimeMillis();
        if (!(currentTime-mOldTime>5000)){
            return;
        }
        mOldTime = currentTime;

        List<ScanResult> scanResults = mWifiAdmin.startScan();
        mWifiList.clear();
        for (ScanResult scanResult : scanResults) {
            boolean startFlag = scanResult.SSID.startsWith(Global.SSID_START);
            boolean endFlag = scanResult.SSID.endsWith(Global.SSID_END);
            if (startFlag&&endFlag){
                mWifiList.add(scanResult);
            }
        }
        mConnectAdapter.notifyDataSetChanged();

        String ssid = mWifiAdmin.getWifiInfo().getSSID();
        boolean startFlag = ssid.contains(Global.SSID_START);
        boolean endFlag = ssid.contains(Global.SSID_END);
        if (startFlag&&endFlag){
            mTvState.setTextColor(Color.parseColor("#00C853"));
            mIvLogo.setImageResource(R.drawable.ic_wifi_complete);
            mIvRound.setImageResource(R.drawable.connect_view_completed);
        }else {
            mTvTip.setText(R.string.click_check);
            mTvState.setTextColor(getResources().getColor(R.color.textSecondary));
            mIvLogo.setImageResource(R.drawable.ic_wifi_uncomplete);
            mIvRound.setImageResource(R.drawable.connect_view_uncomplete);
        }

        String result=getString(R.string.search_result)+mWifiList.size();
        mTvCheckResult.setText(result);
    }

    private void sendTestCmd() {
        String ssid = mWifiAdmin.getWifiInfo().getSSID();
        boolean startFlag = ssid.contains(Global.SSID_START);
        boolean endFlag = ssid.contains(Global.SSID_END);
        if (startFlag&&endFlag){
            connHandler.sendEmptyMessageDelayed(SEND_TEST,2000);
        }

    }



    @TargetApi(Build.VERSION_CODES.M)
    private void RequestPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // 给出一个提示，告诉用户为什么需要这个权限

            } else {
                // 用户没有拒绝，直接申请权限
                String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
                ActivityCompat.requestPermissions(getActivity(), permissions, REQUST_LOCATION_PERMISSION_CODE);
                //用户授权的结果会回调到FragmentActivity的onRequestPermissionsResult
                loadData();
            }
        } else {
            //已经拥有授权
            loadData();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadData();
        } else {
            // 权限拒绝了
            loadData();
            Toast.makeText(getContext(), R.string.tos_request_permission, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getContext().unregisterReceiver(mWiFiReceiver);//取消监听广播
    }

    @Override
    public void onItemClick(View view, int position) {
        //点击，开始动画旋转
        //先判断当前连接是不是这个，如果是则不进行操作，如果不是才进行连接
        if (mWifiAdmin.getWifiInfo().getSSID().equals("\"" + mWifiList.get(position).SSID + "\"")) {
            return;
        }
        mIvRound.startAnimation(mInsideAnim);
        mTvTip.setText(R.string.connecting);
        connectWifi(position);
    }

    @OnClick({R.id.iv_connect_round, R.id.iv_connect_help})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_connect_round:
                roundInsideOnclick();
                break;
            case R.id.iv_connect_help:
                startActivity(new Intent(getContext(), ConnectHelpDiagActivity.class));
                break;
        }
    }
}
