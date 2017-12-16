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
import com.example.zz.zhihu.MyDatabaseHelper;
import com.example.zz.zhihu.R;
import com.example.zz.zhihu.activity.ArticleActivity;
import com.example.zz.zhihu.item.Messages;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class HotAdapter extends RecyclerView.Adapter<HotAdapter.ViewHolder>{

    private List<Hot> mHotList;
    private Context mContext;
    private MyDatabaseHelper dbHelper;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View hotView;
        ImageView hotImage;
        TextView hotTitle;
        ImageView like_article;
        ImageView love_article;

        ViewHolder(View view) {
            super(view);
            hotView = view;
            hotImage = view.findViewById(R.id.hot_image);
            hotTitle = view.findViewById(R.id.hot_title);
            like_article=view.findViewById(R.id.like_article);
            love_article=view.findViewById(R.id.love_article);
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
        final HotAdapter.ViewHolder holder=new HotAdapter.ViewHolder(view);
        holder.hotView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=holder.getAdapterPosition();
                Hot hot = mHotList.get(position);
                Intent intent=new Intent(mContext,ArticleActivity.class);
                intent.putExtra("Title_intent",hot.getTitle());
                intent.putExtra("username_intent",hot.getUsername());
                intent.putExtra("NewsId_intent",hot.getNews_id());
                intent.putExtra("Url_intent",hot.getUrl());
                intent.putExtra("Thumbnail_intent",hot.getThumbnail());
                intent.putExtra("intent_intent","hot");
                mContext.startActivity(intent);
            }
        });
        holder.like_article.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onClick(View v) {
                boolean LikeColumn=false;
                int position=holder.getAdapterPosition();
                Hot hot= mHotList.get(position);
                dbHelper =new MyDatabaseHelper(mContext,"data.db",null,1) ;
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                Cursor cursor=db.query("like_article_table",null,null,null,null,null,null);
                if (cursor.moveToFirst()) {
                    do {
                        String username=cursor.getString(cursor.getColumnIndex("username"));
                        String column_id=cursor.getString(cursor.getColumnIndex("news_id"));
                        if (username.equals(hot.getUsername())&&column_id.equals(hot.getNews_id())){
                            LikeColumn=true;
                            break;
                        }
                    }while (cursor.moveToNext());
                }
                cursor.close();
                if (!LikeColumn){
                    ContentValues values = new ContentValues();
                    values.put("news_id",hot.getNews_id());
                    values.put("username",hot.getUsername());
                    values.put("title",hot.getTitle());
                    values.put("url",hot.getUrl());
                    values.put("thumbnail",hot.getThumbnail());
                    db.insert("like_article_table", null, values);
                    db.close();
                    values.clear();
                    Toast.makeText(mContext,"已收藏",Toast.LENGTH_SHORT).show();
                    Glide.with(mContext).load(R.drawable.collect1).asBitmap().into(holder.like_article);
                }else {
                    db.delete("like_article_table","news_id=?",new String[]{hot.getNews_id()});
                    db.close();
                    Toast.makeText(mContext,"已取消收藏",Toast.LENGTH_SHORT).show();
                    Glide.with(mContext).load(R.drawable.collection).asBitmap().into(holder.like_article);
                }
            }
        });

        holder.love_article.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onClick(View v) {
                boolean LoveArticle=false;
                int position=holder.getAdapterPosition();
                Hot hot= mHotList.get(position);
                dbHelper =new MyDatabaseHelper(mContext,"data.db",null,1) ;
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                Cursor cursor=db.query("love_article_table",null,null,null,null,null,null);
                if (cursor.moveToFirst()) {
                    do {
                        String username=cursor.getString(cursor.getColumnIndex("username"));
                        String news_id=cursor.getString(cursor.getColumnIndex("news_id"));
                        if (username.equals(hot.getUsername())&&news_id.equals(hot.getNews_id())){
                            LoveArticle=true;
                            break;
                        }
                    }while (cursor.moveToNext());
                }
                cursor.close();
                if (!LoveArticle){
                    ContentValues values = new ContentValues();
                    values.put("news_id",hot.getNews_id());
                    values.put("username",hot.getUsername());
                    values.put("title",hot.getTitle());
                    values.put("thumbnail",hot.getThumbnail());
                    db.insert("love_article_table", null, values);
                    db.close();
                    values.clear();
                    sendRequestWithHttpURLConnection(hot.getNews_id());
                    Toast.makeText(mContext,"已喜爱",Toast.LENGTH_SHORT).show();
                    Glide.with(mContext).load(R.drawable.love1).asBitmap().into(holder.love_article);
                }else {
                    db.delete("love_article_table","news_id=?",new String[]{hot.getNews_id()});
                    db.close();
                    Toast.makeText(mContext,"已取消喜爱",Toast.LENGTH_SHORT).show();
                    Glide.with(mContext).load(R.drawable.love0).asBitmap().into(holder.love_article);
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Hot hot = mHotList.get(position);
        holder.hotTitle.setText(hot.getTitle());
        Glide.with(mContext).load(hot.getThumbnail()).asBitmap().placeholder(R.drawable.zhihu).error(R.drawable.zhihu).into(holder.hotImage);
        dbHelper =new MyDatabaseHelper(mContext,"data.db",null,1) ;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor=db.query("like_article_table",null,null,null,null,null,null);
        if (cursor.moveToFirst()) {
            do {
                String username=cursor.getString(cursor.getColumnIndex("username"));
                String column_id=cursor.getString(cursor.getColumnIndex("news_id"));
                if (username.equals(hot.getUsername())&&column_id.equals(hot.getNews_id())){
                    Glide.with(mContext).load(R.drawable.collect1).asBitmap().into(holder.like_article);
                    break;
                }else {
                    Glide.with(mContext).load(R.drawable.collection).asBitmap().into(holder.like_article);
                }
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        SQLiteDatabase sdb = dbHelper.getWritableDatabase();
        Cursor cursor1=sdb.query("love_article_table",null,null,null,null,null,null);
        if (cursor1.moveToFirst()) {
            do {
                String username=cursor1.getString(cursor1.getColumnIndex("username"));
                String article_id=cursor1.getString(cursor1.getColumnIndex("news_id"));
                if (username.equals(hot.getUsername())&&article_id.equals(hot.getNews_id())){
                    Glide.with(mContext).load(R.drawable.love1).asBitmap().into(holder.love_article);
                    break;
                }else {
                    Glide.with(mContext).load(R.drawable.love0).asBitmap().into(holder.love_article);
                }
            }while (cursor1.moveToNext());
        }
        cursor1.close();
        sdb.close();
    }
    @Override
    public int getItemCount() {
        return mHotList.size();
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