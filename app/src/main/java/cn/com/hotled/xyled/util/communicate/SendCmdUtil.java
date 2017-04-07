package cn.com.hotled.xyled.util.communicate;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.com.hotled.xyled.global.Global;
import cn.com.hotled.xyled.util.android.WifiAdmin;

import static cn.com.hotled.xyled.global.Global.PAUSE_OK;
import static cn.com.hotled.xyled.global.Global.RESET_OK;
import static cn.com.hotled.xyled.global.Global.RESUME_OK;
import static cn.com.hotled.xyled.global.Global.SOCKET_ERRO;
import static cn.com.hotled.xyled.global.Global.TEST_OK;
import static cn.com.hotled.xyled.global.Global.WIFI_ERRO;

/**
 * Created by Lam on 2017/3/17.
 */

public class SendCmdUtil {

    private Context mContext;
    private Handler mHandler;

    public SendCmdUtil(Context context, Handler handler) {
        mContext = context;
        mHandler = handler;
    }


    public enum Cmd{
        Test,Resume,Pause,Reset
    }

    public void sendCmd(final Cmd cmd){
        new Thread(new Runnable() {
            @Override
            public void run() {
                send(cmd);
            }
        }).start();
    }

    //返回控制卡所返回的信息
    private void send(Cmd cmd) {
        Socket socket = null;
        OutputStream os = null;
        WifiAdmin wifiAdmin = new WifiAdmin(mContext);
        WifiInfo wifiInfo = wifiAdmin.getWifiInfo();
        String ssid = wifiInfo.getSSID();
        String macStr = "";
        byte[] readMsg = new byte[16];
        readMsg[11] =-1;
        boolean startFlag = ssid.contains(Global.SSID_START);
        boolean endFlag = ssid.contains(Global.SSID_END);
        if (startFlag && endFlag) {
            macStr = ssid.substring(ssid.indexOf("[") + 1, ssid.indexOf("]"));
        } else {
            Message message = mHandler.obtainMessage();
            message.what = WIFI_ERRO;
            mHandler.sendMessage(message);
            return;
        }
        String regEx = "[0-9a-fA-F]{6}";
        Pattern pat = Pattern.compile(regEx);
        Matcher mat = pat.matcher(macStr);
        //旧的8位
        String regExEight = "[0-9a-fA-F]{8}";
        Pattern patEight = Pattern.compile(regExEight);
        Matcher matcEight = patEight.matcher(macStr);
        int macInt1 = 0;
        int macInt2 = 0;
        int macInt3 = 0;
        int macInt4 = 0;
        if (mat.matches()) {
            String mac0 = "80";
            String mac1 = macStr.substring(0, 2);
            String mac2 = macStr.substring(2, 4);
            String mac3 = macStr.substring(4, 6);

            macInt1 = Integer.parseInt(mac0, 16);
            macInt2 = Integer.parseInt(mac1, 16);
            macInt3 = Integer.parseInt(mac2, 16);
            macInt4 = Integer.parseInt(mac3, 16);
        } else if (matcEight.matches()) {
            String mac1 = macStr.substring(0, 2);
            String mac2 = macStr.substring(2, 4);
            String mac3 = macStr.substring(4, 6);
            String mac4 = macStr.substring(6, 8);

            macInt1 = Integer.parseInt(mac1, 16);
            macInt2 = Integer.parseInt(mac2, 16);
            macInt3 = Integer.parseInt(mac3, 16);
            macInt4 = Integer.parseInt(mac4, 16);
        } else {
            mHandler.sendEmptyMessage(WIFI_ERRO);
            return;
        }
        try {
            socket = new Socket(Global.SERVER_IP, Global.SERVER_PORT);

            byte[] pauseCMD = new byte[16];
            pauseCMD[0] = (byte) macInt1;
            pauseCMD[1] = (byte) macInt2;
            pauseCMD[2] = (byte) macInt3;
            pauseCMD[3] = (byte) macInt4;
            pauseCMD[4] = 16;
            pauseCMD[11] = 8; //cmd

            byte[] resetCMD = new byte[16];
            resetCMD[0] = (byte) macInt1;
            resetCMD[1] = (byte) macInt2;
            resetCMD[2] = (byte) macInt3;
            resetCMD[3] = (byte) macInt4;
            resetCMD[4] = 16;
            resetCMD[11] = 4; //cmd


            byte[] resumeCMD = new byte[16];
            resumeCMD[0] = (byte) macInt1;
            resumeCMD[1] = (byte) macInt2;
            resumeCMD[2] = (byte) macInt3;
            resumeCMD[3] = (byte) macInt4;
            resumeCMD[4] = 16;
            resumeCMD[11] = 12; //cmd 0x00000100

            byte[] testCMD = new byte[16];
            //8032364e 128 50 35 78
            //
            //8030ed86  128 48 237 134
            testCMD[0] = (byte) macInt1;
            testCMD[1] = (byte) macInt2;
            testCMD[2] = (byte) macInt3;
            testCMD[3] = (byte) macInt4;
            testCMD[4] = 16;
            testCMD[11] = 0; //cmd

            os = socket.getOutputStream();
            //执行测试指令

            switch (cmd){
                case Test:
                    os.write(testCMD);
                    socket.getInputStream().read(readMsg);//读取返回
                    if (readMsg[11]==0) {
                        mHandler.sendEmptyMessage(TEST_OK);
                    }
                    break;
                case Pause:
                    os.write(pauseCMD);
                    socket.getInputStream().read(readMsg);//读取返回
                    if (readMsg[11]==8) {
                        mHandler.sendEmptyMessage(PAUSE_OK);
                    }
                    break;
                case Resume:
                    os.write(resumeCMD);
                    socket.getInputStream().read(readMsg);//读取返回
                    if (readMsg[11]==12) {
                        mHandler.sendEmptyMessage(RESUME_OK);
                    }
                    break;
                case Reset:
                    os.write(resetCMD);
                    socket.getInputStream().read(readMsg);//读取返回
                    if (readMsg[11]==4) {
                        mHandler.sendEmptyMessage(RESET_OK);
                    }
                    break;
            }

        }catch (SocketException e) {
            e.printStackTrace();
            Message msg = mHandler.obtainMessage();
            msg.what=SOCKET_ERRO;
            Bundle b=new Bundle();
            b.putString("error",e.toString());
            msg.setData(b);
            mHandler.sendMessage(msg);
        } catch (IOException e) {
            e.printStackTrace();
            Message msg = mHandler.obtainMessage();
            msg.what=SOCKET_ERRO;
            Bundle b=new Bundle();
            b.putString("error",e.toString());
            msg.setData(b);
            mHandler.sendMessage(msg);
        } finally {
            if (socket != null && !socket.isClosed()) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
