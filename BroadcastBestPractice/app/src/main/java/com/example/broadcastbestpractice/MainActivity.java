package com.example.broadcastbestpractice;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        Button forceOffline = findViewById(R.id.force_offline);
        forceOffline.setOnClickListener(v -> {
            Intent intent = new Intent("com.example.broadcastbestpractice.OFFLINE");
            sendBroadcast(intent);
        });
    }
}