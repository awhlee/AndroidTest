package com.juicedfootball.account;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.juicedfootball.common.JuicedFootballUtils;
import com.juicedfootball.ui.activity.JuicedFootballLoginActivity;

/**
 * The account authenticator that allows the user to create a custom Juiced Football account
 */
public class JuicedFootballAccountAuthenticator extends AbstractAccountAuthenticator {
    private static final String LOG_TAG = JuicedFootballUtils.LOG_TAG_AUTHENTICATOR;

    Context mContext;

    public JuicedFootballAccountAuthenticator(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType,
            String authTokenType, String[] requiredFeatures, Bundle options)
                    throws NetworkErrorException {
        Log.i(LOG_TAG, "addAccount()");

        // We absolutely cannot add an account without some information
        // from the user; so we're definitely going to return an Intent
        // via KEY_INTENT
        final Bundle bundle = new Bundle();

        Log.i(LOG_TAG, "addAccount() - sending Intent to JuicedFootballLoginActivity");

        // This intent goes to the login activity that we created for juiced football
        final Intent intent = new Intent(mContext, JuicedFootballLoginActivity.class);

        // We can configure that activity however we wish via the
        // Intent.  We'll set ARG_IS_ADDING_NEW_ACCOUNT so the Activity
        // knows to ask for the account name as well
        intent.putExtra(JuicedFootballLoginActivity.ARG_ACCOUNT_TYPE, accountType);
        intent.putExtra(JuicedFootballLoginActivity.ARG_AUTH_TYPE, authTokenType);
        intent.putExtra(JuicedFootballLoginActivity.ARG_IS_ADDING_NEW_ACCOUNT, true);

        // It will also need to know how to send its response to the
        // account manager; LoginActivity must derive from
        // AccountAuthenticatorActivity, which will want this key set
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);

        // Wrap up this intent, and return it, which will cause the
        // intent to be run
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);

        Log.i(LOG_TAG, "addAccount() - return bundle " + bundle);
        return bundle;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account,
            String authTokenType, Bundle options) throws NetworkErrorException {
        Log.i(LOG_TAG, "getAuthToken() - account: " + account);

        // We can add rejection of a request for a token type we
        // don't support here

        // Get the instance of the AccountManager that's making the
        // request
        final AccountManager am = AccountManager.get(mContext);

        // See if there is already an authentication token stored
        String authToken = am.peekAuthToken(account, authTokenType);

        // If we have no token, use the account credentials to fetch
        // a new one, effectively another logon
        if (TextUtils.isEmpty(authToken)) {
            final String password = am.getPassword(account);
            if (password != null) {
//                authToken = fetchTokenFromCredentials(account.name, password, authTokenType)
            }
        }

        // If we either got a cached token, or fetched a new one, hand
        // it back to the client that called us.
        if (!TextUtils.isEmpty(authToken)) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
            Log.i(LOG_TAG, "getAuthToken() - found it, returning: " + result);
            return result;
        }

        // If we get here, then we don't have a token, and we don't have
        // a password that will let us get a new one (or we weren't able
        // to use the password we do have).  We need to fetch
        // information from the user, we do that by creating an Intent
        // to an Activity child class.
        final Intent intent = new Intent(mContext, JuicedFootballLoginActivity.class);

        // We want to give the Activity the information we want it to
        // return to the AccountManager.  We'll cover that with the
        // KEY_ACCOUNT_AUTHENTICATOR_RESPONSE parameter.
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE,
                response);
        // We'll also give it the parameters we've already looked up, or
        // were given.
        intent.putExtra(JuicedFootballLoginActivity.ARG_IS_ADDING_NEW_ACCOUNT, false);
        intent.putExtra(JuicedFootballLoginActivity.ARG_ACCOUNT_NAME, account.name);
        intent.putExtra(JuicedFootballLoginActivity.ARG_ACCOUNT_TYPE, account.type);
        intent.putExtra(JuicedFootballLoginActivity.ARG_AUTH_TYPE, authTokenType);

        // Remember that we have to return a Bundle, not an Intent, but
        // we can tell the caller to run our intent to get its
        // information with the KEY_INTENT parameter in the returned
        // Bundle
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);

        Log.i(LOG_TAG, "getAuthToken() - could not find it, returning: " + bundle);
        return bundle;
    }

    @Override
    public String getAuthTokenLabel(String authTokenType){
        return "Juiced Football Auth Token";
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response,
            Account account, String[] features) throws NetworkErrorException {
        Log.d(LOG_TAG, "hasFeatures()");
        return null;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response,
            Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        Log.d(LOG_TAG, "updateCredentials()");
        return null;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response,
            Account account, Bundle options) throws NetworkErrorException {
        Log.d(LOG_TAG, "confirmCredentials()");
        return null;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        Log.d(LOG_TAG, "editProperties()");
        return null;
    }
}
