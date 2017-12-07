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

    private static final String Collection="create table collection_table("
            +"id integer primary key autoincrement,"
            +"username text,"
            +"collection_article text,"
            +"collection_column text)";

    private static final String Like="create table like_table("
            +"id integer primary key autoincrement,"
            +"like_article text,"
            +"like_column text)";

    MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(User);
        db.execSQL(Collection);
        db.execSQL(Like);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists user_table") ;
        db.execSQL("drop table if exists collection_table");
        db.execSQL("drop table if exists like_table");
        onCreate(db) ;
    }
}
