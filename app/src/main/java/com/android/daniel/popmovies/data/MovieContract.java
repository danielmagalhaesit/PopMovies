package com.android.daniel.popmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.renderscript.Long2;

/**
 * Created by danie on 16/05/2017.
 */

public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.android.daniel.popmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movie";
    public static final String PATH_VIDEO = "video";
    public static final String PATH_REVIEW = "review";

    public static final class MovieEntry implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;


        public static final String TABLE_NAME = "movie";

        // Movie id returned by API
        public static final String COLUMN_MOVIE_ID = "movie_id";
        // Movie title
        public static final String COLUMN_TITLE = "title";
        // Poster path
        public static final String COLUMN_POSTER = "poster";
        // Backdrop path
        public static final String COLUMN_BACKDROP = "backdrop";
        // String
        public static final String COLUMN_SYNOPSIS = "synopsis";
        // String
        public static final String COLUMN_RELEASE_DATE = "release_date";
        // double
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        // boolean
        public static final String COLUMN_FAVORITE = "favorite";
        // boolean
        public static final String COLUMN_POPULAR = "popular";
        // boolean
        public static final String COLUMN_TOP_RATED = "top_rated";


        // Build URI from movieId
        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMoviePopTopFavUri(String type) {
            Uri uriResult = CONTENT_URI.buildUpon().appendPath(type).build();
            return uriResult;
        }

        public static String getIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static String getTypeFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }

    public static final class VideoEntry implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_VIDEO).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VIDEO;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VIDEO;

        public static final String TABLE_NAME = "video";

        public static final String COLUMN_VIDEO_ID = "video_id";
        // Foreign key - movie table
        public static final String COLUMN_MOVIE_KEY = "movie_id";

        public static final String COLUMN_PATH = "path";

        public static Uri buildVideoUri(long idVideo) {
            return ContentUris.withAppendedId(CONTENT_URI, idVideo);
        }

        public static Uri buildUriWithVideoId(String idVideo) {
            Uri uriResult = CONTENT_URI.buildUpon().appendPath(idVideo).build();
            return uriResult;
        }

        public static String getMovieIdFromUri(Uri uri) {
            String movieId = uri.getPathSegments().get(2);
            return movieId;
        }

        public static String getIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        // content://com.android.daniel.popmovies/video/movie/550
        public static Uri buildVideoUriWithMovieId(long movieId){
            Uri uriResult = CONTENT_URI.buildUpon().appendPath(MovieContract.PATH_MOVIE).
                    appendPath(Long.toString(movieId)).build();
            return uriResult;
        }
    }

    public static final class ReviewEntry implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEW).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;


        public static final String TABLE_NAME = "review";

        public static final String COLUMN_REVIEW_ID = "review_id";

        // Column with the foreign key into the MOVIE table.
        public static final String COLUMN_MOVIE_KEY = "movie_id";

        public static final String COLUMN_AUTHOR = "author";

        public static final String COLUMN_CONTENT = "content";


        public static Uri buildReviewUri(long idReview) {
            return ContentUris.withAppendedId(CONTENT_URI, idReview);
        }

        public static Uri buildUriReviewFromId(String idReview) {
            Uri uri = CONTENT_URI.buildUpon().appendPath(idReview).build();
            return uri;
        }

        public static String getMovieIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static String getMovieIdFromReviewUri(Uri uri) {
            return uri.getPathSegments().get(2);

        }

        // content://com.android.daniel.popmovies/review/movie/131631
        public static Uri buildReviewUriWithMovieId(long movieId) {
            Uri uriResult = CONTENT_URI.buildUpon().appendPath(PATH_MOVIE)
                    .appendPath(Long.toString(movieId)).build();
            return uriResult;
        }
    }

}
