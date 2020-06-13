package com.maged.elmagdnews;

import androidx.appcompat.app.AppCompatActivity;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {
    private static final int NEWS_LOADER_ID = 1;
    private static final String GUARDIANS_REQUEST_URL = "https://content.guardianapis.com/search?q=debate&tag=politics/politics&from-date=2014-01-01&api-key=test&show-fields=thumbnail&show-tags=contributor";
    private static final String GUARDIANS_GAMES_URL = "http://content.guardianapis.com/search?q=debates&api-key=test";
    private NewsAdapter newsAdapter;
    private ListView newsListView;
    private TextView emptyStateTextView;
    private View loadingIndicator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        emptyStateTextView = findViewById(R.id.empty_view);
        loadingIndicator = findViewById(R.id.loading_indicator);
        newsListView = findViewById(R.id.news_list);

        newsAdapter = new NewsAdapter(this, new ArrayList<News>());

        newsListView.setAdapter(newsAdapter);

        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (cm != null) {
            networkInfo = cm.getActiveNetworkInfo();
        }
        if(networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        }else{
            loadingIndicator.setVisibility(View.GONE);
            emptyStateTextView.setText(getString(R.string.no_internet_connection));
            emptyStateTextView.setVisibility(View.VISIBLE);
        }
/* news List ClickListener */
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                News currentNews = newsAdapter.getItem(position);
                if (currentNews != null) {
                    String webUrl = currentNews.getmArticleUrl();
                    Intent webIntent = new Intent(Intent.ACTION_VIEW);
                    if(webUrl != null){
                        webIntent.setData(Uri.parse(webUrl));
                    }else{
                        webIntent.setData(Uri.parse(GUARDIANS_GAMES_URL));
                    }
                    startActivity(webIntent);
                }
            }
        });
    }
/*CreateLoader*/
    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {
        return new NewsLoader(this, GUARDIANS_REQUEST_URL);
    }
/*onLoadFinished*/
    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> news) {

        loadingIndicator.setVisibility(View.GONE);
        emptyStateTextView.setText(getString(R.string.no_news_found));
        newsListView.setEmptyView(emptyStateTextView);
        newsAdapter.clear();
        if(news != null && !news.isEmpty()){
            newsAdapter.addAll(news);
        }

    }
/*onLoaderReset*/
    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
    }
}