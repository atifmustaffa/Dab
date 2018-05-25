package com.atif.dab.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.atif.dab.HomeFragment;
import com.atif.dab.PreviousFragment;

/**
 * Created by Atif Mustaffa on 13/4/2018.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    HomeFragment homeFragment;
    PreviousFragment previousFragment;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
        homeFragment = new HomeFragment();
        previousFragment = new PreviousFragment();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: return homeFragment;
            case 1: return previousFragment;
            default: return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

}
