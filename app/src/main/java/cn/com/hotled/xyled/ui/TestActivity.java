package cn.com.hotled.xyled.ui;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.util.TcpSend;

public class TestActivity extends AppCompatActivity {

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
    private byte[] mFirstFrame;
    private byte[] mSecondFrame;
    private byte[] mThirdFrame;
    private byte[] mTimeAxisOne;
    private byte[] mTimeAxistwo;
    private byte[] mTimeAxisThree;
    private byte[] mTimeAxisStartAddress;
    private File mColorPRG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);
        readFile();
        intiData();
        genFile();
    }

    @OnClick(R.id.bt_sendFile)
    public void sendFile(){
        tcpIPaddress.setText("192.168.1.102");
        tcpPort.setText("10010");
        String ipAddress = tcpIPaddress.getText().toString();
        String port = tcpPort.getText().toString();
        TcpSend tcpSend = new TcpSend(this,ipAddress,Integer.parseInt(port),mColorPRG);
        tcpSend.send();

    }
    private void intiData() {
        //总头长度4byte  节目个数1byte,节目表长度10byte,帧头长度16byte,字层属性长度6byte
        mSecondPart = new byte[]{4, 1, 10, 16, 6};
        //10Byte节目 = (1BIT属性[0=0节目，1=其它],6BIT未用,1BIT[0=64色,1=7色]）,2Byte轴上帧数,3Byte时钟, 4Byte时间轴存放地址
        mThirdPart = new byte[10];
        mThirdPart[2] = 3;
        mFirstFrame = new byte[4096];
        mSecondFrame = new byte[4096];
        mThirdFrame = new byte[4096];
        Bitmap bitmap = getIntent().getParcelableExtra("bitmap");
        Log.i("test","getHeight="+bitmap.getHeight());
        Log.i("test","getWidth="+bitmap.getWidth());
        for (int x1=0,i = 0;x1<bitmap.getWidth();x1++){
            for (int y1=0;y1<bitmap.getHeight();y1++){
                int pixel = bitmap.getPixel(x1, y1);

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

                Log.i("test","blue="+blue);
                Log.i("test","green="+green);
                Log.i("test","red="+red);

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

                mFirstFrame[i] = color;
                mSecondFrame[i]= color;
                mThirdFrame[i]= color;

                Log.i("test","color="+color);
                i++;
            }
        }

        mTimeAxisStartAddress = new byte[4];
        int timeAxisStartAddress = mHeadBytes.length + mSecondPart.length + mThirdPart.length;
        //计算时间轴开始地址
        timeAxisStartAddress = timeAxisStartAddress + mFirstFrame.length + mSecondFrame.length+ mThirdFrame.length-4096;
        //转化成byte
        int timeAxisIntOne = timeAxisStartAddress >> 24;
//        mTimeAxisStartAddress[0] = (byte) (timeAxisIntOne & 0xff);
        mThirdPart[6] = (byte) (timeAxisIntOne & 0xff);
        int timeAxisIntTwo = timeAxisStartAddress >> 16;
//        mTimeAxisStartAddress[1] = (byte) (timeAxisIntTwo & 0xff);
        mThirdPart[7] = (byte) (timeAxisIntTwo & 0xff);
        int timeAxisIntThree = timeAxisStartAddress >> 8;
//        mTimeAxisStartAddress[2] = (byte) (timeAxisIntThree & 0xff);
        mThirdPart[8] = (byte) (timeAxisIntThree & 0xff);
//        mTimeAxisStartAddress[3] = (byte) (timeAxisStartAddress & 0xff);
        mThirdPart[9] = (byte) (timeAxisStartAddress & 0xff);

        //写入时间轴地址
//        for (int i = 0, k = 6; i < mTimeAxisStartAddress.length; i++) {
//            mThirdPart[k] = mTimeAxisStartAddress[i];
//        }

        int firstFrameStartAdr = mHeadBytes.length + mSecondPart.length + mThirdPart.length - 4096;
        int secondFrameStartAdr = mHeadBytes.length + mSecondPart.length + mThirdPart.length + mFirstFrame.length-4096;
        int thirdFrameStartAdr = mHeadBytes.length + mSecondPart.length + mThirdPart.length + mFirstFrame.length + mSecondFrame.length-4096;

        byte[] firstFrameSaByte = new byte[4];
        byte[] secondFrameSaByte=new byte[4];
        byte[] thirdFrameSaByte=new byte[4];

        for (int length = firstFrameSaByte.length,i=0; length > 0; length--,i++) {
            int bitCount = (length-1) * 8;
            int tempFFSA=firstFrameStartAdr;
            int tempSFSA=secondFrameStartAdr;
            int tempTFSA=thirdFrameStartAdr;
            System.out.println("bitCount:"+bitCount);
            tempFFSA = tempFFSA >> bitCount;
            System.out.println("firstFrameStartAdr>>bitCount:"+tempFFSA);
            tempSFSA=tempSFSA>>bitCount;
            System.out.println("secondFrameStartAdr>>bitCount:"+tempSFSA);
            tempTFSA=tempTFSA>>bitCount;
            System.out.println("thirdFrameStartAdr>>bitCount:"+tempTFSA);
            firstFrameSaByte[i] = (byte) (tempFFSA & 0xff);
            secondFrameSaByte[i]=(byte)(tempSFSA & 0xff);
            thirdFrameSaByte[i]=(byte)(tempTFSA & 0xff);
        }


        mTimeAxisOne = new byte[16];
        mTimeAxistwo = new byte[16];
        mTimeAxisThree = new byte[16];

        int time=255;
        mTimeAxisOne[0] = (byte) time;
        mTimeAxistwo[0]=(byte) time;
        mTimeAxisThree[0]=(byte) time;


        for (int i = 0,m = 2; i < firstFrameSaByte.length; i++,m++) {
            mTimeAxisOne[m] = firstFrameSaByte[i];
            mTimeAxistwo[m]=secondFrameSaByte[i];
            mTimeAxisThree[m]=thirdFrameSaByte[i];
        }
    }


    private void readFile() {
        File file = new File(Environment.getExternalStorageDirectory()+"/COLOR_01.PRG");
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


    private void genFile() {
        mColorPRG = new File(Environment.getExternalStorageDirectory()+"/amap/COLOR_01.PRG");
        if (mColorPRG.exists()) {
            Log.i("test","文件已存在"+ mColorPRG.getAbsolutePath());
            Toast.makeText(this, "文件已存在"+ mColorPRG.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            mColorPRG.delete();
            try {
                mColorPRG.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            tvShowResult.setText("文件已存在"+ mColorPRG.getAbsolutePath()+"并删除");
        }else {
            try {
                mColorPRG.createNewFile();
                tvShowResult.setText("生成新文件"+ mColorPRG.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            FileOutputStream fos=new FileOutputStream(mColorPRG,true);
            fos.write(mHeadBytes);//写入4096头
            tvShowResult.setText(tvShowResult.getText().toString()+"\n"+"写入头文件4096 byte...");
            fos.write(mSecondPart);
            tvShowResult.setText(tvShowResult.getText().toString()+"\n"+"写入文件mSecondPart"+mSecondPart.length+"byte");
            fos.write(mThirdPart);
            tvShowResult.setText(tvShowResult.getText().toString()+"\n"+"写入文件mThirdPart"+mThirdPart.length+"byte");
            fos.write(mFirstFrame);
            tvShowResult.setText(tvShowResult.getText().toString()+"\n"+"写入第一帧文件mFirstFrame"+mFirstFrame.length+"byte");
            fos.write(mSecondFrame);
            tvShowResult.setText(tvShowResult.getText().toString()+"\n"+"写入第二帧文件mSecondFrame"+mSecondFrame.length+"byte");;
            fos.write(mThirdFrame);
            tvShowResult.setText(tvShowResult.getText().toString()+"\n"+"写入第三帧文件mThirdFrame"+mThirdFrame.length+"byte");
            fos.write(mTimeAxisOne);
            tvShowResult.setText(tvShowResult.getText().toString()+"\n"+"写入第一帧时间轴文件mTimeAxisOne"+mTimeAxisOne.length+"byte");
            fos.write(mTimeAxistwo);
            tvShowResult.setText(tvShowResult.getText().toString()+"\n"+"写入第二帧时间轴文件mTimeAxistwo"+mTimeAxistwo.length+"byte");
            fos.write(mTimeAxisThree);
            tvShowResult.setText(tvShowResult.getText().toString()+"\n"+"写入第三帧时间轴文件mTimeAxisThree"+mTimeAxisThree.length+"byte");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
