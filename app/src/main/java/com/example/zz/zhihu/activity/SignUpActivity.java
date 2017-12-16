package com.example.zz.zhihu.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.zz.zhihu.MyDatabaseHelper;
import com.example.zz.zhihu.R;

public class SignUpActivity extends AppCompatActivity {
    private MyDatabaseHelper dbHelper ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        dbHelper =new MyDatabaseHelper(this,"data.db",null,1) ;
        dbHelper.getWritableDatabase();

        final Button sign_yes= findViewById(R.id.sign_yes);
        final Button sign_no = findViewById(R.id.sign_no);
        final EditText sign_nickname=findViewById(R.id.sign_nickname);
        final EditText sign_username=findViewById(R.id.sign_username);
        final EditText sign_password=findViewById(R.id.sign_password);
        final EditText re_sign_password=findViewById(R.id.re_sign_password);
        Toolbar toolbar = findViewById(R.id.toolbar_sign_up);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.drawable.zhihu);
        toolbar.setTitle("");
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setTitle("");
        }


        sign_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        sign_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check1()&&!check2()) {
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("nickname",sign_nickname.getText().toString().trim());
                    values.put("username", sign_username.getText().toString().trim());
                    values.put("password", sign_password.getText().toString().trim());
                    values.put("sex","m");
                    db.insert("user_table", null, values);
                    values.clear();
                    Toast.makeText(SignUpActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                    intent.putExtra("username_intent",sign_username.getText().toString().trim());
                    startActivity(intent);
                    finish();
                }
            }
            private boolean check1(){
                if (sign_username.getText().toString().contains(" ")||sign_password.getText().toString().contains(" ")||re_sign_password.getText().toString().contains(" ")){
                    Toast.makeText(SignUpActivity.this,"不能包含空格",Toast.LENGTH_SHORT).show();
                }else if (sign_nickname.getText().toString().trim().equals("")){
                    Toast.makeText(SignUpActivity.this, "昵称不能为空", Toast.LENGTH_SHORT).show();
                }else if(sign_username.getText().toString().trim().equals("")){
                    Toast.makeText(SignUpActivity.this, "账号不能为空", Toast.LENGTH_SHORT).show();
                }else if(sign_password.getText().toString().trim().equals("")||re_sign_password.getText().toString().trim().equals("")){
                    Toast.makeText(SignUpActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                }else if(!sign_password.getText().toString().trim().equals(re_sign_password.getText().toString().trim())) {
                    Toast.makeText(SignUpActivity.this, "两次密码输入不一致", Toast.LENGTH_SHORT).show();
                }else{
                    return true;
                }
                return false;
            }
            private boolean check2() {
                SQLiteDatabase sdb = dbHelper.getReadableDatabase();
                Cursor cursor=sdb.query("user_table",null,null,null,null,null,null);
                if (cursor.moveToFirst()) {
                    do {
                        String username=cursor.getString(cursor.getColumnIndex("username"));
                        String nickname=cursor.getString(cursor.getColumnIndex("nickname"));
                        if (username.equals(sign_username.getText().toString().trim())){
                            Toast.makeText(SignUpActivity.this,"账号已存在",Toast.LENGTH_SHORT).show();
                            return true;
                        }else if (nickname.equals(sign_nickname.getText().toString().trim())){
                            Toast.makeText(SignUpActivity.this,"昵称已存在",Toast.LENGTH_SHORT).show();
                            return true;
                        }
                    }while (cursor.moveToNext());
                }
                cursor.close();
                sdb.close();
                return false;
            }
        });
    }
    public void onBackPressed() {
        Intent intent=new Intent(SignUpActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
