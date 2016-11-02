package cn.com.hotled.xyled.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import java.util.List;

import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.bean.TextButton;

/**
 * Created by Lam on 2016/10/31.
 */

public class TextButtonAdapter extends RecyclerView.Adapter{
    private Context mContext;
    private List<TextButton> mTextButtonList;
    private OnItemOnClickListener onItemOnClickListener;
    private FrameLayout oldFramelayout;
    private int oldPostion;


    public TextButtonAdapter(Context context, List<TextButton> textButtons) {
        mContext=context;
        mTextButtonList=textButtons;
    }
    public interface OnItemOnClickListener{
        void onItemClick(View view , int position);
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(mContext, R.layout.content_button, null);
        TextButtonViewHolder viewHolder=new TextButtonViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //强制关闭复用
        holder.setIsRecyclable(true);
        final TextButtonViewHolder tbViewHolder= (TextButtonViewHolder) holder;
        TextButton textButton = mTextButtonList.get(position);
        //// TODO: 2016/11/1 textbutton中的button与viewholder的button是否一致？
        tbViewHolder.button.setAllCaps(false);                          //设置区分大小写
        tbViewHolder.button.setText(textButton.getText());              //设置文本
        tbViewHolder.button.setTextColor(textButton.getTextColor());    //设置字体颜色
        tbViewHolder.button.setBackgroundColor(textButton.getTextBackgroudColor());//设置字体背景颜色
        if (textButton.isbold())
            tbViewHolder.button.setTypeface(Typeface.DEFAULT,Typeface.BOLD);//设置粗体
        if (textButton.isIlatic())
            tbViewHolder.button.setTypeface(Typeface.DEFAULT,Typeface.ITALIC);//设置斜体
        if (textButton.isUnderline())
            tbViewHolder.button.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);//设置下划线
        if (textButton.isSelected()){
            //选中状态
            tbViewHolder.frameLayout.setBackgroundResource(R.drawable.textbutton_selected);
        }else{
            //未选中状态
        }
        if (onItemOnClickListener!=null){
            tbViewHolder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = tbViewHolder.getLayoutPosition();
                    if (oldFramelayout!=null){
                        oldFramelayout.setBackgroundResource(R.drawable.textbutton_unselected);
                    }
                    onItemOnClickListener.onItemClick(v,pos);
                    tbViewHolder.frameLayout.setBackgroundResource(R.drawable.textbutton_selected);
                    //重新设置oldFramelayout
                    oldFramelayout=tbViewHolder.frameLayout;
                    oldPostion=pos;
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return mTextButtonList.size();
    }
    public void setItemOnClickListener(OnItemOnClickListener itemOnClickListener){
        this.onItemOnClickListener=itemOnClickListener;
    }
    class TextButtonViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        FrameLayout frameLayout;
        Button button;
        public TextButtonViewHolder(View itemView) {
            super(itemView);
            button= (Button) itemView.findViewById(R.id.content_button);
            frameLayout = (FrameLayout) itemView.findViewById(R.id.fl_contentButton);
            button.setOnClickListener(TextButtonViewHolder.this);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
