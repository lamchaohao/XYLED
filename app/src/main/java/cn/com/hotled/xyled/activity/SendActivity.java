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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.com.hotled.xyled.App;
import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.bean.Program;
import cn.com.hotled.xyled.bean.ProgramType;
import cn.com.hotled.xyled.bean.TextContent;
import cn.com.hotled.xyled.dao.TextContentDao;
import cn.com.hotled.xyled.global.Common;
import cn.com.hotled.xyled.global.Global;
import cn.com.hotled.xyled.util.android.WifiAdmin;
import cn.com.hotled.xyled.util.genFile.GenFileUtil2;

import static cn.com.hotled.xyled.global.Global.CONNECT_NORESPONE;
import static cn.com.hotled.xyled.global.Global.CONNECT_TIMEOUT;
import static cn.com.hotled.xyled.global.Global.GENFILE_DONE;
import static cn.com.hotled.xyled.global.Global.UPDATE_PROGRESS;
import static cn.com.hotled.xyled.global.Global.WIFI_ERRO;

public class SendActivity extends BaseActivity {
    private TextView mTvStatus;
    private boolean isSending=false;
    private boolean fileReady=false;
    Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case UPDATE_PROGRESS:
                    int arg1 = msg.arg1;
                    mTvStatus.setText(getString(R.string.sending_data)+arg1+"%");
                    mSendProgress.setText(arg1+"%");
                    if (arg1==1){
                        mSendRound.setImageResource(R.drawable.send_data_uncomplete_low);
                    }else if (arg1==30){
                        mSendRound.setImageResource(R.drawable.send_data_uncomplete_middle);
                    }else if (arg1==70){
                        mSendRound.setImageResource(R.drawable.send_data_uncomplete_high);
                    }
                    if (arg1==100){
                        Toast.makeText(SendActivity.this,R.string.tos_sendProgram_done,Toast.LENGTH_LONG).show();
                        mTvStatus.setText(R.string.tos_sendProgram_done);
                        mSendAnim.cancel();
                        mSendOutsideAnim.cancel();
                        isSending=false;
                    }
                    break;
                case WIFI_ERRO:
                    Toast.makeText(SendActivity.this,R.string.tos_wifi_switch,Toast.LENGTH_LONG).show();
                    mTvStatus.setText(R.string.tos_wifi_switch);
                    mSendAnim.cancel();
                    mSendOutsideAnim.cancel();
                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    isSending=false;
                    break;
                case CONNECT_NORESPONE:
                    Toast.makeText(SendActivity.this,R.string.tos_screen_noresponse,Toast.LENGTH_LONG).show();
                    mTvStatus.setText(R.string.tos_screen_noresponse);
                    mSendAnim.cancel();
                    mSendOutsideAnim.cancel();
                    isSending=false;
                    break;
                case CONNECT_TIMEOUT:
                    Toast.makeText(SendActivity.this,R.string.tos_wifi_timeout,Toast.LENGTH_LONG).show();
                    mTvStatus.setText(R.string.tos_wifi_timeout);
                    mSendAnim.cancel();
                    mSendOutsideAnim.cancel();
                    isSending=false;
                    break;
                case GENFILE_DONE:
                    fileReady=true;
                    isGeningFile = false;
                    mPbSend.setVisibility(View.GONE);
                    mBtSend.setEnabled(true);
                    Toast.makeText(SendActivity.this,R.string.tos_genAndSend,Toast.LENGTH_SHORT).show();
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
    private boolean isGeningFile;

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
        isGeningFile= true;
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
                    Snackbar.make(mTvStatus,R.string.tos_isSending,Snackbar.LENGTH_LONG).show();
                }
            }
        });
        //进入开始录制时候，外圈动画开始
        msendRoundOutside.startAnimation(mSendOutsideAnim);
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
            socket = new Socket(Global.SERVER_IP,Global.SERVER_PORT);
            socket.setSoTimeout(10000);
            File file =new File(getFilesDir()+ Common.FL_COLOR_PRG);
            fis = new FileInputStream(file);
            os = socket.getOutputStream();
            byte[] buf = new byte[512];
            int len = 0 ;
            float progress=0;
            long fileLength = file.length();

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
                int serialNum = (int) (file.length()/512); //包序是从0开始，基数与个数
                if (file.length()%512==0){
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

                    setInbyteArray(0,writeCMD,sendPack);//写指令
                    setInbyteArray(16,buf,sendPack);//文件数据

                    os.write(sendPack,0,sendPack.length);
                    socket.getInputStream().read(feedBackData);//读取返回的数据

                    flashAddress += 512;
                    serialNum --;
                    //检车是否发送成功
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
                    .setMessage(R.string.msg_sendingAndAsk)
                    .setPositiveButton(R.string.msg_quit, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SendActivity.super.onBackPressed();
                        }
                    })
                    .setNegativeButton(R.string.msg_cancle,null)
                    .show();
        }else if(isGeningFile){
            new AlertDialog.Builder(this)
                    .setMessage(R.string.msg_convertingAndAsk)
                    .setPositiveButton(R.string.msg_quit, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SendActivity.super.onBackPressed();
                        }
                    })
                    .setNegativeButton(R.string.msg_cancle,null)
                    .show();
        }else {
            super.onBackPressed();
        }
    }


    @Override
    public void onCreateCustomToolBar(Toolbar toolbar) {
        toolbar.setTitle(R.string.send_data);
    }

}
