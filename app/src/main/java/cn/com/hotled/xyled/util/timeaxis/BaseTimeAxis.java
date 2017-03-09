package cn.com.hotled.xyled.util.timeaxis;

import java.util.List;
import java.util.Map;

import cn.com.hotled.xyled.bean.Program;

/**
 * Created by Lam on 2017/3/6.
 */

public abstract class BaseTimeAxis {
    public int mVerticalAddressIndicator;//垂直方向所需
    public List<List<Integer>> mVerticalTextFrameCountList;//记录每个上下移节目的每一帧有多少个字节的list的List
    public List<byte[]> mHorizontalTextByteList;//垂直方向所需
    public Program mProgram;
    public Map<Integer, Integer> mFlowMap;
    public List<byte[]> mFlowByteList;
    public List<byte[]> mTimeAxisList;
    public int mColbyteCountIndicator; //水平方向上移动
    public List<byte[]> mColByteCountList;//水平方向上移动
    public int mTextFrameIndicator;
    public int mFrameCount;
    public byte[] attrAddress;
    public BaseTimeAxis setProgram(Program program) {
        mProgram = program;
        return this;
    }

    public BaseTimeAxis setFlowMap(Map<Integer, Integer> flowMap) {
        mFlowMap = flowMap;
        return this;
    }

    public BaseTimeAxis setFlowByteList(List<byte[]> flowByteList) {
        mFlowByteList = flowByteList;
        return this;
    }

    public BaseTimeAxis setTimeAxisList(List<byte[]> timeAxisList) {
        mTimeAxisList = timeAxisList;
        return this;
    }

    public BaseTimeAxis setFrameCount(int frameCount) {
        mFrameCount = frameCount;
        return this;
    }

    public BaseTimeAxis setTextFrameIndicator(int textFrameIndicator) {
        mTextFrameIndicator = textFrameIndicator;
        return this;
    }

    public BaseTimeAxis setAttrAddress(byte[] attrAddress) {
        this.attrAddress = attrAddress;
        return this;
    }

    public BaseTimeAxis setVerticalAddressIndicator(int verticalAddressIndicator) {
        mVerticalAddressIndicator = verticalAddressIndicator;
        return this;
    }

    public BaseTimeAxis setVerticalTextFrameCountList(List<List<Integer>> verticalTextFrameCountList) {
        mVerticalTextFrameCountList = verticalTextFrameCountList;
        return this;
    }

    public BaseTimeAxis setHorizontalTextByteList(List<byte[]> horizontalTextByteList) {
        mHorizontalTextByteList = horizontalTextByteList;
        return this;
    }

    public BaseTimeAxis setColbyteCountIndicator(int colbyteCountIndicator) {
        mColbyteCountIndicator = colbyteCountIndicator;
        return this;
    }

    public BaseTimeAxis setColByteCountList(List<byte[]> colByteCountList) {
        mColByteCountList = colByteCountList;
        return this;
    }

    public abstract void setTimeAxis(int textContentAddressInt, int frameOfThisProgram, int verticalOrHorizontalIndex, int flowBoundslength, int screenHeightOrWidth);


    public abstract int getAddressIndicator();

    public abstract int getTextFrameIndicator();

    public abstract int getFrameCount();


}
