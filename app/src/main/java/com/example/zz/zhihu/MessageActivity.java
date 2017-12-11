package com.example.zz.zhihu;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import java.util.Objects;

public class MessageActivity extends AppCompatActivity {
    private List<Message> messageList = new ArrayList<>();
    RecyclerView.LayoutManager recyclerViewlayoutManager;
    private String columnId_intent;
    private String like_columnId_intent=null;
    private String main;
    private String username_intent;
    private String columnName_intent;
    private String columnDescription_intent;
    private String columnThumbnail_intent;
    private MyDatabaseHelper dbHelper;
    private String Url;

    private boolean collect_column=false;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        dbHelper =new MyDatabaseHelper(this,"data.db",null,1) ;
        dbHelper.getWritableDatabase();

        Toolbar toolbar = findViewById(R.id.toolbar_message);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setTitle("栏目详情");
        }

        Intent intent=getIntent();
        columnId_intent=intent.getStringExtra("columnId_intent");
        like_columnId_intent=intent.getStringExtra("like_columnId_intent");
        username_intent=intent.getStringExtra("username_intent");
        columnName_intent=intent.getStringExtra("columnName_intent");
        columnDescription_intent=intent.getStringExtra("columnDescription_intent");
        columnThumbnail_intent=intent.getStringExtra("columnThumbnail_intent");
        main=intent.getStringExtra("main");
        if(main.equals("main"))
            Url= "http://news-at.zhihu.com/api/3/section/"+columnId_intent;
        else
            Url = "http://news-at.zhihu.com/api/3/section/"+like_columnId_intent;
        RecyclerView recyclerView = findViewById(R.id.rev_message);
        MessageAdapter adapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(adapter);

        recyclerViewlayoutManager = new LinearLayoutManager(this);
        GridLayoutManager layoutManager=new GridLayoutManager(this,1);
        recyclerView.setLayoutManager(layoutManager);



        swipeRefreshLayout=findViewById(R.id.sre_message);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,R.color.colorAccent,R.color.colorButton);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                ( new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        messageList.clear();
                        sendRequestWithHttpURLConnection();
                    }
                }, 3000);
            }
        });
        if (ContextCompat.checkSelfPermission(MessageActivity.this, Manifest.permission.INTERNET)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MessageActivity.this,new String[]{Manifest.permission.INTERNET},1);
        } else{
            sendRequestWithHttpURLConnection();
        }
        SQLiteDatabase sdb = dbHelper.getReadableDatabase();
        Cursor cursor=sdb.query("like_column_table",null,null,null,null,null,null);
        if (cursor.moveToFirst()) {
            do {
                String username=cursor.getString(cursor.getColumnIndex("username"));
                String column=cursor.getString(cursor.getColumnIndex("column_id"));
                if(main.equals("main")){
                    if (username.equals(username_intent)&&column.equals(columnId_intent)){
                        collect_column=true;
                        break;
                    }
                }
                else {
                    if (username.equals(username_intent)&&column.equals(like_columnId_intent)){
                        collect_column=true;
                        break;
                    }
                }
            }while (cursor.moveToNext());
        }
        cursor.close();
        sdb.close();

        MessageActivity.ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new MessageActivity.ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                final Message message = messageList.get(position);
                Intent intent=new Intent(MessageActivity.this,ArticleActivity.class);
                intent.putExtra("NewsId_intent",message.getId());
                intent.putExtra("hot","message");
                intent.putExtra("columnId_intent",columnId_intent);
                intent.putExtra("username_intent",username_intent);
                intent.putExtra("title",message.getTitle());
                intent.putExtra("thumbnail",message.getImages());
                startActivity(intent);
            }
        });

    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        switch (requestCode){
            case 1:
                if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    sendRequestWithHttpURLConnection();
                }else {
                    Toast.makeText(MessageActivity.this,"你拒绝了权限",Toast.LENGTH_SHORT).show();
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
                    URL url= new URL(Url);
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
        String images = null;
        try {
            JSONObject jsonObject=new JSONObject(data);
            String timestamp=jsonObject.getString("timestamp");
            JSONArray jsonArray=jsonObject.getJSONArray("stories");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = (JSONObject)jsonArray.get(i);
                JSONArray jsonArray1=jsonObject1.getJSONArray("images");
                for (int j=0;j<jsonArray1.length();j++) {
                    images=jsonArray1.getString(j);
                }
                String date=jsonObject1.getString("date");
                String display_date = jsonObject1.getString("display_date");
                String id = jsonObject1.getString("id");
                String title = jsonObject1.getString("title");

                messageList.add(new Message(title,id,display_date,images));

            }
            String name=jsonObject.getString("name");
            showResponse();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void showResponse() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RecyclerView recyclerView = findViewById(R.id.rev_message);
                MessageAdapter adapter = new MessageAdapter(messageList);
                recyclerView.setAdapter(adapter);
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu,menu) ;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String id;
        if(main.equals("main"))
            id=columnId_intent;
        else
            id =like_columnId_intent;
        switch (item.getItemId()) {
            case R.id.Collect_Column:
                if (!collect_column){
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("column_id",id);
                    values.put("username",username_intent);
                    values.put("name",columnName_intent);
                    values.put("description",columnDescription_intent);
                    values.put("thumbnail",columnThumbnail_intent);
                    db.insert("like_column_table", null, values);
                    values.clear();
                    db.close();
                    collect_column=true;
                    Toast.makeText(MessageActivity.this,"已收藏",Toast.LENGTH_SHORT).show();
                }else Toast.makeText(MessageActivity.this,"收藏已存在",Toast.LENGTH_SHORT).show();
                break;
            case R.id.QuitCollect_Column:
                if (collect_column){
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    db.delete("like_column_table","column_id=?",new String[]{id});
                    db.close();
                    collect_column=false;
                    Toast.makeText(MessageActivity.this,"已取消收藏",Toast.LENGTH_SHORT).show();
                }else Toast.makeText(MessageActivity.this,"收藏不存在",Toast.LENGTH_SHORT).show();
            default:
        }
        return true;
    }
    public void onBackPressed() {
        Intent intent1 =new Intent(MessageActivity.this,MainActivity.class);
        intent1.putExtra("username_intent",username_intent);
        Intent intent2=new Intent(MessageActivity.this,LikeColumnActivity.class);
        intent2.putExtra("username_intent",username_intent);
        if(main.equals("main"))
            startActivity(intent1);
        else startActivity(intent2);
        finish();
    }
    public static class ItemClickSupport {
        private final RecyclerView mRecyclerView;
        private MessageActivity.ItemClickSupport.OnItemClickListener mOnItemClickListener;
        private MessageActivity.ItemClickSupport.OnItemLongClickListener mOnItemLongClickListener;
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

        public static MessageActivity.ItemClickSupport addTo(RecyclerView view) {
            MessageActivity.ItemClickSupport support = (MessageActivity.ItemClickSupport) view.getTag(R.id.item_click_support);
            if (support == null) {
                support = new MessageActivity.ItemClickSupport(view);
            }
            return support;
        }

        public static MessageActivity.ItemClickSupport removeFrom(RecyclerView view) {
            MessageActivity.ItemClickSupport support = (MessageActivity.ItemClickSupport) view.getTag(R.id.item_click_support);
            if (support != null) {
                support.detach(view);
            }
            return support;
        }

        public MessageActivity.ItemClickSupport setOnItemClickListener(MessageActivity.ItemClickSupport.OnItemClickListener listener) {
            mOnItemClickListener = listener;
            return this;
        }

        public MessageActivity.ItemClickSupport setOnItemLongClickListener(MessageActivity.ItemClickSupport.OnItemLongClickListener listener) {
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
