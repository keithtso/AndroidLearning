package com.example.sqldatabasepractice;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class MySQLHelper extends SQLiteOpenHelper {
    private static final String CREATE_BOOK = "create table Book ("
            +"id integer primary key autoincrement, "
            +"author text, "
            +"price real, "
            +"page integer, "
            +"name text)";

    private static final String CREAT_CATEGORY = "create table category("
            +"id integer primary key autoincrement, "
            +"category_name text, "
            +"category_code integer)";

    private Context mContext;
    public MySQLHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory,version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_BOOK); // 创建表格
        db.execSQL(CREAT_CATEGORY); // 创建分类
//        Toast.makeText(mContext, "Create successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists Book");
        db.execSQL("drop table if exists Category");
        onCreate(db);
    }
}
