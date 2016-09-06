package com.example.candidosg.popularmovies.adapters;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.example.candidosg.popularmovies.Config;
import com.example.candidosg.popularmovies.R;
import com.example.candidosg.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

/**
 * Created by candidosg on 04/09/16.
 */
public class MovieAdapter extends CursorAdapter {
    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();

    public MovieAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.grid_item_movie, parent, false);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ImageView imageView = (ImageView) view.findViewById(R.id.grid_item_movie_image);

        int moviePosterColumn = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH);
        String moviePoster = cursor.getString(moviePosterColumn);

        Uri imageUri = Uri.parse(Config.IMAGE_BASE_URL).buildUpon()
                .appendPath(context.getString(R.string.api_image_medium))
                .appendPath(moviePoster.substring(1))
                .build();

        Picasso.with(context).load(imageUri).into(imageView);
    }
}
