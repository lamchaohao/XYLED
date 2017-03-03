package cn.com.hotled.xyled.global;

/**
 * Created by Lam on 2017/1/20.
 */

public class Global {
    public static final int FLOW_EFFECT_CLOCKWISE=0;
    public static final int FLOW_EFFECT_ANTICLOCKWISE=1;
    public static final String SERVER_IP="192.168.3.1";
    public static final int SERVER_PORT=16389;
    public static final String CARD_PASSWORD="88888888";
    public static final String SP_SCREEN_CONFIG="ScreenConfig";
    public static final String SP_SYSTEM_CONFIG="SystemConfig";
    public static final String KEY_IS_FIRSTIN="isFirstIn";
    public static final String KEY_SCREEN_W="screenWidth";
    public static final String KEY_SCREEN_H="screenHeight";
    public static final String KEY_SCREEN_SCAN="screenScanCount";
    public static final String KEY_RGB_ORDER="rgb_order";

    //----------extra for activityResult------------
    public static final String EXTRA_SELECT_FONT= "fontFileName";
    public static final String EXTRA_SELECT_FLOW = "flowFileName";

    public static final int TEXT_EFFECT_MOVE_LEFT=0;
    public static final int TEXT_EFFECT_MOVE_RIGHT=1;
    public static final int TEXT_EFFECT_APPEAR_MOVE_LEFT=2;
    public static final int TEXT_EFFECT_APPEAR_MOVE_RIGHT=3;
    public static final int TEXT_EFFECT_STATIC=4;
    public static final int TEXT_EFFECT_MOVE_UP=5;
    public static final int TEXT_EFFECT_MOVE_DOWN=6;
}
