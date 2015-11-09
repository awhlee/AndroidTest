package com.juicedfootball.ui.activity;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.juicedfootball.R;
import com.juicedfootball.common.JuicedFootballUtils;
import com.juicedfootball.content.JuicedFootballContentProvider;

public class JuicedFootballLoginActivity extends AccountAuthenticatorActivity {
    private static final String LOG_TAG = JuicedFootballUtils.LOG_TAG_LOGIN_ACTIVITY;

    public static final String ARG_ACCOUNT_TYPE = "account_type";
    public static final String ARG_ACCOUNT_NAME = "account_name";
    public static final String ARG_AUTH_TYPE = "auth_type";
    public static final String ARG_IS_ADDING_NEW_ACCOUNT = "add_new_account";
    public static final String PARAM_USER_PASS = "account_password";
    public static final String KEY_ERROR_MESSAGE = "error_message";

    protected String mAccountName;
    protected String mPassword;
    protected String mAccountType;
    protected String mAuthType;

    AccountManager mAccountManager;
    UserLoginTask mAuthTask;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juiced_football_login);

        Log.i(LOG_TAG, "onCreate()");

        Button loginButton=(Button)findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Grab input from UX, we need to do it here on the UI thread.
                EditText username = (EditText)findViewById(R.id.username);
                mAccountName = username.getText().toString();
                EditText password = (EditText)findViewById(R.id.password);
                mPassword = password.getText().toString();

                // Obviously PII problems.
                Log.d(LOG_TAG, "Login button was pressed. Logging in with " + mAccountName +
                    " password: " + mPassword);

                // The user clicked the button so let's start the login process.
                mAuthTask = new UserLoginTask();
                mAuthTask.execute((Void) null);
            }
        });

        mAccountManager = AccountManager.get(getBaseContext());

        // We know that this activity was started using an intent that we created (and populated)
        // in our the Authenticator.  Let's grab the info out of the there for later.
        Intent authenticatorRequest = getIntent();
        // ARG_ACCOUNT_NAME will come when its an existing Account.
        mAccountName = authenticatorRequest.getStringExtra(ARG_ACCOUNT_NAME);
        mAccountType = authenticatorRequest.getStringExtra(ARG_ACCOUNT_TYPE);
        mAuthType = authenticatorRequest.getStringExtra(ARG_AUTH_TYPE);
        if (mAuthType == null) {
            mAuthType = "default";
        }

        // TODO: Do we need to set the default text of the username to mAccountName here?
        // Just in case this is an edit?

    }


    public class UserLoginTask extends AsyncTask<Void, Void, Intent> {
        @Override
        protected Intent doInBackground(Void... params) {
            final Intent res = new Intent();
            String authToken;

            Log.i(LOG_TAG, "UserLoginTask.doInBackground()");

            // TODO: Call some external method which handles your application-specific login,
            // returning a token. An auth token is hard coded for now.
            try {
//                authToken = fetchTokenFromCredentials(mAccountName, mPassword, mAuthTokenType);
                authToken = "auth_token_123_abc";
                res.putExtra(AccountManager.KEY_ACCOUNT_NAME, mAccountName);
                res.putExtra(AccountManager.KEY_ACCOUNT_TYPE, mAccountType);
                res.putExtra(AccountManager.KEY_AUTHTOKEN, authToken);
                res.putExtra(PARAM_USER_PASS, mPassword);
            } catch (Exception e) {
                res.putExtra(KEY_ERROR_MESSAGE, e.getMessage());
            }
            return res;
        }

        /**
         * Finish processing the login
         * @param intent The intent that was returned from doInBackground
         */
        @Override
        protected void onPostExecute(final Intent intent) {
            Log.i(LOG_TAG, "UserLoginTask.onPostExecute() intent: " + intent);

            // Complete, clear the reference
            mAuthTask = null;
            // If we got an error message, put it on the UI
            if (intent.hasExtra(KEY_ERROR_MESSAGE)) {
                // Popup and error message but for now...just log something
                Log.e(LOG_TAG, "Error logging in the user...please try again");
                AlertDialog alertDialog =
                        new AlertDialog.Builder(JuicedFootballLoginActivity.this).create();
                alertDialog.setTitle("Error Logging In");
                alertDialog.setMessage("We had some problems.  Please try again.");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            } else {
                finishLogin(intent);
                // Calling finish() closes the activity because we are done
                finish();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }

    private void finishLogin(Intent intent) {
        Log.i(LOG_TAG, "finishLogin() intent: " + intent);

        String accountPassword = intent.getStringExtra(PARAM_USER_PASS);
        Log.i(LOG_TAG, "finishLogin() accountPassword: " + accountPassword);

        String accountType = intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE);
        Log.i(LOG_TAG, "finishLogin() accountType: " + accountType);

        String authtoken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
        Log.i(LOG_TAG, "finishLogin() authtoken: " + authtoken);

        final Account account = new Account(mAccountName, accountType);
        Log.i(LOG_TAG, "finishLogin() account: " + account);

        if (getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false)) {
            // Creating the account
            // Password is optional to this call, safer not to send it really.
            mAccountManager.addAccountExplicitly(account, accountPassword, null);
        } else {
            // Password change only
            mAccountManager.setPassword(account, accountPassword);
        }
        // set the auth token we got (Not setting the auth token will cause
        // another call to the server to authenticate the user)
        Log.i(LOG_TAG, "finishLogin() mAuthType: " + mAuthType);
        mAccountManager.setAuthToken(account, mAuthType, authtoken);

        // Let's set this account to sync automatically
        ContentResolver cr = getContentResolver();
        cr.setSyncAutomatically(account, JuicedFootballContentProvider.PROVIDER_AUTHORITY,
                true);

        // Our base class can do what Android requires with the
        // KEY_ACCOUNT_AUTHENTICATOR_RESPONSE extra that onCreate has
        // already grabbed
        setAccountAuthenticatorResult(intent.getExtras());

        // Tell the account manager settings page that all went well
        setResult(RESULT_OK, intent);
    }
}
