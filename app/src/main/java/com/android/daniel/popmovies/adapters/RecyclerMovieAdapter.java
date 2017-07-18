package com.android.daniel.popmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.daniel.popmovies.utils.Constants;
import com.android.daniel.popmovies.utils.OnItemClickListener;
import com.android.daniel.popmovies.R;
import com.android.daniel.popmovies.models.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by daniel on 23/03/2017.
 */

public class RecyclerMovieAdapter extends RecyclerView.Adapter {

    private List<Movie> mMovieList;
    private Context mContext;
    private OnItemClickListener mItemClickListener;


    public RecyclerMovieAdapter(List<Movie> movies, Context context) {
        this.mMovieList = movies;
        this.mContext = context;
    }

    // Method to inflate the ViewHolder
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.recycler_img_movie, parent, false);

        return new MoviesViewHolder(view);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        MoviesViewHolder moviesViewHolder = (MoviesViewHolder) holder;

        Movie movie = mMovieList.get(position);

        Picasso.with(mContext)
                .load(Constants.POSTER_BASE_URL_185 + movie.getmPoster())
                .into(moviesViewHolder.imgMovie);

    }

    @Override
    public int getItemCount() {
        return mMovieList.size();
    }

    // Method needed in order to create a click listener in recycler view
    public void setClickListener(OnItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    public class MoviesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private final ImageView imgMovie;

        public MoviesViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            imgMovie = (ImageView) itemView.findViewById(R.id.img_movie);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) mItemClickListener.onClick(v, getAdapterPosition());
        }

    }
}