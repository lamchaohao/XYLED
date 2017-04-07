package cn.com.hotled.xyled.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.global.Common;
import cn.com.hotled.xyled.global.Global;

import static cn.com.hotled.xyled.global.Common.FL_QQFILE;
import static cn.com.hotled.xyled.global.Common.FL_WECHAT;
import static cn.com.hotled.xyled.global.Global.FILE_ILLEGAL;
import static cn.com.hotled.xyled.global.Global.UPDATE_PROGRESS;

public class ForwardDataActivity extends BaseActivity {

    private static final int SELECT_FILE = 220;
    @BindView(R.id.tv_forward_fromQQ)
    TextView mTvForwardFromQQ;
    @BindView(R.id.tv_forward_fromWechat)
    TextView mTvForwardFromWechat;
    @BindView(R.id.ll_forward_fromOther)
    LinearLayout mTvForwardFromOther;
    @BindView(R.id.tv_forward_selectResult)
    TextView mTvForwardSelectResult;
    @BindView(R.id.tv_forward_checking)
    TextView mTvForwardChecking;
    @BindView(R.id.bt_forward_send)
    Button mBtForwardSend;
    @BindView(R.id.pb_forward_check)
    ProgressBar mPbForwardCheck;

    private boolean isCopyCompleted;
    private boolean mHasConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forward_data);
        ButterKnife.bind(this);
    }


    private Handler mHandler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case FILE_ILLEGAL:
                    mTvForwardChecking.setText(R.string.file_illegal);
                    break;
                case UPDATE_PROGRESS:
                    int arg1 = msg.arg1;
                    mTvForwardChecking.setText(arg1+"%");
                    mPbForwardCheck.setProgress(arg1);
                    if (arg1==100){
                        isCopyCompleted = true;
                        mPbForwardCheck.setProgress(arg1);
                        mTvForwardChecking.setText(R.string.tos_check_success);
                        Intent intent = new Intent(ForwardDataActivity.this, SendPcDataActivity.class);
                        if (mHasConfig) {
                            intent.setType(Common.StartType_FromComputer_HAS_CFG);
                        }else {
                            intent.setType(Common.StartType_FromComputer_NO_CFG);
                        }
                        startActivity(intent);
                    }

            }
        }
    };



    @OnClick({R.id.tv_forward_fromQQ, R.id.tv_forward_fromWechat, R.id.ll_forward_fromOther, R.id.bt_forward_send})
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.tv_forward_fromQQ:
                File parentFile = new File(Environment.getExternalStorageDirectory() + FL_QQFILE);
                if (!parentFile.exists()) {
                    parentFile.mkdirs();
                }
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setDataAndType(Uri.parse(parentFile.toString()), "file/*");
                startActivityForResult(intent, SELECT_FILE);
                break;
            case R.id.tv_forward_fromWechat:
                File wechatFile = new File(Environment.getExternalStorageDirectory() + FL_WECHAT);
                if (!wechatFile.exists()) {
                    wechatFile.mkdirs();
                }
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setDataAndType(Uri.parse(wechatFile.toString()), "file/*");
                startActivityForResult(intent, SELECT_FILE);
                break;
            case R.id.ll_forward_fromOther:
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("file/*");
                startActivityForResult(intent, SELECT_FILE);
                break;
            case R.id.bt_forward_send:
                if (isCopyCompleted) {
                    Intent sendIntent = new Intent(this, SendPcDataActivity.class);
                    if (mHasConfig) {
                        intent.setType(Common.StartType_FromComputer_HAS_CFG);
                    }else {
                        intent.setType(Common.StartType_FromComputer_NO_CFG);
                    }
                    startActivity(sendIntent);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == SELECT_FILE) {
            isCopyCompleted=false;
            String path = Uri.decode(data.getDataString());
            Log.i("forward", path);
            String truePath = path.substring(7);
            if (path.contains("."))
                checkFileLegal(truePath);
            else
                mHandler.sendEmptyMessage(FILE_ILLEGAL);
            mTvForwardSelectResult.setText(truePath);
            Log.i("forward", truePath);

        }
    }

    private void checkFileLegal(final String path) {
        //检查是否已.prg结尾
        String postfix = path.substring(path.lastIndexOf("."));
        Log.i("forward", "postfix=" + postfix);
        if (postfix.contains("PRG")||postfix.contains("prg")) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    copyFile(path);
                }
            }).start();
        } else {
            mHandler.sendEmptyMessage(FILE_ILLEGAL);
        }
    }

    private void copyFile(String path) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        FileOutputStream configFos = null;
        try {
            File fileTobeSend = new File(path);
            File copyedFile=new File(getFilesDir() + Common.FL_SEND_FILE);
            File pcConfig=new File(getFilesDir() + Common.FL_CONFIG_FROM_PC);
            if (copyedFile.exists()) {
                copyedFile.delete();
            }
            if (pcConfig.exists()){
                pcConfig.delete();
            }
            fis = new FileInputStream(fileTobeSend);
            fos = new FileOutputStream(getFilesDir() + Common.FL_SEND_FILE, true);
            configFos = new FileOutputStream(getFilesDir() + Common.FL_CONFIG_FROM_PC, true);
            byte[] buff = new byte[512];
            int len = -1;
            int readIndex = 0;
            float progress = 0;
            float fileLength = fileTobeSend.length();
            while ((len = fis.read(buff)) != -1) {
                if (readIndex == 0) {
                    String readPreFix = new String(buff, 0, 12);
                    //如果前面的字节等于COLOR_01.PRG hc1, LED_HC01.PRG hc-2;
                    if (readPreFix.equals(Global.HC1_FILENAME) ||
                            readPreFix.equals(Global.HC2_FILENAME)) {
                        //带有屏幕参数的文件
                        mHasConfig = true;
                        configFos.write(buff);
                    } else {
                        //不带屏幕参数的文件
                        fos.write(buff);
                    }
                } else {
                    if (mHasConfig && progress >= 4096) {
                        fos.write(buff);//如果有参数的,4096后再写入
                    } else if (mHasConfig && progress < 4096) {
                        configFos.write(buff);
                    } else {
                        fos.write(buff);
                    }

                }
                progress += len;
                float sumProgress = (progress / fileLength)*100;
                Message msg=mHandler.obtainMessage();
                msg.arg1= (int) sumProgress;
                msg.what=UPDATE_PROGRESS;
                mHandler.sendMessageDelayed(msg,1000);
                readIndex++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


}
