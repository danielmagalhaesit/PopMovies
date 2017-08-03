package com.android.daniel.popmovies.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.daniel.popmovies.R;
import com.android.daniel.popmovies.data.MovieContract;
import com.android.daniel.popmovies.models.Review;

/**
 * Created by danie on 18/07/2017.
 */

public class ReviewsCursorAdapter extends CursorRecyclerViewAdapter<ReviewsCursorAdapter.ViewHolder> {

    public Context mContext;

    public ReviewsCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        mContext = context;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextViewAuthor;
        public TextView mTextViewContent;

        public ViewHolder(View view) {
            super(view);
            mTextViewAuthor = (TextView) view.findViewById(R.id.tv_author_review);
            mTextViewContent = (TextView) view.findViewById(R.id.tv_content_review);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_reviews, parent, false);

        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {

        Review review = new Review();
        review.setmAuthor(cursor.getString(cursor.getColumnIndex(MovieContract.ReviewEntry.COLUMN_AUTHOR)));
        review.setmContent(cursor.getString(cursor.getColumnIndex(MovieContract.ReviewEntry.COLUMN_CONTENT)));
        viewHolder.mTextViewAuthor.setText(review.getmAuthor());
        viewHolder.mTextViewContent.setText(review.getmContent());

    }

}
