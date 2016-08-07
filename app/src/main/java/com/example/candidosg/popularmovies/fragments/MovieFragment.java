package com.example.candidosg.popularmovies.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.example.candidosg.popularmovies.tasks.FetchMoviesTask;
import com.example.candidosg.popularmovies.R;
import com.example.candidosg.popularmovies.adapters.MovieAdapter;
import com.example.candidosg.popularmovies.models.Movie;

import org.parceler.Parcels;
import java.util.ArrayList;

/**
 * Created by candidosg on 18/07/16.
 */
public class MovieFragment extends Fragment {

    private final String LOG_TAG = MovieFragment.class.getSimpleName();

    private MovieAdapter movieAdapter;

    public MovieFragment() {

    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }


    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.moviefragment, menu);
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

        movieAdapter = new MovieAdapter(getActivity(), new ArrayList<Movie>());

        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movie);
        gridView.setAdapter(movieAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Movie movie = movieAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("Movie", Parcels.wrap(movie));
                startActivity(intent);

            }
        });
        return rootView;
    }



}
