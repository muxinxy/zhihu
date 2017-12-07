package com.example.zz.zhihu;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ColumnAdapter extends RecyclerView.Adapter<ColumnAdapter.ViewHolder>{

    private List<Column> mColumnList;
    private Context mContext;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View columnView;
        CardView cardView;
        ImageView columnImage;
        TextView columnName;
        TextView columnDescription;

        public ViewHolder(View view) {
            super(view);
            columnView=view;
            cardView = (CardView) view;
            columnImage = view.findViewById(R.id.column_image);
            columnName = view.findViewById(R.id.column_name);
            columnDescription=view.findViewById(R.id.column_description);
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
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Column column = mColumnList.get(position);
        holder.columnName.setText(column.getName());
        holder.columnDescription.setText(column.getDescription());
        Glide.with(mContext).load(column.getThumbnail()).asBitmap().placeholder(R.drawable.zhihu).error(R.drawable.zhihu).into(holder.columnImage);
}

    @Override
    public int getItemCount() {
        return mColumnList.size();
    }

}