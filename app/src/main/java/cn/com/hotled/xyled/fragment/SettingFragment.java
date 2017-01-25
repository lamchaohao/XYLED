package cn.com.hotled.xyled.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.io.File;

import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.activity.SocketActivity;
import cn.com.hotled.xyled.util.SendTest;

/**
 * Created by Lam on 2016/12/1.
 */

public class SettingFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        view.findViewById(R.id.ll_setting_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendFile();
            }
        });
        view.findViewById(R.id.ll_setting_socket).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), SocketActivity.class));
            }
        });

        View ll_enterWifi = view.findViewById(R.id.ll_setting_enterWifi);
        ll_enterWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            }
        });
        view.findViewById(R.id.ll_setting_language).setOnClickListener(new View.OnClickListener() {
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
    private void sendFile() {
        final File file = new File(getContext().getFilesDir()+"/color.prg");
        View view = LayoutInflater.from(getContext()).inflate(R.layout.tcp_send, null);
        final EditText et_tcpIp = (EditText) view.findViewById(R.id.et_tcpIp);
        final EditText et_tcpPort = (EditText) view.findViewById(R.id.et_tcpPort);
        et_tcpIp.setText("192.168.3.1");
        et_tcpPort.setText("16389");
        new AlertDialog.Builder(getContext())
                .setTitle("设置服务器IP与端口")
                .setView(view)
                .setPositiveButton("发送文件", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String tcpIP = et_tcpIp.getText().toString();
                        String tcpPort = et_tcpPort.getText().toString();
                        SendTest tcpSend=new SendTest(getContext(),tcpIP,Integer.parseInt(tcpPort),file);
                        tcpSend.send();
                        dialog.dismiss();
//                        getActivity().startService(new Intent(getActivity(), SendDataService.class));
                    }
                })
                .setNegativeButton("cancle",null)
                .show();


    }


}
