package cn.com.hotled.xyled.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.util.android.UpdateUtil;

public class AboutUsActivity extends BaseActivity {

    private static final int UPDATE_CODE = 100;
    private static final int IS_LATEST = 200;
    String mResult;
    UpdateInfo mUpdateInfo;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_CODE:
                    showAlertDialog();
                    break;
                case IS_LATEST:
                    Toast.makeText(AboutUsActivity.this, "你的版本已是最新版", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    @BindView(R.id.tv_update_check)
    TextView mTvUpdateCheck;
    @BindView(R.id.tv_current_version)
    TextView mTvCurrentVersion;

    private void showAlertDialog() {
        new AlertDialog.Builder(this)
                .setTitle("发现新版本")
                .setMessage(mUpdateInfo.whatsNews)
                .setPositiveButton("赶紧升级", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(AboutUsActivity.this, "开始下载更新", Toast.LENGTH_SHORT).show();
                        UpdateUtil.download(AboutUsActivity.this, mUpdateInfo.downloadUrl, "led新版本");
                    }
                })
                .setNegativeButton("忽略", null)
                .show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        ButterKnife.bind(this);
        setVersion();
    }

    private void setVersion() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            mTvCurrentVersion.setText("版本"+packageInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void connectServer() {
        HttpURLConnection conn = null;
        try {
            URL url = new URL("http://192.168.1.106:8080/update/updateInfo.json");
            conn = (HttpURLConnection) url.openConnection();

            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setRequestProperty("contentType", "utf-8");
            conn.setRequestProperty("Accept-Charset", "utf-8");
            conn.setRequestProperty("Content-type", "text/html");
            conn.connect();
            int responseCode = conn.getResponseCode();
            byte[] result = new byte[512];
            if (responseCode == conn.HTTP_OK) {
                Log.i("update", responseCode + "");
                InputStream inputStream = conn.getInputStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = in.readLine()) != null) {
                    buffer.append(line);
                }
                mResult = buffer.toString();

                Log.i("update", buffer.toString());
                JSONObject jsonObject = new JSONObject(buffer.toString());
                int version = jsonObject.getInt("version");
                String name = jsonObject.getString("name");
                String downloadUrl = jsonObject.getString("url");
                String whatsnew = jsonObject.getString("whatsnew");
                mUpdateInfo = new UpdateInfo(version, name, downloadUrl, whatsnew);
                Log.i("update", "version=" + version);
                Log.i("update", "name=" + name);
                Log.i("update", "downloadUrl=" + downloadUrl);
                Log.i("update", "whatsnew=" + whatsnew);
                boolean needToUpdate = compareVersion(version);
                if (needToUpdate) {
                    mHandler.sendEmptyMessage(UPDATE_CODE);
                } else {
                    mHandler.sendEmptyMessage(IS_LATEST);
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
            Log.i("update", "conn.disconnect()");
        }
    }


    private boolean compareVersion(int versionCode) {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            Log.i("about", "version=" + packageInfo.versionCode);
            Log.i("about", "versionName=" + packageInfo.versionName);
            if (versionCode > packageInfo.versionCode) {
                return true;
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    @OnClick(R.id.tv_update_check)
    public void onClick() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                connectServer();
            }
        }).start();
    }

    class UpdateInfo {
        int version;
        String name;
        String downloadUrl;
        String whatsNews;

        public UpdateInfo(int version, String name, String downloadUrl, String whatsNews) {
            this.version = version;
            this.name = name;
            this.downloadUrl = downloadUrl;
            this.whatsNews = whatsNews;
        }

    }
}
