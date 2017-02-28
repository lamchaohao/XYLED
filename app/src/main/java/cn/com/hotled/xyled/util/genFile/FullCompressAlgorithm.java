package cn.com.hotled.xyled.util.genFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lam on 2016/12/10.
 */

public class FullCompressAlgorithm {


    private List<Byte> mCompressContentList;

    public List<Byte> compress(byte[] content){
        mCompressContentList = new ArrayList<>();
        int lastCompressStyle = 0;
        byte lastPoint = -1;
        int sameCount = 1;
        for (int index = 0; index < content.length; index++) {
            if (index==0){
                lastPoint = content[index];
            }else if (content[index] == lastPoint) {
                sameCount++;
                if (index==content.length-1){
                    startCompress(lastCompressStyle, lastPoint, sameCount);
                    sameCount = 1;
                }
            } else {
                lastCompressStyle = startCompress(lastCompressStyle, lastPoint, sameCount);
                sameCount = 1;
                if (index==content.length-1){
                    startCompress(lastCompressStyle, lastPoint, sameCount);
                }
            }

            lastPoint = content[index];
        }
        return mCompressContentList;
    }

    private int startCompress(int lastCompressStyle, byte lastPoint, int sameCount) {
        //数到不同的点，进行计算压缩
        //分情况进行压缩
        switch (sameCount) {
            case 0:
                break;
            case 1:
                mCompressContentList.add(lastPoint);
                break;
            case 2:
                    byte twoSame = lastPoint;
                    twoSame+=64;//使用高二位压缩,01xxxxxx==64
                mCompressContentList.add(twoSame);
                    lastCompressStyle=64;
                break;
            case 3:
                    byte threeSame = lastPoint;
                    threeSame+=128;//使用高二位压缩,10xxxxxx==128
                mCompressContentList.add(threeSame);
                    lastCompressStyle=128;
                break;
            case 4:
                byte fourSame = lastPoint;
                fourSame+=192;//使用高二位压缩,11xxxxxx==192
                mCompressContentList.add(fourSame);
                lastCompressStyle=192;
                break;
            case 5:
                mCompressContentList.add(lastPoint);//先加一颗，剩下四颗进行压缩
                byte fiveSame = lastPoint;
                fiveSame+=192;//使用00xxxxxx+11xxxxxx压缩,11xxxxxx==192
                mCompressContentList.add(fiveSame);
                lastCompressStyle=192;
                break;
            case 6:
                byte sixFirstPart = lastPoint;
                sixFirstPart+=64;//先压缩两颗，剩下四颗进行压缩
                mCompressContentList.add(sixFirstPart);
                byte sixSecondPart = lastPoint;
                sixSecondPart+=192;//使用01xxxxxx+11xxxxxx压缩,11xxxxxx==192
                mCompressContentList.add(sixSecondPart);
                lastCompressStyle=192;
                break;
            case 7:
                byte sevenFirstPart = lastPoint;
                sevenFirstPart+=128;//先压缩三颗，剩下四颗进行压缩
                mCompressContentList.add(sevenFirstPart);
                byte sevenSecondPart = lastPoint;
                sevenSecondPart+=192;//使用10xxxxxx+11xxxxxx压缩,11xxxxxx==192
                mCompressContentList.add(sevenSecondPart);
                lastCompressStyle=192;
                break;
            case 8 :
                byte eightFirstPart = lastPoint;
                eightFirstPart+=192;//先压缩四颗，剩下四颗进行压缩
                mCompressContentList.add(eightFirstPart);
                byte eightSecondPart = lastPoint;
                eightSecondPart+=192;//使用11xxxxxx+11xxxxxx压缩,11xxxxxx==192
                mCompressContentList.add(eightSecondPart);
                lastCompressStyle=192;
                break;
            default:
                if (sameCount>=9&&sameCount<=263) {
                    byte nineMoreFirstPart = lastPoint;
                    nineMoreFirstPart+=64;
                    mCompressContentList.add(nineMoreFirstPart);
                    mCompressContentList.add(nineMoreFirstPart);
                    //连续两次都为01xxxxxx，后一个byte代表后面还有多少个相同的颜色
                    byte nineMoreSecondPart = (byte) (sameCount-4-4);
                    mCompressContentList.add(nineMoreSecondPart);
                }else if (sameCount>=264) {
                    byte moreMoreFirstPart = lastPoint;
                    moreMoreFirstPart+=128;
                    mCompressContentList.add(moreMoreFirstPart);
                    mCompressContentList.add(moreMoreFirstPart);
                    //连续两次都为01xxxxxx，后一个byte代表后面还有多少个相同的颜色
                    byte moreMoreSecondPart = (byte) (sameCount-6-258);
                    mCompressContentList.add(moreMoreSecondPart);
                }
                lastCompressStyle=0;
                break;
        }
        return lastCompressStyle;
    }

}
