package cn.com.hotled.xyled.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.bean.SocketMessage;

/**
 * Created by Lam on 2016/12/20.
 */

public class MessageAdapter extends RecyclerView.Adapter {
    private static final int IS_SERVER = 0x12;
    private static final int IS_CLIENT = 0x24;
    Context mContext;
    List<SocketMessage> mMessageList;

    public MessageAdapter(Context context, List<SocketMessage> messageList) {
        mContext = context;
        mMessageList = messageList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View messageView = inflater.from(mContext).inflate(R.layout.content_message,parent,false);
        viewHolder = new MessageViewHolder(messageView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int itemViewType = getItemViewType(position);
        MessageViewHolder viewHolder= (MessageViewHolder) holder;
        if (itemViewType==IS_CLIENT){
            viewHolder.serverTextView.setVisibility(View.GONE);
            viewHolder.fromServerTv.setVisibility(View.GONE);
            viewHolder.clientTextView.setText(mMessageList.get(position).getMessageText());
            viewHolder.fromMeTv.setText(R.string.from_phone);
        }else {
            viewHolder.fromServerTv.setText(R.string.from_card);
            viewHolder.clientTextView.setVisibility(View.GONE);
            viewHolder.fromMeTv.setVisibility(View.GONE);
            viewHolder.serverTextView.setText(mMessageList.get(position).getMessageText());
        }
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mMessageList.get(position).isServer()) {
            return IS_SERVER;
        }else {
            return IS_CLIENT;
        }
    }

    class MessageViewHolder extends RecyclerView.ViewHolder{
        TextView clientTextView;
        TextView serverTextView;
        TextView fromServerTv;
        TextView fromMeTv;
        public MessageViewHolder(View itemView) {
            super(itemView);
            fromServerTv = (TextView) itemView.findViewById(R.id.tv_message_fromserver);
            fromMeTv=(TextView)itemView.findViewById(R.id.tv_message_fromme);
            clientTextView = (TextView) itemView.findViewById(R.id.tv_client);
            serverTextView  = (TextView) itemView.findViewById(R.id.tv_server);
        }
    }
}
