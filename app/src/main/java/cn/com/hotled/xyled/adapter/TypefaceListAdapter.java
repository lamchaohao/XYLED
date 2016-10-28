package cn.com.hotled.xyled.adapter;

import android.content.Context;
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

import cn.com.hotled.xyled.R;

/**
 * Created by Lam on 2016/10/28.
 */

public class TypefaceListAdapter extends BaseAdapter {
    Context mContext;
    File[] typeFaceList;
    public TypefaceListAdapter(File[] typefaceList, Context context){
        this.typeFaceList=typefaceList;
        mContext=context;
    }

    @Override
    public int getCount() {
        return typeFaceList.length;
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
        viewHolder.textView.setText(typeFaceList[position].getName());
        viewHolder.textView.setTypeface(Typeface.createFromFile(typeFaceList[position]));

        return ll_typeface_content;
    }
    class ViewHolder{
        ImageView imageView;
        TextView textView;
    }
}
