package com.example.myapplication;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class MenuDemo extends AppCompatActivity {

    ContentValues val;
    SQLiteDatabase  db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_demo);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater= getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
       return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {



        DatabaseSQLite obj=new DatabaseSQLite(this);
        db=obj.getWritableDatabase();


        int id=item.getItemId();

        if(id==R.id.Settings)
        {
            Toast.makeText(this,"Home", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this,MainActivity.class));

        }
        else if(id==R.id.view)
        {
            Toast.makeText(this,"Database", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this,DatabaseMain.class));

        }
        else if(id==R.id.dump)
        {
            //      obj.onDelete(db);


            Toast.makeText(this, "Offline Communication Activated", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, wifiMVC.class));

        }
        else if(id==R.id.map)
        {

            Toast.makeText(this,"Post-Route Builder Activated", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this,mapinit.class));


        }

        return super.onOptionsItemSelected(item);
    }
}