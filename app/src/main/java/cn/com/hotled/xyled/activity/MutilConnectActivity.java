package cn.com.hotled.xyled.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.List;

import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.adapter.MutilConnAdapter;
import cn.com.hotled.xyled.util.WifiAdmin;

public class MutilConnectActivity extends AppCompatActivity {
    public static final int WIFI_STATE_CONNECTING = 101;
    public static final int WIFI_STATE_CONNECTED = 102;
    public static final int WIFI_STATE_DISCONNECTING = 103;
    public static final int WIFI_STATE_DISCONNECTED = 104;
    int WifiStatuCode ;
    private RecyclerView mRecyclerView;
    private WifiAdmin mWifiAdmin;
    private List<ScanResult> mWifiList;
    private MutilConnAdapter mAdapter;
    private int mConnectCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mutil_connect);
        initView();
        registerWifiReciver();

    }

    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_multilconn);
        loadData();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new MutilConnAdapter(this,mWifiList,mWifiAdmin);
        mRecyclerView.setAdapter(mAdapter);
        View bt_startConnect = findViewById(R.id.bt_startConnect);

    }

    private void loadData() {
        mWifiAdmin = new WifiAdmin(this);
        mWifiList=mWifiAdmin.startScan();
    }


    private void refreshWifiList() {
        // 剔除ssid中的重复项，只保留相同ssid中信号最强的哪一个

        List<ScanResult> tempList= mWifiAdmin.startScan();
        mWifiList.clear();
        boolean isAdd = true;

        if (mWifiList != null) {
            for (int i = 0; i < tempList.size(); i++) {
                isAdd = true;
                for (int j = 0; j < mWifiList.size(); j++) {
                    if (mWifiList.get(j).SSID.equals(tempList.get(i).SSID)) {
                        isAdd = false;
                        if (mWifiList.get(j).level < tempList.get(i).level) {
                            // ssid相同且新的信号更强
                            mWifiList.remove(j);
                            mWifiList.add(tempList.get(i));
                            break;
                        }
                    }
                }
                if (isAdd){
                    if (tempList.get(i).SSID!=null&&!tempList.get(i).SSID.equals("")&&tempList.get(i).SSID.contains("HC-LED"))//防止有无名称的WiFi加入进来
                        mWifiList.add(tempList.get(i));
                }
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    public int getWifiStatuCode() {
        return WifiStatuCode;
    }


    private void registerWifiReciver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION); // ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.setPriority(Integer.MAX_VALUE); // 设置优先级，最高为1000
        registerReceiver(wifiStateReceiver,intentFilter);
    }


    public BroadcastReceiver wifiStateReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.v("connecAct","action="+action);
            if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {

            }else if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                // 这个监听wifi的打开与关闭，与wifi的连接无关
                int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
                switch (wifiState) {
                    case WifiManager.WIFI_STATE_DISABLED:

                        break;
                    case WifiManager.WIFI_STATE_ENABLED:

                        break;
                }
                refreshWifiList();
            }else if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)){
                //可以监听更精确的状态
                NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (networkInfo!=null){
                    if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)){
                        WifiStatuCode=WIFI_STATE_CONNECTED;
                        Log.d("connecAct","WIFI_STATE_CONNECTED 已连接");
                        if (mConnectCount<=20)
                            connectOther();
                        mConnectCount++;
                    }else if (networkInfo.getState().equals(NetworkInfo.State.CONNECTING)){
                        WifiStatuCode=WIFI_STATE_CONNECTING;
                        Log.d("connecAct","WIFI_STATE_CONNECTED 正在连接");
                    }else if (networkInfo.getState().equals(NetworkInfo.State.DISCONNECTING)){
                        WifiStatuCode=WIFI_STATE_DISCONNECTING;
                        Log.d("connecAct","WIFI_STATE_CONNECTED 正在断开");
                    }else if (networkInfo.getState().equals(NetworkInfo.State.DISCONNECTED)){
                        WifiStatuCode=WIFI_STATE_DISCONNECTED;
                        Log.d("connecAct","WIFI_STATE_CONNECTED 已断开");
                    }

                    // 刷新状态显示
                    refreshWifiList();
                }

            }
        }
    };

    private void connectOther() {
        ScanResult scanResultZero = null;
        ScanResult scanResultOne = null;
        if (mWifiList.size()>=2){
            scanResultZero = mWifiList.get(0);
            scanResultOne = mWifiList.get(1);
        }else {
            mConnectCount=0;
            return;
        }


        WifiInfo wifiInfo = mWifiAdmin.getWifiInfo();
        if (wifiInfo.getSSID().equals(scanResultZero.SSID)) {
            WifiConfiguration exsitsConfig = mWifiAdmin.isExsits(scanResultZero.SSID);//这里的SSID 打印出来没有双引号包括
            if (exsitsConfig!=null){
                // 1.已连接过，直接使用该配置进行连接
                Log.i("multiconnecAct","已连接过"+exsitsConfig.SSID);
                mWifiAdmin.setMaxPriority(exsitsConfig);//已经连接过的，需要设置优先级为最大的才能连上
                boolean success = mWifiAdmin.connectWifi(exsitsConfig.networkId);
                //连接完成，下面代码不再执行
                if (success)
                    Log.i("multiconnecAct","success");
                else
                    connectOther();
            }else {
                int networkID = mWifiAdmin.createWifiInfo2(scanResultZero, "88888888");
                boolean b = mWifiAdmin.connectWifi(networkID);
                if (!b){
                    connectOther();
                }
            }
        }else {
            WifiConfiguration exsitsConfig = mWifiAdmin.isExsits(scanResultOne.SSID);//这里的SSID 打印出来没有双引号包括
            if (exsitsConfig!=null){
                // 1.已连接过，直接使用该配置进行连接
                Log.i("multiconnecAct","已连接过"+exsitsConfig.SSID);
                mWifiAdmin.setMaxPriority(exsitsConfig);//已经连接过的，需要设置优先级为最大的才能连上
                boolean success = mWifiAdmin.connectWifi(exsitsConfig.networkId);
                //连接完成，下面代码不再执行
                if (success)
                    Log.i("multiconnecAct","success");
                else
                    connectOther();
            }else {
                int networkID = mWifiAdmin.createWifiInfo2(scanResultOne, "88888888");
                boolean b = mWifiAdmin.connectWifi(networkID);
                if (!b){
                    connectOther();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(wifiStateReceiver);
    }


}
