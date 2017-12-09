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

public class LongCommitsAdapter extends RecyclerView.Adapter<LongCommitsAdapter.ViewHolder>{

    private List<LongCommits> mLongCommitsList;
    private Context mContext;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View long_commitsView;
        ImageView long_commitsImage;
        TextView long_commitsAuthor;
        TextView reply_long_commitsAuthor;
        TextView long_commitsTime;
        TextView long_commitsLikes;
        TextView long_commitsContent;
        TextView reply_Long_commitsContent;

        public ViewHolder(View view) {
            super(view);
            long_commitsView = view;
            long_commitsImage = view.findViewById(R.id.avatar);
            long_commitsTime = view.findViewById(R.id.time);
            long_commitsAuthor=view.findViewById(R.id.author);
            long_commitsContent=view.findViewById(R.id.content);
            long_commitsLikes=view.findViewById(R.id.likes);
            reply_long_commitsAuthor=view.findViewById(R.id.reply_author);
            reply_Long_commitsContent=view.findViewById(R.id.reply_content);
        }
    }

    public LongCommitsAdapter(List<LongCommits> long_commitsList) {
        mLongCommitsList = long_commitsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.long_commits_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final LongCommits long_commits = mLongCommitsList.get(position);
        holder.long_commitsAuthor.setText(long_commits.getAuthor());
        holder.reply_long_commitsAuthor.setText(long_commits.getReply_author());
        holder.long_commitsLikes.setText(long_commits.getLikes());
        holder.long_commitsTime.setText(long_commits.getTime());
        holder.long_commitsContent.setText(long_commits.getContent());
        holder.reply_Long_commitsContent.setText(long_commits.getReply_content());
        Glide.with(mContext).load(long_commits.getAvatar()).asBitmap().placeholder(R.drawable.zhihu).error(R.drawable.zhihu).into(holder.long_commitsImage);
    }
    @Override
    public int getItemCount() {
        return mLongCommitsList.size();
    }
}