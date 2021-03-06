package cn.com.hotled.xyled.adapter;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.activity.CheckScreenActivity;
import cn.com.hotled.xyled.global.Global;
import cn.com.hotled.xyled.util.android.WifiAdmin;

/**
 * Created by Lam on 2016/10/18.
 */

public class ConnectWifiAdapter extends RecyclerView.Adapter {


    CheckScreenActivity mContextAct;
    List<ScanResult> mWifiList;
    public OnItemOnClickListener mOnItemClickLitener;
    public WifiInfo wifiInfo;
    WifiAdmin mWifiAdmin;
    /**
     * 点击事件监听借口
     */
    public interface OnItemOnClickListener{
        void onItemClick(View view ,int position);
        void onItemLongClick(View view ,int position);
    }

    public ConnectWifiAdapter(CheckScreenActivity context, List<ScanResult> wifiList, WifiAdmin wifiAdmin) {
        mContextAct=context;
        mWifiList=wifiList;
        mWifiAdmin=wifiAdmin;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) mContextAct.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view =inflater.from(mContextAct).inflate(R.layout.content_wifi_list,parent,false);
        WifiAdapterViewHolder viewHolder =new WifiAdapterViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final WifiAdapterViewHolder viewHolder= (WifiAdapterViewHolder) holder;

        setUplevel(viewHolder.iv_wifiLevel,position);
        viewHolder.tv_wifiName.setText(mWifiList.get(position).SSID);
        setupWifiState(mWifiList.get(position),viewHolder.tv_wifiState);
        if (needToVisibility(mWifiList.get(position).SSID)) {
            viewHolder.iv_identifyWifi.setVisibility(View.VISIBLE);//先设置不显示是否属于新翼led的图标
        }else {
            viewHolder.iv_identifyWifi.setVisibility(View.GONE);
        }
        // 如果设置了回调，则设置点击事件
        if (mOnItemClickLitener != null) {
            viewHolder.rl_wifilist.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    int pos = viewHolder.getLayoutPosition();
                    mOnItemClickLitener.onItemClick(viewHolder.itemView, pos);
                }
            });

            viewHolder.rl_wifilist.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = viewHolder.getLayoutPosition();
                    mOnItemClickLitener.onItemLongClick(viewHolder.itemView, pos);
                    return true;
                }
            });
        }
    }

    private boolean needToVisibility(String ssid) {
        boolean startFlag = ssid.startsWith(Global.SSID_START);
        boolean endFlag = ssid.endsWith(Global.SSID_END);
        if (startFlag&&endFlag){
            return true;
        }else
            return false;
    }

    private void setupWifiState(ScanResult sc,TextView tv_wifiState) {
        String lock_str;
        if (sc.capabilities.contains("WPA2-PSK")) {
            // WPA-PSK加密
            lock_str = "通过WPA2-PSK进行保护";
        } else if (sc.capabilities.contains("WPA-PSK")) {
            // WPA-PSK加密
            lock_str = "通过WPA-PSK进行保护";
        } else if (sc.capabilities.contains("WPA-EAP")) {
            // WPA-EAP加密
            lock_str = "通过WPA-EAP进行保护";
        } else if (sc.capabilities.contains("WEP")) {
            // WEP加密
            lock_str = "通过WEP进行保护";
        } else {
            // 无密码
            lock_str = "开放网络";
        }
        tv_wifiState.setText(lock_str);

        //显示已连接的WiFi名，显示已连接
        WifiInfo wifiInfo = mWifiAdmin.getWifiInfo();
        String newSSID="\"" + sc.SSID + "\"";
        if (newSSID.equals(wifiInfo.getSSID())){
            switch (mContextAct.getWifiStatuCode()){
                default:
                    tv_wifiState.setText("已连接by default");
                case CheckScreenActivity.WIFI_STATE_CONNECTING:
                    tv_wifiState.setText("正在连接");
                    break;
                case CheckScreenActivity.WIFI_STATE_CONNECTED:
                    tv_wifiState.setText("已连接");
                    break;
                case CheckScreenActivity.WIFI_STATE_DISCONNECTING:
                    tv_wifiState.setText("正在断开");
                    break;
                case CheckScreenActivity.WIFI_STATE_DISCONNECTED:
                    tv_wifiState.setText("已断开");
                    break;
            }
        }
    }

    /**
     * 设置WiFi信号图标，是否需要密码等
     * @param iv_wifiLevel
     * @param position
     */
    private void setUplevel(ImageView iv_wifiLevel,int position) {
        ScanResult wifiInfo = mWifiList.get(position);
        if (isNeedKey(mWifiList.get(position).capabilities)){
          if (wifiInfo.level<0&&wifiInfo.level>=-50)
              iv_wifiLevel.setImageResource(R.drawable.ic_signal_wifi_4_bar_lock_cyan_600_24dp);
            else if (wifiInfo.level<-50&&wifiInfo.level>=-70)
              iv_wifiLevel.setImageResource(R.drawable.ic_signal_wifi_3_bar_lock_cyan_600_24dp);
            else if (wifiInfo.level<-70&&wifiInfo.level>=-85)
              iv_wifiLevel.setImageResource(R.drawable.ic_signal_wifi_2_bar_lock_cyan_600_24dp);
            else if (wifiInfo.level<-85&&wifiInfo.level>=-100)
              iv_wifiLevel.setImageResource(R.drawable.ic_signal_wifi_1_bar_lock_cyan_600_24dp);
        }else {
            if (wifiInfo.level<0&&wifiInfo.level>=-50)
                iv_wifiLevel.setImageResource(R.drawable.ic_signal_wifi_4_bar_cyan_600_24dp);
            else if (wifiInfo.level<-50&&wifiInfo.level>=-70)
                iv_wifiLevel.setImageResource(R.drawable.ic_signal_wifi_3_bar_cyan_600_24dp);
            else if (wifiInfo.level<-70&&wifiInfo.level>=-85)
                iv_wifiLevel.setImageResource(R.drawable.ic_signal_wifi_2_bar_cyan_600_24dp);
            else if (wifiInfo.level<-85&&wifiInfo.level>=-100)
                iv_wifiLevel.setImageResource(R.drawable.ic_signal_wifi_1_bar_cyan_600_24dp);
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


    @Override
    public int getItemCount() {
        return mWifiList.size();
    }


    public void setItemOnClickListener(OnItemOnClickListener itemOnClickListener){
        this.mOnItemClickLitener=itemOnClickListener;
    }

    class WifiAdapterViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_wifiLevel;
        TextView tv_wifiName;
        TextView tv_wifiState;
        ImageView iv_identifyWifi;
        View rl_wifilist;

        public WifiAdapterViewHolder(View itemView) {
            super(itemView);
            rl_wifilist = itemView.findViewById(R.id.rl_wifilist);
            iv_wifiLevel = (ImageView) itemView.findViewById(R.id.iv_cwl_wifiLevel);
            tv_wifiName = (TextView) itemView.findViewById(R.id.tv_cwl_wifiName);
            iv_identifyWifi = (ImageView) itemView.findViewById(R.id.iv_cwl_identifyWifi);
            tv_wifiState = (TextView) itemView.findViewById(R.id.tv_cwl_wifiState);
        }
    }
}

