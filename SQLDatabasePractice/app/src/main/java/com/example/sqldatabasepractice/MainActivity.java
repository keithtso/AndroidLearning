package com.example.sqldatabasepractice;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.ColorSpace;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;

import org.litepal.tablemanager.Connector;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private MySQLHelper sqlHelper;

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

        setupSQLHelper();
        Button button = findViewById(R.id.create_db);
        button.setOnClickListener(v -> {
//            sqlHelper.getWritableDatabase(); // sqlite
            Connector.getDatabase(); // litepal
        });

        Button addButton = findViewById(R.id.db_add_data);
        addButton.setOnClickListener(v -> {
//            addData();
            addDataByLitePal();
        });

    }

    private void addDataByLitePal() {
        Book book = new Book();
        book.setName("Book AAA");
        book.setAuthor("AAA");
        book.setPages(46);
        book.setPrice(99);
        book.setPress("Unknown");
        book.save();
    }

    private void setupSQLHelper() {
        sqlHelper = new MySQLHelper(this,"BOOKSTORE.db",null,2);
    }

    private void addData() {
        SQLiteDatabase database = sqlHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name","BOOK A");
        values.put("author","A");
        values.put("page", 500);
        values.put("price", 90.99);
        database.insert("Book", null, values);
        values.clear();
        values.put("name","BOOK B");
        values.put("author","B");
        values.put("page", 400);
        values.put("price", 190.99);
        database.insert("Book", null, values);
    }
}