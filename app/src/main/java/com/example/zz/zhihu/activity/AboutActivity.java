package com.example.zz.zhihu.activity;

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.zz.zhihu.R;

public class AboutActivity extends AppCompatActivity {
    private ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        pd = ProgressDialog.show(AboutActivity.this, "关于", "加载中，请稍后……");
        WebView about=findViewById(R.id.about);
        about.getSettings().setJavaScriptEnabled(true);
        about.setWebViewClient(new WebViewClient());
        about.loadUrl("https://github.com/muxinxy/zhihu/blob/master/README.md");
        handler.sendEmptyMessage(0);
    }
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {// handler接收到消息后就会执行此方法
            pd.dismiss();// 关闭ProgressDialog
        }
    };
}
