package cn.com.hotled.xyled.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.greenrobot.greendao.database.Database;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.hotled.xyled.App;
import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.global.Common;

import static cn.com.hotled.xyled.global.Common.FL_FLOW_DIR;
import static cn.com.hotled.xyled.global.Common.FL_TRACE_DIR;
import static cn.com.hotled.xyled.global.Global.KEY_IS_FIRSTIN;
import static cn.com.hotled.xyled.global.Global.SP_SYSTEM_CONFIG;

public class WelcomeActivity extends BaseAppCompatAtivity {
    private static final int COPY_FINISH = 200;
    private static final int START_ACTIVITY = 300;
    private static final int COPY_PROGRESS = 400;
    private static final int UPDATE_FILE_NAME = 500;
    private static final int READY_DB = 600;
    @BindView(R.id.pb_copy)
    ProgressBar mPbCopy;
    @BindView(R.id.tv_copy_state)
    TextView mTvCopyState;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case COPY_FINISH:
                    mSharePref.edit().putBoolean(SP_SYSTEM_CONFIG, false).apply();
                    countDownAndEnter();
                    break;
                case START_ACTIVITY:
                    if (mIsFirst) {
                        Intent intent = new Intent(WelcomeActivity.this, ConnectCardActivity.class);
                        intent.setType(Common.Type_Forward);
                        startActivity(intent);
                        finish();
                    }else {
                        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                        finish();
                    }
                    break;
                case COPY_PROGRESS:
                    copyCount++;
                    float progress = (copyCount / mFilesLength) * 100;
                    mPbCopy.setProgress((int)progress);
                    break;
                case UPDATE_FILE_NAME:
                    String fileName = msg.getData().getString("fileName", "...");
                    mTvCopyState.setText(getString(R.string.initialize)+fileName);
                    break;
                case READY_DB:
                    mTvCopyState.setText(R.string.completing);
                    break;
            }
        }
    };
    private SharedPreferences mSharePref;
    private float mFilesLength;
    private float copyCount;
    private boolean mIsFirst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_guide);
        ButterKnife.bind(this);
        initView();
        loadData();
    }

    private void loadData() {
        mSharePref = getSharedPreferences(SP_SYSTEM_CONFIG, MODE_PRIVATE);

        boolean isFirstIn = mSharePref.getBoolean(KEY_IS_FIRSTIN, true);
        mIsFirst = isFirstIn;
        File flowFileDir = new File(getFilesDir() + FL_FLOW_DIR);
        File traceFileDir = new File(getFilesDir() + FL_TRACE_DIR);
        if (!flowFileDir.exists()) {
            flowFileDir.mkdir();
        }
        if (!traceFileDir.exists()) {
            traceFileDir.mkdir();
        }
        if (isFirstIn) {
            mTvCopyState.setVisibility(View.VISIBLE);
            mPbCopy.setVisibility(View.VISIBLE);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    copyFilesFassets("flow", getFilesDir() + FL_FLOW_DIR);
                    copyFilesFassets("trace", getFilesDir() + FL_TRACE_DIR);
                    insertToDb();
                    Message message = mHandler.obtainMessage();
                    message.what = COPY_FINISH;
                    mHandler.sendMessage(message);
                    mSharePref.edit().putBoolean(KEY_IS_FIRSTIN, false).apply();
                }
            }).start();

        } else {
            countDownAndEnter();
        }

    }

    private void insertToDb() {
        Database db = ((App) getApplication()).getDb();
        InputStream is = null;
        ByteArrayOutputStream bos = null;
        try {
            is = getAssets().open("file/trace_file.sql");
            bos = new ByteArrayOutputStream();
            byte[] data = new byte[1024];
            int length = -1;
            while ((length = is.read(data)) != -1) {
                bos.write(data, 0, length);
            }
            bos.flush();
            String sql = bos.toString();
            db.beginTransaction();
            String[] sqls = sql.split(";");
            for (int i = 0; i < sqls.length; i++) {
                String s = sqls[i] += ";";
                db.execSQL(s);
            }
            db.setTransactionSuccessful();
            db.endTransaction();
            mHandler.sendEmptyMessage(READY_DB);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void countDownAndEnter() {
        mHandler.sendEmptyMessageDelayed(START_ACTIVITY, 1000);
    }

    private void initView() {

    }

    /**
     * 从assets目录中复制整个文件夹内容
     *
     * @param oldPath String  原文件路径  如：/aa
     * @param newPath String  复制后路径  如：xx:/bb/cc
     */
    public void copyFilesFassets(String oldPath, String newPath) {
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            String fileNames[] = getAssets().list(oldPath);//获取assets目录下的所有文件及目录名
            if (fileNames.length > 0) {//如果是目录
                File file = new File(newPath);
                file.mkdirs();//如果文件夹不存在，则递归
                mFilesLength = fileNames.length;
                copyCount=0;
                for (String fileName : fileNames) {
                    Message msg = mHandler.obtainMessage();
                    Bundle bundle = new Bundle();
                    String substring = fileName.substring(0,fileName.lastIndexOf("."));
                    bundle.putString("fileName",substring);
                    msg.setData(bundle);
                    msg.what=UPDATE_FILE_NAME;
                    mHandler.sendMessage(msg);
                    copyFilesFassets(oldPath + "/" + fileName, newPath + "/" + fileName);
                }
            } else {//如果是文件
                is = getAssets().open(oldPath);
                fos = new FileOutputStream(new File(newPath));
                byte[] buffer = new byte[1024];
                int byteCount = 0;
                while ((byteCount = is.read(buffer)) != -1) {//循环从输入流读取 buffer字节
                    fos.write(buffer, 0, byteCount);//将读取的输入流写入到输出流
                }
                fos.flush();//刷新缓冲区
               mHandler.sendEmptyMessage(COPY_PROGRESS);
            }
        } catch (Exception e) {
            e.printStackTrace();
            //如果捕捉到错误则通知UI线程
        } finally {
            if (is != null) {
                try {
                    is.close();
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
