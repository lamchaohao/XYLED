package cn.com.hotled.xyled.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.com.hotled.xyled.R;

/**
 * Created by Lam on 2016/11/9.
 */

public class LanguageAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private List<String> languages;
    private OnItemOnClickListener onItemOnClickListener;
    private boolean[] isChecks;
    private int oldPos = 0;

    public interface OnItemOnClickListener{
        void onItemClick(View view, int position);
    }

    public LanguageAdapter(List<String> languages, Context context,int checkPosition) {
        this.mContext = context;
        this.languages = languages;
        isChecks = new boolean[languages.size()];
        isChecks[checkPosition]=true;
        oldPos=checkPosition;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View inflate = inflater.from(mContext).inflate(R.layout.content_language,parent,false);
        LanguageHolder viewHolder = new LanguageHolder(inflate);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final LanguageHolder viewHolder= (LanguageHolder) holder;
        String lang = languages.get(position);
        viewHolder.textView.setText(lang);
        if (isChecks[position]) {
            viewHolder.checkView.setVisibility(View.VISIBLE);
        }else {
            viewHolder.checkView.setVisibility(View.INVISIBLE);
        }

        if (onItemOnClickListener!=null){
            viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //拿到被点击的位置
                    int layoutPosition = viewHolder.getLayoutPosition();
                    for (int i = 0; i < isChecks.length; i++) {
                        if (i==layoutPosition) {
                            isChecks[i]=true;
                        }else {
                            isChecks[i]=false;
                        }
                    }
                    notifyItemChanged(layoutPosition);
                    notifyItemChanged(oldPos);
                    oldPos=layoutPosition;
                    onItemOnClickListener.onItemClick(v,layoutPosition);
                }
            });
        }

    }


    @Override
    public int getItemCount() {
        return languages.size();
    }

    public void setOnItemClickListener(OnItemOnClickListener itemOnClickListener){
        this.onItemOnClickListener=itemOnClickListener;
    }

    class LanguageHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayout;
        TextView textView;
        ImageView checkView;
        public LanguageHolder(View itemView) {
            super(itemView);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.ll_language);
            textView = (TextView) itemView.findViewById(R.id.tv_language);
            checkView = (ImageView) itemView.findViewById(R.id.iv_language);
        }
    }
}
