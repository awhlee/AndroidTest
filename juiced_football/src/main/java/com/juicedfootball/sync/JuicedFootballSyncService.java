package com.juicedfootball.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.juicedfootball.common.JuicedFootballUtils;

public class JuicedFootballSyncService extends Service {
    private static final String LOG_TAG = JuicedFootballUtils.LOG_TAG_SYNC_SERVICE;

    // Storage for an instance of the sync adapter
    private static JuicedFootballSyncAdapter sSyncAdapter = null;

    // Object to use as a thread-safe lock
    private static final Object sSyncAdapterLock = new Object();

    public JuicedFootballSyncService() {
    }

    /**
     * Instantiate the sync adapter object.
     */
    @Override
    public void onCreate() {
        Log.i(LOG_TAG, "Creating our sync service");

        /*
         * Create the sync adapter as a singleton. Set the sync adapter as syncable
         * Disallow parallel syncs
         */        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null) {
                sSyncAdapter = new JuicedFootballSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    /**
     * Return an object that allows the system to invoke the sync adapter.
     */
     @Override
    public IBinder onBind(Intent intent) {
        Log.i(LOG_TAG, "Binding to our sync service");

        /*
         * Get the object that allows external processes to call onPerformSync(). The object is
         * created in the base class code when the SyncAdapter constructors call super()
         */
        return sSyncAdapter.getSyncAdapterBinder();
    }
}
