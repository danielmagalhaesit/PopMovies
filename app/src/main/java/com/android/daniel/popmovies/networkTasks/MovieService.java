package com.android.daniel.popmovies.networkTasks;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.android.daniel.popmovies.BuildConfig;
import com.android.daniel.popmovies.utils.Constants;
import com.android.daniel.popmovies.utils.ProcessJSON;
import com.android.daniel.popmovies.models.Movie;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by daniel on 30/03/2017.
 */

public class MovieService extends AsyncTask <String, Void, List> {

    private static final String LOG_TAG = MovieService.class.getSimpleName();
    private AsyncTaskDelegate asyncTaskDelegate = null;
    private Context mContext;

    public MovieService(Context context, AsyncTaskDelegate response) {
        mContext = context;
        asyncTaskDelegate = response;
    }

    @Override
    protected List<Movie> doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String moviesJsonStr = null;

        try {
            // Building a URL
            Uri builtUri = Uri.parse(Constants.MOVIE_BASE_URL).buildUpon()
                    .appendEncodedPath(params[0])
                    .appendQueryParameter(Constants.API_KEY, BuildConfig.THE_MOVIE_DB_API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());

            Log.v(LOG_TAG, "Built URI " + builtUri.toString());

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

            moviesJsonStr = buffer.toString();

            Log.v(LOG_TAG, "Movies Json: " + moviesJsonStr);

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
        try{
            return ProcessJSON.getMovieListFromJson(moviesJsonStr);
        }catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List list) {
        super.onPostExecute(list);
        if (asyncTaskDelegate != null){
            asyncTaskDelegate.processFinish(list);
        }
    }

    public Context getContext(){
        return mContext;
    }
}
