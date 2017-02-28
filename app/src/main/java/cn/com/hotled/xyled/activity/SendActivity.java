package cn.com.hotled.xyled.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.com.hotled.xyled.App;
import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.bean.Program;
import cn.com.hotled.xyled.bean.ProgramType;
import cn.com.hotled.xyled.bean.TextContent;
import cn.com.hotled.xyled.dao.TextContentDao;
import cn.com.hotled.xyled.global.Global;
import cn.com.hotled.xyled.util.android.WifiAdmin;
import cn.com.hotled.xyled.util.genFile.GenFileUtil2;

public class SendActivity extends BaseActivity {
    private static final int UPDATE_PROGRESS = 0x11;
    private static final int WIFI_ERRO = 0x114;
    private static final int CONN_ERRO = 0x124;
    private static final int CONN_OUT_OF_TIME = 0x134;
    public static final int GENFILE_DONE=0x204;
    private TextView mTvStatus;
    private boolean isSending=false;
    private boolean fileReady=false;
    Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case UPDATE_PROGRESS:
                    int arg1 = msg.arg1;
                    mTvStatus.setText("正在传输数据 "+arg1+"%");
                    mSendProgress.setText(arg1+"%");
                    if (arg1==1){
                        mSendRound.setImageResource(R.drawable.send_data_uncomplete_low);
                    }else if (arg1==30){
                        mSendRound.setImageResource(R.drawable.send_data_uncomplete_middle);
                    }else if (arg1==70){
                        mSendRound.setImageResource(R.drawable.send_data_uncomplete_high);
                    }
                    if (arg1==100){
                        Toast.makeText(SendActivity.this,"已发送",Toast.LENGTH_LONG).show();
                        mTvStatus.setText("发送完成");
                        mSendAnim.cancel();
                        mSendOutsideAnim.cancel();
                        isSending=false;
                    }
                    break;
                case WIFI_ERRO:
                    Toast.makeText(SendActivity.this,"所连接WiFi非本公司产品，请切换WiFi",Toast.LENGTH_LONG).show();
                    mTvStatus.setText("所连接WiFi非本公司产品，请切换WiFi");
                    mSendAnim.cancel();
                    mSendOutsideAnim.cancel();
                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    isSending=false;
                    break;
                case CONN_ERRO:
                    Toast.makeText(SendActivity.this,"连接错误，请重新连接屏幕",Toast.LENGTH_LONG).show();
                    mTvStatus.setText("无法发送，请重新连接屏幕");
                    mSendAnim.cancel();
                    mSendOutsideAnim.cancel();
                    isSending=false;
                    break;
                case CONN_OUT_OF_TIME:
                    Toast.makeText(SendActivity.this,"连接超时，请重试",Toast.LENGTH_LONG).show();
                    mTvStatus.setText("连接超时，请重试");
                    mSendAnim.cancel();
                    mSendOutsideAnim.cancel();
                    isSending=false;
                    break;
                case GENFILE_DONE:
                    fileReady=true;
                    mPbSend.setVisibility(View.GONE);
                    mBtSend.setEnabled(true);
                    Toast.makeText(SendActivity.this,"文件已生成，开始传送",Toast.LENGTH_SHORT).show();
                    if (!isSending&&fileReady) {
                        send();
                    }
                    break;
            }


        }
    };
    private ProgressBar mPbSend;
    private TextView mSendProgress;
    private Animation mSendAnim;
    private ImageView mSendRound;
    private ImageView msendRoundOutside;
    private Animation mSendOutsideAnim;
    private Button mBtSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        initView();
        loadData();

    }

    private void loadData() {
        List<Program> mProgramList  = ((App) getApplication()).getDaoSession().getProgramDao().queryBuilder().list();

        Program[] sortProgramList = new Program[mProgramList.size()];
        for (int i = 0; i < mProgramList.size(); i++) {
            sortProgramList[mProgramList.get(i).getSortNumber()]=mProgramList.get(i);
        }
        mProgramList.clear();
        List<Program> programs = Arrays.asList(sortProgramList);
        mProgramList.addAll(programs);
        int screenWidth = getSharedPreferences(Global.SP_SCREEN_CONFIG, MODE_PRIVATE).getInt(Global.KEY_SCREEN_W, 64);
        int screenHeight = getSharedPreferences(Global.SP_SCREEN_CONFIG, MODE_PRIVATE).getInt(Global.KEY_SCREEN_H, 32);

//        CompressUtil compressUtil=new CompressUtil(this,mProgramList,screenWidth,screenHeight,mHandler);
//        compressUtil.startGenFile();
        TextContentDao textContentDao = ((App) getApplication()).getDaoSession().getTextContentDao();
        List<TextContent> textContents = new ArrayList<>();
        for (Program program : mProgramList) {
            if (program.getProgramType()== ProgramType.Text) {
                List<TextContent> list = textContentDao.queryBuilder().where(TextContentDao.Properties.ProgramId.eq(program.getId())).list();
                TextContent textContent = list.get(0);
                textContents.add(textContent);
            }
        }
        GenFileUtil2 genFileUtil2 = new GenFileUtil2(this,mHandler,mProgramList,textContents,screenWidth,screenHeight);
        genFileUtil2.startGenFile();
    }


    private void initView() {
        mPbSend = (ProgressBar) findViewById(R.id.pb_send_record);
        mSendRound = (ImageView) findViewById(R.id.iv_send_round);
        msendRoundOutside = (ImageView) findViewById(R.id.iv_send_round_outside);
        mTvStatus = (TextView) findViewById(R.id.tv_progress_tip);
        mSendProgress = (TextView) findViewById(R.id.tv_send_progress);
        mSendAnim = AnimationUtils.loadAnimation(this, R.anim.search_round);
        mSendOutsideAnim = AnimationUtils.loadAnimation(this, R.anim.anti_clock);
        mBtSend = (Button) findViewById(R.id.bt_send_send);
        mBtSend.setEnabled(false);
        mBtSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSending) {
                    send();
                }else {
                    Snackbar.make(mTvStatus,"已经在传送",Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    public void send(){
        mSendRound.startAnimation(mSendAnim);
        msendRoundOutside.startAnimation(mSendOutsideAnim);
        new Thread(){
            @Override
            public void run() {
                super.run();
                sendFile();
            }
        }.start();
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
            socket = new Socket(Global.SERVER_IP,Global.SERVER_PORT);
//            socket.setSoTimeout(3000);
            File file =new File(getFilesDir()+"/color.prg");
            fis = new FileInputStream(file);
            os = socket.getOutputStream();
            byte[] buf = new byte[512];
            int len = 0 ;
            float progress=0;
            long fileLength = file.length();

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
                    Log.d("tcpSend","暂停不成功 pauseCMD[i]!=readMsg[i]");
                    pauseSuccess = false;
                }
            }
            if (pauseSuccess) {
                Log.d("tcpSend","暂停成功");
                //暂停了,开始写数据
                Log.d("tcpSend","开始写数据");
                Log.d("tcpSend","mFile size--"+file.length()+" byte");
                int serialNum = (int) (file.length()/512); //包序是从0开始，基数与个数
                if (file.length()%512==0){
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
                    isSending=true;
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
//                    Log.d("tcpSend","writeSB == "+writeSB.toString());
                    setInbyteArray(0,writeCMD,sendPack);//写指令
                    setInbyteArray(16,buf,sendPack);//文件数据

//                    Log.d("tcpSend","flashAddress == "+flashAddress+",serialNum= "+serialNum);
                    Log.d("tcpSend","serialNum == "+serialNum);
                    os.write(sendPack,0,sendPack.length);
                    flashAddress += 512;
                    serialNum --;

                    socket.getInputStream().read(feedBackData);//读取返回的数据

                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < feedBackData.length; i++) {
                        String s = feedBackData[i] + " ";
                        sb.append(s);
                    }
//                    Log.d("tcpSend","feedBackData == "+sb.toString());
                    for(int startIndex =5;startIndex<8;startIndex++ ){
                        if(feedBackData[startIndex]==0){
                            writeSuccess=true;
                        }else {
                            writeSuccess=false;
                            continue;
                        }
                    }

//                    Log.d("tcpSend","writeSuccess == "+writeSuccess+" ");
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
//                Log.w("tcpSend","firstPackage == "+firstPackage);
                Log.d("tcpSend","flashAddress == "+0+",serialNum= "+0);
                os.write(sendPack,0,sendPack.length);
                progress+=firstPackage.length;
                float sumProgress = (progress / fileLength)*100;
                Message msg=mHandler.obtainMessage();
                msg.arg1= (int) sumProgress;
                msg.what=UPDATE_PROGRESS;
                mHandler.sendMessage(msg);
                Log.d("tcpSend","sumProgress == "+msg.arg1);

                socket.getInputStream().read(feedBackData);

                os.write(resetCMD);
                socket.getInputStream().read(feedBackData);
                Log.d("tcpSend","写入reset指令后读取 == "+0+",feedBackData= "+0);
                Log.d("tcpSend","send file done");
            }

        } catch (ConnectException e){
            Message message = mHandler.obtainMessage();
            message.what=CONN_ERRO;
            mHandler.sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
            Message message = mHandler.obtainMessage();
            message.what=CONN_OUT_OF_TIME;
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

    @Override
    public void onBackPressed() {
        if (isSending){
            new AlertDialog.Builder(this)
                    .setMessage("正在传送，确定退出吗？")
                    .setPositiveButton("退出", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SendActivity.super.onBackPressed();
                        }
                    })
                    .setNegativeButton("不退出",null)
                    .show();
        }else {
            super.onBackPressed();
        }
    }


    @Override
    public void onCreateCustomToolBar(Toolbar toolbar) {
        toolbar.setTitle("发送数据");
    }
}
