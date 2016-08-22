package com.example.candidosg.popularmovies.tasks;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.example.candidosg.popularmovies.BuildConfig;
import com.example.candidosg.popularmovies.adapters.MovieAdapter;
import com.example.candidosg.popularmovies.models.Movie;
import com.example.candidosg.popularmovies.models.MovieReview;
import com.example.candidosg.popularmovies.models.MovieVideo;

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

/**
 * Created by candidosg on 06/08/16.
 */
public class FetchMoviesTask extends AsyncTask<String, Void, Movie[]> {

    private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

    private MovieAdapter mMovieAdapter;
    private final Context mContext;

    public FetchMoviesTask(Context context, MovieAdapter movieAdapter) {
        mContext = context;
        mMovieAdapter = movieAdapter;
    }

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

            movies[i] = new Movie(movieObject.getLong(ID),
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
        if (result != null && mMovieAdapter != null) {
            mMovieAdapter.clear();
            for(Movie movie : result) {
                mMovieAdapter.add(movie);
            }
        }
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager manager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            // Network is present and connected
            isAvailable = true;
        }
        return isAvailable;
    }
}
