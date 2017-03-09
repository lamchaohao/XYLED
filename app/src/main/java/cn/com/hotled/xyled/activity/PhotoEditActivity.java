package cn.com.hotled.xyled.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import cn.com.hotled.xyled.App;
import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.bean.Program;
import cn.com.hotled.xyled.dao.ProgramDao;
import cn.com.hotled.xyled.global.Global;
import cn.com.hotled.xyled.view.PhotoView;

public class PhotoEditActivity extends BaseActivity implements SeekBar.OnSeekBarChangeListener{

    private static final int REQUEST_CHOOSE_PIC = 210;
    PhotoView mPhotoView;
    FloatingActionButton fabAdd;
    Bitmap mBitmap;
    private Program mProgram;
    private int mScreenWidth;
    private int mScreenHeight;
    private SeekBar mSbStaytime;
    private TextView mTvShowStaytime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_edit);

        initView();
        loadData();
    }

    private void loadData() {

        long programId = getIntent().getLongExtra("programId",-1);
        List<Program> list = ((App) getApplication()).getDaoSession().getProgramDao().queryBuilder().where(ProgramDao.Properties.Id.eq(programId)).list();
        if (list==null) {
            mProgram=new Program();
        }else{
            mProgram = list.get(0);
            File picFile = mProgram.getPicFile();
            if (picFile!=null){
                Bitmap bitmap = BitmapFactory.decodeFile(picFile.getAbsolutePath());
                setBitmapToView(bitmap);
            }
            float stayTime = mProgram.getStayTime();
            stayTime *=2;
            if (stayTime==0){
                stayTime = 2;
            }
            mSbStaytime.setProgress((int) stayTime);
        }
    }

    private void initView() {
        mPhotoView= (PhotoView) findViewById(R.id.pv_photo);
        fabAdd = (FloatingActionButton) findViewById(R.id.fab_photo_add);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setImage();
            }
        });
        mPhotoView.enable();
        mPhotoView.setMaxScale(8);
        mScreenWidth = getSharedPreferences(Global.SP_SCREEN_CONFIG, MODE_PRIVATE).getInt(Global.KEY_SCREEN_W, 64);
        mScreenHeight = getSharedPreferences(Global.SP_SCREEN_CONFIG, MODE_PRIVATE).getInt(Global.KEY_SCREEN_H, 32);
        mSbStaytime = (SeekBar) findViewById(R.id.sb_photo_stayTime);
        mTvShowStaytime = (TextView) findViewById(R.id.tv_photo_showStaytime);
        mSbStaytime.setOnSeekBarChangeListener(this);
    }

    public void setImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "选择图片"), REQUEST_CHOOSE_PIC);
    }
    public void setBitmapToView(Bitmap bm){
        mBitmap = bm;
        mPhotoView.setImageBitmap(mBitmap);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CHOOSE_PIC) {
            UCrop.Options options = new UCrop.Options();
            options.setCompressionFormat(Bitmap.CompressFormat.PNG);
            options.setCompressionQuality(100);

            UCrop uCrop = UCrop.of(data.getData(), Uri.parse(data.toURI()));
            uCrop = uCrop.withOptions(options);
            uCrop.start(this);
//
        }
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            Uri resultUri = UCrop.getOutput(data);
            Bitmap bitmap = BitmapFactory.decodeFile(resultUri.getPath());
            Bitmap bitmap1 = resizeBitmap(bitmap, mScreenWidth, mScreenHeight);
            String destinationFileName="";
            if (mProgram.getPicFile()==null||mProgram.getPicFile().getName().equals("")){
                File fileDir=new File(getFilesDir().getAbsolutePath() +"/pic");
                if (!fileDir.exists()) {
                    fileDir.mkdir();
                }
                destinationFileName=fileDir.getAbsolutePath() +"/"+ System.currentTimeMillis()+".png";
            }else {
                destinationFileName=mProgram.getPicFile().getAbsolutePath();
            }

            File file= new File(destinationFileName);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            FileOutputStream fileOutputStream=null;
            try {
                fileOutputStream = new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            if (fileOutputStream!=null){
                bitmap1.compress(Bitmap.CompressFormat.PNG,100,fileOutputStream);
            }
            setBitmapToView(bitmap1);
            mProgram.setPicFile(file);

            } else if (resultCode == UCrop.RESULT_ERROR) {
                final Throwable cropError = UCrop.getError(data);
            }
        }

    public  Bitmap resizeBitmap(Bitmap bitmap, int wid, int hei)
    {
        Bitmap BitmapOrg = bitmap;
        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();
        int newWidth = wid;
        int newHeight = hei;

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // if you want to rotate the Bitmap
        // matrix.postRotate(45);
        Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
                height, matrix, true);
        return resizedBitmap;
    }

    @Override
    public void onBackPressed() {
        ((App) getApplication()).getDaoSession().getProgramDao().insertOrReplace(mProgram);
        super.onBackPressed();
    }

    @Override
    public void onCreateCustomToolBar(Toolbar toolbar) {
        super.onCreateCustomToolBar(toolbar);
        toolbar.setTitle(getIntent().getStringExtra("programName"));
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.sb_photo_stayTime:
                int second = progress/2;
                mTvShowStaytime.setText(second+" s");
                mProgram.setStayTime(second);
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
