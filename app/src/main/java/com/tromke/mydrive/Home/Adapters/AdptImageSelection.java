package com.tromke.mydrive.Home.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.tromke.mydrive.Fragments.FrgCameraImageSelection;
import com.tromke.mydrive.Fragments.FrgGalleryImageSelection;

/**
 * Created by Devrath on 11-09-2016.
 */
public class AdptImageSelection  extends FragmentPagerAdapter {

    private final String[] TITLES = { "CAMERA" , "GALLERY" };

    public AdptImageSelection(FragmentManager fm) {
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TITLES[position];
    }

    @Override
    public int getCount() {
        return TITLES.length;
    }

    @Override
    public Fragment getItem(int position) {
        return displayView(position);
    }


    private Fragment displayView(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = FrgCameraImageSelection.newInstance();
                break;
            case 1:
                fragment = FrgGalleryImageSelection.newInstance();
                break;
            default:
                break;
        }
        return fragment;

    }

}
