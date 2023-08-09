package com.example.stocks;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {
    String ticker;
    public ViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    public ViewPagerAdapter(FragmentActivity fragmentActivity, String ticker) {
        super(fragmentActivity);
        this.ticker=ticker;
        Log.d("fragaj","Ticker is "+ticker);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position){
            case 0:
                return new FragmentChart1(ticker);
            case 1:
                return new FragmentChart2(ticker);
            default:
                return new FragmentChart1(ticker);
        }

      //  return null;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
