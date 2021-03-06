package com.example.zz.zhihu.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.zz.zhihu.HtmlUtil;
import com.example.zz.zhihu.R;
import com.example.zz.zhihu.article;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class ArticleActivity extends AppCompatActivity {
    private SwipeRefreshLayout swipeRefreshLayout;
    private String body;
    private List<String> css;
    private WebView web_article;
    private String Url,NewsId_intent;
    private ProgressDialog pd;

    @SuppressLint({"SetJavaScriptEnabled", "ObsoleteSdkInt"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        web_article = findViewById(R.id.web_article);
        web_article.getSettings().setJavaScriptEnabled(true);
        web_article.setWebViewClient(new WebViewClient());
        web_article.getSettings().setSupportZoom(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            web_article.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        } else {
            web_article.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        }

        Toolbar toolbar = findViewById(R.id.toolbar_article);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("文章");
        }
        Intent intent = getIntent();
        NewsId_intent = intent.getStringExtra("NewsId_intent");
            Url = "http://news-at.zhihu.com/api/2/news/" + NewsId_intent;
        swipeRefreshLayout = findViewById(R.id.sre_article);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent, R.color.colorButton);
        swipeRefreshLayout.setProgressViewEndTarget (false,300);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sendRequestWithHttpURLConnection();
                    }
                }, 1000);
            }
        });
        sendRequestWithHttpURLConnection();
    }

    /*private String getHtmlData(String bodyHTML) {
        String head = "<head><style>img{max-width: 100%; width:auto; height: auto;}</style></head>";
        return "<html>" + head + "<body>" + bodyHTML + "</body></html>";
    }*/

    private void sendRequestWithHttpURLConnection() {
        pd = ProgressDialog.show(ArticleActivity.this, "文章", "加载中，请稍后……");
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
                    handler.sendEmptyMessage(0);
                    parseJSONWithJSONObject(response.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    //handler.sendEmptyMessage(8000);
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
            Gson gson=new Gson();
            article article=gson.fromJson(data, com.example.zz.zhihu.article.class);
            body=article.getBody();
            css=article.getCss();
            showResponse();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.article_menu, menu);
            return true;
        }
        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.LongCommits:
                    Intent intent=new Intent(ArticleActivity.this,LongCommitsActivity.class);
                    intent.putExtra("NewsId_intent",NewsId_intent);
                    startActivity(intent);
                    break;
                case R.id.ShortCommits:
                    Intent intent1=new Intent(ArticleActivity.this,ShortCommitsActivity.class);
                    intent1.putExtra("NewsId_intent",NewsId_intent);
                    startActivity(intent1);
                    break;
                default:
            }
            return true;
        }
    private void showResponse() {
        web_article.post(new Runnable() {
            @Override
            public void run() {
                String htmlData = HtmlUtil.createHtmlData(body, css);
                web_article.loadDataWithBaseURL("", htmlData, "text/html", "UTF-8", "");
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // handler接收到消息后就会执行此方法
            pd.dismiss();// 关闭ProgressDialog
        }
    };
}
