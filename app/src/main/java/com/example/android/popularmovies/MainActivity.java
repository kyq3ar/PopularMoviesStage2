package com.example.android.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.FavoriteAdapter;
import com.example.android.popularmovies.data.FavoriteContract;
import com.example.android.popularmovies.data.FavoriteDbHelper;
import com.example.android.popularmovies.utilities.MovieJsonUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements MovieAdapter.MovieAdapterOnClickHandler,
        FavoriteAdapter.FavoriteAdapterOnClickHandler {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int MOVIE_LOADER = 100;
    private static final int FAVORITE_LOADER = 200;
    private static final String MOVIE_DATA_URL = "movieurl";
    private static final String SORT_ORDER = "sortorder";

    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;
    private FavoriteAdapter mFavoriteAdapter;

    private TextView mErrorMessageDisplay;

    private ProgressBar mLoadingIndicator;

    private String sortOrder;
    private String movieDataURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movies);
        mErrorMessageDisplay = (TextView) findViewById(R.id.error_message);

        GridLayoutManager layoutManager
                = new GridLayoutManager(this, 4);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter(this);
        mFavoriteAdapter = new FavoriteAdapter(this);
        mRecyclerView.setAdapter(mMovieAdapter);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        if(savedInstanceState!=null){
            movieDataURL = savedInstanceState.getString(MOVIE_DATA_URL);
            sortOrder = savedInstanceState.getString(SORT_ORDER);
            if(sortOrder.equals("popular")){
                setTitle("Popular");
                getSupportLoaderManager().restartLoader(MOVIE_LOADER, null, movieResultLoaderListener);
                mRecyclerView.setAdapter(mMovieAdapter);
            }
            else if(sortOrder.equals("top_rated")) {
                setTitle("Top-Rated");
                getSupportLoaderManager().restartLoader(MOVIE_LOADER, null, movieResultLoaderListener);
                mRecyclerView.setAdapter(mMovieAdapter);
            }
            else{
                setTitle("Favorites");
                mRecyclerView.setAdapter(mFavoriteAdapter);
                getSupportLoaderManager().restartLoader(FAVORITE_LOADER, null, favoriteResultLoaderListener);
            }

        }
        else{
            sortOrder = "popular";
            setTitle("Popular");
            if(isOnline())
                loadMovieData();
            else{
                showErrorMessage();
            }

        }
        getSupportLoaderManager().initLoader(MOVIE_LOADER, null, movieResultLoaderListener);
        getSupportLoaderManager().initLoader(FAVORITE_LOADER, null, favoriteResultLoaderListener);
    }

    private void loadMovieData() {
        Bundle queryBundle = new Bundle();

        URL movieDataUrl = NetworkUtils.buildUrl(sortOrder);
        queryBundle.putString(MOVIE_DATA_URL, movieDataUrl.toString());
        queryBundle.putString(SORT_ORDER, sortOrder);

        android.support.v4.app.LoaderManager loaderManager = getSupportLoaderManager();

        if (loaderManager== null) {
            loaderManager.initLoader(MOVIE_LOADER, queryBundle, movieResultLoaderListener);
        } else {
            loaderManager.restartLoader(MOVIE_LOADER, queryBundle, movieResultLoaderListener);
        }
    }

    @Override
    public void onClick(String movie) {
        Context context = this;
        Class destinationClass = DetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra(Intent.EXTRA_TEXT, movie);
        startActivity(intentToStartDetailActivity);
    }

    private void showMovieDataView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }
    private LoaderManager.LoaderCallbacks<String> movieResultLoaderListener
            = new LoaderManager.LoaderCallbacks<String>() {
        @Override
        public Loader<String> onCreateLoader(int id, final Bundle args) {
            return new AsyncTaskLoader<String>(getApplicationContext()) {
                @Override
                protected void onStartLoading() {
                    if (args == null) {
                        return;
                    }
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                }

                @Override
                public String loadInBackground() {
                    String dataURL = args.getString(MOVIE_DATA_URL);

                    if (dataURL == null || TextUtils.isEmpty(dataURL)) {
                        return null;
                    }

                    try {
                        //if(screenType.equals("main")){
                        URL movieURL = new URL(dataURL);
                        String jsonMovieResponse = NetworkUtils
                                .getResponseFromHttpUrl(movieURL);
                        return jsonMovieResponse;
                    /*}
                    else {
                        Context context = getApplicationContext();
                        Class destinationClass = DetailActivity.class;
                        Intent intentToStartDetailActivity = new Intent(context, destinationClass);

                        String jsonDetailResponse = args.getString(MOVIE_DATA_URL);

                        String[] data = jsonDetailResponse.split("\\n");
                        intentToStartDetailActivity.putExtra(jsonDetailResponse, data[1]);
                        startActivity(intentToStartDetailActivity);
                    }
                    return null;*/

                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<String> loader, String data) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);

            if (null == data) {
                showErrorMessage();
            } else {
                try{
                    String[] information = MovieJsonUtils
                            .getMovieInformationFromJson(MainActivity.this, data);
                    mMovieAdapter.setMovieData(information);
                    showMovieDataView();
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onLoaderReset(Loader<String> loader) {

        }
    };

    private LoaderManager.LoaderCallbacks<Cursor> favoriteResultLoaderListener
            = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, final Bundle args) {
            return new AsyncTaskLoader<Cursor>(getApplicationContext()) {

                Cursor mFavoriteData = null;
                @Override
                protected void onStartLoading() {
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    if (mFavoriteData != null) {
                        deliverResult(mFavoriteData);
                    }
                    else {
                        forceLoad();
                    }
                }
                @Override
                public Cursor loadInBackground() {
                    try {
                        return getContentResolver().query(FavoriteContract.FavoriteEntry.CONTENT_URI,
                                null,
                                null,
                                null,
                                null);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }

                public void deliverResult(Cursor data){
                    mFavoriteData = data;
                    super.deliverResult(data);
                }

            };
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            mFavoriteAdapter.swapCursor(data);
            mLoadingIndicator.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mFavoriteAdapter.swapCursor(null);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movies, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.sort_popular) {
            if(sortOrder.equals("popular")){
                Toast toast = Toast.makeText(getApplicationContext(), "Already sorted by popularity.",
                        Toast.LENGTH_LONG);
                toast.show();
            }
            else {
                mMovieAdapter.setMovieData(null);
                mRecyclerView.setAdapter(mMovieAdapter);
                sortOrder = "popular";
                setTitle("Popular");
                loadMovieData();
            }
            return true;
        }
        else if (id == R.id.sort_top) {
            if(sortOrder.equals("top_rated")){
                Toast toast = Toast.makeText(getApplicationContext(), "Already sorted by Top-Rated.",
                        Toast.LENGTH_LONG);
                toast.show();

            }
            else {
                mMovieAdapter.setMovieData(null);
                mRecyclerView.setAdapter(mMovieAdapter);
                sortOrder = "top_rated";
                setTitle("Top-Rated");
                loadMovieData();
            }
            return true;
        }
        else {
            if(sortOrder.equals("favorites")){
                Toast toast = Toast.makeText(getApplicationContext(), "Already sorted by Favorites.",
                        Toast.LENGTH_LONG);
                toast.show();
            }
            else {
                mMovieAdapter.setMovieData(null);
                sortOrder = "favorites";
                setTitle("Favorites");

                mRecyclerView.setAdapter(mFavoriteAdapter);

                getSupportLoaderManager().restartLoader(FAVORITE_LOADER, null, favoriteResultLoaderListener);
            }
        }
        return super.onOptionsItemSelected(item);
    }
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(MOVIE_DATA_URL, movieDataURL);
        outState.putString(SORT_ORDER, sortOrder);
    }
}
