package com.example.candidosg.popularmovies;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.candidosg.popularmovies.data.MovieContract;
import com.example.candidosg.popularmovies.fragments.MovieFragment;
import com.example.candidosg.popularmovies.tasks.FetchMoviesTask;
import com.facebook.stetho.Stetho;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MovieFragment())
                    .commit();
        }

        updateTiles();
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(
                                Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(
                                Stetho.defaultInspectorModulesProvider(this))
                        .build());

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

    private  void updateTiles(){
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

}
