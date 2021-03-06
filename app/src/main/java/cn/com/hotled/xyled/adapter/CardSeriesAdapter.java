package cn.com.hotled.xyled.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.bean.LedCard;

/**
 * Created by Lam on 2016/11/9.
 */

public class CardSeriesAdapter extends RecyclerView.Adapter {
    Context mContext;
    List<LedCard> cardNameList;
    private OnItemOnClickListener onItemOnClickListener;
    private int oldPosition=-1;

    public interface OnItemOnClickListener{
        void onItemClick(View view, int position);
    }

    public CardSeriesAdapter(List<LedCard> cardNameList, Context context) {
        this.mContext = context;
        this.cardNameList = cardNameList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View inflate = inflater.from(mContext).inflate(R.layout.content_card_series,parent,false);
        CardViewHolder typefaceViewHolder = new CardViewHolder(inflate);
        return typefaceViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final CardViewHolder viewHolder= (CardViewHolder) holder;
        viewHolder.textView.setText(cardNameList.get(position).getCardName());

        if (cardNameList.get(position).isSelected()){
            viewHolder.ivCheck.setVisibility(View.VISIBLE);
            viewHolder.textView.setTextColor(Color.parseColor("#FF4081"));
        }else {
            viewHolder.ivCheck.setVisibility(View.GONE);
            viewHolder.textView.setTextColor(Color.parseColor("#212121"));
        }
        if (onItemOnClickListener!=null){
            viewHolder.linearlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //拿到被点击的位置
                    int layoutPosition = viewHolder.getLayoutPosition();
                    if (oldPosition!=-1){
                        //非第一次点击
                        cardNameList.get(oldPosition).setSelected(false);
                        cardNameList.get(layoutPosition).setSelected(true);
                        notifyItemChanged(layoutPosition);
                        if (layoutPosition!=oldPosition)
                            notifyItemChanged(oldPosition);
                    }else {
                        //第一次点击
                        oldPosition =layoutPosition;
                        boolean selected = cardNameList.get(layoutPosition).isSelected();
                        cardNameList.get(layoutPosition).setSelected(!selected);//取反操作
                        notifyItemChanged(layoutPosition);
                    }
                    oldPosition =layoutPosition;
                    onItemOnClickListener.onItemClick(v,layoutPosition);
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return cardNameList.size();
    }

    public void setOnItemClickListener(OnItemOnClickListener itemOnClickListener){
        this.onItemOnClickListener=itemOnClickListener;
    }

    class CardViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCardPic;
        ImageView ivCheck;
        TextView textView;
        LinearLayout linearlayout;
        public CardViewHolder(View itemView) {
            super(itemView);
            linearlayout = (LinearLayout) itemView.findViewById(R.id.ll_card);
            textView= (TextView) itemView.findViewById(R.id.tv_card_cardName);
            ivCardPic = (ImageView) itemView.findViewById(R.id.iv_card_pic);
            ivCheck= (ImageView) itemView.findViewById(R.id.iv_card_check);
        }
    }
}
