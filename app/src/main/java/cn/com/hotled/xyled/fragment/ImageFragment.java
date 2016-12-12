package cn.com.hotled.xyled.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.yalantis.ucrop.UCrop;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.view.PhotoView;

import static android.app.Activity.RESULT_OK;

public class ImageFragment extends BaseFragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    @BindView(R.id.pv_fgImage)
    PhotoView mPhotoView;
    @BindView(R.id.iv_fgImage_add)
    ImageView iv_add;
    Bitmap mBitmap;
    public ImageFragment() {
        // Required empty public constructor
    }

    public static ImageFragment newInstance(String param1, String param2) {
        ImageFragment fragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_image, container, false);
        ButterKnife.bind(this,view);
        initView();
        return view;
    }

    private void initView() {
        mPhotoView.enable();
        mPhotoView.setMaxScale(8);
    }


    @OnClick(R.id.iv_fgImage_add)
    void setImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "选择图片"), 199);
    }

    public void setBitmapToView(Bitmap bm){
        mBitmap = bm;
        mPhotoView.setImageBitmap(mBitmap);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public Bitmap getBitmap() {
        return mBitmap;
    }

    @Override
    public float getFrameTime() {
        return 0;
    }

    @Override
    public float getStayTime() {
        return 0;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 199) {
            BitmapFactory.Options opt = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(data.getData().getPath(),opt);
            Bitmap bitmap1 = resizeBitmap(bitmap,opt.outWidth,opt.outHeight);

            setBitmapToView(bitmap1);
            UCrop.Options options = new UCrop.Options();
            options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
            options.setCompressionQuality(100);
            String destinationFileName = "test";
            destinationFileName += ".jpg";
            UCrop uCrop = UCrop.of(data.getData(), Uri.fromFile(new File(getActivity().getCacheDir(), destinationFileName)));
            uCrop = uCrop.withOptions(options);
            uCrop.withAspectRatio(2, 1).withMaxResultSize(100, 100).start(getContext(),this);
        }
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            Uri resultUri = UCrop.getOutput(data);
            if (resultUri != null) {
                Bitmap bitmap = BitmapFactory.decodeFile(resultUri.getPath());
                setBitmapToView(bitmap);

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
}
