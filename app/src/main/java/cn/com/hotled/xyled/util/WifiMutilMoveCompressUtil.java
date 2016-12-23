package cn.com.hotled.xyled.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Typeface;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.com.hotled.xyled.App;
import cn.com.hotled.xyled.bean.Program;
import cn.com.hotled.xyled.bean.TextButton;
import cn.com.hotled.xyled.dao.TextButtonDao;

import static android.graphics.Color.BLACK;

/**
 * Created by Lam on 2016/12/15.
 */

public class WifiMutilMoveCompressUtil {

    private static final int BLACK_BG_COL_BYTE_COUNT = 3;
    private byte[] mHeadBytes;
    //文件总头区 5byte
    private byte fileHeadLength;   //总头长度
    private byte mItemCount;       //节目个数
    private byte mItemTableLength; //节目表长度
    private byte mFrameHeadLength; //帧头长度
    private byte mTextAttrsLength;  //字层性长度
    private byte[] mFileHeadPart = new byte[5];

    //节目区 10byte
    private byte[] mItemPart = new byte[10];

    //文字属性区 6byte
    private byte[] mTextAttrs=new byte[6];
    private byte textStyle;
    private byte[] screenAddress = new byte[2];
    private byte[] screenWidthByte = new byte[2];
    private byte screenHeightByte;

    private byte mFrameTime;
    private byte mStayTime;

    private byte picStyle;

    private List<byte[]> mTimeAxisList;
    private List<Program> mProgramList;
    private List<byte[]> mTextContentList;
    private File mColorPRG;
    private int mScreenWidth;
    private int mScreenHeight;
    private byte[] mBlackBG;
    private int mFrameCount;
    private Activity mContext;
    private Handler genFileHandler ;

    private List<byte[]> mColByteCountList;
    private List<TextButton> mTextButtonList ;

    private int mTextSize ;
    private int mTextColor = Color.RED;
    private int mTextBgColor = BLACK;
    private boolean isBold;
    private boolean isItalic;
    private boolean isUnderLine;
    private File mTypeFile;
    private int mWidth;
    private int mHeight;
    private int mBaseX = 0;
    private int mBaseY = 25;
    private List<Bitmap> mBitmapList;
    private int mTempColbyteCount;

    public WifiMutilMoveCompressUtil(Activity context, List<Program> programs, int screenWidth, int screenHeight, float frameTime, float stayTime) {
        mContext=context;
        mProgramList = programs;
        mScreenWidth=screenWidth;
        mScreenHeight=screenHeight;
        mFrameTime= (byte) frameTime;
        mStayTime= (byte) stayTime;
        genFileHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.arg1==100){
                    Toast.makeText(mContext, "此节目文件已生成", Toast.LENGTH_SHORT).show();
                }
            }
        };

    }
    private void initFileHead() {
        fileHeadLength=4;
        mItemCount= 1;
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
        mBitmapList = drawBitmap();
        mTextContentList = new ArrayList<>();
        mColByteCountList = new ArrayList<>();
        for (int i = 0; i < mBitmapList.size(); i++) {
            mTextContentList.add(convertBitmapToPixel(mBitmapList.get(i)));
        }
    }

    private byte[] convertBitmapToPixel(Bitmap bitmap) {
        byte[] bitmapPixels = new byte[bitmap.getWidth()*bitmap.getHeight()];
        for (int x1=0,i = 0;x1<bitmap.getWidth();x1++){
            for (int y1=0;y1<bitmap.getHeight();y1++){
                int pixel = bitmap.getPixel(x1, y1);
                int blue = 0;
                int green = 0;
                int red = 0;
                String hexStr = Integer.toHexString(pixel);
                if (pixel==0&&hexStr.length()<6){
//                    Log.i("move","no color,x = "+x1+", y= "+y1);
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
                bitmapPixels[i] = color;
                i++;
            }
        }
        //取点后压缩
        CompressAlgorithm compressAlgorithm =new CompressAlgorithm();
        List<Byte> compress = compressAlgorithm.compress(bitmapPixels, bitmap.getWidth(), mScreenHeight);//进行压缩
        byte[] textContent = new byte[compress.size()];
        for (int i = 0; i < compress.size(); i++) {
            textContent[i]=compress.get(i);
        }

        mColByteCountList.add(compressAlgorithm.getColByteCount());
        return textContent;
    }

    private void initBlackBG() {
        byte count=(byte) 24;
        byte blackColor = 64;
        mBlackBG=new byte[BLACK_BG_COL_BYTE_COUNT*mScreenWidth];
        int index = 0;
        for (int i = 0; i < mBlackBG.length; i++) {
            mBlackBG[i]=blackColor;
            index++;
            if (index%BLACK_BG_COL_BYTE_COUNT==0){
                mBlackBG[i]=count;
            }
        }

    }

    private void initItemPart() {
        //帧数是图片的宽度加上后面张与屏宽一致的黑色图片
        for (Bitmap bitmap : mBitmapList) {
            int width = bitmap.getWidth();
            mFrameCount+=width;
            mFrameCount+=mScreenWidth;
        }
        mFrameCount-=mScreenWidth;

        byte[] frameCountByte = intToByteArray(mFrameCount, 2);
        setInbyteArray(1,frameCountByte,mItemPart);
        int textContentLength=0;
        for (byte[] bytes : mTextContentList) {
            textContentLength+=bytes.length;
        }
        textContentLength+=mBlackBG.length*mProgramList.size();
        int timeAxisAddress=mHeadBytes.length+mFileHeadPart.length+mItemPart.length+mTextAttrs.length+mBlackBG.length+textContentLength-4096;
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
        for (int i = 0; i < mProgramList.size(); i++) {
            setTimeAxis(i);
        }

    }

    private void setTimeAxis(int index) {
        //文字属性地址 3byte
        int attrStartAddress = mHeadBytes.length + mFileHeadPart.length + mItemPart.length - 4096;
        //文字地址
        int textContentAddressInt = mHeadBytes.length + mFileHeadPart.length + mItemPart.length + mTextAttrs.length + mBlackBG.length - 4096;
        picStyle = 1;//1BIT地址指向(0=图层,1=跳转地址),7BIT未用

        for (int i = 0; i <  mColByteCountList.get(index).length; i++) {
            byte[] timeAxis = new byte[16];
            //时间
            timeAxis[0] = (byte) mProgramList.get(index).getFrameTime();
            timeAxis[1] = picStyle;
            //字属性地址
            int tempTextAddress = 0;
            //字内容地址 4byte
            if (i == 0&&index==0) {
                mTempColbyteCount = 0;
            }else if(i==0&&index!=0){
                mTempColbyteCount += BLACK_BG_COL_BYTE_COUNT; //把黑色过渡图片的最后一列加入进去
            } else {
                mTempColbyteCount += mColByteCountList.get(index)[i - 1];
            }

//            if (i%mScreenWidth==0){
//                timeAxis[0] = (byte) mProgramList.get(index).getStayTime();
//                Log.e("setstaytime",timeAxis[0]+"");
//            }

            tempTextAddress = textContentAddressInt + mTempColbyteCount;

            byte[] picAddress = intToByteArray(textContentAddressInt - mBlackBG.length, 4);//当方式为跳向指定帧时这个地址是指向时间轴上的一个时间点(头0开始)
            byte[] atrrAddress = intToByteArray(attrStartAddress, 3);
            byte[] textContentAddress = intToByteArray(tempTextAddress, 4);
            byte[] clockOrTem = new byte[3];

            setInbyteArray(2, picAddress, timeAxis);
            setInbyteArray(6, atrrAddress, timeAxis);
            setInbyteArray(9, textContentAddress, timeAxis);
            setInbyteArray(13, clockOrTem, timeAxis);

            mTimeAxisList.add(timeAxis);
        }

        //加入一副黑色图片过渡
        for (int i = 0; i < mScreenWidth; i++) {
            byte[] timeAxis = new byte[16];
            //时间
            timeAxis[0] = 30;
            timeAxis[1] = picStyle;
            //字属性地址
            int tempTextAddress = 0;
            //字内容地址 4byte
            int temp=0;
            if (i==0){
                temp += mColByteCountList.get(index)[mColByteCountList.get(index).length - 1];  //把上个节目的最后一列加入进去
                timeAxis[0] = (byte) mProgramList.get(index).getStayTime();
            }else {
                temp=BLACK_BG_COL_BYTE_COUNT;
            }

            mTempColbyteCount+=temp;
            tempTextAddress = textContentAddressInt + mTempColbyteCount;

            byte[] picAddress = intToByteArray(textContentAddressInt - mBlackBG.length, 4);//当方式为跳向指定帧时这个地址是指向时间轴上的一个时间点(头0开始)
            byte[] atrrAddress = intToByteArray(attrStartAddress, 3);
            byte[] textContentAddress = intToByteArray(tempTextAddress, 4);
            byte[] clockOrTem = new byte[3];

            setInbyteArray(2, picAddress, timeAxis);
            setInbyteArray(6, atrrAddress, timeAxis);
            setInbyteArray(9, textContentAddress, timeAxis);
            setInbyteArray(13, clockOrTem, timeAxis);

            mTimeAxisList.add(timeAxis);
        }


    }


    private List<Bitmap> drawBitmap() {
        List<Bitmap> bitmaps = new ArrayList<>();
        for (Program program : mProgramList) {
            mTextButtonList = ((App) mContext.getApplication()).getDaoSession().getTextButtonDao().queryBuilder().where(TextButtonDao.Properties.ProgramId.eq(program.getId())).list();
            if (mTextButtonList==null||mTextButtonList.size()==0){
                mTextButtonList = new ArrayList<>();
            }

            for (int i = 0; i < mTextButtonList.size(); i++) {
                if (i==mTextButtonList.size()-1){
                    TextButton textButton = mTextButtonList.get(0);
                    mTextBgColor=textButton.getTextBackgroudColor();
                    mTextColor=textButton.getTextColor();
                    mTextSize = textButton.getTextSize();
                    isUnderLine = textButton.getIsUnderline();
                    isItalic = textButton.getIsIlatic();
                    isBold = textButton.getIsbold();
                    mTypeFile = textButton.getTypeface();
                    mBaseX = program.getBaseX();
                    mBaseY = program.getBaseY();
                }
            }
            bitmaps.add(drawText()); ;
        }
        return bitmaps;
    }

    private Bitmap drawText() {
        Paint paint =new Paint();
        Canvas canvas=new Canvas();
        StringBuilder sb=new StringBuilder();
        for (TextButton textButton : mTextButtonList) {
            sb.append(textButton.getText());
        }

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawPaint(paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));

        //如果图片比所设置的宽，则需加长

        //先设置好画笔，才进行计算
        paint.setColor(mTextColor);
        paint.setTextSize(mTextSize);

        if (mTypeFile!=null) {
            Typeface typeface = Typeface.createFromFile(mTypeFile);
            paint.setTypeface(typeface);
            if (isBold) {//粗体
                paint.setTypeface(Typeface.create(typeface,Typeface.BOLD));
            }
            if (isItalic) {//斜体
                paint.setTypeface(Typeface.create(typeface,Typeface.ITALIC));
            }
            if (isBold&&isItalic){//粗斜体
                paint.setTypeface(Typeface.create(typeface,Typeface.BOLD_ITALIC));
            }
        }
        else {
            paint.setTypeface(Typeface.DEFAULT);

            if (isBold) {//粗体
                paint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
            }
            if (isItalic) {//斜体
                paint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.ITALIC));
            }
            if (isBold&&isItalic){//粗斜体
                paint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD_ITALIC));
            }


        }
        if (isUnderLine) {//下划线
            paint.setUnderlineText(true);
        }else {
            paint.setUnderlineText(false);
        }
        paint.setTextAlign(Paint.Align.LEFT);
        mWidth = 64;
        // TODO: 2016/11/29 临时改为16高
        mHeight = 32;

        Bitmap bitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);

        //设置好画笔，开始计算
        float drawWidth = computeWidth(sb.toString(),paint);
        if (drawWidth>64) {
            mWidth = (int) drawWidth;
            bitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
            if (bitmap != null)
                canvas.setBitmap(bitmap);
        }
        //背景
        drawBgColor(drawWidth,canvas);
        //文本
        canvas.drawText(sb.toString(),mBaseX,mBaseY,paint);

        return bitmap;
    }


    private float computeWidth(String text,Paint paint) {
        float drawWidth=mBaseX;
        float[] widths=new float[text.length()];
        paint.getTextWidths(text,widths);
        for (int i = 0; i < widths.length; i++) {
            drawWidth+=widths[i];
        }
        Log.i("advance", "computeWidth:drawWidth "+drawWidth);
        //为了避免OOM，bitmap的最大宽度设置为2048,
        // TODO: 2016/12/8 如果文字太长，需要重新绘制一个bitmap，bitmap的最大尺寸为4096*4096
        if (drawWidth>=2048)
            return 2048;
        return drawWidth;
    }
    private void drawBgColor(float drawWidth,Canvas canvas) {
        Paint bgPaint=new Paint();
        bgPaint.setColor(mTextBgColor);
        canvas.drawRect(mBaseX,0,drawWidth,mHeight,bgPaint);
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
        new Thread(new WifiMutilMoveCompressUtil.GenFileThread()).start();
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
        Log.i("move","帧数 mframeCount = "+ mFrameCount);
        Log.i("move","时间轴 mTimeAxisList = "+ mTimeAxisList.size());

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
            // TODO: 2016/12/23 为测试WiFi，先不写入4096字节头
//            fos.write(mHeadBytes);//写入4096头
//            Log.i("move","写入头文件4096 byte...");

            fos.write(mFileHeadPart);
            Log.i("move","写入文件总头mFileHeadPart"+mFileHeadPart.length+"byte");

            fos.write(mItemPart);
            Log.i("move","写入节目区mItemPart"+mItemPart.length+"byte");

            fos.write(mTextAttrs);
            Log.i("move","写入字属性文件mTextAttrs"+mTextAttrs.length+"byte");

            //多加一块黑色图片，使其完整左移
            fos.write(mBlackBG);
            Log.i("move","写入黑色背景图片 backBG"+ mBlackBG.length+"byte");//写入黑色背景图片

            for (byte[] bytes : mTextContentList) {
                fos.write(bytes);
                Log.i("move","写入文件mContent"+bytes.length+"byte");
                //多加一块黑色图片，使其完整左移
                fos.write(mBlackBG);
                Log.i("move","写入黑色背景图片 backBG"+ mBlackBG.length+"byte");
            }

            for (int i = 0; i < mTimeAxisList.size(); i++) {
                fos.write(mTimeAxisList.get(i));
            }

            fos.flush();
            Log.i("move","genfile done--------------------------------");
            Message msg= new Message();
            msg.arg1=100;
            genFileHandler.sendMessage(msg);

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
