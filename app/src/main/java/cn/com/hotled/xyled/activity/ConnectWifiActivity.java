package cn.com.hotled.xyled.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.List;

import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.adapter.ConnectWifiAdapter;
import cn.com.hotled.xyled.decoration.WifiItemDecoration;
import cn.com.hotled.xyled.util.WifiAdmin;

public class ConnectWifiActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private WifiAdmin mWifiAdmin;
    private List<ScanResult> mWifiList;
    private ConnectWifiAdapter connectWifiAdapter;
    public int WifiStatuCode;
    public static final int WIFI_STATE_CONNECTING = 101;
    public static final int WIFI_STATE_CONNECTED = 102;
    public static final int WIFI_STATE_DISCONNECTING = 103;
    public static final int WIFI_STATE_DISCONNECTED = 104;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_wifi);
        loadData();
        initView();
        registerWifiReciver();//监听WiFi状态广播

    }

    private void loadData() {
        mWifiAdmin = new WifiAdmin(this);
        mWifiList=mWifiAdmin.startScan();
    }

    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.rcv_connw_show);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        connectWifiAdapter = new ConnectWifiAdapter(this, mWifiList,mWifiAdmin);
        recyclerView.setAdapter(connectWifiAdapter);
        recyclerView.addItemDecoration(new WifiItemDecoration(this,WifiItemDecoration.VERTICAL_LIST));
        connectWifiAdapter.setItemOnClickListener(new ConnectWifiAdapter.OnItemOnClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                connectWifi(position);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
    }

    private void registerWifiReciver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        intentFilter.addAction("android.net.wifi.STATE_CHANGE"); // ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.setPriority(1000); // 设置优先级，最高为1000
        registerReceiver(wifiStateReceiver,intentFilter);
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
                    if (tempList.get(i).SSID!=null||tempList.get(i).SSID.equals(""))//防止有无名称的WiFi加入进来
                    mWifiList.add(tempList.get(i));
                }
            }
        }
        connectWifiAdapter.notifyDataSetChanged();
    }



    /**
     * 连接WiFi
     * @param position
     */
    private void connectWifi(final int position) {
        //检查是否已连接过
        WifiConfiguration exsitsConfig = mWifiAdmin.isExsits(mWifiList.get(position).SSID);//这里的SSID 打印出来没有双引号包括
        if (exsitsConfig!=null){
            // 1.已连接过，直接使用该配置进行连接
            Log.i("connecAct","已连接过，直接使用该配置进行连接");
            mWifiAdmin.setMaxPriority(exsitsConfig);//已经连接过的，需要设置优先级为最大的才能连上
            boolean success = mWifiAdmin.connectWifi(exsitsConfig.networkId);
            //连接完成，下面代码不再执行
            if (success)
                return;
            else{
                Log.i("connecAct",exsitsConfig.toString());
            }
            //// TODO: 2016/10/19 WiFi列表上显示已连接
        }

        //2.没连接过
        //2.1需要密码连接的，需输入密码

        final WifiConfiguration wfConf=new WifiConfiguration();

        if (isNeedKey(mWifiList.get(position).capabilities)){
            MaterialDialog.Builder builder = new MaterialDialog.Builder(this);
            builder.title("连接到"+mWifiList.get(position).SSID)
                    .inputType(InputType.TYPE_CLASS_TEXT)
                    .input("请输入密码", null, new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                            int networkID = mWifiAdmin.createWifiInfo2(mWifiList.get(position), input.toString());
                            mWifiAdmin.connectWifi(networkID);
                            dialog.dismiss();
                        }
                    })
                    .positiveText("connect")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            Toast.makeText(ConnectWifiActivity.this, "onPositive(new MaterialDialog.SingleButtonCallback()", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .negativeText("cancle")
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                        }
                    }).show();

        }else{//2.2不需要密码

            int netId = mWifiAdmin.createWifiInfo2(mWifiList.get(position), "");
            mWifiAdmin.connectWifi(netId);
        }

    }

    private boolean isNeedKey(String capabilities) {
        if (capabilities.contains("WPA2-PSK")) {
            // WPA2-PSK加密
            return true;
        } else if (capabilities.contains("WPA-PSK")) {
            // WPA-PSK加密
            return true;
        } else if (capabilities.contains("WPA-EAP")) {
            // WPA-EAP加密
            return true;
        } else if (capabilities.contains("WEP")) {
            // WEP加密
            return true;
        } else {
            // 无密码
            return false;
        }

    }


    public BroadcastReceiver wifiStateReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {

                Log.i("connecAct","wifi列表刷新了");

                refreshWifiList();
            } else if (action.equals("android.net.wifi.STATE_CHANGE")) {
                //可以监听更精确的状态
                NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)){
                    setWifiStatuCode(WIFI_STATE_CONNECTED);
                }else if (networkInfo.getState().equals(NetworkInfo.State.CONNECTING)){
                    setWifiStatuCode(WIFI_STATE_CONNECTING);
                }else if (networkInfo.getState().equals(NetworkInfo.State.DISCONNECTING)){
                    setWifiStatuCode(WIFI_STATE_DISCONNECTING);
                }else if (networkInfo.getState().equals(NetworkInfo.State.DISCONNECTED)){
                    setWifiStatuCode(WIFI_STATE_DISCONNECTED);
                }

                Log.i("connecAct","state:");
                Log.i("connecAct","wifi状态发生了变化");

                // 刷新状态显示
                refreshWifiList();
            }
        }
    };

    public int getWifiStatuCode() {
        return WifiStatuCode;
    }

    public void setWifiStatuCode(int wifiStatuCode) {
        WifiStatuCode = wifiStatuCode;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(wifiStateReceiver);
    }
}
