package cn.com.hotled.xyled.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.bean.TypefaceFile;

/**
 * Created by Lam on 2016/10/28.
 */

public class TypefaceListAdapter extends BaseAdapter {
    Context mContext;
    List<TypefaceFile> typeFaceList;
    public TypefaceListAdapter(List<TypefaceFile> typefaceList, Context context){
        this.typeFaceList=typefaceList;
        mContext=context;
    }

    @Override
    public int getCount() {
        return typeFaceList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=null;
        LinearLayout  ll_typeface_content=null;
        if (convertView!=null){
            ll_typeface_content= (LinearLayout) convertView;
            viewHolder= (ViewHolder)convertView.getTag();
        }else {
            ll_typeface_content = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.content_typeface, null);
            viewHolder=new ViewHolder();
            viewHolder.textView = (TextView) ll_typeface_content.findViewById(R.id.tv_typeFace_content);
            viewHolder.imageView= (ImageView) ll_typeface_content.findViewById(R.id.iv_typeFace_content);
            ll_typeface_content.setTag(viewHolder);
        }
        String fileName = typeFaceList.get(position).getFile().getName();
        viewHolder.textView.setText(dealFileName(fileName));
        if (typeFaceList.get(position).isSelected()){
            viewHolder.imageView.setImageResource(R.drawable.ic_check_circle_green_700_36dp);
            viewHolder.textView.setTextColor(Color.parseColor("#FF4081"));
        }else {
            viewHolder.imageView.setImageResource(R.drawable.ic_text_fields_black_36dp);
            viewHolder.textView.setTextColor(Color.parseColor("#212121"));
        }
        //设置显示的字体
        viewHolder.textView.setTypeface(Typeface.createFromFile(typeFaceList.get(position).getFile()));

        return ll_typeface_content;
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

    class ViewHolder{
        ImageView imageView;
        TextView textView;
    }
}
