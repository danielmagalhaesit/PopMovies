package com.android.daniel.popmovies.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.daniel.popmovies.BuildConfig;
import com.android.daniel.popmovies.data.MovieContract;
import com.android.daniel.popmovies.models.Movie;
import com.android.daniel.popmovies.models.Review;
import com.android.daniel.popmovies.models.Video;
import com.android.daniel.popmovies.utils.Constants;

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
 * Created by danie on 02/08/2017.
 */

public class PopMovieService extends IntentService {


    private static final String LOG_TAG = PopMovieService.class.getSimpleName();
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public PopMovieService() {
        super("PopMovieService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        String movieOrVideo = intent.getStringExtra(Constants.MOVIE_OR_VIDEO);
        String sortBy = intent.getStringExtra(Constants.SORT_BY);
        String idMovieDb = intent.getStringExtra(Constants.MOVIE_DB_ID);
        String idMovieDatabase = intent.getStringExtra(Constants.MOVIE_ID);



        /*
        * param[o] = movie, video or review
        * param[1] = popular or top_rated
        * param[2] = ID from movieDB
        * param[3] = ID movie database
        */
        try{

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;
            Uri builtUri = null;

            switch (movieOrVideo) {
                case Constants.MOVIE: {
                    builtUri = Uri.parse(Constants.MOVIE_BASE_URL).buildUpon()
                            .appendEncodedPath(sortBy)
                            .appendQueryParameter(Constants.API_KEY, BuildConfig.THE_MOVIE_DB_API_KEY)
                            .build();
                    setMoviesFromJson(getJSON(builtUri), sortBy);
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
                inserted = this.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);
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
                inserted = this.getContentResolver().bulkInsert(MovieContract.VideoEntry.CONTENT_URI, cvArray);
            }

            Log.d(LOG_TAG, "Videos Complete. " + inserted + " Inserted");

        } catch (JSONException e){
            e.printStackTrace();
            Log.e(LOG_TAG, e.getMessage(), e);
        }
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
                inserted = this.getContentResolver().bulkInsert(MovieContract.ReviewEntry.CONTENT_URI, cvArray);
            }

            Log.d(LOG_TAG, "Reviews Complete. " + inserted + " Inserted");

        } catch (JSONException e){
            e.printStackTrace();
            Log.e(LOG_TAG, e.getMessage(), e);
        }
    }


}
