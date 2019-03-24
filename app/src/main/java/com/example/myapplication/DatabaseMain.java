package com.example.myapplication;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class DatabaseMain extends MenuDemo {

    private TextView t;
  @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_main);
        ContentValues val;
        SQLiteDatabase db;


      t = (TextView) findViewById(R.id.textt);

      DatabaseSQLite obj=new DatabaseSQLite(this);
//        db=obj.getWritableDatabase();
//        val=new ContentValues();
//
//        val.put("gps","ABC");
//        long row=db.insert("first",null,val);
//        Toast.makeText(this,"Clicjed    "+row, Toast.LENGTH_SHORT).show();
//
//        System.out.print("Output"+row);

        db=obj.getReadableDatabase();

        String projection[]={"time","lat","longi"};

        Cursor c=db.query("first1",projection,null,null,null,null,null);


          c.moveToPosition(0);

          do {
              c.getString(0);
              t.append("\n " + c.getString(0) + "   " + c.getString(1) + "   " + c.getString(2));
          }
          while (c.moveToNext());

    }
}