package cn.com.hotled.xyled.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.hotled.xyled.R;

public class SocketActivity extends AppCompatActivity {

    @BindView(R.id.socket_address)
    EditText socket_address;
    @BindView(R.id.socket_port)
    EditText socket_port;
    @BindView(R.id.socket_cmd)
    EditText socket_cmd;
    @BindView(R.id.inputText)
    EditText inputText;
    @BindView(R.id.send)
    Button send;
    @BindView(R.id.socket_tvShow)
    TextView socket_tvShow;
    private String messageText;
    private String socketAddress;
    private String socketPort;
    private RecievMsgHandler msgHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket);
        ButterKnife.bind(this);
        msgHandler = new RecievMsgHandler();

    }


    @OnClick(R.id.send)
    void sendMessage(){
        messageText = inputText.getText().toString();
        socketAddress = socket_address.getText().toString();
        socketPort = socket_port.getText().toString();
        new SendMessage().start();
    }

    class SendMessage extends Thread{
        @Override
        public void run() {
            Socket client=null;
            try {
                String inputStr = messageText;

                InetAddress addr = InetAddress.getByName(socketAddress);
                int port = Integer.parseInt(socketPort);

                client = new Socket(addr,port);

                byte[] sendBuf = new byte[512];
                byte[] readBuf = new byte[128];
                sendBuf[0]=12;
                sendBuf[1]=34;
                sendBuf[2]=56;
                sendBuf[3]=78;
                sendBuf[4]=10;
                //写入指令
                String cmdStr = socket_cmd.getText().toString();
                if (!cmdStr.equals("")){
                    int cmdInt = Integer.parseInt(cmdStr);
                    sendBuf[11]= (byte) cmdInt;
                }
                byte[] inputStrBytes = inputStr.getBytes();
                for (int i=0,index=16;i<inputStrBytes.length;i++,index++){
                    sendBuf[index]=inputStrBytes[i];
                }
                OutputStream os = client.getOutputStream();
                InputStream is = client.getInputStream();

                os.write(sendBuf);
                is.read(readBuf);

                Message msg=new Message();
                Bundle b=new Bundle();
                String recStr = new String(readBuf,"GBK");
                b.putString("result",recStr);
                msg.setData(b);
                msgHandler.sendMessage(msg);
                Log.i("handler-rec",recStr);
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
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
            Bundle data = msg.getData();
            String result = data.getString("result");
            Log.i("handle","rec"+result);
            socket_tvShow.setText("收到的信息 : "+result);
        }
    }

}
