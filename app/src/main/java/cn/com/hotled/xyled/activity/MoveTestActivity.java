package cn.com.hotled.xyled.activity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.util.TcpSend;

public class MoveTestActivity extends BaseActivity {

    @BindView(R.id.tv_debug)
    TextView tvDebug;
    @BindView(R.id.tv_showResult)
    TextView tvShowResult;
    @BindView(R.id.et_tcpIp)
    EditText tcpIPaddress;
    @BindView(R.id.et_tcpPort)
    EditText tcpPort;
    @BindView(R.id.bt_sendFile)
    Button send;

    private byte[] mHeadBytes;
    private byte[] mSecondPart;
    private byte[] mThirdPart;
    private byte[] mContent;
    private Bitmap mBitmap;
    private byte[] mTextAttrs;
    private List<byte[]> mTimeAxisList;
    private File mColorPRG;
    private int mScreenWidth;
    private int mScreenHeight;
    private byte[] mBackBG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);
        readFile();
        intiData();
        new Thread(new GenFile()).start();
    }

    private void intiData() {
        mBitmap = getIntent().getParcelableExtra("bitmap");

        mSecondPart = new byte[]{4, 1, 10, 16, 6};
        mThirdPart = new byte[10];

        mScreenWidth = 64;
        mScreenHeight = 32;

        mBackBG = new byte[mScreenWidth*mScreenHeight];

        int frameCount=mBitmap.getWidth()*2;
        int frameCountOne=frameCount>>8;

        //帧数
        mThirdPart[1] = (byte) (frameCountOne&0xff);
        mThirdPart[2]= (byte) (frameCount&0xff);

        //字属性
        mTextAttrs = new byte[6];
        mTextAttrs[0]=1;//方式
        int tempScreenWidth= mScreenWidth;
        tempScreenWidth = tempScreenWidth >> 8;
        mTextAttrs[1]=0; //屏地址1
        mTextAttrs[2]=0; //屏地址2
        mTextAttrs[3]=(byte) (tempScreenWidth & 0xff);//屏宽1
        mTextAttrs[4]=(byte) (mScreenWidth & 0xff);//屏宽2
        mTextAttrs[5]= (byte) mScreenHeight;//屏高

        mContent = new byte[mBitmap.getWidth()*mBitmap.getHeight()];
    
        for (int x1=0,i = 0;x1<mBitmap.getWidth();x1++){
            for (int y1=0;y1<mBitmap.getHeight();y1++){
                int pixel = mBitmap.getPixel(x1, y1);

                int blue = 0;
                int green = 0;
                int red = 0;
                Log.i("test","pixel="+pixel);
                String hexStr = Integer.toHexString(pixel);
                Log.i("test","hexStr="+hexStr);
                if (pixel==0&&hexStr.length()<6){

                }else if (hexStr.length()>=6){
                    int length = hexStr.length();
                    hexStr=hexStr.substring(length-6);//保证要有6位数字
                    red = Integer.parseInt(hexStr.substring(0, 2),16);
                    green = Integer.parseInt(hexStr.substring(2, 4),16);
                    blue = Integer.parseInt(hexStr.substring(4, 6),16);
                }
                // 因为rgb，分别用一个int来表示，值为0-255
                blue = blue/85;
                green = green/85;
                red = red/85;
                // 最终要转换成一个byte中的低6位，每个颜色用两位来表示，序列是 bb gg rr，每种颜色最多有4种可能，00,01,10,11
                // 最若全蓝 110000 =,48,100000=32,010000=16，
                blue = blue *16;
                green= green*4;
                red = red *1;

                byte color= (byte) (blue+green+red);

                int rgb = Color.rgb(blue, green, red);
                Log.i("test","rgb="+rgb);

                mContent[i] = color;
                Log.i("test","color="+color);
                i++;
            }
        }
        int timeAxisStartAddress = mHeadBytes.length + mSecondPart.length + mThirdPart.length+mTextAttrs.length;
        //计算时间轴开始地址
        timeAxisStartAddress = timeAxisStartAddress + mBackBG.length + mContent.length + mBackBG.length -4096;
        //转化成byte
        int timeAxisIntOne = timeAxisStartAddress >> 24;
        mThirdPart[6] = (byte) (timeAxisIntOne & 0xff);
        int timeAxisIntTwo = timeAxisStartAddress >> 16;
        mThirdPart[7] = (byte) (timeAxisIntTwo & 0xff);
        int timeAxisIntThree = timeAxisStartAddress >> 8;
        mThirdPart[8] = (byte) (timeAxisIntThree & 0xff);
        mThirdPart[9] = (byte) (timeAxisStartAddress & 0xff);

        mTimeAxisList = new ArrayList<>();

        int width = mBitmap.getWidth();
        //文字属性地址
        int attrsAddress=mHeadBytes.length + mSecondPart.length + mThirdPart.length-4096;
        //文字开始的地址
        int textStartAddress=mHeadBytes.length + mSecondPart.length + mThirdPart.length+mTextAttrs.length-4096;

        for (int i = 0; i<width+mScreenWidth*2; i++){
            byte[] timeAxis=new byte[16];
            //时间
            timeAxis[0]=100;
            //字属性地址 3byte
            for (int length = 3,j=6; length > 0; length--,j++) {
                int bitCount = (length-1) * 8;
                int tempAA=attrsAddress;
                tempAA = tempAA >> bitCount;
                timeAxis[j] = (byte) (tempAA & 0xff);
            }
            //字内容地址 4byte
            for (int length = 4,k=9; length > 0; length--,k++) {
                int bitCount = (length-1) * 8;
                int tempTextAddress=textStartAddress+i*mScreenHeight;
                tempTextAddress = tempTextAddress >> bitCount;
                timeAxis[k] = (byte) (tempTextAddress & 0xff);
            }
            mTimeAxisList.add(timeAxis);

        }

    }


    private void readFile() {
        File file =new File(Environment.getExternalStorageDirectory()+"/COLOR_01.PRG");
        if (file.exists()) {
            Toast.makeText(this, "COLOR_01.PRG 已找到", Toast.LENGTH_SHORT).show();
        }
        mHeadBytes = new byte[4096];
        try {
            FileInputStream fis =new FileInputStream(file);
            int flag = fis.read(mHeadBytes);
            if (flag!=-1){
                Log.i("test",mHeadBytes.toString());
                Toast.makeText(this, "4096字节已经读取", Toast.LENGTH_SHORT).show();
                tvShowResult.setText(tvShowResult.getText().toString()+"\n"+"4096字节已经读取");
                tvDebug.setText("4096字节已经读取");
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.bt_sendFile)
    public void sendFile(){
        tcpIPaddress.setText("192.168.1.100");
        tcpPort.setText("10010");
        String ipAddress = tcpIPaddress.getText().toString();
        String port = tcpPort.getText().toString();
        TcpSend tcpSend = new TcpSend(this,ipAddress,Integer.parseInt(port),mColorPRG);
        tcpSend.send();

    }


    private class GenFile implements Runnable{

        @Override
        public void run() {
            genFile();
        }
    }
    private void genFile() {
        mColorPRG = new File(Environment.getExternalStorageDirectory()+"/amap/COLOR_01.PRG");
        if (mColorPRG.exists()) {
            Log.i("move","文件已存在"+ mColorPRG.getAbsolutePath());
            mColorPRG.delete();
            try {
                mColorPRG.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.i("move","文件已存在"+ mColorPRG.getAbsolutePath()+"并删除");
        }else {
            try {
                mColorPRG.createNewFile();
                Log.i("move","生成新文件"+ mColorPRG.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileOutputStream fos=null;
        try {
            fos=new FileOutputStream(mColorPRG,true);
            fos.write(mHeadBytes);//写入4096头
            Log.i("move","写入头文件4096 byte...");

            fos.write(mSecondPart);
            Log.i("move","写入文件mSecondPart"+mSecondPart.length+"byte");

            fos.write(mThirdPart);
            Log.i("move","写入文件mThirdPart"+mThirdPart.length+"byte");

            fos.write(mTextAttrs);
            Log.i("move","写入字属性文件mTextAttrs"+mTextAttrs.length+"byte");
            //多加一块黑色图片，使其完整左移
            fos.write(mBackBG);
            Log.i("move","写入黑色背景图片 backBG"+ mBackBG.length+"byte");
            fos.write(mContent);
            Log.i("move","写入文件mContent"+mContent.length+"byte");
            //多加一块黑色图片，使其完整左移
            fos.write(mBackBG);
            Log.i("move","写入黑色背景图片 backBG"+ mBackBG.length+"byte");
            for (int i = 0; i < mTimeAxisList.size(); i++) {
                fos.write(mTimeAxisList.get(i));
            }


            Log.i("move","genfile done--------------------------------");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (fos!=null)
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

    }

}
