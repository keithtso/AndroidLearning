package com.example.providertest;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private String newID;

    private static final String BOOK_PROVIDE_URI = "content://com.example.sqldatabasepractice.provider/book";

    private static final String nameKey = "name";
    private static final String authorKey = "author";
    private static final String pagesKey = "page";
    private static final String priceKey = "price";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setUpButtons(R.id.add_data);
        setUpButtons(R.id.update_data);
        setUpButtons(R.id.query_data);
        setUpButtons(R.id.delete_data);
    }

    private void setUpButtons(int buttonID) {
        Button button = findViewById(buttonID);
        button.setOnClickListener(v -> {
            if (buttonID == R.id.add_data) {
                addData();
            } else if (buttonID == R.id.query_data) {
                queryData();
            } else if (buttonID == R.id.delete_data) {
                deleteData();
            } else if (buttonID == R.id.update_data) {
                updateData();
            }
        });
    }

    private void addData() {
        Uri uri = Uri.parse(BOOK_PROVIDE_URI);
        ContentValues values = new ContentValues();
        values.put(nameKey,"Book AAAA");
        values.put(authorKey, "Mr.AAA");
        values.put(pagesKey,1000);
        values.put(priceKey,99.99);
        Uri newUri = getContentResolver().insert(uri,values);
        newID = newUri.getPathSegments().get(1);
    }

    private void queryData() {
        Uri uri = Uri.parse(BOOK_PROVIDE_URI);
        Cursor cursor = getContentResolver().query(uri, null,null,null,null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(nameKey));
                String author = cursor.getString(cursor.getColumnIndex(authorKey));
                int pages = cursor.getInt(cursor.getColumnIndex(pagesKey));
                double price = cursor.getDouble(cursor.getColumnIndex(priceKey));
                Log.d("MainActivity","Bookinfo :"+"name: "+ name + " author: "+author
                        +" pages:"+pages+" price: "+price);
            }
        }
    }

    private void updateData() {
        Uri uri = Uri.parse(BOOK_PROVIDE_URI + "/"+newID);
        ContentValues values = new ContentValues();
        values.put(nameKey,"Strom of Swords");
        values.put(pagesKey,10000);
        values.put(priceKey, 0.99);
        getContentResolver().update(uri,values,null,null);
    }

    private void deleteData() {
        Uri uri = Uri.parse(BOOK_PROVIDE_URI+"/"+newID);
        getContentResolver().delete(uri,null,null);
    }
}