package cn.com.hotled.xyled.flowbound;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.hotled.xyled.global.Global;
import cn.com.hotled.xyled.util.genFile.BitmapToPixel;
import cn.com.hotled.xyled.util.genFile.FullCompressAlgorithm;

/**
 * Created by Lam on 2017/1/3.
 */

public class ClockwiseFlow extends BaseFlowBound{
    File[] flowFile;
    List<Integer> programLengths;
    boolean[] useFlows;
    private List<byte[]> mBitmapPixelList;
    private List<Integer> mBitmapHeightList;
    private List<byte[]> mFlowBoundList;
    private Map<Integer,Integer> mFlowMap;
    private int mIndex;
    private int[] flowEffect;

    public ClockwiseFlow(Context context, int screenWidth, int screenHeight) {
        super(context, screenWidth, screenHeight);
        mFlowMap=new HashMap<>();
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

    public void setFlowStyle(int[] flowStyle){
        this.flowEffect=flowStyle;
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
                setFlowToBack(flow,mBitmapHeightList.get(flowFileIndex),programLengths.get(i),flowEffect[flowFileIndex]);
                //每次执行完之后，flowFileIndex要加1
                flowFileIndex++;
            }else {
                //如果没有使用流水边框，则使用黑色背景
                List<byte[]> tempBlack=new ArrayList<>();
                byte[] blackBG = getBlackBG();
                for (int j = 0; j < programLengths.get(i); j++) {
                    tempBlack.add(blackBG);
                }
                compareAndCompress(tempBlack);
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
    private void setFlowToBack( byte[] flow,int height,int length,int flowEffect) {

        switch (flowEffect){
            case Global.FLOW_EFFECT_CLOCKWISE:
                clockWise(flow,height,length);
                break;
            case Global.FLOW_EFFECT_ANTICLOCKWISE:
                antiClockWise(flow,height,length);
                break;
        }

    }


    private void antiClockWise(byte[] flow,int height,int length){
        List<byte[]> tempFlowList=new ArrayList<>();

        for (int i = 0,index =0; i < length; i++,index++) {
            byte[] tempFlow = new byte[screenHeight * screenWidth];
            while (index >= screenWidth) {
                index -= screenWidth;
            }

            //right part1
            int colIndex=index;
            while (colIndex>=screenHeight){
                colIndex-=screenHeight;
            }
            for (int j = 0; j < screenHeight-colIndex; j++) {
                int colorIndex = (colIndex+j)*height;
                while (colorIndex >= flow.length) {
                    colorIndex -= flow.length;
                }
                for (int q = 1; q <= height; q++) {
                    tempFlow[tempFlow.length-screenHeight* (q)+j]=flow[colorIndex+q-1];
                }
            }

            //right part2
            for (int j = 0; j <colIndex; j++) {
                int colorIndex= (j) * height;
                while (colorIndex>=flow.length){
                    colorIndex-=flow.length;
                }
                for (int q = 0; q < height; q++) {
                    tempFlow[tempFlow.length - screenHeight * q + j-colIndex] = flow[colorIndex + q];
                }
            }

            //left part1
            for (int j = 0; j < screenHeight-colIndex; j++) {
                int colorIndex=(screenHeight-j)*height;
                while (colorIndex>=flow.length){
                    colorIndex-=flow.length;
                }
                for (int q = 1; q <=height ; q++) {
                    tempFlow[screenHeight*(q-1)+j+ colIndex] = flow[colorIndex + q-1];
                }
            }

            //left part2
            for (int j = 0; j <colIndex; j++) {
                int colorIndex= (colIndex-j)*height;
                while (colorIndex>=flow.length){
                    colorIndex-=flow.length;
                }
                for (int q = 1; q <= height; q++) {
                    tempFlow[screenHeight*(q-1)+j] = flow[colorIndex + q-1];
                }
            }

            //up part1
            for (int k = 0; k <screenWidth - index; k++) {
                int colorIndex = (k + index)*height;//this is right
                while (colorIndex >= flow.length) {
                    colorIndex -= flow.length;
                }
                for (int q=0;q<height;q++){
                    tempFlow[((k* screenHeight)+ q)]= flow[colorIndex+q];

                }
            }

            //up part2
            for (int k = 1; k <= index; k++) {
                int colorIndex = (index - k)*height;
                while (colorIndex >= flow.length) {
                    colorIndex -= flow.length;
                }
                for (int q=0;q<height;q++){
                    tempFlow[(screenWidth - k ) * screenHeight+q] = flow[colorIndex+q];
                }
            }

            //down part1
            for (int k = 0; k < screenWidth - index; k++){
                int colorIndex = (k + index)*height;//this is right
                while (colorIndex >= flow.length) {
                    colorIndex -= flow.length;
                }
                for (int q=height;q>0;q--){
                    tempFlow[(screenWidth - k) * screenHeight-q] = flow[colorIndex+q-1];
                }
            }


            //down part2
            for (int k = 0; k < index; k++) {
                int colorIndex = (index - k) * height;
                while (colorIndex >= flow.length) {
                    colorIndex -= flow.length;
                }
                for (int q = height; q >0; q--) {
                    tempFlow[((k * screenHeight)+screenHeight - q)] = flow[colorIndex + q-1];
                }
            }



            FullCompressAlgorithm ca=new FullCompressAlgorithm();
            List<Byte> compress = ca.compress(tempFlow);
            byte[] compressFlow = new byte[compress.size()];

            for (int k = 0; k < compress.size(); k++) {
                compressFlow[k]=compress.get(k);
            }
            tempFlowList.add(compressFlow);
        }
        compareAndCompress(tempFlowList);
    }



    private void clockWise(byte[] flow,int height,int length){
        List<byte[]> tempFlowList=new ArrayList<>();

        for (int i = 0,index =0; i < length; i++,index++) {
            byte[] tempFlow = new byte[screenHeight * screenWidth];
            while (index >= screenWidth) {
                index -= screenWidth;
            }

            //right part1
            int colIndex=index;
            while (colIndex>=screenHeight){
                colIndex-=screenHeight;
            }
            for (int j = 0; j < screenHeight-colIndex; j++) {
                int colorIndex = (screenHeight-j) * height;
                while (colorIndex >= flow.length) {
                    colorIndex -= flow.length;
                }
                for (int q = 0; q < height; q++) {
                    tempFlow[tempFlow.length - screenHeight * (q + 1) + j + colIndex] = flow[colorIndex + q];
                }
            }

            //right part2
            for (int j = 0; j <colIndex; j++) {
                int colorIndex= (colIndex-j)*height;
                while (colorIndex>=flow.length){
                    colorIndex-=flow.length;
                }
                for (int q = 0; q < height; q++) {
                    tempFlow[tempFlow.length-screenHeight* (q + 1)+j]=flow[colorIndex+q];
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

            //down part1
            for (int k=screenWidth;k>=0;k--){
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
            for (int k=index;k>=0;k--){
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



            FullCompressAlgorithm ca=new FullCompressAlgorithm();
            List<Byte> compress = ca.compress(tempFlow);
            byte[] compressFlow = new byte[compress.size()];

            for (int k = 0; k < compress.size(); k++) {
                compressFlow[k]=compress.get(k);
            }
            tempFlowList.add(compressFlow);
        }
        compareAndCompress(tempFlowList);
    }



    private void compareAndCompress(List<byte[]> tempList) {
        for (int i = 0; i < tempList.size(); i++) {
            byte[] candidateBytes = tempList.get(i);
            //1.compare with the bytes which in the list's value,if it did't exists,add in.otherwise,should not add.
            //比较是否同一副流水边
            if (mFlowBoundList.size()==0){
                //第一次时候，mFlowBoundList.size()为0，无法进行比较
                mFlowBoundList.add(candidateBytes);
                mFlowMap.put(mIndex, 0);
            }else {
                int size = mFlowBoundList.size();
                for (int tempIndexForFlow = 0; tempIndexForFlow < size; tempIndexForFlow++) {
                    byte[] tempFlowBytes = mFlowBoundList.get(tempIndexForFlow);
                    if (tempFlowBytes.length==candidateBytes.length){
                        //如果长度相同，进而比较内容是否相同
                        boolean isSame=true;
                        for (int m = 0; m < tempFlowBytes.length; m++) {
                            if (tempFlowBytes[m]!=candidateBytes[m]) {
                                isSame=false;//其中有一个byte不同，则两帧为不同
                                break;//退出此次循环
                            }
                        }
                        //如果内容也相同，则为相同的一帧流水边
                        if (isSame) {
                            mFlowMap.put(mIndex,tempIndexForFlow);
                            break;
                        }else {
                            //如果内容不同了，则判断是否为已有流水边的最后一个，假若最后一个都不同，则新增进去
                            if (tempIndexForFlow==size-1){
                                mFlowBoundList.add(candidateBytes);
                                mFlowMap.put(mIndex, mFlowBoundList.size()-1);
                            }
                        }
                    }else {
                        //如果长度不同，则判断是否为最后一个，假若最后一个都不同，则新增进去
                        if (tempIndexForFlow==size-1){
                            mFlowBoundList.add(candidateBytes);
                            mFlowMap.put(mIndex, mFlowBoundList.size()-1);
                        }
                    }
                }
            }
            mIndex++;
        }

    }

    private void convertFileToPixels(File file) {
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        byte[] bitmapPixels = BitmapToPixel.convertBitmapToPixel(bitmap, context);
        mBitmapPixelList.add(bitmapPixels);
        mBitmapHeightList.add(bitmap.getHeight());//保存好每个流水边框的高度
    }

    public Map<Integer, Integer> getFlowMap() {
        return mFlowMap;
    }
}
