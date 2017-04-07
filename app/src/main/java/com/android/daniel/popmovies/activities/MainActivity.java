package com.android.daniel.popmovies.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.daniel.popmovies.networkTasks.AsyncTaskDelegate;
import com.android.daniel.popmovies.networkTasks.MovieService;
import com.android.daniel.popmovies.networkTasks.NetworkUtil;
import com.android.daniel.popmovies.R;
import com.android.daniel.popmovies.utils.Constants;
import com.android.daniel.popmovies.utils.OnItemClickListener;
import com.android.daniel.popmovies.adapters.RecyclerMovieAdapter;
import com.android.daniel.popmovies.models.Movie;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnItemClickListener, AsyncTaskDelegate {

    private RecyclerMovieAdapter mMovieAdapter;
    private RecyclerView mRecyclerView;
    private ArrayList<Movie> mMovieList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    protected void onStart() {
        super.onStart();
        loadMovie();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Starting the Settings Activity
        int id = item.getItemId();
        if(id == R.id.action_settings){
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view, int position) {
        // Click event sending the movie information through the intent
        try {
            Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
            // Passing a Parcelable object.
            intent.putExtra(Constants.PARCELABLE_MOVIE, mMovieList.get(position));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadMovie() {
        // Getting the Sort By preference from shared preferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        // The second parameter is the default in case the file is empty.
        final String unitPref = sharedPreferences.getString(getString(R.string.pref_sort_key)
                , getString(R.string.pref_sort_default));
        if (NetworkUtil.isNetworkConnected(getApplicationContext())) {
            MovieService movieService = new MovieService(getApplicationContext(), MainActivity.this);
            movieService.execute(unitPref);
        } else {
            View view = findViewById(R.id.container);
            Snackbar snackbar = Snackbar.make(view, getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG);
            snackbar.setAction(getString(R.string.retry), new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    loadMovie();
                }
            });
            snackbar.show();
        }
    }

    @Override
    public void processFinish(Object output) {

        if (output != null) {
            mMovieList = (ArrayList<Movie>) output;
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movies);

        // Setting up the adapter
        mMovieAdapter = new RecyclerMovieAdapter(mMovieList, getApplicationContext());
        mRecyclerView.setAdapter(mMovieAdapter);
        // Click event
        mMovieAdapter.setClickListener(MainActivity.this);
        // Setting up the layout manager
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(MainActivity.this, 2);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setHasFixedSize(true);

    }
}
