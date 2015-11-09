package com.juicedfootball.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.juicedfootball.R;
import com.juicedfootball.common.JuicedApplicationData;
import com.juicedfootball.ui.presenter.JuicedFootballTouchHelper;
import com.juicedfootball.ui.presenter.JuicedFootballViewAdapter;

public class ItemListFragment extends Fragment
        implements JuicedFootball.AccountsDataChangedListener {
    private RecyclerView  mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private JuicedFootballViewAdapter mAdapter;
    private JuicedApplicationData mData;
    private AccountsSelectedListenerImpl mListener = new AccountsSelectedListenerImpl();
    private AccountLongPressListenerImpl mLongListener = new AccountLongPressListenerImpl();

    public ItemListFragment() {
    }

    @Override
    public void onAccountsChanged() {
        // The accounts changed in the UX somewhere
        if (mAdapter != null) {
            // We have not been initialized yet
            mAdapter.notifyDataSetChanged();
        }
    }

    private class AccountsSelectedListenerImpl
                implements JuicedFootballViewAdapter.AccountSelectedListener {
        public void onAccountSelected(int position) {
            JuicedFootball parent = (JuicedFootball)getActivity();
            parent.viewMessageFragment(position);
        }
    }

    private class AccountLongPressListenerImpl
        implements JuicedFootballViewAdapter.AccountLongPressListener {
        public void onLongPress(int position) {
            // Let's show a menu that slides up from the bottom.


        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View ourView = inflater.inflate(R.layout.activity_item_list, container, false);

        JuicedFootball parent = (JuicedFootball)getActivity();
        mData = parent.getAppData();

        // Get all of our stuff ready.
        mRecyclerView = (RecyclerView)ourView.findViewById(R.id.juiced_football_recycler_view);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(parent, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new JuicedFootballViewAdapter(parent, mData.getBackingList(), mListener,
            mLongListener);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.setOnCreateContextMenuListener(this);

        ItemTouchHelper.Callback callback =
                new JuicedFootballTouchHelper(mAdapter, mRecyclerView);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mRecyclerView);
        parent.registerAccountsChangedListener(this);

        return ourView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
