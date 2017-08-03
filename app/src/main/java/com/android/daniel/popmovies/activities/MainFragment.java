package com.android.daniel.popmovies.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.android.daniel.popmovies.R;
import com.android.daniel.popmovies.adapters.CursorMovieAdapter;
import com.android.daniel.popmovies.data.MovieContract;
import com.android.daniel.popmovies.utils.NetworkUtil;
import com.android.daniel.popmovies.service.PopMovieService;
import com.android.daniel.popmovies.utils.Constants;
import com.android.daniel.popmovies.utils.Utility;


public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


    private CursorMovieAdapter mMovieAdapter;
    private GridView mGridView;

    private static final int MOVIE_LOADER = 0;
    private static final int FAVORITES_LOADER = 1;

    private static final String SELECTED_KEY = "selected_position";
    private int mPosition = GridView.INVALID_POSITION;


    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Starting the Settings Activity
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        if(id == R.id.action_favorite_list){
            getLoaderManager().restartLoader(FAVORITES_LOADER, null, this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        onSortByChanged();
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mMovieAdapter = new CursorMovieAdapter(getContext(), null, 0);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mGridView = (GridView) rootView.findViewById(R.id.gridview_movies);
        mGridView.setAdapter(mMovieAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                Log.v("ClickEvent MainFragment", DatabaseUtils.dumpCursorToString(cursor));
                if (cursor != null) {
                    long movieDbId = cursor.getLong(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID));
                    ((Callback) getActivity())
                            .onItemSelected(id, movieDbId);
                }
                mPosition = position;
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            // The listview probably hasn't even been populated yet.  Actually perform the
            // swapout in onLoadFinished.
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(mPosition != GridView.INVALID_POSITION){
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    private void loadMovie() {
        final String sortBy = Utility.getSortBy(getActivity());
        /*
        * param[o] = movie, video or review
        * param[1] = popular or top_rated
        * param[2] = ID from movieDB
        * param[3] = ID movie database
        */
        if (NetworkUtil.isNetworkConnected(getActivity())) {
            Intent intent = new Intent(getActivity(), PopMovieService.class);
            intent.putExtra(Constants.MOVIE_OR_VIDEO, Constants.MOVIE);
            intent.putExtra(Constants.SORT_BY, sortBy);
            getActivity().startService(intent);
        } else {
            View view = getActivity().findViewById(R.id.frame_main_fragment);
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

    void onSortByChanged() {
        loadMovie();
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
        getLoaderManager().destroyLoader(FAVORITES_LOADER);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortBy;

        switch (id){
            case MOVIE_LOADER:
            {
                sortBy = Utility.getSortBy(getActivity());
                break;
            }
            case FAVORITES_LOADER:
            {
                sortBy = "favorite";
                break;
            }
            default:
            {
                sortBy = "popular";
                break;
            }
        }

        CursorLoader cursorLoader = new CursorLoader(getActivity(),
                MovieContract.MovieEntry.buildMoviePopTopFavUri(sortBy),
                null,
                null,
                null,
                null);

        return cursorLoader;

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

//        cursor.moveToFirst();
//        Log.v("Cursor LoadFinish", DatabaseUtils.dumpCursorToString(cursor));

        mMovieAdapter.swapCursor(cursor);

        if(mPosition != GridView.INVALID_POSITION){
            mGridView.setSelection(mPosition);
        }else {
//            mGridView.setSelection(0);
//            mGridView.getSelectedView().setSelected(true);
//            cursor.moveToPosition(0);
//            long movieId = cursor.getLong(cursor.getColumnIndex(MovieContract.MovieEntry._ID));
//            long movieDbId = cursor.getLong(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID));
//            ((Callback) getActivity())
//                    .onItemSelected(movieId, movieDbId);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieAdapter.swapCursor(null);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */

    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(long idMovie, long movieDbId);
    }
}
