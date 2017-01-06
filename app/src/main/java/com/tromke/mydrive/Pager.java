package com.tromke.mydrive;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.tromke.mydrive.Fragments.LocationFragment;
import com.tromke.mydrive.Fragments.TripsFragment;

/**
 * Created by drrao on 1/6/2017.
 */
public class Pager extends FragmentPagerAdapter {
    int tabCount;
    public Pager(FragmentManager fm,int tabCount) {
        super(fm);
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                LocationFragment locationFragment = new LocationFragment();
                return locationFragment;
            case 1:
                TripsFragment tripsFragment = new TripsFragment();
                return tripsFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
