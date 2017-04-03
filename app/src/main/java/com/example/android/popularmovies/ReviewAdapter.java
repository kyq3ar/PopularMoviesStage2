package com.example.android.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class ReviewAdapter extends ArrayAdapter<String> {
    private static final String TAG = DetailActivity.class.getSimpleName();

    public ReviewAdapter(Context context, ArrayList<String> reviewData){
        super(context, 0, reviewData);
    }

    public View getView(int position, View view, ViewGroup viewGroup){
        if(view == null){
            view = LayoutInflater.from(getContext())
                    .inflate(R.layout.review_item, viewGroup, false);
        }
        String [] reviewInfo = getItem(position).split("\\|");
        TextView authorTextView = (TextView) view.findViewById(R.id.review_author);
        authorTextView.setText(reviewInfo[0]);

        TextView contentTextView = (TextView) view.findViewById(R.id.review_text);
        contentTextView.setText(reviewInfo[1]);

        return view;
    }
}
