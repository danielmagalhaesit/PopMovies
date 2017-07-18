package com.android.daniel.popmovies.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by daniel on 23/03/2017.
 */


public class Movie implements Parcelable {


    private int mId;
    private String mTitle;
    private String mPoster;
    private String mBackdrop;
    private String mSynopsis;
    private String mReleaseDate;
    private double mVoteAverage;
    private boolean mFavorite;
    private boolean mIsPopular;
    private boolean mIsTopRated;

    public Movie() {
    }

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmPoster() {
        return mPoster;
    }

    public void setmPoster(String mPoster) {
        this.mPoster =  mPoster;
    }

    public String getmBackdrop() {
        return mBackdrop;
    }

    public void setmBackdrop(String mBackdrop) {
        this.mBackdrop = mBackdrop;
    }

    public String getmSynopsis() {
        return mSynopsis;
    }

    public void setmSynopsis(String mSynopsis) {
        this.mSynopsis = mSynopsis;
    }

    public String getmReleaseDate() {
        return mReleaseDate;
    }

    public void setmReleaseDate(String mReleaseDate) {
        this.mReleaseDate = mReleaseDate;
    }

    public double getmVoteAverage() {
        return mVoteAverage;
    }

    public void setmVoteAverage(double mVoteAverage) {
        this.mVoteAverage = mVoteAverage;
    }

    public boolean ismFavorite() {
        return mFavorite;
    }

    public void setmFavorite(boolean mFavorite) {
        this.mFavorite = mFavorite;
    }

    public boolean getmIsPopular() {
        return mIsPopular;
    }

    public void setmIsPopular(boolean mIsPopular) {
        this.mIsPopular = mIsPopular;
    }

    public boolean getmIsTopRated() {
        return mIsTopRated;
    }

    public void setmIsTopRated(boolean mIsTopRated) {
        this.mIsTopRated = mIsTopRated;
    }

    protected Movie(Parcel in) {
        mId = in.readInt();
        mTitle = in.readString();
        mPoster = in.readString();
        mBackdrop = in.readString();
        mSynopsis = in.readString();
        mReleaseDate = in.readString();
        mVoteAverage = in.readDouble();
        mFavorite = in.readByte() != 0;
        mIsPopular = in.readByte() != 0;
        mIsTopRated = in.readByte() != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mTitle);
        dest.writeString(mPoster);
        dest.writeString(mBackdrop);
        dest.writeString(mSynopsis);
        dest.writeString(mReleaseDate);
        dest.writeDouble(mVoteAverage);
        dest.writeByte((byte) (mFavorite? 1 :0));
        dest.writeByte((byte) (mIsPopular? 1 :0));
        dest.writeByte((byte) (mIsTopRated? 1 :0));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
