package cn.com.hotled.xyled.util.timeaxis;

import java.util.List;
import java.util.Map;

import cn.com.hotled.xyled.bean.Program;

import static cn.com.hotled.xyled.util.genFile.ByteUtil.intToByteArray;
import static cn.com.hotled.xyled.util.genFile.ByteUtil.setInbyteArray;

/**
 * Created by Lam on 2017/3/4.
 */

public class MoveRight {
    private Program mProgram;
    private Map<Integer, Integer> mFlowMap;
    private List<byte[]> mFlowByteList;
    private List<byte[]> mTimeAxisList;
    private int mColbyteCountIndicator;
    private List<byte[]> mColByteCountList;
    private int mTextFrameIndicator;
    private int mFrameCount;
    private byte[] attrAddress;

    public MoveRight(Program program, Map<Integer, Integer> flowMap, List<byte[]> flowByteList, List<byte[]> timeAxisList, int colbyteCountIndicator, List<byte[]> colByteCountList, int textFrameIndicator, int frameCount, byte[] attrAddress) {

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
        int oldIndicator = mColbyteCountIndicator;
        for (int i = 0; i < frameOfThisProgram; i++) {
            if (i == 0 && horizontalIndex==0) {
                mColbyteCountIndicator = 0;
            }else if (i == 0 && horizontalIndex>0){
                mColbyteCountIndicator += mColByteCountList.get(horizontalIndex-1)[mColByteCountList.get(horizontalIndex-1).length-1];
            }else {
                mColbyteCountIndicator += mColByteCountList.get(horizontalIndex)[i-1];
            }
        }

        int rightIndicator = mColbyteCountIndicator;
        float frameTime = mProgram.getFrameTime();
        //流水边速度
        int flowSpeed = mProgram.getFlowSpeed();
        //先获取所有的流水边地址
        int[] flowAddress= new int[frameOfThisProgram];
        //也获取所有的文字地址
        int[] textAddress = new int[frameOfThisProgram];

        for (int i = frameOfThisProgram-1; i >= 0; i--){
            if (i == 0) {
                rightIndicator = oldIndicator;
            }else if (i==frameOfThisProgram){
                rightIndicator = mColbyteCountIndicator;
            }else {
                rightIndicator -= mColByteCountList.get(horizontalIndex)[i-1];
            }//文字地址
            int tempTextAddress=textContentAddressInt+rightIndicator;
            textAddress[i] = tempTextAddress;
            //流水边地址
            int tempFlowadd=0;
            if (mFlowMap!=null) {
                Integer flowIndex = mFlowMap.get(mTextFrameIndicator);
                for (int j = 0; flowIndex!=null&&j < flowIndex; j++) {
                    tempFlowadd+=mFlowByteList.get(j).length;
                }
                flowAddress[i]=tempFlowadd;
            }

            mTextFrameIndicator++;
            //这一个非常重要！是防止出现字幕乱跳的必须
            if (i==frameOfThisProgram-1){
                //如果是节目的最后一帧，则压缩的mTempColbyteCount应该加上还没有的录入的
                byte[] bytes = mColByteCountList.get(horizontalIndex);
                for (int k = i+1; k < bytes.length; k++) {
                    mColbyteCountIndicator+=bytes[k];
                }
            }
        }
        int flowIndicator = 0;
        float stayTime = mProgram.getStayTime();
        //在此加入时间轴list
        for (int i = frameOfThisProgram-1; i >= 0; i--) {
            byte[] timeAxis = new byte[16];
            timeAxis[0] = (byte) frameTime;
            timeAxis[1] = 0;
            //文字地址
            int tempTextAddress = textAddress[i];
            //流水边地址
            int tempPicAddress = 0;

            if (flowSpeed == 0) {
                tempPicAddress = textContentAddressInt - flowBoundslength + flowAddress[0]; //静止
            } else {
                if (i == 0) {
                    flowIndicator = 0;
                } else if (i % flowSpeed == 0) {
                    flowIndicator++;
                }
                if (flowIndicator >= flowAddress.length) {
                    flowIndicator -= flowAddress.length;
                }
                tempPicAddress = textContentAddressInt - flowBoundslength + flowAddress[flowIndicator];//流水边地址
            }

            byte[] picAddress = intToByteArray(tempPicAddress, 4);//当方式为跳向指定帧时这个地址是指向时间轴上的一个时间点(头0开始)
            byte[] textContentAddress = intToByteArray(tempTextAddress, 4);
            byte[] clockOrTem = new byte[3];

            setInbyteArray(2, picAddress, timeAxis);
            setInbyteArray(6, attrAddress, timeAxis);
            setInbyteArray(9, textContentAddress, timeAxis);
            setInbyteArray(13, clockOrTem, timeAxis);
            mTimeAxisList.add(timeAxis);
            if (i % screenWidth == 0) {
                int stayFrame = (int) (stayTime * screenWidth / 4);
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
                    int stayPicAddress = textContentAddressInt - flowBoundslength + flowAddress[flowIndicator];
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
