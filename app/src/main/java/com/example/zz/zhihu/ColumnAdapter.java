package com.example.zz.zhihu;

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

import java.util.List;

import javax.microedition.khronos.opengles.GL;

public class ColumnAdapter extends RecyclerView.Adapter<ColumnAdapter.ViewHolder>{

    private List<Column> mColumnList;
    private Context mContext;
    private MyDatabaseHelper dbHelper;
    private boolean like=false;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View columnView;
        CardView cardView;
        ImageView columnImage;
        ImageView columnLike;
        TextView columnName;
        TextView columnDescription;

        public ViewHolder(View view) {
            super(view);
            columnView=view;
            cardView = (CardView) view;
            columnImage = view.findViewById(R.id.column_image);
            columnLike=view.findViewById(R.id.column_like);
            columnName = view.findViewById(R.id.column_name);
            columnDescription=view.findViewById(R.id.column_description);
        }
    }

    public ColumnAdapter(List<Column> columnList) {
        mColumnList = columnList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        dbHelper =new MyDatabaseHelper(mContext,"data.db",null,1) ;
        dbHelper.getWritableDatabase();
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.column_item, parent, false);
        final ViewHolder holder=new ViewHolder(view);
        holder.columnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=holder.getAdapterPosition();
                final Column column = mColumnList.get(position);
                Intent intent=new Intent(mContext,MessageActivity.class);
                intent.putExtra("columnId_intent",column.getId());
                mContext.startActivity(intent);
            }
        });
        holder.columnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Column column = mColumnList.get(position);
        /*SQLiteDatabase sdb = dbHelper.getReadableDatabase();
        Cursor cursor=sdb.query("like_table",null,null,null,null,null,null);
        if (cursor.moveToFirst()) {
            do {
                String like_id=cursor.getString(cursor.getColumnIndex("like_id"));
                if (like_id.equals(column.getId()))
                    like=true;
                break;
            }while (cursor.moveToNext());
        }
        cursor.close();
        sdb.close();*/
        holder.columnName.setText(column.getName());
        holder.columnDescription.setText(column.getDescription());
        Glide.with(mContext).load(column.getThumbnail()).asBitmap().placeholder(R.drawable.zhihu).error(R.drawable.zhihu).into(holder.columnImage);
        /*if (like){
            Glide.with(mContext).load(R.drawable.like1).asBitmap().error(R.drawable.like0).into(holder.columnLike);
        }*/

}

    @Override
    public int getItemCount() {
        return mColumnList.size();
    }

}