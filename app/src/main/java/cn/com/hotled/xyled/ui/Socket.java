package cn.com.hotled.xyled.ui;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.hotled.xyled.R;

public class Socket extends AppCompatActivity {

    @BindView(R.id.socket_address)
    EditText socket_address;
    @BindView(R.id.socket_port)
    EditText socket_port;
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
            DatagramSocket client=null;
            String recvStr=null;
            try {
                client = new DatagramSocket();
                String inputStr = messageText;
                byte[] sendBuf = inputStr.getBytes();
                InetAddress addr = InetAddress.getByName(socketAddress);
                int port = Integer.parseInt(socketPort);
                DatagramPacket sendPacket
                        = new DatagramPacket(sendBuf ,sendBuf.length , addr , port);
                client.send(sendPacket);
                byte[] recvBuf = new byte[100];
                DatagramPacket recvPacket
                        = new DatagramPacket(recvBuf , recvBuf.length);
                client.receive(recvPacket);
                client.close();

                recvStr = new String(recvPacket.getData() , 0 ,recvPacket.getLength());
                Message msg=new Message();
                Bundle b=new Bundle();
                b.putString("result",recvStr);
                msg.setData(b);
                msgHandler.sendMessage(msg);
                Log.i("handler","can execute to here");
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    class RecievMsgHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(Socket.this,"handleMessage...",Toast.LENGTH_LONG).show();
            Bundle data = msg.getData();
            String result = data.getString("result");
            Log.i("handle","rec"+result);
            socket_tvShow.setText("收到 "+result);
        }
    }

}
