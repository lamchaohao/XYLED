package cn.com.hotled.xyled.util.communicate;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.com.hotled.xyled.global.Common;
import cn.com.hotled.xyled.global.Global;
import cn.com.hotled.xyled.util.android.WifiAdmin;

import static cn.com.hotled.xyled.global.Global.READ_FAILE;
import static cn.com.hotled.xyled.global.Global.READ_SUCCESS;
import static cn.com.hotled.xyled.global.Global.WIFI_ERRO;


/**
 * 此工具用于切换节目
 * Created by Lam on 2017/3/8.
 */

public class RemoteSwitchUtil {

    private Context mContext;
    private Handler mHandler;
    private int mIndex;

    public RemoteSwitchUtil(Context context, Handler handler) {
        mContext = context;
        mHandler = handler;
    }


    public void startReadData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                readData();
            }
        }).start();
    }

    public void startWriteData(int programIndex){
        mIndex = programIndex;
        new Thread(new Runnable() {
            @Override
            public void run() {
                writeData(mIndex);
            }
        }).start();
    }

    private void writeData(int programIndex){
        Socket socket = null;
        OutputStream os =null;
        WifiAdmin wifiAdmin =new WifiAdmin(mContext);
        WifiInfo wifiInfo = wifiAdmin.getWifiInfo();
        String ssid = wifiInfo.getSSID();
        String macStr = "";
        boolean startFlag = ssid.contains(Global.SSID_START);
        boolean endFlag = ssid.contains(Global.SSID_END);
        if (startFlag&&endFlag){
            macStr = ssid.substring(ssid.indexOf("[")+1, ssid.indexOf("]"));
        }else {
            Message message = mHandler.obtainMessage();
            message.what=WIFI_ERRO;
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
        if(mat.matches()){
            String mac0 = "80";
            String mac1 = macStr.substring(0, 2);
            String mac2 = macStr.substring(2, 4);
            String mac3 = macStr.substring(4, 6);

            macInt1 = Integer.parseInt(mac0, 16);
            macInt2 = Integer.parseInt(mac1, 16);
            macInt3 = Integer.parseInt(mac2, 16);
            macInt4 = Integer.parseInt(mac3, 16);
        }else if (matcEight.matches()){
            String mac1 = macStr.substring(0, 2);
            String mac2 = macStr.substring(2, 4);
            String mac3 = macStr.substring(4, 6);
            String mac4 = macStr.substring(6, 8);

            macInt1 = Integer.parseInt(mac1, 16);
            macInt2 = Integer.parseInt(mac2, 16);
            macInt3 = Integer.parseInt(mac3, 16);
            macInt4 = Integer.parseInt(mac4, 16);
        } else{
            Message message = mHandler.obtainMessage();
            message.what=WIFI_ERRO;
            mHandler.sendMessage(message);
            return;
        }
        try {
            socket = new Socket(Global.SERVER_IP, Global.SERVER_PORT);

            byte[] pauseCMD = new byte[16];
            pauseCMD[0]= (byte) macInt1;
            pauseCMD[1]= (byte) macInt2;
            pauseCMD[2]= (byte) macInt3;
            pauseCMD[3]= (byte) macInt4;
            pauseCMD[4]= 16;
            pauseCMD[11]= 8; //cmd

            byte[] resetCMD=new byte[16];
            resetCMD[0]= (byte) macInt1;
            resetCMD[1]= (byte) macInt2;
            resetCMD[2]= (byte) macInt3;
            resetCMD[3]= (byte) macInt4;
            resetCMD[4]= 16;
            resetCMD[11]= 4; //cmd

            byte[] writeCMD = new byte[16];
            writeCMD[0]= (byte) macInt1;
            writeCMD[1]= (byte) macInt2;
            writeCMD[2]= (byte) macInt3;
            writeCMD[3]= (byte) macInt4;
            writeCMD[4]= 16;
            writeCMD[11]= 21; //read cmd 0x00010001  write cmd=0x00010101;

            os = socket.getOutputStream();
            byte[] readMsg = new byte[16];

            //执行暂停指令
            os.write(pauseCMD);
            socket.getInputStream().read(readMsg);//读取返回
            boolean pauseSuccess = true;
            for (int i = 0; i < readMsg.length; i++) {
                if(pauseCMD[i]!=readMsg[i]){
                    mHandler.sendEmptyMessageDelayed(READ_FAILE,1500);
                    pauseSuccess = false;
                }
            }
            if (pauseSuccess) {
                //暂停成功，开始写入
                byte[] feedbackcmd=new byte[16];
                int index = 255 - programIndex;
                int length =512;
                int flashAddress=122880;
                byte[] sendPack = new byte[16+512];
                byte[] dataPackLength = intToByteArray(length, 3);
                byte[] serialNumBytes = intToByteArray(0, 3);
                byte[] flashAddBytes = intToByteArray(flashAddress, 4);
                setInbyteArray(5,dataPackLength,writeCMD);//包长度
                setInbyteArray(8,serialNumBytes,writeCMD); //包序
                setInbyteArray(12,flashAddBytes,writeCMD); //flash地址
                byte[] indexData = intToByteArray(index, 1);

                setInbyteArray(0,writeCMD,sendPack);
                setInbyteArray(16,indexData,sendPack);
                os.write(sendPack);
                Log.i("readProgram","写入内容"+programIndex);
                socket.getInputStream().read(feedbackcmd);
                Log.i("readProgram","回读到写指令"+programIndex);
                os.write(resetCMD);
                socket.getInputStream().read(feedbackcmd);
            }

        }catch (IOException e){
            e.printStackTrace();
            Message message = mHandler.obtainMessage();
            message.what=READ_FAILE;
            mHandler.sendMessage(message);
        }finally {
            if (os!=null){
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
           if (socket!=null&&!socket.isClosed()){
               try {
                   socket.close();
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
        }
    }

    private void readData() {
        Socket socket = null;
        OutputStream os =null;
        WifiAdmin wifiAdmin =new WifiAdmin(mContext);
        WifiInfo wifiInfo = wifiAdmin.getWifiInfo();
        String ssid = wifiInfo.getSSID();
        String macStr = "";
        boolean startFlag = ssid.contains(Global.SSID_START);
        boolean endFlag = ssid.contains(Global.SSID_END);
        if (startFlag&&endFlag){
            macStr = ssid.substring(ssid.indexOf("[")+1, ssid.indexOf("]"));
        }else {
            Message message = mHandler.obtainMessage();
            message.what=WIFI_ERRO;
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
        if(mat.matches()){
            String mac0 = "80";
            String mac1 = macStr.substring(0, 2);
            String mac2 = macStr.substring(2, 4);
            String mac3 = macStr.substring(4, 6);

            macInt1 = Integer.parseInt(mac0, 16);
            macInt2 = Integer.parseInt(mac1, 16);
            macInt3 = Integer.parseInt(mac2, 16);
            macInt4 = Integer.parseInt(mac3, 16);
        }else if (matcEight.matches()){
            String mac1 = macStr.substring(0, 2);
            String mac2 = macStr.substring(2, 4);
            String mac3 = macStr.substring(4, 6);
            String mac4 = macStr.substring(6, 8);

            macInt1 = Integer.parseInt(mac1, 16);
            macInt2 = Integer.parseInt(mac2, 16);
            macInt3 = Integer.parseInt(mac3, 16);
            macInt4 = Integer.parseInt(mac4, 16);
        } else{
            Message message = mHandler.obtainMessage();
            message.what=WIFI_ERRO;
            mHandler.sendMessage(message);
            return;
        }

        try {
            socket = new Socket(Global.SERVER_IP, Global.SERVER_PORT);
            File dataRead=new File(mContext.getFilesDir()+ Common.FL_SCREEN_DATA);
            if (!dataRead.exists()) {
                dataRead.createNewFile();
            }else {
                dataRead.delete();
            }
            os = socket.getOutputStream();

            byte[] testCMD = new byte[16];
            testCMD[0] = (byte) macInt1;
            testCMD[1] = (byte) macInt2;
            testCMD[2] = (byte) macInt3;
            testCMD[3] = (byte) macInt4;
            testCMD[4] = 16;

            byte[] pauseCMD = new byte[16];
            pauseCMD[0]= (byte) macInt1;
            pauseCMD[1]= (byte) macInt2;
            pauseCMD[2]= (byte) macInt3;
            pauseCMD[3]= (byte) macInt4;
            pauseCMD[4]= 16;
            pauseCMD[11]= 8; //cmd

            byte[] resumeCMD=new byte[16];
            resumeCMD[0]= (byte) macInt1;
            resumeCMD[1]= (byte) macInt2;
            resumeCMD[2]= (byte) macInt3;
            resumeCMD[3]= (byte) macInt4;
            resumeCMD[4]= 16;
            resumeCMD[11]= 12; //cmd 0x00000100

            byte[] readCMD = new byte[16];
            readCMD[0]= (byte) macInt1;
            readCMD[1]= (byte) macInt2;
            readCMD[2]= (byte) macInt3;
            readCMD[3]= (byte) macInt4;
            readCMD[4]= 16;
            readCMD[11]= 17; //read cmd 0x00010001  write cmd=0x00010101;


            //执行测试指令
            os.write(testCMD);
            byte[] readMsg = new byte[16];
            socket.getInputStream().read(readMsg);//读取返回


            for (int i = 0; i < readMsg.length; i++) {
                if(testCMD[i]!=readMsg[i]){
                    for (int i1 = 0; i1 < readMsg.length; i1++) {
                    }
                    mHandler.sendEmptyMessage(READ_FAILE);
                }else {

                }
            }

            //执行暂停指令
            os.write(pauseCMD);
            socket.getInputStream().read(readMsg);//读取返回
            boolean pauseSuccess = true;
            for (int i = 0; i < readMsg.length; i++) {
                if(pauseCMD[i]!=readMsg[i]){
                    mHandler.sendEmptyMessage(READ_FAILE);
                    pauseSuccess = false;
                }
            }
            if (pauseSuccess) {
                //暂停成功，开始回读
                byte[] feedbackcmd=new byte[16];
                byte[] readbackmsg=new byte[512];
                int programCount=0;//总共有多少个节目
                int readAddress = 122880;
                for (int k = 0; k < 1; k++) {
                    byte[] bytes = intToByteArray(readAddress, 4);
                    setInbyteArray(12,bytes,readCMD);
                    os.write(readCMD);
                    socket.getInputStream().read(feedbackcmd);
                    socket.getInputStream().read(readbackmsg);
                    readAddress+=512;
                    for (int i = 0; i < readbackmsg.length; i++) {
                        if (k==0){
                            //第一扇区的数据
                            if (i==0){
                                programCount = readbackmsg[0] & 0xff;//节目个数
                            }
                        }
                    }
                }
                mHandler.sendEmptyMessage(READ_SUCCESS);
                os.write(resumeCMD);
                socket.getInputStream().read(feedbackcmd);

            }

        }catch (IOException e){
            e.printStackTrace();
            Message message = mHandler.obtainMessage();
            message.what=READ_FAILE;
            mHandler.sendMessage(message);
        }finally {
            if (os!=null){
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
           if (socket!=null){
               try {
                   socket.close();
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
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
}
