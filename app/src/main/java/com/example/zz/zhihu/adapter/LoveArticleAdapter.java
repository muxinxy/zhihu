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
import com.example.zz.zhihu.item.Hot;
import com.example.zz.zhihu.item.LikeArticle;
import com.example.zz.zhihu.MyDatabaseHelper;
import com.example.zz.zhihu.R;
import com.example.zz.zhihu.activity.ArticleActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class LoveArticleAdapter extends RecyclerView.Adapter<LoveArticleAdapter.ViewHolder>{

    private List<LikeArticle> mLoveArticleList;
    private Context mContext;
    private MyDatabaseHelper dbHelper;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View love_articleView;
        ImageView love_articleImage;
        TextView love_articleTitle;
        ImageView love_article_love;

        ViewHolder(View view) {
            super(view);
            love_articleView = view;
            love_articleImage = view.findViewById(R.id.love_article_image);
            love_articleTitle = view.findViewById(R.id.love_article_title);
            love_article_love=view.findViewById(R.id.love_article_love);
        }
    }

    public LoveArticleAdapter(List<LikeArticle> love_articleList) {
        mLoveArticleList = love_articleList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.love_article_item, parent, false);
        final LoveArticleAdapter.ViewHolder holder=new LoveArticleAdapter.ViewHolder(view);
        holder.love_articleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=holder.getAdapterPosition();
                LikeArticle loveArticle = mLoveArticleList.get(position);
                Intent intent=new Intent(mContext,ArticleActivity.class);
                intent.putExtra("NewsId_intent",loveArticle.getNews_id());
                mContext.startActivity(intent);
            }
        });
        holder.love_article_love.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onClick(View v) {
                boolean LoveArticle=false;
                int position=holder.getAdapterPosition();
                LikeArticle loveArticle= mLoveArticleList.get(position);
                dbHelper =new MyDatabaseHelper(mContext,"data.db",null,1) ;
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                Cursor cursor=db.query("love_article_table",null,null,null,null,null,null);
                if (cursor.moveToFirst()) {
                    do {
                        String username=cursor.getString(cursor.getColumnIndex("username"));
                        String news_id=cursor.getString(cursor.getColumnIndex("news_id"));
                        if (username.equals(loveArticle.getUsername())&&news_id.equals(loveArticle.getNews_id())){
                            LoveArticle=true;
                            break;
                        }
                    }while (cursor.moveToNext());
                }
                cursor.close();
                if (!LoveArticle){
                    ContentValues values = new ContentValues();
                    values.put("news_id",loveArticle.getNews_id());
                    values.put("username",loveArticle.getUsername());
                    values.put("title",loveArticle.getTitle());
                    values.put("thumbnail",loveArticle.getThumbnail());
                    db.insert("love_article_table", null, values);
                    db.close();
                    values.clear();
                    sendRequestWithHttpURLConnection(loveArticle.getNews_id());
                    Toast.makeText(mContext,"已喜爱",Toast.LENGTH_SHORT).show();
                    Glide.with(mContext).load(R.drawable.love1).asBitmap().into(holder.love_article_love);
                }else {
                    db.delete("love_article_table","news_id=?",new String[]{loveArticle.getNews_id()});
                    db.close();
                    Toast.makeText(mContext,"已取消喜爱",Toast.LENGTH_SHORT).show();
                    Glide.with(mContext).load(R.drawable.love0).asBitmap().into(holder.love_article_love);
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final LikeArticle love_article = mLoveArticleList.get(position);
        holder.love_articleTitle.setText(love_article.getTitle());
        Glide.with(mContext).load(love_article.getThumbnail()).asBitmap().placeholder(R.drawable.zhihu).error(R.drawable.zhihu).into(holder.love_articleImage);
        dbHelper =new MyDatabaseHelper(mContext,"data.db",null,1) ;
        SQLiteDatabase sdb = dbHelper.getWritableDatabase();
        Cursor cursor1=sdb.query("love_article_table",null,null,null,null,null,null);
        if (cursor1.moveToFirst()) {
            do {
                String username=cursor1.getString(cursor1.getColumnIndex("username"));
                String article_id=cursor1.getString(cursor1.getColumnIndex("news_id"));
                if (username.equals(love_article.getUsername())&&article_id.equals(love_article.getNews_id())){
                    Glide.with(mContext).load(R.drawable.love1).asBitmap().into(holder.love_article_love);
                    break;
                }else {
                    Glide.with(mContext).load(R.drawable.love0).asBitmap().into(holder.love_article_love);
                }
            }while (cursor1.moveToNext());
        }
        cursor1.close();
        sdb.close();
    }
    @Override
    public int getItemCount() {
        return mLoveArticleList.size();
    }
    private void sendRequestWithHttpURLConnection(final String id) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL("http://news-at.zhihu.com/api/2/news/" + id);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    parseJSONWithJSONObject(response.toString(),id);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    private void parseJSONWithJSONObject(String s,String id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("json",s);
        db.update("love_article_table",values,"news_id=",new String[]{id});
    }
}