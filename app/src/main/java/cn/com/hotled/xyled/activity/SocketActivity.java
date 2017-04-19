package cn.com.hotled.xyled.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.adapter.MessageAdapter;
import cn.com.hotled.xyled.bean.SocketMessage;
import cn.com.hotled.xyled.util.communicate.SendCmdUtil;

import static cn.com.hotled.xyled.global.Global.PAUSE_OK;
import static cn.com.hotled.xyled.global.Global.RESET_OK;
import static cn.com.hotled.xyled.global.Global.RESUME_OK;
import static cn.com.hotled.xyled.global.Global.SOCKET_ERRO;
import static cn.com.hotled.xyled.global.Global.TEST_OK;
import static cn.com.hotled.xyled.global.Global.WIFI_ERRO;

public class SocketActivity extends BaseActivity implements View.OnClickListener{

    private static final int RESET_COOLED = 333;
    @BindView(R.id.rv_message)
    RecyclerView mRecyclerView;
    @BindView(R.id.socket_test)
    Button btTest;
    @BindView(R.id.socket_pause)
    Button btPause;
    @BindView(R.id.socket_rest)
    Button btReset;
    @BindView(R.id.socket_resume)
    Button btResume;

    private RecievMsgHandler msgHandler;
    private ArrayList<SocketMessage> mMessageList;
    private MessageAdapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private SendCmdUtil mSendCmdUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        msgHandler = new RecievMsgHandler();
        mMessageList = new ArrayList<>();
        mAdapter = new MessageAdapter(this, mMessageList);
        mSendCmdUtil = new SendCmdUtil(this,msgHandler);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        btPause.setOnClickListener(this);
        btTest.setOnClickListener(this);
        btReset.setOnClickListener(this);
        btResume.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.socket_pause:
                mSendCmdUtil.sendCmd(SendCmdUtil.Cmd.Pause);
                SocketMessage pauseMSG = new SocketMessage(getString(R.string.pause),System.currentTimeMillis(),true,false);
                mMessageList.add(pauseMSG);
                break;
            case R.id.socket_rest:
                mSendCmdUtil.sendCmd(SendCmdUtil.Cmd.Reset);
                SocketMessage reSetMSG = new SocketMessage(getString(R.string.reset),System.currentTimeMillis(),true,false);
                mMessageList.add(reSetMSG);
                msgHandler.sendEmptyMessageDelayed(RESET_COOLED,1000);
                btReset.setEnabled(false);
                break;
            case R.id.socket_resume:
                mSendCmdUtil.sendCmd(SendCmdUtil.Cmd.Resume);
                SocketMessage resumeMSG = new SocketMessage(getString(R.string.resume),System.currentTimeMillis(),true,false);
                mMessageList.add(resumeMSG);
                break;
            case R.id.socket_test:
                mSendCmdUtil.sendCmd(SendCmdUtil.Cmd.Test);
                SocketMessage testMSG = new SocketMessage(getString(R.string.test),System.currentTimeMillis(),true,false);
                mMessageList.add(testMSG);
                break;
        }
        mAdapter.notifyItemInserted(mMessageList.size());
        mRecyclerView.smoothScrollToPosition(mMessageList.size());
    }


    class RecievMsgHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            SocketMessage smsg =null;
            switch (msg.what){
                case SOCKET_ERRO:
                    Bundle bundle = msg.getData();
                    String error1 = bundle.getString("error");
                    String message = "";

                    if (error1.contains("ECONNREFUSED")) {
                        message= getString(R.string.refuse_connect);//android.system.ErrnoException: connect failed: ECONNREFUSED (Connection refused)
                    }else {
                        message= getString(R.string.tos_wifi_timeout);
                    }
                    SocketMessage error=new SocketMessage(message,System.currentTimeMillis(),false,true);
                    mMessageList.add(error);
                    mAdapter.notifyItemInserted(mMessageList.size());
                    break;
                case WIFI_ERRO:
                    Toast.makeText(SocketActivity.this,R.string.tos_wifi_switch,Toast.LENGTH_LONG).show();
                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    break;
                case TEST_OK:
                    smsg=new SocketMessage(getString(R.string.testSuccess),System.currentTimeMillis(),true,true);
                    mMessageList.add(smsg);
                    mAdapter.notifyItemInserted(mMessageList.size());
                    break;
                case PAUSE_OK:
                    smsg=new SocketMessage(getString(R.string.pauseSuccess),System.currentTimeMillis(),true,true);
                    mMessageList.add(smsg);
                    mAdapter.notifyItemInserted(mMessageList.size());
                    break;
                case RESUME_OK:
                    smsg=new SocketMessage(getString(R.string.resumeSuccess),System.currentTimeMillis(),true,true);
                    mMessageList.add(smsg);
                    mAdapter.notifyItemInserted(mMessageList.size());
                    break;
                case RESET_OK:
                    smsg=new SocketMessage(getString(R.string.resetSuccess),System.currentTimeMillis(),true,true);
                    mMessageList.add(smsg);
                    mAdapter.notifyItemInserted(mMessageList.size());
                    break;
                case RESET_COOLED:
                    btReset.setEnabled(true);
                    break;
            }
            mAdapter.notifyDataSetChanged();
            mRecyclerView.smoothScrollToPosition(mMessageList.size());
        }
    }

    @Override
    public void onCreateCustomToolBar(Toolbar toolbar) {
        toolbar.setTitle(R.string.test_connet);
    }
}
