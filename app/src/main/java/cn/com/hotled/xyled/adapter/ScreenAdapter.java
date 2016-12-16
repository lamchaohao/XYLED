package cn.com.hotled.xyled.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.bean.LedScreen;
import cn.com.hotled.xyled.bean.ProgramType;

/**
 * Created by Lam on 2016/12/2.
 */

public class ScreenAdapter extends RecyclerView.Adapter {

    private static final int SCREEN_TYPE = 101;
    private static final int PROGRAM_TYPE = 201;
    private Context mContext;
    List<LedScreen> mScreenList;
    private boolean isDataSetChanged;
    private ArrayList<Integer> mScreenTypePositionList ;
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    public ScreenAdapter(Context context, List<LedScreen> screens) {
        mContext=context;
        mScreenList=screens;
        mScreenTypePositionList = new ArrayList<>();
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
        int itemViewType = getItemViewType(position);
        ScreenViewHolder viewHolder= (ScreenViewHolder) holder;
        switch (itemViewType){
            case SCREEN_TYPE:
                viewHolder.programParent.setVisibility(View.GONE);
                viewHolder.screenNameView.setText(mScreenList.get(getScreenPosition(position)).getScreenName());
                viewHolder.sizeView.setText(mScreenList.get(getScreenPosition(position)).getWidth()+"x"+mScreenList.get(getScreenPosition(position)).getHeight());
                viewHolder.cardView.setText(mScreenList.get(getScreenPosition(position)).getCardName());
                viewHolder.locationView.setText(mScreenList.get(getScreenPosition(position)).getLocation());
                break;
            case PROGRAM_TYPE:
                viewHolder.screenParent.setVisibility(View.GONE);
                ProgramType programType = mScreenList.get(getScreenPosition(position)).getProgramList().get(getProgramPosition(position)).getProgramType();
                if (programType== ProgramType.Text)
                    viewHolder.progmIcon.setImageResource(R.drawable.ic_text_fields_green_600_36dp);
                else if (programType== ProgramType.Pic)
                    viewHolder.progmIcon.setImageResource(R.drawable.ic_photo_deep_orange_500_36dp);
                viewHolder.textView.setText(mScreenList.get(getScreenPosition(position)).getProgramList().get(getProgramPosition(position)).getProgramName());
                break;
        }
        if (mOnItemClickListener!=null){
            viewHolder.viewParent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onClick(v,position,mScreenTypePositionList.contains(position),getScreenPosition(position),getProgramPosition(position));
                }
            });
        }
        if (mOnItemLongClickListener!=null){
            viewHolder.viewParent.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mOnItemLongClickListener.onLongClick(v,position,mScreenTypePositionList.contains(position),getScreenPosition(position),getProgramPosition(position));
                    return true;
                }
            });
        }


    }

    private int getScreenPosition(int position) {
       int screenPosition=0;
        for (int i = 0; i < mScreenTypePositionList.size(); i++) {
         if (position<mScreenTypePositionList.get(i)){
             return i-1;
         }
         if (position==mScreenTypePositionList.get(i)){
             return i;
         }
         if (i==mScreenTypePositionList.size()-1&&position>mScreenTypePositionList.get(i)){
             //最后一个Item，pisition依旧比最后一个屏的position大，则此为节目，屏幕为最后的一个屏幕
             return mScreenTypePositionList.size()-1;
         }
        }
        return screenPosition;

    }

    private int getProgramPosition(int position){
        int screenPosition = getScreenPosition(position); // 先得到它父节点在mScreenTypePositionList的位置
        Integer parentPosition = mScreenTypePositionList.get(screenPosition);
        return position-parentPosition-1; //父节点位置减去其在list中的位置则是其在父节点的子位置
    }

    @Override
    public int getItemCount() {

        int index=0;
        int itemCount=0;
        if (isDataSetChanged) {
            mScreenTypePositionList.clear();
            for (LedScreen ledScreen : mScreenList) {
                itemCount++;//先加屏幕
                if (!mScreenTypePositionList.contains(index))
                    mScreenTypePositionList.add(index);
                index++;
                int size = ledScreen.getProgramList().size();
                index+=size;
                itemCount+=size;//再加节目
            }
        }else {
            //删除了数据后，需要重新计算
            mScreenTypePositionList.clear();
            for (LedScreen ledScreen : mScreenList) {
                itemCount++;//先加屏幕
                if (!mScreenTypePositionList.contains(index))
                    mScreenTypePositionList.add(index);
                index++;
                int size = ledScreen.getProgramList().size();
                index+=size;
                itemCount+=size;//再加节目
            }
            isDataSetChanged=false;
        }
        return itemCount;
    }

    public void updateDataSet(){
        isDataSetChanged=true;
    }


    @Override
    public int getItemViewType(int position) {
        boolean isScreenType = mScreenTypePositionList.contains(position);
        if (isScreenType) {
            return SCREEN_TYPE;
        }else {
            return PROGRAM_TYPE;
        }

    }

    private class ScreenViewHolder extends RecyclerView.ViewHolder{
        ImageView icon;
        TextView screenNameView;
        TextView sizeView;
        TextView cardView;
        TextView locationView;
        ImageView progmIcon;
        TextView textView;
        LinearLayout screenParent;
        LinearLayout programParent;
        LinearLayout viewParent;
        public ScreenViewHolder(View itemView) {
            super(itemView);
            screenParent= (LinearLayout) itemView.findViewById(R.id.ll_contentScreen_screen);
            programParent= (LinearLayout) itemView.findViewById(R.id.ll_contentScreen_program);
            icon= (ImageView) itemView.findViewById(R.id.iv_screenCatag_screen);
            screenNameView = (TextView) itemView.findViewById(R.id.tv_screenCatag_name);
            locationView = (TextView) itemView.findViewById(R.id.tv_screenCatag_location);
            sizeView = (TextView) itemView.findViewById(R.id.tv_screenCatag_size);
            cardView = (TextView) itemView.findViewById(R.id.tv_screenCatag_card);
            progmIcon = (ImageView) itemView.findViewById(R.id.iv_screenProgm_program);
            textView = (TextView) itemView.findViewById(R.id.tv_screenProgm_text);
            viewParent = (LinearLayout) itemView.findViewById(R.id.ll_contentScreen);
        }

    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mOnItemClickListener=listener;
    }

    public interface OnItemClickListener{
        void onClick(View v,int position,boolean isScreenParent,int screenPosition,int programPosition);
    }

    public interface OnItemLongClickListener{
        void onLongClick(View v,int position,boolean isScreenParent,int screenPosition,int programPosition);
    }
    public void setOnItemLongClickListener(OnItemLongClickListener longlistener){
        mOnItemLongClickListener=longlistener;
    }
}
