package com.crycetruly.a4app.adapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.crycetruly.a4app.fragments.GroupsFragment;
import com.crycetruly.a4app.fragments.JourneysFragment;

public class PagerAdapter extends FragmentPagerAdapter {

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 1:
                return new JourneysFragment();
            case 0:
                return new GroupsFragment();
                default:
                return null;
        }


    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Created";
            case 1:
                return "Journeys";

            default:
                return super.getPageTitle(position);
        }


    }
}
