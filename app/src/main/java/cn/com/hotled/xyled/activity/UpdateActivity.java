package cn.com.hotled.xyled.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.hotled.xyled.R;

public class UpdateActivity extends AppCompatActivity {

    @BindView(R.id.tv_result)
    TextView mTvResult;
    @BindView(R.id.bt_check)
    Button mBtCheck;
    String mResult;

    Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            mTvResult.setText(mResult);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.bt_check)
    public void onClick() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                connectServer();
            }
        }).start();
    }

    private void connectServer() {
            HttpURLConnection conn = null;
        try {
            URL url = new URL("http://192.168.1.103:8080/update/check.txt");
//            URL url = new URL("http://www.baidu.com");
            conn = (HttpURLConnection) url.openConnection();

            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.addRequestProperty("Content-Type","text/html; charset=utf-8");
            conn.connect();
            int responseCode = conn.getResponseCode();
            byte[] result = new byte[512];
            if (responseCode==conn.HTTP_OK){
                Log.i("update",responseCode+"");
                InputStream inputStream = conn.getInputStream();
                ByteArrayOutputStream baos=new ByteArrayOutputStream();
                int length=0;
                while((length = inputStream.read(result))!=-1){
                    baos.write(result,0,length);
                }
                mResult=baos.toString("UTF-8");
                Log.i("update",baos.toString());
                mHandler.sendEmptyMessage(100);

            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            conn.disconnect();
            Log.i("update","conn.disconnect()");
        }
    }
}
