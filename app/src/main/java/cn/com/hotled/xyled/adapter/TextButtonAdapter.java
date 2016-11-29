package cn.com.hotled.xyled.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
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
    public static  boolean SELECT_MODE = false;
    private Context mContext;
    private List<TextButton> mTextButtonList;
    private OnItemOnClickListener onItemOnClickListener;
    private FrameLayout oldFramelayout;


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
        return new TextButtonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //强制关闭复用
        holder.setIsRecyclable(true);
        final TextButtonViewHolder tbViewHolder = (TextButtonViewHolder) holder;
        TextButton textButton = mTextButtonList.get(position);
        tbViewHolder.button.setAllCaps(false);                          //设置区分大小写
        tbViewHolder.button.setText(textButton.getText());              //设置文本
        tbViewHolder.button.setTextColor(textButton.getTextColor());    //设置字体颜色
        tbViewHolder.button.setBackgroundColor(textButton.getTextBackgroudColor());//设置字体背景颜色
        if (textButton.isbold())
            tbViewHolder.button.setTypeface(Typeface.DEFAULT, Typeface.BOLD);//设置粗体
        if (textButton.isIlatic())
            tbViewHolder.button.setTypeface(Typeface.DEFAULT, Typeface.ITALIC);//设置斜体
        if (textButton.isbold()&&textButton.isIlatic())
            tbViewHolder.button.setTypeface(Typeface.DEFAULT, Typeface.BOLD_ITALIC);//设置粗体斜体
        if (textButton.isUnderline())
            tbViewHolder.button.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);//设置下划线
        if (textButton.getTypeface() != null)
            tbViewHolder.button.setTypeface(Typeface.createFromFile(textButton.getTypeface()));//设置字体
        if (textButton.isSelected()) {
            //选中状态
            tbViewHolder.frameLayout.setBackgroundResource(R.drawable.textbutton_selected);
        } else {
            //未选中状态
            tbViewHolder.frameLayout.setBackgroundResource(R.drawable.textbutton_unselected);
        }
        if (onItemOnClickListener != null) {
            tbViewHolder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = tbViewHolder.getLayoutPosition();
                    onItemOnClickListener.onItemClick(v, pos);//这里设置了是否选择
                    if (SELECT_MODE) {
                        //多选模式
                        if (mTextButtonList.get(pos).isSelected()) {
                            //如果已经选择了
                            tbViewHolder.frameLayout.setBackgroundResource(R.drawable.textbutton_selected);
                        } else {
                            //还未选择
                            tbViewHolder.frameLayout.setBackgroundResource(R.drawable.textbutton_unselected);
                        }
                    } else {
                        //单选模式
                        if (oldFramelayout != null) {
                            oldFramelayout.setBackgroundResource(R.drawable.textbutton_unselected);
                        }
                        tbViewHolder.frameLayout.setBackgroundResource(R.drawable.textbutton_selected);
                        //重新设置oldFramelayout
                        oldFramelayout = tbViewHolder.frameLayout;
                    }

                }
            });
        }

            tbViewHolder.button.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (!SELECT_MODE){
                        Snackbar.make(tbViewHolder.button,"进入多选模式",Snackbar.LENGTH_SHORT).setAction("全选", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                for (int i=0;i<mTextButtonList.size();i++){
                                    mTextButtonList.get(i).setSelected(true);//全选
                                    notifyItemChanged(i);//设置回背景
                                }
                            }
                        }).show();
                        SELECT_MODE=true;
                    }else {
                        Snackbar.make(tbViewHolder.button,"退出多选模式",Snackbar.LENGTH_SHORT).show();
                        for (int i=0;i<mTextButtonList.size();i++){
                            mTextButtonList.get(i).setSelected(false);//全部设置未未选择
                            notifyItemChanged(i);//设置回背景
                        }
                        SELECT_MODE=false;
                    }

                    return true;
                }
            });
        }

    @Override
    public int getItemCount() {
        return mTextButtonList.size();
    }
    public void setItemOnClickListener(OnItemOnClickListener itemOnClickListener){
        this.onItemOnClickListener=itemOnClickListener;
    }
    private class TextButtonViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        FrameLayout frameLayout;
        Button button;
        TextButtonViewHolder(View itemView) {
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
