package com.example.zz.zhihu;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

public class LikeArticleActivity extends AppCompatActivity {
    private List<LikeArticle> like_articleList = new ArrayList<>();
    RecyclerView.LayoutManager recyclerViewlayoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String username_intent;
    private MyDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like_article);

        Intent intent=getIntent();
        username_intent=intent.getStringExtra("username_intent");

        dbHelper =new MyDatabaseHelper(this,"data.db",null,1) ;
        dbHelper.getWritableDatabase();
        SQLiteDatabase sdb = dbHelper.getReadableDatabase();
        Cursor cursor=sdb.query("like_article_table",null,null,null,null,null,null);
        if (cursor.moveToFirst()) {
            do {
                String username=cursor.getString(cursor.getColumnIndex("username"));
                String news_id=cursor.getString(cursor.getColumnIndex("news_id"));
                String title=cursor.getString(cursor.getColumnIndex("title"));
                String thumbnail=cursor.getString(cursor.getColumnIndex("thumbnail"));
                String url=cursor.getString(cursor.getColumnIndex("url"));
                if (username.equals(username_intent)){
                    like_articleList.add(new LikeArticle(title,news_id,thumbnail,url));
                }
            }while (cursor.moveToNext());
        }
        cursor.close();
        sdb.close();


        Toolbar toolbar = findViewById(R.id.toolbar_like_article);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setTitle("");
        }

        RecyclerView recyclerView = findViewById(R.id.rev_like_article);
        LikeArticleAdapter adapter = new LikeArticleAdapter(like_articleList);
        recyclerView.setAdapter(adapter);

        recyclerViewlayoutManager = new LinearLayoutManager(this);
        GridLayoutManager layoutManager=new GridLayoutManager(this,1);
        recyclerView.setLayoutManager(layoutManager);

        swipeRefreshLayout=findViewById(R.id.sre_like_article);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,R.color.colorAccent,R.color.colorButton);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                ( new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        like_articleList.clear();
                        SQLiteDatabase sdb = dbHelper.getReadableDatabase();
                        Cursor cursor=sdb.query("like_article_table",null,null,null,null,null,null);
                        if (cursor.moveToFirst()) {
                            do {
                                String username=cursor.getString(cursor.getColumnIndex("username"));
                                String news_id=cursor.getString(cursor.getColumnIndex("news_id"));
                                String title=cursor.getString(cursor.getColumnIndex("title"));
                                String thumbnail=cursor.getString(cursor.getColumnIndex("thumbnail"));
                                String url=cursor.getString(cursor.getColumnIndex("url"));
                                if (username.equals(username_intent)){
                                    like_articleList.add(new LikeArticle(title,news_id,thumbnail,url));
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
        if (ContextCompat.checkSelfPermission(LikeArticleActivity.this, Manifest.permission.INTERNET)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(LikeArticleActivity.this,new String[]{Manifest.permission.INTERNET},1);
        } else{
            showResponse();
        }

        LikeArticleActivity.ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new LikeArticleActivity.ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                final LikeArticle like_article = like_articleList.get(position);
                Intent intent=new Intent(LikeArticleActivity.this,ArticleActivity.class);
                intent.putExtra("Title_intent",like_article.getTitle());
                intent.putExtra("username_intent",username_intent);
                intent.putExtra("like_NewsId_intent",like_article.getNews_id());
                intent.putExtra("Url_intent",like_article.getUrl());
                intent.putExtra("Thumbnail_intent",like_article.getThumbnail());
                intent.putExtra("hot","like");
                startActivity(intent);
                finish();
            }
        });
    }

    private void showResponse() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RecyclerView recyclerView = findViewById(R.id.rev_like_article);
                LikeArticleAdapter adapter = new LikeArticleAdapter(like_articleList);
                recyclerView.setAdapter(adapter);
            }
        });
    }

    public static class ItemClickSupport {
        private final RecyclerView mRecyclerView;
        private LikeArticleActivity.ItemClickSupport.OnItemClickListener mOnItemClickListener;
        private LikeArticleActivity.ItemClickSupport.OnItemLongClickListener mOnItemLongClickListener;
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

        public static LikeArticleActivity.ItemClickSupport addTo(RecyclerView view) {
            LikeArticleActivity.ItemClickSupport support = (LikeArticleActivity.ItemClickSupport) view.getTag(R.id.item_click_support);
            if (support == null) {
                support = new LikeArticleActivity.ItemClickSupport(view);
            }
            return support;
        }

        public static LikeArticleActivity.ItemClickSupport removeFrom(RecyclerView view) {
            LikeArticleActivity.ItemClickSupport support = (LikeArticleActivity.ItemClickSupport) view.getTag(R.id.item_click_support);
            if (support != null) {
                support.detach(view);
            }
            return support;
        }

        public LikeArticleActivity.ItemClickSupport setOnItemClickListener(LikeArticleActivity.ItemClickSupport.OnItemClickListener listener) {
            mOnItemClickListener = listener;
            return this;
        }

        public LikeArticleActivity.ItemClickSupport setOnItemLongClickListener(LikeArticleActivity.ItemClickSupport.OnItemLongClickListener listener) {
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
