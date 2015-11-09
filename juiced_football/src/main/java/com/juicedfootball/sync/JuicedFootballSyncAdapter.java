package com.juicedfootball.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.juicedfootball.common.JuicedFootballUtils;

/**
 * JuicedFootballSyncAdapter
 * The SyncAdapter that pull data into our database
 *
 * The sync adapter can run under a couple of different circumstances:
 * 1. When server data changes. GCM and a BroadcastReceiver
 * 2. When local data changes, call ContentResolver.requestSync() via a ContentObserver.onChange()
 * 3. When the network becomes available.  This is set up by calling setSyncAutomatically()
 * 4. Run the sync periodically.  addPeriodicSync() with a SYNC_INTERVAL and it works with
 *    setSyncAutomatically()
 * 5. Run on demand.  Call content resolver's requestSync()
 *
 * Details at:
 * https://developer.android.com/training/sync-adapters/running-sync-adapter.html
 *
 */
public class JuicedFootballSyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String LOG_TAG = JuicedFootballUtils.LOG_TAG_SYNC_ADAPTER;

    // Since we use a content provider to store data, we need a reference to the
    // content resolver.
    ContentResolver mContentResolver;

    /**
     * Set up the sync adapter
     */
    public JuicedFootballSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContentResolver = context.getContentResolver();
    }

    /**
     * Set up the sync adapter. This form of the constructor maintains compatibility with
     * Android 3.0 and later platform versions
     */
    public JuicedFootballSyncAdapter(
            Context context,
            boolean autoInitialize,
            boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        mContentResolver = context.getContentResolver();
    }

    /**
     * The meaty part of the sync adapter. The sync is already happening in a background thread
     * so we don't need to worry too much about heavy operations.
     * @param account Android account so we can auth to the data source.
     * @param extras Extras passed when the sync was requested.
     * @param authority The content authority
     * @param provider Some sort of helper to use the content provider
     * @param syncResult The result that we need to populate when we are done syncing.
     */
    @Override
    public void onPerformSync(
            Account account,
            Bundle extras,
            String authority,
            ContentProviderClient provider,
            SyncResult syncResult) {
        // Do the actual sync here
        Log.i(LOG_TAG, "onPerformSync() - called");

        // Do we need to set something when the result is OK?
        Log.i(LOG_TAG, "onPerformSync() - done");
        return;
    }
}
