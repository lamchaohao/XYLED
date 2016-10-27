package cn.com.hotled.xyled.ui;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.hotled.xyled.R;

public class OTGActivity extends AppCompatActivity {

    private static final String TAG = "OTGActivity";
    private UsbManager usbManager;
    List<String> devicesName=new ArrayList<>();
    @BindView(R.id.getName)
    Button et_getDeviceName;
    @BindView(R.id.tv_otg_show)
    TextView tv_show;
    @BindView(R.id.bt_copyFile)
    Button btcopy;
    private UsbDevice device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otg);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        usbManager = (UsbManager)getSystemService(Context.USB_SERVICE);

    }

    @OnClick(R.id.getName)
    void getDevicesName(){
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        while(deviceIterator.hasNext()){
            device = deviceIterator.next();
            //在这里添加处理设备的代码
            devicesName.add(device.toString());

        }
        StringBuffer sb=new StringBuffer();
        for (String name:devicesName){
            sb.append(name);
            sb.append("\n");

        }
        tv_show.setText(sb);
    }
    @OnClick(R.id.bt_copyFile)
    void copyFile(){
        File file =new File(Environment.getExternalStorageDirectory()+"/wifi_config.log");
        if(file.exists()){
            Toast.makeText(OTGActivity.this, "file exists", Toast.LENGTH_SHORT).show();
        }else {
            try {
                boolean newFile = file.createNewFile();
                if (newFile){
                    Toast.makeText(OTGActivity.this, "create new File", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(OTGActivity.this, "create new File failed", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        new Thread(new Runnable() {

            @Override
            public void run() {

                UsbInterface intf= device.getInterface(0);
                UsbEndpoint endpoint= intf.getEndpoint(0);
                UsbDeviceConnection connection= usbManager.openDevice(device);
                connection.claimInterface(intf, true);
                try {
                    FileInputStream fis=new FileInputStream(new File(Environment.getExternalStorageDirectory()+"/wifi_config.log"));
                    byte[] buffer = new byte[1024];
                    int byteread = 0;
                    int bytesum = 0;
                    int length;
                    while ( (byteread = fis.read(buffer)) != -1) {
                        bytesum += byteread; //字节数 文件大小
                        System.out.println(bytesum);
                        int i = connection.bulkTransfer(endpoint, buffer, buffer.length, 10000);
                        Log.i(TAG,i+"__bytesum:=="+bytesum);
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }).start();
    }
}
