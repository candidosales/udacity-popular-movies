package com.example.candidosg.popularmovies.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.candidosg.popularmovies.DetailActivity;
import com.example.candidosg.popularmovies.Utility;
import com.example.candidosg.popularmovies.adapters.MovieAdapter;
import com.example.candidosg.popularmovies.data.MovieContract;
import com.example.candidosg.popularmovies.tasks.FetchMoviesTask;
import com.example.candidosg.popularmovies.R;
import com.example.candidosg.popularmovies.adapters.MovieArrayAdapter;
import com.example.candidosg.popularmovies.models.Movie;

import org.parceler.Parcels;
import java.util.ArrayList;

/**
 * Created by candidosg on 18/07/16.
 */
public class MovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = MovieFragment.class.getSimpleName();

    private  MovieAdapter movieAdapter;
    public static final int MOVIE_LOADER = 0;

    public MovieFragment() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }


    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.moviefragment, menu);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        String type = null;

        if (id == R.id.action_order_popular) {
            type = "popular";
        }

        if (id == R.id.action_order_top_rated) {
            type = "top_rated";
        }

        if (id != 0 && type != null) {
            updateMovies(type);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateMovies(String type) {
        if (isOnline(getContext())) {
            FetchMoviesTask movieTask = new FetchMoviesTask(getActivity(), movieAdapter);
            movieTask.execute(type);
        }
        else {
            Log.d(LOG_TAG, "Wifi connected: false");
            Toast.makeText(getContext(), R.string.log_connection_fail, Toast.LENGTH_LONG).show();
        }
    }

    public boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isOnline(getContext())) {
            updateMovies("popular");
        }
        else {
            Log.d(LOG_TAG, "Wifi connected: false");
            Toast.makeText(getContext(), R.string.log_connection_fail, Toast.LENGTH_LONG).show();
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        movieAdapter = new MovieAdapter(getActivity(), null, 0);

        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movie);
        gridView.setAdapter(movieAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                Movie movie = movieArrayAdapter.getItem(position);
//                Intent intent = new Intent(getActivity(), DetailActivity.class);
//                intent.putExtra("Movie", Parcels.wrap(movie));
//                startActivity(intent);


                Cursor currentData = (Cursor) adapterView.getItemAtPosition(position);
                if (currentData != null) {
                    Intent detailsIntent = new Intent(getActivity(), DetailActivity.class);
                    final int MOVIE_ID_COL = currentData.getColumnIndex(MovieContract.MovieEntry._ID);
                    Uri movieUri = MovieContract.MovieEntry.buildMovieWithId(currentData.getInt(MOVIE_ID_COL));

                    detailsIntent.setData(movieUri);
                    startActivity(detailsIntent);
                }

            }
        });
        return rootView;


//        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
//
//        moviesGridView = (GridView) rootView.findViewById(R.id.movies_gridview);
//
//        movieAdapter = new MovieAdapter(getActivity(), null, 0);
//
//        moviesGridView.setAdapter(movieAdapter);
//
//        moviesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Cursor currentData = (Cursor) parent.getItemAtPosition(position);
//                if (currentData != null) {
//                    Intent detailsIntent = new Intent(getActivity(), MovieDetailsActivity.class);
//                    final int MOVIE_ID_COL = currentData.getColumnIndex(MovieContract.MovieEntry._ID);
//                    Uri movieUri = MovieContract.MovieEntry.buildMovieWithId(currentData.getInt(MOVIE_ID_COL));
//
//                    detailsIntent.setData(movieUri);
//                    startActivity(detailsIntent);
//                }
//            }
//        });
//
//        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrderSetting = Utility.getPreferredSortOrder(getActivity());
        String sortOrder;
        final int NUMBER_OF_MOVIES = 20;

        if (sortOrderSetting.equals(getString(R.string.prefs_sort_default_value))) {
            sortOrder = MovieContract.MovieEntry.COLUMN_POPULARITY + " DESC";
        } else {
            //sort by rating
            sortOrder = MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE + " DESC";
        }

        return new CursorLoader(getActivity(),
                MovieContract.MovieEntry.CONTENT_URI,
                new String[]{MovieContract.MovieEntry._ID, MovieContract.MovieEntry.COLUMN_POSTER_PATH},
                null,
                null,
                sortOrder + " LIMIT " + NUMBER_OF_MOVIES);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.d(LOG_TAG, "Cursor loaded, " + cursor.getCount() + " rows fetched");
        movieAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        movieAdapter.swapCursor(null);
    }



}
