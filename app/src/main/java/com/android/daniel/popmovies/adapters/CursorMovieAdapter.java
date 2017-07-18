package com.android.daniel.popmovies.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.android.daniel.popmovies.R;
import com.android.daniel.popmovies.data.MovieContract;
import com.android.daniel.popmovies.utils.Constants;
import com.squareup.picasso.Picasso;

/**
 * Created by danie on 06/06/2017.
 */

public class CursorMovieAdapter extends CursorAdapter {

    public class MoviesCursorViewHolder {
        private final ImageView imgMovie;

        public MoviesCursorViewHolder(View itemView) {
            imgMovie = (ImageView) itemView.findViewById(R.id.img_grid_movie);
        }
    }

    public CursorMovieAdapter(Context context, Cursor c, int autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_gridview, parent, false);

        MoviesCursorViewHolder viewHolder = new MoviesCursorViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        MoviesCursorViewHolder viewHolder = (MoviesCursorViewHolder) view.getTag();

        int columnIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER);
        Picasso.with(context)
                .load(Constants.POSTER_BASE_URL_185 + cursor.getString(columnIndex))
                .into(viewHolder.imgMovie);
    }
}
