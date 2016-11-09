package cn.com.hotled.xyled.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.bean.TypefaceFile;

/**
 * Created by Lam on 2016/11/9.
 */

public class TypefaceAdapter extends RecyclerView.Adapter {
    Context mContext;
    List<TypefaceFile> typeFaceList;
    private OnItemOnClickListener onItemOnClickListener;
    private int oldPosition=-1;

    public interface OnItemOnClickListener{
        void onItemClick(View view , int position);
    }

    public TypefaceAdapter(List<TypefaceFile> typeFaceList,Context context) {
        this.mContext = context;
        this.typeFaceList = typeFaceList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View inflate = inflater.from(mContext).inflate(R.layout.content_typeface,parent,false);
        TypefaceViewHolder typefaceViewHolder = new TypefaceViewHolder(inflate);
        return typefaceViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//        holder.setIsRecyclable(true);
        final TypefaceViewHolder viewHolder= (TypefaceViewHolder) holder;
        String fileName = typeFaceList.get(position).getFile().getName();
        viewHolder.textView.setText(dealFileName(fileName));//设置文字
        if (typeFaceList.get(position).isSelected()){
            viewHolder.imageView.setImageResource(R.drawable.ic_check_circle_green_700_36dp);
            viewHolder.textView.setTextColor(Color.parseColor("#FF4081"));
        }else {
            viewHolder.imageView.setImageResource(R.drawable.ic_text_fields_black_36dp);
            viewHolder.textView.setTextColor(Color.parseColor("#212121"));
        }
        //设置显示的字体
        viewHolder.textView.setTypeface(Typeface.createFromFile(typeFaceList.get(position).getFile()));

        if (onItemOnClickListener!=null){
            viewHolder.linearlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //拿到被点击的位置
                    int layoutPosition = viewHolder.getLayoutPosition();
                    if (oldPosition!=-1){
                        //非第一次点击
                        typeFaceList.get(oldPosition).setSelected(false);
                        typeFaceList.get(layoutPosition).setSelected(true);
                        notifyItemChanged(layoutPosition);
                        if (layoutPosition!=oldPosition)
                            notifyItemChanged(oldPosition);
                    }else {
                        //第一次点击
                        oldPosition =layoutPosition;
                        boolean selected = typeFaceList.get(layoutPosition).isSelected();
                        typeFaceList.get(layoutPosition).setSelected(!selected);//取反操作
                        notifyItemChanged(layoutPosition);
                    }
                    oldPosition =layoutPosition;
                    onItemOnClickListener.onItemClick(v,layoutPosition);
                }
            });
        }

    }

    private String dealFileName(String fileName) {
        //2.get lastIndexof -Regular
        int lastIndexOf = fileName.lastIndexOf("-Regular");
        //3.substring
        String substring=null;
        if (lastIndexOf!=-1){
            substring = fileName.substring(0, lastIndexOf);
            return substring;
        }else {
            return fileName;
        }
    }


    @Override
    public int getItemCount() {
        return typeFaceList.size();
    }

    public void setOnItemClickListener(OnItemOnClickListener itemOnClickListener){
        this.onItemOnClickListener=itemOnClickListener;
    }

    class TypefaceViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;
        LinearLayout linearlayout;
        public TypefaceViewHolder(View itemView) {
            super(itemView);
            linearlayout = (LinearLayout) itemView.findViewById(R.id.ll_typeface_content);
            textView= (TextView) itemView.findViewById(R.id.tv_typeFace_content);
            imageView = (ImageView) itemView.findViewById(R.id.iv_typeFace_content);
        }
    }
}
