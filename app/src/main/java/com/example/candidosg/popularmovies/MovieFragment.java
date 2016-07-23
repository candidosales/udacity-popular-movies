package com.example.candidosg.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by candidosg on 18/07/16.
 */
public class MovieFragment extends Fragment {

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
        FetchMoviesTask movieTask = new FetchMoviesTask();
        movieTask.execute(type);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies("popular");
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
                intent.putExtra("movie", Parcels.wrap(movie));
                startActivity(intent);

            }
        });
        return rootView;
    }


    public class FetchMoviesTask extends AsyncTask<String, Void, Movie[]> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        private Movie[] getMovieDataFromJson(String movieJsonStr) throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String RESULTS = "results";
            final String ID = "id";
            final String ORIGINAL_TITLE = "original_title";
            final String ORIGINAL_LANGUAGE = "original_language";
            final String POSTER_PATH = "poster_path";
            final String BACKDROP_PATH = "backdrop_path";
            final String OVERVIEW = "overview";
            final String RELEASE_DATE = "release_date";
            final String VOTE_AVERAGE = "vote_average";

            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(RESULTS);

            Movie[] movies = new Movie[movieArray.length()];

            for(int i = 0; i < movieArray.length(); i++) {

                JSONObject movieObject = movieArray.getJSONObject(i);

                movies[i] = new Movie(movieObject.getString(ID),
                        movieObject.getString(ORIGINAL_TITLE),
                        movieObject.getString(ORIGINAL_LANGUAGE),
                        movieObject.getString(POSTER_PATH),
                        movieObject.getString(BACKDROP_PATH),
                        movieObject.getString(OVERVIEW),
                        movieObject.getString(RELEASE_DATE),
                        movieObject.getString(VOTE_AVERAGE),
                        new ArrayList<MovieReview>(),
                        new ArrayList<MovieVideo>());
            }

            for (Movie m : movies) {
                Log.v(LOG_TAG, "Movie entry: " + m.getOriginalTitle());
            }
            return movies;
        }

        @Override
        protected Movie[] doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String moviesJsonStr = null;

            try {

                String baseUrl = "https://api.themoviedb.org/3/movie/" + params[0];
                String apiKey = "?api_key=" + BuildConfig.OPEN_THE_MOVIE_DB_API_KEY;
                URL url = new URL(baseUrl.concat(apiKey));

                Log.v(LOG_TAG, "Built URI " + url.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }

                moviesJsonStr = buffer.toString();

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMovieDataFromJson(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(Movie[] result) {
            if (result != null) {
                movieAdapter.clear();
                for(Movie movie : result) {
                    movieAdapter.add(movie);
                }
            }
        }
    }
}
