package com.juicedfootball.ui.presenter;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

import com.juicedfootball.common.JuicedFootballUtils;


/**
 * Created by anthonylee on 10/24/15.
 */
public class JuicedFootballTouchHelper extends ItemTouchHelper.Callback {
    private final String LOG_TAG = JuicedFootballUtils.LOG_TAG_MAIN_ACTIVITY;

    public interface JuicedFootballTouchHelperAdapter {
        void onItemMove(int fromPosition, int toPosition);
        void onItemDismiss(int position);
        void onItemChanged(int position);
    }

    JuicedFootballTouchHelperAdapter mAdapter;
    RecyclerView mRecyclerView;

    public JuicedFootballTouchHelper(JuicedFootballTouchHelperAdapter adapter,
                                     RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
        mAdapter = adapter;
    }

    public boolean onMove(RecyclerView recyclerView,
            RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        Log.d(LOG_TAG, "onMove() - viewHolder: " + viewHolder);
        mAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
        Log.d(LOG_TAG, "onSwiped() - viewHolder: " + viewHolder + " direction: " + direction);

        JuicedFootballViewAdapter.AccountViewHolder theViewHolder =
            (JuicedFootballViewAdapter.AccountViewHolder)viewHolder;
        theViewHolder.dumpViewVisibility();

//        theViewHolder.mIsPendingDelete = !theViewHolder.mIsPendingDelete;
//        mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        Log.d(LOG_TAG, "clearView() - viewHolder: " + viewHolder);
//        if (viewHolder != null) {
//            getDefaultUIUtil().clearView(
//                ((JuicedFootballViewAdapter.AccountViewHolder) viewHolder).getSwipableView());
//        }
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        Log.d(LOG_TAG, "onSelectedChanged() - viewHolder: " + viewHolder + " actionState: " + actionState);
        super.onSelectedChanged(viewHolder, actionState);
        /*
        if (viewHolder != null) {
            getDefaultUIUtil().onSelected(
                ((JuicedFootballViewAdapter.AccountViewHolder) viewHolder).getSwipableView());
        }
*/
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
            float dX, float dY, int actionState, boolean isCurrentlyActive) {
        Log.d(LOG_TAG, "onChildDraw() - viewHolder: " + viewHolder + " dX: " + dX + " actionState: "
            + actionState + " isCurrentlyActive: " + isCurrentlyActive);
        if (viewHolder != null) {
            getDefaultUIUtil().onDraw(c, recyclerView,
                ((JuicedFootballViewAdapter.AccountViewHolder) viewHolder).getSwipableView(), dX, dY,
                actionState, isCurrentlyActive);
        }
    }

    @Override
    public void onChildDrawOver(Canvas c, RecyclerView recyclerView,
            RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState,
            boolean isCurrentlyActive) {
        Log.d(LOG_TAG, "onChildDrawOver() - viewHolder: " + viewHolder + " dX: " + dX + " actionState: "
            + actionState + " isCurrentlyActive: " + isCurrentlyActive);
        if (viewHolder != null) {
            getDefaultUIUtil().onDrawOver(c, recyclerView,
                ((JuicedFootballViewAdapter.AccountViewHolder) viewHolder).getSwipableView(), dX, dY,
                actionState, isCurrentlyActive);
        }
    }

    // Outlines which way things can be dragged/swiped
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        Log.d(LOG_TAG, "getMovementFlags() - viewHolder: " + viewHolder);
        if (viewHolder instanceof JuicedFootballViewAdapter.AccountViewHolder) {
//            int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
            int swipeFlags = ItemTouchHelper.END;
            // Uncomment this if you want to be able to drag stuff around.
//        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            return makeMovementFlags(0, swipeFlags);
        }
        return 0;
    }

    // Drag w/o swipe is enabled
    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    // Allow drag to start anywhere in the view
    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }
}
