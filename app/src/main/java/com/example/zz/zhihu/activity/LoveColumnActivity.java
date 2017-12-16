package com.example.zz.zhihu.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.example.zz.zhihu.item.LikeColumn;
import com.example.zz.zhihu.adapter.LoveColumnAdapter;
import com.example.zz.zhihu.MyDatabaseHelper;
import com.example.zz.zhihu.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class LoveColumnActivity extends AppCompatActivity {
    private List<LikeColumn> love_columnList = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private MyDatabaseHelper dbHelper;
    private String username_intent,json;
    private Timer mTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_love_column);
        // init timer
        mTimer = new Timer();
        // start timer task
        setTimerTask();

        Intent intent=getIntent();
        username_intent=intent.getStringExtra("username_intent");

        dbHelper =new MyDatabaseHelper(this,"data.db",null,1) ;
        dbHelper.getWritableDatabase();
        SQLiteDatabase sdb = dbHelper.getReadableDatabase();
        Cursor cursor=sdb.query("love_column_table",null,null,null,null,null,null);
        if (cursor.moveToFirst()) {
            do {
                String username=cursor.getString(cursor.getColumnIndex("username"));
                String column_id=cursor.getString(cursor.getColumnIndex("column_id"));
                String name=cursor.getString(cursor.getColumnIndex("name"));
                String description=cursor.getString(cursor.getColumnIndex("description"));
                String thumbnail=cursor.getString(cursor.getColumnIndex("thumbnail"));
                json=cursor.getString(cursor.getColumnIndex("json"));
                if (username.equals(username_intent)){
                    love_columnList.add(new LikeColumn(username,name,column_id,description,thumbnail));
                }
            }while (cursor.moveToNext());
        }
        cursor.close();
        sdb.close();

        RecyclerView recyclerView = findViewById(R.id.rev_love_column);
        LoveColumnAdapter likeColumnAdapter = new LoveColumnAdapter(love_columnList);

        Toolbar toolbar = findViewById(R.id.toolbar_love_column);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setTitle("");
        }

        GridLayoutManager layoutManager=new GridLayoutManager(this,1);
        recyclerView.setLayoutManager(layoutManager);

        swipeRefreshLayout=findViewById(R.id.sre_love_column);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,R.color.colorAccent,R.color.colorButton);
        swipeRefreshLayout.setProgressViewEndTarget (false,300);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                ( new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        love_columnList.clear();
                        SQLiteDatabase sdb = dbHelper.getReadableDatabase();
                        Cursor cursor=sdb.query("love_column_table",null,null,null,null,null,null);
                        if (cursor.moveToFirst()) {
                            do {
                                String username=cursor.getString(cursor.getColumnIndex("username"));
                                String column_id=cursor.getString(cursor.getColumnIndex("column_id"));
                                String name=cursor.getString(cursor.getColumnIndex("name"));
                                String description=cursor.getString(cursor.getColumnIndex("description"));
                                String thumbnail=cursor.getString(cursor.getColumnIndex("thumbnail"));
                                if (username.equals(username_intent)){
                                    love_columnList.add(new LikeColumn(username_intent,name,column_id,description,thumbnail));
                                }
                            }while (cursor.moveToNext());
                        }
                        cursor.close();
                        sdb.close();
                        showResponse();
                    }
                }, 1000);
            }
        });
        if (ContextCompat.checkSelfPermission(LoveColumnActivity.this, Manifest.permission.INTERNET)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(LoveColumnActivity.this,new String[]{Manifest.permission.INTERNET},1);
        } else{
            showResponse();
        }
        recyclerView.setAdapter(likeColumnAdapter);
    }

    private void showResponse() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RecyclerView recyclerView = findViewById(R.id.rev_love_column);
                LoveColumnAdapter adapter = new LoveColumnAdapter(love_columnList);
                recyclerView.setAdapter(adapter);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // cancel timer
        mTimer.cancel();
    }

    private void setTimerTask() {
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 1;
                doActionHandler.sendMessage(message);
            }
        }, 1000, 1000/* 表示1000毫秒之後，每隔1000毫秒執行一次 */);
    }

    /**
     * do some action
     */
    @SuppressLint("HandlerLeak")
    private Handler doActionHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int msgId = msg.what;
            switch (msgId) {
                case 1:
                    // do some action
                    love_columnList.clear();
                    SQLiteDatabase sdb = dbHelper.getReadableDatabase();
                    Cursor cursor=sdb.query("love_column_table",null,null,null,null,null,null);
                    if (cursor.moveToFirst()) {
                        do {
                            String username=cursor.getString(cursor.getColumnIndex("username"));
                            String column_id=cursor.getString(cursor.getColumnIndex("column_id"));
                            String name=cursor.getString(cursor.getColumnIndex("name"));
                            String description=cursor.getString(cursor.getColumnIndex("description"));
                            String thumbnail=cursor.getString(cursor.getColumnIndex("thumbnail"));
                            if (username.equals(username_intent)){
                                love_columnList.add(new LikeColumn(username_intent,name,column_id,description,thumbnail));
                            }
                        }while (cursor.moveToNext());
                    }
                    cursor.close();
                    sdb.close();
                    showResponse();
                    break;
                default:
                    break;
            }
        }
    };

}
