package com.juicedfootball.ui.presenter;

import android.accounts.Account;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.juicedfootball.R;
import com.juicedfootball.common.JuicedFootballUtils;
import com.juicedfootball.ui.activity.ItemDetailsFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by anthonylee on 10/21/15.
 */
public class JuicedFootballViewAdapter extends
        RecyclerView.Adapter<JuicedFootballViewAdapter.AccountViewHolder>  implements
        JuicedFootballTouchHelper.JuicedFootballTouchHelperAdapter {

    private static final String LOG_TAG = JuicedFootballUtils.LOG_TAG_DATA_ADAPTER;

    CopyOnWriteArrayList<Account> mAccounts;
    Context mContext;
    RecyclerView mRecyclerView;
    AccountSelectedListener mListener;
    AccountLongPressListener mLongListener;

    public JuicedFootballViewAdapter(Context context, CopyOnWriteArrayList<Account> accounts,
                 AccountSelectedListener listener, AccountLongPressListener longListener) {
        // We need to convert this to a list for manipulation purposes
        mAccounts = accounts;
        mContext = context;
        mListener = listener;
        mLongListener = longListener;
    }

    public class AccountViewHolder extends RecyclerView.ViewHolder
            implements View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
//            implements View.OnTouchListener, View.OnLongClickListener, PopupMenu.OnMenuItemClickListener {
//            implements View.OnTouchListener {
        public CardView cv;
        public TextView mUsername;
        public TextView mAccountType;
        public View mUndoView;
        public View mContentView;
        public boolean mIsPendingDelete;
        public View mParentView;
        public int mPosition;

        AccountViewHolder(View itemView) {
            super(itemView);
            Log.d(LOG_TAG, "Creating AccountViewHolder with view: " + itemView);

            mParentView = itemView;
            mUndoView = itemView.findViewById(R.id.undo);
            mContentView = itemView.findViewById(R.id.card_guts);
            cv = (CardView)itemView.findViewById(R.id.account_card);
            mUsername = (TextView)itemView.findViewById(R.id.card_username);
            mAccountType = (TextView)itemView.findViewById(R.id.card_account_type);
            mIsPendingDelete = false;

//            mContentView.setOnTouchListener(AccountViewHolder.this);
//            mUndoView.setOnTouchListener(AccountViewHolder.this);

            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            Log.d(LOG_TAG, "onMenuItemClick() - position: " + mPosition);
            Account theAccount = mAccounts.get(mPosition);
            // We might just want to ripple to up to some handler
            Toast.makeText(mContext, "Account: " + theAccount.name, Toast.LENGTH_LONG).show();
            return true;
        }

        /*
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (v == mContentView) {
                Log.d(LOG_TAG, "onTouch - content view event:" + event);
            } else if (v == mUndoView) {
                Log.d(LOG_TAG, "onTouch - undo view event: " + event);
            } else {
                Log.d(LOG_TAG, "onTouch - unknown view");
            }
            return false;
        }
*/

        public boolean isPendingDelete() {
            return mIsPendingDelete;
        }

        public void setIsPendingDelete(boolean isPendingDelete) {
            mIsPendingDelete = isPendingDelete;
        }

        public void dumpViewVisibility() {
            Log.d(LOG_TAG, "dumpViewVisiblity() - content: " + mContentView.getVisibility());
            Log.d(LOG_TAG, "dumpViewVisiblity() - undo: " + mUndoView.getVisibility());
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v,
                                        ContextMenu.ContextMenuInfo menuInfo) {
            MenuInflater inflater = new MenuInflater(mContext);
            inflater.inflate(R.menu.menu_juiced_football2, menu);
            menu.setHeaderTitle("Booyah!");

            // We are going to create the listeners here because we are in the AccountViewHolder
            // So we know the position of what was selected. If we move this code into something
            // like the holding activity or fragment, it seems that we'll lose this information.
            int numItems = menu.size();
            for (int index = 0; index < numItems; index++) {
                MenuItem theItem = menu.getItem(index);
                theItem.setOnMenuItemClickListener(this);
            }


/*
            menu.setHeaderTitle("Select The Action");
            menu.add(0, v.getId(), 0, "Call");//groupId, itemId, order, title
            menu.add(0, v.getId(), 0, "SMS");


            myActionItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Account theAccount = mAccounts.get(mPosition);

                    return true;
                }
            });
*/
        }

        public View getSwipableView() {
            if (mIsPendingDelete) {
//                Log.d(LOG_TAG, "getSwipeableView() - is pending delete returning undo view");
                // If we are pending a delete, we need to be able to swipe away the undo view
                // but maybe in this case, we don't need to swipe away anything?  TBD
                return mUndoView;
            }
            // If we are not pending a delete, that means that we need to be be able swipe
            // away the content view.
//            Log.d(LOG_TAG, "getSwipeableView() - is not pending delete returning content view");
            return mContentView;
        }

//        @Override
//        public boolean onLongClick(View v) {
//            PopupMenu popup = new PopupMenu(v.getContext(), v);
//            popup.inflate(R.menu.menu_juiced_football2);
//            popup.setOnMenuItemClickListener(this);
//            popup.show();
//            return true;
//        }

/*
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            Log.d(LOG_TAG, "onMenuItemClick()!!");
            return true;
        }
*/

    }

    @Override
    public int getItemCount() {
        return mAccounts.size();
    }

    @Override
    public AccountViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Log.d(LOG_TAG, "onCreateViewHolder() i: " + i);
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.account_cardview,
                viewGroup, false);
        return new AccountViewHolder(v);
    }

    public interface AccountSelectedListener {
        void onAccountSelected(int position);
    }

    public interface AccountLongPressListener {
        void onLongPress(int position);
    }

    @Override
    public void onBindViewHolder(AccountViewHolder accountViewHolder, int i) {
        Account theAccount = mAccounts.get(i);
        final int position = i;

        Log.i(LOG_TAG, "onBindViewHolder() - binding Account: " + theAccount);

        accountViewHolder.mUsername.setText(theAccount.name);
        accountViewHolder.mAccountType.setText(theAccount.type);
        accountViewHolder.mPosition = i;

        accountViewHolder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG, "onClick!!! view: " + v + " position: " + position);

                // TODO send intent to change the main fragment to the details view
                // for this message.
                mListener.onAccountSelected(position);

            }
        });

//        accountViewHolder.cv.setOnLongClickListener(accountViewHolder);

/*
        accountViewHolder.cv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.d(LOG_TAG, "onLongClick!!!! view: " + v + " position: " + position);

                // Show a menu of some sort?
                mLongListener.onLongPress(position);
                return true;
            }
        });
*/
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
        super.onAttachedToRecyclerView(recyclerView);
    }

    // JuicedFootballTouchHelper.JuicedFootballTouchHelperAdapter implementation to facilitate
    // update to our adapter data based on what is going on in the ItemTouchHelper
    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mAccounts, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mAccounts, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    public void onItemDismiss(int position) {
        Log.d(LOG_TAG, "onItemDismiss()");

        // Its possible that we want to give the user some time to undo this operation
        // so instead of processing this operation right now, we could set a timer
        // to update mAccounts later? and to revert the operation entirely if the user
        // decides that they want to undo the action
        mAccounts.remove(position);
        notifyItemRemoved(position);
    }

    public void onItemChanged(int position) {
        Log.d(LOG_TAG, "onItemDismiss()");
        notifyItemChanged(position);
    }
}
