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

public class LikeColumnAdapter extends RecyclerView.Adapter<LikeColumnAdapter.ViewHolder>{

    private List<com.example.zz.zhihu.item.LikeColumn> mLikeColumnList;
    private Context mContext;
    private boolean LikeColumn=false;
    private MyDatabaseHelper dbHelper;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View like_columnView;
        CardView cardView;
        ImageView like_columnImage;
        TextView like_columnName;
        TextView like_columnDescription;
        ImageView like_column_like;

        ViewHolder(View view) {
            super(view);
            like_columnView=view;
            cardView = (CardView) view;
            like_columnImage = view.findViewById(R.id.like_column_image);
            like_columnName = view.findViewById(R.id.like_column_name);
            like_columnDescription=view.findViewById(R.id.like_column_description);
            like_column_like=view.findViewById(R.id.like_column_like);
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
        final LikeColumnAdapter.ViewHolder holder=new LikeColumnAdapter.ViewHolder(view);
        holder.like_columnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=holder.getAdapterPosition();
                LikeColumn likeColumn = mLikeColumnList.get(position);
                Intent intent=new Intent(mContext,MessageActivity.class);
                intent.putExtra("columnId_intent",likeColumn.getId());
                intent.putExtra("username_intent",likeColumn.getUsername());
                intent.putExtra("columnName_intent",likeColumn.getName());
                intent.putExtra("columnDescription_intent",likeColumn.getDescription());
                intent.putExtra("columnThumbnail_intent",likeColumn.getThumbnail());
                intent.putExtra("main","main");
                mContext.startActivity(intent);
            }
        });
        holder.like_column_like.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onClick(View v) {
                int position=holder.getAdapterPosition();
                LikeColumn likeColumn = mLikeColumnList.get(position);
                dbHelper =new MyDatabaseHelper(mContext,"data.db",null,1) ;
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                Cursor cursor=db.query("like_column_table",null,null,null,null,null,null);
                if (cursor.moveToFirst()) {
                    do {
                        String username=cursor.getString(cursor.getColumnIndex("username"));
                        String column_id=cursor.getString(cursor.getColumnIndex("column_id"));
                        if (username.equals(likeColumn.getUsername())&&column_id.equals(likeColumn.getId())){
                            LikeColumn=true;
                            break;
                        }
                    }while (cursor.moveToNext());
                }
                cursor.close();
                if (!LikeColumn){
                    //Glide.with(mContext).load(R.drawable.collect1).asBitmap().into(holder.like_column);
                    ContentValues values = new ContentValues();
                    values.put("column_id",likeColumn.getId());
                    values.put("username",likeColumn.getUsername());
                    values.put("name",likeColumn.getName());
                    values.put("description",likeColumn.getDescription());
                    values.put("thumbnail",likeColumn.getThumbnail());
                    db.insert("like_column_table", null, values);
                    values.clear();
                    LikeColumn=true;
                    Toast.makeText(mContext,"已收藏",Toast.LENGTH_SHORT).show();
                    Glide.with(mContext).load(R.drawable.collect1).asBitmap().into(holder.like_column_like);
                }else {
                    //Glide.with(mContext).load(R.drawable.collection).asBitmap().into(holder.like_column);
                    db.delete("like_column_table","column_id=?",new String[]{likeColumn.getId()});
                    db.close();
                    LikeColumn=false;
                    Toast.makeText(mContext,"已取消收藏",Toast.LENGTH_SHORT).show();
                    Glide.with(mContext).load(R.drawable.collection).asBitmap().into(holder.like_column_like);
                }
            }
        });
        //return new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final LikeColumn like_column = mLikeColumnList.get(position);
        holder.like_columnName.setText(like_column.getName());
        holder.like_columnDescription.setText(like_column.getDescription());
        Glide.with(mContext).load(like_column.getThumbnail()).asBitmap().placeholder(R.drawable.zhihu).error(R.drawable.zhihu).into(holder.like_columnImage);
        dbHelper =new MyDatabaseHelper(mContext,"data.db",null,1) ;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor=db.query("like_column_table",null,null,null,null,null,null);
        if (cursor.moveToFirst()) {
            do {
                String username=cursor.getString(cursor.getColumnIndex("username"));
                String column_id=cursor.getString(cursor.getColumnIndex("column_id"));
                if (username.equals(like_column.getUsername())&&column_id.equals(like_column.getId())){
                    Glide.with(mContext).load(R.drawable.collect1).asBitmap().into(holder.like_column_like);
                    break;
                }else {
                    Glide.with(mContext).load(R.drawable.collection).asBitmap().into(holder.like_column_like);
                }
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
    }

    @Override
    public int getItemCount() {
        return mLikeColumnList.size();
    }

}