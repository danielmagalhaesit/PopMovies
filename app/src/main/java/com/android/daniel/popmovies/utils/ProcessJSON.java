package com.android.daniel.popmovies.utils;

import android.util.Log;

import com.android.daniel.popmovies.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by daniel on 30/03/2017.
 */

public class ProcessJSON {

    private static final String LOG_TAG = ProcessJSON.class.getSimpleName();

    public static List<Movie> getMovieListFromJson(String movieJsonString){

        List<Movie> movieList = new ArrayList<>();
        try{
            JSONObject jsonObject = new JSONObject(movieJsonString);
            JSONArray moviesJsonArray = jsonObject.getJSONArray(Constants.RESULTS);

            // Go through the JSON Array an population the movie list
            for (int i = 0 ; i < moviesJsonArray.length(); i++){
                JSONObject jsonMovie = moviesJsonArray.getJSONObject(i);
                Movie movie = new Movie();
                movie.setmId(jsonMovie.getInt(Constants.ID));
                movie.setmTitle(jsonMovie.optString(Constants.TITLE));
                movie.setmPoster(jsonMovie.optString(Constants.POSTER));
                movie.setmSynopsis(jsonMovie.optString(Constants.OVERVIEW));
                movie.setmReleaseDate(jsonMovie.optString(Constants.RELEASE_DATE));
                movie.setmVoteAverage(jsonMovie.optDouble(Constants.VOTE_AVERAGE));
                movieList.add(movie);
            }
        } catch (JSONException e){
            e.printStackTrace();
            Log.e(LOG_TAG, e.getMessage(), e);
        }

        // Returning a list of Movies
        return movieList;
    }
}
