package com.example.fragmentbesttest;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class NewsTitleFragment extends Fragment {
    private boolean isTwoPane;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.news_title_frag,container,false);

        RecyclerView newsTitleRecycleView = view.findViewById(R.id.news_title_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        newsTitleRecycleView.setLayoutManager(layoutManager);

        NewsAdapter adapter = new NewsAdapter(getNews());
        newsTitleRecycleView.setAdapter(adapter);

        return view;
    }

    private List<News> getNews() {
        List<News> newsList = new ArrayList<>();
        for (int i = 0; i <= 50; i++) {
            News news = new News();
            news.setTitle("News title num: " + i);
            news.setContent(getRandomLengthContent("Random length Content"+i+"."));
            newsList.add(news);
        }
        return newsList;
    }

    private String getRandomLengthContent(String content) {
        Random random = new Random();
        int length = random.nextInt(20) + 1;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append(content);
        }
        return builder.toString();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        isTwoPane = view.findViewById(R.id.news_title_frag_sw600) != null;
    }

    class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

        private final List<News> mNewsList;
        class ViewHolder extends RecyclerView.ViewHolder {
            TextView newsTitleText;

            public ViewHolder(View view) {
                super(view);
                newsTitleText = view.findViewById(R.id.news_title);
            }

        }

        public NewsAdapter(List<News> newsList) {
            mNewsList = newsList;
        }

        @NonNull
        @Override
        public NewsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item, parent, false);
            final ViewHolder viewHolder = new ViewHolder(view);

            view.setOnClickListener(v -> {
                News news = mNewsList.get(viewHolder.getBindingAdapterPosition());
                if (isTwoPane) {
                    NewsContentFragment newsContentFragment = (NewsContentFragment) getParentFragmentManager().findFragmentById(R.id.news_content_fragment);
                    if (newsContentFragment != null) {
                        newsContentFragment.refresh(news.getTitle(), news.getContent());
                    }
                } else {
                    NewsContentActivity.actionStart(getActivity(),news.getTitle(),news.getContent());
                }
            });

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull NewsAdapter.ViewHolder holder, int position) {
            News news = mNewsList.get(position);
            holder.newsTitleText.setText(news.getTitle());
        }

        @Override
        public int getItemCount() {
            return mNewsList.size();
        }
    }
}
