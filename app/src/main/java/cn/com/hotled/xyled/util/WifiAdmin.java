package cn.com.hotled.xyled.util;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Lam on 2016/10/13.
 */

public class WifiAdmin {

    //定义一个WifiManager对象
    private WifiManager mWifiManager;
    //定义一个WifiInfo对象
    private WifiInfo mWifiInfo;
    //扫描出的网络连接列表
    private List<ScanResult> mWifiList;
    //网络连接列表
    private List<WifiConfiguration> mWifiConfigurations;
    private Context mContext ;
    WifiManager.WifiLock mWifiLock;

    /** 定义几种加密方式，一种是WEP，一种是WPA/WPA2，还有没有密码的情况 */
    public enum WifiCipherType {
        WIFI_CIPHER_WEP, WIFI_CIPHER_WPA_EAP, WIFI_CIPHER_WPA_PSK, WIFI_CIPHER_WPA2_PSK, WIFI_CIPHER_NOPASS
    }

    public WifiAdmin(Context act){
        this.mContext=act;
        //取得WifiManager对象
        mWifiManager=(WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        //取得WifiInfo对象
        mWifiInfo=mWifiManager.getConnectionInfo();

    }

    /**
     * 检测wifi状态 opened return true;
     */
    public boolean checkWifiState() {
        boolean isOpen = true;
        int wifiState = mWifiManager.getWifiState();

        if (wifiState == WifiManager.WIFI_STATE_DISABLED
                || wifiState == WifiManager.WIFI_STATE_DISABLING
                || wifiState == WifiManager.WIFI_STATE_UNKNOWN
                || wifiState == WifiManager.WIFI_STATE_ENABLING) {
            isOpen = false;
        }

        return isOpen;
    }

    //打开wifi
    public void openWifi(){
        if(!mWifiManager.isWifiEnabled()){
            mWifiManager.setWifiEnabled(true);
        }
    }

    //关闭wifi
    public void closeWifi(){
        if(!mWifiManager.isWifiEnabled()){
            mWifiManager.setWifiEnabled(false);
        }
    }

    //锁定wifiLock
    public void acquireWifiLock(){
        mWifiLock.acquire();
    }

    //解锁wifiLock
    public void releaseWifiLock(){
        //判断是否锁定
        if(mWifiLock.isHeld()){
            mWifiLock.acquire();
        }
    }

    //得到配置好的网络
    public List<WifiConfiguration> getConfigurations(){
        return mWifiConfigurations;
    }

    //指定配置好的网络进行连接
    public boolean connetionConfiguration(int index){
        if(index>mWifiConfigurations.size()){
            return false;
        }
        //连接配置好指定ID的网络
       return mWifiManager.enableNetwork(mWifiConfigurations.get(index).networkId, true);
    }

    /**
     * 扫描WiFi热点
     */
    public List<ScanResult> startScan(){
        mWifiManager.startScan();
        //得到扫描结果
        mWifiList=mWifiManager.getScanResults();
        //得到配置好的网络连接
        mWifiConfigurations=mWifiManager.getConfiguredNetworks();
        return mWifiList;
    }

    //得到扫描结果列表
    public List<ScanResult> getWifiList(){
        return mWifiList;
    }

    //查看扫描结果
    public StringBuffer lookUpScan(){
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<mWifiList.size();i++){
            sb.append("Index_" + new Integer(i + 1).toString() + ":");
            // 将ScanResult信息转换成一个字符串包
            // 其中把包括：BSSID、SSID、capabilities、frequency、level
            sb.append((mWifiList.get(i)).toString()).append("\n");
        }
        return sb;
    }

    public String getMacAddress(){
        return (mWifiInfo==null)?"NULL":mWifiInfo.getMacAddress();
    }

    public String getBSSID(){
        return (mWifiInfo==null)?"NULL":mWifiInfo.getBSSID();
    }

    public int getIpAddress(){
        return (mWifiInfo==null)?0:mWifiInfo.getIpAddress();
    }

    //得到连接的ID
    public int getNetWordId(){
        return (mWifiInfo==null)?0:mWifiInfo.getNetworkId();
    }

    //得到wifiInfo的所有信息
    public WifiInfo getWifiInfo(){
        return mWifiManager.getConnectionInfo();
    }


    /** 查看以前是否也配置过这个网络 */
    public WifiConfiguration isExsits(String SSID) {
        List<WifiConfiguration> existingConfigs = mWifiManager
                .getConfiguredNetworks();
        for (WifiConfiguration existConf : existingConfigs) {
            if (existConf.SSID!=null){
                if (existConf.SSID.equals("\"" + SSID + "\"")) {
                    return existConf;
                }
            }

        }
        return null;
    }



    public int createWifiInfo2(ScanResult wifiinfo, String pwd) {
        WifiCipherType type;

        if (wifiinfo.capabilities.contains("WPA2-PSK")) {
            // WPA2-PSK加密
            type = WifiCipherType.WIFI_CIPHER_WPA2_PSK;
        } else if (wifiinfo.capabilities.contains("WPA-PSK")) {
            // WPA-PSK加密
            type = WifiCipherType.WIFI_CIPHER_WPA_PSK;
        } else if (wifiinfo.capabilities.contains("WPA-EAP")) {
            // WPA-EAP加密
            type = WifiCipherType.WIFI_CIPHER_WPA_EAP;
        } else if (wifiinfo.capabilities.contains("WEP")) {
            // WEP加密
            type = WifiCipherType.WIFI_CIPHER_WEP;
        } else {
            // 无密码
            type = WifiCipherType.WIFI_CIPHER_NOPASS;
        }

        WifiConfiguration config = createWifiInfo(wifiinfo.SSID,
                wifiinfo.BSSID, pwd, type);
        if (config != null) {
            return mWifiManager.addNetwork(config);
        } else {
            return -1;
        }
    }

    /**
     * 获取最大优先级
     * @return
     */
    private int getMaxPriority() {
        List<WifiConfiguration> localList = this.mWifiManager
                .getConfiguredNetworks();
        int i = 0;
        Iterator<WifiConfiguration> localIterator = localList.iterator();
        while (true) {
            if (!localIterator.hasNext())
                return i;
            WifiConfiguration localWifiConfiguration = (WifiConfiguration) localIterator
                    .next();
            if (localWifiConfiguration.priority <= i)
                continue;
            i = localWifiConfiguration.priority;
        }
    }

    /**
     * 设置最大优先级
     * @param config
     * @return
     */
    public WifiConfiguration setMaxPriority(WifiConfiguration config) {
        int priority = getMaxPriority() + 1;
        if (priority > 99999) {
            priority = shiftPriorityAndSave();
        }

        config.priority = priority; // 2147483647;
        System.out.println("priority=" + priority);

        mWifiManager.updateNetwork(config);

        // 本机之前配置过此wifi热点，直接返回
        return config;
    }

    /**
     * 配置一个连接
     */
    public WifiConfiguration createWifiInfo(String SSID, String BSSID,
                                            String password, WifiCipherType type) {

        int priority;

        WifiConfiguration config = this.isExsits(SSID);
        if (config != null) {
            // 本机之前配置过此wifi热点，调整优先级后，直接返回
            return setMaxPriority(config);
        }

        config = new WifiConfiguration();
		/* 清除之前的连接信息 */
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";
        config.status = WifiConfiguration.Status.ENABLED;
        priority = getMaxPriority() + 1;
        if (priority > 99999) {
            priority = shiftPriorityAndSave();
        }

        config.priority = priority; // 2147483647;
		/* 各种加密方式判断 */
        if (type == WifiCipherType.WIFI_CIPHER_NOPASS) {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        } else if (type == WifiCipherType.WIFI_CIPHER_WEP) {
            config.preSharedKey = "\"" + password + "\"";

            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        } else if (type == WifiCipherType.WIFI_CIPHER_WPA_EAP) {

            config.preSharedKey = "\"" + password + "\"";
            config.hiddenSSID = true;
            config.status = WifiConfiguration.Status.ENABLED;
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);

            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.CCMP);
            config.allowedProtocols.set(WifiConfiguration.Protocol.RSN
                    | WifiConfiguration.Protocol.WPA);

        } else if (type == WifiCipherType.WIFI_CIPHER_WPA_PSK) {


            config.preSharedKey = "\"" + password + "\"";
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);

            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.CCMP);
            config.allowedProtocols.set(WifiConfiguration.Protocol.RSN
                    | WifiConfiguration.Protocol.WPA);

        } else if (type == WifiCipherType.WIFI_CIPHER_WPA2_PSK) {

            config.preSharedKey = "\"" + password + "\"";
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);

            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);

            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.CCMP);
            config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);

        } else {
            return null;
        }

        return config;
    }
    private int shiftPriorityAndSave() {
        List<WifiConfiguration> localList = this.mWifiManager
                .getConfiguredNetworks();
        sortByPriority(localList);
        int i = localList.size();
        for (int j = 0;; ++j) {
            if (j >= i) {
                this.mWifiManager.saveConfiguration();
                return i;
            }
            WifiConfiguration localWifiConfiguration = (WifiConfiguration) localList
                    .get(j);
            localWifiConfiguration.priority = j;
            this.mWifiManager.updateNetwork(localWifiConfiguration);
        }
    }

    private void sortByPriority(List<WifiConfiguration> paramList) {
        Collections.sort(paramList, new WifiManagerCompare());
    }

    class WifiManagerCompare implements Comparator<WifiConfiguration> {
        public int compare(WifiConfiguration paramWifiConfiguration1,
                           WifiConfiguration paramWifiConfiguration2) {
            return paramWifiConfiguration1.priority
                    - paramWifiConfiguration2.priority;
        }
    }
    //添加一个网络并连接
    public boolean addNetWork(WifiConfiguration configuration){
        int wcgId=mWifiManager.addNetwork(configuration);
        boolean b = mWifiManager.enableNetwork(wcgId, true);
        return b;
    }

    //连接到指定的WiFi
    public boolean connectWifi(int netWorkId){
        boolean b = mWifiManager.enableNetwork(netWorkId, true);
        return b;
    }
    //断开指定ID的网络
    public void disConnectionWifi(int netId){
        mWifiManager.disableNetwork(netId);
        mWifiManager.disconnect();
    }
}
