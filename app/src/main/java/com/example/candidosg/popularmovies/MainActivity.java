package com.example.candidosg.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.candidosg.popularmovies.data.MovieContract;
import com.example.candidosg.popularmovies.fragments.DetailFragment;
import com.example.candidosg.popularmovies.fragments.MovieFragment;
import com.example.candidosg.popularmovies.tasks.FetchMoviesTask;

import java.util.Date;

public class MainActivity extends AppCompatActivity implements MovieFragment.Callback {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        updateMovies();

        if(findViewById(R.id.movie_detail_container) != null){
            Log.i(LOG_TAG, "Two Pane Layout");
            mTwoPane = true;
            if(savedInstanceState == null){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new DetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        }else{
            mTwoPane = false;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

    private  void updateMovies(){
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final long bestBefore = settings.getLong(Config.VALID_TILL, 0L);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                if (new Date().after(new Date(bestBefore))) {
                    if (Utility.hasActiveInternetConnection(getApplicationContext())) {
                        getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI, null, null);

                        new FetchMoviesTask(getApplicationContext()).execute(String.valueOf(Config.PREF_HIGH_RATED));
                        new FetchMoviesTask(getApplicationContext()).execute(String.valueOf(Config.PREF_MOST_POPULAR));

                        settings.edit().putLong(Config.VALID_TILL, new Date().getTime() + 86400000L).apply();
                    }
                } else {
                    return;
                }
            }
        });

    }

    @Override
    public void onItemSelected(Uri movieUri) {
        DetailFragment detailFragment = (DetailFragment) getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
        if(detailFragment != null){

            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.DETAIL_URI, movieUri);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();

        }else{

            Intent detailIntent = new Intent(getApplicationContext(), DetailActivity.class);
            detailIntent.setData(movieUri);
            startActivity(detailIntent);
        }
    }

}
