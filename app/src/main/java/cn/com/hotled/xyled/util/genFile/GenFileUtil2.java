package cn.com.hotled.xyled.util.genFile;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.com.hotled.xyled.bean.Program;
import cn.com.hotled.xyled.bean.ProgramType;
import cn.com.hotled.xyled.bean.TextContent;
import cn.com.hotled.xyled.flowbound.GenFlow;
import cn.com.hotled.xyled.global.Global;
import cn.com.hotled.xyled.util.timeaxis.BaseTimeAxis;
import cn.com.hotled.xyled.util.timeaxis.MoveDown;
import cn.com.hotled.xyled.util.timeaxis.MoveLeft;
import cn.com.hotled.xyled.util.timeaxis.MoveRight;
import cn.com.hotled.xyled.util.timeaxis.MoveUp;
import cn.com.hotled.xyled.util.timeaxis.StaticShow;

import static cn.com.hotled.xyled.global.Global.GENFILE_DONE;
import static cn.com.hotled.xyled.util.genFile.ByteUtil.intToByteArray;
import static cn.com.hotled.xyled.util.genFile.ByteUtil.setInbyteArray;

/**
 * Created by Lam on 2017/2/24.
 * 1.节目头区
 * 2.文字属性
 * 3.图片节目内容
 * 4.文字节目内容
 * 5.初始化黑色的背景图片，为列压缩
 * 6.初始化流水边
 * 7.初始化时间轴
 * 8.初始化节目区
 */


public class GenFileUtil2 {
    private static final int FIRST_PRAT_LENGTH = 15;
    private static final int BLACK_BG_COL_BYTE_COUNT = 3;
    private byte[] mFileHeadPart=new byte[5];
    private byte[] mItemPart = new byte[10];
    private List<byte[]> mTextAttrsList;
    private List<Integer> mTextScreenHeightList;
    private List<Integer> mTextScreenWidthList;
    private Activity mActivity;
    private Handler mHandler;
    private int mScreenHeight;
    private int mScreenWidth;
    private byte[] mPicAttrs = new byte[6];
    private List<Program> mProgramList;
    private List<Program> mPicProgramList;
    private List<Program> mTextProgramList;
    private List<TextContent> mTextContentList;
    private List<byte[]> mPicContentList;
    private List<byte[]> mHorizontalTextByteList;
    private List<List<byte[]>> mVerticalTextByteList;
    private List<List<Integer>> mVerticalTextFrameCountList;//记录每个上下移节目的每一帧有多少个字节的list的List
    private List<byte[]> mColByteCountList;
    private List<byte[]> mFlowByteList;
    private Map<Integer, Integer> mFlowMap;
    private List<byte[]> mTimeAxisList;
    private byte[] mBlackBG;
    private int mFrameCount;
    private int mColbyteCountIndicator;
    private int mVerticalAddressIndicator;
    private int mTextFrameIndicator;
    private int[] mFrameCountArrays;
    private int mHorizontalIndex;
    private int mVerticalIndex;

    public GenFileUtil2(Activity activity, Handler handler,List<Program> programs, List<TextContent> contents, int screenWidth, int screenHeight) {
        mActivity = activity;
        mHandler=handler;
        mScreenWidth = screenWidth;
        mScreenHeight = screenHeight;
        mPicProgramList = new ArrayList<>();
        mTextProgramList = new ArrayList<>();
        mTextContentList = contents;
        mProgramList = programs;
        for (Program program : programs) {
            if (program.getProgramType()== ProgramType.Pic) {
                mPicProgramList.add(program);
            }else if (program.getProgramType()==ProgramType.Text){
                mTextProgramList.add(program);
            }
        }
    }

    /**
     * 1.初始化文件头 5bytes
     */
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
     * 2.初始化节目的文字属性(包含图片节目，文字节目)
     */
    private void initTextAttrs(){
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
        //------------------------------------------
        for (Program textProgram : mTextProgramList) {
            //使用流水边
            byte[] tempTextAttr = new byte[6];
            if (textProgram.getUseFlowBound()) {
                Bitmap bitmap = BitmapFactory.decodeFile(textProgram.getFlowBoundFile().getAbsolutePath());
                int height = bitmap.getHeight();
                byte[] screenStartAddress = ByteUtil.intToByteArray(mScreenHeight * height + height, 2);
                byte[] screenWidthByte = ByteUtil.intToByteArray(mScreenWidth - height * 2, 2);
                //设置到文字属性
                tempTextAttr[0]=0;
                setInbyteArray(1, screenStartAddress, tempTextAttr);
                setInbyteArray(3, screenWidthByte, tempTextAttr);
                tempTextAttr[5]= (byte) (mScreenHeight-height*2);
                //保存数据
                mTextAttrsList.add(tempTextAttr);
                mTextScreenHeightList.add(mScreenHeight-height*2);
                mTextScreenWidthList.add(mScreenWidth - height * 2);
            }else {
                //文字节目不使用流水边
                mTextAttrsList.add(mPicAttrs);
                mTextScreenHeightList.add(mScreenHeight);
                mTextScreenWidthList.add(mScreenWidth);
            }
        }
    }

    /**
     * 3.初始化图片节目内容，将图片转成byte[]
     * 图片节目是采用全压缩方式
     */
    private void initPicContent(){
        mPicContentList = new ArrayList<>();
        for (Program program : mPicProgramList) {
            Bitmap bitmap = BitmapFactory.decodeFile(program.getPicFile().getAbsolutePath());
            byte[] bytes = BitmapToPixel.convertBitmapToPixel(bitmap,mActivity);
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
     * 4.初始化文字节目内容 文字节目采用列压缩
     */
    private void initTextContent(){
        mFrameCountArrays = new int[mTextProgramList.size()];
        mVerticalTextFrameCountList = new ArrayList<>();
        mHorizontalTextByteList = new ArrayList<>();
        mVerticalTextByteList = new ArrayList<>();
        mColByteCountList = new ArrayList<>();
        int textProgramIndex=0;
        for (Program program : mTextProgramList) {
            DrawBitmapUtil3 bitmapUtil=new DrawBitmapUtil3(program,mTextContentList.get(textProgramIndex),mTextScreenWidthList.get(textProgramIndex),mTextScreenHeightList.get(textProgramIndex));
            Bitmap bitmap = bitmapUtil.drawBitmap();
            //左右移动
            if (mTextContentList.get(textProgramIndex).getTextEffect()<= Global.TEXT_EFFECT_STATIC) {
                convertBitmapHorizontal(bitmap,textProgramIndex );
            }else {
            //上下移动
                convertBitmapVertical(bitmap,mTextScreenHeightList.get(textProgramIndex),textProgramIndex);
            }
            textProgramIndex++;
        }


    }

    /**
     * 垂直方向上的转换文字内容，并采取压缩算法
     * @param bitmap 所需转换的bitmap
     * @param height 文字属性中的屏幕高度
     */
    private void convertBitmapVertical(Bitmap bitmap, int height,int textProgramIndex) {
        //取到每一帧所需的图片的字节数组
        List<byte[]> pixelsByteArrayList = BitmapToPixel.convertBitmapToPixelByVertical(bitmap, mActivity, height);
        List<Integer> tempSumCountForOnePrograms = new ArrayList<>();
        List<byte[]> textContentbyteList = new ArrayList<>();

        for (byte[] pixelsByteArray : pixelsByteArrayList) {
            CompressAlgorithm algorithm = new CompressAlgorithm();
//            pixelsByteArray 是一张与屏宽高一致的图片，上下移动就每一帧就是一张图片，pixelsByteArrayList存放了每一帧的图片，共有bitmap.getHeight()-屏高 张(帧)
//            每一帧图片进行压缩
            List<Byte> compress = algorithm.compress(pixelsByteArray, bitmap.getWidth(), height);
            byte[] textContent = new byte[compress.size()];
            for (int i = 0; i < textContent.length; i++) {
                textContent[i]=compress.get(i);
            }
            textContentbyteList.add(textContent);
            byte[] colByteCount = algorithm.getColByteCount();
            int sumCountForOneFrame=0;
            for (byte b : colByteCount) {
                sumCountForOneFrame+=b;
            }
            //保存到记录压缩后每一个帧有多少个字节的list
            tempSumCountForOnePrograms.add(sumCountForOneFrame);

        }
        mFrameCount += pixelsByteArrayList.size();
        mFrameCountArrays[textProgramIndex] = pixelsByteArrayList.size();
        mVerticalTextByteList.add(textContentbyteList);
        //把list保存到记录每个上下移节目每一帧有多少个字节的list中
        mVerticalTextFrameCountList.add(tempSumCountForOnePrograms);
    }

    /**
     * 水平方向的转换文字内容，采用压缩算法
     * @param bitmap 需要转化的bitmap
     * @param textIndex 文字节目的索引
     */
    private void convertBitmapHorizontal(Bitmap bitmap, int textIndex) {
        byte[] bitmapPixels = BitmapToPixel.convertBitmapToPixel(bitmap,mActivity);
        int widthToCompress = bitmap.getWidth();

        //取点后压缩
        CompressAlgorithm compressAlgorithm = new CompressAlgorithm();
        List<Byte> compress = compressAlgorithm.compress(bitmapPixels, widthToCompress, mTextScreenHeightList.get(textIndex));//进行压缩
        byte[] textContent = new byte[compress.size()];
        for (int i = 0; i < textContent.length; i++) {
            textContent[i]=compress.get(i);
        }
        mColByteCountList.add(compressAlgorithm.getColByteCount());

        if (mTextContentList.get(textIndex).getTextEffect()== Global.TEXT_EFFECT_STATIC) {
            float stayTime = mTextProgramList.get(textIndex).getStayTime();
            mFrameCountArrays[textIndex] = (int) (stayTime*20);
            mFrameCount += (int) (stayTime*20);
        }else {
            mFrameCountArrays[textIndex] = widthToCompress-mTextScreenWidthList.get(textIndex);
            mFrameCount += (widthToCompress-mTextScreenWidthList.get(textIndex));
        }

        mHorizontalTextByteList.add(textContent);
    }

    /**
     * 5.初始化黑色的背景图片，为列压缩
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
     * 6.初始化流水边
     */
    private void initFlowBound(){
        mFlowByteList = new ArrayList<>();
        boolean[] useFlow=new boolean[mTextProgramList.size()];

        for (int i = 0; i < mTextProgramList.size(); i++) {
            useFlow[i]=mTextProgramList.get(i).getUseFlowBound();
        }
        //计算多少个文字节目使用了流水边
        int useCount=0;
        for (boolean use : useFlow) {
            if (use) {
                useCount++;
            }
        }

        File[] flowFiles =new File[useCount];
        int[] flowStyle= new int[useCount];
        int fileIndex = 0;
        //取出流水边文件，流水边效果
        for (int i = 0; i < mTextProgramList.size(); i++) {
            if (useFlow[i]){
                flowFiles[fileIndex]=mTextProgramList.get(i).getFlowBoundFile();
                flowStyle[fileIndex]=mTextProgramList.get(i).getFlowEffect();
                fileIndex++;
            }
        }

        GenFlow genFlow = new GenFlow(mActivity,mScreenWidth,mScreenHeight,flowFiles,mFrameCountArrays,useFlow,flowStyle);
        mFlowByteList = genFlow.genFlowBound();
        //map中的key对应的是那一帧，value对应是mflowByteList中的index
        mFlowMap =genFlow.getFlowMap();

    }

    /**
     * 7.初始化时间轴
     */
    private void initTimeAxis(){
        mTimeAxisList = new ArrayList<>();
        int textProgramIndex=0;
        int picProgramIndex=0;
        for (Program program : mProgramList) {
            if (program.getProgramType()== ProgramType.Pic) {
                setPicProgramTimeAxis(picProgramIndex);
                picProgramIndex++;
            }else {
                setTextProgramTimeAxis(textProgramIndex);
                textProgramIndex++;
            }
        }

    }

    /**
     * 初始化文字节目时间轴
     * @param textProgramIndex 文字节目索引号
     */
    private void setTextProgramTimeAxis(int textProgramIndex) {
        int frameOfThisProgram=mFrameCountArrays[textProgramIndex];
        Log.i("genfile2","frameOfThis=="+frameOfThisProgram+",textProgramIndex="+textProgramIndex);
        //流水边所占空间 用于计算文字地址
        int flowBoundslength = 0;
        for (byte[] bytes : mFlowByteList) {
            flowBoundslength+=bytes.length;
        }
        //图片节目所占空间 用于计算文字地址
        int pictureLength=0;
        for (byte[] bytes : mPicContentList) {
            pictureLength+=bytes.length;
        }
        //文字地址  6*(mTextAttrsList.size()+1)加1代表把图片的文字属性加入
        int textContentAddressInt = FIRST_PRAT_LENGTH + 6*(mTextAttrsList.size()+1) +mBlackBG.length+ pictureLength +flowBoundslength ;

        Program program = mTextProgramList.get(textProgramIndex);
        byte[] attrAddress = getAttrAddress(textProgramIndex);
        Integer screenWidth = mTextScreenWidthList.get(textProgramIndex);
        Integer screenHeight = mTextScreenHeightList.get(textProgramIndex);
        BaseTimeAxis baseTimeAxis =null;
        switch (mTextContentList.get(textProgramIndex).getTextEffect()){
            case Global.TEXT_EFFECT_APPEAR_MOVE_LEFT:
                baseTimeAxis = new MoveLeft();
                baseTimeAxis.setProgram(program)
                        .setFlowMap(mFlowMap)
                        .setFlowByteList(mFlowByteList)
                        .setTimeAxisList(mTimeAxisList)
                        .setColbyteCountIndicator(mColbyteCountIndicator)
                        .setColByteCountList(mColByteCountList)
                        .setTextFrameIndicator(mTextFrameIndicator)
                        .setFrameCount(mFrameCount)
                        .setAttrAddress(attrAddress)
                        .setTimeAxis(textContentAddressInt,frameOfThisProgram,mHorizontalIndex,flowBoundslength,screenWidth);
                mColbyteCountIndicator = baseTimeAxis.getAddressIndicator();
                mFrameCount = baseTimeAxis.getFrameCount();
                mTextFrameIndicator = baseTimeAxis.getTextFrameIndicator();
                mHorizontalIndex++;
                break;
            case Global.TEXT_EFFECT_MOVE_LEFT:
                baseTimeAxis = new MoveLeft();
                baseTimeAxis.setProgram(program)
                        .setFlowMap(mFlowMap)
                        .setFlowByteList(mFlowByteList)
                        .setTimeAxisList(mTimeAxisList)
                        .setColbyteCountIndicator(mColbyteCountIndicator)
                        .setColByteCountList(mColByteCountList)
                        .setTextFrameIndicator(mTextFrameIndicator)
                        .setFrameCount(mFrameCount)
                        .setAttrAddress(attrAddress)
                        .setTimeAxis(textContentAddressInt,frameOfThisProgram,mHorizontalIndex,flowBoundslength,screenWidth);
                mColbyteCountIndicator = baseTimeAxis.getAddressIndicator();
                mFrameCount = baseTimeAxis.getFrameCount();
                mTextFrameIndicator = baseTimeAxis.getTextFrameIndicator();
                mHorizontalIndex++;
                break;
            case Global.TEXT_EFFECT_APPEAR_MOVE_RIGHT:
                baseTimeAxis = new MoveRight();
                baseTimeAxis.setProgram(program)
                        .setFlowMap(mFlowMap)
                        .setFlowByteList(mFlowByteList)
                        .setTimeAxisList(mTimeAxisList)
                        .setColbyteCountIndicator(mColbyteCountIndicator)
                        .setColByteCountList(mColByteCountList)
                        .setTextFrameIndicator(mTextFrameIndicator)
                        .setFrameCount(mFrameCount)
                        .setAttrAddress(attrAddress)
                        .setTimeAxis(textContentAddressInt,frameOfThisProgram,mHorizontalIndex,flowBoundslength,screenWidth);
                mColbyteCountIndicator = baseTimeAxis.getAddressIndicator();
                mFrameCount = baseTimeAxis.getFrameCount();
                mTextFrameIndicator = baseTimeAxis.getTextFrameIndicator();
                mHorizontalIndex++;
                break;
            case Global.TEXT_EFFECT_MOVE_RIGHT:
                baseTimeAxis = new MoveRight();
                baseTimeAxis.setProgram(program)
                        .setFlowMap(mFlowMap)
                        .setFlowByteList(mFlowByteList)
                        .setTimeAxisList(mTimeAxisList)
                        .setColbyteCountIndicator(mColbyteCountIndicator)
                        .setColByteCountList(mColByteCountList)
                        .setTextFrameIndicator(mTextFrameIndicator)
                        .setFrameCount(mFrameCount)
                        .setAttrAddress(attrAddress)
                        .setTimeAxis(textContentAddressInt,frameOfThisProgram,mHorizontalIndex,flowBoundslength,screenWidth);
                mColbyteCountIndicator = baseTimeAxis.getAddressIndicator();
                mFrameCount = baseTimeAxis.getFrameCount();
                mTextFrameIndicator = baseTimeAxis.getTextFrameIndicator();
                mHorizontalIndex++;
                break;
            case Global.TEXT_EFFECT_STATIC:
                baseTimeAxis = new StaticShow();
                baseTimeAxis.setProgram(program)
                        .setFlowMap(mFlowMap)
                        .setFlowByteList(mFlowByteList)
                        .setTimeAxisList(mTimeAxisList)
                        .setColbyteCountIndicator(mColbyteCountIndicator)
                        .setColByteCountList(mColByteCountList)
                        .setTextFrameIndicator(mTextFrameIndicator)
                        .setFrameCount(mFrameCount)
                        .setAttrAddress(attrAddress)
                        .setTimeAxis(textContentAddressInt,frameOfThisProgram,mHorizontalIndex,flowBoundslength,screenWidth);
                mColbyteCountIndicator = baseTimeAxis.getAddressIndicator();
                mFrameCount = baseTimeAxis.getFrameCount();
                mTextFrameIndicator = baseTimeAxis.getTextFrameIndicator();
                mHorizontalIndex++;
                break;
            case Global.TEXT_EFFECT_MOVE_UP:
                baseTimeAxis = new MoveUp();
                baseTimeAxis.setProgram(program)
                        .setFlowMap(mFlowMap)
                        .setFlowByteList(mFlowByteList)
                        .setTimeAxisList(mTimeAxisList)
                        .setVerticalAddressIndicator(mVerticalAddressIndicator)
                        .setVerticalTextFrameCountList(mVerticalTextFrameCountList)
                        .setHorizontalTextByteList(mHorizontalTextByteList)
                        .setTextFrameIndicator(mTextFrameIndicator)
                        .setFrameCount(mFrameCount)
                        .setAttrAddress(attrAddress)
                        .setTimeAxis(textContentAddressInt,frameOfThisProgram,mVerticalIndex,flowBoundslength,screenHeight);
                mVerticalAddressIndicator = baseTimeAxis.getAddressIndicator();
                mFrameCount = baseTimeAxis.getFrameCount();
                mTextFrameIndicator = baseTimeAxis.getTextFrameIndicator();
                mVerticalIndex++;
                break;
            case Global.TEXT_EFFECT_MOVE_DOWN:
                baseTimeAxis = new MoveDown();
                baseTimeAxis.setProgram(program)
                        .setFlowMap(mFlowMap)
                        .setFlowByteList(mFlowByteList)
                        .setTimeAxisList(mTimeAxisList)
                        .setVerticalAddressIndicator(mVerticalAddressIndicator)
                        .setVerticalTextFrameCountList(mVerticalTextFrameCountList)
                        .setHorizontalTextByteList(mHorizontalTextByteList)
                        .setTextFrameIndicator(mTextFrameIndicator)
                        .setFrameCount(mFrameCount)
                        .setAttrAddress(attrAddress)
                        .setTimeAxis(textContentAddressInt,frameOfThisProgram,mVerticalIndex,flowBoundslength,screenHeight);
                mVerticalAddressIndicator = baseTimeAxis.getAddressIndicator();
                mFrameCount = baseTimeAxis.getFrameCount();
                mTextFrameIndicator = baseTimeAxis.getTextFrameIndicator();
                mVerticalIndex++;
                break;
        }


    }



    private void setPicProgramTimeAxis(int picProgramIndex) {
        byte[] timeAxis = new byte[16];
        timeAxis[0] = (byte) 100;
        timeAxis[1] = 0;
        int tempInc=0;
        for (int j = 0; j < picProgramIndex; j++) {
            tempInc += mPicContentList.get(j).length;//将其前面的图片地址累加
        }
        int picContentAdd = FIRST_PRAT_LENGTH + 6*(mTextAttrsList.size()+1)+mBlackBG.length+tempInc;
        int tempTextAddress= picContentAdd-tempInc-mBlackBG.length;//文字用黑色图片表示
        byte[] picAddress = intToByteArray(picContentAdd, 4);//当方式为跳向指定帧时这个地址是指向时间轴上的一个时间点(头0开始)
        byte[] textContentAddress = intToByteArray(tempTextAddress, 4);
        byte[] clockOrTem = new byte[3];

        setInbyteArray(2, picAddress, timeAxis);
        setInbyteArray(6, mPicAttrs, timeAxis);
        setInbyteArray(9, textContentAddress, timeAxis);
        setInbyteArray(13, clockOrTem, timeAxis);
        Program program = mPicProgramList.get(picProgramIndex);
        float stayTime = program.getStayTime();
        stayTime*=10;
        for (int j = 0; j < stayTime; j++) {
            mTimeAxisList.add(timeAxis);
        }
        mFrameCount+=stayTime;//每个图片节目帧数加上
    }

    /**
     * 通过传入节目索引来计算该帧的文字属性的地址
     * @param programIndex 文字节目索引
     * @return 文字属性地址的字节数组
     */
    private byte[] getAttrAddress(int programIndex) {
        int attrStartAddress =  FIRST_PRAT_LENGTH;
        for (int i = 0; i < programIndex; i++) {
            attrStartAddress+=mTextAttrsList.get(i).length;
        }
        return intToByteArray(attrStartAddress, 3);
    }

    /**
     * 8.初始化节目区
     */
    private void initItemPart() {

        if (mTextScreenWidthList.size()!=0){

        }
        byte[] frameCountByte = intToByteArray(mFrameCount, 2);
        setInbyteArray(1, frameCountByte, mItemPart);

        int textContentLength = 0;
        //水平方向的
        for (byte[] bytes : mHorizontalTextByteList) {
            textContentLength += bytes.length;
        }//垂直方向的
        for (List<byte[]> list : mVerticalTextByteList) {
            for (byte[] bytes : list) {
                textContentLength += bytes.length;
            }
        }
        int flowBoundslength = 0;
        for (byte[] bytes : mFlowByteList) {
            flowBoundslength+=bytes.length;
        }
        int pictureLength=0;
        for (byte[] bytes : mPicContentList) {
            pictureLength+=bytes.length;
        }
        int timeAxisAddress = FIRST_PRAT_LENGTH+ 6*(mTextAttrsList.size()+1) + mBlackBG.length + pictureLength +flowBoundslength+ textContentLength ;
        setInbyteArray(6, intToByteArray(timeAxisAddress, 4), mItemPart);
    }


    public void startGenFile(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                genFile();
            }
        }).start();
    }

    private void genFile(){

            initFileHead();
            initTextAttrs();
            initPicContent();
            initTextContent();
            initBlackBG();
            initFlowBound();
            initTimeAxis();
            initItemPart();
            Log.i("genfile2", "帧数 mframeCount = " + mFrameCount);
            Log.i("genfile2", "时间轴 mTimeAxisList = " + mTimeAxisList.size());

            File colorPRGFile = new File(mActivity.getFilesDir()+"/color.prg");
            if (colorPRGFile.exists()) {
                colorPRGFile.delete();
            }
            try {
                colorPRGFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(colorPRGFile, true);

                fos.write(mFileHeadPart);

                fos.write(mItemPart);

                for (byte[] bytes : mTextAttrsList) {
                    fos.write(bytes);
                }
                //写入图片节目属性
                fos.write(mPicAttrs);

                fos.write(mBlackBG);

                for (byte[] bytes : mPicContentList) {
                    fos.write(bytes);
                }

                for (byte[] bytes : mFlowByteList) {
                    fos.write(bytes);
                }

                for (byte[] bytes : mHorizontalTextByteList) {
                    fos.write(bytes);
                }

                for (List<byte[]> list : mVerticalTextByteList) {
                    for (byte[] bytes : list) {
                        fos.write(bytes);
                    }
                }

                for (int i = 0; i < mTimeAxisList.size(); i++) {
                    fos.write(mTimeAxisList.get(i));
                }

                fos.flush();
                Log.i("genfile2", "genfile done--------------------------------");

                Message message = mHandler.obtainMessage();
                message.what= GENFILE_DONE;
                mHandler.sendMessage(message);

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
