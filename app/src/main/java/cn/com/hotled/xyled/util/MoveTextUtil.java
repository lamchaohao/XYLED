package cn.com.hotled.xyled.util;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lam on 2016/11/22.
 */

public class MoveTextUtil {

    private byte[] mHeadBytes;
    //文件总头区 5byte
    private byte fileHeadLength;   //总头长度
    private byte mItemCount;       //节目个数
    private byte mItemTableLength; //节目表长度
    private byte mFrameHeadLength; //帧头长度
    private byte mTextAttrsLength;  //字层性长度
    private byte[] mFileHeadPart = new byte[5];

    //节目区 10byte
    private byte itemAttr;
    private byte[] mFrameCountOfAxis =new byte[2]; //轴上帧数
    private byte[] mClock = new byte[3]; //时钟
    private byte[] mTimeAxisAddress = new byte[4]; //时间轴存放地址
    private byte[] mItemPart = new byte[10];

    //文字属性区 6byte
    private byte[] mTextAttrs=new byte[6];
    private byte textStyle;
    private byte[] screenAddress = new byte[2];
    private byte[] screenWidthByte = new byte[2];
    private byte screenHeightByte;

    private byte[] mTextContent;
    private Bitmap mBitmap;

    private byte frameTime;
    private byte picStyle;

    private List<byte[]> mTimeAxisList;

    private File mColorPRG;
    private int mScreenWidth;
    private int mScreenHeight;
    private byte[] mBlackBG;
    private int mFrameCount;


    public MoveTextUtil(Bitmap bitmap,int screenWidth,int screenHeight) {
        mBitmap = bitmap;
        mScreenWidth=screenWidth;
        mScreenHeight=screenHeight;
    }
    private void initFileHead() {
        fileHeadLength=4;
        mItemCount=1;
        mItemTableLength=10;
        mFrameHeadLength=16;
        mTextAttrsLength=6;
        mFileHeadPart[0] = fileHeadLength;
        mFileHeadPart[1] = mItemCount;
        mFileHeadPart[2] = mItemTableLength;
        mFileHeadPart[3] = mFrameHeadLength;
        mFileHeadPart[4] = mTextAttrsLength;
    }

    private void initTextContent() {
        mTextContent=new byte[mBitmap.getWidth()*mBitmap.getHeight()];
        for (int x1=0,i = 0;x1<mBitmap.getWidth();x1++){
            for (int y1=0;y1<mBitmap.getHeight();y1++){
                int pixel = mBitmap.getPixel(x1, y1);
                int blue = 0;
                int green = 0;
                int red = 0;
                String hexStr = Integer.toHexString(pixel);
                if (pixel==0&&hexStr.length()<6){
                    Log.i("move","no color,x = "+x1+", y= "+y1);
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

                mTextContent[i] = color;
                i++;
            }
        }
    }



    private void initBlackBG() {
        //64*32 =2048
        mBlackBG=new byte[mScreenWidth*mScreenHeight];//new byte[mScreenWidth*mScreenWidth];
        Log.i("move","initBlackBG_mBlackBG.length="+mBlackBG.length);
        int index=0;
        for (int i=0;i<mScreenWidth;i++){
            for (int j = 0; j < mScreenHeight; j++) {
                mBlackBG[index]=12;//
                index++;
                Log.i("move","initBlackBG_index="+index);//initBlackBG_index=2048
            }
        }
    }

    private void initItemPart() {
        //帧数是图片的宽度加上后面张与屏宽一致的黑色图片
        mFrameCount = mBitmap.getWidth();
        byte[] frameCountByte = intToByteArray(mFrameCount, 2);
        setInbyteArray(1,frameCountByte,mItemPart);
        int timeAxisAddress=mHeadBytes.length+mFileHeadPart.length+mItemPart.length+mTextAttrs.length+mBlackBG.length+mTextContent.length+mBlackBG.length-4096;
        setInbyteArray(6,intToByteArray(timeAxisAddress, 4),mItemPart);
    }

    private void initTextAttrs() {
        textStyle=1;//直贴
        screenAddress[0]=0;
        screenAddress[1]=0;
        screenWidthByte = intToByteArray(mScreenWidth, 2);
        screenHeightByte= (byte) mScreenHeight;

        mTextAttrs[0] = textStyle;
        setInbyteArray(1,screenAddress,mTextAttrs);
        setInbyteArray(3,screenWidthByte,mTextAttrs);
        mTextAttrs[5]=screenHeightByte;
    }


    private void initTimeAxis() {
        mTimeAxisList = new ArrayList<>();

        //文字属性地址 3byte
        int attrStartAddress=mHeadBytes.length+mFileHeadPart.length+mItemPart.length-4096;
        //文字地址
        int textContentAddressInt=mHeadBytes.length+mFileHeadPart.length+mItemPart.length+mTextAttrs.length+mBlackBG.length-4096;
        frameTime = 40;
        picStyle= 1;//1BIT地址指向(0=图层,1=跳转地址),7BIT未用
        Log.i("move","frameCount=="+mFrameCount+"bitmap.getwidth=="+mBitmap.getWidth());
        for (int i = 0; i<mFrameCount; i++){
            byte[] timeAxis=new byte[16];
            //时间
            timeAxis[0]=frameTime;
            timeAxis[1] = picStyle;
            //字属性地址

            //字内容地址 4byte
            int tempTextAddress=textContentAddressInt+i*mScreenHeight;

            byte[] picAddress = intToByteArray(textContentAddressInt-mBlackBG.length, 4);//当方式为跳向指定帧时这个地址是指向时间轴上的一个时间点(头0开始)
            byte[] atrrAddress=intToByteArray(attrStartAddress,3);
            byte[] textContentAddress = intToByteArray(tempTextAddress, 4);
            byte[] clockOrTem = new byte[3];

            setInbyteArray(2,picAddress,timeAxis);
            setInbyteArray(6,atrrAddress,timeAxis);
            setInbyteArray(9,textContentAddress,timeAxis);
            setInbyteArray(13,clockOrTem,timeAxis);

            mTimeAxisList.add(timeAxis);
        }
    }







    /**
     *
     * @param source 源数值
     * @param byteArrayLength 要转变成的byte数组长度
     * @return
     */
    private byte[] intToByteArray(int source,int byteArrayLength){
        byte[] result = new byte[byteArrayLength];
        for (int length = byteArrayLength,index=0; length > 0; length--,index++) {
            int bitCount = (length-1) * 8;
            int temp=source;
            temp = temp >> bitCount; //移位
            result[index] = (byte) (temp & 0xff);
        }
        return result;
    }

    /**
     *
     * @param targetStart 要赋值的目标数组的开始序列,从0开始
     * @param source 源数组
     * @param target 目标数组
     */
    private void setInbyteArray(int targetStart,byte[] source,byte[] target){
        for (int i = 0;i<source.length;i++){
            target[targetStart+i]=source[i];
        }
    }

    private void setHeadBytes(){
        File file =new File(Environment.getExternalStorageDirectory()+"/COLOR_01.PRG");
        mHeadBytes = new byte[4096];
        FileInputStream fis=null;
        if (file.exists()) {
            try {
                fis=new FileInputStream(file);
                fis.read(mHeadBytes);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if (fis!=null){
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    public void startGenFile(){
        new Thread(new GenFileThread()).start();
    }

    class GenFileThread implements Runnable{

        @Override
        public void run() {
            genFile();
        }
    }

    private void genFile(){

        setHeadBytes();
        initFileHead();
        initTextContent();
        initBlackBG();
        initTextAttrs();
        initItemPart();
        initTimeAxis();

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

            fos.write(mFileHeadPart);
            Log.i("move","写入文件总头mFileHeadPart"+mFileHeadPart.length+"byte");

            fos.write(mItemPart);
            Log.i("move","写入节目区mItemPart"+mItemPart.length+"byte");

            fos.write(mTextAttrs);
            Log.i("move","写入字属性文件mTextAttrs"+mTextAttrs.length+"byte");

            //多加一块黑色图片，使其完整左移
            fos.write(mBlackBG);
            Log.i("move","写入黑色背景图片 backBG"+ mBlackBG.length+"byte");//写入黑色背景图片 backBG4096byte

            fos.write(mTextContent);
            Log.i("move","写入文件mContent"+mTextContent.length+"byte");

            //多加一块黑色图片，使其完整左移
            fos.write(mBlackBG);
            Log.i("move","写入黑色背景图片 backBG"+ mBlackBG.length+"byte");

            for (int i = 0; i < mTimeAxisList.size(); i++) {
                fos.write(mTimeAxisList.get(i));
            }
            fos.flush();
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
