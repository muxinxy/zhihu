package com.example.zz.zhihu;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
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

public class HotActivity extends AppCompatActivity {
    private List<Hot> hotList = new ArrayList<>();
    RecyclerView.LayoutManager recyclerViewlayoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String username_intent;
    private Toolbar toolbar;
    private FloatingActionButton top;

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
                }, 3000);
            }
        });
        if (ContextCompat.checkSelfPermission(HotActivity.this, Manifest.permission.INTERNET)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(HotActivity.this,new String[]{Manifest.permission.INTERNET},1);
        } else{
            sendRequestWithHttpURLConnection();
        }

        HotActivity.ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new HotActivity.ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                final Hot hot = hotList.get(position);
                Intent intent=new Intent(HotActivity.this,ArticleActivity.class);
                intent.putExtra("Title_intent",hot.getTitle());
                intent.putExtra("username_intent",username_intent);
                intent.putExtra("NewsId_intent",hot.getNews_id());
                intent.putExtra("Url_intent",hot.getUrl());
                intent.putExtra("Thumbnail_intent",hot.getThumbnail());
                intent.putExtra("hot","hot");
                startActivity(intent);
                finish();
            }
        });

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
                JSONObject jsonObject1 = (JSONObject)jsonArray.get(i);
                String news_id = jsonObject1.getString("news_id");
                String title = jsonObject1.getString("title");
                String thumbnail=jsonObject1.getString("thumbnail");
                String url=jsonObject1.getString("url");
                hotList.add(new Hot(title,news_id,thumbnail,url));
            }
            showResponse();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public static class ItemClickSupport {
        private final RecyclerView mRecyclerView;
        private HotActivity.ItemClickSupport.OnItemClickListener mOnItemClickListener;
        private HotActivity.ItemClickSupport.OnItemLongClickListener mOnItemLongClickListener;
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

        public static HotActivity.ItemClickSupport addTo(RecyclerView view) {
            HotActivity.ItemClickSupport support = (HotActivity.ItemClickSupport) view.getTag(R.id.item_click_support);
            if (support == null) {
                support = new HotActivity.ItemClickSupport(view);
            }
            return support;
        }

        public static HotActivity.ItemClickSupport removeFrom(RecyclerView view) {
            HotActivity.ItemClickSupport support = (HotActivity.ItemClickSupport) view.getTag(R.id.item_click_support);
            if (support != null) {
                support.detach(view);
            }
            return support;
        }

        public HotActivity.ItemClickSupport setOnItemClickListener(HotActivity.ItemClickSupport.OnItemClickListener listener) {
            mOnItemClickListener = listener;
            return this;
        }

        public HotActivity.ItemClickSupport setOnItemLongClickListener(HotActivity.ItemClickSupport.OnItemLongClickListener listener) {
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
