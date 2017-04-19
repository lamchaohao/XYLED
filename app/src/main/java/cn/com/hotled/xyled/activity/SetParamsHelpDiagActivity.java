package cn.com.hotled.xyled.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.util.communicate.ReadScreenDataUtil;

import static cn.com.hotled.xyled.global.Global.READ_CONFIG_CODE;
import static cn.com.hotled.xyled.global.Global.READ_FAILE;
import static cn.com.hotled.xyled.global.Global.READ_SUCCESS;
import static cn.com.hotled.xyled.global.Global.WIFI_ERRO;

public class SetParamsHelpDiagActivity extends Activity {

    @BindView(R.id.bt_setParam_enterSet)
    Button mBtSetParamEnterSet;
    @BindView(R.id.bt_setParam_readback)
    Button mBtSetParamReadback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_params_help);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.bt_setParam_enterSet, R.id.bt_setParam_readback})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_setParam_enterSet:
                startActivity(new Intent(this, SettingActivity.class));
                break;
            case R.id.bt_setParam_readback:

                mHandler.sendEmptyMessageDelayed(READ_CONFIG_CODE,3000);
                Toast.makeText(this,R.string.tos_reading,Toast.LENGTH_LONG).show();
                break;
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WIFI_ERRO:
                    Toast.makeText(SetParamsHelpDiagActivity.this, R.string.tos_disConnect_screen, Toast.LENGTH_SHORT).show();
                    break;
                case READ_SUCCESS:
                    Toast.makeText(SetParamsHelpDiagActivity.this, R.string.tos_refresh_success, Toast.LENGTH_SHORT).show();
                    break;
                case READ_FAILE:
                    Toast.makeText(SetParamsHelpDiagActivity.this, R.string.tos_screen_noresponse, Toast.LENGTH_SHORT).show();
                    break;
                case READ_CONFIG_CODE:
                    ReadScreenDataUtil readUtil = new ReadScreenDataUtil(SetParamsHelpDiagActivity.this, mHandler);
                    readUtil.startReadData();
                    break;
            }
        }
    };
}
