package cn.com.hotled.xyled.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.adapter.CardSeriesAdapter;
import cn.com.hotled.xyled.bean.LedCard;
import cn.com.hotled.xyled.bean.LedScreen;

public class AddScreenActivity extends BaseActivity {

    @BindView(R.id.et_addscreen_screenName)
    TextInputEditText etScreenName;
    @BindView(R.id.et_addscreen_address)
    TextInputEditText etAddress;
    @BindView(R.id.et_addscreen_width)
    TextInputEditText etWidth;
    @BindView(R.id.et_addscreen_height)
    TextInputEditText etHeight;
    @BindView(R.id.bt_addscreen_save)
    Button btSave;
    @BindView(R.id.ll_addscreen_card)
    RelativeLayout llCardSerial;
    @BindView(R.id.tv_addscreen_cardName)
    TextView tvCardName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_screen);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {

        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInput();
            }
        });
        llCardSerial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getLayoutInflater().from(AddScreenActivity.this).inflate(R.layout.typeface_list, null);
                RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.typeFaceListView);
                recyclerView.setLayoutManager(new LinearLayoutManager(AddScreenActivity.this));
                final List<LedCard> cards = new ArrayList<LedCard>();
                cards.add(new LedCard("HC-1"));
                cards.add(new LedCard("HC-1S"));
                cards.add(new LedCard("HC-2"));
                CardSeriesAdapter cardSeriesAdapter = new CardSeriesAdapter(cards, AddScreenActivity.this);
                recyclerView.setAdapter(cardSeriesAdapter);
                cardSeriesAdapter.setOnItemClickListener(new CardSeriesAdapter.OnItemOnClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        tvCardName.setText(cards.get(position).getCardName());
                    }
                });
                new AlertDialog.Builder(AddScreenActivity.this)
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
        });
    }

    private void checkInput() {
        String screenName = etScreenName.getText().toString();
        String address = etAddress.getText().toString();
        String widthStr = etWidth.getText().toString();
        String heightStr = etHeight.getText().toString();
        if (TextUtils.isEmpty(screenName)){
            Snackbar.make(etScreenName,"名称不能为空",Snackbar.LENGTH_LONG).show();
            return;
        }
        // TODO: 2016/12/7 宽度高度需要校验，宽度不能超过多少，高度不能超过多少
        if (TextUtils.isEmpty(widthStr)){
            Snackbar.make(etScreenName,"屏幕宽度不能为空",Snackbar.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(heightStr)){
            Snackbar.make(etScreenName,"屏幕高度不能为空",Snackbar.LENGTH_LONG).show();
            return;
        }

        LedScreen screen=new LedScreen(screenName,Integer.parseInt(widthStr),Integer.parseInt(heightStr),tvCardName.getText().toString(),null);
        Intent intent = new Intent();
        intent.putExtra("screen",screen);
        setResult(RESULT_OK,intent);
        onBackPressed();
    }
}
