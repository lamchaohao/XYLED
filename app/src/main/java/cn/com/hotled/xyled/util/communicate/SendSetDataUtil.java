package cn.com.hotled.xyled.util.communicate;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.os.Handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.com.hotled.xyled.bean.TraceFile;
import cn.com.hotled.xyled.global.Common;
import cn.com.hotled.xyled.global.Global;
import cn.com.hotled.xyled.util.android.WifiAdmin;

import static cn.com.hotled.xyled.global.Global.CONNECT_TIMEOUT;
import static cn.com.hotled.xyled.global.Global.PAUSE_FAILE;
import static cn.com.hotled.xyled.global.Global.SEND_DONE;
import static cn.com.hotled.xyled.global.Global.WIFI_ERRO;
import static cn.com.hotled.xyled.util.genFile.ByteUtil.intToByteArray;
import static cn.com.hotled.xyled.util.genFile.ByteUtil.setInbyteArray;

/**
 * 发送设屏参设置
 * Created by Lam on 2017/3/13.
 */

public class SendSetDataUtil {

    private Context mContext;
    private TraceFile mTraceFile;
    private Handler mHandler;

    public SendSetDataUtil(Context context, TraceFile file, Handler handler) {
        mContext=context;
        mTraceFile =file;
        mHandler = handler;
    }

    public void startSendData(){
        new Thread(){
            @Override
            public void run() {
                sendSetData();
            }
        }.start();
    }

    public void startSendPcConfig(){
        new Thread(){
            @Override
            public void run() {
                sendPcConfigData();
            }
        }.start();
    }

    private void sendSetData(){
        Socket socket = null;
        OutputStream os =null;
        FileInputStream fis = null;
        WifiAdmin wifiAdmin =new WifiAdmin(mContext);
        WifiInfo wifiInfo = wifiAdmin.getWifiInfo();
        String ssid = wifiInfo.getSSID();
        String macStr = "";
        boolean startFlag = ssid.contains(Global.SSID_START);
        boolean endFlag = ssid.contains(Global.SSID_END);
        if (startFlag&&endFlag){
            macStr = ssid.substring(ssid.indexOf("[")+1, ssid.indexOf("]"));
        }else {
            mHandler.sendEmptyMessage(WIFI_ERRO);
            return;
        }
        String regEx = "[0-9a-fA-F]{6}";
        Pattern pat = Pattern.compile(regEx);
        Matcher mat = pat.matcher(macStr);
        //旧的8位
        String regExEight = "[0-9a-fA-F]{8}";
        Pattern patEight = Pattern.compile(regExEight);
        Matcher matcEight = patEight.matcher(macStr);
        String mac1;
        String mac2;
        String mac3;
        String mac4;
        if(mat.matches()){
            mac1 = "80";
            mac2 = macStr.substring(0, 2);
            mac3 = macStr.substring(2, 4);
            mac4 = macStr.substring(4, 6);

        }else if (matcEight.matches()){
            mac1 = macStr.substring(0, 2);
            mac2 = macStr.substring(2, 4);
            mac3 = macStr.substring(4, 6);
            mac4 = macStr.substring(6, 8);
        } else{
            mHandler.sendEmptyMessage(WIFI_ERRO);
            return;
        }
        int macInt1 = Integer.parseInt(mac1, 16);
        int macInt2 = Integer.parseInt(mac2, 16);
        int macInt3 = Integer.parseInt(mac3, 16);
        int macInt4 = Integer.parseInt(mac4, 16);
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
                    mHandler.sendEmptyMessageDelayed(PAUSE_FAILE,1500);
                    pauseSuccess = false;
                }
            }
            if (pauseSuccess) {
                //暂停成功，开始写入
                byte[] feedbackcmd=new byte[16];
                int length =512;
                int serialNum = 4;
                int flashAddress=129024;
                File filePath = mTraceFile.getFilePath();
                File traceFile=new File(mContext.getFilesDir()+ Common.FL_TRACE_DIR,filePath.getAbsolutePath());
                fis= new FileInputStream(traceFile);

                byte[] traceFileBytes=new byte[512];
                int read = fis.read(traceFileBytes);

                for (int i = 0; i < 4; i++) {
                    serialNum--;
                    byte[] sendPack = new byte[16+512];
                    byte[] dataPackLength = intToByteArray(length, 3);
                    byte[] serialNumBytes = intToByteArray(serialNum, 3);
                    byte[] flashAddBytes = intToByteArray(flashAddress, 4);
                    setInbyteArray(5,dataPackLength,writeCMD);//包长度
                    setInbyteArray(8,serialNumBytes,writeCMD); //包序
                    setInbyteArray(12,flashAddBytes,writeCMD); //flash地址
                    byte[] dataBytes = null;
                    if (i==0){
                        dataBytes=genData();//写入第一区设置内容
                    }else if (i==1){
                        dataBytes=traceFileBytes;//写入走线记录表
                    }else {
                        dataBytes = new byte[512];
                    }
                    setInbyteArray(0,writeCMD,sendPack);
                    setInbyteArray(16,dataBytes,sendPack);
                    os.write(sendPack);
                    socket.getInputStream().read(feedbackcmd);
                    flashAddress += length;

                }
                os.write(resetCMD);
                socket.getInputStream().read(feedbackcmd);
                mHandler.sendEmptyMessage(SEND_DONE);
            }

        }catch (IOException e){
            e.printStackTrace();
            mHandler.sendEmptyMessage(CONNECT_TIMEOUT);
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
            if (fis!=null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public void testPrintData(){
        FileInputStream fis = null;
        FileOutputStream fos =null;
        File traceFile=new File(mContext.getFilesDir()+ Common.FL_TRACE_DIR,mTraceFile.getFilePath().getAbsolutePath());
        try {
            fis= new FileInputStream(traceFile);
            fos =new FileOutputStream(mContext.getFilesDir()+"/setting.prg",true);
            byte[] traceFileBytes=new byte[512];
            fis.read(traceFileBytes);
            for (int i = 0; i < 4; i++) {
                byte[] dataBytes = null;
                if (i==0){
                    dataBytes=genData();//写入第一区设置内容
                    fos.write(dataBytes);
                }else if (i==1){
                    dataBytes=traceFileBytes;//写入走线记录表
                    fos.write(dataBytes);
                }else {
                    dataBytes = new byte[512];
                    fos.write(dataBytes);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (fis!=null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos!=null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }


    private byte[] genData(){
        //取出设置
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Global.SP_SCREEN_CONFIG, Context.MODE_PRIVATE);
        int width = sharedPreferences.getInt(Global.KEY_SCREEN_W, 64);
        int height = sharedPreferences.getInt(Global.KEY_SCREEN_H, 64);
        int rgbOrder = sharedPreferences.getInt(Global.KEY_RGB_ORDER, 0);
        int data = sharedPreferences.getInt(Global.KEY_DATA, 0);
        int oe = sharedPreferences.getInt(Global.KEY_OE, 0);
        int code = sharedPreferences.getInt(Global.KEY_138CODE, 0);
        int dataOrient = sharedPreferences.getInt(Global.KEY_DATA_ORIENTATION, 0);
        int special = sharedPreferences.getInt(Global.KEY_SPECIAL, 0);
        byte[] dataBytes =new byte[512];


        int picture =width*height;
        int foldCount = mTraceFile.getFoldCount();
        int scanCount = mTraceFile.getScanCount();
        int line = width*foldCount;
        int output = height/(scanCount*foldCount);
        byte[] scanOrderArray=new byte[16];
        for (int i = 0; i < scanOrderArray.length; i++) {
            scanOrderArray[i]= (byte) i;
        }
        int route = mTraceFile.getDotCount();
        int batH = scanCount*foldCount;
        int batW = mTraceFile.getModuleWidth();

        String fileName = Global.HC1_FILENAME;
        byte[] fileBytes = fileName.getBytes();     //0-11 文件名
        setInbyteArray(0,fileBytes,dataBytes);
        dataBytes[19] = 97; //19 版本号
        dataBytes[24] = (byte) 195; //24

        byte[] pictureArray = intToByteArray(picture, 3);//实像素  32-34
        setInbyteArray(32,pictureArray,dataBytes);
        byte[] widthArray = intToByteArray(width, 2); //35-36 width
        setInbyteArray(35,widthArray,dataBytes);
        dataBytes[37]= (byte) height;  //37 height
        if (code==1){
            scanCount+=128; //无138 最高位为1
        }
        dataBytes[38]= (byte) scanCount; //38 扫描次数
        byte[] lineArray = intToByteArray(line, 2); //39-40 线带点数
        setInbyteArray(39,lineArray,dataBytes);
        dataBytes[41] = (byte) output; //41 输出端口
        dataBytes[42] = (byte) data; //data相位
        dataBytes[43] = (byte) oe;  //oe
        setInbyteArray(48,scanOrderArray,dataBytes); //48-63扫行次序
        byte[] routeArray = intToByteArray(route, 2); //64-65 走线表点数
        setInbyteArray(64,routeArray,dataBytes);
        dataBytes[66] = (byte) batH; // 66 1个端口带高度
        dataBytes[67] = (byte) batW; // 67 一个模组宽度

        //----software part-----
        dataBytes[496] = (byte) special;
        dataBytes[497] = (byte) dataOrient;
        dataBytes[498] = (byte) rgbOrder;
        dataBytes[499] = (byte) mTraceFile.getModuleHeight();
        dataBytes[500] = (byte) mTraceFile.getModuleWidth();
        dataBytes[501] = (byte) mTraceFile.getRGBCount();

        return dataBytes;
    }


    private void sendPcConfigData(){
        Socket socket = null;
        OutputStream os =null;
        FileInputStream fis = null;
        WifiAdmin wifiAdmin =new WifiAdmin(mContext);
        WifiInfo wifiInfo = wifiAdmin.getWifiInfo();
        String ssid = wifiInfo.getSSID();
        String macStr = "";
        boolean startFlag = ssid.contains(Global.SSID_START);
        boolean endFlag = ssid.contains(Global.SSID_END);
        if (startFlag&&endFlag){
            macStr = ssid.substring(ssid.indexOf("[")+1, ssid.indexOf("]"));
        }else {
            mHandler.sendEmptyMessage(WIFI_ERRO);
            return;
        }
        String regEx = "[0-9a-fA-F]{6}";
        Pattern pat = Pattern.compile(regEx);
        Matcher mat = pat.matcher(macStr);
        //旧的8位
        String regExEight = "[0-9a-fA-F]{8}";
        Pattern patEight = Pattern.compile(regExEight);
        Matcher matcEight = patEight.matcher(macStr);
        String mac1;
        String mac2;
        String mac3;
        String mac4;
        if(mat.matches()){
            mac1 = "80";
            mac2 = macStr.substring(0, 2);
            mac3 = macStr.substring(2, 4);
            mac4 = macStr.substring(4, 6);

        }else if (matcEight.matches()){
            mac1 = macStr.substring(0, 2);
            mac2 = macStr.substring(2, 4);
            mac3 = macStr.substring(4, 6);
            mac4 = macStr.substring(6, 8);
        } else{
            mHandler.sendEmptyMessage(WIFI_ERRO);
            return;
        }
        int macInt1 = Integer.parseInt(mac1, 16);
        int macInt2 = Integer.parseInt(mac2, 16);
        int macInt3 = Integer.parseInt(mac3, 16);
        int macInt4 = Integer.parseInt(mac4, 16);
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
                    mHandler.sendEmptyMessageDelayed(PAUSE_FAILE,1500);
                    pauseSuccess = false;
                }
            }
            if (pauseSuccess) {
                //暂停成功，开始写入
                byte[] feedbackcmd=new byte[16];
                int length =512;
                int serialNum = 4;
                int flashAddress=129024;
                File configData=new File(mContext.getFilesDir() + Common.FL_CONFIG_FROM_PC);
                fis= new FileInputStream(configData);

                byte[] configByte=new byte[512];

                for (int i = 0; i < 4; i++) {
                    serialNum--;
                    byte[] sendPack = new byte[16+512];
                    byte[] dataPackLength = intToByteArray(length, 3);
                    byte[] serialNumBytes = intToByteArray(serialNum, 3);
                    byte[] flashAddBytes = intToByteArray(flashAddress, 4);
                    setInbyteArray(5,dataPackLength,writeCMD);//包长度
                    setInbyteArray(8,serialNumBytes,writeCMD); //包序
                    setInbyteArray(12,flashAddBytes,writeCMD); //flash地址
                    fis.read(configByte);
                    setInbyteArray(0,writeCMD,sendPack);
                    setInbyteArray(16,configByte,sendPack);
                    os.write(sendPack);
                    socket.getInputStream().read(feedbackcmd);
                    flashAddress += length;

                }
                os.write(resetCMD);
                socket.getInputStream().read(feedbackcmd);
                mHandler.sendEmptyMessageDelayed(SEND_DONE,2000);//发送完参数后等待重启,然后在发送重要文件
            }

        }catch (IOException e){
            e.printStackTrace();
            mHandler.sendEmptyMessage(CONNECT_TIMEOUT);
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
            if (fis!=null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }



}
