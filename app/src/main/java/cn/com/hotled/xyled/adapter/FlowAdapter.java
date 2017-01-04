package cn.com.hotled.xyled.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;
import java.util.List;

import cn.com.hotled.xyled.R;

/**
 * Created by Lam on 2017/1/4.
 */

public class FlowAdapter extends RecyclerView.Adapter {
    List<File> mFileList;
    Context mContext;
    OnItemClickListener mItemClickListener;
    public interface OnItemClickListener{
        void onItemClick(View view,int position);
    }

    public FlowAdapter(List<File> fileList, Context context) {
        mFileList = fileList;
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View inflate = inflater.from(mContext).inflate(R.layout.content_select_flow,parent,false);
        FlowViewHolder typefaceViewHolder = new FlowViewHolder(inflate);
        return typefaceViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        FlowViewHolder flowViewHolder= (FlowViewHolder) holder;
        File file = mFileList.get(position);

        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        // 设置想要的大小
        int newWidth = width*10;
        int newHeight = height*10;
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        flowViewHolder.mImageView.setImageBitmap(newbm);
        if (mItemClickListener!=null){
            flowViewHolder.mLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemClickListener.onItemClick(v,position);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return mFileList.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mItemClickListener=listener;
    }

    class FlowViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;
        LinearLayout mLinearLayout;
        public FlowViewHolder(View itemView) {
            super(itemView);
            mLinearLayout = (LinearLayout) itemView.findViewById(R.id.ll_content_flow);
            mImageView = (ImageView) itemView.findViewById(R.id.iv_rvselectflow);
        }
    }



}
