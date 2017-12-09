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

public class LikeArticleAdapter extends RecyclerView.Adapter<LikeArticleAdapter.ViewHolder>{

    private List<LikeArticle> mLikeArticleList;
    private Context mContext;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View like_articleView;
        ImageView like_articleImage;
        TextView like_articleTitle;

        public ViewHolder(View view) {
            super(view);
            like_articleView = view;
            like_articleImage = view.findViewById(R.id.like_article_image);
            like_articleTitle = view.findViewById(R.id.like_article_title);
        }
    }

    public LikeArticleAdapter(List<LikeArticle> like_articleList) {
        mLikeArticleList = like_articleList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.like_article_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final LikeArticle like_article = mLikeArticleList.get(position);
        holder.like_articleTitle.setText(like_article.getTitle());
        Glide.with(mContext).load(like_article.getThumbnail()).asBitmap().placeholder(R.drawable.zhihu).error(R.drawable.zhihu).into(holder.like_articleImage);
    }
    @Override
    public int getItemCount() {
        return mLikeArticleList.size();
    }
}