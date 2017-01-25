package cn.com.hotled.xyled.activity;

import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.util.WifiAdmin;

public class ReadActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        initView();
    }

    private void initView() {
//        findViewById(R.id.bt_readback).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        readData();
//                    }
//                }).start();
//
//            }
//        });
    }

    private void readData() {
        Socket socket = null;
        FileInputStream fis = null;
        OutputStream os =null;
        WifiAdmin wifiAdmin =new WifiAdmin(this);
        WifiInfo wifiInfo = wifiAdmin.getWifiInfo();
        String ssid = wifiInfo.getSSID();
        String macStr = "";
        if (ssid.contains("HC-LED")){
            Log.w("tcpSend","ssid = "+ssid);
            macStr = ssid.substring(ssid.indexOf("[")+1, ssid.indexOf("]"));
        }else {
//            Message message = mHandler.obtainMessage();
//            message.what=WIFI_ERRO;
//            mHandler.sendMessage(message);
            return;
        }
        String mac1 = macStr.substring(0, 2);
        String mac2 = macStr.substring(2, 4);
        String mac3 = macStr.substring(4, 6);
        String mac4 = macStr.substring(6, 8);

        int macInt1 = Integer.parseInt(mac1, 16);
        int macInt2 = Integer.parseInt(mac2, 16);
        int macInt3 = Integer.parseInt(mac3, 16);
        int macInt4 = Integer.parseInt(mac4, 16);
        Log.w("tcpSend","mac = "+macInt1+":"+macInt2+":"+macInt3+":"+macInt4);
        try {
            socket = new Socket("192.168.3.1", 16389);
//            socket.setSoTimeout(3000);
//            fis = new FileInputStream(mFile);
            os = socket.getOutputStream();
            byte[] buf = new byte[512];
            int len = 0;
            float progress = 0;
//            long fileLength = mFile.length();

            byte[] testCMD = new byte[16];
            //8032364e 128 50 35 78
            //
            //8030ed86  128 48 237 134
            testCMD[0] = (byte) macInt1;
            testCMD[1] = (byte) macInt2;
            testCMD[2] = (byte) macInt3;
            testCMD[3] = (byte) macInt4;
            testCMD[4] = 16;
            testCMD[5] = 0;//Data Length
            testCMD[6] = 0;//Data Length
            testCMD[7] = 0;//Data Length
            testCMD[8] = 0;//包序Serial Number
            testCMD[9] = 0;//包序Serial Number
            testCMD[10] = 0;//包序Serial Number
            testCMD[11] = 0; //cmd
            testCMD[12] = 0;
            testCMD[13] = 0;
            testCMD[14] = 0;
            testCMD[15] = 0;

            byte[] pauseCMD = new byte[16];
            pauseCMD[0]= (byte) macInt1;
            pauseCMD[1]= (byte) macInt2;
            pauseCMD[2]= (byte) macInt3;
            pauseCMD[3]= (byte) macInt4;
            pauseCMD[4]= 16;
            pauseCMD[5]= 0;//Data Length
            pauseCMD[6]= 0;//Data Length
            pauseCMD[7]= 0;//Data Length
            pauseCMD[8]= 0;//包序Serial Number
            pauseCMD[9]= 0;//包序Serial Number
            pauseCMD[10]= 0;//包序Serial Number
            pauseCMD[11]= 8; //cmd
            pauseCMD[12]= 0;
            pauseCMD[13]= 0;
            pauseCMD[14]= 0;
            pauseCMD[15]= 0;

            byte[] resumeCMD=new byte[16];
            resumeCMD[0]= (byte) macInt1;
            resumeCMD[1]= (byte) macInt2;
            resumeCMD[2]= (byte) macInt3;
            resumeCMD[3]= (byte) macInt4;
            resumeCMD[4]= 16;
            resumeCMD[5]= 0;//Data Length
            resumeCMD[6]= 0;//Data Length
            resumeCMD[7]= 0;//Data Length
            resumeCMD[8]= 0;//包序Serial Number
            resumeCMD[9]= 0;//包序Serial Number
            resumeCMD[10]= 0;//包序Serial Number
            resumeCMD[11]= 12; //cmd 0x00000100
            resumeCMD[12]= 0;
            resumeCMD[13]= 0;
            resumeCMD[14]= 0;
            resumeCMD[15]= 0;

            byte[] readCMD = new byte[16];
            readCMD[0]= (byte) macInt1;
            readCMD[1]= (byte) macInt2;
            readCMD[2]= (byte) macInt3;
            readCMD[3]= (byte) macInt4;
            readCMD[4]= 16;
            readCMD[5]= 0;//Data Length
            readCMD[6]= 0;//Data Length
            readCMD[7]= 0;//Data Length
            readCMD[8]= 0;//包序Serial Number
            readCMD[9]= 0;//包序Serial Number
            readCMD[10]= 0;//包序Serial Number
            readCMD[11]= 17; //cmd
            readCMD[12]= 0;//flash address
            readCMD[13]= 0;//flash address
            readCMD[14]= 0;//flash address
            readCMD[15]= 0;//flash address
            byte[] bytes = intToByteArray(504, 4);
            setInbyteArray(12,bytes,readCMD);

            //执行测试指令
            os.write(testCMD);
            Log.w("tcpSend","发送测试指令");
            byte[] readMsg = new byte[16];
            socket.getInputStream().read(readMsg);//读取返回


            for (int i = 0; i < readMsg.length; i++) {
                if(testCMD[i]!=readMsg[i]){
                    Log.w("tcpSend","握手不成功 readMsg.equals(testCMD) false");
                }else {

                }
            }

            //执行暂停指令
            os.write(pauseCMD);
            socket.getInputStream().read(readMsg);//读取返回
            boolean pauseSuccess = true;
            for (int i = 0; i < readMsg.length; i++) {
                if(pauseCMD[i]!=readMsg[i]){
                    Log.d("tcpSend","暂停不成功 pauseCMD[i]!=readMsg[i]");
                    pauseSuccess = false;
                }
            }
            if (pauseSuccess) {
                //暂停成功，开始回读
                byte[] feedbackcmd=new byte[16];
                byte[] readbackmsg=new byte[512];
                os.write(readCMD);
                socket.getInputStream().read(feedbackcmd);
                socket.getInputStream().read(readbackmsg);
                os.write(resumeCMD);
                Log.d("tcpSend","readbackmsg-----start-------");
                for (int i = 0; i < readbackmsg.length; i++) {
                    Log.d("readAct","readbackmsg[i]= "+readbackmsg[i]+", i = "+i);
                }
                Log.d("tcpSend","readbackmsg-----end--------");
            }

        }catch (IOException e){
            e.printStackTrace();
        }
    }


    /**
     *
     * @param source 源数值
     * @param byteArrayLength 要转变成的byte数组长度
     * @return
     */
    private byte[] intToByteArray(int source,int byteArrayLength){
        byte[] result = new byte[byteArrayLength];
        for (int length = byteArrayLength,index=0; length > 0; length--,index++) {
            int bitCount = (length-1) * 8;
            int temp=source;
            temp = temp >> bitCount; //移位
            result[index] = (byte) (temp & 0xff);
        }
        return result;
    }

    /**
     *
     * @param targetStart 要赋值的目标数组的开始序列,从0开始
     * @param source 源数组
     * @param target 目标数组
     */
    private void setInbyteArray(int targetStart,byte[] source,byte[] target){
        for (int i = 0;i<source.length;i++){
            target[targetStart+i]=source[i];
        }
    }

    @Override
    public void onCreateCustomToolBar(Toolbar toolbar) {
        toolbar.setTitle("我的卡包");
    }
}
