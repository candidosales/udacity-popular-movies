package com.example.candidosg.popularmovies;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by candidosg on 20/07/16.
 */
public class MovieAdapter extends ArrayAdapter<Movie> {
    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();

    public MovieAdapter(Activity context, List<Movie> movies) {
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

//        SquaredImageView view = (SquaredImageView) convertView;
//        if (view == null) {
//            view = new SquaredImageView(context);
//        }
//        String url = getItem(position);

        ImageView imageView = (ImageView) convertView.findViewById(R.id.grid_item_movie_image);
        if (imageView == null) {
            imageView = new ImageView(getContext());
        }

        Picasso.with(getContext()).load(movie.getPosterUrlPath()).into(imageView);

        TextView textView = (TextView) convertView.findViewById(R.id.grid_item_movie_textview);
        textView.setText(movie.getOriginalTitle());

        return convertView;
    }
}
