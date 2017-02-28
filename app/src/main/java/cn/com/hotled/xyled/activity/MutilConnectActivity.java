package cn.com.hotled.xyled.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import cn.com.hotled.xyled.R;

public class MutilConnectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mutil_connect);
        ImageView viewById = (ImageView) findViewById(R.id.mutil_showPng);
        Bitmap bitmap = BitmapFactory.decodeFile(getFilesDir() + "/64for.png");
        viewById.setImageBitmap(bitmap);
    }


}
