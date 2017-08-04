package com.android.daniel.popmovies.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.android.daniel.popmovies.R;
import com.android.daniel.popmovies.sync.PopMovieSyncAdapter;
import com.android.daniel.popmovies.utils.Constants;
import com.android.daniel.popmovies.utils.Utility;

import io.github.skyhacker2.sqliteonweb.SQLiteOnWeb;

public class MainActivity extends AppCompatActivity implements MainFragment.Callback {

    private static final String DETAIL_FRAGMENT_TAG = "DFTAG";

    private boolean mTwoPane;
    private String mSortBy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSortBy = Utility.getSortBy(this);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        if (findViewById(R.id.movie_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new DetailFragment(), DETAIL_FRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }

        SQLiteOnWeb.init(this).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        String sortBy = Utility.getSortBy(this);
        if (sortBy != null && !sortBy.equals(mSortBy)) {
            MainFragment mf = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.main_fragment_id);
            if (mf != null) {
                mf.onSortByChanged();
            }
        }
    }

    @Override
    public void onItemSelected(long idMovie, long movieDbId) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putLong(Constants.MOVIE_ID, idMovie);
            args.putLong(Constants.MOVIE_DB_ID, movieDbId);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, DETAIL_FRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .putExtra(Constants.MOVIE_ID, idMovie)
                    .putExtra(Constants.MOVIE_DB_ID, movieDbId);
            startActivity(intent);
        }

        PopMovieSyncAdapter.initializeSyncAdapter(this);
    }
}


