package cn.com.hotled.xyled.util.genFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lam on 2016/12/10.
 */

public class CompressAlgorithm {
    private byte[] colByteCount;//每一列字节数
    
    public List<Byte> compress(byte[] content,int textWidth,int mScreenHeight){
        List<Byte> compressContentList=new ArrayList<>();
        colByteCount = new byte[textWidth];//每一列有多少个字节，需要记录下来，写入时间轴
        List<Byte> colPixelList = new ArrayList<>();
        int lastCompressStyle = 0;
        for (int x = 0,index=0; x < textWidth; x++) {
            byte lastPoint= 0;//列压缩
            int sameCount = 1;
            colPixelList.clear();//每列开始前清除之前的
            for (int y = 0; y < mScreenHeight; y++) {
              if (y==0){
                  lastPoint=content[index];//防止跳入到content[index]!=lastPoint中，因为背景颜色设定可能不是黑色
              } else if (y==mScreenHeight-1){
                    if (content[index]==lastPoint){
                        sameCount++;
                        lastCompressStyle = startCompress(content, colPixelList, lastCompressStyle, x, index, lastPoint, sameCount, y);
                        sameCount=1;
                    }else{
                        //如果最后一个与前一个不同，那么则开始压缩之前的，则最后一个没有加入进去，故而漏了以一个点
                        lastCompressStyle = startCompress(content, colPixelList, lastCompressStyle, x, index, lastPoint, sameCount, y);
                        sameCount=1;
                        //所以在最后一个点前面的压缩完后，还要压缩最后一个点
                        lastPoint=content[index];
                        lastCompressStyle = startCompress(content, colPixelList, lastCompressStyle, x, index, lastPoint, sameCount, y);
                    }


              } else if (content[index]==lastPoint){
                    if (y!=0)
                        sameCount++;
              } else if (content[index]!=lastPoint){
                    lastCompressStyle = startCompress(content, colPixelList, lastCompressStyle, x, index, lastPoint, sameCount, y);
                    sameCount=1;
              }
                lastPoint=content[index];
                index++;
            }
            colByteCount[x]=(byte) colPixelList.size();//每一列的有多少个字节
            for (int i = 0; i < colPixelList.size(); i++) {
                compressContentList.add(colPixelList.get(i));
            }
        }

        return compressContentList;
    }

    private int startCompress(byte[] content, List<Byte> colPixelList, int lastCompressStyle, int x, int index, byte lastPoint, int sameCount, int y) {
        //数到不同的点，进行计算压缩
        //分情况进行压缩
        switch (sameCount) {
            case 0:
                break;
            case 1:
                colPixelList.add(lastPoint);
                break;
            case 2:
                    byte twoSame = lastPoint;
                    twoSame+=64;//使用高二位压缩,01xxxxxx==64
                    colPixelList.add(twoSame);
                    lastCompressStyle=64;
                break;
            case 3:
                    byte threeSame = lastPoint;
                    threeSame+=128;//使用高二位压缩,10xxxxxx==128
                    colPixelList.add(threeSame);
                    lastCompressStyle=128;
                break;
            case 4:
                byte fourSame = lastPoint;
                fourSame+=192;//使用高二位压缩,11xxxxxx==192
                colPixelList.add(fourSame);
                lastCompressStyle=192;
                break;
            case 5:
                colPixelList.add(lastPoint);//先加一颗，剩下四颗进行压缩
                byte fiveSame = lastPoint;
                fiveSame+=192;//使用00xxxxxx+11xxxxxx压缩,11xxxxxx==192
                colPixelList.add(fiveSame);
                lastCompressStyle=192;
                break;
            case 6:
                byte sixFirstPart = lastPoint;
                sixFirstPart+=64;//先压缩两颗，剩下四颗进行压缩
                colPixelList.add(sixFirstPart);
                byte sixSecondPart = lastPoint;
                sixSecondPart+=192;//使用01xxxxxx+11xxxxxx压缩,11xxxxxx==192
                colPixelList.add(sixSecondPart);
                lastCompressStyle=192;
                break;
            case 7:
                byte sevenFirstPart = lastPoint;
                sevenFirstPart+=128;//先压缩三颗，剩下四颗进行压缩
                colPixelList.add(sevenFirstPart);
                byte sevenSecondPart = lastPoint;
                sevenSecondPart+=192;//使用10xxxxxx+11xxxxxx压缩,11xxxxxx==192
                colPixelList.add(sevenSecondPart);
                lastCompressStyle=192;
                break;
            case 8 :
                byte eightFirstPart = lastPoint;
                eightFirstPart+=192;//先压缩四颗，剩下四颗进行压缩
                colPixelList.add(eightFirstPart);
                byte eightSecondPart = lastPoint;
                eightSecondPart+=192;//使用11xxxxxx+11xxxxxx压缩,11xxxxxx==192
                colPixelList.add(eightSecondPart);
                lastCompressStyle=192;
                break;
            default:
                if (sameCount>=9&&sameCount<=263) {
                    byte nineMoreFirstPart = lastPoint;
                    nineMoreFirstPart+=64;
                    colPixelList.add(nineMoreFirstPart);
                    colPixelList.add(nineMoreFirstPart);
                    //连续两次都为01xxxxxx，后一个byte代表后面还有多少个相同的颜色
                    byte nineMoreSecondPart = (byte) (sameCount-4-4);
                    colPixelList.add(nineMoreSecondPart);
                }else if (sameCount>=264) {
                    byte moreMoreFirstPart = lastPoint;
                    moreMoreFirstPart+=128;
                    colPixelList.add(moreMoreFirstPart);
                    colPixelList.add(moreMoreFirstPart);
                    //连续两次都为01xxxxxx，后一个byte代表后面还有多少个相同的颜色
                    byte moreMoreSecondPart = (byte) (sameCount-6-258);
                    colPixelList.add(moreMoreSecondPart);
                }
                lastCompressStyle=0;
                break;
        }
        return lastCompressStyle;
    }

    /**
     * 返回经过压缩后的每一列占据多少个字节
     * @return
     */
    public byte[] getColByteCount() {
        return colByteCount;
    }
}
