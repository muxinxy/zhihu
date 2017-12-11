package com.example.zz.zhihu;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ArticleActivity extends AppCompatActivity {
    private String NewsId_intent,like_NewsId_intent,Title_intent,Thumbnail_intent,Url_intent;
    private String username_intent;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MyDatabaseHelper dbHelper;
    private String body,css,CSS;
    private WebView web_article;
    private String hot;
    private boolean collect_article=false;
    private String Url;
    private boolean love_article=false;
    private Menu menu;
    private  String columnId_intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        dbHelper =new MyDatabaseHelper(this,"data.db",null,1) ;
        dbHelper.getWritableDatabase();

        web_article=findViewById(R.id.web_article);
        web_article.getSettings().setJavaScriptEnabled(true);
        web_article.setWebViewClient(new WebViewClient());
        web_article.getSettings().setSupportZoom(false);
        web_article.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        web_article.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        web_article.getSettings().setLoadWithOverviewMode(true);
        web_article.getSettings().setUseWideViewPort(true);
        web_article.getSettings().setDomStorageEnabled(true);
        web_article.getSettings().setAllowFileAccessFromFileURLs(true);
        web_article.getSettings().setAllowUniversalAccessFromFileURLs(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            web_article.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        }
        else
        {
            web_article.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        }

        Toolbar toolbar = findViewById(R.id.toolbar_article);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setTitle("文章");
        }
        Intent intent=getIntent();
        columnId_intent=intent.getStringExtra("columnId_intent");
        username_intent=intent.getStringExtra("username_intent");
        NewsId_intent=intent.getStringExtra("NewsId_intent");
        like_NewsId_intent=intent.getStringExtra("like_NewsId_intent");
        Title_intent=intent.getStringExtra("Title_intent");
        Thumbnail_intent=intent.getStringExtra("Thumbnail_intent");
        Url_intent=intent.getStringExtra("Url_intent");
        hot=intent.getStringExtra("hot");
        if(hot.equals("hot")||hot.equals("message"))
            Url= "http://news-at.zhihu.com/api/2/news/"+NewsId_intent;
        else
            Url = "http://news-at.zhihu.com/api/2/news/"+like_NewsId_intent;

        swipeRefreshLayout=findViewById(R.id.sre_article);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,R.color.colorAccent,R.color.colorButton);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                ( new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        sendRequestWithHttpURLConnection();
                    }
                }, 3000);
            }
        });
        sendRequestWithHttpURLConnection();

        SQLiteDatabase sdb = dbHelper.getReadableDatabase();
        Cursor cursor=sdb.query("like_article_table",null,null,null,null,null,null);
        if (cursor.moveToFirst()) {
            do {
                String username=cursor.getString(cursor.getColumnIndex("username"));
                String article=cursor.getString(cursor.getColumnIndex("news_id"));
                if(hot.equals("hot")){
                    if (username.equals(username_intent)&&article.equals(NewsId_intent)){
                        collect_article=true;
                        break;
                    }
                }
                else {
                    if (username.equals(username_intent)&&article.equals(like_NewsId_intent)){
                        collect_article=true;
                        break;
                    }
                }
            }while (cursor.moveToNext());
        }
        cursor.close();
        sdb.close();

        /*SQLiteDatabase sdb1 = dbHelper.getReadableDatabase();
        Cursor cursor1=sdb1.query("love_article_table",null,null,null,null,null,null);
        if (cursor1.moveToFirst()) {
            do {
                String username=cursor1.getString(cursor1.getColumnIndex("username"));
                String article=cursor1.getString(cursor1.getColumnIndex("news_id"));
                if(hot.equals("hot")){
                    if (username.equals(username_intent)&&article.equals(NewsId_intent)){
                        love_article=true;
                        getMenuInflater().inflate(R.menu.article_menu,menu) ;
                        MenuItem menuItem=menu.findItem(R.id.LoveArticle);
                        menuItem.setIcon(R.drawable.like1);
                        break;
                    }
                }
                else {
                    if (username.equals(username_intent)&&article.equals(like_NewsId_intent)){
                        love_article=true;
                        getMenuInflater().inflate(R.menu.article_menu,menu) ;
                        MenuItem menuItem=menu.findItem(R.id.LoveArticle);
                        menuItem.setIcon(R.drawable.like1);
                        break;
                    }
                }
            }while (cursor1.moveToNext());
        }
        cursor1.close();
        sdb1.close();*/
    }
    private String getHtmlData(String bodyHTML) {
        String head = "<head><style>img{max-width: 100%; width:auto; height: auto;}</style></head>";
        return "<html>" + head + "<body>" + bodyHTML + "</body></html>";
    }
    private void sendRequestWithHttpURLConnection() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL(Url);
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
    /*private void getCSS(final String Css) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL(Css);
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
                    CSS=response.toString();
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
    }*/
    private void parseJSONWithJSONObject(String data) {
        try {
            JSONObject jsonObject=new JSONObject(data);
            body = jsonObject.getString("body");
            String image_source = jsonObject.getString("image_source");
            String title = jsonObject.getString("title");
            String image=jsonObject.getString("image");
            String share_url=jsonObject.getString("share_url");
            String thumbnail=jsonObject.getString("thumbnail");
            String ga_prefix=jsonObject.getString("ga_prefix");
            String id=jsonObject.getString("id");
            JSONArray js=jsonObject.getJSONArray("js");
            JSONArray jsonArray=jsonObject.getJSONArray("css");
            for (int j=0;j<jsonArray.length();j++) {
                css=jsonArray.getString(j);
            }
            showResponse();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        /*String id;
        if(hot.equals("hot"))
            id=NewsId_intent;
        else
            id =like_NewsId_intent;
        getMenuInflater().inflate(R.menu.article_menu, menu);
        MenuItem item = menu.findItem(R.id.LoveArticle);
        if (love_article){
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.delete("love_article_table","news_id=?",new String[]{id});
            db.close();
            love_article=false;
            item.setIcon(R.drawable.like0);
        }else {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("news_id",id);
            values.put("username",username_intent);
            db.insert("love_article_table", null, values);
            values.clear();
            db.close();
            love_article=true;
            item.setIcon(R.drawable.like1);
        }
        return super.onCreateOptionsMenu(menu);*/
        getMenuInflater().inflate(R.menu.article_menu,menu) ;
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String id;
        if(hot.equals("hot")||hot.equals("message"))
            id=NewsId_intent;
        else
            id =like_NewsId_intent;
        switch (item.getItemId()) {
            /*case R.id.LoveArticle:
                if (love_article){
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    db.delete("love_article_table","news_id=?",new String[]{id});
                    db.close();
                    love_article=false;
                    getMenuInflater().inflate(R.menu.article_menu,menu) ;
                    MenuItem menuItem=menu.findItem(R.id.LoveArticle);
                    menuItem.setIcon(R.drawable.like0);
                }else {
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("news_id",id);
                    values.put("username",username_intent);
                    db.insert("love_article_table", null, values);
                    values.clear();
                    db.close();
                    love_article=true;
                    getMenuInflater().inflate(R.menu.article_menu,menu) ;
                    MenuItem menuItem=menu.findItem(R.id.LoveArticle);
                    menuItem.setIcon(R.drawable.like1);
                }
                break;*/
            case R.id.LongCommits:
                Intent intent=new Intent(ArticleActivity.this,LongCommitsActivity.class);
                intent.putExtra("NewsId_intent",NewsId_intent);
                intent.putExtra("like_NewsId_intent",like_NewsId_intent);
                intent.putExtra("hot",hot);
                startActivity(intent);
                break;
            case R.id.ShortCommits:
                Intent intent1=new Intent(ArticleActivity.this,ShortCommitsActivity.class);
                intent1.putExtra("NewsId_intent",NewsId_intent);
                intent1.putExtra("like_NewsId_intent",like_NewsId_intent);
                intent1.putExtra("hot",hot);
                startActivity(intent1);
                break;
            case R.id.Collect_Article:
                if (!collect_article){
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("news_id",id);
                    values.put("username",username_intent);
                    values.put("title",Title_intent);
                    values.put("thumbnail",Thumbnail_intent);
                    values.put("url",Url_intent);
                    db.insert("like_article_table", null, values);
                    values.clear();
                    db.close();
                    collect_article=true;
                    Toast.makeText(ArticleActivity.this,"已收藏",Toast.LENGTH_SHORT).show();
                }else Toast.makeText(ArticleActivity.this,"收藏已存在",Toast.LENGTH_SHORT).show();
                break;
            case R.id.QuitCollect_Article:
                if (collect_article){
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    db.delete("like_article_table","news_id=?",new String[]{id});
                    db.close();
                    collect_article=false;
                    Toast.makeText(ArticleActivity.this,"已取消收藏",Toast.LENGTH_SHORT).show();
                }else Toast.makeText(ArticleActivity.this,"收藏不存在",Toast.LENGTH_SHORT).show();
            default:
        }
        return true;
    }
    private void showResponse(){
        web_article.post(new Runnable() {
            @Override
            public void run() {
                web_article.loadDataWithBaseURL("", getHtmlData(body), "text/html", "UTF-8", "");
            }
        });
    }
    public void onBackPressed() {
        Intent intent1 =new Intent(ArticleActivity.this,HotActivity.class);
        intent1.putExtra("username_intent",username_intent);
        Intent intent2=new Intent(ArticleActivity.this,LikeArticleActivity.class);
        intent2.putExtra("username_intent",username_intent);
        Intent intent3=new Intent(ArticleActivity.this,MessageActivity.class);
        intent3.putExtra("username_intent",username_intent);
        intent3.putExtra("main","main");
        intent3.putExtra("columnId_intent",columnId_intent);
        if(hot.equals("hot"))
            startActivity(intent1);
        else if (hot.equals("message")){
            startActivity(intent3);
        }
        else startActivity(intent2);
        finish();
    }
}
