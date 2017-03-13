package cn.com.hotled.xyled.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.global.Global;

public class AdvanceSetActivity extends BaseActivity implements AdapterView.OnItemSelectedListener{

    @BindView(R.id.spn_set_dataOrientation)
    Spinner mSpnSetDataOrientation;
    @BindView(R.id.spn_set_special)
    Spinner mSpnSetSpecial;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

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
    }

    private void loadData() {
        mSharedPreferences = getSharedPreferences(Global.SP_SCREEN_CONFIG, MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        int dataOrient = mSharedPreferences.getInt(Global.KEY_DATA_ORIENTATION, 0);
        int special = mSharedPreferences.getInt(Global.KEY_SPECIAL, 0);
        mSpnSetDataOrientation.setSelection(dataOrient);
        mSpnSetSpecial.setSelection(special);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spn_set_dataOrientation:
                mEditor.putInt(Global.KEY_DATA_ORIENTATION,position).apply();
                break;
            case R.id.spn_set_special:
                mEditor.putInt(Global.KEY_SPECIAL,position).apply();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
