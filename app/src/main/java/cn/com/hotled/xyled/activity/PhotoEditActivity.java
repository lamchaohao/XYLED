package cn.com.hotled.xyled.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;

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
import cn.com.hotled.xyled.view.PhotoView;

public class PhotoEditActivity extends BaseActivity {

    private static final int REQUEST_CHOOSE_PIC = 210;
    PhotoView mPhotoView;
    FloatingActionButton fabAdd;
    Bitmap mBitmap;
    private Program mProgram;
    private boolean isPicChanged;

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
//            BitmapFactory.Options opt = new BitmapFactory.Options();
//            Bitmap bitmap = BitmapFactory.decodeFile(data.getData().getPath(),opt);
//            Bitmap bitmap1 = resizeBitmap(bitmap,opt.outWidth,opt.outHeight);
//
//            setBitmapToView(bitmap1);
//            UCrop.Options options = new UCrop.Options();
//            options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
//            options.setCompressionQuality(100);
//            String destinationFileName = "";
//            if (mProgram.getPicFile()==null||mProgram.getPicFile().getName().equals("")){
//                destinationFileName=System.currentTimeMillis()+".jpg";
//            }else {
//                destinationFileName=mProgram.getPicFile().getName();
//            }
//
//            UCrop uCrop = UCrop.of(data.getData(), Uri.fromFile(new File(getCacheDir(), destinationFileName)));
//            uCrop = uCrop.withOptions(options);
//            uCrop.withAspectRatio(2, 1).withMaxResultSize(100, 100).start(this);
            Bitmap bitmap = BitmapFactory.decodeFile(data.getData().getPath());
            Bitmap bitmap1 = resizeBitmap(bitmap, 64, 32);
            String destinationFileName="";
            if (mProgram.getPicFile()==null||mProgram.getPicFile().getName().equals("")){
                File fileDir=new File(getFilesDir().getAbsolutePath() +"/pic");
                if (!fileDir.exists()) {
                    fileDir.mkdir();
                }
                destinationFileName=fileDir.getAbsolutePath() +"/"+ System.currentTimeMillis()+".jpg";
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
                bitmap1.compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream);
            }
            setBitmapToView(bitmap1);
            isPicChanged =true;
            mProgram.setPicFile(file);
        }
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            Uri resultUri = UCrop.getOutput(data);
            if (resultUri != null) {
                Bitmap bitmap = BitmapFactory.decodeFile(resultUri.getPath());
                Bitmap bitmap1 = resizeBitmap(bitmap, 64, 32);
                String destinationFileName="";
                if (mProgram.getPicFile()==null||mProgram.getPicFile().getName().equals("")){
                    destinationFileName=getFilesDir().getAbsolutePath() +"/"+ System.currentTimeMillis()+".jpg";
                }else {
                    destinationFileName=mProgram.getPicFile().getAbsolutePath();
                }
                File file= new File(destinationFileName);
                FileOutputStream fileOutputStream=null;
                try {
                    fileOutputStream = new FileOutputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                if (fileOutputStream!=null){
                    bitmap1.compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream);
                }
                setBitmapToView(bitmap1);
                isPicChanged =true;
                mProgram.setPicFile(file);
            } else if (resultCode == UCrop.RESULT_ERROR) {
                final Throwable cropError = UCrop.getError(data);
            }
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
        if (isPicChanged) {
            new AlertDialog.Builder(this)
                    .setTitle("节目内容已改变，是否保存")
                    .setNegativeButton("不保存", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            PhotoEditActivity.super.onBackPressed();
                        }
                    })
                    .setPositiveButton("保存", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ((App) getApplication()).getDaoSession().getProgramDao().insertOrReplace(mProgram);

                            PhotoEditActivity.super.onBackPressed();
                        }
                    })
                    .show();
        }else {
            PhotoEditActivity.super.onBackPressed();
        }
    }

    @Override
    public void onCreateCustomToolBar(Toolbar toolbar) {
        super.onCreateCustomToolBar(toolbar);
        toolbar.setTitle(getIntent().getStringExtra("programName"));
    }
}
