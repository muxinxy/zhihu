package com.example.zz.zhihu;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class IndexActivity extends AppCompatActivity {
    private SQLiteOpenHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        FloatingActionButton fab=findViewById(R.id.fab);

        dbHelper =new MyDatabaseHelper(this,"data.db",null,1) ;
        dbHelper.getWritableDatabase();

        Intent intent=getIntent();
        final String username_intent=intent.getStringExtra("username_intent");

        final TextView index_nickname=findViewById(R.id.index_nickname);
        final TextView index_signature=findViewById(R.id.index_signature);
        final TextView index_username=findViewById(R.id.index_username);
        final ImageView index_head=findViewById(R.id.head_index);
        final TextView index_sex=findViewById(R.id.index_sex);
        final Button index_like_column=findViewById(R.id.index_like_column);
        final Button index_like_article=findViewById(R.id.index_like_article);

        SQLiteDatabase sdb = dbHelper.getReadableDatabase();
        Cursor cursor=sdb.query("user_table",null,null,null,null,null,null);
        if (cursor.getCount()!=0&&cursor.moveToFirst()) {
            do {
                String username=cursor.getString(cursor.getColumnIndex("username"));
                String nickname=cursor.getString(cursor.getColumnIndex("nickname"));
                String header=cursor.getString(cursor.getColumnIndex("user_image"));
                String signature=cursor.getString(cursor.getColumnIndex("signature"));
                String sex=cursor.getString(cursor.getColumnIndex("sex"));
                if (username.equals(username_intent)){
                    index_username.setText(username);
                    index_nickname.setText(nickname);
                    index_signature.setText(signature);
                    if(sex.equals("m"))index_sex.setText("男");
                    else index_sex.setText("女");
                    Glide.with(this).load(header).asBitmap().placeholder(R.drawable.zhihu).error(R.drawable.zhihu).into(index_head);
                    break;
                }
            }while (cursor.moveToNext());
        }
        cursor.close();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(IndexActivity.this,PersonActivity.class);
                intent.putExtra("username_intent",username_intent);
                startActivity(intent);
                finish();
            }
        });

        index_like_column.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(IndexActivity.this,LikeColumnActivity.class);
                intent.putExtra("username_intent",username_intent);
                startActivity(intent);
            }
        });

        index_like_article.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(IndexActivity.this,LikeArticleActivity.class);
                intent.putExtra("username_intent",username_intent);
                startActivity(intent);
            }
        });
    }
    @Override
    public void onBackPressed() {
        TextView username=findViewById(R.id.index_username);
        Intent intent =new Intent(IndexActivity.this,MainActivity.class);
        intent.putExtra("username_intent",username.getText().toString());
        startActivity(intent);
        finish();
    }
}
