package com.example.zz.zhihu;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.TimingLogger;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.List;

public class ColumnAdapter extends RecyclerView.Adapter<ColumnAdapter.ViewHolder>{

    private List<Column> mColumnList;
    private Context mContext;
    private boolean LikeColumn=false;
    private MyDatabaseHelper dbHelper;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View columnView;
        CardView cardView;
        ImageView columnImage;
        TextView columnName;
        TextView columnDescription;
        ImageView like_column;

        public ViewHolder(View view) {
            super(view);
            columnView=view;
            cardView = (CardView) view;
            columnImage = view.findViewById(R.id.column_image);
            columnName = view.findViewById(R.id.column_name);
            columnDescription=view.findViewById(R.id.column_description);
            like_column=view.findViewById(R.id.like_column);
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
                intent.putExtra("main","main");
                mContext.startActivity(intent);
            }
        });
        holder.like_column.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onClick(View v) {
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
                    //Glide.with(mContext).load(R.drawable.collect1).asBitmap().into(holder.like_column);
                    ContentValues values = new ContentValues();
                    values.put("column_id",column.getId());
                    values.put("username",column.getUsername());
                    values.put("name",column.getName());
                    values.put("description",column.getDescription());
                    values.put("thumbnail",column.getThumbnail());
                    db.insert("like_column_table", null, values);
                    values.clear();
                    LikeColumn=true;
                    Toast.makeText(mContext,"已收藏",Toast.LENGTH_SHORT).show();
                    Glide.with(mContext).load(R.drawable.collect1).asBitmap().into(holder.like_column);
                }else {
                    //Glide.with(mContext).load(R.drawable.collection).asBitmap().into(holder.like_column);
                    db.delete("like_column_table","column_id=?",new String[]{column.getId()});
                    db.close();
                    LikeColumn=false;
                    Toast.makeText(mContext,"已取消收藏",Toast.LENGTH_SHORT).show();
                    Glide.with(mContext).load(R.drawable.collection).asBitmap().into(holder.like_column);
                }
            }
        });
        //return new ViewHolder(view);
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
}
    @Override
    public int getItemCount() {
        return mColumnList.size();
    }

}