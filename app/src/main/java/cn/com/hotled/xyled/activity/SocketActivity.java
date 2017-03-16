package cn.com.hotled.xyled.activity;

import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.adapter.MessageAdapter;
import cn.com.hotled.xyled.bean.SocketMessage;
import cn.com.hotled.xyled.global.Global;
import cn.com.hotled.xyled.util.android.WifiAdmin;

import static cn.com.hotled.xyled.global.Global.WIFI_ERRO;

public class SocketActivity extends BaseActivity implements View.OnClickListener{

    private static final int ERROR_CODE = 0x100;
    private static final int READ_MSG_CODE = 0x64;
    private static final int ERROR_WIFI = 0x128;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket);
        ButterKnife.bind(this);
        msgHandler = new RecievMsgHandler();
        mMessageList = new ArrayList<>();
        mAdapter = new MessageAdapter(this, mMessageList);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        setOnclick();
    }

    private void setOnclick() {
        btPause.setOnClickListener(this);
        btTest.setOnClickListener(this);
        btReset.setOnClickListener(this);
        btResume.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        WifiAdmin wifiAdmin =new WifiAdmin(this);
        WifiInfo wifiInfo = wifiAdmin.getWifiInfo();
        String ssid = wifiInfo.getSSID();
        String macStr = "";
        boolean startFlag = ssid.contains(Global.SSID_START);
        boolean endFlag = ssid.contains(Global.SSID_END);
        if (startFlag&&endFlag){
            macStr = ssid.substring(ssid.indexOf("[")+1, ssid.indexOf("]"));
        }else {
            Message msg=new Message();
            msg.what=ERROR_WIFI;
            msgHandler.sendMessage(msg);
            return;
        }
        String regEx = "[0-9a-fA-F]{6}";
        Pattern pat = Pattern.compile(regEx);
        Matcher mat = pat.matcher(macStr);
        //旧的8位
        String regExEight = "[0-9a-fA-F]{8}";
        Pattern patEight = Pattern.compile(regExEight);
        Matcher matcEight = patEight.matcher(macStr);
        int macInt1 = 0;
        int macInt2 = 0;
        int macInt3 = 0;
        int macInt4 = 0;
        if(mat.matches()){
            String mac0 = "80";
            String mac1 = macStr.substring(0, 2);
            String mac2 = macStr.substring(2, 4);
            String mac3 = macStr.substring(4, 6);

            macInt1 = Integer.parseInt(mac0, 16);
            macInt2 = Integer.parseInt(mac1, 16);
            macInt3 = Integer.parseInt(mac2, 16);
            macInt4 = Integer.parseInt(mac3, 16);
        }else if (matcEight.matches()){
            String mac1 = macStr.substring(0, 2);
            String mac2 = macStr.substring(2, 4);
            String mac3 = macStr.substring(4, 6);
            String mac4 = macStr.substring(6, 8);

            macInt1 = Integer.parseInt(mac1, 16);
            macInt2 = Integer.parseInt(mac2, 16);
            macInt3 = Integer.parseInt(mac3, 16);
            macInt4 = Integer.parseInt(mac4, 16);
        } else{
            Message message = msgHandler.obtainMessage();
            message.what=WIFI_ERRO;
            msgHandler.sendMessage(message);
            return;
        }

        switch (v.getId()){
            case R.id.socket_pause:
                byte[] pauseCMD = new byte[16];
                pauseCMD[0]= (byte) macInt1;
                pauseCMD[1]= (byte) macInt2;
                pauseCMD[2]= (byte) macInt3;
                pauseCMD[3]= (byte) macInt4;
                pauseCMD[4]= 16;
                pauseCMD[11]= 8; //cmd

                SendCMD pause=new SendCMD(pauseCMD);
                new Thread(pause).start();
                SocketMessage pauseMSG = new SocketMessage(getString(R.string.pause),System.currentTimeMillis(),true,false);
                mMessageList.add(pauseMSG);
                break;
            case R.id.socket_rest:
                byte[] resetCMD=new byte[16];
                resetCMD[0]= (byte) macInt1;
                resetCMD[1]= (byte) macInt2;
                resetCMD[2]= (byte) macInt3;
                resetCMD[3]= (byte) macInt4;
                resetCMD[4]= 16;
                resetCMD[11]= 4; //cmd 0x00000100

                SendCMD reset=new SendCMD(resetCMD);
                new Thread(reset).start();
                SocketMessage reSetMSG = new SocketMessage(getString(R.string.reset),System.currentTimeMillis(),true,false);
                mMessageList.add(reSetMSG);
                break;
            case R.id.socket_resume:
                byte[] resumeCMD=new byte[16];
                resumeCMD[0]= (byte) macInt1;
                resumeCMD[1]= (byte) macInt2;
                resumeCMD[2]= (byte) macInt3;
                resumeCMD[3]= (byte) macInt4;
                resumeCMD[4]= 16;
                resumeCMD[11]= 12; //cmd 0x00000100

                SendCMD resume=new SendCMD(resumeCMD);
                new Thread(resume).start();
                SocketMessage resumeMSG = new SocketMessage(getString(R.string.resume),System.currentTimeMillis(),true,false);
                mMessageList.add(resumeMSG);
                break;
            case R.id.socket_test:
                byte[] testCMD=new byte[16];
                //8032364e 128 50 35 78
                //
                //8030ed86  128 48 237 134
                testCMD[0]= (byte) macInt1;
                testCMD[1]= (byte) macInt2;
                testCMD[2]= (byte) macInt3;
                testCMD[3]= (byte) macInt4;
                testCMD[4]= 16;
                testCMD[11]= 0; //cmd

                SendCMD test=new SendCMD(testCMD);
                new Thread(test).start();

                SocketMessage testMSG = new SocketMessage(getString(R.string.test),System.currentTimeMillis(),true,false);
                mMessageList.add(testMSG);
                break;
        }
        mAdapter.notifyDataSetChanged();
        mRecyclerView.smoothScrollToPosition(mMessageList.size());
    }

    class SendCMD extends Thread{
        byte[] CMDbyte;
        public SendCMD(byte[] msgB) {
            CMDbyte = msgB;
        }

        @Override
        public void run() {
            Socket client=null;
            try {

                client = new Socket(Global.SERVER_IP,Global.SERVER_PORT);
                byte[] readBuf = new byte[16];

                OutputStream os = client.getOutputStream();
                InputStream is = client.getInputStream();
                os.write(CMDbyte);

                is.read(readBuf);
                String result="";
                for (int i = 0; i < readBuf.length; i++) {
                    if (i==11){
                        switch (readBuf[i]) {
                            case 0:
                                result=getString(R.string.testSuccess);
                                break;
                            case 8:
                                result=getString(R.string.pauseSuccess);
                                break;
                            case 12:
                                result=getString(R.string.resumeSuccess);
                                break;
                            case 4:
                                result=getString(R.string.restSuccess);
                                break;
                        }
                    }
                }

                Message msg=new Message();
                msg.what=READ_MSG_CODE;
                Bundle b=new Bundle();

                b.putString("result",result);
                msg.setData(b);
                msgHandler.sendMessage(msg);
            } catch (SocketException e) {
                e.printStackTrace();
                Message msg=new Message();
                msg.what=ERROR_CODE;
                Bundle bundle=new Bundle();
                msg.setData(bundle);
                bundle.putString("error",e.toString());
                msgHandler.sendMessage(msg);
            } catch (UnknownHostException e) {
                e.printStackTrace();
                Message msg=new Message();
                msg.what=ERROR_CODE;
                Bundle bundle=new Bundle();
                msg.setData(bundle);
                bundle.putString("error",e.toString());
                msgHandler.sendMessage(msg);
            } catch (IOException e) {
                e.printStackTrace();
                Message msg=new Message();
                msg.what=ERROR_CODE;
                Bundle bundle=new Bundle();
                bundle.putString("error",e.toString());
                msg.setData(bundle);
                msgHandler.sendMessage(msg);
            }finally {
                if (client!=null){
                    try {
                        client.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    class RecievMsgHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){
                case ERROR_CODE:
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
                    mRecyclerView.smoothScrollToPosition(mMessageList.size());
                    break;
                case READ_MSG_CODE:
                    Bundle data = msg.getData();
                    SocketMessage smsg=new SocketMessage(data.getString("result"),System.currentTimeMillis(),true,true);
                    mMessageList.add(smsg);
                    mAdapter.notifyItemInserted(mMessageList.size());
                    mRecyclerView.smoothScrollToPosition(mMessageList.size());
                    break;
                case ERROR_WIFI:
                    Toast.makeText(SocketActivity.this,R.string.tos_wifi_switch,Toast.LENGTH_LONG).show();
                    break;
            }
            mLinearLayoutManager.scrollToPosition(mMessageList.size());
        }
    }

}
