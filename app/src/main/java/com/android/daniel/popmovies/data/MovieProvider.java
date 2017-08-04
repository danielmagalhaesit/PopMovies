package com.android.daniel.popmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by danie on 19/05/2017.
 */

public class MovieProvider extends ContentProvider {

    private MovieDBHelper mOpenHelper;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    static final int MOVIE = 100;
    static final int MOVIE_FROM_ID = 101;
    static final int MOVIE_POPULAR = 102;
    static final int MOVIE_TOP_RATED = 103;
    static final int MOVIE_FAVORITE= 104;

    static final int VIDEO = 200;
    static final int VIDEO_FROM_ID = 201;
    static final int VIDEOS_FROM_MOVIE_ID = 202;

    static final int REVIEW = 300;
    static final int REVIEW_FROM_ID = 301;
    static final int REVIEWS_FROM_MOVIE_ID = 302;

    private static final SQLiteQueryBuilder sVideoByMovieIdQueryBuilder;

    static{
        sVideoByMovieIdQueryBuilder = new SQLiteQueryBuilder();

        sVideoByMovieIdQueryBuilder.setTables(
                MovieContract.VideoEntry.TABLE_NAME + " LEFT JOIN " +
                        MovieContract.MovieEntry.TABLE_NAME +
                        " ON " + MovieContract.VideoEntry.TABLE_NAME +
                        "." + MovieContract.VideoEntry.COLUMN_MOVIE_KEY +
                        " = " + MovieContract.MovieEntry.TABLE_NAME +
                        "." + MovieContract.MovieEntry._ID);
    }

    private static final SQLiteQueryBuilder sReviewByMovieIdQueryBuilder;

    static{
        sReviewByMovieIdQueryBuilder = new SQLiteQueryBuilder();

        sReviewByMovieIdQueryBuilder.setTables(MovieContract.ReviewEntry.TABLE_NAME);
    }

    private static final SQLiteQueryBuilder sReviewAndVideoByMovieIdQueryBuilder;

    static{
        sReviewAndVideoByMovieIdQueryBuilder = new SQLiteQueryBuilder();

        sReviewAndVideoByMovieIdQueryBuilder.setTables(
                "movie LEFT JOIN video ON movie._id = video.movie_id LEFT JOIN review ON movie._id = review.movie_id");
    }

    private static final String sMovieSelection =
            MovieContract.MovieEntry.TABLE_NAME +
                    "." + MovieContract.MovieEntry._ID + " = ? ";

    private static final String sVideoSelection =
            MovieContract.VideoEntry.TABLE_NAME +
                    "." + MovieContract.VideoEntry._ID + " = ? ";

    private static final String sMovieSelectionInVideoTable =
            MovieContract.MovieEntry.TABLE_NAME +
                    "." + MovieContract.VideoEntry.COLUMN_MOVIE_KEY + " = ? ";

    private static final String sReviewSelection =
            MovieContract.ReviewEntry.TABLE_NAME +
                    "." + MovieContract.ReviewEntry._ID + " = ? ";

    private static final String sMovieSelectionInReviewTable =
            MovieContract.ReviewEntry.COLUMN_MOVIE_KEY + " = ? ";

    private static final String sMovieTypeSelection =
                    MovieContract.MovieEntry.COLUMN_POPULAR + " = ? ";

    private static final String sFavoriteMovieSelection =
            MovieContract.MovieEntry.COLUMN_FAVORITE + " = ? ";

    private static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
                                /* MOVIE TABLE */
        // This brings the movie list
        matcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIE);
        // This brings only one movie
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/#", MOVIE_FROM_ID);

        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/popular", MOVIE_POPULAR);
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/top_rated", MOVIE_TOP_RATED);
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/favorite", MOVIE_FAVORITE);

                                /* VIDEO TABLE */
        matcher.addURI(authority, MovieContract.PATH_VIDEO, VIDEO);
        // This brings only one trailer from id
        matcher.addURI(authority, MovieContract.PATH_VIDEO + "/*", VIDEO_FROM_ID);
        // This bring every trailer of the a certain movie
        matcher.addURI(authority, MovieContract.PATH_VIDEO + "/*/#", VIDEOS_FROM_MOVIE_ID);

                                /* REVIEW TABLE */
        matcher.addURI(authority, MovieContract.PATH_REVIEW, REVIEW);
        // This brings only one trailer from id
        matcher.addURI(authority, MovieContract.PATH_REVIEW + "/*", REVIEW_FROM_ID);
        // This bring every review of the a certain movie
        matcher.addURI(authority, MovieContract.PATH_REVIEW + "/*/#", REVIEWS_FROM_MOVIE_ID);

        return matcher;
    }

    private Cursor getVideosByMovieId(
            Uri uri, String[] projection, String sortOrder) {
        String movieFromUri = MovieContract.VideoEntry.getMovieIdFromUri(uri);

        Cursor cursor = sVideoByMovieIdQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sMovieSelectionInVideoTable,
                new String[]{movieFromUri},
                null,
                null,
                sortOrder
        );

            return cursor;
    }

    private Cursor getReviewsByMovieId(
            Uri uri, String[] projection, String sortOrder) {
        String movieIdFromUri = MovieContract.ReviewEntry.getMovieIdFromReviewUri(uri);

        Cursor cursor = sReviewByMovieIdQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sMovieSelectionInReviewTable,
                new String[]{movieIdFromUri},
                null,
                null,
                sortOrder
        );

        cursor.moveToFirst();
        String strCursor = DatabaseUtils.dumpCursorToString(cursor);
        Log.v("Reviews by Id Cursor",strCursor );

        return cursor;
    }

    private Cursor getMovieDataByMovieId(
            Uri uri, String[] projection, String sortOrder) {
        String movieIdFromUri = MovieContract.ReviewEntry.getMovieIdFromUri(uri);
        sReviewAndVideoByMovieIdQueryBuilder.setDistinct(true);

        Cursor cursor = sReviewAndVideoByMovieIdQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sMovieSelection,
                new String[]{movieIdFromUri},
                null,
                null,
                sortOrder
        );

        return cursor;
    }

    private Cursor getVideoById(Uri uri, String[] projection, String sortOrder) {
        String videoFromUri = MovieContract.VideoEntry.getIdFromUri(uri);

        Cursor cursor = mOpenHelper.getReadableDatabase().query(
                MovieContract.VideoEntry.TABLE_NAME,
                projection,
                sVideoSelection,
                new String[]{videoFromUri},
                null,
                null,
                sortOrder
        );
        return cursor;
    }

    private Cursor getReviewById(Uri uri, String[] projection, String sortOrder) {
        String reviewIdFromUri = MovieContract.MovieEntry.getIdFromUri(uri);

        Cursor cursor = mOpenHelper.getReadableDatabase().query(
                MovieContract.ReviewEntry.TABLE_NAME,
                projection,
                sReviewSelection,
                new String[]{reviewIdFromUri},
                null,
                null,
                sortOrder
        );
        return cursor;
    }



    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case MOVIE:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
            }
                break;
            case MOVIE_FROM_ID: {
                retCursor = getMovieDataByMovieId(uri, projection, sortOrder);
                break;
            }
            case MOVIE_FAVORITE: {
                retCursor = mOpenHelper.getReadableDatabase().
                        rawQuery("SELECT * FROM movie WHERE favorite = '1'", null);
                break;
            }
            case MOVIE_POPULAR: {
                retCursor = mOpenHelper.getReadableDatabase().
                        rawQuery("SELECT * FROM movie WHERE popular = '1'", null);
                break;
            }
            case MOVIE_TOP_RATED: {
                retCursor = mOpenHelper.getReadableDatabase().
                        rawQuery("SELECT * FROM movie WHERE top_rated = '1'", null);
                break;
            }
            case VIDEO:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.VideoEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case VIDEO_FROM_ID: {
                retCursor = getVideoById(uri, projection, sortOrder);
                break;
            }
            case VIDEOS_FROM_MOVIE_ID:{
                retCursor = getVideosByMovieId(uri, projection, sortOrder);
                break;
            }
            case REVIEW:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.ReviewEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case REVIEW_FROM_ID: {
                retCursor = getReviewById(uri, projection, sortOrder);
                break;
            }
            case REVIEWS_FROM_MOVIE_ID:{
                retCursor = getReviewsByMovieId(uri, projection, sortOrder);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }

        retCursor.moveToFirst();
        Log.v("Cursor Object", DatabaseUtils.dumpCursorToString(retCursor));

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }


    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        String uriReturnType = null;
        switch (match) {
            case MOVIE:{
                uriReturnType = MovieContract.MovieEntry.CONTENT_TYPE;
                break;
            }
            case MOVIE_FROM_ID:{
                uriReturnType = MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
                break;
            }
            case MOVIE_POPULAR: {
                uriReturnType = MovieContract.MovieEntry.CONTENT_TYPE;
                break;
            }
            case MOVIE_TOP_RATED: {
                uriReturnType = MovieContract.MovieEntry.CONTENT_TYPE;
                break;
            }
            case MOVIE_FAVORITE: {
                uriReturnType = MovieContract.MovieEntry.CONTENT_TYPE;
                break;
            }
            case VIDEO:{
                uriReturnType = MovieContract.VideoEntry.CONTENT_TYPE;
                break;
            }
            case VIDEO_FROM_ID:{
                uriReturnType = MovieContract.VideoEntry.CONTENT_ITEM_TYPE;
                break;
            }
            case VIDEOS_FROM_MOVIE_ID:{
                uriReturnType = MovieContract.VideoEntry.CONTENT_TYPE;
                break;
            }
            case REVIEW:{
                uriReturnType = MovieContract.ReviewEntry.CONTENT_TYPE;
                break;
            }
            case REVIEW_FROM_ID:{
                uriReturnType = MovieContract.ReviewEntry.CONTENT_ITEM_TYPE;
                break;
            }
            case REVIEWS_FROM_MOVIE_ID:{
                uriReturnType = MovieContract.ReviewEntry.CONTENT_TYPE;
                break;
            }
        }
        return uriReturnType;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIE: {
                long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case VIDEO: {
                long _id = db.insert(MovieContract.VideoEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.VideoEntry.buildVideoUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case REVIEW: {
                long _id = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.ReviewEntry.buildReviewUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case MOVIE:
                rowsDeleted = db.delete(
                        MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case VIDEO:
                rowsDeleted = db.delete(
                        MovieContract.VideoEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case REVIEW:
                rowsDeleted = db.delete(
                        MovieContract.ReviewEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case MOVIE:
                rowsUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case VIDEO:
                rowsUpdated = db.update(MovieContract.VideoEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case REVIEW:
                rowsUpdated = db.update(MovieContract.ReviewEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        db.beginTransaction();
        int returnCount = 0;
        switch (match) {
            case MOVIE: {
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            case VIDEO: {
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.VideoEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            case REVIEW: {
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
