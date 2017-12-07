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
import android.view.MenuItem;
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
import java.util.Collection;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<Column> columnList = new ArrayList<>();
    private DrawerLayout mydrawerLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MyDatabaseHelper dbHelper;
    private ColumnAdapter ColumnAdapter;
    private String username_intent;
    private boolean like=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper =new MyDatabaseHelper(this,"data.db",null,1) ;
        dbHelper.getWritableDatabase();

        Intent intent=getIntent();
        final String username_intent=intent.getStringExtra("username_intent");

        RecyclerView recyclerView = findViewById(R.id.rev_main);
        ColumnAdapter=new ColumnAdapter(columnList);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
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
                    case R.id.nav_collection:
                        Intent intent3=new Intent(MainActivity.this,CollectionActivity.class);
                        startActivity(intent3);
                        break;
                    case R.id.nav_hot:
                        Intent intent4=new Intent(MainActivity.this,HotActivity.class);
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

        ImageView columnLike=findViewById(R.id.column_like);
        /*if (columnLike.getDrawable().getCurrent().getConstantState()==(getResources().getDrawable(R.drawable.like1).getConstantState())){
            like=true;
        }
        if (!like){
            Glide.with(this).load(R.drawable.like1).asBitmap().into(columnLike);
            like=true;
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("like_id",Column.getId());
            db.insert("like_table", null, values);
            values.clear();
            db.close();
        }
        else {
            Glide.with(this).load(R.drawable.like0).asBitmap().into(columnLike);
            like=false;
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.delete("like_table", "like_id=?",new String[]{Column.getId()});
            db.close();
        }*/

    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        switch (requestCode){
            case 1:
                if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    sendRequestWithHttpURLConnection();
                    showResponse();
                }else {
                    Toast.makeText(MainActivity.this,"你拒绝了权限",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    private void sendRequestWithHttpURLConnection() {
        // 开启线程来发起网络请求
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
                    // 下面对获取到的输入流进行读取
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
                // 在这里进行UI操作，将结果显示到界面上
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
        }
        return true;
    }
    protected void onResume(){
        super.onResume();
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
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data){
        switch (requestCode){
            case 1:
                if (requestCode==RESULT_OK){
                    username_intent=data.getStringExtra("username_intent");
                }
                break;
            default:
        }
    }
}
