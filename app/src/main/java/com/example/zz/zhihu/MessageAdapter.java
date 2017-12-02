package com.example.zz.zhihu;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{

    private List<Message> mMessageList;
    private Context mContext;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View messageView;
        ImageView messageImage;
        TextView messageTitle;
        TextView messageDate;

        public ViewHolder(View view) {
            super(view);
            messageView = view;
            messageImage = view.findViewById(R.id.message_image);
            messageTitle = view.findViewById(R.id.message_title);
            messageDate=view.findViewById(R.id.message_date);
        }
    }

    public MessageAdapter(List<Message> messageList) {
        mMessageList = messageList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.message_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Message message = mMessageList.get(position);
        holder.messageTitle.setText(message.getTitle());
        holder.messageDate.setText(message.getDisplay_date());
        Glide.with(mContext).load(message.getImages()).asBitmap().placeholder(R.drawable.zhihu).error(R.drawable.zhihu).into(holder.messageImage);
    }
    @Override
    public int getItemCount() {
        return mMessageList.size();
    }
}


