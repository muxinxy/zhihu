package com.example.zz.zhihu;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
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
import java.util.Collection;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<Column> columnList = new ArrayList<>();
    private DrawerLayout mydrawerLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MyDatabaseHelper dbHelper;
    private ColumnAdapter ColumnAdapter;
    private String username_intent;
    private Toolbar toolbar;
    private FloatingActionButton top;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        top=findViewById(R.id.top);

        dbHelper =new MyDatabaseHelper(this,"data.db",null,1) ;
        dbHelper.getWritableDatabase();

        final Intent intent=getIntent();
        username_intent=intent.getStringExtra("username_intent");

        final RecyclerView recyclerView = findViewById(R.id.rev_main);
        ColumnAdapter=new ColumnAdapter(columnList);

        toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.menu);
            actionBar.setTitle("");
        }

        mydrawerLayout=findViewById(R.id.drawer_layout);

        NavigationView navView=findViewById(R.id.nav_view);
        navView.setCheckedItem(R.id.username_header);
        View headerView = navView.getHeaderView(0);
        ImageView user_image = headerView.findViewById(R.id.user_image);
        TextView username_header=headerView.findViewById(R.id.username_header);
        SQLiteDatabase sdb = dbHelper.getReadableDatabase();
        Cursor cursor=sdb.query("user_table",null,null,null,null,null,null);
        if (cursor.moveToFirst()) {
            do {
                String username=cursor.getString(cursor.getColumnIndex("username"));
                String nickname=cursor.getString(cursor.getColumnIndex("nickname"));
                String userimage=cursor.getString(cursor.getColumnIndex("user_image"));
                if (username.equals(username_intent)){
                    username_header.setText(nickname);
                    Glide.with(this).load(userimage).asBitmap().placeholder(R.drawable.zhihu).error(R.drawable.zhihu).into(user_image);
                    break;
                }
            }while (cursor.moveToNext());
        }
        cursor.close();
        sdb.close();
        navView.setCheckedItem(R.id.username_header);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_index:
                        Intent intent=new Intent(MainActivity.this,IndexActivity.class);
                        intent.putExtra("username_intent",username_intent);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.nav_about:
                        Intent intent2=new Intent(MainActivity.this,AboutActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.nav_quit:
                        Intent intent1=new Intent(MainActivity.this,LoginActivity.class);
                        startActivity(intent1);
                        finish();
                        break;
                    case R.id.nav_hot:
                        Intent intent4=new Intent(MainActivity.this,HotActivity.class);
                        intent4.putExtra("username_intent",username_intent);
                        startActivity(intent4);
                        break;
                }
                mydrawerLayout.closeDrawers();
                return true;
            }
        });


        GridLayoutManager layoutManager=new GridLayoutManager(this,1);
        recyclerView.setLayoutManager(layoutManager);

        swipeRefreshLayout=findViewById(R.id.sre_main);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,R.color.colorAccent,R.color.colorButton);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                ( new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        columnList.clear();
                        sendRequestWithHttpURLConnection();
                    }
                }, 3000);
            }
        });
        if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.INTERNET)!=PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.INTERNET},1);
        } else{
            sendRequestWithHttpURLConnection();
        }
        recyclerView.setAdapter(ColumnAdapter);
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
        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                final Column column = columnList.get(position);
                Intent intent=new Intent(MainActivity.this,MessageActivity.class);
                intent.putExtra("columnId_intent",column.getId());
                intent.putExtra("username_intent",username_intent);
                intent.putExtra("columnName_intent",column.getName());
                intent.putExtra("columnDescription_intent",column.getDescription());
                intent.putExtra("columnThumbnail_intent",column.getThumbnail());
                intent.putExtra("main","main");
                startActivity(intent);
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
                    Toast.makeText(MainActivity.this,"你拒绝了权限",Toast.LENGTH_SHORT).show();
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
                    URL url = new URL("http://news-at.zhihu.com/api/3/sections");
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
            JSONArray jsonArray=jsonObject.getJSONArray("data");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = (JSONObject)jsonArray.get(i);
                String id = jsonObject1.getString("id");
                String name = jsonObject1.getString("name");
                String description = jsonObject1.getString("description");
                String thumbnail=jsonObject1.getString("thumbnail");
                columnList.add(new Column(name,id,description,thumbnail));
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
                RecyclerView recyclerView = findViewById(R.id.rev_main);
                ColumnAdapter adapter = new ColumnAdapter(columnList);
                recyclerView.setAdapter(adapter);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mydrawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
        }
        return true;
    }

    public static class ItemClickSupport {
        private final RecyclerView mRecyclerView;
        private OnItemClickListener mOnItemClickListener;
        private OnItemLongClickListener mOnItemLongClickListener;
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

        public static ItemClickSupport addTo(RecyclerView view) {
            ItemClickSupport support = (ItemClickSupport) view.getTag(R.id.item_click_support);
            if (support == null) {
                support = new ItemClickSupport(view);
            }
            return support;
        }

        public static ItemClickSupport removeFrom(RecyclerView view) {
            ItemClickSupport support = (ItemClickSupport) view.getTag(R.id.item_click_support);
            if (support != null) {
                support.detach(view);
            }
            return support;
        }

        public ItemClickSupport setOnItemClickListener(OnItemClickListener listener) {
            mOnItemClickListener = listener;
            return this;
        }

        public ItemClickSupport setOnItemLongClickListener(OnItemLongClickListener listener) {
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
