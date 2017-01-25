package cn.com.hotled.xyled.flowbound;

import java.util.List;

/**
 * Created by Lam on 2017/1/3.
 */

public abstract class BaseFlowBound {
    int screenWidth;
    int screenHeight;

    public BaseFlowBound(int screenWidth,int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight=screenHeight;
    }

    public abstract List<byte[]> genFlowBound();

}
