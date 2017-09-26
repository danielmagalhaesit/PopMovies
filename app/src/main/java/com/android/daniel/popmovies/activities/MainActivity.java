package com.android.daniel.popmovies.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.android.daniel.popmovies.R;
import com.android.daniel.popmovies.sync.PopMovieSyncAdapter;
import com.android.daniel.popmovies.utils.Constants;
import com.android.daniel.popmovies.utils.Utility;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import io.github.skyhacker2.sqliteonweb.SQLiteOnWeb;

public class MainActivity extends AppCompatActivity implements MainFragment.Callback {

    private static final String DETAIL_FRAGMENT_TAG = "DFTAG";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String LOG_TAG = MainActivity.class.getSimpleName() ;

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

//        if (!checkPlayServices()) {
//            // This is where we could either prompt a user that they should install
//            // the latest version of Google Play Services, or add an error snackbar
//            // that some features won't be available.
//        }
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

//    /**
//     * Check the device to make sure it has the Google Play Services APK. If
//     * it doesn't, display a dialog that allows users to download the APK from
//     * the Google Play Store or enable it in the device's system settings.
//     */
//    private boolean checkPlayServices() {
//        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
//        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
//        if (resultCode != ConnectionResult.SUCCESS) {
//            if (apiAvailability.isUserResolvableError(resultCode)) {
//                apiAvailability.getErrorDialog(this, resultCode,
//                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
//            } else {
//                Log.i(LOG_TAG, "This device is not supported.");
//                finish();
//            }
//            return false;
//        }
//        return true;
//    }
}


