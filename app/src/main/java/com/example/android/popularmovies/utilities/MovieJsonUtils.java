package com.example.android.popularmovies.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.android.popularmovies.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

public class MovieJsonUtils {
    private static final String TAG = MainActivity.class.getSimpleName();
    public static String[] getMovieInformationFromJson(Context context, String movieJsonStr)
            throws JSONException {
        JSONObject movieJson = new JSONObject(movieJsonStr);

        if (movieJson.has("status_code")) return null;

        JSONArray movieArray = movieJson.getJSONArray("results");
        if(movieArray.length() == 0) return null;

        String [] information = new String[movieArray.length()];


        for(int i = 0; i < movieArray.length(); i++){
            JSONObject movie = movieArray.getJSONObject(i);

            String imageLink = movie.getString("poster_path");
            String details = movie.getString("original_title") + "\n" +
                    movie.getString("overview") + "\n" +
                    movie.getString("vote_average") + "\n" +
                    movie.getString("release_date") + "\n" +
                    movie.getString("id");
            information[i] = imageLink + "\n" + details;
        }
        return information;
    }
    public static String[] getTrailerInformationFromJson(String trailerJsonStr) throws JSONException{
        JSONObject videoJson = new JSONObject(trailerJsonStr);

        if (videoJson.has("status_code")) return null;

        JSONArray videoArray = videoJson.getJSONArray("results");
        if(videoArray.length() == 0) return null;

        String [] information = new String[videoArray.length()];
        for(int i = 0; i < videoArray.length(); i++){
            JSONObject video = videoArray.getJSONObject(i);
            information[i] = video.getString("key") + "|" + video.getString("name");
        }
        return information;
    }
    public static String[] getReviewInformationFromJson(String reviewJsonStr) throws JSONException{
        JSONObject reviewJson = new JSONObject(reviewJsonStr);

        if (reviewJson.has("status_code")) return null;

        JSONArray reviewArray = reviewJson.getJSONArray("results");
        if(reviewArray.length() == 0) return null;

        String [] information = new String[reviewArray.length()];
        for(int i = 0; i < reviewArray.length(); i++){
            JSONObject review = reviewArray.getJSONObject(i);
            information[i] = review.getString("author") + "|" + review.getString("content");
        }
        return information;
    }

}
