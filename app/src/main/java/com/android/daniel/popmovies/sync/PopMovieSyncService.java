package com.android.daniel.popmovies.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by danie on 03/08/2017.
 */

public class PopMovieSyncService extends Service {

    private static final Object sSyncAdapterLock = new Object();
    private static PopMovieSyncAdapter sPopMovieSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("SunshineSyncService", "onCreate - SunshineSyncService");
        synchronized (sSyncAdapterLock) {
            if (sPopMovieSyncAdapter == null) {
                sPopMovieSyncAdapter = new PopMovieSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sPopMovieSyncAdapter.getSyncAdapterBinder();
    }
}