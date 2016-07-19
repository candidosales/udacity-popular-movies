package com.example.candidosg.popularmovies;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by candidosg on 18/07/16.
 */
public class MovieFragment extends Fragment {

    ArrayAdapter<String> movieAdapter;

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
            FetchMovieTask movieTask = new FetchMovieTask();
            movieTask.execute(type);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        String[] data = {
                "Filme 1",
                "Filme 2",
                "Filme 3",
                "Filme 4",
                "Filme 5",
                "Filme 6",
                "Filme 7"
        };

        List<String> movies = new ArrayList<String>(Arrays.asList(data));

        movieAdapter =
                new ArrayAdapter<String>(
                        getActivity(),
                        R.layout.list_item_movie,
                        R.id.list_item_movie_textview,
                        movies);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ListView listView = (ListView) rootView.findViewById(R.id.listview_movie);
        listView.setAdapter(movieAdapter);

        return rootView;
    }


    public class FetchMovieTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        private String[] getMovieDataFromJson(String movieJsonStr) throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String RESULTS = "results";
            final String ID = "id";
            final String ORIGINAL_TITLE = "original_title";
            final String ORIGINAL_LANGUAGE = "original_language";
            final String POSTER_PATH = "poster_path";
            final String BACKDROP_PATH = "backdrop_path";
            final String OVERVIEW = "overview";
            final String RELEASE_DATE = "release_date";

            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(RESULTS);

            String[] resultStrs = new String[movieArray.length()];

            for(int i = 0; i < movieArray.length(); i++) {

                JSONObject movieObject = movieArray.getJSONObject(i);

                String id = movieObject.getString(ID);
                String original_title = movieObject.getString(ORIGINAL_TITLE);
                String original_language = movieObject.getString(ORIGINAL_LANGUAGE);
                String poster_path = movieObject.getString(POSTER_PATH);
                String backdrop_path = movieObject.getString(BACKDROP_PATH);

                resultStrs[i] = id + " - " + original_title + " - " + original_language;
            }

            for (String s : resultStrs) {
                Log.v(LOG_TAG, "Movie entry: " + s);
            }
            return resultStrs;
        }

        @Override
        protected String[] doInBackground(String... params) {

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

        protected void onPostExecute(String[] result) {
            if (result != null) {
                movieAdapter.clear();
                for(String movieStr : result) {
                    movieAdapter.add(movieStr);
                }
            }
        }
    }
}
