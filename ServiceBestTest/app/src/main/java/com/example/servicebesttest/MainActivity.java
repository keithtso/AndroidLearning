package com.example.servicebesttest;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.concurrent.Executor;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private DownloadService.DownloadBinder downloadBinder;

    private static final String DOWNLOAD_URL = "https://raw.githubusercontent.com/guolindev/eclipse/master/eclipse-inst-win64.exe";

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downloadBinder = (DownloadService.DownloadBinder) service;
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

        // setup buttons
        Button start_Download = findViewById(R.id.start_download);
        start_Download.setOnClickListener(this);
        Button pause_Download = findViewById(R.id.pause_download);
        pause_Download.setOnClickListener(this);
        Button cancel_Download = findViewById(R.id.cancel_download);
        cancel_Download.setOnClickListener(this);

        // start service
        Intent intent = new Intent(this, DownloadService.class);
        startService(intent);

        //bind Service
        bindService(intent,connection,BIND_AUTO_CREATE);

        // check permission
        if ((ContextCompat.checkSelfPermission(MainActivity.this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE))
            != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new  String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
        }

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.start_download) {
            downloadBinder.startDownload(DOWNLOAD_URL);
        } else if (id == R.id.pause_download) {
            downloadBinder.pauseDownload();
        } else if (id == R.id.cancel_download) {
            downloadBinder.cancelDownload();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "No permission and App will terminate", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }
}