package com.example.servicetest;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class MyService extends Service {

    private DownloadBinder mBinder = new DownloadBinder();

    public MyService() {

    }

    @Override
    public IBinder onBind(Intent intent) {

        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("MyService", "onCreate: Service started");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("MyService", "onStartCommand: start command");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d("MyService", "onDestroy: destroyed");
        super.onDestroy();
    }

    static class DownloadBinder extends Binder {
        public void startDownload() {
            Log.d("MyService", "startDownload: start download");

        }

        public int getProgress() {
            Log.d("MyService", "getProgress: progress:");
            return 0;
        }
    }
}