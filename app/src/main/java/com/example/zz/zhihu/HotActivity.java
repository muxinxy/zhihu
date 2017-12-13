package com.example.zz.zhihu;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
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
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.Toast;

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
import java.util.Timer;
import java.util.TimerTask;

public class HotActivity extends AppCompatActivity {
    private List<Hot> hotList = new ArrayList<>();
    RecyclerView.LayoutManager recyclerViewlayoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String username_intent;
    private Toolbar toolbar;
    private FloatingActionButton top;
    private String LCN,SCN;//long commits number and short commits number
    private Timer mTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hot);

        top=findViewById(R.id.top);

        Intent intent=getIntent();
        username_intent=intent.getStringExtra("username_intent");
        toolbar = findViewById(R.id.toolbar_hot);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setTitle("");
        }

        final RecyclerView recyclerView = findViewById(R.id.rev_hot);
        HotAdapter adapter = new HotAdapter(hotList);
        recyclerView.setAdapter(adapter);
        //noinspection deprecation
        recyclerView.setOnScrollListener(new HidingScrollListener() {
            @Override
            public void onHide() {
                hideViews();
            }
            @Override
            public void onShow() {
                showViews();
            }
        });

        recyclerViewlayoutManager = new LinearLayoutManager(this);
        GridLayoutManager layoutManager=new GridLayoutManager(this,1);
        recyclerView.setLayoutManager(layoutManager);

        swipeRefreshLayout=findViewById(R.id.sre_hot);
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
                        hotList.clear();
                        sendRequestWithHttpURLConnection();
                    }
                }, 1000);
            }
        });
        if (ContextCompat.checkSelfPermission(HotActivity.this, Manifest.permission.INTERNET)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(HotActivity.this,new String[]{Manifest.permission.INTERNET},1);
        } else{
            sendRequestWithHttpURLConnection();
        }

        top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.smoothScrollToPosition(0);
            }
        });
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        switch (requestCode){
            case 1:
                if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    sendRequestWithHttpURLConnection();
                }else {
                    Toast.makeText(HotActivity.this,"你拒绝了权限",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    private void sendRequestWithHttpURLConnection() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL("https://news-at.zhihu.com/api/3/news/hot");
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
                    parseJSONWithJSONObject(response.toString());
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

    private void parseJSONWithJSONObject(String data) {
        try {
            JSONObject jsonObject=new JSONObject(data);
            JSONArray jsonArray=jsonObject.getJSONArray("recent");
            for (int i = 0; i < jsonArray.length(); i++) {
                //LCN="";
                //SCN="";
                JSONObject jsonObject1 = (JSONObject)jsonArray.get(i);
                String news_id = jsonObject1.getString("news_id");
                String title = jsonObject1.getString("title");
                String thumbnail=jsonObject1.getString("thumbnail");
                String url=jsonObject1.getString("url");
                LCN=sendRequestWithHttpURLConnection_LongCommits(news_id);
                SCN=sendRequestWithHttpURLConnection_ShortCommits(news_id);
                hotList.add(new Hot(username_intent,title,news_id,thumbnail,url,LCN,SCN));
            }
            showResponse();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private String sendRequestWithHttpURLConnection_LongCommits(final String id) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url= new URL("https://news-at.zhihu.com/api/4/story/"+id+"/long-comments");
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
                    LCN=parseJSONWithJSONObject_LongCommits(response.toString());
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
        return LCN;
    }

    private String parseJSONWithJSONObject_LongCommits(String data) {
        try {
            JSONObject jsonObject=new JSONObject(data);
            JSONArray jsonArray=jsonObject.getJSONArray("comments");
            LCN=String.valueOf(jsonArray.length());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return LCN;
    }
    private String sendRequestWithHttpURLConnection_ShortCommits(final String id) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url= new URL("https://news-at.zhihu.com/api/4/story/"+id+"/short-comments");
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
                    SCN=parseJSONWithJSONObject_ShortCommits(response.toString());
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
        return SCN;
    }

    private String parseJSONWithJSONObject_ShortCommits(String data) {
        try {
            JSONObject jsonObject=new JSONObject(data);
            JSONArray jsonArray=jsonObject.getJSONArray("comments");
            SCN=String.valueOf(jsonArray.length());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return SCN;
    }
    private void showResponse() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RecyclerView recyclerView = findViewById(R.id.rev_hot);
                HotAdapter adapter = new HotAdapter(hotList);
                recyclerView.setAdapter(adapter);
            }
        });
    }

    public abstract class HidingScrollListener extends RecyclerView.OnScrollListener {
        private static final int HIDE_THRESHOLD = 20;
        private int scrolledDistance = 0;
        private boolean controlsVisible = true;
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (scrolledDistance > HIDE_THRESHOLD && controlsVisible) {
                onHide();
                controlsVisible = false;
                scrolledDistance = 0;
            } else if (scrolledDistance < -HIDE_THRESHOLD && !controlsVisible) {
                onShow();
                controlsVisible = true;
                scrolledDistance = 0;
            }
            if((controlsVisible && dy>0) || (!controlsVisible && dy<0)) {
                scrolledDistance += dy;
            }
        }
        public abstract void onHide();
        public abstract void onShow();

    }
    private void hideViews() {
        toolbar.animate().translationY(-toolbar.getHeight()).setInterpolator(new AccelerateInterpolator(2));
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) top.getLayoutParams();
        int fabBottomMargin = lp.bottomMargin;
        top.animate().translationY(top.getHeight()+fabBottomMargin).setInterpolator(new AccelerateInterpolator(2)).start();
    }

    private void showViews() {
        toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
        top.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
    }
}
