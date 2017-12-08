package com.example.zz.zhihu;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private static final String User="create table user_table("
            + "id integer primary key autoincrement,"
            +"user_image text,"
            + "nickname text,"
            + "username text,"
            + "password text,"
            + "signature text,"
            + "sex text)";

    private static final String Collection="create table like_column_table("
            +"id integer primary key autoincrement,"
            +"username text,"
            +"column_id text,"
            +"name text,"
            +"description text,"
            +"thumbnail text)";

    MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(User);
        db.execSQL(Collection);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists user_table") ;
        db.execSQL("drop table if exists collection_table");
        onCreate(db) ;
    }
}
