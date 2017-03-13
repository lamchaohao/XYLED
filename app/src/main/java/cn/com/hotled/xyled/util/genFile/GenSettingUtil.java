package cn.com.hotled.xyled.util.genFile;

import android.content.Context;
import android.content.SharedPreferences;

import cn.com.hotled.xyled.bean.TraceFile;
import cn.com.hotled.xyled.global.Global;

/**
 * Created by Lam on 2017/3/13.
 */

public class GenSettingUtil {

    private Context mContext;
    private TraceFile mTraceFile;

    public GenSettingUtil(Context context, TraceFile file) {
        mContext=context;
        mTraceFile =file;
    }

    public byte[] genData(){
        //取出设置
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Global.SP_SCREEN_CONFIG, mContext.MODE_PRIVATE);
        int width = sharedPreferences.getInt(Global.KEY_SCREEN_W, 64);
        int height = sharedPreferences.getInt(Global.KEY_SCREEN_H, 64);
        int rgbOrder = sharedPreferences.getInt(Global.KEY_RGB_ORDER, 0);
        int data = sharedPreferences.getInt(Global.KEY_DATA, 0);
        int oe = sharedPreferences.getInt(Global.KEY_OE, 0);
        int code = sharedPreferences.getInt(Global.KEY_138CODE, 0);
        int dataOrient = sharedPreferences.getInt(Global.KEY_DATA_ORIENTATION, 0);
        int special = sharedPreferences.getInt(Global.KEY_SPECIAL, 0);
        byte[] dataBytes =new byte[512];


        int picture =width*height;
        int foldCount = mTraceFile.getFoldCount();
        int scanCount = mTraceFile.getScanCount();
        int line = picture/foldCount;
        int output = height/(scanCount*foldCount);
        byte[] scanOrderArray=new byte[16];
        for (int i = 0; i < scanOrderArray.length; i++) {
            scanOrderArray[i]= (byte) i;
        }
        int route = mTraceFile.getDotCount();
        int batH = height/foldCount;
        int batW = mTraceFile.getModuleWidth();

        String fileName = Global.FILE_NAME;
        byte[] fileBytes = fileName.getBytes();     //0-11 文件名
        ByteUtil.setInbyteArray(0,fileBytes,dataBytes);
        dataBytes[19] = 97; //19 版本号
        dataBytes[24] = 81; //24

        byte[] pictureArray = ByteUtil.intToByteArray(picture, 3);//实像素  32-34
        ByteUtil.setInbyteArray(32,pictureArray,dataBytes);
        byte[] widthArray = ByteUtil.intToByteArray(width, 2); //35-36 width
        ByteUtil.setInbyteArray(35,widthArray,dataBytes);
        dataBytes[37]= (byte) height;  //37 height
        if (code==1){
            scanCount+=128; //无138 最高位为1
        }
        dataBytes[38]= (byte) scanCount; //38 扫描次数
        byte[] lineArray = ByteUtil.intToByteArray(line, 2); //39-40 线带点数
        ByteUtil.setInbyteArray(39,lineArray,dataBytes);
        dataBytes[41] = (byte) output; //41 输出端口
        dataBytes[42] = (byte) data; //data相位
        dataBytes[43] = (byte) oe;  //oe
        ByteUtil.setInbyteArray(48,scanOrderArray,dataBytes); //48-63扫行次序
        byte[] routeArray = ByteUtil.intToByteArray(route, 2);
        ByteUtil.setInbyteArray(64,routeArray,dataBytes); //64-65 走线表点数
        dataBytes[66] = (byte) batH; // 66 1个端口带高度
        dataBytes[67] = (byte) batW; // 67 一个模组宽度

        //----software part-----
        dataBytes[496] = (byte) special;
        dataBytes[497] = (byte) dataOrient;
        dataBytes[498] = (byte) rgbOrder;
        dataBytes[499] = (byte) mTraceFile.getModuleHeight();
        dataBytes[500] = (byte) mTraceFile.getModuleWidth();
        dataBytes[501] = (byte) mTraceFile.getRGBCount();

        return dataBytes;
    }
}
