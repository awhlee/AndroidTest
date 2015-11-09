package com.juicedfootball.ui.activity;

import android.accounts.Account;
import android.support.v4.app.Fragment;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.juicedfootball.R;
import com.juicedfootball.common.JuicedApplicationData;
import com.juicedfootball.common.JuicedFootballUtils;

public class ItemDetailsFragment extends Fragment {
    private static final String LOG_TAG = JuicedFootballUtils.LOG_TAG_MAIN_ACTIVITY;

    private JuicedApplicationData mData;
    private int mPosition;
    private View mView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mView = inflater.inflate(R.layout.activity_item_details, container, false);

        JuicedFootball parent = (JuicedFootball)getActivity();
        mData = parent.getAppData();

        Bundle args = getArguments();
        int position = args.getInt("position");
        updateAccountView(position);

        // We need to set up the action bar for our per message options
        configureActionBar();

        parent.getSupportActionBar().setHomeAsUpIndicator(null);

        return mView;
    }

    @Override
    public void onDestroyView() {
        configureActionBar();
        super.onDestroyView();
    }

    private void configureActionBar() {
        Log.d(LOG_TAG, "configureActionBar()");
        JuicedFootball parent = (JuicedFootball)getActivity();
        ActionBar actionBar = parent.getSupportActionBar();
        actionBar.invalidateOptionsMenu();
    }

    public void updateAccountView(int position) {
        mPosition = position;
        Account theAccount = mData.getAccountAt(mPosition);

        TextView theView = (TextView)mView.findViewById(R.id.text_view);
        theView.setText(theAccount.toString());
        return;
    }


}
