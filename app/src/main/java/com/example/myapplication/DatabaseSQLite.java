package com.example.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseSQLite extends SQLiteOpenHelper {

    public DatabaseSQLite(Context context) {
        super(context, "gps1.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table first1 (time TEXT,lat TEXT,longi TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void onDelete(SQLiteDatabase db)
    {

        db.delete("first1",null,null);
    }
}
