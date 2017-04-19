package cn.com.hotled.xyled.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.global.Global;

import static cn.com.hotled.xyled.global.Global.KEY_AUTO_SEND;

public class ConnectedSendSettingActivity extends BaseActivity {

    private SharedPreferences.Editor mEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connected_send_setting);
        initView();
    }

    private void initView() {
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.rg_auto);
        RadioButton moveLeft = (RadioButton) findViewById(R.id.rb_auto_send);
        RadioButton rightAndleft = (RadioButton) findViewById(R.id.rb_auto_notSend);

        SharedPreferences sp = getSharedPreferences(Global.SP_SYSTEM_CONFIG, MODE_PRIVATE);
        mEdit = sp.edit();
        if (sp.getBoolean(KEY_AUTO_SEND,true)) {
            moveLeft.setChecked(true);
        }else{
            rightAndleft.setChecked(true);
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_auto_send:
                        mEdit.putBoolean(KEY_AUTO_SEND,true).apply();
                        break;
                    case R.id.rb_auto_notSend:
                        mEdit.putBoolean(KEY_AUTO_SEND,false).apply();
                        break;
                }
            }
        });
    }
    @Override
    public void onCreateCustomToolBar(Toolbar toolbar) {
        super.onCreateCustomToolBar(toolbar);
        toolbar.setTitle(R.string.connected_display);
    }
}
