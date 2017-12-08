package com.example.zz.zhihu;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class LikeColumnAdapter extends RecyclerView.Adapter<LikeColumnAdapter.ViewHolder>{

    private List<LikeColumn> mLikeColumnList;
    private Context mContext;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View like_columnView;
        CardView cardView;
        ImageView like_columnImage;
        TextView like_columnName;
        TextView like_columnDescription;

        public ViewHolder(View view) {
            super(view);
            like_columnView=view;
            cardView = (CardView) view;
            like_columnImage = view.findViewById(R.id.like_column_image);
            like_columnName = view.findViewById(R.id.like_column_name);
            like_columnDescription=view.findViewById(R.id.like_column_description);
        }
    }

    public LikeColumnAdapter(List<LikeColumn> like_columnList) {
        mLikeColumnList = like_columnList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.like_column_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final LikeColumn like_column = mLikeColumnList.get(position);
        holder.like_columnName.setText(like_column.getName());
        holder.like_columnDescription.setText(like_column.getDescription());
        Glide.with(mContext).load(like_column.getThumbnail()).asBitmap().placeholder(R.drawable.zhihu).error(R.drawable.zhihu).into(holder.like_columnImage);
    }

    @Override
    public int getItemCount() {
        return mLikeColumnList.size();
    }

}