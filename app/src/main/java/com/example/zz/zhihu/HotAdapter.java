package com.example.zz.zhihu;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class HotAdapter extends RecyclerView.Adapter<HotAdapter.ViewHolder>{

    private List<Hot> mHotList;
    private Context mContext;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View hotView;
        ImageView hotImage;
        TextView hotTitle;

        public ViewHolder(View view) {
            super(view);
            hotView = view;
            hotImage = view.findViewById(R.id.hot_image);
            hotTitle = view.findViewById(R.id.hot_title);
        }
    }

    public HotAdapter(List<Hot> hotList) {
        mHotList = hotList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.hot_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Hot hot = mHotList.get(position);
        holder.hotTitle.setText(hot.getTitle());
        Glide.with(mContext).load(hot.getThumbnail()).asBitmap().placeholder(R.drawable.zhihu).error(R.drawable.zhihu).into(holder.hotImage);
    }
    @Override
    public int getItemCount() {
        return mHotList.size();
    }
}