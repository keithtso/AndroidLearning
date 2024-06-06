package com.example.broadcasttest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private IntentFilter intentFilter;
    private NetworkChangeReceiver networkChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

//        intentFilter = new IntentFilter();
//        intentFilter.addAction("android.net.com.CONNECTIVITY_CHANGE");
//        networkChangeReceiver = new NetworkChangeReceiver();
////        registerReceiver(networkChangeReceiver, intentFilter);
//        registerReceiver(networkChangeReceiver,intentFilter, RECEIVER_EXPORTED);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(v -> {
            Intent intent = new Intent("com.example.broadcasttest.test.MY_BROADCAST");
            // Note: 发送broadcast的inter需要设置包名
//            intent.setPackage(getPackageName());
            intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            v.getContext().sendBroadcast(intent);
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkChangeReceiver);
    }

    static class  NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isAvailable()) {
                Toast.makeText(context, "network available", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "network not available", Toast.LENGTH_SHORT).show();
            }


        }
    }
}