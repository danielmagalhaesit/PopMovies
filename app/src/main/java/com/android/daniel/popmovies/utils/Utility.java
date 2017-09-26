package com.android.daniel.popmovies.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.android.daniel.popmovies.R;
import com.android.daniel.popmovies.sync.PopMovieSyncAdapter;

/**
 * Created by danie on 08/06/2017.
 */

public class Utility {

    public static String getSortBy(Context context){
        // Getting the Sort By preference from shared preferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        // The second parameter is the default in case the file is empty.
        final String sortBy = sharedPreferences.getString(context.getString(R.string.pref_sort_key)
                , context.getString(R.string.pref_sort_default));

        return sortBy;
    }

    public static void setSortBy(Context context, String sortBy){
        // Getting the Sort By preference from shared preferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getString(R.string.pref_sort_key), sortBy);
        editor.commit();
    }

    @SuppressWarnings("ResourceType")
    static public @PopMovieSyncAdapter.MovieStatus int getMovieStatus(Context context){
        // Getting the Movie Status preference from shared preferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        // The second parameter is the default in case the file is empty.
        return sharedPreferences.getInt(context.getString(R.string.movie_status)
                , PopMovieSyncAdapter.STATUS_UNKNOWN);

    }
}
