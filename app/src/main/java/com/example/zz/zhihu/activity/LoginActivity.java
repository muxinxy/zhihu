package com.example.zz.zhihu.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.zz.zhihu.MyDatabaseHelper;
import com.example.zz.zhihu.R;

public class LoginActivity extends AppCompatActivity {
    private MyDatabaseHelper dbHelper;
    private EditText login_username,login_password;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private CheckBox rememberPass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper =new MyDatabaseHelper(this,"data.db",null,1) ;
        dbHelper.getWritableDatabase();

        final Button login = findViewById(R.id.login);
        final Button signup= findViewById(R.id.signup);
        login_username=findViewById(R.id.login_username);
        login_password=findViewById(R.id.login_passward);

        pref= PreferenceManager.getDefaultSharedPreferences(this);
        rememberPass=findViewById(R.id.remember_pass);
        boolean isRemember=pref.getBoolean("remember_password",false);
        if (isRemember){
            String username=pref.getString("username","");
            String password=pref.getString("password","");
            login_username.setText(username);
            login_password.setText(password);
            rememberPass.setChecked(true);
        }

        Toolbar toolbar = findViewById(R.id.toolbar_login);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.drawable.zhihu);
        toolbar.setTitle("");
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setTitle("");
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (check1()) {
                    if (check2()){
                        editor=pref.edit();
                        if (rememberPass.isChecked()){
                            editor.putBoolean("remember_password",true);
                            editor.putString("username",login_username.getText().toString());
                            editor.putString("password",login_password.getText().toString());
                        }else {
                            editor.clear();
                        }
                        editor.apply();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("username_intent",login_username.getText().toString().trim());
                        startActivity(intent);
                        Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                        finish();
                    }else Toast.makeText(LoginActivity.this, "账号或密码错误", Toast.LENGTH_SHORT).show();
                }
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private boolean check1(){
        if(login_username.getText().toString().trim().equals("")){
            Toast.makeText(LoginActivity.this, "账号不能为空", Toast.LENGTH_SHORT).show();
        }else if(login_password.getText().toString().trim().equals("")){
            Toast.makeText(LoginActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
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
                String password=cursor.getString(cursor.getColumnIndex("password"));
                if (username.equals(login_username.getText().toString().trim())&&password.equals(login_password.getText().toString().trim())){
                    return true;
                }
            }while (cursor.moveToNext());
        }
        cursor.close();
        return false;
    }
}
