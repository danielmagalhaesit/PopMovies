package com.android.daniel.popmovies.activities;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.daniel.popmovies.utils.Constants;
import com.android.daniel.popmovies.R;
import com.android.daniel.popmovies.models.Movie;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container_detail, new DetailFragment())
                    .commit();
        }
    }

    public static class DetailFragment extends Fragment {

        private Movie mMovie;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

            // Getting the information through the intent
            Intent intent = getActivity().getIntent();
            if (intent != null) {
                // Getting a Parcelable object
                mMovie =  intent.getParcelableExtra(Constants.PARCELABLE_MOVIE);
            }

            // Setting up the views
            ImageView imageViewPoster = (ImageView) rootView.findViewById(R.id.imageview_detail_poster);
            Picasso.with(getActivity())
                    .load(Constants.POSTER_BASE_URL_500 + mMovie.getmPoster())
                    .into(imageViewPoster);
            ((TextView) rootView.findViewById(R.id.textview_title)).setText(mMovie.getmTitle());
            ((TextView) rootView.findViewById(R.id.textview_rating))
                    .setText("" + mMovie.getmVoteAverage());
            ((TextView) rootView.findViewById(R.id.textview_release_date)).setText(mMovie.getmReleaseDate());
            ((TextView) rootView.findViewById(R.id.textview_overview)).setText(mMovie.getmSynopsis());

            return rootView;
        }
    }


}
