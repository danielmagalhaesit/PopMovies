package com.android.daniel.popmovies.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.daniel.popmovies.R;
import com.android.daniel.popmovies.models.Video;
import com.android.daniel.popmovies.utils.Constants;
import com.android.daniel.popmovies.utils.OnItemClickListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by danie on 12/07/2017.
 */

public abstract class RecyclerVideoAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

//    private List<String> mVideoList;

    private Cursor mCursor;
    private Context mContext;
    private Boolean mDataValid;
    private int mRowIdColumn;
    private DataSetObserver mDataSetObserver;
//    private OnItemClickListener mItemClickListener;


    public RecyclerVideoAdapter(Context context, Cursor cursor) {
        this.mCursor = cursor;
        this.mContext = context;
        mDataValid = cursor != null;
        mRowIdColumn = mDataValid ? mCursor.getColumnIndex("_id"): -1;
        mDataSetObserver = new NotifyingDataSetObserver();
        if (mCursor != null){
            mCursor.registerDataSetObserver(mDataSetObserver);
        }

    }

    public Cursor getCursor(){
            return mCursor;

    }

    @Override
    public long getItemId(int position) {
        if(mDataValid && mCursor != null && mCursor.moveToPosition(position)){
            return mCursor.getLong(mRowIdColumn);
        }
        return 0;
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }
//
//    public class VideosViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
//        private final ImageView imgVideo;
//
//        public VideosViewHolder(View itemView) {
//            super(itemView);
//            itemView.setOnClickListener(this);
//            imgVideo = (ImageView) itemView.findViewById(R.id.img_list_videos);
//        }
//
//        @Override
//        public void onClick(View v) {
//            if (mItemClickListener != null) mItemClickListener.onClick(v, getAdapterPosition());
//        }
//    }
//
//
//    @Override
//    public VideosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_listview_videos, parent, false);
//        VideosViewHolder videosViewHolder = new VideosViewHolder(view);
//
//        return videosViewHolder;
//    }

    public abstract void onBindViewHolder(VH viewHolder, Cursor cursor);

    @Override
    public void onBindViewHolder(VH holder, int position) {

        if (!mDataValid){
            throw new IllegalStateException("this should only be called when the cursor is valid");
        }
        if(!mCursor.moveToPosition(position)){
            throw new IllegalStateException("couldn't move cursor to position " + position);
        }

        onBindViewHolder(holder, mCursor);

    }

    public void changeCursor(Cursor cursor) {
        Cursor old = swapCursor(cursor);
        if (old != null) {
            old.close();
        }
    }

    /**
            * Swap in a new Cursor, returning the old Cursor.  Unlike
     * {@link #changeCursor(Cursor)}, the returned old Cursor is <em>not</em>
            * closed.
     */
    public Cursor swapCursor(Cursor newCursor) {
        if (newCursor == mCursor) {
            return null;
        }
        final Cursor oldCursor = mCursor;
        if (oldCursor != null && mDataSetObserver != null) {
            oldCursor.unregisterDataSetObserver(mDataSetObserver);
        }
        mCursor = newCursor;
        if (mCursor != null) {
            if (mDataSetObserver != null) {
                mCursor.registerDataSetObserver(mDataSetObserver);
            }
            mRowIdColumn = newCursor.getColumnIndexOrThrow("_id");
            mDataValid = true;
            notifyDataSetChanged();
        } else {
            mRowIdColumn = -1;
            mDataValid = false;
            notifyDataSetChanged();
            //There is no notifyDataSetInvalidated() method in RecyclerView.Adapter
        }
        return oldCursor;
    }

    @Override
    public int getItemCount() {
        if (mDataValid && mCursor != null){
            return mCursor.getCount();
        }
        return 0;
    }

    private class NotifyingDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            super.onChanged();
            mDataValid = true;
            notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
            mDataValid = false;
            notifyDataSetChanged();
            //There is no notifyDataSetInvalidated() method in RecyclerView.Adapter
        }
    }



}
