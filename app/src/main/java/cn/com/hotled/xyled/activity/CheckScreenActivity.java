package cn.com.hotled.xyled.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.List;

import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.adapter.ConnectWifiAdapter;
import cn.com.hotled.xyled.util.android.DensityUtil;
import cn.com.hotled.xyled.util.android.WifiAdmin;

public class CheckScreenActivity extends BaseActivity {

    private static final int REQUST_LOCATION_PERMISSION_CODE = 101;
    private RecyclerView recyclerView;
    private WifiAdmin mWifiAdmin;
    private List<ScanResult> mWifiList;
    private ConnectWifiAdapter connectWifiAdapter;
    public int WifiStatuCode;
    public static final int WIFI_STATE_CONNECTING = 101;
    public static final int WIFI_STATE_CONNECTED = 102;
    public static final int WIFI_STATE_DISCONNECTING = 103;
    public static final int WIFI_STATE_DISCONNECTED = 104;
    private Switch mWifiSwitcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_screen);
        RequestPermission();
        loadData();
        initView();
        registerWifiReciver();//监听WiFi状态广播

    }

    @TargetApi(Build.VERSION_CODES.M)
    private void RequestPermission() {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // 给出一个提示，告诉用户为什么需要这个权限

            } else {
                // 用户没有拒绝，直接申请权限
                String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
                ActivityCompat.requestPermissions(this, permissions, REQUST_LOCATION_PERMISSION_CODE);
                //用户授权的结果会回调到FragmentActivity的onRequestPermissionsResult
                loadData();
            }
        }else {
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
            Toast.makeText(this, "需要权限才能开启WIFI", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
        }
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
        mWifiSwitcher = (Switch) findViewById(R.id.sw_openWiFi);
        View rlOpenWifi = findViewById(R.id.rl_openwifi);
        rlOpenWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWifi();
            }
        });
        mWifiSwitcher.setChecked(mWifiAdmin.mWifiManager.isWifiEnabled());
        mWifiSwitcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    openWifi();
            }
        });

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

    private void openWifi() {
        if (mWifiSwitcher.isChecked()) {
            //想要开启
            if (mWifiAdmin.mWifiManager.isWifiEnabled()) {
                mWifiSwitcher.setChecked(true);
                return;
            }else {
                if (!mWifiAdmin.openWifi()) {
                    mWifiSwitcher.setChecked(false);
                    Snackbar.make(recyclerView,"打开WIFI失败",Snackbar.LENGTH_LONG).setAction("打开系统设置", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        }
                    }).show();
                }
            }

        }else {
            //想要关闭
            if (!mWifiAdmin.mWifiManager.isWifiEnabled()) {
                mWifiSwitcher.setChecked(false);
                return;
            }else {
                if (!mWifiAdmin.closeWifi()) {
                    mWifiSwitcher.setChecked(true);
                    Snackbar.make(recyclerView,"关闭WIFI失败",Snackbar.LENGTH_LONG).setAction("打开系统设置", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        }
                    }).show();
                }
            }
        }
    }

    private void registerWifiReciver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION); // ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.setPriority(Integer.MAX_VALUE); // 设置优先级，最高为1000
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
                    if (tempList.get(i).SSID!=null&&!tempList.get(i).SSID.equals(""))//防止有无名称的WiFi加入进来
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
                            WifiConfiguration config = mWifiAdmin.createWifiInfo2(mWifiList.get(position), input.toString());
                            mWifiAdmin.connectWifi(config.networkId);
                            dialog.dismiss();
                        }
                    })
                    .positiveText("connect")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            Toast.makeText(CheckScreenActivity.this, "MaterialDialog.SingleButtonCallback()", Toast.LENGTH_SHORT).show();
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

            WifiConfiguration wifiInfo2 = mWifiAdmin.createWifiInfo2(mWifiList.get(position), "");
            mWifiAdmin.connectWifi(wifiInfo2.networkId);
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
            Log.v("connecAct","action="+action);
            if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                refreshWifiList();
            }else if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                // 这个监听wifi的打开与关闭，与wifi的连接无关
                int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
                switch (wifiState) {
                    case WifiManager.WIFI_STATE_DISABLED:
                        Log.i("connecAct","wifi关闭");
                        mWifiSwitcher.setChecked(false);
                        mWifiList.clear();
                        connectWifiAdapter.notifyDataSetChanged();
                        break;
                    case WifiManager.WIFI_STATE_ENABLED:
                        Log.i("connecAct","wifi开启");
                        mWifiSwitcher.setChecked(true);
                        break;
                }
            }else if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)){
                //可以监听更精确的状态
                NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (networkInfo!=null){
                    if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)){
                        setWifiStatuCode(WIFI_STATE_CONNECTED);
                        Log.i("connecAct","WIFI_STATE_CONNECTED 已连接");
                    }else if (networkInfo.getState().equals(NetworkInfo.State.CONNECTING)){
                        setWifiStatuCode(WIFI_STATE_CONNECTING);
                        Log.i("connecAct","WIFI_STATE_CONNECTED 正在连接");
                    }else if (networkInfo.getState().equals(NetworkInfo.State.DISCONNECTING)){
                        setWifiStatuCode(WIFI_STATE_DISCONNECTING);
                        Log.i("connecAct","WIFI_STATE_CONNECTED 正在断开");
                    }else if (networkInfo.getState().equals(NetworkInfo.State.DISCONNECTED)){
                        setWifiStatuCode(WIFI_STATE_DISCONNECTED);
                        Log.i("connecAct","WIFI_STATE_CONNECTED 已断开");
                    }

                    Log.i("connecAct","networkInfo.getState()="+networkInfo.getState());
                    // 刷新状态显示
                    refreshWifiList();
                }

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
    public void onCreateCustomToolBar(Toolbar toolbar) {
        ImageView ivEdit=new ImageView(this);
        Toolbar.LayoutParams prams =new Toolbar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        prams.gravity= Gravity.RIGHT;
        prams.rightMargin= DensityUtil.dp2px(this,10);

        ivEdit.setImageResource(R.drawable.ic_settings_black_24dp);
        ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            }
        });
        ivEdit.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(CheckScreenActivity.this, "系统WIFI设置", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        toolbar.addView(ivEdit,prams);
        toolbar.setTitle("连接WIFI");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(wifiStateReceiver);
    }

}
