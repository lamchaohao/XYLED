package cn.com.hotled.xyled.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.global.Common;
import cn.com.hotled.xyled.util.communicate.SendDataUtil;
import cn.com.hotled.xyled.util.communicate.SendSetDataUtil;

import static cn.com.hotled.xyled.global.Common.FL_SEND_FILE;
import static cn.com.hotled.xyled.global.Global.CONNECT_NORESPONE;
import static cn.com.hotled.xyled.global.Global.CONNECT_TIMEOUT;
import static cn.com.hotled.xyled.global.Global.GENFILE_DONE;
import static cn.com.hotled.xyled.global.Global.Start_Send;
import static cn.com.hotled.xyled.global.Global.UPDATE_PROGRESS;
import static cn.com.hotled.xyled.global.Global.WIFI_ERRO;

public class SendPcDataActivity extends BaseActivity {
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
                    isSending = true;
                    if (arg1==100){
                        Toast.makeText(SendPcDataActivity.this,R.string.tos_sendProgram_done,Toast.LENGTH_LONG).show();
                        mTvStatus.setText(R.string.tos_sendProgram_done);
                        mSendAnim.cancel();
                        mSendOutsideAnim.cancel();
                        isSending=false;
                    }
                    break;
                case WIFI_ERRO:
                    Toast.makeText(SendPcDataActivity.this,R.string.tos_wifi_switch,Toast.LENGTH_LONG).show();
                    mTvStatus.setText(R.string.tos_wifi_switch);
                    mSendAnim.cancel();
                    mSendOutsideAnim.cancel();
                    Intent intent = new Intent(SendPcDataActivity.this, ConnectCardActivity.class);
                    startActivity(intent);
                    isSending=false;
                    mBtSend.setEnabled(true);
                    break;
                case CONNECT_NORESPONE:
                    Toast.makeText(SendPcDataActivity.this,R.string.tos_screen_noresponse,Toast.LENGTH_LONG).show();
                    mTvStatus.setText(R.string.tos_screen_noresponse);
                    mSendAnim.cancel();
                    mSendOutsideAnim.cancel();
                    isSending=false;
                    mBtSend.setEnabled(true);
                    break;
                case CONNECT_TIMEOUT:
                    Toast.makeText(SendPcDataActivity.this,R.string.tos_wifi_timeout,Toast.LENGTH_LONG).show();
                    mTvStatus.setText(R.string.tos_wifi_timeout);
                    mSendAnim.cancel();
                    mSendOutsideAnim.cancel();
                    isSending=false;
                    mBtSend.setEnabled(true);
                    break;
                case GENFILE_DONE:
                    fileReady=true;
                    isGeningFile = false;
                    mPbSend.setVisibility(View.GONE);
                    mBtSend.setEnabled(true);
                    Toast.makeText(SendPcDataActivity.this,R.string.tos_genAndSend,Toast.LENGTH_SHORT).show();
                    if (!isSending&&fileReady) {
                        send();
                    }
                    break;
                case Start_Send:
                    mTvStatus.setText(R.string.tos_start_send);
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
    private SendDataUtil mDataUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        initView();
        String type = getIntent().getType();
        File filetoSend = new File(getFilesDir()+ Common.FL_COLOR_PRG);
        mDataUtil = new SendDataUtil(this,mHandler,filetoSend);
        if (type!=null&&Common.StartType_FromComputer_NO_CFG.equals(type)){
            File file = new File(getFilesDir()+FL_SEND_FILE);
            mDataUtil.setFile(file);
            mHandler.sendEmptyMessage(GENFILE_DONE);
        }else if (type!=null&&Common.StartType_FromComputer_HAS_CFG.equals(type)){
            File file = new File(getFilesDir()+FL_SEND_FILE);
            mDataUtil.setFile(file);
            SendSetDataUtil configUtil=new SendSetDataUtil(this,null,mHandler);
            configUtil.startSendPcConfig();
            fileReady=true;
        }

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
        mHandler.sendEmptyMessage(Start_Send);
        mSendRound.startAnimation(mSendAnim);
        msendRoundOutside.startAnimation(mSendOutsideAnim);
        mDataUtil.send();
    }


    @Override
    public void onBackPressed() {
        if (isSending){
            new AlertDialog.Builder(this)
                    .setMessage(R.string.msg_sendingAndAsk)
                    .setPositiveButton(R.string.msg_quit, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SendPcDataActivity.super.onBackPressed();
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
                            SendPcDataActivity.super.onBackPressed();
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
