package cn.com.hotled.xyled.global;

/**
 * Created by Lam on 2017/1/20.
 */

public class Global {
    public static final int FLOW_EFFECT_CLOCKWISE=0;
    public static final int FLOW_EFFECT_ANTICLOCKWISE=1;
    public static final int TEXT_CONTENT_CHANGE_CODE =2;
    public static final String SERVER_IP="192.168.3.1";
    public static final int SERVER_PORT=16389;
    public static final String CARD_PASSWORD="88888888";
    public static final String FILE_NAME="COLOR_01.PRG";

    //---------------sharedPreferences---------------------
    public static final String SP_SCREEN_CONFIG="ScreenConfig";
    public static final String SP_SYSTEM_CONFIG="SystemConfig";
    public static final String KEY_IS_FIRSTIN="isFirstIn";
    public static final String KEY_SCREEN_W="screenWidth";
    public static final String KEY_SCREEN_H="screenHeight";
    public static final String KEY_SCREEN_SCAN="screenScanCount";
    public static final String KEY_RGB_ORDER="rgb_order";
    public static final String KEY_CARD_SERIES="cardSeries";
    public static final String KEY_TRACE_SELECT="traceSelected";
    public static final String KEY_DATA_ORIENTATION="dataOrientation";
    public static final String KEY_SPECIAL="special";
    public static final String KEY_DATA="data";
    public static final String KEY_OE="OE";
    public static final String KEY_138CODE="138code";

    //----------extra for activityResult------------
    public static final String EXTRA_SELECT_FONT= "fontFileName";
    public static final String EXTRA_SELECT_FLOW = "flowFileName";
    public static final String EXTRA_SELECT_TRACE = "traceFileName";
    public static final String EXTRA_TEXT_CONTENT="textContent";

    public static final int TEXT_EFFECT_MOVE_LEFT=0;
    public static final int TEXT_EFFECT_MOVE_RIGHT=1;
    public static final int TEXT_EFFECT_APPEAR_MOVE_LEFT=2;
    public static final int TEXT_EFFECT_APPEAR_MOVE_RIGHT=3;
    public static final int TEXT_EFFECT_STATIC=4;
    public static final int TEXT_EFFECT_MOVE_UP=5;
    public static final int TEXT_EFFECT_MOVE_DOWN=6;

    //-----------------handler message code-----------------------
    public static final int READ_SUCCESS = 200;
    public static final int SEND_DONE = 201;
    public static final int GENFILE_DONE = 203;
    public static final int READ_FAILE = 400;
    public static final int PAUSE_FAILE = 401;
    public static final int CONNECT_TIMEOUT = 402;
    public static final int CONNECT_NORESPONE = 403;
    public static final int WIFI_ERRO = 404;
    public static final int UPDATE_PROGRESS = 500;
}
