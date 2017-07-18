package com.android.daniel.popmovies.models;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.android.daniel.popmovies.data.MovieContract;

/**
 * Created by danie on 17/05/2017.
 */

public class Video implements Parcelable {

    private String mId;
    private String mPath;

    public Video() {
    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getmPath() {
        return mPath;
    }

    public void setmPath(String mPath) {
        this.mPath = mPath;
    }

    protected Video(Parcel in){
        mId = in.readString();
        mPath = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mPath);
    }

    public static final Parcelable.Creator<Video> CREATOR = new Parcelable.Creator<Video>(){

        @Override
        public Video createFromParcel(Parcel source) {
            return new Video(source);
        }

        @Override
        public Video[] newArray(int size) {
            return new Video[size];
        }
    };

    public static Video fromCursor(Cursor cursor) {
        Video video = new Video();

        video.setmId(cursor.getString(cursor.getColumnIndex(MovieContract.VideoEntry.COLUMN_VIDEO_ID)));
        video.setmPath(cursor.getString(cursor.getColumnIndex(MovieContract.VideoEntry.COLUMN_PATH)));

        return video;
    }
}


