package com.android.daniel.popmovies.data;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.test.AndroidTestCase;
import android.util.Log;

import com.android.daniel.popmovies.utils.Constants;

/**
 * Created by danie on 20/05/2017.
 */

public class TestProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    /*
       This helper function deletes all records from both database tables using the ContentProvider.
       It also queries the ContentProvider to make sure that the database has been successfully
       deleted, so it cannot be used until the Query and Delete functions have been written
       in the ContentProvider.

       Students: Replace the calls to deleteAllRecordsFromDB with this one after you have written
       the delete functionality in the ContentProvider.
     */
    public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                MovieContract.VideoEntry.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                MovieContract.ReviewEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Movie table during delete", 0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                MovieContract.VideoEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Video table during delete", 0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                MovieContract.ReviewEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Review table during delete", 0, cursor.getCount());
        cursor.close();
    }

    /*
    Student: Refactor this function to use the deleteAllRecordsFromProvider functionality once
    you have implemented delete functionality there.
 */
    public void deleteAllRecords() {
        deleteAllRecordsFromProvider();
    }

    // Since we want each test to start with a clean slate, run deleteAllRecords
    // in setUp (called by the test runner before each test).
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    /*
        This test checks to make sure that the content provider is registered correctly.
        Students: Uncomment this test to make sure you've correctly registered the WeatherProvider.
     */
    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        // We define the component name based on the package name from the context and the
        // WeatherProvider class.
        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                MovieProvider.class.getName());
        try {
            // Fetch the provider info using the component name from the PackageManager
            // This throws an exception if the provider isn't registered.
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            // Make sure that the registered authority matches the authority from the Contract.
            assertEquals("Error: WeatherProvider registered with authority: " + providerInfo.authority +
                            " instead of authority: " + MovieContract.CONTENT_AUTHORITY,
                    providerInfo.authority, MovieContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            // I guess the provider isn't registered correctly.
            assertTrue("Error: WeatherProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }

    /*
            This test doesn't touch the database.  It verifies that the ContentProvider returns
            the correct type for each type of URI that it can handle.
            Students: Uncomment this test to verify that your implementation of GetType is
            functioning correctly.
         */
    public void testGetType() {
        // This returns movie list
        // content://com.android.daniel.popmovies/movie/
        String type = mContext.getContentResolver().getType(MovieContract.MovieEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.android.daniel.popmovies/movie
        assertEquals("Error: the MovieEntry CONTENT_URI should return MovieEntry.CONTENT_TYPE",
                MovieContract.MovieEntry.CONTENT_TYPE, type);

        // This returns a movie from id
        long testMovieId = 550;
        // content://com.android.daniel.popmovies/movie/550
        type = mContext.getContentResolver().getType(
                MovieContract.MovieEntry.buildMovieUri(testMovieId));
        // vnd.android.cursor.item/com.android.daniel.popmovies/movie
        assertEquals("Error: the MovieEntry CONTENT_URI with location should return MovieEntry.CONTENT_ITEM_TYPE",
                MovieContract.MovieEntry.CONTENT_ITEM_TYPE, type);

        type = mContext.getContentResolver().getType(MovieContract.MovieEntry.buildMoviePopTopFavUri("popular"));
        // vnd.android.cursor.dir/com.android.daniel.popmovies/movie/popular
        assertEquals("Error: the VideoEntry CONTENT_URI should return VideoEntry.CONTENT_TYPE",
                MovieContract.MovieEntry.CONTENT_TYPE, type);

        type = mContext.getContentResolver().getType(MovieContract.MovieEntry.buildMoviePopTopFavUri("top_rated"));
        // vnd.android.cursor.dir/com.android.daniel.popmovies/movie/popular
        assertEquals("Error: the VideoEntry CONTENT_URI should return VideoEntry.CONTENT_TYPE",
                MovieContract.MovieEntry.CONTENT_TYPE, type);

        type = mContext.getContentResolver().getType(MovieContract.MovieEntry.buildMoviePopTopFavUri("favorite"));
        // vnd.android.cursor.dir/com.android.daniel.popmovies/movie/popular
        assertEquals("Error: the VideoEntry CONTENT_URI should return VideoEntry.CONTENT_TYPE",
                MovieContract.MovieEntry.CONTENT_TYPE, type);


        type = mContext.getContentResolver().getType(MovieContract.VideoEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.android.daniel.popmovies/video
        assertEquals("Error: the VideoEntry CONTENT_URI should return VideoEntry.CONTENT_TYPE",
                MovieContract.VideoEntry.CONTENT_TYPE, type);

        // content://com.android.daniel.popmovies/video/54300b340e0a2646400007e6
        String testVideoId = "54300b340e0a2646400007e6";
        type = mContext.getContentResolver().getType(
                MovieContract.VideoEntry.buildUriWithVideoId(testVideoId));
        // vnd.android.cursor.dir/content://com.android.daniel.popmovies/video/54300b340e0a2646400007e6
        assertEquals("Error: the VideoEntry CONTENT_URI with ID should return VideoEntry.CONTENT_TYPE",
                MovieContract.VideoEntry.CONTENT_ITEM_TYPE, type);

        // content://com.android.daniel.popmovies/video/movie/550
        type = mContext.getContentResolver().getType(
                MovieContract.VideoEntry.buildVideoUriWithMovieId(testMovieId));
        // vnd.android.cursor.item/com.android.daniel.popmovies/video/movie/550
        assertEquals("Error: the VideoEntry CONTENT_URI with movie and movieID should return VideoEntry.CONTENT_TYPE",
                MovieContract.VideoEntry.CONTENT_TYPE, type);

        type = mContext.getContentResolver().getType(MovieContract.ReviewEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.android.daniel.popmovies/review
        assertEquals("Error: the VideoEntry CONTENT_URI should return VideoEntry.CONTENT_TYPE",
                MovieContract.ReviewEntry.CONTENT_TYPE, type);

        // content://com.android.daniel.popmovies/review/547e6075c3a368256200022f
        String testReviewId = "547e6075c3a368256200022f";
        type = mContext.getContentResolver().getType(
                MovieContract.ReviewEntry.buildUriReviewFromId(testReviewId));
        // vnd.android.cursor.dir/com.android.daniel.popmovies/review/547e6075c3a368256200022f
        assertEquals("Error: the VideoEntry CONTENT_URI with location should return VideoEntry.CONTENT_ITEM_TYPE",
                MovieContract.ReviewEntry.CONTENT_ITEM_TYPE, type);

        // content://com.android.daniel.popmovies/review/movie/550
        type = mContext.getContentResolver().getType(
                MovieContract.ReviewEntry.buildReviewUriWithMovieId(testMovieId));
        // vnd.android.cursor.item/content://com.android.daniel.popmovies/review/movie/550
        assertEquals("Error: the ReviewEntry CONTENT_URI with movie_Id should return ReviewEntry.CONTENT_TYPE",
                MovieContract.ReviewEntry.CONTENT_TYPE, type);
    }

    public void testBasicVideoQuery() {
        // insert our test records into the database
        MovieDBHelper dbHelper = new MovieDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //ContentValues testValues = TestUtilities.createNorthPoleLocationValues();
        long movieRowId = TestUtilities.insertFightClubValues(mContext);

        // Fantastic.  Now that we have a location, add some weather!
        ContentValues videoValues = TestUtilities.createVideoValues(movieRowId);

        long videoRowId = db.insert(MovieContract.VideoEntry.TABLE_NAME, null, videoValues);
        assertTrue("Unable to Insert WeatherEntry into the Database", videoRowId != -1);

        db.close();
        Uri uriQueryVideo = MovieContract.VideoEntry.CONTENT_URI;

        // Test the basic content provider query
        Cursor videoCursor = mContext.getContentResolver().query(
                uriQueryVideo,
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicWeatherQuery", videoCursor, videoValues);
    }

    public void testBasicReviewQuery() {
        // insert our test records into the database
        MovieDBHelper dbHelper = new MovieDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //ContentValues testValues = TestUtilities.createNorthPoleLocationValues();
        long movieRowId = TestUtilities.insertFightClubValues(mContext);

        // Fantastic.  Now that we have a location, add some weather!
        ContentValues reviewValues = TestUtilities.createReviewValues(movieRowId);

        long videoRowId = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, reviewValues);
        assertTrue("Unable to Insert WeatherEntry into the Database", videoRowId != -1);

        db.close();
        Uri uriQueryReview = MovieContract.ReviewEntry.CONTENT_URI;

        // Test the basic content provider query
        Cursor videoCursor = mContext.getContentResolver().query(
                uriQueryReview,
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicWeatherQuery", videoCursor, reviewValues);
    }

    public void testBasicMovieQueries() {
        // insert our test records into the database
        MovieDBHelper dbHelper = new MovieDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createFightClub();
        long movieRowId = TestUtilities.insertFightClubValues(mContext);

        // Test the basic content provider query
        Cursor movieCursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicMovieQueries, location query", movieCursor, testValues);

        // Has the NotificationUri been set correctly? --- we can only test this easily against API
        // level 19 or greater because getNotificationUri was added in API level 19.
        if ( Build.VERSION.SDK_INT >= 19 ) {
            assertEquals("Error: Location Query did not properly set NotificationUri",
                    movieCursor.getNotificationUri(),  MovieContract.MovieEntry.CONTENT_URI);
        }
    }


    public void testUpdateMovie() {
        // Create a new map of values, where column names are the keys
        ContentValues values = TestUtilities.createFightClub();

        Uri locationUri = mContext.getContentResolver().
                insert(MovieContract.MovieEntry.CONTENT_URI, values);
        long movieRowId = ContentUris.parseId(locationUri);

        // Verify we got a row back.
        assertTrue(movieRowId != -1);
        Log.d(LOG_TAG, "New row id: " + movieRowId);

        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(MovieContract.MovieEntry._ID, movieRowId);
        updatedValues.put(MovieContract.MovieEntry.COLUMN_TITLE, "Pulp fiction");

        // Create a cursor with observer to make sure that the content provider is notifying
        // the observers as expected
        Cursor movieCursor = mContext.getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, null, null, null, null);

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        movieCursor.registerContentObserver(tco);

        int count = mContext.getContentResolver().update(
                MovieContract.MovieEntry.CONTENT_URI, updatedValues, MovieContract.MovieEntry._ID + "= ?",
                new String[] { Long.toString(movieRowId)});
        assertEquals(count, 1);

        // Test to make sure our observer is called.  If not, we throw an assertion.
        //
        // Students: If your code is failing here, it means that your content provider
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();

        movieCursor.unregisterContentObserver(tco);
        movieCursor.close();

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,   // projection
                MovieContract.MovieEntry._ID + " = " + movieRowId,
                null,   // Values for the "where" clause
                null    // sort order
        );

        TestUtilities.validateCursor("testUpdateMovie.  Error validating location entry update.",
                cursor, updatedValues);

        cursor.close();
    }

    public void testInsertReadProvider() {
        ContentValues testValues = TestUtilities.createFightClub();

        // Register a content observer for our insert.  This time, directly with the content resolver
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieContract.MovieEntry.CONTENT_URI, true, tco);
        Uri movieUri = mContext.getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, testValues);

        // Did our content observer get called?  Students:  If this fails, your insert location
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long movieRowId = ContentUris.parseId(movieUri);

        // Verify we got a row back.
        assertTrue(movieRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating LocationEntry.",
                cursor, testValues);

        // Fantastic.  Now that we have a location, add some weather!
        ContentValues videoValues = TestUtilities.createVideoValues(movieRowId);
        // The TestContentObserver is a one-shot class
        tco = TestUtilities.getTestContentObserver();

        mContext.getContentResolver().registerContentObserver(MovieContract.VideoEntry.CONTENT_URI, true, tco);

        Uri videoInsertUri = mContext.getContentResolver()
                .insert(MovieContract.VideoEntry.CONTENT_URI, videoValues);
        assertTrue(videoInsertUri != null);

        // Did our content observer get called?  Students:  If this fails, your insert weather
        // in your ContentProvider isn't calling
        // getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        // A cursor is your primary interface to the query results.
        Cursor videoCursor = mContext.getContentResolver().query(
                MovieContract.VideoEntry.CONTENT_URI,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // columns to group by
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating WeatherEntry insert.",
                videoCursor, videoValues);

        // Add the location values in with the weather data so that we can make
        // sure that the join worked and we actually get all the values back
        videoValues.putAll(testValues);

        // Get the joined Weather and Location data
        videoCursor = mContext.getContentResolver().query(
                MovieContract.VideoEntry.buildVideoUriWithMovieId(TestUtilities.TEST_MOVIE_ID),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        TestUtilities.validateCursor("testInsertReadProvider.  Error validating joined Video and Movie Data.",
                videoCursor, videoValues);

        // Fantastic.  Now that we have a location, add some weather!
        ContentValues reviewValues = TestUtilities.createReviewValues(movieRowId);
        // The TestContentObserver is a one-shot class
        tco = TestUtilities.getTestContentObserver();

        mContext.getContentResolver().registerContentObserver(MovieContract.ReviewEntry.CONTENT_URI, true, tco);

        Uri reviewInsertUri = mContext.getContentResolver()
                .insert(MovieContract.ReviewEntry.CONTENT_URI, reviewValues);
        assertTrue(reviewInsertUri != null);

        // Did our content observer get called?  Students:  If this fails, your insert weather
        // in your ContentProvider isn't calling
        // getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        // A cursor is your primary interface to the query results.
        Cursor reviewCursor = mContext.getContentResolver().query(
                MovieContract.ReviewEntry.CONTENT_URI,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // columns to group by
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating WeatherEntry insert.",
                reviewCursor, reviewValues);

        // Add the location values in with the weather data so that we can make
        // sure that the join worked and we actually get all the values back
        reviewValues.putAll(testValues);

        // Get the joined Weather and Location data
        reviewCursor = mContext.getContentResolver().query(
                MovieContract.ReviewEntry.buildReviewUriWithMovieId(TestUtilities.TEST_MOVIE_ID),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        TestUtilities.validateCursor("testInsertReadProvider.  Error validating joined Review and Movie Data.",
                reviewCursor, reviewValues);
    }

    public void testDeleteRecords() {
        testInsertReadProvider();

        // Register a content observer for our movie delete.
        TestUtilities.TestContentObserver movieObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieContract.MovieEntry.CONTENT_URI, true, movieObserver);

        // Register a content observer for our video delete.
        TestUtilities.TestContentObserver videoObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieContract.VideoEntry.CONTENT_URI, true, videoObserver);

        // Register a content observer for our review delete.
        TestUtilities.TestContentObserver reviewObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieContract.ReviewEntry.CONTENT_URI, true, reviewObserver);

        deleteAllRecordsFromProvider();

        // Students: If either of these fail, you most-likely are not calling the
        // getContext().getContentResolver().notifyChange(uri, null); in the ContentProvider
        // delete.  (only if the insertReadProvider is succeeding)
        movieObserver.waitForNotificationOrFail();
        videoObserver.waitForNotificationOrFail();
        reviewObserver.waitForNotificationOrFail();

        mContext.getContentResolver().unregisterContentObserver(movieObserver);
        mContext.getContentResolver().unregisterContentObserver(videoObserver);
        mContext.getContentResolver().unregisterContentObserver(reviewObserver);
    }

    static private final int BULK_INSERT_RECORDS_TO_INSERT = 10;

    static ContentValues[] createBulkInsertMovieValues() {

        ContentValues[] returnContentValues = new ContentValues[BULK_INSERT_RECORDS_TO_INSERT];
        for ( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++) {
            ContentValues testValues = new ContentValues();
            testValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, 550 + i);
            testValues.put(MovieContract.MovieEntry.COLUMN_TITLE, "Fight Club");
            testValues.put(MovieContract.MovieEntry.COLUMN_POSTER, "lIv1QinFqz4dlp5U4lQ6HaiskOZ");
            testValues.put(MovieContract.MovieEntry.COLUMN_BACKDROP, "-0wdVrC4OM4");
            testValues.put(MovieContract.MovieEntry.COLUMN_SYNOPSIS, "TOP MOVIE");
            testValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, "1998-05-05");
            testValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, 8.8);
            if (i%2 == 0){
                testValues.put(MovieContract.MovieEntry.COLUMN_FAVORITE, 0);
            }else {
                testValues.put(MovieContract.MovieEntry.COLUMN_FAVORITE, 1);
            }
            if (i%2 == 0){
                testValues.put(MovieContract.MovieEntry.COLUMN_POPULAR, 1);
            }else {
                testValues.put(MovieContract.MovieEntry.COLUMN_TOP_RATED, 1);
            }

            returnContentValues[i] = testValues;
        }
        return returnContentValues;
    }


    static ContentValues[] createBulkInsertVideoValues(long movieRowId) {

        ContentValues[] returnContentValues = new ContentValues[BULK_INSERT_RECORDS_TO_INSERT];
        for ( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++) {
            ContentValues videoValues = new ContentValues();
            videoValues.put(MovieContract.VideoEntry.COLUMN_MOVIE_KEY, movieRowId);
            videoValues.put(MovieContract.VideoEntry.COLUMN_PATH, "0wdVrC4OM4");
            videoValues.put(MovieContract.VideoEntry.COLUMN_VIDEO_ID, "54300b340e0a"+i+"2646400007e6");
            returnContentValues[i] = videoValues;
        }
        return returnContentValues;
    }

    static ContentValues[] createBulkInsertReviewValues(long movieRowId) {

        ContentValues[] returnContentValues = new ContentValues[BULK_INSERT_RECORDS_TO_INSERT];
        for ( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++) {
            ContentValues reviewValues = new ContentValues();
            reviewValues.put(MovieContract.ReviewEntry.COLUMN_MOVIE_KEY, movieRowId);
            reviewValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_ID, "547e6075c3a"+i+"368256200022f");
            reviewValues.put(MovieContract.ReviewEntry.COLUMN_AUTHOR, "-anthonypagan1975");
            reviewValues.put(MovieContract.ReviewEntry.COLUMN_CONTENT, "It was good. Although I wish it had more action scenes. It's worth watching ago don't miss out!\",\n" +
                    "            \"url\": \"https://www.themoviedb.org/review/547e6075c3a368256200022f");
            returnContentValues[i] = reviewValues;
        }
        return returnContentValues;
    }


    public void testBulkInsert() {
        // first, let's create a movie value
        ContentValues[] movieValues = createBulkInsertMovieValues();

        // Register a content observer for our bulk insert.
        TestUtilities.TestContentObserver movieObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieContract.MovieEntry.CONTENT_URI, true, movieObserver);

        int insertMovieCount = mContext.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, movieValues);

        // Students:  If this fails, it means that you most-likely are not calling the
        // getContext().getContentResolver().notifyChange(uri, null); in your BulkInsert
        // ContentProvider method.
        movieObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(movieObserver);

        assertEquals(insertMovieCount, BULK_INSERT_RECORDS_TO_INSERT);

        Cursor movieCursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        assertEquals("", movieCursor.getCount(), BULK_INSERT_RECORDS_TO_INSERT);

        movieCursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.buildMoviePopTopFavUri(Constants.FAVORITE),
                null,
                null,
                null,
                null
        );

        movieCursor.moveToFirst();
        Log.v("Cursor Object", DatabaseUtils.dumpCursorToString(movieCursor));

        assertEquals("", movieCursor.getCount(), BULK_INSERT_RECORDS_TO_INSERT/2);

        movieCursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.buildMoviePopTopFavUri(Constants.POPULAR),
                null,
                null,
                null,
                null
        );

        movieCursor.moveToFirst();
        Log.v("Cursor Object", DatabaseUtils.dumpCursorToString(movieCursor));

        assertEquals("", movieCursor.getCount(), BULK_INSERT_RECORDS_TO_INSERT/2);

        movieCursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.buildMoviePopTopFavUri(Constants.TOP_RATED),
                null,
                null,
                null,
                null
        );

        movieCursor.moveToFirst();
        Log.v("Cursor Object", DatabaseUtils.dumpCursorToString(movieCursor));

        assertEquals("", movieCursor.getCount(), BULK_INSERT_RECORDS_TO_INSERT/2);


        ContentValues testValues = TestUtilities.createFightClub();
        Uri movieUri = mContext.getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, testValues);
        long movieRowId = ContentUris.parseId(movieUri);

        // Verify we got a row back.
        assertTrue(movieRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // Now we can bulkInsert some weather.  In fact, we only implement BulkInsert for weather
        // entries.  With ContentProviders, you really only have to implement the features you
        // use, after all.
        ContentValues[] bulkInsertVideoValues = createBulkInsertVideoValues(movieRowId);

        // Register a content observer for our bulk insert.
        TestUtilities.TestContentObserver videoObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieContract.VideoEntry.CONTENT_URI, true, videoObserver);

        int insertCount = mContext.getContentResolver().bulkInsert(MovieContract.VideoEntry.CONTENT_URI, bulkInsertVideoValues);

        // Students:  If this fails, it means that you most-likely are not calling the
        // getContext().getContentResolver().notifyChange(uri, null); in your BulkInsert
        // ContentProvider method.
        videoObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(videoObserver);

        assertEquals(insertCount, BULK_INSERT_RECORDS_TO_INSERT);

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.VideoEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null
        );

        // we should have as many records in the database as we've inserted
        assertEquals(cursor.getCount(), BULK_INSERT_RECORDS_TO_INSERT);

        // and let's make sure they match the ones we created
        cursor.moveToFirst();
        for ( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++, cursor.moveToNext() ) {
            TestUtilities.validateCurrentRecord("testBulkInsert.  Error validating WeatherEntry " + i,
                    cursor, bulkInsertVideoValues[i]);
        }

        cursor.close();

        ContentValues[] bulkInsertReviewValues = createBulkInsertReviewValues(movieRowId);

        // Register a content observer for our bulk insert.
        TestUtilities.TestContentObserver reviewObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieContract.ReviewEntry.CONTENT_URI, true, reviewObserver);

        insertCount = mContext.getContentResolver().bulkInsert(MovieContract.ReviewEntry.CONTENT_URI, bulkInsertReviewValues);

        // Students:  If this fails, it means that you most-likely are not calling the
        // getContext().getContentResolver().notifyChange(uri, null); in your BulkInsert
        // ContentProvider method.
        reviewObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(videoObserver);

        assertEquals(insertCount, BULK_INSERT_RECORDS_TO_INSERT);

        // A cursor is your primary interface to the query results.
        cursor = mContext.getContentResolver().query(
                MovieContract.ReviewEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null
        );

        // we should have as many records in the database as we've inserted
        assertEquals(cursor.getCount(), BULK_INSERT_RECORDS_TO_INSERT);

        // and let's make sure they match the ones we created
        cursor.moveToFirst();
        for ( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++, cursor.moveToNext() ) {
            TestUtilities.validateCurrentRecord("testBulkInsert.  Error validating WeatherEntry " + i,
                    cursor, bulkInsertReviewValues[i]);
        }

        cursor.close();
    }


}
