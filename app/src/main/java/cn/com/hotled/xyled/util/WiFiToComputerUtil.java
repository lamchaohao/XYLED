package cn.com.hotled.xyled.util;

import android.app.Activity;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import cn.com.hotled.xyled.bean.Program;

/**
 * Created by Lam on 2017/1/12.
 */

public class WiFiToComputerUtil extends PicCompressUtil{
    private byte[] mHeadBytes;
    private WifiMutilMoveCompressUtil mUtil;
    private Activity mContext;

    public WiFiToComputerUtil(Activity context, List<Program> programs, int screenWidth, int screenHeight, float frameTime, float stayTime) {
        super(context, programs, screenWidth, screenHeight, frameTime, stayTime);
        this.mContext=context;
    }

    private void setHeadBytes(){
        File file =new File(Environment.getExternalStorageDirectory()+"/COLOR_01.PRG");
        mHeadBytes = new byte[4096];
        FileInputStream fis=null;
        if (file.exists()) {
            try {
                fis=new FileInputStream(file);
                fis.read(mHeadBytes);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if (fis!=null){
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void startGen(){
        setHeadBytes();
        FileOutputStream fos = null;
        FileInputStream fis=null;
        File file = new File(mContext.getFilesDir() + "/toComputer.prg");
        File inputFile = new File( mContext.getFilesDir()+ "/color.prg");
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
            fos = new FileOutputStream(file, true);
            fis =new FileInputStream(inputFile);
            byte[] temp=new byte[2048];
            int len=-1;
            fos.write(mHeadBytes);
            while((len=fis.read(temp,0,temp.length))!=-1){
                fos.write(temp,0,len);
            }

        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null)
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if (fis!=null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
