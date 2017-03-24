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
    public static final String SSID_START = "HC-LED[";
    public static final String SSID_END="]";
    public static final String CARD_PASSWORD="12345678";
    public static final String HC1_FILENAME="COLOR_01.PRG";
    public static final String HC2_FILENAME="LED_HC01.PRG";
    public static final String HC1_CARD="HC-1";
    public static final String HC2_CARD="HC-2";

    //---------------sharedPreferences---------------------
    public static final String SP_SCREEN_CONFIG="ScreenConfig";
    public static final String SP_SYSTEM_CONFIG="SystemConfig";
    public static final String KEY_LANGUAGE="language";
    public static final String KEY_IS_FIRSTIN="isFirstIn";
    public static final String KEY_FIRST_IN_PROGRAM="isFirstInProgram";
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
    public static final String KEY_BATH="batH";
    public static final String KEY_BATW="batW";
    public static final String KEY_DOWNLOAD_ID="apk_downloadId";

    //----------extra for activityResult------------


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
    public static final int JUST_STOP_ANIM = 405;
    public static final int UPDATE_PROGRESS = 500;
    public static final int SEND_TEST = 501;
    public static final int Start_Send = 700;
    public static final int SOCKET_ERRO = 13001;
    public static final int TEST_OK = 13200;
    public static final int PAUSE_OK = 13201;
    public static final int RESUME_OK = 13202;
    public static final int RESET_OK = 13203;
    public static final int FILE_ILLEGAL = 13400;


    public static final int WIFI_AVAILABLE_ACTION = 8001;
    public static final int WIFI_DISABLE = 8002;
    public static final int WIFI_ENABLED = 8003;
    public static final String EXTRA_NETWORKSTATE = "network_state";
    public static final int UPDATE_NETWORK_INFO = 8004;
}
