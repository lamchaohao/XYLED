package cn.com.hotled.xyled.util.timeaxis;

import java.util.List;
import java.util.Map;

import cn.com.hotled.xyled.bean.Program;

import static cn.com.hotled.xyled.util.genFile.ByteUtil.intToByteArray;
import static cn.com.hotled.xyled.util.genFile.ByteUtil.setInbyteArray;

/**
 * Created by Lam on 2017/3/4.
 */

public class StaticShow {
    private Program mProgram;
    private Map<Integer, Integer> mFlowMap;
    private List<byte[]> mFlowByteList;
    private List<byte[]> mTimeAxisList;
    private int mColbyteCountIndicator;
    private List<byte[]> mColByteCountList;
    private int mTextFrameIndicator;
    private int mFrameCount;
    private byte[] attrAddress;

    public StaticShow(Program program, Map<Integer, Integer> flowMap, List<byte[]> flowByteList, List<byte[]> timeAxisList, int colbyteCountIndicator, List<byte[]> colByteCountList, int textFrameIndicator, int frameCount, byte[] attrAddress) {

        mProgram = program;
        mFlowMap = flowMap;
        mFlowByteList = flowByteList;
        mTimeAxisList = timeAxisList;
        mColbyteCountIndicator = colbyteCountIndicator;
        mColByteCountList = colByteCountList;
        mTextFrameIndicator = textFrameIndicator;
        mFrameCount = frameCount;
        this.attrAddress = attrAddress;
    }

    public void setTimeAxis(int textContentAddressInt, int frameOfThisProgram, int horizontalIndex, int flowBoundslength,int screenWidth){
        if (horizontalIndex==0) {
            mColbyteCountIndicator = 0;
        }else if (horizontalIndex>0){
            mColbyteCountIndicator += mColByteCountList.get(horizontalIndex-1)[mColByteCountList.get(horizontalIndex-1).length-1];
        }
        int tempTextAddress=textContentAddressInt+mColbyteCountIndicator;
        byte[] thisProgramColByte = mColByteCountList.get(horizontalIndex);
        for (int i = 0; i < thisProgramColByte.length - 1; i++) {
            mColbyteCountIndicator +=thisProgramColByte[i];//不在循环里相加，要把自身节目所占空间累加到mColbyteCountIndicator中，以防后续节目地址不对
        }
        //文字属性地址
        float frameTime = mProgram.getFrameTime();
        for (int i = 0; i < frameOfThisProgram; i++) {
            byte[] timeAxis = new byte[16];
            timeAxis[0] = (byte) frameTime;
            timeAxis[1] = 0;

            //流水边地址
            int tempFlowadd = 0;
            if (mFlowMap != null) {
                Integer flowIndex = mFlowMap.get(mTextFrameIndicator);
                for (int j = 0; flowIndex != null && j < flowIndex; j++) {
                    tempFlowadd += mFlowByteList.get(j).length;
                }
            }
            int tempPicAddress = textContentAddressInt - flowBoundslength + tempFlowadd;
            byte[] picAddress = intToByteArray(tempPicAddress, 4);//当方式为跳向指定帧时这个地址是指向时间轴上的一个时间点(头0开始)
            byte[] textContentAddress = intToByteArray(tempTextAddress, 4);
            byte[] clockOrTem = new byte[3];

            setInbyteArray(2, picAddress, timeAxis);
            setInbyteArray(6, attrAddress, timeAxis);
            setInbyteArray(9, textContentAddress, timeAxis);
            setInbyteArray(13, clockOrTem, timeAxis);

            mTimeAxisList.add(timeAxis);
            mTextFrameIndicator++;
        }
    }

    public int getColbyteCountIndicator() {
        return mColbyteCountIndicator;
    }

    public int getTextFrameIndicator() {
        return mTextFrameIndicator;
    }

    public int getFrameCount() {
        return mFrameCount;
    }
}
