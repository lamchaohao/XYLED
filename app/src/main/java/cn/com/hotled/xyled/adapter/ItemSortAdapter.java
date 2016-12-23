package cn.com.hotled.xyled.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.bean.Program;
import cn.com.hotled.xyled.bean.ProgramType;

/**
 * Created by Lam on 2016/12/19.
 */

public class ItemSortAdapter extends BaseAdapter {
    private Context mContext;
    List<Program> mPrograms;

    public ItemSortAdapter(Context context, List<Program> programs) {
        mContext = context;
        mPrograms = programs;
    }

    @Override
    public int getCount() {
        return mPrograms.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return mPrograms.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout ll_sort = null;
        ViewHolder viewHolder = null;
        if (convertView!=null){
            ll_sort= (LinearLayout) convertView;
            viewHolder = (ViewHolder) convertView.getTag();

        }else {
            View inflate = layoutInflater.from(mContext).inflate(R.layout.content_drag_sort,parent,false);
            ll_sort= (LinearLayout) inflate.findViewById(R.id.ll_dragSort);
            viewHolder = new ViewHolder();
            viewHolder.programTypeView = (ImageView) inflate.findViewById(R.id.iv_screenProgm_program);
            viewHolder.programNameView = (TextView) inflate.findViewById(R.id.tv_screenProgm_text);
            ll_sort.setTag(viewHolder);
        }

        if (mPrograms.get(position).getProgramType()== ProgramType.Pic){
            viewHolder.programTypeView.setImageResource(R.drawable.ic_photo_deep_orange_500_36dp);
        }else if (mPrograms.get(position).getProgramType()== ProgramType.Text){
            viewHolder.programTypeView.setImageResource(R.drawable.ic_text_fields_green_600_36dp);
        }
        viewHolder.programNameView.setText(mPrograms.get(position).getProgramName());

        return ll_sort;
    }


    private class ViewHolder {
        ImageView programTypeView;
        TextView programNameView;
//        ImageView dragHanlderView;
    }
}
