package com.android.daniel.popmovies.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.daniel.popmovies.R;
import com.android.daniel.popmovies.adapters.ReviewsCursorAdapter;
import com.android.daniel.popmovies.adapters.VideosCursorAdapter;
import com.android.daniel.popmovies.data.MovieContract;
import com.android.daniel.popmovies.data.MovieContract.MovieEntry;
import com.android.daniel.popmovies.data.MovieContract.VideoEntry;
import com.android.daniel.popmovies.utils.NetworkUtil;
import com.android.daniel.popmovies.service.PopMovieService;
import com.android.daniel.popmovies.utils.Constants;
import com.android.daniel.popmovies.utils.OnItemClickListener;
import com.squareup.picasso.Picasso;

/**
 * Created by danie on 04/06/2017.
 */

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, OnItemClickListener {

    private long mMovie_id;
    private long mIdMovieDB;
    Uri mUriMovieDetail;
    Cursor mCursorVideos;

    VideosCursorAdapter mVideosAdapter;
    ReviewsCursorAdapter mReviewsCursorAdapter;

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    private static final int MOVIE_VIDEO_LOADER = 0;
    private static final int REVIEW_LOADER = 1;

    private static final String[] DETAIL_MOVIE_COLUMNS = {
            MovieEntry.TABLE_NAME + "." + MovieEntry._ID,
            MovieEntry.COLUMN_TITLE,
            MovieEntry.COLUMN_POSTER,
            MovieEntry.COLUMN_BACKDROP,
            MovieEntry.COLUMN_SYNOPSIS,
            MovieEntry.COLUMN_RELEASE_DATE,
            MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieEntry.COLUMN_FAVORITE,
            MovieEntry.COLUMN_POPULAR,
            MovieEntry.COLUMN_TOP_RATED,
            VideoEntry.COLUMN_PATH,
    };

    // these constants correspond to the projection defined above, and must change if the
    // projection changes

    public static final int COL_MOVIE_ID = 0;
    public static final int COL_TITLE = 1;
    public static final int COL_POSTER = 2;
    public static final int COL_BACKDROP = 3;
    public static final int COL_SYNOPSIS = 4;
    public static final int COL_RELEASE_DATE = 5;
    public static final int COL_VOTE_AVERAGE = 6;
    public static final int COL_FAVORITE = 7;
    public static final int COL_POPULAR = 8;
    public static final int COL_TOP_RATED = 9;
    public static final int COL_VIDEO_PATH = 10;

    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private Toolbar mToolbar;

    private ImageView mImageViewBackdrop;
    private ImageView mImageViewPoster;
    private TextView mTextViewTitle;
    private TextView mTextViewSynopsis;
    private TextView mTextViewReleaseDate;
    private TextView mTextViewRating;
    private TextView mTextViewReadMore;
    private RecyclerView mRecyclerViewVideos;
    private RecyclerView mRecyclerViewReviews;
    private FloatingActionButton mFab;
    private RatingBar mRatingBar;


    @Override
    public void onStart() {
        super.onStart();
        // Getting the information through the intent
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mMovie_id = arguments.getLong(Constants.MOVIE_ID);
            mIdMovieDB = arguments.getLong(Constants.MOVIE_DB_ID);
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Constants.MOVIE_DB_ID) && intent.hasExtra(Constants.MOVIE_ID)) {
            mMovie_id = intent.getLongExtra(Constants.MOVIE_ID, 0);
            mIdMovieDB = intent.getLongExtra(Constants.MOVIE_DB_ID, 0);
        }

        // Setting up the views

        mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) rootView.findViewById(R.id.collapsing_toolbar);

        mImageViewBackdrop = (ImageView) rootView.findViewById(R.id.header_image);
        if (rootView.findViewById(R.id.imageview_detail_poster) != null){
            mImageViewPoster = (ImageView) rootView.findViewById(R.id.imageview_detail_poster);
        }
        mTextViewTitle = (TextView) rootView.findViewById(R.id.textview_title);
//        mTextViewRating = (TextView) rootView.findViewById(R.id.textview_rating);
        mRatingBar = (RatingBar) rootView.findViewById(R.id.rating_id);
        mTextViewReleaseDate = (TextView) rootView.findViewById(R.id.textview_release_date);
        mTextViewSynopsis = (TextView) rootView.findViewById(R.id.textview_overview);
        mTextViewReadMore = (TextView) rootView.findViewById(R.id.tv_read_more);
        mFab = (FloatingActionButton) rootView.findViewById(R.id.fab);

        mRecyclerViewVideos = (RecyclerView) rootView.findViewById(R.id.recycler_view_videos);

        mVideosAdapter = new VideosCursorAdapter(getContext(), null);

        mRecyclerViewVideos.setHasFixedSize(true);
        LinearLayoutManager videosLayoutManager = new LinearLayoutManager(getActivity());

        videosLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        if (mRecyclerViewVideos != null) {
            mRecyclerViewVideos.setAdapter(mVideosAdapter);
        }
        mRecyclerViewVideos.setLayoutManager(videosLayoutManager);

        mRecyclerViewReviews = (RecyclerView) rootView.findViewById(R.id.recycler_view_reviews);

        mReviewsCursorAdapter = new ReviewsCursorAdapter(getContext(), null);

        mRecyclerViewReviews.setHasFixedSize(true);
        LinearLayoutManager reviewsLayoutManager = new LinearLayoutManager(getActivity());

        reviewsLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        if (mRecyclerViewReviews != null) {
            mRecyclerViewReviews.setAdapter(mReviewsCursorAdapter);
        }
        mRecyclerViewReviews.setLayoutManager(reviewsLayoutManager);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        if(mMovie_id != 0 && mIdMovieDB != 0){
            loadVideoAndReview(Long.toString(mIdMovieDB), Long.toString(mMovie_id));
            getLoaderManager().initLoader(MOVIE_VIDEO_LOADER, null, this);
            getLoaderManager().initLoader(REVIEW_LOADER, null, this);
        }
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Log.v(LOG_TAG, "In onCreateLoader");
        Intent intent = getActivity().getIntent();
        if (intent == null) {
            return null;
        }

        switch (id) {
            case MOVIE_VIDEO_LOADER: {
                mUriMovieDetail = MovieContract.MovieEntry.buildMovieUri(mMovie_id);

                CursorLoader cursorLoader = new CursorLoader(
                        getActivity(),
                        mUriMovieDetail,
                        DETAIL_MOVIE_COLUMNS,
                        null,
                        null,
                        null
                );
                return cursorLoader;
            }
            case REVIEW_LOADER: {
                Uri uriReviews = MovieContract.ReviewEntry.buildReviewUriWithMovieId(mMovie_id);

                CursorLoader cursorLoader1 = new CursorLoader(
                        getActivity(),
                        uriReviews,
                        null,
                        null,
                        null,
                        null
                );
                return cursorLoader1;
            }
            default: {
                return null;
            }
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, final Cursor cursor) {

        boolean cursorValid = cursor.moveToFirst();
        if (!cursorValid) {
            return;
        }
        switch (loader.getId()) {
            case MOVIE_VIDEO_LOADER: {
                mVideosAdapter.swapCursor(cursor);
                mVideosAdapter.notifyDataSetChanged();
                mVideosAdapter.setClickListener(this);

                String backdropPath = ("https://image.tmdb.org/t/p/original/" + cursor.getString(COL_BACKDROP));
                Picasso.with(getContext())
                        .load(backdropPath)
                        .into(mImageViewBackdrop);

                if(mImageViewPoster != null){
                    Picasso.with(getContext())
                            .load(Constants.POSTER_BASE_URL_500 + cursor.getString(COL_POSTER))
                            .into(mImageViewPoster);
                }


                ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
                ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
                actionBar.setDisplayHomeAsUpEnabled(true);

                mCollapsingToolbarLayout.setTitle(cursor.getString(COL_TITLE));

                mCursorVideos = cursor;

                mTextViewTitle.setText(cursor.getString(COL_TITLE));
//                mTextViewRating.setText(cursor.getString(COL_VOTE_AVERAGE));
//                float rating = ((cursor.getFloat(COL_VOTE_AVERAGE) * 2))/10;
                float rating = cursor.getFloat(COL_VOTE_AVERAGE);
                mRatingBar.setRating(cursor.getFloat(COL_VOTE_AVERAGE)/2);
                mTextViewReleaseDate.setText(cursor.getString(COL_RELEASE_DATE));
                mTextViewSynopsis.setText(cursor.getString(COL_SYNOPSIS));
                mTextViewSynopsis.setMaxLines(4);

                mTextViewReadMore.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onClick(View v) {
                        int i = mTextViewSynopsis.getMaxLines();
                        if (i == 4) {
                            mTextViewReadMore.setText(getActivity().getResources().getString(R.string.read_less));
                            mTextViewSynopsis.setMaxLines(30);
                        } else {
                            mTextViewReadMore.setText(getActivity().getResources().getString(R.string.read_more));
                            mTextViewSynopsis.setMaxLines(4);
                        }
                    }
                });

                if(cursor.getInt(COL_FAVORITE) == 0){
                    mFab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_action_heart_gray));
                }else{
                    mFab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_action_heart_white));
                }

                mFab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ContentValues contentValues = new ContentValues();
                        String movieDbIdString = Long.toString(mIdMovieDB);

                        if(cursor.getInt(COL_FAVORITE) == 0){
                            contentValues.put(MovieEntry.COLUMN_FAVORITE, 1);
                            mFab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_action_heart_white));
                        }else{
                            contentValues.put(MovieEntry.COLUMN_FAVORITE, 0);
                            mFab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_action_heart_gray));
                        }
                        getContext().getContentResolver().update(MovieEntry.CONTENT_URI, contentValues, MovieEntry.COLUMN_MOVIE_ID + "=?", new String[]{movieDbIdString});
                        contentValues.clear();
                    }
                });

                break;
            }
            case REVIEW_LOADER: {
                mReviewsCursorAdapter.swapCursor(cursor);
                break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mVideosAdapter.swapCursor(null);
        mReviewsCursorAdapter.swapCursor(null);

    }

    private void loadVideoAndReview(final String idMovieDB, final String idDatabaseMovie) {
        /*
        * param[o] = movie, video or review
        * param[1] = popular or top_rated
        * param[2] = ID from movieDB
        * param[3] = ID movie database
        */
        if (NetworkUtil.isNetworkConnected(getActivity())) {

            Intent intent = new Intent(getActivity(), PopMovieService.class);
            intent.putExtra(Constants.MOVIE_OR_VIDEO, Constants.VIDEO);
            intent.putExtra(Constants.MOVIE_DB_ID, idMovieDB);
            intent.putExtra(Constants.MOVIE_ID, idDatabaseMovie);
            getActivity().startService(intent);        } else {
            View view = getActivity().findViewById(R.id.frame_main_fragment);
            Snackbar snackbar = Snackbar.make(view, getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG);
            snackbar.setAction(getString(R.string.retry), new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    loadVideoAndReview(idMovieDB, idDatabaseMovie);
                }
            });
            snackbar.show();
        }
    }

    @Override
    public void onClick(View view, int position) {
        if (mCursorVideos != null) {
            try {
                mCursorVideos.moveToPosition(position);
                String uriYoutube = Constants.YOUTUBE_VIDEO_URL + mCursorVideos.getString(COL_VIDEO_PATH);
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uriYoutube)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

}
