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
import com.example.zz.zhihu.item.Messages;
import com.example.zz.zhihu.MyDatabaseHelper;
import com.example.zz.zhihu.R;
import com.example.zz.zhihu.activity.ArticleActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{

    private List<Messages> mMessageList;
    private Context mContext;
    private MyDatabaseHelper dbHelper;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View messageView;
        ImageView messageImage;
        TextView messageTitle;
        TextView messageDate;
        ImageView like_article;
        TextView LongCommitsNum;
        TextView ShortCommitsNum;

        ViewHolder(View view) {
            super(view);
            messageView = view;
            messageImage = view.findViewById(R.id.message_image);
            messageTitle = view.findViewById(R.id.message_title);
            messageDate=view.findViewById(R.id.message_date);
            like_article=view.findViewById(R.id.like_article);
            //LongCommitsNum=view.findViewById(R.id.LongCommitsNum);
            //ShortCommitsNum=view.findViewById(R.id.ShortCommitsNum);
        }
    }

    public MessageAdapter(List<Messages> messageList) {
        mMessageList = messageList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.message_item, parent, false);
        final MessageAdapter.ViewHolder holder=new MessageAdapter.ViewHolder(view);
        holder.messageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=holder.getAdapterPosition();
                Messages message = mMessageList.get(position);
                Intent intent=new Intent(mContext,ArticleActivity.class);
                intent.putExtra("Title_intent",message.getTitle());
                intent.putExtra("username_intent",message.getUsername());
                intent.putExtra("NewsId_intent",message.getId());
                intent.putExtra("Thumbnail_intent",message.getImages());
                intent.putExtra("intent_intent","message");
                mContext.startActivity(intent);
            }
        });
        holder.like_article.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onClick(View v) {
                boolean LikeColumn=false;
                int position=holder.getAdapterPosition();
                Messages message= mMessageList.get(position);
                dbHelper =new MyDatabaseHelper(mContext,"data.db",null,1) ;
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                Cursor cursor=db.query("like_article_table",null,null,null,null,null,null);
                if (cursor.moveToFirst()) {
                    do {
                        String username=cursor.getString(cursor.getColumnIndex("username"));
                        String news_id=cursor.getString(cursor.getColumnIndex("news_id"));
                        if (username.equals(message.getUsername())&&news_id.equals(message.getId())){
                            LikeColumn=true;
                            break;
                        }
                    }while (cursor.moveToNext());
                }
                cursor.close();
                if (!LikeColumn){
                    ContentValues values = new ContentValues();
                    values.put("news_id",message.getId());
                    values.put("username",message.getUsername());
                    values.put("title",message.getTitle());
                    values.put("thumbnail",message.getImages());
                    db.insert("like_article_table", null, values);
                    db.close();
                    values.clear();
                    Toast.makeText(mContext,"已收藏",Toast.LENGTH_SHORT).show();
                    Glide.with(mContext).load(R.drawable.collect1).asBitmap().into(holder.like_article);
                }else {
                    db.delete("like_article_table","news_id=?",new String[]{message.getId()});
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
        final Messages message = mMessageList.get(position);
        holder.messageTitle.setText(message.getTitle());
        holder.messageDate.setText(message.getDisplay_date());
        Glide.with(mContext).load(message.getImages()).asBitmap().placeholder(R.drawable.zhihu).error(R.drawable.zhihu).into(holder.messageImage);
        dbHelper =new MyDatabaseHelper(mContext,"data.db",null,1) ;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor=db.query("like_article_table",null,null,null,null,null,null);
        if (cursor.moveToFirst()) {
            do {
                String username=cursor.getString(cursor.getColumnIndex("username"));
                String article_id=cursor.getString(cursor.getColumnIndex("news_id"));
                if (username.equals(message.getUsername())&&article_id.equals(message.getId())){
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
        return mMessageList.size();
    }
}