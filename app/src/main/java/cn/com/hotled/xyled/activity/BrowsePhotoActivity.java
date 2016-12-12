package cn.com.hotled.xyled.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.view.PhotoView;


public class BrowsePhotoActivity extends Activity {

    private PhotoView photoView;
    private Bitmap mBitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_browse_photo);

        photoView = (PhotoView) findViewById(R.id.pv_browsephoto);
        Bitmap bitmap = getIntent().getParcelableExtra("bitmap");
        photoView.enable();
        photoView.setImageBitmap(bitmap);
        photoView.setMaxScale(16);
        photoView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();//返回咯
            }
        });

    }

    private void setImageToView() {
        File appDir = new File(getFilesDir(),"bmp");
        if (!appDir.exists()){
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".png";
        File file =new File(appDir,fileName);
        FileOutputStream fos = null;
        Bitmap bitmap = getIntent().getParcelableExtra("bitmap");
        try {
            fos =new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG,100,fos);
            fos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (fos!=null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        photoView.setImageBitmap(bitmap);

    }

}
