package com.android.daniel.popmovies.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.android.daniel.popmovies.BuildConfig;
import com.android.daniel.popmovies.R;
import com.android.daniel.popmovies.data.MovieContract;
import com.android.daniel.popmovies.models.Movie;
import com.android.daniel.popmovies.models.Review;
import com.android.daniel.popmovies.models.Video;
import com.android.daniel.popmovies.utils.Constants;
import com.android.daniel.popmovies.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Created by danie on 03/08/2017.
 */

public class PopMovieSyncAdapter extends AbstractThreadedSyncAdapter {

    private final String LOG_TAG = PopMovieSyncAdapter.class.getSimpleName();

    public static final int SYNC_INTERVAL = 60 * 360;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;

    public PopMovieSyncAdapter(Context context, boolean autoInitialize){
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.v(LOG_TAG, "onPerformSync Called.");

        String movieOrVideo = extras.getString(Constants.MOVIE_OR_VIDEO);
//        String sortBy = Utility.getSortBy(getContext());
        String idMovieDb = extras.getString(Constants.MOVIE_DB_ID);
        String idMovieDatabase = extras.getString(Constants.MOVIE_ID);

        try{

            Uri builtUri = null;

            switch (movieOrVideo) {
                case Constants.MOVIE: {
                    builtUri = Uri.parse(Constants.MOVIE_BASE_URL).buildUpon()
                            .appendEncodedPath(Constants.POPULAR)
                            .appendQueryParameter(Constants.API_KEY, BuildConfig.THE_MOVIE_DB_API_KEY)
                            .build();
                    setMoviesFromJson(getJSON(builtUri), Constants.POPULAR);

                    builtUri = Uri.parse(Constants.MOVIE_BASE_URL).buildUpon()
                            .appendEncodedPath(Constants.TOP_RATED)
                            .appendQueryParameter(Constants.API_KEY, BuildConfig.THE_MOVIE_DB_API_KEY)
                            .build();
                    setMoviesFromJson(getJSON(builtUri), Constants.TOP_RATED);
                    break;
                }
                case Constants.VIDEO: {

                    String uriString = Constants.MOVIE_BASE_URL + idMovieDb + "/videos?" + Constants.API_KEY + "=" + BuildConfig.THE_MOVIE_DB_API_KEY;
                    builtUri = Uri.parse(uriString);
                    setVideosFromJson(getJSON(builtUri), idMovieDatabase);

                    uriString = Constants.MOVIE_BASE_URL + idMovieDb + "/reviews?" + Constants.API_KEY + "=" + BuildConfig.THE_MOVIE_DB_API_KEY;
                    builtUri = Uri.parse(uriString);
                    setReviewsFromJson(getJSON(builtUri), idMovieDatabase);


//                builtUri = Uri.parse(Constants.MOVIE_BASE_URL).buildUpon()
//                        .appendEncodedPath(params[2])
//                        .path("videos")
//                        .appendQueryParameter(Constants.API_KEY, BuildConfig.THE_MOVIE_DB_API_KEY)
//                        .build();
                /*Uri.parse(https://api.themoviedb.org/3/movie/).buildUpon() .appendEncodedPath(22222) .path("videos") .appendQueryParameter("api_key", "ddsafdsdfafsafsd") .build();*/
                    break;
                }
            }
            return;
        } catch (Exception e){
            e.printStackTrace();
            return;
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    public static void syncImmediately(Context context, Bundle bundle) {
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */

    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }



    public void setMoviesFromJson(String jsonString, String topOrBestMovies){

        try{
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray moviesJsonArray = jsonObject.getJSONArray(Constants.RESULTS);

            Vector<ContentValues> cVVectorMovie = new Vector<ContentValues>(moviesJsonArray.length());
            // Going through the JSON Array and populate the movie list
            for (int i = 0 ; i < moviesJsonArray.length(); i++){
                JSONObject jsonMovie = moviesJsonArray.getJSONObject(i);
                Movie movie = new Movie();
                movie.setmId(jsonMovie.getInt(Constants.ID));
                movie.setmTitle(jsonMovie.optString(Constants.TITLE));
                movie.setmPoster(jsonMovie.optString(Constants.POSTER));
                movie.setmBackdrop(jsonMovie.optString(Constants.BACKDROP));
                movie.setmSynopsis(jsonMovie.optString(Constants.OVERVIEW));
                movie.setmReleaseDate(jsonMovie.optString(Constants.RELEASE_DATE));
                movie.setmVoteAverage(jsonMovie.optDouble(Constants.VOTE_AVERAGE));

                if(topOrBestMovies.equals(Constants.POPULAR)){
                    movie.setmIsPopular(true);
                }
                if (topOrBestMovies.equals(Constants.TOP_RATED)){
                    movie.setmIsTopRated(true);
                }

                ContentValues movieValues = new ContentValues();

                movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.getmId());
                movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.getmTitle());
                movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER, movie.getmPoster());
                movieValues.put(MovieContract.MovieEntry.COLUMN_BACKDROP, movie.getmBackdrop());
                movieValues.put(MovieContract.MovieEntry.COLUMN_SYNOPSIS, movie.getmSynopsis());
                movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getmReleaseDate());
                movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, movie.getmVoteAverage());
                movieValues.put(MovieContract.MovieEntry.COLUMN_POPULAR, movie.getmIsPopular());
                movieValues.put(MovieContract.MovieEntry.COLUMN_TOP_RATED, movie.getmIsTopRated());

                cVVectorMovie.add(movieValues);
            }
            int inserted = 0;
            // add to database
            if ( cVVectorMovie.size() > 0 ) {
                ContentValues[] cvArray = new ContentValues[cVVectorMovie.size()];
                cVVectorMovie.toArray(cvArray);
                inserted = getContext().getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);
            }

            Log.d(LOG_TAG, "Movies Complete. " + inserted + " Inserted");

        } catch (JSONException e){
            e.printStackTrace();
            Log.e(LOG_TAG, e.getMessage(), e);
        }
    }

    public void setVideosFromJson(String jsonString, String movieId){

        try{
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray videoJsonArray = jsonObject.getJSONArray(Constants.RESULTS);

            Vector<ContentValues> cVVectorVideo = new Vector<ContentValues>(videoJsonArray.length());
            // Going through the JSON Array and populate the movie list
            for (int i = 0 ; i < videoJsonArray.length(); i++){
                JSONObject jsonVideo = videoJsonArray.getJSONObject(i);
                Video video = new Video();
                video.setmId(jsonVideo.optString(Constants.ID));
                video.setmPath(jsonVideo.optString(Constants.VIDEO_KEY));

                ContentValues videoValues = new ContentValues();

                videoValues.put(MovieContract.VideoEntry.COLUMN_VIDEO_ID, video.getmId());
                videoValues.put(MovieContract.VideoEntry.COLUMN_PATH, video.getmPath());
                videoValues.put(MovieContract.VideoEntry.COLUMN_MOVIE_KEY, movieId);

                cVVectorVideo.add(videoValues);
            }
            int inserted = 0;
            // add to database
            if ( cVVectorVideo.size() > 0 ) {
                ContentValues[] cvArray = new ContentValues[cVVectorVideo.size()];
                cVVectorVideo.toArray(cvArray);
                inserted = getContext().getContentResolver().bulkInsert(MovieContract.VideoEntry.CONTENT_URI, cvArray);
            }

            Log.d(LOG_TAG, "Videos Complete. " + inserted + " Inserted");

        } catch (JSONException e){
            e.printStackTrace();
            Log.e(LOG_TAG, e.getMessage(), e);
        }
    }
    public String getJSON(Uri uriGetJson) {
        String jsonString;
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {

            URL url = new URL(uriGetJson.toString());

            Log.v(LOG_TAG, "Built URI Video " + uriGetJson.toString());

            // Create the request and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;
            }

            jsonString = buffer.toString();

            Log.v(LOG_TAG, "Json String: " + jsonString);

        } catch (RuntimeException | IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return jsonString;
    }

    public void setReviewsFromJson(String jsonString, String movieId){

        try{
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray reviewJsonArray = jsonObject.getJSONArray(Constants.RESULTS);

            Vector<ContentValues> cVVectorVideo = new Vector<ContentValues>(reviewJsonArray.length());
            // Going through the JSON Array and populate the movie list
            for (int i = 0 ; i < reviewJsonArray.length(); i++){
                JSONObject jsonReview = reviewJsonArray.getJSONObject(i);
                Review review = new Review();
                review.setmId(jsonReview.optString(Constants.ID));
                review.setmContent(jsonReview.optString(Constants.REVIEW_CONTENT));
                review.setmAuthor(jsonReview.optString(Constants.REVIEW_AUTHOR));

                ContentValues reviewValues = new ContentValues();

                reviewValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_ID, review.getmId());
                reviewValues.put(MovieContract.ReviewEntry.COLUMN_CONTENT, review.getmContent());
                reviewValues.put(MovieContract.ReviewEntry.COLUMN_AUTHOR, review.getmAuthor());
                reviewValues.put(MovieContract.ReviewEntry.COLUMN_MOVIE_KEY, movieId);

                cVVectorVideo.add(reviewValues);
            }
            int inserted = 0;
            // add to database
            if ( cVVectorVideo.size() > 0 ) {
                ContentValues[] cvArray = new ContentValues[cVVectorVideo.size()];
                cVVectorVideo.toArray(cvArray);
                inserted = getContext().getContentResolver().bulkInsert(MovieContract.ReviewEntry.CONTENT_URI, cvArray);
            }

            Log.d(LOG_TAG, "Reviews Complete. " + inserted + " Inserted");

        } catch (JSONException e){
            e.printStackTrace();
            Log.e(LOG_TAG, e.getMessage(), e);
        }
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }


    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        PopMovieSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

}
