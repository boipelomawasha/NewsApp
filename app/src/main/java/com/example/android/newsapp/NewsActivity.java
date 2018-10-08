package com.example.android.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.content.Loader;
import android.app.LoaderManager.LoaderCallbacks;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity implements LoaderCallbacks<List<News>> {

    /**
     * URL for news data from the Guardian dataset
     */
    private final String GUARDIAN_REQUEST_URL = "content.guardianapis.com";

    private static final int NEWS_LOADER_ID = 1;

    //Adapter for list of news items
    private NewsAdapter newsAdapter;

    /**
     * TextView that is displayed when the list is empty
     */
    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_activity);

        // Find a reference to the {@link ListView} in the layout
        ListView newsListView = findViewById(R.id.list);

        mEmptyStateTextView = findViewById(R.id.empty_view);
        newsListView.setEmptyView(mEmptyStateTextView);

        // Create a new adapter that takes an empty list of news items as input
        newsAdapter = new NewsAdapter(this, new ArrayList<News>());

        // Populate user interface
        newsListView.setAdapter(newsAdapter);

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected news item.
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current news item that was clicked on
                News currentNewsItem = newsAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri newsItemUri = Uri.parse(currentNewsItem.getURL());

                // Create a new intent to view the news item URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsItemUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }

    }

    @NonNull
    @Override
    public Loader<List<News>> onCreateLoader(int i, @Nullable Bundle bundle) {

        Uri.Builder uriBuilder = new Uri.Builder();

        uriBuilder.scheme("https")
                .encodedAuthority(GUARDIAN_REQUEST_URL)
                .appendQueryParameter("api-key", "1b4996a8-fa54-4f58-b7ac-2f68cd476e54")
                .appendPath("search")
                .appendQueryParameter("q", getString(R.string.search))
                .appendQueryParameter("section", getString(R.string.section))
                .appendQueryParameter("show-tags", getString(R.string.show_tags));

        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<News>> loader, List<News> news) {

        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // Set empty state text to display "No news available."
        mEmptyStateTextView.setText(R.string.no_news);

        // Clear the adapter of previous news data
        newsAdapter.clear();

        // If there is a valid list of {@link News}, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (news != null && !news.isEmpty()) {
            newsAdapter.addAll(news);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<News>> loader) {
        newsAdapter.clear();
    }
}
