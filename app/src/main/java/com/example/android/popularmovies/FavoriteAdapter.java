package com.example.android.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.data.FavoriteContract;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteAdapterViewHolder> {

    private static final String TAG = MainActivity.class.getSimpleName();

    private FavoriteAdapterOnClickHandler mClickHandler;
    private Cursor mCursor;

    public interface FavoriteAdapterOnClickHandler {
        void onClick(String favorite);
    }
    public FavoriteAdapter(FavoriteAdapterOnClickHandler handler){
        this.mClickHandler = handler;
    }

    public class FavoriteAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView mFavoriteImageView;

        public FavoriteAdapterViewHolder(View view) {
            super(view);
            mFavoriteImageView = (ImageView) view.findViewById(R.id.movie_image);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();

            int dataIndex = mCursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_MOVIE_DATA);
            mCursor.moveToPosition(adapterPosition);
            String favorite = mCursor.getString(dataIndex);
            mClickHandler.onClick(favorite);
        }
    }

    @Override
    public FavoriteAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.movie_item, viewGroup, false);
        return new FavoriteAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FavoriteAdapterViewHolder holder, int position) {

        int idIndex = mCursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_ID);
        int titleIndex = mCursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_TITLE);
        int dataIndex = mCursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_MOVIE_DATA);

        mCursor.moveToPosition(position);

        int id = mCursor.getInt(idIndex);
        String title = mCursor.getString(titleIndex);
        String movieData = mCursor.getString(dataIndex);

        String [] data = movieData.split("\\n");

        String base_url = "http://image.tmdb.org/t/p/w185";
        Picasso.with(holder.mFavoriteImageView.getContext()).load(base_url + data[0]).into(holder.mFavoriteImageView);
    }

    @Override
    public int getItemCount() {
        if(mCursor == null){
            return 0;
        }
        return mCursor.getCount();
    }

    public Cursor swapCursor(Cursor c) {
        if (mCursor == c) {
            return null;
        }
        Cursor temp = mCursor;
        this.mCursor = c;

        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }
}