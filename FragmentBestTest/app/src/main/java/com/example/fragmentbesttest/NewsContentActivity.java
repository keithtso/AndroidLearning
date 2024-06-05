package com.example.fragmentbesttest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class NewsContentActivity extends AppCompatActivity {

    public static  final String NEWS_TITLE_KEY = "news_title";
    public static  final String NEWS_CONTENT_KEY = "news_content";

    public static void actionStart(Context context, String newsTitle, String newsContent) {
        Intent intent = new Intent(context, NewsContentActivity.class);
        intent.putExtra(NEWS_TITLE_KEY,newsTitle);
        intent.putExtra(NEWS_CONTENT_KEY,newsContent);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_content);
        String newsTitle = getIntent().getStringExtra(NEWS_TITLE_KEY);
        String newsContent = getIntent().getStringExtra(NEWS_CONTENT_KEY);

        NewsContentFragment newsContentFragment = (NewsContentFragment) getSupportFragmentManager().findFragmentById(R.id.news_content_fragment);

        if (newsContentFragment != null) {
            newsContentFragment.refresh(newsTitle,newsContent);
        }

    }
}