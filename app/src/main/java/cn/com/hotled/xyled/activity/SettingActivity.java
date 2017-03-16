package cn.com.hotled.xyled.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.hotled.xyled.App;
import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.adapter.CardSeriesAdapter;
import cn.com.hotled.xyled.bean.LedCard;
import cn.com.hotled.xyled.bean.TraceFile;
import cn.com.hotled.xyled.dao.TraceFileDao;
import cn.com.hotled.xyled.global.Common;
import cn.com.hotled.xyled.global.Global;
import cn.com.hotled.xyled.util.communicate.ReadScreenDataUtil;
import cn.com.hotled.xyled.util.communicate.SendSetDataUtil;

import static cn.com.hotled.xyled.global.Global.CONNECT_TIMEOUT;
import static cn.com.hotled.xyled.global.Global.PAUSE_FAILE;
import static cn.com.hotled.xyled.global.Global.READ_FAILE;
import static cn.com.hotled.xyled.global.Global.READ_SUCCESS;
import static cn.com.hotled.xyled.global.Global.SEND_DONE;
import static cn.com.hotled.xyled.global.Global.WIFI_ERRO;

public class SettingActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private static final int SELECT_TRACE_CODE = 303;
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
    @BindView(R.id.bt_send_config)
    Button mBtSendConfig;
    @BindView(R.id.bt_read_config)
    Button mBtReadConfig;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private TraceFileDao mTraceFileDao;
    private boolean isChinese;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        initView();
        loadData();
    }

    private void loadData() {
        mTraceFileDao = ((App) getApplication()).getDaoSession().getTraceFileDao();
        mSharedPreferences = getSharedPreferences(Global.SP_SCREEN_CONFIG, MODE_PRIVATE);
        int langInt = mSharedPreferences.getInt(Global.KEY_LANGUAGE, 0);
        isChinese=(langInt==1);
        mEditor = mSharedPreferences.edit();
        String cardSeries = mSharedPreferences.getString(Global.KEY_CARD_SERIES, getString(R.string.default_card));
        int width = mSharedPreferences.getInt(Global.KEY_SCREEN_W, 64);
        int height = mSharedPreferences.getInt(Global.KEY_SCREEN_H, 32);
        long traceSelectedId = mSharedPreferences.getLong(Global.KEY_TRACE_SELECT, -1);
        int rgbOrder = mSharedPreferences.getInt(Global.KEY_RGB_ORDER, 0);
        int data = mSharedPreferences.getInt(Global.KEY_DATA, 0);
        int oe = mSharedPreferences.getInt(Global.KEY_OE, 0);
        int code = mSharedPreferences.getInt(Global.KEY_138CODE, 0);
        if (traceSelectedId != -1) {
            List<TraceFile> list = mTraceFileDao.queryBuilder().where(TraceFileDao.Properties.Id.eq(traceSelectedId)).list();
            TraceFile traceFile = list.get(0);
            if (isChinese) {
                mTvSetTrace.setText(traceFile.getTraceLineFile_zh());
            }else{
                mTvSetTrace.setText(traceFile.getTraceLineFile_en());
            }
        }

        mTvSetCardSeries.setText(cardSeries);
        mTvSetScreenWidth.setText(String.valueOf(width));
        mTvSetScreenHeight.setText(String.valueOf(height));
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
        mSpnRgbSequence.setOnItemSelectedListener(this);
        mSpnSetData.setOnItemSelectedListener(this);
        mSpnSetOe.setOnItemSelectedListener(this);
        mSpnSet138code.setOnItemSelectedListener(this);
        mBtSendConfig.setOnClickListener(this);
        mBtReadConfig.setOnClickListener(this);
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
                Intent intent = new Intent(this, TraceSetActivity.class);
                long traceId = mSharedPreferences.getLong(Global.KEY_TRACE_SELECT, -1);
                intent.putExtra(Common.EX_id,traceId);
                startActivityForResult(intent, SELECT_TRACE_CODE);
                break;
            case R.id.rl_set_advanceSet:
                startActivity(new Intent(this, AdvanceSetActivity.class));
                break;
            case R.id.bt_send_config:
                long traceSelectedId = mSharedPreferences.getLong(Global.KEY_TRACE_SELECT, -1);
                if (traceSelectedId != -1) {
                    List<TraceFile> list = mTraceFileDao.queryBuilder().where(TraceFileDao.Properties.Id.eq(traceSelectedId)).list();
                    TraceFile traceFile = list.get(0);
                    SendSetDataUtil sendSetDataUtil = new SendSetDataUtil(this, traceFile, mHandler);
                    sendSetDataUtil.startSendData();
                    sendSetDataUtil.testPrintData();
                } else {
                    Snackbar.make(mSpnSet138code, R.string.tos_select_trace, Snackbar.LENGTH_SHORT).show();
                }
                break;
            case R.id.bt_read_config:
                ReadScreenDataUtil readUtil =new ReadScreenDataUtil(this,mHandler);
                readUtil.startReadData();
                break;
        }
    }

    private void selectCard() {
        View view = getLayoutInflater().from(this).inflate(R.layout.recycler_vier, null);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.typeFaceListView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final List<LedCard> cards = new ArrayList<LedCard>();
        cards.add(new LedCard("HC-1"));
        cards.add(new LedCard("HC-2"));
        CardSeriesAdapter cardSeriesAdapter = new CardSeriesAdapter(cards, this);
        recyclerView.setAdapter(cardSeriesAdapter);
        cardSeriesAdapter.setOnItemClickListener(new CardSeriesAdapter.OnItemOnClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mTvSetCardSeries.setText(cards.get(position).getCardName());
                mEditor.putString(Global.KEY_CARD_SERIES, cards.get(position).getCardName()).apply();
            }
        });
        new AlertDialog.Builder(this)
                .setTitle(R.string.title_select_card)
                .setView(view)
                .setPositiveButton(R.string.msg_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setNegativeButton(R.string.msg_cancle, null)
                .show();
    }

    private void setScreenHeightOrWidth(final boolean isWidth) {
        String title = null;
        String prefill = null;
        String hint = null;
        if (isWidth) {
            title = getString(R.string.screen_width_set);
            prefill = mTvSetScreenWidth.getText().toString();
            hint = getString(R.string.screen_width_setHint);
        } else {
            title = getString(R.string.screen_height_set);
            prefill = mTvSetScreenHeight.getText().toString();
            hint = getString(R.string.screen_height_setHint);
        }
        MaterialDialog.Builder builder = new MaterialDialog.Builder(this);
        builder.title(title)
                .inputType(InputType.TYPE_CLASS_NUMBER)
                .input(hint, prefill, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        int result = Integer.parseInt(input.toString());
                        int width = mSharedPreferences.getInt(Global.KEY_SCREEN_W, -1);
                        int height = mSharedPreferences.getInt(Global.KEY_SCREEN_H, -1);
                        if (isWidth) {
                            if (result>1024) {
                                Toast.makeText(SettingActivity.this, R.string.tos_wrong_width, Toast.LENGTH_LONG).show();
                                return;
                            }
                            if (result*height>26000){
                                Toast.makeText(SettingActivity.this, R.string.tos_wrong_resolution, Toast.LENGTH_LONG).show();
                            }else {
                                mTvSetScreenWidth.setText(input);
                                mEditor.putInt(Global.KEY_SCREEN_W, result).apply();
                            }
                        } else {
                            if (result>1024) {
                                Toast.makeText(SettingActivity.this, R.string.tos_wrong_width, Toast.LENGTH_LONG).show();
                                return;
                            }
                            if (result*width>26000){
                                Toast.makeText(SettingActivity.this, R.string.tos_wrong_resolution, Toast.LENGTH_LONG).show();
                            }else {
                                mTvSetScreenHeight.setText(input);
                                mEditor.putInt(Global.KEY_SCREEN_H, result).apply();
                            }
                        }
                    }
                })
                .positiveText(R.string.msg_confirm)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                    }
                })
                .negativeText(R.string.msg_cancle)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_TRACE_CODE && resultCode == RESULT_OK) {
            long longExtra = data.getLongExtra(Common.EX_selectTrace, -1);
            if (longExtra != -1) {
                List<TraceFile> list = mTraceFileDao.queryBuilder().where(TraceFileDao.Properties.Id.eq(longExtra)).list();
                TraceFile traceFile = list.get(0);
                if (isChinese) {
                    mTvSetTrace.setText(traceFile.getTraceLineFile_zh());
                }else{
                    mTvSetTrace.setText(traceFile.getTraceLineFile_en());
                }
                mEditor.putLong(Global.KEY_TRACE_SELECT, longExtra).apply();
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spn_rgbSequence:
                mEditor.putInt(Global.KEY_RGB_ORDER, position).apply();
                break;
            case R.id.spn_set_data:
                mEditor.putInt(Global.KEY_DATA, position).apply();
                break;
            case R.id.spn_set_oe:
                mEditor.putInt(Global.KEY_OE, position).apply();
                break;
            case R.id.spn_set_138code:
                mEditor.putInt(Global.KEY_138CODE, position).apply();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WIFI_ERRO:
                    Toast.makeText(SettingActivity.this, R.string.tos_wifi_switch, Toast.LENGTH_LONG).show();
                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    break;
                case CONNECT_TIMEOUT:
                    Snackbar.make(mSpnSet138code, R.string.tos_wifi_timeout, Snackbar.LENGTH_SHORT).show();
                    break;
                case PAUSE_FAILE:
                    Snackbar.make(mSpnSet138code, R.string.tos_screen_noresponse, Snackbar.LENGTH_SHORT).show();
                    break;
                case SEND_DONE:
                    Snackbar.make(mSpnSet138code, R.string.tos_send_config_success, Snackbar.LENGTH_SHORT).show();
                    break;
                case READ_SUCCESS:
                    Snackbar.make(mSpnSet138code, R.string.tos_read_config_success, Snackbar.LENGTH_SHORT).show();
                    loadData();
                    break;
                case READ_FAILE:
                    Snackbar.make(mSpnSet138code, R.string.tos_read_fail, Snackbar.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    public void onCreateCustomToolBar(Toolbar toolbar) {
        toolbar.setTitle(R.string.screenSet_title);

    }
}
