package com.android.daniel.popmovies.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.daniel.popmovies.R;
import com.android.daniel.popmovies.data.MovieContract;
import com.android.daniel.popmovies.models.Video;
import com.android.daniel.popmovies.utils.Constants;
import com.android.daniel.popmovies.utils.OnItemClickListener;
import com.squareup.picasso.Picasso;

/**
 * Created by danie on 17/07/2017.
 */

public class VideosCursorAdapter extends RecyclerVideoAdapter<VideosCursorAdapter.ViewHolder> {

    private Context mContext;
    private OnItemClickListener mItemClickListener;


    public VideosCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        mContext = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView mImageView;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            mImageView = (ImageView) view.findViewById(R.id.img_list_videos);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) mItemClickListener.onClick(v, getAdapterPosition());
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_listview_videos, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {

        String thumbPath = Constants.YOUTUBE_THUMB + cursor.getString(cursor.getColumnIndex(MovieContract.VideoEntry.COLUMN_PATH)) + "/mqdefault.jpg";
        Picasso.with(mContext)
                .load(thumbPath)
                .into(viewHolder.mImageView);
    }

    public void setClickListener(OnItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }
}