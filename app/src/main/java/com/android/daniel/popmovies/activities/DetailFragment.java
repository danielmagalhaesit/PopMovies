package com.android.daniel.popmovies.activities;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.daniel.popmovies.R;
import com.android.daniel.popmovies.adapters.VideosCursorAdapter;
import com.android.daniel.popmovies.data.MovieContract;
import com.android.daniel.popmovies.data.MovieContract.MovieEntry;
import com.android.daniel.popmovies.data.MovieContract.VideoEntry;
import com.android.daniel.popmovies.networkTasks.MovieService;
import com.android.daniel.popmovies.networkTasks.NetworkUtil;
import com.android.daniel.popmovies.utils.Constants;
import com.android.daniel.popmovies.utils.OnItemClickListener;
import com.squareup.picasso.Picasso;

/**
 * Created by danie on 04/06/2017.
 */

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, OnItemClickListener {

    private long mMovie_id;
    private long mIdMovieDB;
    Uri mStringUriId;
    Cursor mCursor;

    VideosCursorAdapter mVideosAdapter;

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    static final String DETAIL_URI = "URI";

    private static final int MOVIE_VIDEO_LOADER = 0;

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


    @Override
    public void onStart() {
        super.onStart();
        // Getting the information through the intent

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Intent intent = getActivity().getIntent();
        if (intent != null) {
            mMovie_id = intent.getLongExtra("uri_id", 0);
            mIdMovieDB = intent.getLongExtra("id_movie_db", 0);
        }
        loadVideoAndReview(Long.toString(mIdMovieDB), Long.toString(mMovie_id));

        // Setting up the views

        mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) rootView.findViewById(R.id.collapsing_toolbar);

        mImageViewBackdrop = (ImageView) rootView.findViewById(R.id.header_image);
        mImageViewPoster = (ImageView) rootView.findViewById(R.id.imageview_detail_poster);
        mTextViewTitle = (TextView) rootView.findViewById(R.id.textview_title);
        mTextViewRating = (TextView) rootView.findViewById(R.id.textview_rating);
        mTextViewReleaseDate = (TextView) rootView.findViewById(R.id.textview_release_date);
        mTextViewSynopsis = (TextView) rootView.findViewById(R.id.textview_overview);
        mTextViewReadMore= (TextView) rootView.findViewById(R.id.tv_read_more);

        mRecyclerViewVideos = (RecyclerView) rootView.findViewById(R.id.recycler_view_videos);

        mVideosAdapter = new VideosCursorAdapter(getContext(), null);

        mRecyclerViewVideos.setHasFixedSize(true);
        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerViewVideos.setHasFixedSize(true);

        MyLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        if (mRecyclerViewVideos != null) {
            mRecyclerViewVideos.setAdapter(mVideosAdapter);
        }
        mRecyclerViewVideos.setLayoutManager(MyLayoutManager);

        mRecyclerViewVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

//        ImageView imageViewPoster = (ImageView) rootView.findViewById(R.id.imageview_detail_poster);
//        Picasso.with(getActivity())
//                .load(Constants.POSTER_BASE_URL_500 + mMovie.getmPoster())
//                .into(imageViewPoster);
//        ((TextView) rootView.findViewById(R.id.textview_title)).setText(mMovie.getmTitle());
//        ((TextView) rootView.findViewById(R.id.textview_rating))
//                .setText("" + mMovie.getmVoteAverage());
//        ((TextView) rootView.findViewById(R.id.textview_release_date)).setText(mMovie.getmReleaseDate());
//        ((TextView) rootView.findViewById(R.id.textview_overview)).setText(mMovie.getmSynopsis());

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_VIDEO_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        mStringUriId = MovieContract.MovieEntry.buildMovieUri(mMovie_id);

        CursorLoader cursorLoader = new CursorLoader(
                getActivity(),
                mStringUriId,
                DETAIL_MOVIE_COLUMNS,
                null,
                null,
                null
        );

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (!cursor.moveToFirst()) {
            return;
        }
        mCursor = cursor;

        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mCollapsingToolbarLayout.setTitle(cursor.getString(COL_TITLE));

        String backdropPath = ("https://image.tmdb.org/t/p/original/" + cursor.getString(COL_BACKDROP));
        Picasso.with(getContext())
                .load(backdropPath)
                .into(mImageViewBackdrop);

        Picasso.with(getContext())
                .load(Constants.POSTER_BASE_URL_500 + cursor.getString(COL_POSTER))
                .into(mImageViewPoster);

        mTextViewTitle.setText(cursor.getString(COL_TITLE));
        mTextViewRating.setText(cursor.getString(COL_VOTE_AVERAGE));
        mTextViewReleaseDate.setText(cursor.getString(COL_RELEASE_DATE));
        mTextViewSynopsis.setText(cursor.getString(COL_SYNOPSIS));
        mTextViewSynopsis.setMaxLines(4);

        mTextViewReadMore.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                int i = mTextViewSynopsis.getMaxLines();
                if( i == 4){
                    mTextViewReadMore.setText(getActivity().getResources().getString(R.string.read_less));
//                    mTextViewSynopsis.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    mTextViewSynopsis.setMaxLines(30);
                }else {
                    mTextViewReadMore.setText(getActivity().getResources().getString(R.string.read_more));
//                    mTextViewSynopsis.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 280));
                    mTextViewSynopsis.setMaxLines(4);
                }
            }
        });


        mVideosAdapter.swapCursor(cursor);
        mVideosAdapter.notifyDataSetChanged();
        mVideosAdapter.setClickListener(this);


//        mListPathVideo = getVideosArrayAdapter(cursor);


//        mRecyclerViewVideos.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mVideosAdapter.swapCursor(null);

    }

    private void loadVideoAndReview(final String idMovieDB, final String idDatabaseMovie) {
        /*
        * param[o] = movie, video or review
        * param[1] = popular or top_rated
        * param[2] = ID from movieDB
        * param[3] = ID movie database
        */
        if (NetworkUtil.isNetworkConnected(getActivity())) {
            MovieService movieService = new MovieService(getActivity());
            movieService.execute(Constants.VIDEO, null, idMovieDB, idDatabaseMovie);
        } else {
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
        if(mCursor != null){
            try {
                mCursor.moveToPosition(position);
                String uriYoutube = "https://www.youtube.com/embed/" + mCursor.getString(mCursor.getColumnIndex(VideoEntry.COLUMN_PATH));
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uriYoutube)));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

//    private List<String> getVideosArrayAdapter(Cursor cursor) {
//        List<String> listPathVideos = new ArrayList<>();
//        if (cursor.moveToFirst()) {
//            Log.v("Parse Video", DatabaseUtils.dumpCursorToString(cursor));
//
//            do {
//                // do what you need with the cursor here
//                String pathMovie = cursor.getString(COL_VIDEO_PATH);
//                if(!listPathVideos.contains(pathMovie)){
//                    Video video = new Video();
//                    video.setmPath(pathMovie);
//                    listPathVideos.add(video.getmPath());
//                }
//            } while(cursor.moveToNext());
//        }
//        return listPathVideos;
//    }
}
