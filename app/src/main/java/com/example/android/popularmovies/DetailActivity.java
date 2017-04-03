package com.example.android.popularmovies;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.data.FavoriteContract;
import com.example.android.popularmovies.data.FavoriteDbHelper;
import com.example.android.popularmovies.utilities.MovieJsonUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;


import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class DetailActivity extends AppCompatActivity{

    private String mMovies;
    private TextView mTitle;
    private ImageView mImageDisplay;
    private TextView mOverview;
    private TextView mUserRating;
    private TextView mReleaseDate;
    private TextView mTrailerErrorMessage;
    private TextView mReviewErrorMessage;
    private ListView mTrailerListView;
    private ListView mReviewListView;

    private TrailerAdapter mTrailerAdapter;
    private ReviewAdapter mReviewAdapter;

    private ImageButton favoriteButton;

    private int ID;

    private static final String TAG = MainActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail);

        mTitle = (TextView) findViewById(R.id.title);
        mImageDisplay = (ImageView) findViewById(R.id.detail_movie_image);
        mOverview = (TextView) findViewById(R.id.overview);
        mUserRating = (TextView) findViewById(R.id.user_rating);
        mReleaseDate = (TextView) findViewById(R.id.release_date);
        mTrailerErrorMessage = (TextView) findViewById(R.id.error_message_trailer);
        mReviewErrorMessage = (TextView) findViewById(R.id.error_message_review);

        mTrailerListView = (ListView) findViewById(R.id.listview_trailers);
        mReviewListView = (ListView) findViewById(R.id.listview_reviews);

        favoriteButton = (ImageButton) findViewById(R.id.favorite_button);
        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
                mMovies = intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT);
                String [] data = mMovies.split("\\n");

                mTitle.setText(data[1]);
                setTitle(data[1]);

                String base_url = "http://image.tmdb.org/t/p/w185";
                Picasso.with(this).load(base_url + data[0]).into(mImageDisplay);

                mOverview.setText(data[2]);
                mUserRating.setText("Rating: " + data[3]);
                mReleaseDate.setText("Release Date: " + data[4]);

                loadTrailerData(data[5]);
                loadReviewData(data[5]);
                ID = Integer.parseInt(data[5]);

                favoriteButton.setTag(data[5]);

                String whereClause = "ID = ?";
                String [] whereArgs = new String [] {ID+""};
                Cursor cursor = getContentResolver().query(
                        FavoriteContract.FavoriteEntry.CONTENT_URI,
                        null, whereClause, whereArgs, null);
                if(cursor.getCount()!=0){
                    favoriteButton.setSelected(true);
                }
            }
        }
        mTrailerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String trailer = (String) mTrailerListView.getItemAtPosition(position);
                String [] info = trailer.split("\\|");

                Uri uri = Uri.parse("http://www.youtube.com/watch?v=" + info[0]);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);

                startActivity(intent);

            }
        });
    }
    private void loadTrailerData(String id) {
        showTrailerDataView();
        new DetailActivity.FetchTrailerTask().execute(id);
    }

    private void loadReviewData(String id) {
        showReviewDataView();
        new DetailActivity.FetchReviewTask().execute(id);
    }

    private void showTrailerDataView() {
        mTrailerErrorMessage.setVisibility(View.INVISIBLE);
        mTrailerListView.setVisibility(View.VISIBLE);
    }
    private void showTrailerErrorMessage() {
        mTrailerListView.setVisibility(View.INVISIBLE);
        mTrailerErrorMessage.setVisibility(View.VISIBLE);
    }
    private void showReviewDataView() {
        mReviewErrorMessage.setVisibility(View.INVISIBLE);
        mReviewListView.setVisibility(View.VISIBLE);
    }
    private void showReviewErrorMessage() {
        mReviewListView.setVisibility(View.INVISIBLE);
        mReviewErrorMessage.setVisibility(View.VISIBLE);
    }

    public class FetchTrailerTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String[] doInBackground(String... params) {
            String id = params[0];
            URL trailerRequestUrl = NetworkUtils.buildDetailUrl(id, "videos");

            try {
                String jsonTrailerResponse = NetworkUtils
                        .getResponseFromHttpUrl(trailerRequestUrl);

                String[] information = MovieJsonUtils
                        .getTrailerInformationFromJson(jsonTrailerResponse);

                return information;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] trailerData) {
            if (trailerData != null) {
                mTrailerAdapter = new TrailerAdapter(DetailActivity.this,
                        new ArrayList<String>(Arrays.asList(trailerData)));
                mTrailerListView.setAdapter(mTrailerAdapter);
                showTrailerDataView();
            } else {
                showTrailerErrorMessage();
            }
        }
    }
    public class FetchReviewTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String[] doInBackground(String... params) {
            String id = params[0];
            URL reviewRequestUrl = NetworkUtils.buildDetailUrl(id, "reviews");

            try {
                String jsonReviewResponse = NetworkUtils
                        .getResponseFromHttpUrl(reviewRequestUrl);
                String[] information = MovieJsonUtils
                        .getReviewInformationFromJson(jsonReviewResponse);

                return information;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] reviewData) {
            if (reviewData != null) {
                mReviewAdapter = new ReviewAdapter(DetailActivity.this,
                        new ArrayList<String>(Arrays.asList(reviewData)));
                mReviewListView.setAdapter(mReviewAdapter);
                showReviewDataView();
            } else {
                showReviewErrorMessage();
            }
        }
    }
    public void onToggleStar(View view) {
        favoriteButton.setSelected(!favoriteButton.isSelected());
        boolean isSelected = favoriteButton.isSelected();

        String whereClause = "ID = ?";
        String [] whereArgs = new String [] {ID+""};
        Cursor cursor = getContentResolver().query(
                FavoriteContract.FavoriteEntry.CONTENT_URI,
                null, whereClause, whereArgs, null);
        if(isSelected){
            //check to see if already in database
            if(cursor.getCount()==0) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(FavoriteContract.FavoriteEntry.COLUMN_ID, ID);
                contentValues.put(FavoriteContract.FavoriteEntry.COLUMN_TITLE, mTitle.getText().toString());

                contentValues.put(FavoriteContract.FavoriteEntry.COLUMN_MOVIE_DATA, mMovies);

                Uri uri = getContentResolver().insert(FavoriteContract.FavoriteEntry.CONTENT_URI, contentValues);
                if(uri!=null){
                    Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();
                }
            }
        }
        else {
            //check to see if in database
            if(cursor.getCount()!=0) {
                Uri uri = FavoriteContract.FavoriteEntry.CONTENT_URI;
                int result = getContentResolver().delete(uri, null, new String [] {ID+""});
            }
        }
    }
}