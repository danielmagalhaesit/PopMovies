package com.android.daniel.popmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.android.daniel.popmovies.data.MovieContract.MovieEntry;
import com.android.daniel.popmovies.data.MovieContract.VideoEntry;
import com.android.daniel.popmovies.data.MovieContract.ReviewEntry;

/**
 * Created by danie on 17/05/2017.
 */

public class MovieDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 5;

    static final String DATABASE_NAME = "popmovie.db";

    public MovieDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_POSTER + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_BACKDROP + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_SYNOPSIS + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL, " +
                MovieEntry.COLUMN_FAVORITE + " INTEGER DEFAULT 0, " +
                MovieEntry.COLUMN_POPULAR + " INTEGER DEFAULT 0 ," +
                MovieEntry.COLUMN_TOP_RATED + " INTEGER DEFAULT 0 ," +

                " UNIQUE (" + MovieEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_VIDEO_TABLE = "CREATE TABLE " + VideoEntry.TABLE_NAME + " (" +
                VideoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                VideoEntry.COLUMN_VIDEO_ID + " TEXT NOT NULL, " +
                VideoEntry.COLUMN_MOVIE_KEY+ " INTEGER NOT NULL, " +
                VideoEntry.COLUMN_PATH + " TEXT NOT NULL, " +

                " FOREIGN KEY (" + VideoEntry.COLUMN_MOVIE_KEY + ") REFERENCES " +
                MovieEntry.TABLE_NAME + " (" + MovieEntry._ID + ") ON DELETE CASCADE, " +
                " UNIQUE (" + VideoEntry.COLUMN_VIDEO_ID + ") ON CONFLICT REPLACE);";


        final String SQL_CREATE_REVIEW_TABLE = "CREATE TABLE " + ReviewEntry.TABLE_NAME + " (" +
                ReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ReviewEntry.COLUMN_REVIEW_ID+ " TEXT NOT NULL, " +
                ReviewEntry.COLUMN_MOVIE_KEY+ " INTEGER NOT NULL, " +
                ReviewEntry.COLUMN_AUTHOR + " TEXT NOT NULL, " +
                ReviewEntry.COLUMN_CONTENT + " TEXT NOT NULL, " +

                " FOREIGN KEY (" + ReviewEntry.COLUMN_MOVIE_KEY + ") REFERENCES " +
                MovieEntry.TABLE_NAME + " (" + MovieEntry._ID + ") ON DELETE CASCADE," +
                " UNIQUE (" + ReviewEntry.COLUMN_REVIEW_ID + ") ON CONFLICT REPLACE);";



        db.execSQL(SQL_CREATE_MOVIE_TABLE);
        db.execSQL(SQL_CREATE_VIDEO_TABLE);
        db.execSQL(SQL_CREATE_REVIEW_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + VideoEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ReviewEntry.TABLE_NAME);
        onCreate(db);

    }
}
