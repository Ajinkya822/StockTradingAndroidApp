package com.example.stocks;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class ItemMoveCallbackFav extends ItemTouchHelper.Callback {

    private final ItemMoveCallbackFav.ItemTouchHelperContract fAdapter;

    public ItemMoveCallbackFav(ItemMoveCallbackFav.ItemTouchHelperContract adapter){
        fAdapter = adapter;
    }
    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        return makeMovementFlags(dragFlags, 0);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        fAdapter.onRowMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if (viewHolder instanceof FavoritesAdapter.MyViewHolder) {
                FavoritesAdapter.MyViewHolder myViewHolder=
                        (FavoritesAdapter.MyViewHolder) viewHolder;
                fAdapter.onRowSelected(myViewHolder);
            }

        }

        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

        if (viewHolder instanceof FavoritesAdapter.MyViewHolder) {
            FavoritesAdapter.MyViewHolder myViewHolder= (FavoritesAdapter.MyViewHolder) viewHolder;
            fAdapter.onRowClear(myViewHolder);
        }
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            Log.d("swipee","swipeeeeddddd");
    }

    public interface ItemTouchHelperContract {

        void onRowMoved(int fromPosition, int toPosition);
        void onRowSelected(FavoritesAdapter.MyViewHolder myViewHolder);
        void onRowClear(FavoritesAdapter.MyViewHolder myViewHolder);

    }

}
