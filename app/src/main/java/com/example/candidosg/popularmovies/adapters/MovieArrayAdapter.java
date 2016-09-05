package com.example.candidosg.popularmovies.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.candidosg.popularmovies.R;
import com.example.candidosg.popularmovies.models.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by candidosg on 20/07/16.
 */
public class MovieArrayAdapter extends ArrayAdapter<Movie> {
    private static final String LOG_TAG = MovieArrayAdapter.class.getSimpleName();

    public MovieArrayAdapter(Activity context, List<Movie> movies) {
        super(context, 0, movies);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Gets the AndroidFlavor object from the ArrayAdapter at the appropriate position
        Movie movie = getItem(position);

        // Adapters recycle views to AdapterViews.
        // If this is a new View object we're getting, then inflate the layout.
        // If not, this view already has the layout inflated from a previous call to getView,
        // and we modify the View widgets as usual.
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item_movie, parent, false);
        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.grid_item_movie_image);
        if (imageView == null) {
            imageView = new ImageView(getContext());
        }

        Picasso.with(getContext()).load(movie.getPosterUrlPath()).into(imageView);

        return convertView;
    }
}
