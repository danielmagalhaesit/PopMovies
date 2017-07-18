package com.android.daniel.popmovies.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by danie on 16/05/2017.
 */

public class Review implements Parcelable {

    private String mId;
    private String mAuthor;
    private String mContent;

    public Review() {
    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getmAuthor() {
        return mAuthor;
    }

    public void setmAuthor(String mAuthor) {
        this.mAuthor = mAuthor;
    }

    public String getmContent() {
        return mContent;
    }

    public void setmContent(String mContent) {
        this.mContent = mContent;
    }

    protected Review(Parcel in){
        mId = in.readString();
        mAuthor = in.readString();
        mContent = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mAuthor);
        dest.writeString(mContent);
    }

    public static final Parcelable.Creator<Review> CREATOR = new Parcelable.Creator<Review>(){

        @Override
        public Review createFromParcel(Parcel source) {
            return new Review(source);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };
}
