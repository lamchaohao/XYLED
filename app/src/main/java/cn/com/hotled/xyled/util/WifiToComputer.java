package cn.com.hotled.xyled.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import cn.com.hotled.xyled.bean.Program;
import cn.com.hotled.xyled.flowbound.AntiClockWiseFlow;

/**
 * Created by Lam on 2016/12/15.
 */

public class WifiToComputer {

    private static final int BLACK_BG_COL_BYTE_COUNT = 3;
    private static final int TEXT_ATTRS_LENGTH = 6;
    //文件总头区 5byte

    private byte[] mFileHeadPart = new byte[5];

    //节目区 10byte
    private byte[] mItemPart = new byte[10];

    //文字属性区 6byte

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
    private Handler genFileHandler;

    private List<byte[]> mColByteCountList;

    private List<Bitmap> mBitmapList;
    private int mTempColbyteCount;
    private List<byte[]> mFlowByteList;
    private int mFrameIndex = 0;
    private int mFlowAddress=0;
    private List<byte[]> mTextAttrsList;
    private List<Integer> mTextScreenHeightList;
    private List<Integer> mTextScreenWidthList;
    private List<Integer> mProgramLengthList;
    private byte[] mHeadBytes;

    public WifiToComputer(Activity context, List<Program> programs, int screenWidth, int screenHeight, float frameTime, float stayTime) {
        mContext = context;
        mProgramList = programs;
        mScreenWidth = screenWidth;
        mScreenHeight = screenHeight;
//        mFrameTime = (byte) frameTime;
//        mStayTime = (byte) stayTime;
        genFileHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.arg1 == 100) {
                    Toast.makeText(mContext, "此节目文件已生成", Toast.LENGTH_SHORT).show();
                }
            }
        };

    }

    private void initFileHead() {

        byte fileHeadLength = 4;          //总头长度
        byte itemCount = 1;              //节目个数
        byte itemTableLength = 10;       //节目表长度
        byte frameHeadLength = 16;       //帧头长度
        byte textAttrsLength = 6;         //字层性长度
        mFileHeadPart[0] = fileHeadLength;
        mFileHeadPart[1] = itemCount;
        mFileHeadPart[2] = itemTableLength;
        mFileHeadPart[3] = frameHeadLength;
        mFileHeadPart[4] = textAttrsLength;
    }

    private void initTextContent() {
        mProgramLengthList = new ArrayList<>();
        DrawBitmapUtil drawBitmapUtil =new DrawBitmapUtil(mContext,mProgramList,mScreenWidth,mTextScreenHeightList);
        mBitmapList = drawBitmapUtil.drawBitmap();
        mTextContentList = new ArrayList<>();
        mColByteCountList = new ArrayList<>();
        for (int i = 0; i < mBitmapList.size(); i++) {
            mTextContentList.add(convertBitmapToPixel(mBitmapList.get(i),i));
        }
    }

    private byte[] convertBitmapToPixel(Bitmap bitmap, int index) {
        byte[] bitmapPixels = null;
        int xStart = 0;
        int yStart = 0;
        bitmapPixels = new byte[bitmap.getWidth() * bitmap.getHeight()];
        int widthToCompress = bitmap.getWidth();
        for (int x1 = 0, i = xStart*yStart; x1 < bitmap.getWidth(); x1++) {
            for (int y1 = 0; y1 < bitmap.getHeight(); y1++) {
                int pixel = bitmap.getPixel(x1, y1);
                int blue = 0;
                int green = 0;
                int red = 0;
                String hexStr = Integer.toHexString(pixel);
                if (pixel == 0 && hexStr.length() < 6) {
//                    Log.i("move","no color,x = "+x1+", y= "+y1);
                } else if (hexStr.length() >= 6) {
                    int length = hexStr.length();
                    hexStr = hexStr.substring(length - 6);//保证要有6位数字
                    red = Integer.parseInt(hexStr.substring(0, 2), 16);//使用十六进制
                    green = Integer.parseInt(hexStr.substring(2, 4), 16);
                    blue = Integer.parseInt(hexStr.substring(4, 6), 16);
                }
                // 因为rgb，分别用一个int来表示，值为0-255
                blue = blue / 85;
                green = green / 85;
                red = red / 85;
                // 最终要转换成一个byte中的低6位，每个颜色用两位来表示，序列是 bb gg rr，每种颜色最多有4种可能，00,01,10,11
                // 最若全蓝 110000 =,48,100000=32,010000=16，
                blue = blue * 16;
                green = green * 4;
                red = red * 1;

                byte color = (byte) (blue + green + red);
                bitmapPixels[i] = color;
                i++;
            }
        }

        //取点后压缩
        CompressAlgorithm compressAlgorithm = new CompressAlgorithm();
        List<Byte> compress = compressAlgorithm.compress(bitmapPixels, widthToCompress, mTextScreenHeightList.get(index));//进行压缩
        //当前节目所使用的屏幕宽度
        int currentScreenWidth=mTextScreenWidthList.get(index);
        //黑色过渡图片
        byte[] blackBgBytes = addABlack(mTextScreenHeightList.get(index),currentScreenWidth);
        //包括两张黑色图片与正文的文字内容字节数组
        byte[] textContent = new byte[compress.size()+blackBgBytes.length*2];
        //此节目中压缩后的每一列的所需用到的字节数
        byte[] tempColByteCount=new byte[widthToCompress+currentScreenWidth*2];

        //前 黑色背景
        for (int i=0;i<blackBgBytes.length;i++){
            textContent[i] = blackBgBytes[i];
        }
        for (int i=0;i<currentScreenWidth;i++){
            tempColByteCount[i]=BLACK_BG_COL_BYTE_COUNT;
        }

        //中 正文
        for (int i = 0,textStart=blackBgBytes.length; i < compress.size(); i++) {
            textContent[textStart+i] = compress.get(i);
        }
        byte[] colByteCount = compressAlgorithm.getColByteCount();
        for (int i = 0; i < colByteCount.length; i++) {
            tempColByteCount[currentScreenWidth+i]=colByteCount[i];
        }

        //后 黑色背景
        for (int i = 0,textStart=compress.size()+blackBgBytes.length; i < blackBgBytes.length; i++) {
            textContent[textStart+i] = blackBgBytes[i];
        }
        for (int i=colByteCount.length+currentScreenWidth;i<tempColByteCount.length;i++){
            tempColByteCount[i]=BLACK_BG_COL_BYTE_COUNT;
        }

        mColByteCountList.add(tempColByteCount);
        mProgramLengthList.add(widthToCompress+currentScreenWidth*2);
        mFrameCount+=(widthToCompress+currentScreenWidth*2);
        return textContent;
    }


    private byte[] addABlack(int height,int currentScreenWidth){
        byte count = (byte) (height-8);
        byte blackColor = 64;
        byte[] blackBg=new byte[BLACK_BG_COL_BYTE_COUNT*currentScreenWidth];
        int index = 0;
        for (int i = 0; i < blackBg.length; i++) {
            blackBg[i] = blackColor;
            index++;
            if (index % BLACK_BG_COL_BYTE_COUNT == 0) {
                blackBg[i] = count;
            }
        }
        return blackBg;
    }

    private void initBlackBG() {
        byte count = (byte) (mScreenHeight-8);
        byte blackColor = 64;
        mBlackBG = new byte[BLACK_BG_COL_BYTE_COUNT * (mScreenWidth)];
        int index = 0;
        for (int i = 0; i < mBlackBG.length; i++) {
            mBlackBG[i] = blackColor;
            index++;
            if (index % BLACK_BG_COL_BYTE_COUNT == 0) {
                mBlackBG[i] = count;
            }
        }

    }

    private void initFlowBound() {
        mFlowByteList = new ArrayList<>();

        boolean[] useFlow=new boolean[mProgramList.size()];

        for (int i = 0; i < mProgramList.size(); i++) {
            useFlow[i]=mProgramList.get(i).getUseFlowBound();
        }

        int useCount=0;
        for (boolean use : useFlow) {
            if (use) {
                useCount++;
            }
        }

        File[] flowFiles =new File[useCount];
        int fileIndex = 0;
        for (int i = 0; i < mProgramList.size(); i++) {
            if (useFlow[i]){
                flowFiles[fileIndex]=mProgramList.get(i).getFlowBoundFile();
                fileIndex++;
            }
        }

        byte[] color={3,12,16};
        AntiClockWiseFlow flow =new AntiClockWiseFlow(mScreenWidth,mScreenHeight);
        flow.setFlowFile(flowFiles);
        flow.setUseFlows(useFlow);
        flow.setProgramLength(mProgramLengthList);
        mFlowByteList = flow.genFlowBound();
    }




    private void initTextAttrs() {
        mTextAttrsList = new ArrayList<>();
        mTextScreenHeightList = new ArrayList<>();
        mTextScreenWidthList=new ArrayList<>();
        for (Program program : mProgramList) {
            byte[] tempTextAttr =new byte[6];
            if (program.getUseFlowBound()) {
                //初始化数据
                Bitmap bitmap = BitmapFactory.decodeFile(program.getFlowBoundFile().getAbsolutePath());
                int height = bitmap.getHeight();
                byte[] screenStartAddress = intToByteArray(mScreenHeight * height + height, 2);
                byte[] screenWidthByte = intToByteArray(mScreenWidth - height * 2, 2);
                //设置到文字属性
                tempTextAttr[0]=0;
                setInbyteArray(1, screenStartAddress, tempTextAttr);
                setInbyteArray(3, screenWidthByte, tempTextAttr);
                tempTextAttr[5]= (byte) (mScreenHeight-height*2);
                //保存到list
                mTextAttrsList.add(tempTextAttr);
                mTextScreenHeightList.add(mScreenHeight-height*2);
                mTextScreenWidthList.add(mScreenWidth - height * 2);
            }else {
                //如果没有使用流水边
                byte[] screenStartAddress = intToByteArray(0, 2);
                byte[] screenWidthByte = intToByteArray(mScreenWidth, 2);
                //设置到文字属性
                tempTextAttr[0]=0;  //底图
                setInbyteArray(1,screenStartAddress,tempTextAttr);
                setInbyteArray(3, screenWidthByte, tempTextAttr);
                tempTextAttr[5]= (byte) (mScreenHeight);
                //保存到list
                mTextAttrsList.add(tempTextAttr);
                mTextScreenHeightList.add(mScreenHeight);
                mTextScreenWidthList.add(mScreenWidth);
            }
        }
    }


    private void initTimeAxis() {
        mTimeAxisList = new ArrayList<>();
        for (int i = 0; i < mProgramList.size(); i++) {
            setTimeAxis(i);
        }

    }

    private void setTimeAxis(int index) {
        //文字属性地址 3byte
        int attrStartAddress =  mFileHeadPart.length + mItemPart.length;
        int whichProgram=0;
        int currentFrame=mProgramLengthList.get(whichProgram)-mTextScreenWidthList.get(whichProgram);

        while(mFrameIndex>currentFrame&&whichProgram<mTextAttrsList.size()-1){
            whichProgram++;
            currentFrame+=mProgramLengthList.get(whichProgram);
        }
        attrStartAddress=attrStartAddress+(whichProgram)*TEXT_ATTRS_LENGTH;

        int flowBoundslength = 0;
        for (byte[] bytes : mFlowByteList) {
            flowBoundslength+=bytes.length;
        }
        //文字地址
        int textContentAddressInt = mFileHeadPart.length + mItemPart.length + 6*mTextAttrsList.size() + mBlackBG.length +flowBoundslength ;
        picStyle = 0;//1BIT地址指向(0=图层,1=跳转地址),7BIT未用

        for (int i = 0; i < mColByteCountList.get(index).length; i++) {
            byte[] timeAxis = new byte[16];
            //时间
            timeAxis[0] = (byte) mProgramList.get(index).getFrameTime();
            timeAxis[1] = picStyle;
            //字属性地址
            int tempTextAddress = 0;
            //字内容地址 4byte
            if (i == 0 && index==0) {
                mTempColbyteCount = 0;
            }else if (i == 0 && index!=0){
                mTempColbyteCount += mColByteCountList.get(index-1)[mColByteCountList.get(index-1).length-1];
            } else {
                mTempColbyteCount += mColByteCountList.get(index)[i - 1];
            }
            mFlowAddress+=mFlowByteList.get(mFrameIndex).length;
            tempTextAddress = textContentAddressInt + mTempColbyteCount;
            int tempPicAddress= textContentAddressInt-flowBoundslength+mFlowAddress;

            byte[] picAddress = intToByteArray(tempPicAddress, 4);//当方式为跳向指定帧时这个地址是指向时间轴上的一个时间点(头0开始)
//            byte[] attrAddress = intToByteArray(attrStartAddress, 3);
            byte[] attrAddress = getAttrAddress(mFrameIndex);
            byte[] textContentAddress = intToByteArray(tempTextAddress, 4);
            byte[] clockOrTem = new byte[3];

            setInbyteArray(2, picAddress, timeAxis);
            setInbyteArray(6, attrAddress, timeAxis);
            setInbyteArray(9, textContentAddress, timeAxis);
            setInbyteArray(13, clockOrTem, timeAxis);
            mFrameIndex++;
            mTimeAxisList.add(timeAxis);
        }

    }

    private byte[] getAttrAddress(int frameIndex) {
        int attrStartAddress =  mFileHeadPart.length + mItemPart.length;
        int whichProgram=0;
        int currentFrame=mProgramLengthList.get(whichProgram)-mTextScreenWidthList.get(whichProgram);
        while(frameIndex>currentFrame&&whichProgram<mTextAttrsList.size()-1){
            whichProgram++;
            currentFrame+=mProgramLengthList.get(whichProgram);
        }
        int other=attrStartAddress+(whichProgram)*6;
        return intToByteArray(other, 3);
    }

    private void checkFault() {
        int i=0;
        int attrStartAddress =  mFileHeadPart.length + mItemPart.length;
        for (byte[] bytes : mTimeAxisList) {
            int whichProgram=0;
            int currentFrame=mProgramLengthList.get(whichProgram)-mTextScreenWidthList.get(whichProgram);
            while(i>currentFrame&&whichProgram<mTextAttrsList.size()-1){
                whichProgram++;
                currentFrame+=mProgramLengthList.get(whichProgram);
            }
            int other=attrStartAddress+(whichProgram)*6;
            byte[] textAtt = intToByteArray(other, 3);
            setInbyteArray(6,textAtt,bytes);
            i++;
        }
    }

    private void initItemPart() {
        mFrameCount -= mTextScreenWidthList.get(mTextScreenWidthList.size()-1);
        byte[] frameCountByte = intToByteArray(mFrameCount, 2);
        setInbyteArray(1, frameCountByte, mItemPart);
        int textContentLength = 0;
        for (byte[] bytes : mTextContentList) {
            textContentLength += bytes.length;
        }
        int flowBoundslength = 0;
        for (byte[] bytes : mFlowByteList) {
            flowBoundslength+=bytes.length;
        }
        int timeAxisAddress = mFileHeadPart.length + mItemPart.length + 6*mTextAttrsList.size() + mBlackBG.length +flowBoundslength+ textContentLength ;
        setInbyteArray(6, intToByteArray(timeAxisAddress, 4), mItemPart);
    }

    /**
     * @param source          源数值
     * @param byteArrayLength 要转变成的byte数组长度
     * @return
     */
    private byte[] intToByteArray(int source, int byteArrayLength) {
        byte[] result = new byte[byteArrayLength];
        for (int length = byteArrayLength, index = 0; length > 0; length--, index++) {
            int bitCount = (length - 1) * 8;
            int temp = source;
            temp = temp >> bitCount; //移位
            result[index] = (byte) (temp & 0xff);
        }
        return result;
    }

    /**
     * @param targetStart 要赋值的目标数组的开始序列,从0开始
     * @param source      源数组
     * @param target      目标数组
     */
    private void setInbyteArray(int targetStart, byte[] source, byte[] target) {
        for (int i = 0; i < source.length; i++) {
            target[targetStart + i] = source[i];
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
    public void startGenFile() {
        new Thread(new WifiToComputer.GenFileThread()).start();
    }

    class GenFileThread implements Runnable {

        @Override
        public void run() {
            genFile();
        }
    }

    private void genFile() {
        setHeadBytes();
        initFileHead();
        initTextAttrs();
        initTextContent();
        initBlackBG();
        initFlowBound();
        initTimeAxis();
        initItemPart();
        Log.i("move", "帧数 mframeCount = " + mFrameCount);
        Log.i("move", "时间轴 mTimeAxisList = " + mTimeAxisList.size());

        mColorPRG = new File(Environment.getExternalStorageDirectory() + "/amap/COLOR_01.PRG");
        if (mColorPRG.exists()) {
            Log.i("move", "文件已存在" + mColorPRG.getAbsolutePath());
            mColorPRG.delete();
            try {
                mColorPRG.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.i("move", "文件已存在" + mColorPRG.getAbsolutePath() + "并删除");
        } else {
            try {
                mColorPRG.createNewFile();
                Log.i("move", "生成新文件" + mColorPRG.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mColorPRG, true);
            fos.write(mHeadBytes);
            Log.i("move", "写入总头mHeadBytes " + mHeadBytes.length + " byte");
            fos.write(mFileHeadPart);
            Log.i("move", "写入文件总头mFileHeadPart " + mFileHeadPart.length + " byte");

            fos.write(mItemPart);
            Log.i("move", "写入节目区mItemPart" + mItemPart.length + " byte");

            for (byte[] bytes : mTextAttrsList) {
                fos.write(bytes);
                Log.i("move", "写入字属性文件mTextAttrsList" + bytes.length + " byte");
            }

            //多加一块黑色图片，使其完整左移
            fos.write(mBlackBG);
            Log.i("move", "写入黑色背景图片 backBG " + mBlackBG.length + " byte");//写入黑色背景图片

            for (byte[] bytes : mFlowByteList) {
                fos.write(bytes);
            }
            Log.i("move", "写入流水边 mFlowByteList " + mFlowByteList.size() + " byte");//写入黑色背景图片
            for (byte[] bytes : mTextContentList) {
                fos.write(bytes);
                Log.i("move", "写入文件mContent " + bytes.length + " byte");
            }

            for (int i = 0; i < mTimeAxisList.size(); i++) {
                fos.write(mTimeAxisList.get(i));
            }

            fos.flush();
            Log.i("move", "genfile done--------------------------------");
            Message msg = new Message();
            msg.arg1 = 100;
            genFileHandler.sendMessage(msg);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null)
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }


}
