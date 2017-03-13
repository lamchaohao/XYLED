package cn.com.hotled.xyled.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.bean.Program;
import cn.com.hotled.xyled.bean.ProgramType;
import cn.com.hotled.xyled.bean.TextContent;
import cn.com.hotled.xyled.dao.TextContentDao;

/**
 * Created by Lam on 2016/12/2.
 */

public class ProgramAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<Program> mProgramList;
    private TextContentDao mTextContentDao;
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;
    public ProgramAdapter(Context context, List<Program> programs, TextContentDao dao) {
        mContext=context;
        mProgramList=programs;
        mTextContentDao=dao;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View screenView = inflater.from(mContext).inflate(R.layout.content_screen_recycler,parent,false);
        viewHolder = new ScreenViewHolder(screenView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ScreenViewHolder viewHolder= (ScreenViewHolder) holder;

        ProgramType programType = mProgramList.get(position).getProgramType();
        if (programType== ProgramType.Text)
            viewHolder.progmIcon.setImageResource(R.drawable.ic_text_fields_green_600_36dp);
        else if (programType== ProgramType.Pic)
            viewHolder.progmIcon.setImageResource(R.drawable.ic_photo_deep_orange_500_36dp);
        viewHolder.tvTitle.setText(mProgramList.get(position).getProgramName());
        long id = mProgramList.get(position).getId();
        List<TextContent> list = mTextContentDao.queryBuilder().where(TextContentDao.Properties.ProgramId.eq(id)).list();
        TextContent textContent = null;
        if (list.size()==1) {
            textContent = list.get(0);
        }
        String text ="";
        if (textContent!=null) {
            text = textContent.getText();
        }
        if (TextUtils.isEmpty(text)&&programType==ProgramType.Text) {
            viewHolder.tvContent.setText("无文字");
        }else {
            viewHolder.tvContent.setText(text);
        }
        if (mOnItemClickListener!=null){
            viewHolder.viewParent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onClick(v,position);
                }
            });
        }
        if (mOnItemLongClickListener!=null){
            viewHolder.viewParent.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mOnItemLongClickListener.onLongClick(v,position);
                    return true;
                }
            });
        }


    }


    @Override
    public int getItemCount() {

        return mProgramList.size();
    }

    private class ScreenViewHolder extends RecyclerView.ViewHolder{
        ImageView progmIcon;
        TextView tvTitle;
        TextView tvContent;
        LinearLayout programParent;
        LinearLayout viewParent;
        public ScreenViewHolder(View itemView) {
            super(itemView);
            programParent= (LinearLayout) itemView.findViewById(R.id.ll_contentScreen_program);
            progmIcon = (ImageView) itemView.findViewById(R.id.iv_screenProgm_program);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_screenProgm_title);
            tvContent = (TextView) itemView.findViewById(R.id.tv_screenProgm_content);
            viewParent = (LinearLayout) itemView.findViewById(R.id.ll_contentScreen);
        }

    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mOnItemClickListener=listener;
    }

    public interface OnItemClickListener{
        void onClick(View v,int position);
    }

    public interface OnItemLongClickListener{
        void onLongClick(View v,int position);
    }
    public void setOnItemLongClickListener(OnItemLongClickListener longlistener){
        mOnItemLongClickListener=longlistener;
    }
}
