package cn.com.hotled.xyled.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.global.Global;

public class AdvanceSetActivity extends BaseActivity implements AdapterView.OnItemSelectedListener,SeekBar.OnSeekBarChangeListener {

    @BindView(R.id.spn_set_dataOrientation)
    Spinner mSpnSetDataOrientation;
    @BindView(R.id.spn_set_special)
    Spinner mSpnSetSpecial;
    @BindView(R.id.iv_set_brightness)
    ImageView mIvSetBrightness;
    @BindView(R.id.sb_set_brightness)
    SeekBar mSbSetBrightness;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private int mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advance_set);
        ButterKnife.bind(this);
        initView();
        loadData();
    }

    private void initView() {
        mSpnSetDataOrientation.setOnItemSelectedListener(this);
        mSpnSetSpecial.setOnItemSelectedListener(this);
        mSbSetBrightness.setOnSeekBarChangeListener(this);

    }

    private void loadData() {
        mSharedPreferences = getSharedPreferences(Global.SP_SCREEN_CONFIG, MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        int dataOrient = mSharedPreferences.getInt(Global.KEY_DATA_ORIENTATION, 0);
        int special = mSharedPreferences.getInt(Global.KEY_SPECIAL, 0);
        int brightness=mSharedPreferences.getInt(Global.KEY_BRIGHTNESS,66);
        mSpnSetDataOrientation.setSelection(dataOrient);
        mSpnSetSpecial.setSelection(special);
        mSbSetBrightness.setProgress(brightness);
        if (brightness<=33){
            mIvSetBrightness.setImageResource(R.drawable.ic_brightness_low_red_700_24dp);
        }else if (brightness>33&&brightness<=66){
            mIvSetBrightness.setImageResource(R.drawable.ic_brightness_medium_blue_700_24dp);
        }else if (brightness>66&&brightness<=100){
            mIvSetBrightness.setImageResource(R.drawable.ic_brightness_high_yellow_700_24dp);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spn_set_dataOrientation:
                mEditor.putInt(Global.KEY_DATA_ORIENTATION, position).apply();
                break;
            case R.id.spn_set_special:
                mEditor.putInt(Global.KEY_SPECIAL, position).apply();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser){
            if (progress<=33){
                seekBar.setProgress(33);
                mIvSetBrightness.setImageResource(R.drawable.ic_brightness_low_red_700_24dp);
                mProgress = 33;
            }else if (progress>33&&progress<=66){
                seekBar.setProgress(66);
                mIvSetBrightness.setImageResource(R.drawable.ic_brightness_medium_blue_700_24dp);
                mProgress = 66;
            }else if (progress>66&&progress<=100){
                mIvSetBrightness.setImageResource(R.drawable.ic_brightness_high_yellow_700_24dp);
                seekBar.setProgress(100);
                mProgress = 100;
            }

        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mEditor.putInt(Global.KEY_BRIGHTNESS, mProgress).apply();
    }
}
