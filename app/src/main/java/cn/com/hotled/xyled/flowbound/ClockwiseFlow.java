package cn.com.hotled.xyled.flowbound;

import java.util.ArrayList;
import java.util.List;

import cn.com.hotled.xyled.util.FullCompressAlgorithm;

/**
 * Created by Lam on 2017/1/3.
 */

public class ClockwiseFlow extends BaseFlowBound{

    public ClockwiseFlow(int screenWidth, int screenHeight, byte[] color, int colorWidth, int frameCount) {
        super(screenWidth, screenHeight, color, colorWidth, frameCount);
    }

    @Override
    public List<byte[]> genFlowBound(){

        List<byte[]> flowBoundList =new ArrayList<>();
        byte[] flowColor=new byte[]{3,12,16};
        byte[] flow =new byte[colorWidth*flowColor.length];
        int loopIndex = 0;
        for (int m = 0; m < flowColor.length; m++) {
            for (int p = 0; p <colorWidth; p++) {
                flow[loopIndex]=flowColor[m];
                loopIndex++;
            }
        }
        for (int i = 0,index =0; i < frameCount; i++,index++) {
            byte[] tempFlow = new byte[screenHeight*screenWidth];
            while(index>=screenWidth){
                index-=screenWidth;
            }

            //up part1
            for (int k=0;k<screenWidth-index;k++){
                int colorIndex=k+index;//this is right
                while (colorIndex>=flow.length){
                    colorIndex-=flow.length;
                }
                tempFlow[(screenWidth-k-1)*screenHeight]=flow[colorIndex];
                tempFlow[(screenWidth-k-1)*screenHeight+1]=flow[colorIndex];
            }

            //up part2
            for (int k=0;k <index;k++){
                int colorIndex=index-k;
                while (colorIndex>=flow.length){
                    colorIndex-=flow.length;
                }
                tempFlow[(k)*screenHeight]=flow[colorIndex];
                tempFlow[(k)*screenHeight+1]=flow[colorIndex];
            }

            //right part1
            int colIndex=index;
            while (colIndex>=screenHeight){
                colIndex-=screenHeight;
            }
            for (int j = 2; j < screenHeight-colIndex; j++) {
                int colorIndex=j;
                while (colorIndex>=flow.length){
                    colorIndex-=flow.length;
                }
                tempFlow[tempFlow.length-screenHeight+j+colIndex]=flow[colorIndex];
                tempFlow[tempFlow.length-screenHeight*2+j+colIndex]=flow[colorIndex];
            }

            //right part2
            for (int j = 0; j <colIndex; j++) {
                int colorIndex= screenHeight-colIndex+j;
                while (colorIndex>=flow.length){
                    colorIndex-=flow.length;
                }
                tempFlow[tempFlow.length-screenHeight+j]=flow[colorIndex];
                tempFlow[tempFlow.length-screenHeight*2+j]=flow[colorIndex];
            }

            //down part1
            for (int k=screenWidth;k>=2;k--){
                int colorIndex=k;
                while (colorIndex>=flow.length){
                    colorIndex-=flow.length;
                }
                if (k>index){
                    tempFlow[(k-index)*screenHeight-2]=flow[colorIndex];
                    tempFlow[((k-index)*screenHeight)-1]=flow[colorIndex];
                }else if (k==index){
                    tempFlow[screenHeight-2]=flow[colorIndex];
                    tempFlow[screenHeight-1]=flow[colorIndex];
                }

            }
            //down part2
            for (int k=index;k>=2;k--){
                int colorIndex=index-k;
                while (colorIndex>=flow.length){
                    colorIndex-=flow.length;
                }
                if (screenWidth-k!=0){
                    tempFlow[(screenWidth-k)*screenHeight-2]=flow[colorIndex];
                    tempFlow[((screenWidth-k)*screenHeight)-1]=flow[colorIndex];
                }else if (screenWidth==index){
                    tempFlow[screenHeight-2]=flow[colorIndex];
                    tempFlow[screenHeight-1]=flow[colorIndex];
                }

            }

            //left part1
            for (int j = 0; j < screenHeight-colIndex; j++) {
                int colorIndex=colIndex+j;
                while (colorIndex>=flow.length){
                    colorIndex-=flow.length;
                }
                tempFlow[j]=flow[colorIndex];
                tempFlow[screenHeight+j]=flow[colorIndex];
            }

            //left part2
            for (int j = 0; j <colIndex; j++) {
                int colorIndex= colIndex-j;
                while (colorIndex>=flow.length){
                    colorIndex-=flow.length;
                }
                tempFlow[screenHeight-1-j]=flow[colorIndex];
                tempFlow[screenHeight*2-1-j]=flow[colorIndex];
            }



            FullCompressAlgorithm ca=new FullCompressAlgorithm();
            List<Byte> compress = ca.compress(tempFlow);
            byte[] compressFlow = new byte[compress.size()];

            for (int k = 0; k < compress.size(); k++) {
                compressFlow[k]=compress.get(k);
            }
            flowBoundList.add(compressFlow);
        }
        return  flowBoundList;
    }

}
