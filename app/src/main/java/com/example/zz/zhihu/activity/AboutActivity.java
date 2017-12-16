package com.example.zz.zhihu.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.zz.zhihu.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        WebView about=findViewById(R.id.about);
        about.getSettings().setJavaScriptEnabled(true);
        about.setWebViewClient(new WebViewClient());
        about.loadUrl("https://github.com/muxinxy/zhihu/blob/master/README.md");
    }
}
