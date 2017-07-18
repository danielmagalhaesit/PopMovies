package com.android.daniel.popmovies.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.android.daniel.popmovies.R;

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
}
