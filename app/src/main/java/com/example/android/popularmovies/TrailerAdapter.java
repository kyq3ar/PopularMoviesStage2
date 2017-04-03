package com.example.android.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class TrailerAdapter extends ArrayAdapter<String> {
    private static final String TAG = DetailActivity.class.getSimpleName();

    public TrailerAdapter (Context context, ArrayList<String> trailerData){
        super(context, 0, trailerData);
    }

    public View getView(int position, View view, ViewGroup viewGroup){
        if(view == null){
            view = LayoutInflater.from(getContext())
                    .inflate(R.layout.trailer_item, viewGroup, false);
        }
        String [] trailerInfo = getItem(position).split("\\|");
        TextView trailerTextView = (TextView) view.findViewById(R.id.trailer_title);
        trailerTextView.setText(trailerInfo[1]);

        return view;
    }
}