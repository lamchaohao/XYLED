package cn.com.hotled.xyled.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.util.WifiAdmin;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 101;
    private static final int MY_PERMISSION_ACCESS_COARSE_LOCATION =201 ;

    private WifiAdmin mWifiAdmin;

    @BindView(R.id.bt_getNetWordId)
    Button bt_getNetWordId;
    @BindView(R.id.bt_getIpAddress)
    Button bt_getIpAddress;
    @BindView(R.id.bt_getMacAddress)
    Button bt_getMacAddress;
    @BindView(R.id.bt_getWifiInfo)
    Button bt_getWifiInfo;
    @BindView(R.id.bt_getWifiList)
    Button bt_getWifiList;
    @BindView(R.id.bt_lookUpScan)
    Button bt_lookUpScan;
    @BindView(R.id.bt_wifi_state)
    Button bt_wifi_state;
    @BindView(R.id.bt_scanWifi)
    Button bt_scanWifi;
    @BindView(R.id.tv_state)
    TextView tv_state;

    @BindView(R.id.main_bt_getWifipsw)
    Button main_bt_getWifipsw;
    @BindView(R.id.main_bt_connect)
    Button main_bt_connect;
    @BindView(R.id.main_et_wifiName)
    EditText main_et_wifiName;
    @BindView(R.id.main_et_wifiPsw)
    EditText main_et_wifiPsw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWifiAdmin=new WifiAdmin(this);
        ButterKnife.bind(this);//注解完后，要调用绑定此Activity
    }

    @OnClick(R.id.bt_getIpAddress)
    void getIpAddress(View view){ //参数view可加可不加
        int ipAddress = mWifiAdmin.getIpAddress();
        tv_state.setText(ipAddress+"");
    }

    @OnClick(R.id.bt_getNetWordId)
    void getNetWordId(){
        int netWordId = mWifiAdmin.getNetWordId();
        tv_state.setText(netWordId+"");
        startActivity(new Intent(this,SocketActivity.class));
    }

    @OnClick(R.id.bt_getMacAddress)
    void setBt_getMacAddress(){
        String macAddress = mWifiAdmin.getMacAddress();
        tv_state.setText(macAddress);
        startActivity(new Intent(this,ConnectWifiActivity.class));
    }

    @OnClick(R.id.bt_getWifiInfo)
    void setBt_getWifiInfo(){
        WifiInfo wifiInfo = mWifiAdmin.getWifiInfo();
        tv_state.setText(wifiInfo.toString());
    }

    @OnClick(R.id.bt_getWifiList)
    void setBt_getWifiList(){
        List<ScanResult> wifiList = mWifiAdmin.getWifiList();
        StringBuilder sb = new StringBuilder();
        for (int i=0;i<wifiList.size();i++){
            ScanResult scanResult = wifiList.get(i);
            sb.append("bssid:").append(scanResult.BSSID).append("ssid").append(scanResult.SSID).append("\n");
        }
        tv_state.setText(sb);
    }

    @OnClick(R.id.bt_lookUpScan)
    void lookupScan(){
        StringBuffer stringBuffer = mWifiAdmin.lookUpScan();
        tv_state.setText(stringBuffer.toString());
    }

    @OnClick(R.id.bt_wifi_state)
    void getWifiState(){

    }

    @OnClick(R.id.bt_scanWifi)
    void scanWifi() {
        checkPermission();
    }



    @OnClick(R.id.main_bt_getWifipsw)
    void getWifiPsw(){
        List<WifiConfiguration> configurations = mWifiAdmin.getConfigurations();
        StringBuilder sb=new StringBuilder();
        for (WifiConfiguration conf:configurations){
            sb.append(conf.toString());
            sb.append("BSSID-----------");
            sb.append(conf.BSSID);
            sb.append("\n");
        }
        tv_state.setText(sb.toString());
    }

    public void checkPermission(){
        // API 23: we have to check if ACCESS_FINE_LOCATION and/or ACCESS_COARSE_LOCATION permission are granted
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mWifiAdmin.startScan();
                Log.i("scan",mWifiAdmin.lookUpScan().toString());
        } else {

            // The ACCESS_COARSE_LOCATION is denied, then I request it and manage the result in
            // onRequestPermissionsResult() using the constant MY_PERMISSION_ACCESS_FINE_LOCATION
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, MY_PERMISSION_ACCESS_COARSE_LOCATION);
            }
            // The ACCESS_FINE_LOCATION is denied, then I request it and manage the result in
            // onRequestPermissionsResult() using the constant MY_PERMISSION_ACCESS_FINE_LOCATION
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, MY_PERMISSION_ACCESS_FINE_LOCATION);
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_ACCESS_COARSE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mWifiAdmin.startScan();
                    Log.i("scan",mWifiAdmin.lookUpScan().toString());
                } else {
                    // permission denied
                    Toast.makeText(this, "未授予相应权限ACCESS_COARSE_LOCATION", Toast.LENGTH_SHORT).show();
                }
                break;

            }
            case MY_PERMISSION_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mWifiAdmin.startScan();
                    Log.i("scan",mWifiAdmin.lookUpScan().toString());
                } else {
                    Toast.makeText(this, "未授予相应权限ACCESS_FINE_LOCATION", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }
}
