package cn.com.hotled.xyled.flowbound;

import java.util.List;

/**
 * Created by Lam on 2017/1/3.
 */

public abstract class BaseFlowBound {
    int screenWidth;
    int screenHeight;
    int frameCount;
    int colorWidth;
    byte[] flowColor;
    public BaseFlowBound(int screenWidth,int screenHeight,byte[] color,int colorWidth,int frameCount) {
        this.screenWidth = screenWidth;
        this.screenHeight=screenHeight;
        flowColor=color;
        this.colorWidth=colorWidth;
        this.frameCount=frameCount;
    }

    public abstract List<byte[]> genFlowBound();

}
