package com.juicedfootball.ui.activity;

/**
 JuicedFootball
 The main activity for the app.
 */


import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.juicedfootball.R;
import com.juicedfootball.common.JuicedApplicationData;
import com.juicedfootball.common.JuicedFootballUtils;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;


public class JuicedFootball extends AppCompatActivity {
    private static final String LOG_TAG = JuicedFootballUtils.LOG_TAG_MAIN_ACTIVITY;

    private DrawerLayout mDrawerLayout;
    private LinearLayout mDrawerLinear;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mOriginalTitle;
    private String mDrawerTitle;
    private JuicedApplicationData mAppData;
    private CopyOnWriteArrayList<AccountsDataChangedListener> mAccountsListeners =
            new CopyOnWriteArrayList<>();
    private Account[] mAccounts;
    private Menu mMenu;

    final AccountCreatedListenerImpl mAccountListener = new AccountCreatedListenerImpl(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(LOG_TAG, "onCreate()");

        setContentView(R.layout.activity_juiced_football);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDrawerTitle = "I am the drawer dammit";
        mOriginalTitle = (String)getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.drawer_list);
        mDrawerLinear = (LinearLayout)findViewById(R.id.drawer_linear_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                null,
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_closed  /* "close drawer" description */
        ) {
            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mOriginalTitle);
//                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(mDrawerTitle);
//                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        // Set the adapter for the list view
        String[] osArray = {"Android", "iOS", "Windows", "OS X", "Linux"};
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, osArray));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // Now set up the fragment
        if (findViewById(R.id.juiced_fragment_container) != null) {
            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create an instance of ExampleFragment
            ItemListFragment firstFragment = new ItemListFragment();

            // In case this activity was started with special instructions from an Intent,
            // pass the Intent's extras to the fragment as arguments
            firstFragment.setArguments(getIntent().getExtras());
            registerAccountsChangedListener(firstFragment);

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.juiced_fragment_container, firstFragment).commit();
        }

        // Now that all the UX is set up, let's pull the account data
        populateAccounts();
    }

    public JuicedApplicationData getAppData() {
        return mAppData;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(LOG_TAG, "onCreateOptionsMenu()");

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_juiced_football, menu);
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.d(LOG_TAG, "onPrepareOptionsMenu()");

        // So far this is the only reliable place (along with onCreateOptionsMenu() to change
        // ActionBar actions as most sample code seems to want you to update the menu
        // and then call invalidateOptionsMenu()
        int backstackCount = getSupportFragmentManager().getBackStackEntryCount();
        Log.d(LOG_TAG, "Backstack count " + backstackCount);
        if (backstackCount > 0) {
            MenuItem theItem = menu.findItem(R.id.action_create_account);
            theItem.setVisible(false);
        }
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int backstackCount = getSupportFragmentManager().getBackStackEntryCount();
        Log.d(LOG_TAG, "Backstack count " + backstackCount);

        if (backstackCount == 0) {
            // For now, our backstack is just 1 deep (message list vs. message details)
            // So if we have no backstack, we are on the message list so we can show the
            // drawer, if not, the back button press should go through to onBackPressed()
            if (mDrawerToggle.onOptionsItemSelected(item)) {
                Log.d(LOG_TAG, "onOptionsItemSelected() - handled by mDrawerToggle");
                return true;
            }
        }

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_create_account) {
            createJuicedAccount(this, mAccountListener);
            return true;
        } else if (id == android.R.id.home) {
            onBackPressed();
            // Clear the back button back to the hamburger.
            mDrawerToggle.syncState();
        }

        return super.onOptionsItemSelected(item);
    }

    private void populateAccounts() {
        // Either we are bringing up the list of accounts or the list changed
        Log.d(LOG_TAG, "populateAccounts()");

        AccountManager accountManager = (AccountManager) this.getSystemService(
                this.ACCOUNT_SERVICE);
        String accountType = getString(R.string.juiced_football_account_type);
        mAccounts = accountManager.getAccountsByType(accountType);

        for (Account account : mAccounts) {
            Log.d(LOG_TAG, "populateAccounts() - found account: " + account);
        }

        if (mAppData == null) {
            mAppData = new JuicedApplicationData();
        }
        mAppData.setAccounts(mAccounts);
        notifyAccountsChanged();
    }

    private void notifyAccountsChanged() {
        // Fire on all of our listeners that the accounts changed
        Iterator<AccountsDataChangedListener> iterator = mAccountsListeners.listIterator();
        while (iterator.hasNext()) {
            AccountsDataChangedListener listener = iterator.next();
            listener.onAccountsChanged();
        }
    }

    public void registerAccountsChangedListener(AccountsDataChangedListener newListener) {
        mAccountsListeners.add(newListener);
    }

    private void createJuicedAccount(Activity activity,
            final AccountCreatedListener listener) {
        Log.d(LOG_TAG, "createJuicedAccount() - starting");
        AccountManager accountManager = (AccountManager)activity.getSystemService(
                activity.ACCOUNT_SERVICE);
        String accountType = activity.getString(R.string.juiced_football_account_type);
        accountManager.addAccount(accountType,
                "default", null, null, activity, new AccountManagerCallback<Bundle>() {
                    @Override
                    public void run(AccountManagerFuture<Bundle> future) {
                        Bundle result;
                        Log.d(LOG_TAG, "createJuicedAccount() - running");
                        try {
                            // What is here should be what we put into the Bundle in
                            // JuicedFootballLoginActivity.finishLogin()
                            result = future.getResult();
                            Log.d(LOG_TAG, "createJuicedAccount() - result: " + result);
                            listener.callback();
                        } catch (Exception e) {
                            Log.e(LOG_TAG, "createJuicedAccount() - exception: " + e);
                        }
                        return;
                    }
                }, null);
        Log.d(LOG_TAG, "createJuicedAccount() - done");
        return;
    }


    public interface AccountsDataChangedListener {
        void onAccountsChanged();
    }

    private interface AccountCreatedListener {
        void callback();
    }

    public class AccountCreatedListenerImpl implements AccountCreatedListener {
        Context mContext;

        public AccountCreatedListenerImpl(Context context) {
            mContext = context;
        }

        @Override
        public void callback() {
            // Grab our list of accounts and update it with the new account, this should really
            // be done in the "logic class"
            Log.d(LOG_TAG, "AccountCreatedListenerImpl.callback()");
            populateAccounts();
        }
    }

    public class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }

        private void selectItem(int position) {
            mDrawerList.setItemChecked(position, true);
            Toast.makeText(JuicedFootball.this, "You selected an item in the drawer",
                Toast.LENGTH_LONG ).show();
            mDrawerLayout.closeDrawer(mDrawerLinear);
        }
    }

    public void viewMessageFragment(int position) {
        Log.d(LOG_TAG, "viewMessageFragment() position: " + position);

        // Capture the article fragment from the activity layout
        ItemDetailsFragment detailsFrag = (ItemDetailsFragment)
            getSupportFragmentManager().findFragmentById(R.id.juiced_football_details_fragment);

        if (detailsFrag != null) {
            // If article frag is available, we're in two-pane layout...

            // Call a method in the ArticleFragment to update its content
            detailsFrag.updateAccountView(position);

        } else {
            // If the frag is not available, we're in the one-pane layout and must swap frags...

            // Create fragment and give it an argument for the selected article
            ItemDetailsFragment newFragment = new ItemDetailsFragment();
            Bundle args = new Bundle();
            args.putInt("position", position);
            newFragment.setArguments(args);
            android.support.v4.app.FragmentTransaction transaction =
                getSupportFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            transaction.add(R.id.juiced_fragment_container, newFragment);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
        }
    }
}
