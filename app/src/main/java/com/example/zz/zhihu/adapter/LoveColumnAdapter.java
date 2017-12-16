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
import com.example.zz.zhihu.item.LikeColumn;
import com.example.zz.zhihu.MyDatabaseHelper;
import com.example.zz.zhihu.R;
import com.example.zz.zhihu.activity.MessageActivity;

import java.util.List;

public class LoveColumnAdapter extends RecyclerView.Adapter<LoveColumnAdapter.ViewHolder>{

    private List<com.example.zz.zhihu.item.LikeColumn> mLoveColumnList;
    private Context mContext;
    private boolean LoveColumn=false;
    private MyDatabaseHelper dbHelper;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View love_columnView;
        CardView cardView;
        ImageView love_columnImage;
        TextView love_columnName;
        TextView love_columnDescription;
        ImageView love_column_love;

        ViewHolder(View view) {
            super(view);
            love_columnView=view;
            cardView = (CardView) view;
            love_columnImage = view.findViewById(R.id.love_column_image);
            love_columnName = view.findViewById(R.id.love_column_name);
            love_columnDescription=view.findViewById(R.id.love_column_description);
            love_column_love=view.findViewById(R.id.love_column_love);
        }
    }

    public LoveColumnAdapter(List<LikeColumn> love_columnList) {
        mLoveColumnList = love_columnList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.love_column_item, parent, false);
        final LoveColumnAdapter.ViewHolder holder=new LoveColumnAdapter.ViewHolder(view);
        holder.love_columnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=holder.getAdapterPosition();
                LikeColumn loveColumn = mLoveColumnList.get(position);
                Intent intent=new Intent(mContext,MessageActivity.class);
                intent.putExtra("columnId_intent",loveColumn.getId());
                intent.putExtra("username_intent",loveColumn.getUsername());
                intent.putExtra("columnName_intent",loveColumn.getName());
                intent.putExtra("columnDescription_intent",loveColumn.getDescription());
                intent.putExtra("columnThumbnail_intent",loveColumn.getThumbnail());
                mContext.startActivity(intent);
            }
        });
        holder.love_column_love.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onClick(View v) {
                int position=holder.getAdapterPosition();
                LikeColumn loveColumn = mLoveColumnList.get(position);
                dbHelper =new MyDatabaseHelper(mContext,"data.db",null,1) ;
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                Cursor cursor=db.query("love_column_table",null,null,null,null,null,null);
                if (cursor.moveToFirst()) {
                    do {
                        String username=cursor.getString(cursor.getColumnIndex("username"));
                        String column_id=cursor.getString(cursor.getColumnIndex("column_id"));
                        if (username.equals(loveColumn.getUsername())&&column_id.equals(loveColumn.getId())){
                            LoveColumn=true;
                            break;
                        }
                    }while (cursor.moveToNext());
                }
                cursor.close();
                if (!LoveColumn){
                    //Glide.with(mContext).load(R.drawable.collect1).asBitmap().into(holder.love_column);
                    ContentValues values = new ContentValues();
                    values.put("column_id",loveColumn.getId());
                    values.put("username",loveColumn.getUsername());
                    values.put("name",loveColumn.getName());
                    values.put("description",loveColumn.getDescription());
                    values.put("thumbnail",loveColumn.getThumbnail());
                    db.insert("love_column_table", null, values);
                    values.clear();
                    LoveColumn=true;
                    Toast.makeText(mContext,"已收藏",Toast.LENGTH_SHORT).show();
                    Glide.with(mContext).load(R.drawable.love1).asBitmap().into(holder.love_column_love);
                }else {
                    //Glide.with(mContext).load(R.drawable.collection).asBitmap().into(holder.love_column);
                    db.delete("love_column_table","column_id=?",new String[]{loveColumn.getId()});
                    db.close();
                    LoveColumn=false;
                    Toast.makeText(mContext,"已取消收藏",Toast.LENGTH_SHORT).show();
                    Glide.with(mContext).load(R.drawable.love0).asBitmap().into(holder.love_column_love);
                }
            }
        });
        //return new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final LikeColumn love_column = mLoveColumnList.get(position);
        holder.love_columnName.setText(love_column.getName());
        holder.love_columnDescription.setText(love_column.getDescription());
        Glide.with(mContext).load(love_column.getThumbnail()).asBitmap().placeholder(R.drawable.zhihu).error(R.drawable.zhihu).into(holder.love_columnImage);
        dbHelper =new MyDatabaseHelper(mContext,"data.db",null,1) ;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor=db.query("love_column_table",null,null,null,null,null,null);
        if (cursor.moveToFirst()) {
            do {
                String username=cursor.getString(cursor.getColumnIndex("username"));
                String column_id=cursor.getString(cursor.getColumnIndex("column_id"));
                if (username.equals(love_column.getUsername())&&column_id.equals(love_column.getId())){
                    Glide.with(mContext).load(R.drawable.love1).asBitmap().into(holder.love_column_love);
                    break;
                }else {
                    Glide.with(mContext).load(R.drawable.love0).asBitmap().into(holder.love_column_love);
                }
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
    }

    @Override
    public int getItemCount() {
        return mLoveColumnList.size();
    }

}