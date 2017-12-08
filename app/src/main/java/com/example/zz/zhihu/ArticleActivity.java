package com.example.zz.zhihu;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class ArticleActivity extends AppCompatActivity {
    private String News_id_intent;
    private String username_intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        Toolbar toolbar = findViewById(R.id.toolbar_article);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setTitle("文章");
        }
        Intent intent=getIntent();
        username_intent=intent.getStringExtra("username_intent");
        News_id_intent=intent.getStringExtra("News_id_intent");

    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.article_menu,menu) ;
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.LongCommits:
                Intent intent=new Intent(ArticleActivity.this,LongCommitsActivity.class);
                intent.putExtra("News_id_intent",News_id_intent);
                startActivity(intent);
                break;
            case R.id.ShortCommits:
                Intent intent1=new Intent(ArticleActivity.this,ShortCommitsActivity.class);
                intent1.putExtra("News_id_intent",News_id_intent);
                startActivity(intent1);
            default:
        }
        return true;
    }
}
