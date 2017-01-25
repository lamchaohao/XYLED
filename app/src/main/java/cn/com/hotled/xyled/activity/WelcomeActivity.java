package cn.com.hotled.xyled.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import cn.com.hotled.xyled.R;

public class WelcomeActivity extends Activity {
    private static final int COPY_FINISH = 200;
    private static final int START_ACTIVITY = 300;
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case COPY_FINISH:
                    mSharePref.edit().putBoolean("SystemConfig",false).apply();
                    countDownAndEnter();
                    break;
                case START_ACTIVITY:
                    startActivity(new Intent(WelcomeActivity.this,MainActivity.class));
                    finish();
                    break;
            }
        }
    };
    private SharedPreferences mSharePref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_guide);
        initView();
        loadData();
    }

    private void loadData() {
        mSharePref = getSharedPreferences("SystemConfig", MODE_PRIVATE);

        boolean isFirstIn = mSharePref.getBoolean("isFirstIn", true);
        File flowFileDir=new File(getFilesDir()+"/flow");
        if (isFirstIn&&!flowFileDir.exists()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    copyFilesFassets("flow",getFilesDir()+"/flow");
                    Message message = mHandler.obtainMessage();
                    message.what=COPY_FINISH;
                    mHandler.sendMessage(message);
                }
            }).start();

        }else {
           countDownAndEnter();
        }

    }

    private void countDownAndEnter() {
       mHandler.sendEmptyMessageDelayed(START_ACTIVITY,1000);
    }

    private void initView() {

    }

    /**
     *  从assets目录中复制整个文件夹内容
     *  @param  oldPath  String  原文件路径  如：/aa
     *  @param  newPath  String  复制后路径  如：xx:/bb/cc
     */
    public void copyFilesFassets( String oldPath, String newPath) {
        InputStream is =null;
        FileOutputStream fos=null;
        try {
            String fileNames[] = getAssets().list(oldPath);//获取assets目录下的所有文件及目录名
            if (fileNames.length > 0) {//如果是目录
                File file = new File(newPath);
                file.mkdirs();//如果文件夹不存在，则递归
                for (String fileName : fileNames) {
                    copyFilesFassets(oldPath + "/" + fileName,newPath+"/"+fileName);
                }
            } else {//如果是文件
                is = getAssets().open(oldPath);
                fos = new FileOutputStream(new File(newPath));
                byte[] buffer = new byte[1024];
                int byteCount=0;
                while((byteCount=is.read(buffer))!=-1) {//循环从输入流读取 buffer字节
                    fos.write(buffer, 0, byteCount);//将读取的输入流写入到输出流
                }
                fos.flush();//刷新缓冲区

            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            //如果捕捉到错误则通知UI线程
        }finally {
            if (is!=null) {
                try {
                    is.close();
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
}
