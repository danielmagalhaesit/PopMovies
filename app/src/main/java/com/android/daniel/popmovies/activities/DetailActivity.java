package com.android.daniel.popmovies.activities;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.daniel.popmovies.utils.Constants;
import com.android.daniel.popmovies.R;
import com.android.daniel.popmovies.models.Movie;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Create the detail fragment and add it to the activity
        // using a fragment transaction.

        Bundle arguments = new Bundle();
        arguments.putLong(Constants.MOVIE_ID, getIntent().getExtras().getLong(Constants.MOVIE_ID, 0 ));
        arguments.putLong(Constants.MOVIE_DB_ID, getIntent().getExtras().getLong(Constants.MOVIE_DB_ID, 0 ));

        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(arguments);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.container_detail, fragment)
                .commit();
    }

}
