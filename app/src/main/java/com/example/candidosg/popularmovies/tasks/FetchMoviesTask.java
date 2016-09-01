package com.example.candidosg.popularmovies.tasks;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.candidosg.popularmovies.BuildConfig;
import com.example.candidosg.popularmovies.adapters.MovieAdapter;
import com.example.candidosg.popularmovies.data.MovieContract;
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
import java.util.Vector;

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

        Vector<ContentValues> cVVector = new Vector<ContentValues>(movieArray.length());

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



            // Database
            ContentValues movieValues = new ContentValues();

            // Then add the data, along with the corresponding name of the data type,
            // so the content provider knows what kind of value is being inserted.
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movieObject.getLong(ID));
            movieValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, movieObject.getString(ORIGINAL_TITLE));
            movieValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_LANGUAGE, movieObject.getString(ORIGINAL_LANGUAGE));
            movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, movieObject.getString(POSTER_PATH));
            movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movieObject.getString(OVERVIEW));
            movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movieObject.getString(RELEASE_DATE));
            movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, movieObject.getString(VOTE_AVERAGE));

            cVVector.add(movieValues);
        }

        // Database
        if ( cVVector.size() > 0 ) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            mContext.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);
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

    public long addMovie(String movieId, String originalTitle, String originalLanguage, String posterPath, String overview, String releaseDate, String voteAverage) {
        long id;

        // First, check if the location with this city name exists in the db
        Cursor movieCursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                new String[]{MovieContract.MovieEntry.COLUMN_MOVIE_ID},
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                new String[]{movieId},
                null);

        if (movieCursor.moveToFirst()) {
            int movieIdIndex = movieCursor.getColumnIndex(MovieContract.MovieEntry._ID);
            id = movieCursor.getLong(movieIdIndex);
        } else {
            // Now that the content provider is set up, inserting rows of data is pretty simple.
            // First create a ContentValues object to hold the data you want to insert.
            ContentValues movieValues = new ContentValues();

            // Then add the data, along with the corresponding name of the data type,
            // so the content provider knows what kind of value is being inserted.
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movieId);
            movieValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, originalTitle);
            movieValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_LANGUAGE, originalLanguage);
            movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, posterPath);
            movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, overview);
            movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, releaseDate);
            movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, voteAverage);

            // Finally, insert location data into the database.
            Uri insertedUri = mContext.getContentResolver().insert(
                    MovieContract.MovieEntry.CONTENT_URI,
                    movieValues
            );

            // The resulting URI contains the ID for the row.  Extract the locationId from the Uri.
            id = ContentUris.parseId(insertedUri);
        }

        movieCursor.close();
        // Wait, that worked?  Yes!
        return id;
    }
}
