package cn.com.hotled.xyled.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.adapter.CardSeriesAdapter;
import cn.com.hotled.xyled.bean.LedCard;
import cn.com.hotled.xyled.global.Global;

public class SettingActivity extends BaseActivity implements View.OnClickListener,AdapterView.OnItemSelectedListener{
    @BindView(R.id.tv_set_cardSeries)
    TextView mTvSetCardSeries;
    @BindView(R.id.rl_set_cardSeries)
    RelativeLayout mRlSetCardSeries;
    @BindView(R.id.tv_set_screenWidth)
    TextView mTvSetScreenWidth;
    @BindView(R.id.rl_set_screenWidth)
    RelativeLayout mRlSetScreenWidth;
    @BindView(R.id.tv_set_screenHeight)
    TextView mTvSetScreenHeight;
    @BindView(R.id.rl_set_screenHeight)
    RelativeLayout mRlSetScreenHeight;
    @BindView(R.id.tv_set_trace)
    TextView mTvSetTrace;
    @BindView(R.id.rl_set_trace)
    RelativeLayout mRlSetTrace;
    @BindView(R.id.rl_set_advanceSet)
    RelativeLayout mRlSetAdvanceSet;
    @BindView(R.id.spn_set_dataOrientation)
    Spinner mSpnSetDataOrientation;
    @BindView(R.id.spn_set_special)
    Spinner mSpnSetSpecial;
    @BindView(R.id.spn_rgbSequence)
    Spinner mSpnRgbSequence;
    @BindView(R.id.spn_set_data)
    Spinner mSpnSetData;
    @BindView(R.id.spn_set_oe)
    Spinner mSpnSetOe;
    @BindView(R.id.spn_set_138code)
    Spinner mSpnSet138code;
    @BindView(R.id.activity_setting)
    ScrollView mActivitySetting;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        initView();
        loadData();
    }

    private void loadData() {
        mSharedPreferences = getSharedPreferences(Global.SP_SCREEN_CONFIG, MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        String cardSeries = mSharedPreferences.getString(Global.KEY_CARD_SERIES, "HC-1");
        int width = mSharedPreferences.getInt(Global.KEY_SCREEN_W, 64);
        int height = mSharedPreferences.getInt(Global.KEY_SCREEN_H, 32);
        String traceSelected = mSharedPreferences.getString(Global.KEY_TRACE_SELECT, "NONE");
        int dataOrient = mSharedPreferences.getInt(Global.KEY_DATA_ORIENTATION, 0);
        int special = mSharedPreferences.getInt(Global.KEY_SPECIAL, 0);
        int rgbOrder = mSharedPreferences.getInt(Global.KEY_RGB_ORDER, 0);
        int data = mSharedPreferences.getInt(Global.KEY_DATA, 0);
        int oe = mSharedPreferences.getInt(Global.KEY_OE, 0);
        int code = mSharedPreferences.getInt(Global.KEY_138CODE, 0);

        mTvSetCardSeries.setText(cardSeries);
        mTvSetScreenWidth.setText(width+"");
        mTvSetScreenHeight.setText(height+"");
        mTvSetTrace.setText(traceSelected);
        mSpnSetDataOrientation.setSelection(dataOrient);
        mSpnSetSpecial.setSelection(special);
        mSpnRgbSequence.setSelection(rgbOrder);
        mSpnSetData.setSelection(data);
        mSpnSetOe.setSelection(oe);
        mSpnSet138code.setSelection(code);
    }

    private void initView() {
        mRlSetCardSeries.setOnClickListener(this);
        mRlSetScreenWidth.setOnClickListener(this);
        mRlSetScreenHeight.setOnClickListener(this);
        mRlSetTrace.setOnClickListener(this);
        mRlSetAdvanceSet.setOnClickListener(this);
        mSpnSetDataOrientation.setOnItemSelectedListener(this);
        mSpnSetSpecial.setOnItemSelectedListener(this);
        mSpnRgbSequence.setOnItemSelectedListener(this);
        mSpnSetData.setOnItemSelectedListener(this);
        mSpnSetOe.setOnItemSelectedListener(this);
        mSpnSet138code.setOnItemSelectedListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_set_cardSeries:
                selectCard();
                break;
            case R.id.rl_set_screenWidth:
                setScreenHeightOrWidth(true);
                break;
            case R.id.rl_set_screenHeight:
                setScreenHeightOrWidth(false);
                break;
            case R.id.rl_set_trace:
                startActivity(new Intent(this,TraceSetActivity.class));
                break;
            case R.id.rl_set_advanceSet:
                break;
        }
    }

    private void selectCard() {
        View view = getLayoutInflater().from(this).inflate(R.layout.typeface_list, null);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.typeFaceListView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final List<LedCard> cards = new ArrayList<LedCard>();
        cards.add(new LedCard("HC-1"));
        cards.add(new LedCard("HC-1S"));
        cards.add(new LedCard("HC-2"));
        CardSeriesAdapter cardSeriesAdapter = new CardSeriesAdapter(cards, this);
        recyclerView.setAdapter(cardSeriesAdapter);
        cardSeriesAdapter.setOnItemClickListener(new CardSeriesAdapter.OnItemOnClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mTvSetCardSeries.setText(cards.get(position).getCardName());
                mEditor.putString(Global.KEY_CARD_SERIES,cards.get(position).getCardName()).apply();
            }
        });
        new AlertDialog.Builder(this)
                .setTitle("选择控制卡")
                .setView(view)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setNegativeButton("cancle",null)
                .show();
    }

    private void setScreenHeightOrWidth(final boolean isWidth) {
        String title =null;
        String prefill=null;
        String hint =null;
        if (isWidth) {
            title = "屏宽设置";
            prefill =mTvSetScreenWidth.getText().toString();
            hint = "请输入屏宽";
        }else {
            title = "屏高设置";
            prefill =mTvSetScreenHeight.getText().toString();
            hint = "请输入屏高";
        }
        MaterialDialog.Builder builder = new MaterialDialog.Builder(this);
        builder.title(title)
                .inputType(InputType.TYPE_CLASS_NUMBER)
                .input(hint, prefill, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        int result = Integer.parseInt(input.toString());
                        if (isWidth) {
                            mTvSetScreenWidth.setText(input);
                            mEditor.putInt(Global.KEY_SCREEN_W,result).apply();
                        }else {
                            mTvSetScreenHeight.setText(input);
                            mEditor.putInt(Global.KEY_SCREEN_H,result).apply();
                        }
                    }
                })
                .positiveText("确定")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                    }
                })
                .negativeText("取消")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                }).show();
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
            case R.id.spn_rgbSequence:
                mEditor.putInt(Global.KEY_RGB_ORDER,position).apply();
                break;
            case R.id.spn_set_data:
                mEditor.putInt(Global.KEY_DATA,position).apply();
                break;
            case R.id.spn_set_oe:
                mEditor.putInt(Global.KEY_OE,position).apply();
                break;
            case R.id.spn_set_138code:
                mEditor.putInt(Global.KEY_138CODE,position).apply();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
