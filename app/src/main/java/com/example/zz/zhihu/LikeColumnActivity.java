package com.example.zz.zhihu;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class LikeColumnActivity extends AppCompatActivity {
    private List<LikeColumn> like_columnList = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private MyDatabaseHelper dbHelper;
    private LikeColumnAdapter LikeColumnAdapter;
    private String username_intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like_column);

        Intent intent=getIntent();
        username_intent=intent.getStringExtra("username_intent");

        dbHelper =new MyDatabaseHelper(this,"data.db",null,1) ;
        dbHelper.getWritableDatabase();
        SQLiteDatabase sdb = dbHelper.getReadableDatabase();
        Cursor cursor=sdb.query("like_column_table",null,null,null,null,null,null);
        if (cursor.moveToFirst()) {
            do {
                String username=cursor.getString(cursor.getColumnIndex("username"));
                String column_id=cursor.getString(cursor.getColumnIndex("column_id"));
                String name=cursor.getString(cursor.getColumnIndex("name"));
                String description=cursor.getString(cursor.getColumnIndex("description"));
                String thumbnail=cursor.getString(cursor.getColumnIndex("thumbnail"));
                if (username.equals(username_intent)){
                    like_columnList.add(new LikeColumn(username,name,column_id,description,thumbnail));
                }
            }while (cursor.moveToNext());
        }
        cursor.close();
        sdb.close();

        RecyclerView recyclerView = findViewById(R.id.rev_like_column);
        LikeColumnAdapter=new LikeColumnAdapter(like_columnList);

        Toolbar toolbar = findViewById(R.id.toolbar_like_column);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setTitle("");
        }

        GridLayoutManager layoutManager=new GridLayoutManager(this,1);
        recyclerView.setLayoutManager(layoutManager);

        swipeRefreshLayout=findViewById(R.id.sre_like_column);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,R.color.colorAccent,R.color.colorButton);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                ( new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        like_columnList.clear();
                        SQLiteDatabase sdb = dbHelper.getReadableDatabase();
                        Cursor cursor=sdb.query("like_column_table",null,null,null,null,null,null);
                        if (cursor.moveToFirst()) {
                            do {
                                String username=cursor.getString(cursor.getColumnIndex("username"));
                                String column_id=cursor.getString(cursor.getColumnIndex("column_id"));
                                String name=cursor.getString(cursor.getColumnIndex("name"));
                                String description=cursor.getString(cursor.getColumnIndex("description"));
                                String thumbnail=cursor.getString(cursor.getColumnIndex("thumbnail"));
                                if (username.equals(username_intent)){
                                    like_columnList.add(new LikeColumn(username_intent,name,column_id,description,thumbnail));
                                }
                            }while (cursor.moveToNext());
                        }
                        cursor.close();
                        sdb.close();
                        showResponse();
                    }
                }, 3000);
            }
        });
        if (ContextCompat.checkSelfPermission(LikeColumnActivity.this, Manifest.permission.INTERNET)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(LikeColumnActivity.this,new String[]{Manifest.permission.INTERNET},1);
        } else{
            showResponse();
        }
        recyclerView.setAdapter(LikeColumnAdapter);
        LikeColumnActivity.ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new LikeColumnActivity.ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                final LikeColumn like_column = like_columnList.get(position);
                Intent intent=new Intent(LikeColumnActivity.this,MessageActivity.class);
                intent.putExtra("like_columnId_intent",like_column.getId());
                intent.putExtra("username_intent",username_intent);
                intent.putExtra("columnName_intent",like_column.getName());
                intent.putExtra("columnDescription_intent",like_column.getDescription());
                intent.putExtra("columnThumbnail_intent",like_column.getThumbnail());
                intent.putExtra("main","like");
                startActivity(intent);
                finish();
            }
        });
    }

    private void showResponse() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RecyclerView recyclerView = findViewById(R.id.rev_like_column);
                LikeColumnAdapter adapter = new LikeColumnAdapter(like_columnList);
                recyclerView.setAdapter(adapter);
            }
        });
    }

    public static class ItemClickSupport {
        private final RecyclerView mRecyclerView;
        private LikeColumnActivity.ItemClickSupport.OnItemClickListener mOnItemClickListener;
        private LikeColumnActivity.ItemClickSupport.OnItemLongClickListener mOnItemLongClickListener;
        private View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    RecyclerView.ViewHolder holder = mRecyclerView.getChildViewHolder(v);
                    mOnItemClickListener.onItemClicked(mRecyclerView, holder.getAdapterPosition(), v);
                }
            }
        };
        private View.OnLongClickListener mOnLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnItemLongClickListener != null) {
                    RecyclerView.ViewHolder holder = mRecyclerView.getChildViewHolder(v);
                    return mOnItemLongClickListener.onItemLongClicked(mRecyclerView, holder.getAdapterPosition(), v);
                }
                return false;
            }
        };
        private RecyclerView.OnChildAttachStateChangeListener mAttachListener
                = new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {
                if (mOnItemClickListener != null) {
                    view.setOnClickListener(mOnClickListener);
                }
                if (mOnItemLongClickListener != null) {
                    view.setOnLongClickListener(mOnLongClickListener);
                }
            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {

            }
        };

        private ItemClickSupport(RecyclerView recyclerView) {
            mRecyclerView = recyclerView;
            mRecyclerView.setTag(R.id.item_click_support, this);
            mRecyclerView.addOnChildAttachStateChangeListener(mAttachListener);
        }

        public static LikeColumnActivity.ItemClickSupport addTo(RecyclerView view) {
            LikeColumnActivity.ItemClickSupport support = (LikeColumnActivity.ItemClickSupport) view.getTag(R.id.item_click_support);
            if (support == null) {
                support = new LikeColumnActivity.ItemClickSupport(view);
            }
            return support;
        }

        public static LikeColumnActivity.ItemClickSupport removeFrom(RecyclerView view) {
            LikeColumnActivity.ItemClickSupport support = (LikeColumnActivity.ItemClickSupport) view.getTag(R.id.item_click_support);
            if (support != null) {
                support.detach(view);
            }
            return support;
        }

        public LikeColumnActivity.ItemClickSupport setOnItemClickListener(LikeColumnActivity.ItemClickSupport.OnItemClickListener listener) {
            mOnItemClickListener = listener;
            return this;
        }

        public LikeColumnActivity.ItemClickSupport setOnItemLongClickListener(LikeColumnActivity.ItemClickSupport.OnItemLongClickListener listener) {
            mOnItemLongClickListener = listener;
            return this;
        }

        private void detach(RecyclerView view) {
            view.removeOnChildAttachStateChangeListener(mAttachListener);
            view.setTag(R.id.item_click_support, null);
        }

        public interface OnItemClickListener {

            void onItemClicked(RecyclerView recyclerView, int position, View v);
        }

        public interface OnItemLongClickListener {

            boolean onItemLongClicked(RecyclerView recyclerView, int position, View v);
        }
    }
}
