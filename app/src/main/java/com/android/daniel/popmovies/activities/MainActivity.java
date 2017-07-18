package com.android.daniel.popmovies.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;

import com.android.daniel.popmovies.adapters.CursorMovieAdapter;
import com.android.daniel.popmovies.data.MovieContract;
import com.android.daniel.popmovies.networkTasks.MovieService;
import com.android.daniel.popmovies.networkTasks.NetworkUtil;
import com.android.daniel.popmovies.R;
import com.android.daniel.popmovies.utils.Constants;
import com.android.daniel.popmovies.utils.OnItemClickListener;
import com.android.daniel.popmovies.models.Movie;

import java.util.ArrayList;

import io.github.skyhacker2.sqliteonweb.SQLiteOnWeb;

public class MainActivity extends AppCompatActivity{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MainFragment())
                    .commit();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        SQLiteOnWeb.init(this).start();
    }


//    @Override
//    public void onClick(View view, int position) {
//        // Click event sending the movie information through the intent
//        try {
//            Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
//            // Passing a Parcelable object.
//            intent.putExtra(Constants.PARCELABLE_MOVIE, mMovieList.get(position));
//            startActivity(intent);
//        } catch (Exception e) {
//            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//    }
}
