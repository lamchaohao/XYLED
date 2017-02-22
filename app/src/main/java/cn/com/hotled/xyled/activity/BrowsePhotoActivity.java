package cn.com.hotled.xyled.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import java.io.File;

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
        photoView.enable();
        setImageToView();
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
        File previewFile = new File(getCacheDir()+"/preview.png");
        Bitmap bitmap = BitmapFactory.decodeFile(previewFile.getAbsolutePath());
        photoView.setImageBitmap(bitmap);

    }

}
