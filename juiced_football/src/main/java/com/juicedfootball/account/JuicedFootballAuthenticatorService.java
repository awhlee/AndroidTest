package com.juicedfootball.account;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.juicedfootball.common.JuicedFootballUtils;

/**
 * This class is just a shim to get a reference to the authenticator.
 */
public class JuicedFootballAuthenticatorService extends Service {
    private static final String LOG_TAG = JuicedFootballUtils.LOG_TAG_AUTHENTICATOR_SERVICE;

    public JuicedFootballAuthenticatorService() {
        Log.d(LOG_TAG, "Creating the AuthenticatorService");
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (!intent.getAction().equals(
                android.accounts.AccountManager.ACTION_AUTHENTICATOR_INTENT)) {
            Log.e(LOG_TAG, "The intent is not ACTION_AUTHENTICATOR_INTENT returning null");
            return null;
        }

        Log.e(LOG_TAG, "Creating an authenticator and returning");
        JuicedFootballAccountAuthenticator authenticator =
                new JuicedFootballAccountAuthenticator(this);
        Log.d(LOG_TAG, "Returning the ibinder of authenticator: " + authenticator);
        return authenticator.getIBinder();
    }
}
