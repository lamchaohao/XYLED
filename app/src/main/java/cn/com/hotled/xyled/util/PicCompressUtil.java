package cn.com.hotled.xyled.util;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.com.hotled.xyled.activity.SendActivity;
import cn.com.hotled.xyled.bean.Program;
import cn.com.hotled.xyled.bean.ProgramType;
import cn.com.hotled.xyled.flowbound.ClockwiseFlow;

/**
 * Created by Lam on 2016/12/15.
 */

public class PicCompressUtil {

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
    private List<byte[]> mPicContentList;
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
    private boolean isNeedSend;
    private List<Program> mTextProgram;
    private List<Program> mPicProgram;
    private byte[] mPicAttrs=new byte[6];
    private Map<Integer, Integer> mFlowMap;

    public PicCompressUtil(Activity context, List<Program> programs, int screenWidth, int screenHeight, float frameTime, float stayTime) {
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
                    if (isNeedSend){
                        Toast.makeText(mContext, "录制完成，准备传输", Toast.LENGTH_SHORT).show();
//                        SendTest tcpSend=new SendTest(mContext,"192.168.3.1",16389,mColorPRG);
//                        tcpSend.send();
                        mContext.startActivity(new Intent(mContext, SendActivity.class));

                    }else {
                        Toast.makeText(mContext, "Wifi发送文件已生成", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };
        mTextProgram = new ArrayList<>();
        mPicProgram = new ArrayList<>();
        for (Program program : mProgramList) {
            if (program.getProgramType()== ProgramType.Text) {
                mTextProgram.add(program);
            }
            if (program.getProgramType()==ProgramType.Pic){
                mPicProgram.add(program);
            }
        }
    }

    public void setNeedSend(boolean needSend) {
        isNeedSend = needSend;
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

    /**
     * 初始化文字内容
     */
    private void initTextContent() {
        mProgramLengthList = new ArrayList<>();
        DrawBitmapUtil2 drawBitmapUtil =new DrawBitmapUtil2(mContext,mTextProgram,mTextScreenWidthList,mTextScreenHeightList);
        mBitmapList = drawBitmapUtil.drawBitmap();
        mTextContentList = new ArrayList<>();
        mColByteCountList = new ArrayList<>();
        for (int i = 0; i < mBitmapList.size(); i++) {
            mTextContentList.add(convertBitmapToPixel(mBitmapList.get(i),i));
        }
    }

    /**
     * 将bitmap中每个像素点颜色取出并且进行压缩
     * @param bitmap 需要转换的bitmap
     * @param index  节目的索引值
     * @return 压缩后的图片字节数组
     */
    private byte[] convertBitmapToPixel(Bitmap bitmap, int index) {
        byte[] bitmapPixels = BitmapToPixel.convertBitmapToPixel(bitmap);
        int widthToCompress = bitmap.getWidth();

        //取点后压缩
        CompressAlgorithm compressAlgorithm = new CompressAlgorithm();
        List<Byte> compress = compressAlgorithm.compress(bitmapPixels, widthToCompress, mTextScreenHeightList.get(index));//进行压缩
        byte[] textContent = new byte[compress.size()];
        for (int i = 0; i < textContent.length; i++) {
            textContent[i]=compress.get(i);
        }
        mColByteCountList.add(compressAlgorithm.getColByteCount());
        mProgramLengthList.add(widthToCompress);
        mFrameCount+=(widthToCompress);

        return textContent;
    }

    /**
     * 图片内容
     */
    private void initPicContent(){
        mPicContentList=new ArrayList<>();
        for (Program program : mPicProgram) {
            Bitmap bitmap = BitmapFactory.decodeFile(program.getPicFile().getAbsolutePath());
            byte[] bytes = BitmapToPixel.convertBitmapToPixel(bitmap);
            FullCompressAlgorithm full=new FullCompressAlgorithm();
            List<Byte> compress = full.compress(bytes);
            byte[]  picBytes=new byte[compress.size()];
            int i=0;
            for (Byte compres : compress) {
                picBytes[i]=compres;
                i++;
            }
            mPicContentList.add(picBytes);
        }

    }

    /**
     * 初始化黑色的背景图片，为列压缩
     */
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

    /**
     * 初始化流水边
     */
    private void initFlowBound() {
        mFlowByteList = new ArrayList<>();

        boolean[] useFlow=new boolean[mTextProgram.size()];

        for (int i = 0; i < mTextProgram.size(); i++) {
            useFlow[i]=mTextProgram.get(i).getUseFlowBound();
        }

        int useCount=0;
        for (boolean use : useFlow) {
            if (use) {
                useCount++;
            }
        }

        File[] flowFiles =new File[useCount];
        int[] flowStyle= new int[useCount];
        int fileIndex = 0;
        for (int i = 0; i < mTextProgram.size(); i++) {
            if (useFlow[i]){
                flowFiles[fileIndex]=mTextProgram.get(i).getFlowBoundFile();
                flowStyle[fileIndex]=mTextProgram.get(i).getFlowEffect();
                fileIndex++;
            }
        }

        ClockwiseFlow flow =new ClockwiseFlow(mScreenWidth,mScreenHeight);
        flow.setFlowFile(flowFiles);
        flow.setUseFlows(useFlow);
        flow.setProgramLength(mProgramLengthList);
        flow.setFlowStyle(flowStyle);

        mFlowByteList = flow.genFlowBound();
        //map中的key对应的是那一帧，value对应是mflowByteList中的index
        mFlowMap = flow.getFlowMap();
    }


    /**
     * 初始化文字属性
     */
    private void initTextAttrs() {
        mTextAttrsList = new ArrayList<>();
        mTextScreenHeightList = new ArrayList<>();
        mTextScreenWidthList=new ArrayList<>();
        //------------初始化图片的属性---------------
        byte[] picScreenStartAddress = intToByteArray(0, 2);
        byte[] picScreenWidthByte = intToByteArray(mScreenWidth, 2);
        //设置到文字属性
        mPicAttrs[0]=0;  //底图
        setInbyteArray(1,picScreenStartAddress,mPicAttrs);
        setInbyteArray(3, picScreenWidthByte, mPicAttrs);
        mPicAttrs[5]= (byte) (mScreenHeight);
        //-------------------------------------------------
        for (Program program : mProgramList) {
            byte[] tempTextAttr =new byte[6];
            if (program.getProgramType()==ProgramType.Text){
                //文字节目
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
            }else {
                //图片节目

            }

        }
    }

    /**
     * 初始化时间轴
     */
    private void initTimeAxis() {
        mTimeAxisList = new ArrayList<>();
        int textProgramIndex=0;
        int picProgramIndex=0;

        for (int i = 0; i < mProgramList.size(); i++) {
            ProgramType programType = mProgramList.get(i).getProgramType();
            if (programType==ProgramType.Pic){
                byte[] timeAxis = new byte[16];
                //时间
                timeAxis[0] = (byte) 50;
                timeAxis[1] = picStyle;
                int tempInc=0;
                for (int j = 0; j < picProgramIndex; j++) {
                    tempInc += mPicContentList.get(j).length;
                }
                int picContentAdd = mFileHeadPart.length + mItemPart.length + 6*(mTextAttrsList.size()+1)+mBlackBG.length+tempInc;
                int tempTextAddress= picContentAdd-tempInc-mBlackBG.length;//文字用黑色图片表示
                byte[] picAddress = intToByteArray(picContentAdd, 4);//当方式为跳向指定帧时这个地址是指向时间轴上的一个时间点(头0开始)
                byte[] textContentAddress = intToByteArray(tempTextAddress, 4);
                byte[] clockOrTem = new byte[3];

                setInbyteArray(2, picAddress, timeAxis);
                setInbyteArray(6, mPicAttrs, timeAxis);
                setInbyteArray(9, textContentAddress, timeAxis);
                setInbyteArray(13, clockOrTem, timeAxis);
                picProgramIndex++;
                //每个图片展示30帧
                for (int j = 0; j < 60; j++) {
                    mTimeAxisList.add(timeAxis);
                }
                mFrameCount+=60;//每个图片节目30帧
            }else {
                setTimeAxis(textProgramIndex,i);
                textProgramIndex++;
            }
        }

    }

    /**
     * 初始化每个文字节目的时间轴
     * @param textProgramIndex 第几个文字节目
     * @param programIndex  第几个节目
     */
    private void setTimeAxis(int textProgramIndex,int programIndex) {

        int flowBoundslength = 0;
        for (byte[] bytes : mFlowByteList) {
            flowBoundslength+=bytes.length;
        }
        int pictureLength=0;
        for (byte[] bytes : mPicContentList) {
            pictureLength+=bytes.length;
        }
        //文字地址  6*(mTextAttrsList.size()+1)加一代表把图片的文字属性加入
        int textContentAddressInt = mFileHeadPart.length + mItemPart.length + 6*(mTextAttrsList.size()+1) +mBlackBG.length+ pictureLength +flowBoundslength ;
        picStyle = 0;//1BIT地址指向(0=图层,1=跳转地址),7BIT未用

        for (int i = 0; i < mColByteCountList.get(textProgramIndex).length; i++) {
            byte[] timeAxis = new byte[16];
            //时间
            timeAxis[0] = (byte) mProgramList.get(programIndex).getFrameTime();
            timeAxis[1] = picStyle;
            //字属性地址
            int tempTextAddress = 0;
            //字内容地址 4byte
            if (i == 0 && textProgramIndex==0) {
                mTempColbyteCount = 0;
            }else if (i == 0 && textProgramIndex!=0){
                mTempColbyteCount += mColByteCountList.get(textProgramIndex-1)[mColByteCountList.get(textProgramIndex-1).length-1];
            } else {
                mTempColbyteCount += mColByteCountList.get(textProgramIndex)[i - 1];
            }
            //地图地址
            Integer flowIndex = mFlowMap.get(mFrameIndex);
            int tempFlowadd=0;
            for (int j = 0; j < flowIndex; j++) {
                tempFlowadd+=mFlowByteList.get(j).length;
            }
            tempTextAddress = textContentAddressInt + mTempColbyteCount;
            int tempPicAddress= textContentAddressInt-flowBoundslength+tempFlowadd;

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

    /**
     * 通过传入第几帧来计算该帧的文字属性的地址
     * @param frameIndex 第几帧
     * @return 文字属性地址的字节数组
     */
    private byte[] getAttrAddress(int frameIndex) {
        int attrStartAddress =  mFileHeadPart.length + mItemPart.length;
        int whichProgram=0;
        int currentFrame=0;
        currentFrame=mProgramLengthList.get(whichProgram)-mTextScreenWidthList.get(0);
//        currentFrame=mProgramLengthList.get(whichProgram);
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
        if (mTextScreenWidthList.size()!=0){
            mFrameCount -= mTextScreenWidthList.get(mTextScreenWidthList.size()-1);
        }
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
        int pictureLength=0;
        for (byte[] bytes : mPicContentList) {
            pictureLength+=bytes.length;
        }
        int timeAxisAddress = mFileHeadPart.length + mItemPart.length + 6*(mTextAttrsList.size()+1) + mBlackBG.length + pictureLength +flowBoundslength+ textContentLength ;
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


    public void startGenFile() {
        new Thread(new PicCompressUtil.GenFileThread()).start();
    }

    class GenFileThread implements Runnable {

        @Override
        public void run() {
            genFile();
        }
    }

    private void genFile() {

        initFileHead();
        initTextAttrs();
        initPicContent();
        initTextContent();
        initBlackBG();
        initFlowBound();
        initTimeAxis();
        initItemPart();
//        checkFault();
        Log.i("move", "帧数 mframeCount = " + mFrameCount);
        Log.i("move", "时间轴 mTimeAxisList = " + mTimeAxisList.size());

        mColorPRG = new File(mContext.getFilesDir()+"/color.prg");
        Log.i("move", "mColorPRG = " + mColorPRG.getAbsolutePath());
        if (mColorPRG.exists()) {
            Log.i("move", "文件已存在" + mColorPRG.getAbsolutePath());
            mColorPRG.delete();
            Log.i("move", "文件已存在" + mColorPRG.getAbsolutePath() + "并删除");
        }
        try {
            mColorPRG.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mColorPRG, true);
            // TODO: 2016/12/23 为测试WiFi，先不写入4096字节头

            fos.write(mFileHeadPart);
            Log.i("move", "写入文件总头mFileHeadPart " + mFileHeadPart.length + " byte");

            fos.write(mItemPart);
            Log.i("move", "写入节目区mItemPart" + mItemPart.length + " byte");

            for (byte[] bytes : mTextAttrsList) {
                fos.write(bytes);
                Log.i("move", "写入字属性文件mTextAttrsList" + bytes.length + " byte");
            }
            //写入图片节目属性
            fos.write(mPicAttrs);
            Log.i("move", "写入图片节目的字属性文件mPicAttrs" + mPicAttrs.length + " byte");
            //多加一块黑色图片，使其完整左移
            fos.write(mBlackBG);
            Log.i("move", "写入黑色背景图片 backBG " + mBlackBG.length + " byte");//写入黑色背景图片

            for (byte[] bytes : mPicContentList) {
                fos.write(bytes);
                Log.i("move", "写入图片节目 " + bytes.length + " byte");//写入黑色背景图片
            }

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
