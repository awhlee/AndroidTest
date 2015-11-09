package com.juicedfootball.common;

import android.accounts.Account;
import android.content.Context;

import com.juicedfootball.ui.presenter.JuicedFootballViewAdapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by anthonylee on 10/29/15.
 */
public class JuicedApplicationData {

    // All JuicedApplicationData instances will share the same account list.
    private static CopyOnWriteArrayList<Account> mAccountList = new CopyOnWriteArrayList<>();

    public JuicedApplicationData() {
    }

    public void setAccounts(Account[] accounts) {
        // Add the entries one by one.
        mAccountList.clear();
        for (Account account : accounts) {
            mAccountList.add(account);
        }
    }

    public Account getAccountAt(int position) { return mAccountList.get(position);}

    public CopyOnWriteArrayList<Account> getBackingList() { return mAccountList; };
}

