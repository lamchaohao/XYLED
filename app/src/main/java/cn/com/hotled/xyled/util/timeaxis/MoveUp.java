package cn.com.hotled.xyled.util.timeaxis;

import java.util.List;
import java.util.Map;

import cn.com.hotled.xyled.bean.Program;

import static cn.com.hotled.xyled.util.genFile.ByteUtil.intToByteArray;
import static cn.com.hotled.xyled.util.genFile.ByteUtil.setInbyteArray;

/**
 * Created by Lam on 2017/3/4.
 */

public class MoveUp {
    private Program mProgram;
    private Map<Integer, Integer> mFlowMap;
    private List<byte[]> mFlowByteList;
    private List<byte[]> mTimeAxisList;
    private int mVerticalAddressIndicator;
    private List<List<Integer>> mVerticalTextFrameCountList;//记录每个上下移节目的每一帧有多少个字节的list的List
    private List<byte[]> mHorizontalTextByteList;
    private int mTextFrameIndicator;
    private int mFrameCount;
    private byte[] attrAddress;

    public MoveUp(Program program, Map<Integer, Integer> flowMap, List<byte[]> flowByteList, List<byte[]> timeAxisList, int verticalAddressIndicator, List<List<Integer>> verticalTextFrameCountList, List<byte[]> horizontalTextByteList, int textFrameIndicator, int frameCount, byte[] attrAddress) {

        mProgram = program;
        mFlowMap = flowMap;
        mFlowByteList = flowByteList;
        mTimeAxisList = timeAxisList;
        mVerticalAddressIndicator = verticalAddressIndicator;
        mVerticalTextFrameCountList = verticalTextFrameCountList;
        mHorizontalTextByteList = horizontalTextByteList;
        mTextFrameIndicator = textFrameIndicator;
        mFrameCount = frameCount;
        this.attrAddress = attrAddress;
    }

    public void setTimeAxis(int textContentAddressInt, int frameOfThisProgram, int verticalIndex, int flowBoundslength,int screenHeight){
        List<Integer> frameCountList = mVerticalTextFrameCountList.get(verticalIndex);
        int horizontalLength = 0;
        for (byte[] bytes : mHorizontalTextByteList) {
            horizontalLength += bytes.length;
        }
        //文字属性地址
        float frameTime = mProgram.getFrameTime();
        //流水边速度
        int flowSpeed = mProgram.getFlowSpeed();
        //先获取所有的流水边地址
        int[] flowAddress= new int[frameOfThisProgram];
        //也获取所有的文字地址
        int[] textAddress = new int[frameOfThisProgram];
        for (int i = 0; i < frameOfThisProgram; i++) {
            int tempTextAddress=textContentAddressInt + horizontalLength;
            byte[] timeAxis = new byte[16];
            timeAxis[0] = (byte) frameTime;
            timeAxis[1] = 0;
            //文字地址
            if (i==0&&verticalIndex==0){
                mVerticalAddressIndicator=0;
            }else if (i==0&&verticalIndex>0){
                List<Integer> lastProgram = mVerticalTextFrameCountList.get(verticalIndex - 1);
                Integer length = lastProgram.get(lastProgram.size() - 1);
                mVerticalAddressIndicator += length;
            }else {
                Integer frameBytesCount = frameCountList.get(i - 1);
                mVerticalAddressIndicator+=frameBytesCount;
            }
            tempTextAddress += mVerticalAddressIndicator;
            textAddress[i] = tempTextAddress;
            //流水边地址
            int tempFlowAdd=0;
            if (mFlowMap!=null) {
                Integer flowIndex = mFlowMap.get(mTextFrameIndicator);
                for (int j = 0; flowIndex!=null&&j < flowIndex; j++) {
                    tempFlowAdd+=mFlowByteList.get(j).length;
                }
            }
            int tempPicAddress= textContentAddressInt-flowBoundslength+tempFlowAdd;
            flowAddress[i] =tempPicAddress;
            mTextFrameIndicator++;
        }
        int flowIndicator =0;
        float stayTime = mProgram.getStayTime();
        for (int i = 0; i < frameOfThisProgram; i++) {
            byte[] timeAxis = new byte[16];
            timeAxis[0] = (byte) frameTime;
            timeAxis[1] = 0;
            //文字地址
            int tempTextAddress = textAddress[i];
            //流水边地址
            int tempPicAddress = 0;

            if (flowSpeed == 0) {
                tempPicAddress = flowAddress[0]; //静止
            } else {
                if (i == 0) {
                    flowIndicator = 0;
                } else if (i % flowSpeed == 0) {
                    flowIndicator++;
                }
                if (flowIndicator >= flowAddress.length) {
                    flowIndicator -= flowAddress.length;
                }
                tempPicAddress = flowAddress[flowIndicator];//流水边地址
            }
            byte[] picAddress = intToByteArray(tempPicAddress, 4);//当方式为跳向指定帧时这个地址是指向时间轴上的一个时间点(头0开始)
            byte[] textContentAddress = intToByteArray(tempTextAddress, 4);
            byte[] clockOrTem = new byte[3];

            setInbyteArray(2, picAddress, timeAxis);
            setInbyteArray(6, attrAddress, timeAxis);
            setInbyteArray(9, textContentAddress, timeAxis);
            setInbyteArray(13, clockOrTem, timeAxis);

            mTimeAxisList.add(timeAxis);
            if (i % screenHeight == 0) {
                int stayFrame = (int) (stayTime * screenHeight / 2);
                for (int j = 0; j < stayFrame; j++) {
                    byte[] timeAxisStay = new byte[16];
                    timeAxisStay[0] = (byte) frameTime;
                    timeAxisStay[1] = 0;
                    if (flowSpeed == 0) {
                        //nothing for avoid divide by zero
                    } else if (j % flowSpeed == 0) {
                        flowIndicator++;
                    }
                    if (flowIndicator >= flowAddress.length) {
                        flowIndicator -= flowAddress.length;
                    }
                    int stayPicAddress =flowAddress[flowIndicator];
                    byte[] staypicAddress = intToByteArray(stayPicAddress, 4);//当方式为跳向指定帧时这个地址是指向时间轴上的一个时间点(头0开始)
                    byte[] staytextContentAddress = intToByteArray(tempTextAddress, 4);
                    byte[] stayclockOrTem = new byte[3];

                    setInbyteArray(2, staypicAddress, timeAxisStay);
                    setInbyteArray(6, attrAddress, timeAxisStay);
                    setInbyteArray(9, staytextContentAddress, timeAxisStay);
                    setInbyteArray(13, stayclockOrTem, timeAxisStay);
                    mTimeAxisList.add(timeAxisStay);
                }
                mFrameCount += stayFrame;
            }
        }

    }

    public int getVerticalAddressIndicator() {
        return mVerticalAddressIndicator;
    }

    public int getTextFrameIndicator() {
        return mTextFrameIndicator;
    }

    public int getFrameCount() {
        return mFrameCount;
    }
}
