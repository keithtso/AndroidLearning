package com.example.servicebesttest;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadTask extends AsyncTask<String, Integer, Integer> {

   public static final int TYPE_SUCCESS = 0;
    public static final int TYPE_FAILED = 1;
    public static final int TYPE_PAUSED = 2;
    public static final int TYPE_CANCELED = 3;

    private DownloadListener listener;

    private boolean isCanceled = false;

    private boolean isPaused = false;

    private int lastProgress;

    public DownloadTask(DownloadListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        Log.d("ddd", "onPreExecute: pre");
        super.onPreExecute();
    }

    // 后台更新活动
    @Override
    protected Integer doInBackground(String... strings) {
        InputStream is = null;
        RandomAccessFile savedFile = null;
        File file = null;
        Log.d("ddd", "doInBackground: do in background");
        try {
            long downloadLength = 0;
            String downloadURL = strings[0];
            String fileName = downloadURL.substring(downloadURL.lastIndexOf("/"));
            String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
            Log.d("ddd", "doInBackground: file name:"+dir+fileName);
            file = new File(dir+fileName);
            if (file.exists()) {
                Log.d("ddd", "doInBackground: file exist");
                downloadLength = file.length();
            } else {
                Log.d("ddd", "doInBackground: file not exist");
            }
            long contentLength = getContentLength(downloadURL);
            if (contentLength == 0) {
                Log.d("ddd", "doInBackground: contentlength 0");
                return TYPE_FAILED;
            } else if (contentLength == downloadLength) {
                Log.d("ddd", "doInBackground: contentlength"+contentLength+" dowloadlength "+downloadLength);
                return TYPE_SUCCESS;
            }
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .addHeader("RANGE","bytes="+downloadLength+"-") //断点续传
                    .url(downloadURL).build();
            Response response = client.newCall(request).execute();
            if (response != null) {
                is = response.body().byteStream();
                savedFile = new RandomAccessFile(file,"rw");
                savedFile.seek(downloadLength);// 跳过已下载的字节
                byte[] bytes = new byte[1024];
                int total = 0;
                int len;
                while ((len = is.read(bytes)) != -1) {
                    if (isCanceled) {
                        return TYPE_CANCELED;
                    } else if (isPaused) {
                        return TYPE_PAUSED;
                    } else {
                        total += len;
                        savedFile.write(bytes,0,len);
                        int progress = (int) ((total + downloadLength) * 100 / contentLength);
                        // 触发界面更新
                        publishProgress(progress);
                    }
                }

                response.body().close();
                return TYPE_SUCCESS;
            }
        }catch (Exception e) {
            Log.d("ddd", "doInBackground: exception");
            e.printStackTrace();
        }finally {
            try {
                if (is != null) {
                    is.close();
                }

                if (savedFile != null) {
                    savedFile.close();
                }

                if (isCanceled && file != null) {
                    file.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }



        return TYPE_FAILED;
    }

    private long getContentLength(String downloadURL) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(downloadURL).build();
        Response response = client.newCall(request).execute();
        if (response != null && response.isSuccessful()) {
            long contentLength = response.body().contentLength();
            response.close();
            return contentLength;
        }

        return 0;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        int progress = values[0];
        if (progress > lastProgress) {
            listener.onProgress(progress);
            lastProgress = progress;
        }
        Log.d("ddd", "onProgressUpdate: update progress:" + lastProgress);

    }

    //通知下载结果
    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        switch (integer) {
            case TYPE_SUCCESS:
                listener.onSuccess();
                break;
            case TYPE_FAILED:
                listener.onFailed();
                break;
            case TYPE_PAUSED:
                listener.onPaused();
                break;
            case TYPE_CANCELED:
                listener.onCanceled();
                break;
            default:
                break;
        }
    }

    public void pauseDownload() {
        isPaused = true;
    }

    public void cancelDownload() {
        isCanceled = true;
    }
}
