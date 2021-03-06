package cn.com.hotled.xyled.util.communicate;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.com.hotled.xyled.App;
import cn.com.hotled.xyled.bean.TraceFile;
import cn.com.hotled.xyled.dao.TraceFileDao;
import cn.com.hotled.xyled.global.Common;
import cn.com.hotled.xyled.global.Global;
import cn.com.hotled.xyled.util.android.WifiAdmin;

import static cn.com.hotled.xyled.global.Common.FL_TRACE_DIR;
import static cn.com.hotled.xyled.global.Global.READ_FAILE;
import static cn.com.hotled.xyled.global.Global.READ_SUCCESS;
import static cn.com.hotled.xyled.global.Global.WIFI_ERRO;

/**
 * Created by Lam on 2017/2/10.
 */

public class ReadScreenDataUtil {

    private Activity mContext;
    private Handler mHandler;
    public ReadScreenDataUtil(Activity context, Handler handler) {
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
    private void readData() {
        Socket socket = null;
        FileOutputStream fos = null;
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
        Log.w("tcpSend","mac = "+macInt1+":"+macInt2+":"+macInt3+":"+macInt4);
        try {
            socket = new Socket(Global.SERVER_IP, Global.SERVER_PORT);
            File dataRead=new File(mContext.getFilesDir()+ Common.FL_SCREEN_DATA);
            if (!dataRead.exists()) {
                dataRead.createNewFile();
            }else {
                dataRead.delete();
            }
            fos = new FileOutputStream(dataRead,true);
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
            readCMD[11]= 17; //cmd 0x00010001


            //执行测试指令
            os.write(testCMD);
            byte[] readMsg = new byte[16];
            socket.getInputStream().read(readMsg);//读取返回


            for (int i = 0; i < readMsg.length; i++) {
                if(testCMD[i]!=readMsg[i]){
                    mHandler.sendEmptyMessageDelayed(READ_FAILE,1000);
                }
            }

            //执行暂停指令
            os.write(pauseCMD);
            socket.getInputStream().read(readMsg);//读取返回
            boolean pauseSuccess = true;
            for (int i = 0; i < readMsg.length; i++) {
                if(pauseCMD[i]!=readMsg[i]){
                    mHandler.sendEmptyMessageDelayed(READ_FAILE,1000);
                    pauseSuccess = false;
                }
            }
            if (pauseSuccess) {
                //暂停成功，开始回读
                byte[] feedbackcmd=new byte[16];
                byte[] readbackmsg=new byte[512];

                int readAddress=129024;
                for (int k = 0; k < 4; k++) {
                    byte[] bytes = intToByteArray(readAddress, 4);
                    setInbyteArray(12,bytes,readCMD);
                    os.write(readCMD);
                    socket.getInputStream().read(feedbackcmd);//返回的指令
                    socket.getInputStream().read(readbackmsg);//接着返回读取到的数据512byte
                    readAddress+=512;//读完后地址加512
                    fos.write(readbackmsg);
                    if (k==0){
                        //第一扇区的数据
                        saveConfigToSharedPref(readbackmsg);
                    }else if (k==1){
                        //第二扇区 走线表
                        findTraceFile(readbackmsg);
                    }
                }
                os.write(resumeCMD);
                socket.getInputStream().read(feedbackcmd);
                mHandler.sendEmptyMessageDelayed(READ_SUCCESS,1000);

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

    private void findTraceFile(byte[] readbackmsg) {
        SharedPreferences sp = mContext.getSharedPreferences(Global.SP_SCREEN_CONFIG, Context.MODE_PRIVATE);
        int scanCount = sp.getInt(Global.KEY_SCREEN_SCAN, -1);
        int batH = sp.getInt(Global.KEY_BATH, -1);
        int batW = sp.getInt(Global.KEY_BATW, -1);
        int foldCount = batH/scanCount;
        TraceFileDao traceFileDao = ((App) mContext.getApplication()).getDaoSession().getTraceFileDao();
        if (scanCount!=-1){
            //取出扫描份数一致的走线文件
            List<TraceFile> list = traceFileDao.queryBuilder().where(TraceFileDao.Properties.ScanCount.eq(scanCount)).list();
            FileInputStream fis = null;
            for (TraceFile traceFile : list) {
                //比较折数与模组宽度是否一致
                if (traceFile.getFoldCount()==foldCount&&traceFile.getModuleWidth()==batW) {
                    File file = new File(mContext.getFilesDir()+FL_TRACE_DIR,traceFile.getFilePath().getAbsolutePath());
                    byte[] fileData =new byte[512];
                    try {
                        fis =new FileInputStream(file);
                        fis.read(fileData);
                        boolean isMatch = true;
                        //比较内容是否一致
                        for (int i = 0; i < fileData.length; i++) {
                            if (readbackmsg[i]!=fileData[i]) {
                                isMatch = false;
                                break;
                            }
                        }
                        if (isMatch) {
                            //匹配后保存到sp
                            long id = traceFile.getId();
                            sp.edit().putLong(Global.KEY_TRACE_SELECT,id).apply();
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
                    }
                }
            }
        }
    }

    private void saveConfigToSharedPref(byte[] readbackmsg) {
        //转换过程中可能会出现负数，故而与0xFF进行与运算
        String readCard = new String(readbackmsg,0,12);
        String cardSeries="";
        if (readCard.equals(Global.HC1_FILENAME)){
            cardSeries = Global.HC1_CARD;
        }else {
            cardSeries = Global.HC2_CARD;
        }
        int screenWid=readbackmsg[35]&0xff; //宽度
        int screenHei=readbackmsg[37]&0xff; // 高度
        int data=readbackmsg[42]&0xff; //data
        int oe=readbackmsg[43]&0xff; // oe
        int code = 0;//138code
        int brightness=readbackmsg[47]&0xff;
        Log.i("brightness","brightness = "+brightness);
        if (brightness==1){
            brightness=100;
        }else if (brightness==255){
            brightness=33;
        }else {
            brightness=66;
        }
        Log.i("brightness","brightness = "+brightness);
        int screenScan=readbackmsg[38]&0xff;
        if (screenScan>=128){
            code=1;
            screenScan-=128;
        }
        int batH = readbackmsg[66] & 0xff;
        int batW = readbackmsg[67] & 0xff;
        int special = readbackmsg[496]&0xff; //增高加长
        int dataOrientation = readbackmsg[497]&0xff; //数据方向
        int RGBorder=readbackmsg[498]&0xff; //rgb次序
        screenWid=screenWid<<8;
        screenWid+=readbackmsg[36]&0xff;

        SharedPreferences.Editor edit = mContext.getSharedPreferences(Global.SP_SCREEN_CONFIG, Context.MODE_PRIVATE).edit();
        edit.putString(Global.KEY_CARD_SERIES,cardSeries);
        edit.putInt(Global.KEY_RGB_ORDER,RGBorder<6?RGBorder:0);
        edit.putInt(Global.KEY_SCREEN_W,screenWid<1025?screenWid:64);
        edit.putInt(Global.KEY_SCREEN_H,screenHei<1025?screenHei:64);
        edit.putInt(Global.KEY_SCREEN_SCAN,screenScan<64?screenScan:8);
        edit.putInt(Global.KEY_DATA,data<2?data:0);
        edit.putInt(Global.KEY_OE,oe<2?oe:0);
        edit.putInt(Global.KEY_BRIGHTNESS,brightness);
        edit.putInt(Global.KEY_138CODE,code<3?code:0);
        edit.putInt(Global.KEY_BATH,batH<255?batH:16);
        edit.putInt(Global.KEY_BATW,batW<255?batW:16);
        edit.putInt(Global.KEY_SPECIAL,special<8?special:0);
        edit.putInt(Global.KEY_DATA_ORIENTATION,dataOrientation<4?dataOrientation:0);
        edit.apply();
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
