package com.example.servicetest;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private MyService.DownloadBinder downloadBinder;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downloadBinder = (MyService.DownloadBinder)service;
            downloadBinder.startDownload();
            downloadBinder.getProgress();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

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

        Button startButt = findViewById(R.id.start_service_button);
        startButt.setOnClickListener(this);
        Button stopButt = findViewById(R.id.stop_service_button);
        stopButt.setOnClickListener(this);

        Button bindButt = findViewById(R.id.bind_service_button);
        bindButt.setOnClickListener(this);

        Button unbindButt = findViewById(R.id.unbind_service_button);
        unbindButt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int viewID = v.getId();
        if (viewID == R.id.start_service_button) {
            Intent startIntent = new Intent(this, MyService.class);
            startService(startIntent);
        } else if (viewID == R.id.stop_service_button ) {
            Intent stopIntent = new Intent(this, MyService.class);
            stopService(stopIntent);
        } else if (viewID == R.id.bind_service_button ) {
            Intent bindIntent = new Intent(this, MyService.class);
            bindService(bindIntent, serviceConnection, BIND_AUTO_CREATE);
        } else if (viewID == R.id.unbind_service_button ) {
            unbindService(serviceConnection);
        }
    }
}