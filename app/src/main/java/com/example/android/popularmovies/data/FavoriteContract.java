package com.example.android.popularmovies.data;


import android.net.Uri;
import android.provider.BaseColumns;

public class FavoriteContract {
    public static final String AUTHORITY = "com.example.android.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_FAVORITES = "favorites";

    public static final class FavoriteEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();

        public static final String TABLE_NAME = "favorites";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_MOVIE_DATA = "data";
        /*public static final String COLUMN_RELEASE_DATE="release";
        public static final String COLUMN_USER_RATING = "rating";
        public static final*/
    }
}
