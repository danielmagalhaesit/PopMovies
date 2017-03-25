package com.android.daniel.popmovies.activities;


import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.daniel.popmovies.BuildConfig;
import com.android.daniel.popmovies.Utils.OnItemClickListener;
import com.android.daniel.popmovies.R;
import com.android.daniel.popmovies.Utils.Constants;
import com.android.daniel.popmovies.adapters.RecyclerMovieAdapter;
import com.android.daniel.popmovies.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements OnItemClickListener {

    RecyclerMovieAdapter mMovieAdapter;
    RecyclerView mRecyclerView;
    List<Movie> mMovieList;


    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Getting the Sort By preference from shared preferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        // The second parameter is the default in case the file is empty.
        String unitPref = sharedPreferences.getString(getString(R.string.pref_sort_key)
                , getString(R.string.pref_sort_default));
        FetchMovieTask fetchMovieTask = new FetchMovieTask();
        fetchMovieTask.execute(unitPref);
    }

    @Override
    public void onClick(View view, int position) {
        // Click event sending the movie information through the intent
        try{
            Intent intent = new Intent(getActivity(), DetailActivity.class);
            intent.putExtra(Constants.TITLE, mMovieList.get(position).getmTitle());
            intent.putExtra(Constants.POSTER, mMovieList.get(position).getmPoster());
            intent.putExtra(Constants.OVERVIEW, mMovieList.get(position).getmSynopsis());
            intent.putExtra(Constants.RELEASE_DATE, mMovieList.get(position).getmReleaseDate());
            intent.putExtra(Constants.VOTE_AVERAGE, mMovieList.get(position).getmVoteAverage());
            startActivity(intent);
        }catch (Exception e){
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    public class FetchMovieTask extends AsyncTask<String, Void, List>{

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

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
                return getMovieDataFromJson(moviesJsonStr);
            }catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        private List<Movie> getMovieDataFromJson(String movieJsonString)
                throws JSONException{

            List<Movie> movieList = new ArrayList<>();

            JSONObject jsonObject = new JSONObject(movieJsonString);
            JSONArray moviesJsonArray = jsonObject.getJSONArray(Constants.RESULTS);

            // Go through the JSON Array an population the movie list
            for (int i = 0 ; i < moviesJsonArray.length(); i++){
                JSONObject jsonMovie = moviesJsonArray.getJSONObject(i);
                Movie movie = new Movie();
                movie.setmId(jsonMovie.getInt(Constants.ID));
                movie.setmTitle(jsonMovie.getString(Constants.TITLE));
                movie.setmPoster(jsonMovie.getString(Constants.POSTER));
                movie.setmSynopsis(jsonMovie.getString(Constants.OVERVIEW));
                movie.setmReleaseDate(jsonMovie.getString(Constants.RELEASE_DATE));
                movie.setmVoteAverage(jsonMovie.getDouble(Constants.VOTE_AVERAGE));
                movieList.add(movie);
            }

            // Returning a list of Movies
            return movieList;
        }

        // Converting String date in Calendar
        /*private Calendar convertStringToCalendar(String dateString){
            Calendar cal = Calendar.getInstance();
            try{
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                cal.setTime(sdf.parse(dateString));
            }catch (ParseException ex){
                ex.printStackTrace();
            }
            return cal;
        }*/

        @Override
        protected void onPostExecute(List list) {

            // Find the recycler view
            mRecyclerView = (RecyclerView) getView().findViewById(R.id.recyclerview_movies);

            // Instantiating the adapter
            mMovieAdapter = new RecyclerMovieAdapter(list, getActivity());
            mMovieAdapter.setClickListener(MainFragment.this);
            mRecyclerView.setAdapter(mMovieAdapter);

            // Setting up the layout manager
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
            mRecyclerView.setLayoutManager(layoutManager);

            mRecyclerView.setHasFixedSize(true);
            mMovieList = list;
        }
    }

}
