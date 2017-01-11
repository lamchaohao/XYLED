package cn.com.hotled.xyled.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lam on 2017/1/6
 */

public class MultiCompressAlgorithm {
    
    private List<Integer> mScreenHeightArr;
    private List<Integer> programLengthList;
    List<Byte> mColPixelList ;

    public MultiCompressAlgorithm(List<Integer> programLengthList, List<Integer> screenHeights) {
        this.programLengthList = programLengthList;
        this.mScreenHeightArr = screenHeights;
        mColPixelList = new ArrayList<>();
    }

    private byte[] colByteCount;//每一列字节数
    
    public List<Byte> compress(byte[] content,int textWidth){
        List<Byte> compressContentList = new ArrayList<>();
        colByteCount = new byte[textWidth];//每一列有多少个字节，需要记录下来，写入时间轴
        int index=0;
        for (int i = 0; i < programLengthList.size(); i++){

            int lastCompressStyle = 0;
            for (int x = 0; x < programLengthList.get(i); x++) {
                byte lastPoint= 0;//列压缩
                int sameCount = 1;
                mColPixelList.clear();//每列开始前清除之前的
                for (int y = 0; y < mScreenHeightArr.get(i); y++) {
                    if (y==0){
                        lastPoint=content[index];//防止跳入到content[index]!=lastPoint中，因为背景颜色设定可能不是黑色
                    } else if (y==mScreenHeightArr.get(i)-1){
                        if (content[index]==lastPoint){
                            sameCount++;
                            lastCompressStyle = startCompress(lastCompressStyle, lastPoint, sameCount);
                            sameCount=1;
                        }else{
                            //如果最后一个与前一个不同，那么则开始压缩之前的，则最后一个没有加入进去，故而漏了以一个点
                            lastCompressStyle = startCompress(lastCompressStyle, lastPoint, sameCount);
                            sameCount=1;
                            //所以在最后一个点前面的压缩完后，还要压缩最后一个点
                            lastPoint=content[index];
                            lastCompressStyle = startCompress(lastCompressStyle, lastPoint, sameCount);
                        }


                    } else if (content[index]==lastPoint){
                        if (y!=0)
                            sameCount++;
                    } else if (content[index]!=lastPoint){
                        lastCompressStyle = startCompress(lastCompressStyle, lastPoint, sameCount);
                        sameCount=1;
                    }
                    lastPoint=content[index];
                    index++;
                }
                colByteCount[x]=(byte) mColPixelList.size();//每一列的有多少个字节
                for (int m = 0; m < mColPixelList.size(); m++) {
                    compressContentList.add(mColPixelList.get(m));
                }
            }
        }


        return compressContentList;
    }

    private int startCompress(int lastCompressStyle, byte lastPoint, int sameCount) {
        //数到不同的点，进行计算压缩
        //分情况进行压缩
        switch (sameCount) {
            case 0:
                break;
            case 1:
                mColPixelList.add(lastPoint);
                break;
            case 2:
                    byte twoSame = lastPoint;
                    twoSame+=64;//使用高二位压缩,01xxxxxx==64
                    mColPixelList.add(twoSame);
                    lastCompressStyle=64;
                break;
            case 3:
                    byte threeSame = lastPoint;
                    threeSame+=128;//使用高二位压缩,10xxxxxx==128
                    mColPixelList.add(threeSame);
                    lastCompressStyle=128;
                break;
            case 4:
                byte fourSame = lastPoint;
                fourSame+=192;//使用高二位压缩,11xxxxxx==192
                mColPixelList.add(fourSame);
                lastCompressStyle=192;
                break;
            case 5:
                mColPixelList.add(lastPoint);//先加一颗，剩下四颗进行压缩
                byte fiveSame = lastPoint;
                fiveSame+=192;//使用00xxxxxx+11xxxxxx压缩,11xxxxxx==192
                mColPixelList.add(fiveSame);
                lastCompressStyle=192;
                break;
            case 6:
                byte sixFirstPart = lastPoint;
                sixFirstPart+=64;//先压缩两颗，剩下四颗进行压缩
                mColPixelList.add(sixFirstPart);
                byte sixSecondPart = lastPoint;
                sixSecondPart+=192;//使用01xxxxxx+11xxxxxx压缩,11xxxxxx==192
                mColPixelList.add(sixSecondPart);
                lastCompressStyle=192;
                break;
            case 7:
                byte sevenFirstPart = lastPoint;
                sevenFirstPart+=128;//先压缩三颗，剩下四颗进行压缩
                mColPixelList.add(sevenFirstPart);
                byte sevenSecondPart = lastPoint;
                sevenSecondPart+=192;//使用10xxxxxx+11xxxxxx压缩,11xxxxxx==192
                mColPixelList.add(sevenSecondPart);
                lastCompressStyle=192;
                break;
            case 8 :
                byte eightFirstPart = lastPoint;
                eightFirstPart+=192;//先压缩四颗，剩下四颗进行压缩
                mColPixelList.add(eightFirstPart);
                byte eightSecondPart = lastPoint;
                eightSecondPart+=192;//使用11xxxxxx+11xxxxxx压缩,11xxxxxx==192
                mColPixelList.add(eightSecondPart);
                lastCompressStyle=192;
                break;
            default:
                if (sameCount>=9&&sameCount<=263) {
                    byte nineMoreFirstPart = lastPoint;
                    nineMoreFirstPart+=64;
                    mColPixelList.add(nineMoreFirstPart);
                    mColPixelList.add(nineMoreFirstPart);
                    //连续两次都为01xxxxxx，后一个byte代表后面还有多少个相同的颜色
                    byte nineMoreSecondPart = (byte) (sameCount-4-4);
                    mColPixelList.add(nineMoreSecondPart);
                }else if (sameCount>=264) {
                    byte moreMoreFirstPart = lastPoint;
                    moreMoreFirstPart+=128;
                    mColPixelList.add(moreMoreFirstPart);
                    mColPixelList.add(moreMoreFirstPart);
                    //连续两次都为01xxxxxx，后一个byte代表后面还有多少个相同的颜色
                    byte moreMoreSecondPart = (byte) (sameCount-6-258);
                    mColPixelList.add(moreMoreSecondPart);
                }
                lastCompressStyle=0;
                break;
        }
        return lastCompressStyle;
    }

    public byte[] getColByteCount() {
        return colByteCount;
    }
}
