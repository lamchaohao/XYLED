package cn.com.hotled.xyled.util;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.view.NumberProgressBar;
import cn.com.hotled.xyled.view.OnProgressBarListener;

/**
 * Created by Lam on 2016/11/15.
 */

public class TcpSend {

    private String targetIP;
    private int targetPort;
    private File mFile;
    private Context mContext;
    private NumberProgressBar mProgressBar;
    Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int arg1 = msg.arg1;
            mProgressBar.setProgress(arg1);
        }
    };

    public TcpSend(Context context,String targetIP, int targetPort, File file) {
        this.mContext=context;
        this.targetIP = targetIP;
        this.targetPort = targetPort;
        mFile = file;
    }

    public void send(){
        showDialog();
        new Thread(){
            @Override
            public void run() {
                super.run();
                sendFile();
            }
        }.start();
    }



    private void showDialog() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.content_progress, null);
        mProgressBar = (NumberProgressBar) view.findViewById(R.id.content_progressbar);
        mProgressBar.setProgress(0);
        mProgressBar.setMax(100);
        mProgressBar.setOnProgressBarListener(new OnProgressBarListener() {
            @Override
            public void onProgressChange(int current, int max) {
                mProgressBar.setProgress(current);
            }
        });
        new AlertDialog.Builder(mContext)
                .setTitle("sending...")
                .setView(view)
                .setPositiveButton("ok",null)
                .setNegativeButton("dismiss",null)
                .show();
    }

    private void sendFile(){
        Socket socket = null;
        FileInputStream fis = null;
        OutputStream os =null;
        try {
            socket = new Socket(targetIP,targetPort);

            fis = new FileInputStream(mFile);
            os = socket.getOutputStream();
            byte[] buf = new byte[512];
            int len = 0 ;
            float progress=0;
            long fileLength = mFile.length();

            Log.i("tcpSend","mFile size--"+mFile.length()+"byte");
            while((len=fis.read(buf))!=-1){
                os.write(buf,0,len);
                progress+=len;
                float sumProgress = (progress / fileLength)*100;
                Message msg=mHandler.obtainMessage();
                msg.arg1= (int) sumProgress;

                mHandler.sendMessage(msg);
            }
            Log.i("tcpSend","send file done");
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
        }

    }


}
