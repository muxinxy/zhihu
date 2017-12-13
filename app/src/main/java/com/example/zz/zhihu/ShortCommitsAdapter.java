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

public class ShortCommitsAdapter extends RecyclerView.Adapter<ShortCommitsAdapter.ViewHolder>{

    private List<ShortCommits> mShortCommitsList;
    private Context mContext;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View short_commitsView;
        ImageView short_commitsImage;
        TextView short_commitsAuthor;
        TextView reply_short_commitsAuthor;
        TextView short_commitsTime;
        TextView short_commitsLikes;
        TextView short_commitsContent;
        TextView reply_Short_commitsContent;

        ViewHolder(View view) {
            super(view);
            short_commitsView = view;
            short_commitsImage = view.findViewById(R.id.avatar);
            short_commitsTime = view.findViewById(R.id.time);
            short_commitsAuthor=view.findViewById(R.id.author);
            short_commitsContent=view.findViewById(R.id.content);
            short_commitsLikes=view.findViewById(R.id.likes);
            reply_short_commitsAuthor=view.findViewById(R.id.reply_author);
            reply_Short_commitsContent=view.findViewById(R.id.reply_content);
        }
    }

    ShortCommitsAdapter(List<ShortCommits> short_commitsList) {
        mShortCommitsList = short_commitsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.short_commits_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ShortCommits short_commits = mShortCommitsList.get(position);
        holder.short_commitsAuthor.setText(short_commits.getAuthor());
        holder.reply_short_commitsAuthor.setText(short_commits.getReply_author());
        holder.short_commitsLikes.setText(short_commits.getLikes());
        holder.short_commitsTime.setText(short_commits.getTime());
        holder.short_commitsContent.setText(short_commits.getContent());
        holder.reply_Short_commitsContent.setText(short_commits.getReply_content());
        Glide.with(mContext).load(short_commits.getAvatar()).asBitmap().placeholder(R.drawable.zhihu).error(R.drawable.zhihu).into(holder.short_commitsImage);
    }
    @Override
    public int getItemCount() {
        return mShortCommitsList.size();
    }
}