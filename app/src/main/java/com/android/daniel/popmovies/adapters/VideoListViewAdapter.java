package com.android.daniel.popmovies.adapters;

import android.content.Context;
import android.database.Cursor;
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
 * Created by danie on 17/07/2017.
 */

public class VideoListViewAdapter extends CursorAdapter {

    public class VideosViewHolder {
        private final ImageView imgVideo;

        public VideosViewHolder(View itemView) {
            imgVideo = (ImageView) itemView.findViewById(R.id.img_list_videos);
        }
    }

    public VideoListViewAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_listview_videos, parent, false);

        VideoListViewAdapter.VideosViewHolder viewHolder = new VideoListViewAdapter.VideosViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        VideosViewHolder viewHolder = (VideosViewHolder) view.getTag();

        int columnIndex = cursor.getColumnIndex(MovieContract.VideoEntry.COLUMN_PATH);
        String pathThumb = Constants.YOUTUBE_THUMB +  cursor.getString(columnIndex) + "/mqdefault.jpg";
        Picasso.with(context)
                .load(pathThumb)
                .into(viewHolder.imgVideo);
    }
}
