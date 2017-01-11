package cn.com.hotled.xyled.flowbound;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.com.hotled.xyled.util.FullCompressAlgorithm;

/**
 * Created by Lam on 2017/1/3.
 */

public class FromFile extends BaseFlowBound {
    File[] flowFile;
    List<Integer> programLengths;
    boolean[] useFlows;
    private List<byte[]> mBitmapPixelList;
    private List<Integer> mBitmapHeightList;
    private List<byte[]> mFlowBoundList;

    public FromFile(int screenWidth, int screenHeight, byte[] color, int colorWidth, int frameCount) {
        super(screenWidth, screenHeight, color, colorWidth, frameCount);
    }

    public void setFlowFile(File[] file) {
        flowFile = file;
    }

    public void setUseFlows(boolean[] useFlow){
        useFlows=useFlow;
    }

    public void setProgramLength(List<Integer> lengths){
        programLengths=lengths;
    }

    @Override
    public List<byte[]> genFlowBound() {
        mBitmapPixelList = new ArrayList<>();
        mBitmapHeightList = new ArrayList<>();
        //1. 每个流水边框转换
        for (File file : flowFile) {
            convertFileToPixels(file);
        }
        mFlowBoundList = new ArrayList<>();
        //2.赋值
        setToBackground();


        return mFlowBoundList;
    }

    private void setToBackground() {
        int flowFileIndex=0;
        for (int i = 0; i < useFlows.length; i++) {
            if (useFlows[i]) {
                //如果有使用流水边框，则使用
                byte[] flow = mBitmapPixelList.get(flowFileIndex);
                //传过来的节目长度参数已经有包含了过渡帧数
                setFlowToBack(flow,mBitmapHeightList.get(flowFileIndex),programLengths.get(i));
                //每次执行完之后，flowFileIndex要加1
                flowFileIndex++;
            }else {
                //如果没有使用流水边框，则使用黑色背景
                byte[] blackBG = getBlackBG();
                for (int j = 0; j < programLengths.get(i); j++) {
                    mFlowBoundList.add(blackBG);
                }
            }

        }
    }

    private byte[] getBlackBG(){
        byte[] blackBG=new byte[screenHeight*screenWidth];
        FullCompressAlgorithm fullCompress=new FullCompressAlgorithm();
        List<Byte> compress = fullCompress.compress(blackBG);
        byte[] compressedBlack=new byte[compress.size()];
        for (int m = 0; m < compress.size(); m++) {
            compressedBlack[m]=compress.get(m);
        }

        return  compressedBlack;
    }

    /**
     *
     * @param flow 流水边框的数组形式
     * @param height 流水边框的高度
     * @param length 流水边框要走动多少帧
     */
    private void setFlowToBack( byte[] flow,int height,int length) {

        for (int i = 0,index =0; i < length; i++,index++) {
            byte[] tempFlow = new byte[screenHeight * screenWidth];
            while (index >= screenWidth) {
                index -= screenWidth;
            }

            //up part1
            for (int k = 0; k < screenWidth - index; k++) {
                int colorIndex = (k + index)*height;//this is right
                while (colorIndex >= flow.length) {
                    colorIndex -= flow.length;
                }
                for (int q=0;q<height;q++){
                    tempFlow[(screenWidth - k - 1) * screenHeight+q] = flow[colorIndex+q];
                }
            }

            //up part2
            for (int k = 0; k < index; k++) {
                int colorIndex = (index - k)*height;
                while (colorIndex >= flow.length) {
                    colorIndex -= flow.length;
                }
                for (int q=0;q<height;q++){
                    tempFlow[((k* screenHeight)+ q)]= flow[colorIndex+q];
                }
            }

            //right part1
            int colIndex=index;
            while (colIndex>=screenHeight){
                colIndex-=screenHeight;
            }
            for (int j = 0; j < screenHeight-colIndex; j++) {
                int colorIndex = j * height;
                while (colorIndex >= flow.length) {
                    colorIndex -= flow.length;
                }
                for (int q = 0; q < height; q++) {
                    tempFlow[tempFlow.length - screenHeight * (q + 1) + j + colIndex] = flow[colorIndex + q];
                }
            }

            //right part2
            for (int j = 0; j <colIndex; j++) {
                int colorIndex= (screenHeight-colIndex+j)*height;
                while (colorIndex>=flow.length){
                    colorIndex-=flow.length;
                }
                for (int q = 0; q < height; q++) {
                    tempFlow[tempFlow.length-screenHeight* (q + 1)+j]=flow[colorIndex+q];
                }
            }

            //down part1
            for (int k=screenWidth;k>=2;k--){
                int colorIndex=k*height;
                while (colorIndex>=flow.length){
                    colorIndex-=flow.length;
                }
                if (k>index){
                    for (int q=height;q>0;q--)
                        tempFlow[(k-index)*screenHeight-q]=flow[colorIndex+q-1];
                }else if (k==index){
                    for (int q=height;q>0;q--)
                        tempFlow[screenHeight-q]=flow[colorIndex+q-1];
                }

            }

            //down part2
            for (int k=index;k>=2;k--){
                int colorIndex=(index-k)*height;
                while (colorIndex>=flow.length){
                    colorIndex-=flow.length;
                }
                if (screenWidth-k!=0){
                    for (int q=height;q>0;q--)
                        tempFlow[(screenWidth-k)*screenHeight-q]=flow[colorIndex+q-1];
                }else if (screenWidth==index){
                    for (int q=height;q>0;q--)
                        tempFlow[screenHeight-q]=flow[colorIndex+q-1];
                }

            }

            //left part1
            for (int j = 0; j < screenHeight-colIndex; j++) {
                int colorIndex=(colIndex+j)*height;
                while (colorIndex>=flow.length){
                    colorIndex-=flow.length;
                }
                for (int q = 0; q < height; q++) {
                    tempFlow[screenHeight*q+j] = flow[colorIndex + q];
                }
            }

            //left part2
            for (int j = 0; j <colIndex; j++) {
                int colorIndex= (colIndex-j)*height;
                while (colorIndex>=flow.length){
                    colorIndex-=flow.length;
                }
                for (int q = 1; q <= height; q++) {
                    tempFlow[screenHeight*q-1-j] = flow[colorIndex + q-1];
                }

            }

            FullCompressAlgorithm ca=new FullCompressAlgorithm();
            List<Byte> compress = ca.compress(tempFlow);
            byte[] compressFlow = new byte[compress.size()];

            for (int k = 0; k < compress.size(); k++) {
                compressFlow[k]=compress.get(k);
            }
            mFlowBoundList.add(compressFlow);
        }
    }

    private void convertFileToPixels(File file) {
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        byte[] bitmapPixels = new byte[bitmap.getWidth() * bitmap.getHeight()];
        for (int x1 = 0, i = 0; x1 < bitmap.getWidth(); x1++) {
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
                    red = Integer.parseInt(hexStr.substring(0, 2), 16);
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
        Log.i("fromfile","bitmapPixels.length:"+bitmapPixels.length);
        mBitmapPixelList.add(bitmapPixels);
        mBitmapHeightList.add(bitmap.getHeight());//保存好每个流水边框的高度
    }


}
