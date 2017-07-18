package com.android.daniel.popmovies.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import com.android.daniel.popmovies.utils.PollingCheck;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by danie on 19/05/2017.
 */

class TestUtilities extends AndroidTestCase {
    static final long TEST_MOVIE_ID = 550;
    public static final String TEST_VIDEO_ID = "54300b340e0a2646400007e6";

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    public static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {

        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String aexpectedValue = entry.getValue().toString();
            String avalue = valueCursor.getString(idx) ;
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    aexpectedValue + "'. " + error, aexpectedValue, avalue );
        }
    }

    public static ContentValues createVideoValues(long movieRowId) {
        ContentValues videoValues = new ContentValues();
        videoValues.put(MovieContract.VideoEntry.COLUMN_MOVIE_KEY, movieRowId);
        videoValues.put(MovieContract.VideoEntry.COLUMN_PATH, "-0wdVrC4OM4");
        videoValues.put(MovieContract.VideoEntry.COLUMN_VIDEO_ID, "54300b340e0a2646400007e6");

        return videoValues;
    }

    public static ContentValues createFightClub() {

        ContentValues testValues = new ContentValues();
        testValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, TEST_MOVIE_ID);
        testValues.put(MovieContract.MovieEntry.COLUMN_TITLE, "Fight Club");
        testValues.put(MovieContract.MovieEntry.COLUMN_POSTER, "lIv1QinFqz4dlp5U4lQ6HaiskOZ");
        testValues.put(MovieContract.MovieEntry.COLUMN_BACKDROP, "-0wdVrC4OM4");
        testValues.put(MovieContract.MovieEntry.COLUMN_SYNOPSIS, "TOP MOVIE");
        testValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, "1998-05-05");
        testValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, 8.8);
        testValues.put(MovieContract.MovieEntry.COLUMN_FAVORITE, 1);
        testValues.put(MovieContract.MovieEntry.COLUMN_TOP_RATED, 1);
        testValues.put(MovieContract.MovieEntry.COLUMN_POPULAR, 0);

        return testValues;

    }

    public static ContentValues createReviewValues(long movieRowId) {

        ContentValues reviewValues = new ContentValues();
        reviewValues.put(MovieContract.ReviewEntry.COLUMN_MOVIE_KEY, movieRowId);
        reviewValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_ID, "547e6075c3a368256200022f");
        reviewValues.put(MovieContract.ReviewEntry.COLUMN_AUTHOR, "-anthonypagan1975");
        reviewValues.put(MovieContract.ReviewEntry.COLUMN_CONTENT, "It was good. Although I wish it had more action scenes. It's worth watching ago don't miss out!\",\n" +
                "            \"url\": \"https://www.themoviedb.org/review/547e6075c3a368256200022f");

        return reviewValues;
    }


    static long insertFightClubValues(Context context) {
        // insert our test records into the database
        MovieDBHelper dbHelper = new MovieDBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues fightClubValues = TestUtilities.createFightClub();

        long movieRowId;
        movieRowId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, fightClubValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert North Pole Location Values", movieRowId != -1);

        return movieRowId;
    }

    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }


}
