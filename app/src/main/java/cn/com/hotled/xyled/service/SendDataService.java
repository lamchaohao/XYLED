package cn.com.hotled.xyled.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.activity.MainActivity;
import cn.com.hotled.xyled.util.android.WifiAdmin;

public class SendDataService extends Service {
    private NotificationManager mNotificationManager;
    private Notification mNotification;
    private PendingIntent mPendingIntent;

    private static final int UPDATE_PROGRESS = 0x11;
    private static final int WIFI_ERRO = 0x114;
    private String targetIP;
    private int targetPort;
    private File mFile;
    private RemoteViews mRemoteViews;
    private int notification_id=101;

    public SendDataService() {
        targetIP="192.168.3.1";
        targetPort=16389;
        mFile=new File(Environment.getExternalStorageDirectory()+"/amap/COLOR_01.PRG");
    }

    Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case UPDATE_PROGRESS:
                    int arg1 = msg.arg1;
                    if (arg1==100){
                        mRemoteViews.setTextViewText(R.id.tv_notification_send,"传送完成");
                        mRemoteViews.setTextViewText(R.id.tv_notification_percent,arg1+"%");
                        mNotificationManager.notify(notification_id,mNotification);
                        Toast.makeText(SendDataService.this,"已发送",Toast.LENGTH_LONG).show();
                    }else {
                        mRemoteViews.setProgressBar(R.id.pb_notification_send,100,arg1,false);
                        mRemoteViews.setTextViewText(R.id.tv_notification_send,"正在传送");
                        mRemoteViews.setTextViewText(R.id.tv_notification_percent,arg1+"%");
                        mNotificationManager.notify(notification_id,mNotification);
                    }
                    break;
                case WIFI_ERRO:
                    Toast.makeText(SendDataService.this,"所连接WiFi非本公司产品，请切换WiFi",Toast.LENGTH_LONG).show();
                    break;
            }


        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        createNotification();
        new Thread(){
            @Override
            public void run() {
                super.run();
                sendFile();
            }
        }.start();
        return super.onStartCommand(intent, flags, startId);
    }

    private void createNotification() {
        mNotificationManager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        /**
         * 用自定义view来显示notification
         */
        mRemoteViews=new RemoteViews(getPackageName(),R.layout.content_notification_send);
        mRemoteViews.setTextViewText(R.id.tv_notification_send,"准备传送");
        mRemoteViews.setTextViewText(R.id.tv_notification_percent,"0%");
        mRemoteViews.setProgressBar(R.id.pb_notification_send,100,56,false);
        mRemoteViews.setImageViewResource(R.id.iv_notific_send,R.drawable.ic_live_tv_primary_dark_700_36dp);

        Intent sendIntent =new Intent(this, MainActivity.class);
        mPendingIntent =PendingIntent.getActivity(this,0,sendIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mNotification = new Notification.Builder(this)
                    .setContentTitle("传送数据")
                    .setSmallIcon(R.drawable.ic_live_tv_primary_dark_700_36dp)
                    .setAutoCancel(true)
                    .setContentIntent(mPendingIntent)
                    .build();
        }else {
            mNotification = new Notification();
            mNotification.contentIntent=mPendingIntent;
        }
        mNotification.icon=R.drawable.ic_live_tv_primary_dark_700_36dp;
        mNotification.contentView=mRemoteViews;
        mNotification.flags|=Notification.FLAG_AUTO_CANCEL;
        mNotification.defaults=Notification.DEFAULT_LIGHTS;
        mNotification.tickerText="闪现文字";
        mNotificationManager.notify(notification_id,mNotification);

    }

    private void sendFile(){
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
            Message message = mHandler.obtainMessage();
            message.what=WIFI_ERRO;
            mHandler.sendMessage(message);
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
            socket = new Socket(targetIP,targetPort);
//            socket.setSoTimeout(3000);
            fis = new FileInputStream(mFile);
            os = socket.getOutputStream();
            byte[] buf = new byte[512];
            int len = 0 ;
            float progress=0;
            long fileLength = mFile.length();

            byte[] testCMD=new byte[16];
            //8032364e 128 50 35 78
            //
            //8030ed86  128 48 237 134
            testCMD[0]= (byte) macInt1;
            testCMD[1]= (byte) macInt2;
            testCMD[2]= (byte) macInt3;
            testCMD[3]= (byte) macInt4;
            testCMD[4]= 16;
            testCMD[5]= 0;//Data Length
            testCMD[6]= 0;//Data Length
            testCMD[7]= 0;//Data Length
            testCMD[8]= 0;//包序Serial Number
            testCMD[9]= 0;//包序Serial Number
            testCMD[10]= 0;//包序Serial Number
            testCMD[11]= 0; //cmd
            testCMD[12]= 0;
            testCMD[13]= 0;
            testCMD[14]= 0;
            testCMD[15]= 0;

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

            byte[] resetCMD=new byte[16];
            resetCMD[0]= (byte) macInt1;
            resetCMD[1]= (byte) macInt2;
            resetCMD[2]= (byte) macInt3;
            resetCMD[3]= (byte) macInt4;
            resetCMD[4]= 16;
            resetCMD[5]= 0;//Data Length
            resetCMD[6]= 0;//Data Length
            resetCMD[7]= 0;//Data Length
            resetCMD[8]= 0;//包序Serial Number
            resetCMD[9]= 0;//包序Serial Number
            resetCMD[10]= 0;//包序Serial Number
            resetCMD[11]= 4; //cmd
            resetCMD[12]= 0;
            resetCMD[13]= 0;
            resetCMD[14]= 0;
            resetCMD[15]= 0;

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
//                Log.w("tcpSend","返回的 testCMD msg="+readMsg[i]);
            }

            //执行暂停指令
            os.write(pauseCMD);
            socket.getInputStream().read(readMsg);//读取返回

            boolean pauseSuccess = true;
            for (int i = 0; i < readMsg.length; i++) {
                if(pauseCMD[i]!=readMsg[i]){
                    Log.w("tcpSend","暂停不成功 pauseCMD[i]!=readMsg[i]");
                    pauseSuccess = false;
                }
//                Log.w("tcpSend","返回的暂停 pauseCMD msg = "+readMsg[i]);
            }
            if (pauseSuccess) {
                Log.w("tcpSend","暂停成功");
                //暂停了,开始写数据
                Log.w("tcpSend","开始写数据");
                Log.i("tcpSend","mFile size--"+mFile.length()+" byte");
                int serialNum = (int) (mFile.length()/512); //包序是从0开始，基数与个数
                if (mFile.length()%512==0){
                    serialNum--;
                }
                Log.w("tcpSend","总包长度 == "+serialNum+" ");
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
                writeCMD[5]= 0;//Data Length
                writeCMD[6]= 0;//Data Length
                writeCMD[7]= 0;//Data Length
                writeCMD[8]= 0;//包序Serial Number
                writeCMD[9]= 0;//包序Serial Number
                writeCMD[10]= 0;//包序Serial Number
                writeCMD[11]= 22; //cmd
                writeCMD[12]= 0;
                writeCMD[13]= 0;
                writeCMD[14]= 0;
                writeCMD[15]= 0;


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

                    StringBuilder writeSB = new StringBuilder();
                    for (int i = 0; i < writeCMD.length; i++) {
                        String s = writeCMD[i] + " ";
                        writeSB.append(s);
                    }
                    Log.w("tcpSend","writeSB == "+writeSB.toString());
                    setInbyteArray(0,writeCMD,sendPack);//写指令
                    setInbyteArray(16,buf,sendPack);//文件数据

                    Log.w("tcpSend","flashAddress == "+flashAddress+",serialNum= "+serialNum);
                    os.write(sendPack,0,sendPack.length);
                    flashAddress += 512;
                    serialNum --;

                    socket.getInputStream().read(feedBackData);//读取返回的数据

                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < feedBackData.length; i++) {
                        String s = feedBackData[i] + " ";
                        sb.append(s);
                    }
                    Log.w("tcpSend","feedBackData == "+sb.toString());
                    for(int startIndex =5;startIndex<8;startIndex++ ){
                        if(feedBackData[startIndex]==0){
                            writeSuccess=true;
                        }else {
                            writeSuccess=false;
                            continue;
                        }
                    }

                    Log.w("tcpSend","writeSuccess == "+writeSuccess+" ");
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
                Log.w("tcpSend","firstPackage == "+firstPackage);
                Log.w("tcpSend","flashAddress == "+0+",serialNum= "+0);
                os.write(sendPack,0,sendPack.length);
                progress+=firstPackage.length;
                float sumProgress = (progress / fileLength)*100;
                Message msg=mHandler.obtainMessage();
                msg.arg1= (int) sumProgress;
                msg.what=UPDATE_PROGRESS;
                mHandler.sendMessage(msg);
                Log.w("tcpSend","sumProgress == "+msg.arg1);

                socket.getInputStream().read(feedBackData);

                os.write(resetCMD);
                socket.getInputStream().read(feedBackData);
                Log.w("tcpSend","写入reset指令后读取 == "+0+",feedBackData= "+0);
                Log.i("tcpSend","send file done");
            }

        } catch (IOException e) {
            e.printStackTrace();
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
            Log.w("tcpSend","关闭TCP");
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
