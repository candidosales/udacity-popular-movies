package com.example.candidosg.popularmovies.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.candidosg.popularmovies.BuildConfig;
import com.example.candidosg.popularmovies.R;
import com.example.candidosg.popularmovies.models.Movie;
import com.example.candidosg.popularmovies.models.MovieReview;
import com.example.candidosg.popularmovies.models.MovieVideo;
import com.squareup.picasso.Picasso;

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
import java.util.List;

/**
 * Created by candidosg on 06/08/16.
 */
public class DetailFragment extends Fragment {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    private static final String MOVIE_SHARE_HASHTAG = " #PopularMovie";
    private Movie movie;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Intent intent = getActivity().getIntent();
        movie = Parcels.unwrap(intent.getParcelableExtra("Movie"));

        if (intent != null){
            FetchMovieVideosTask movieVideosTask = new FetchMovieVideosTask(movie);
            movieVideosTask.execute("videos");

            FetchMovieReviewsTask movieReviewsTask = new FetchMovieReviewsTask(movie);
            movieReviewsTask.execute("reviews");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Intent intent = getActivity().getIntent();
        movie = Parcels.unwrap(intent.getParcelableExtra("Movie"));

        if (intent != null && movie.getOriginalTitle() != "") {
            ((TextView) rootView.findViewById(R.id.detail_movie_title))
                    .setText(movie.getOriginalTitle());
            ((TextView) rootView.findViewById(R.id.detail_movie_overview))
                    .setText(movie.getOverview());
            ((TextView) rootView.findViewById(R.id.detail_movie_release))
                    .setText(movie.getReleaseDate());

            ((RatingBar) rootView.findViewById(R.id.detail_movie_vote_average))
                    .setRating(movie.getVoteAverage());

            ImageView imageView = (ImageView) rootView.findViewById(R.id.detail_movie_image);
            Picasso.with(getContext()).load(movie.getPosterUrlPath()).into(imageView);
        }

        return rootView;
    }

    public void addMovieVideosViews(List<MovieVideo> movieVideos) {
        final LayoutInflater inflater = LayoutInflater.from(getActivity());

        if (movieVideos != null && !movieVideos.isEmpty()) {
            LinearLayout mMovieVideoLinearLayout = (LinearLayout) getActivity().findViewById(R.id.movie_detail_video_container);
            for (MovieVideo movieVideo : movieVideos) {

                final View movieVideoView = inflater.inflate(R.layout.list_item_movie_video, mMovieVideoLinearLayout, false);

                final String videoKey =  movieVideo.getKey();

                ((TextView) movieVideoView.findViewById(R.id.movie_video_title))
                        .setText(movieVideo.getName());


                movieVideoView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        openYouTubeIntent(videoKey);
                    }
                });

                mMovieVideoLinearLayout.addView(movieVideoView);
            }
        }
    }

    public void addMovieReviewsViews(List<MovieReview> movieReviews) {
        final LayoutInflater inflater = LayoutInflater.from(getActivity());

        if (movieReviews != null && !movieReviews.isEmpty()) {
            LinearLayout mMovieReviewLinearLayout = (LinearLayout) getActivity().findViewById(R.id.movie_detail_review_container);
            for (MovieReview movieReview : movieReviews) {

                final View movieReviewView = inflater.inflate(R.layout.list_item_movie_review, mMovieReviewLinearLayout, false);

                ((TextView) movieReviewView.findViewById(R.id.movie_review_content))
                        .setText(movieReview.getContent());

                ((TextView) movieReviewView.findViewById(R.id.movie_review_author))
                        .setText(movieReview.getAuthor());

                mMovieReviewLinearLayout.addView(movieReviewView);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detailfragment, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        ShareActionProvider mShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // Attach an intent to this ShareActionProvider.  You can update this at any time,
        // like when the user selects a new piece of data they might like to share.
        if (mShareActionProvider != null ) {
            mShareActionProvider.setShareIntent(createShareMovieIntent());
        } else {
            Log.d(LOG_TAG, "Share Action Provider is null?");
        }
    }

    private Intent createShareMovieIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                movie.getOriginalTitle() + MOVIE_SHARE_HASHTAG);
        return shareIntent;
    }

    private void openYouTubeIntent(String key) {
        Intent youTubeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + key));
        youTubeIntent.putExtra("force_fullscreen", true);
        startActivity(youTubeIntent);
    }

    public class FetchMovieVideosTask extends AsyncTask<String, Void, Movie> {
        private final String LOG_TAG = FetchMovieVideosTask.class.getSimpleName();

        private Movie mMovie;

        public FetchMovieVideosTask(Movie movie) {
            mMovie = movie;
        }

        private Movie getMovieDataFromJson(String resourceJsonStr, String typeResource) throws JSONException {

            // These are the names of the JSON objects that need to be extracted.

            JSONObject resourceJson = new JSONObject(resourceJsonStr);
            JSONArray resourceArray = resourceJson.getJSONArray("results");


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

            mMovie.setMovieVideoList(videos);


            return mMovie;
        }

        @Override
        protected Movie doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            String typeResource = params[0];

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String resourceJsonStr = null;

            try {

                String baseUrl = "https://api.themoviedb.org/3/movie/" + mMovie.getId() + "/" + typeResource;
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
                return getMovieDataFromJson(resourceJsonStr, typeResource);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(Movie result) {
            if (result != null) {
                if (!result.getMovieVideoList().isEmpty()) {
                    addMovieVideosViews(result.getMovieVideoList());
                }
            }
        }
    }

    public class FetchMovieReviewsTask extends AsyncTask<String, Void, Movie> {
        private final String LOG_TAG = FetchMovieReviewsTask.class.getSimpleName();

        private Movie mMovie;

        public FetchMovieReviewsTask(Movie movie) {
            mMovie = movie;
        }

        private Movie getMovieDataFromJson(String resourceJsonStr, String typeResource) throws JSONException {

            // These are the names of the JSON objects that need to be extracted.

            JSONObject resourceJson = new JSONObject(resourceJsonStr);
            JSONArray resourceArray = resourceJson.getJSONArray("results");

            mMovie.setMovieReviewList(new ArrayList<MovieReview>());

            List<MovieReview> reviews = new ArrayList<MovieReview>();
            for(int i = 0; i < resourceArray.length(); i++) {

                JSONObject reviewObject = resourceArray.getJSONObject(i);

                reviews.add(new MovieReview(reviewObject.getString("id"),
                        reviewObject.getString("author"),
                        reviewObject.getString("content"),
                        reviewObject.getString("url")));
            }

            mMovie.setMovieReviewList(reviews);

            return mMovie;
        }

        @Override
        protected Movie doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            String typeResource = params[0];

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String resourceJsonStr = null;

            try {

                String baseUrl = "https://api.themoviedb.org/3/movie/" + mMovie.getId() + "/" + typeResource;
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
                return getMovieDataFromJson(resourceJsonStr, typeResource);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(Movie result) {
            if (result != null) {
                if (!result.getMovieReviewList().isEmpty()) {
                    addMovieReviewsViews(result.getMovieReviewList());
                }
            }
        }
    }

}
