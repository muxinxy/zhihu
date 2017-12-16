package com.example.zz.zhihu.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.zz.zhihu.item.LikeArticle;
import com.example.zz.zhihu.MyDatabaseHelper;
import com.example.zz.zhihu.R;
import com.example.zz.zhihu.activity.ArticleActivity;

import java.util.List;

public class LikeArticleAdapter extends RecyclerView.Adapter<LikeArticleAdapter.ViewHolder>{

    private List<LikeArticle> mLikeArticleList;
    private Context mContext;
    private MyDatabaseHelper dbHelper;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View like_articleView;
        ImageView like_articleImage;
        TextView like_articleTitle;
        ImageView like_article;


        ViewHolder(View view) {
            super(view);
            like_articleView = view;
            like_articleImage = view.findViewById(R.id.like_article_image);
            like_articleTitle = view.findViewById(R.id.like_article_title);
            like_article=view.findViewById(R.id.like_article);

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
        final LikeArticleAdapter.ViewHolder holder=new LikeArticleAdapter.ViewHolder(view);
        holder.like_articleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=holder.getAdapterPosition();
                LikeArticle likeArticle = mLikeArticleList.get(position);
                Intent intent=new Intent(mContext,ArticleActivity.class);
                intent.putExtra("NewsId_intent",likeArticle.getNews_id());
                mContext.startActivity(intent);
            }
        });
        holder.like_article.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onClick(View v) {
                boolean LikeColumn=false;
                int position=holder.getAdapterPosition();
                LikeArticle likeArticle = mLikeArticleList.get(position);
                dbHelper =new MyDatabaseHelper(mContext,"data.db",null,1) ;
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                Cursor cursor=db.query("like_article_table",null,null,null,null,null,null);
                if (cursor.moveToFirst()) {
                    do {
                        String username=cursor.getString(cursor.getColumnIndex("username"));
                        String column_id=cursor.getString(cursor.getColumnIndex("news_id"));
                        if (username.equals(likeArticle.getUsername())&&column_id.equals(likeArticle.getNews_id())){
                            LikeColumn=true;
                            break;
                        }
                    }while (cursor.moveToNext());
                }
                cursor.close();
                if (!LikeColumn){
                    ContentValues values = new ContentValues();
                    values.put("news_id",likeArticle.getNews_id());
                    values.put("username",likeArticle.getUsername());
                    values.put("title",likeArticle.getTitle());
                    values.put("url",likeArticle.getUrl());
                    values.put("thumbnail",likeArticle.getThumbnail());
                    db.insert("like_article_table", null, values);
                    values.clear();
                    Toast.makeText(mContext,"已收藏",Toast.LENGTH_SHORT).show();
                    Glide.with(mContext).load(R.drawable.collect1).asBitmap().into(holder.like_article);
                }else {
                    db.delete("like_article_table","news_id=?",new String[]{likeArticle.getNews_id()});
                    db.close();
                    Toast.makeText(mContext,"已取消收藏",Toast.LENGTH_SHORT).show();
                    Glide.with(mContext).load(R.drawable.collection).asBitmap().into(holder.like_article);
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final LikeArticle like_article = mLikeArticleList.get(position);
        holder.like_articleTitle.setText(like_article.getTitle());
        Glide.with(mContext).load(like_article.getThumbnail()).asBitmap().placeholder(R.drawable.zhihu).error(R.drawable.zhihu).into(holder.like_articleImage);
        dbHelper =new MyDatabaseHelper(mContext,"data.db",null,1) ;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor=db.query("like_article_table",null,null,null,null,null,null);
        if (cursor.moveToFirst()) {
            do {
                String username=cursor.getString(cursor.getColumnIndex("username"));
                String column_id=cursor.getString(cursor.getColumnIndex("news_id"));
                if (username.equals(like_article.getUsername())&&column_id.equals(like_article.getNews_id())){
                    Glide.with(mContext).load(R.drawable.collect1).asBitmap().into(holder.like_article);
                    break;
                }else {
                    Glide.with(mContext).load(R.drawable.collection).asBitmap().into(holder.like_article);
                }
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
    }
    @Override
    public int getItemCount() {
        return mLikeArticleList.size();
    }
}