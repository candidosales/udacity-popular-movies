package com.example.candidosg.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.parceler.Parcels;

import java.util.ArrayList;

/**
 * Created by candidosg on 23/07/16.
 */
public class MovieVideoFragment extends Fragment {

    private MovieVideoAdapter movieVideoAdapter;

    public MovieVideoFragment() {

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

    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies("popular");
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        movieVideoAdapter = new MovieVideoAdapter(getActivity(), new ArrayList<MovieVideo>());

        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movie);
        gridView.setAdapter(movieVideoAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                MovieVideo movieVideo = movieVideoAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("movie", Parcels.wrap(movieVideo));
                startActivity(intent);

            }
        });
        return rootView;
    }
}
