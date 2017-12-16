package com.example.zz.zhihu.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.example.zz.zhihu.item.Column;
import com.example.zz.zhihu.MyDatabaseHelper;
import com.example.zz.zhihu.R;
import com.example.zz.zhihu.activity.MessageActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class ColumnAdapter extends RecyclerView.Adapter<ColumnAdapter.ViewHolder>{

    private List<Column> mColumnList;
    private Context mContext;
    private MyDatabaseHelper dbHelper;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View columnView;
        CardView cardView;
        ImageView columnImage;
        TextView columnName;
        TextView columnDescription;
        ImageView like_column;
        ImageView love_column;

        ViewHolder(View view) {
            super(view);
            columnView=view;
            cardView = (CardView) view;
            columnImage = view.findViewById(R.id.column_image);
            columnName = view.findViewById(R.id.column_name);
            columnDescription=view.findViewById(R.id.column_description);
            like_column=view.findViewById(R.id.like_column);
            love_column=view.findViewById(R.id.love_column);
        }
    }
    public ColumnAdapter(List<Column> columnList) {
        mColumnList = columnList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.column_item, parent, false);
        final ViewHolder holder=new ViewHolder(view);
        holder.columnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=holder.getAdapterPosition();
                Column column = mColumnList.get(position);
                Intent intent=new Intent(mContext,MessageActivity.class);
                intent.putExtra("columnId_intent",column.getId());
                intent.putExtra("username_intent",column.getUsername());
                intent.putExtra("columnName_intent",column.getName());
                intent.putExtra("columnDescription_intent",column.getDescription());
                intent.putExtra("columnThumbnail_intent",column.getThumbnail());
                intent.putExtra("intent_intent","main");
                mContext.startActivity(intent);
            }
        });
        holder.like_column.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onClick(View v) {
                boolean LikeColumn=false;
                int position=holder.getAdapterPosition();
                Column column = mColumnList.get(position);
                dbHelper =new MyDatabaseHelper(mContext,"data.db",null,1) ;
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                Cursor cursor=db.query("like_column_table",null,null,null,null,null,null);
                if (cursor.moveToFirst()) {
                    do {
                        String username=cursor.getString(cursor.getColumnIndex("username"));
                        String column_id=cursor.getString(cursor.getColumnIndex("column_id"));
                        if (username.equals(column.getUsername())&&column_id.equals(column.getId())){
                                LikeColumn=true;
                                break;
                        }
                    }while (cursor.moveToNext());
                }
                cursor.close();
                if (!LikeColumn){
                    ContentValues values = new ContentValues();
                    values.put("column_id",column.getId());
                    values.put("username",column.getUsername());
                    values.put("name",column.getName());
                    values.put("description",column.getDescription());
                    values.put("thumbnail",column.getThumbnail());
                    db.insert("like_column_table", null, values);
                    values.clear();
                    Toast.makeText(mContext,"已收藏",Toast.LENGTH_SHORT).show();
                    Glide.with(mContext).load(R.drawable.collect1).asBitmap().into(holder.like_column);
                }else {
                    db.delete("like_column_table","column_id=?",new String[]{column.getId()});
                    db.close();
                    Toast.makeText(mContext,"已取消收藏",Toast.LENGTH_SHORT).show();
                    Glide.with(mContext).load(R.drawable.collection).asBitmap().into(holder.like_column);
                }
            }
        });
        holder.love_column.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onClick(View v) {
                boolean LoveColumn=false;
                int position=holder.getAdapterPosition();
                Column column= mColumnList.get(position);
                dbHelper =new MyDatabaseHelper(mContext,"data.db",null,1) ;
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                Cursor cursor=db.query("love_column_table",null,null,null,null,null,null);
                if (cursor.moveToFirst()) {
                    do {
                        String username=cursor.getString(cursor.getColumnIndex("username"));
                        String news_id=cursor.getString(cursor.getColumnIndex("column_id"));
                        if (username.equals(column.getUsername())&&news_id.equals(column.getId())){
                            LoveColumn=true;
                            break;
                        }
                    }while (cursor.moveToNext());
                }
                cursor.close();
                if (!LoveColumn){
                    ContentValues values = new ContentValues();
                    values.put("column_id",column.getId());
                    values.put("username",column.getUsername());
                    values.put("name",column.getName());
                    values.put("thumbnail",column.getThumbnail());
                    db.insert("love_column_table", null, values);
                    db.close();
                    values.clear();
                    sendRequestWithHttpURLConnection(column.getId());
                    Toast.makeText(mContext,"已喜爱",Toast.LENGTH_SHORT).show();
                    Glide.with(mContext).load(R.drawable.love1).asBitmap().into(holder.love_column);
                }else {
                    db.delete("love_column_table","column_id=?",new String[]{column.getId()});
                    db.close();
                    Toast.makeText(mContext,"已取消喜爱",Toast.LENGTH_SHORT).show();
                    Glide.with(mContext).load(R.drawable.love0).asBitmap().into(holder.love_column);
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Column column = mColumnList.get(position);
        holder.columnName.setText(column.getName());
        holder.columnDescription.setText(column.getDescription());
        Glide.with(mContext).load(column.getThumbnail()).asBitmap().placeholder(R.drawable.zhihu).error(R.drawable.zhihu).into(holder.columnImage);
        dbHelper =new MyDatabaseHelper(mContext,"data.db",null,1) ;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor=db.query("like_column_table",null,null,null,null,null,null);
        if (cursor.moveToFirst()) {
            do {
                String username=cursor.getString(cursor.getColumnIndex("username"));
                String column_id=cursor.getString(cursor.getColumnIndex("column_id"));
                if (username.equals(column.getUsername())&&column_id.equals(column.getId())){
                    Glide.with(mContext).load(R.drawable.collect1).asBitmap().into(holder.like_column);
                    break;
                }else {
                    Glide.with(mContext).load(R.drawable.collection).asBitmap().into(holder.like_column);
                }
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        SQLiteDatabase sdb = dbHelper.getWritableDatabase();
        Cursor cursor1=sdb.query("love_column_table",null,null,null,null,null,null);
        if (cursor1.moveToFirst()) {
            do {
                String username=cursor1.getString(cursor1.getColumnIndex("username"));
                String article_id=cursor1.getString(cursor1.getColumnIndex("column_id"));
                if (username.equals(column.getUsername())&&article_id.equals(column.getId())){
                    Glide.with(mContext).load(R.drawable.love1).asBitmap().into(holder.love_column);
                    break;
                }else {
                    Glide.with(mContext).load(R.drawable.love0).asBitmap().into(holder.love_column);
                }
            }while (cursor1.moveToNext());
        }
        cursor1.close();
        sdb.close();
}
    @Override
    public int getItemCount() {
        return mColumnList.size();
    }

    private void sendRequestWithHttpURLConnection(final String id) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL("http://news-at.zhihu.com/api/3/section/"+ id);
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
        db.update("love_column_table",values,"column_id=",new String[]{id});
    }

}