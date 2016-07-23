package com.example.candidosg.popularmovies;

import android.os.AsyncTask;
import android.util.Log;

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
 * Created by candidosg on 23/07/16.
 */
public abstract class MovieResources extends AsyncTask<Movie, Void, Movie> {
    private final String LOG_TAG = MovieResources.class.getSimpleName();

    private Movie getMovieDataFromJson(String resourceJsonStr, String typeResource, Movie movie) throws JSONException {

        // These are the names of the JSON objects that need to be extracted.

        JSONObject resourceJson = new JSONObject(resourceJsonStr);
        JSONArray resourceArray = resourceJson.getJSONArray("results");

        if ( typeResource == "reviews" ) {
            List<MovieReview> reviews = new ArrayList<MovieReview>();
            for(int i = 0; i < resourceArray.length(); i++) {

                JSONObject reviewObject = resourceArray.getJSONObject(i);

                reviews.add(new MovieReview(reviewObject.getString("id"),
                        reviewObject.getString("author"),
                        reviewObject.getString("content"),
                        reviewObject.getString("url")));
            }

            movie.setMovieReviewList(reviews);
        }

        if ( typeResource == "videos" ) {
            List<MovieVideo> videos = new ArrayList<MovieVideo>();
            for(int i = 0; i < resourceArray.length(); i++) {

                JSONObject videoObject = resourceArray.getJSONObject(i);

                videos.add(new MovieVideo(videoObject.getString("id"),
                        videoObject.getString("key"),
                        videoObject.getString("name"),
                        videoObject.getString("site"),
                        videoObject.getString("size"),
                        videoObject.getString("type")));
            }

            movie.setMovieVideoList(videos);
        }

        return movie;
    }

    protected Movie doInBackground(Movie movie) {

        if (movie == null) {
            return null;
        }
        
        String[] typeResources = { "videos", "reviews" };

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String resourceJsonStr = null;

        for (String typeResource : typeResources) {
            try {

                String baseUrl = "https://api.themoviedb.org/3/movie/" + movie.getId() + "/" + typeResource;
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

                resourceJsonStr = buffer.toString();

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
                return getMovieDataFromJson(resourceJsonStr, typeResource, movie);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
        }



        return null;
    }

    protected void onPostExecute(Movie result) {
        if (result != null) {

        }
    }
}
