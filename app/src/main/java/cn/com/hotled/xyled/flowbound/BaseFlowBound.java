package cn.com.hotled.xyled.flowbound;

import android.content.Context;

import java.util.List;

/**
 * Created by Lam on 2017/1/3.
 */

public abstract class BaseFlowBound {
    int screenWidth;
    int screenHeight;
    Context context;
    public BaseFlowBound(Context context,int screenWidth, int screenHeight) {
        this.context=context;
        this.screenWidth = screenWidth;
        this.screenHeight=screenHeight;
    }

    public abstract List<byte[]> genFlowBound();

}
