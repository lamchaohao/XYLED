package cn.com.hotled.xyled.util.communicate;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.com.hotled.xyled.global.Global;
import cn.com.hotled.xyled.util.android.WifiAdmin;

import static cn.com.hotled.xyled.global.Global.CONNECT_NORESPONE;
import static cn.com.hotled.xyled.global.Global.CONNECT_TIMEOUT;
import static cn.com.hotled.xyled.global.Global.UPDATE_PROGRESS;
import static cn.com.hotled.xyled.global.Global.WIFI_ERRO;
import static cn.com.hotled.xyled.util.genFile.ByteUtil.intToByteArray;
import static cn.com.hotled.xyled.util.genFile.ByteUtil.setInbyteArray;

/**
 * 发送录制文件
 * Created by Lam on 2017/3/17.
 */

public class SendDataUtil {

    private Context mContext;
    private Handler mHandler;
    private File mFile;

    public SendDataUtil(Context context, Handler handler,File fileTobeSend) {
        mContext = context;
        mHandler = handler;
        mFile = fileTobeSend;
    }

    public void send(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                sendFile(mFile);
            }
        }.start();
    }

    public void setFile(File file) {
        mFile = file;
    }

    private void sendFile(File fileTobeSend){
        Socket socket = null;
        FileInputStream fis = null;
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
            Message message = mHandler.obtainMessage();
            message.what=WIFI_ERRO;
            mHandler.sendMessage(message);
            return;
        }
        int macInt1 = Integer.parseInt(mac1, 16);
        int macInt2 = Integer.parseInt(mac2, 16);
        int macInt3 = Integer.parseInt(mac3, 16);
        int macInt4 = Integer.parseInt(mac4, 16);
        try {
            socket = new Socket(Global.SERVER_IP,Global.SERVER_PORT);
            socket.setSoTimeout(30000);

            fis = new FileInputStream(fileTobeSend);
            os = socket.getOutputStream();
            byte[] buf = new byte[512];
            int len = 0 ;
            float progress=0;
            long fileLength = fileTobeSend.length();

            byte[] testCMD=new byte[16];
            testCMD[0]= (byte) macInt1;
            testCMD[1]= (byte) macInt2;
            testCMD[2]= (byte) macInt3;
            testCMD[3]= (byte) macInt4;
            testCMD[4]= 16;

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

            //执行测试指令
            os.write(testCMD);
            byte[] readMsg = new byte[16];
            socket.getInputStream().read(readMsg);//读取返回

            for (int i = 0; i < readMsg.length; i++) {
                if(testCMD[i]!=readMsg[i]){
                }else {

                }
            }

            //执行暂停指令
            os.write(pauseCMD);
            socket.getInputStream().read(readMsg);//读取返回

            boolean pauseSuccess = true;
            for (int i = 0; i < readMsg.length; i++) {
                if(pauseCMD[i]!=readMsg[i]){
                    pauseSuccess = false;
                }
            }
            if (pauseSuccess) {
                //暂停了,开始写数据
                int serialNum = (int) (fileTobeSend.length()/512); //包序是从0开始，基数与个数
                if (fileTobeSend.length()%512==0){
                    serialNum--;
                }
                boolean isFirstPack = true;
                byte[] firstPackage = null;
                byte[] feedBackData = new byte[16];
                int flashAddress = 0;
                boolean writeSuccess = true;

                byte[] writeCMD=new byte[16];
                writeCMD[0]= (byte) macInt1;
                writeCMD[1]= (byte) macInt2;
                writeCMD[2]= (byte) macInt3;
                writeCMD[3]= (byte) macInt4;
                writeCMD[4]= 16;
                writeCMD[11]= 22; //cmd


                while((len=fis.read(buf))!=-1&&writeSuccess){
                    if (isFirstPack){ // 第一个包放在最后写入，故先保存好第一个包的数据
                        isFirstPack = false;
                        firstPackage = new byte[len];
                        for (int i = 0;i<len;i++){
                            firstPackage[i] = buf[i];
                        }
                        flashAddress = len;
                        continue;
                    }

                    byte[] sendPack = new byte[16+512];
                    byte[] dataPackLength = intToByteArray(512, 3);
                    byte[] serialNumBytes = intToByteArray(serialNum, 3);
                    byte[] flashAddBytes = intToByteArray(flashAddress, 4);

                    setInbyteArray(5,dataPackLength,writeCMD);//包长度
                    setInbyteArray(8,serialNumBytes,writeCMD); //包序
                    setInbyteArray(12,flashAddBytes,writeCMD); //flash地址

                    setInbyteArray(0,writeCMD,sendPack);//写指令
                    setInbyteArray(16,buf,sendPack);//文件数据

                    os.write(sendPack,0,sendPack.length);
                    socket.getInputStream().read(feedBackData);//读取返回的数据

                    flashAddress += 512;
                    serialNum --;
                    //检查是否发送成功
                    for(int startIndex =5;startIndex<8;startIndex++ ){
                        if(feedBackData[startIndex]==0){
                            writeSuccess=true;
                        }else {
                            writeSuccess=false;
                            continue;
                        }
                    }
                    //更新进度
                    progress+=len;
                    float sumProgress = (progress / fileLength)*100;
                    Message msg=mHandler.obtainMessage();
                    msg.arg1= (int) sumProgress;
                    msg.what=UPDATE_PROGRESS;
                    mHandler.sendMessage(msg);
                }
                //最后发送第一个包
                byte[] sendPack = new byte[16+512];
                byte[] dataPackLength = intToByteArray(512, 3);
                byte[] serialNumBytes = intToByteArray(0, 3);
                byte[] flashAddBytes = intToByteArray(0, 4);
                setInbyteArray(5,dataPackLength,writeCMD);//包长度
                setInbyteArray(8,serialNumBytes,writeCMD); //包序
                setInbyteArray(12,flashAddBytes,writeCMD); //flash地址

                setInbyteArray(0,writeCMD,sendPack);//暂停指令
                setInbyteArray(16,firstPackage,sendPack);//文件数据

                os.write(sendPack,0,sendPack.length);
                progress+=firstPackage.length;
                float sumProgress = (progress / fileLength)*100;
                Message msg=mHandler.obtainMessage();
                msg.arg1= (int) sumProgress;
                msg.what=UPDATE_PROGRESS;
                mHandler.sendMessage(msg);

                socket.getInputStream().read(feedBackData);

                os.write(resetCMD);
                socket.getInputStream().read(feedBackData);
                Log.d("tcpSend","send file done");
            }

        } catch (ConnectException e){
            Message message = mHandler.obtainMessage();
            message.what=CONNECT_NORESPONE;
            mHandler.sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
            Message message = mHandler.obtainMessage();
            message.what=CONNECT_TIMEOUT;
            mHandler.sendMessage(message);
        }finally {
            if (socket!=null&&!socket.isClosed()){
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }if (fis!=null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }if (os!=null){
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
