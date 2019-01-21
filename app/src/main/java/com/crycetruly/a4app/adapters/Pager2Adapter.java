package com.crycetruly.a4app.adapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.crycetruly.a4app.fragments.AddMemberFragment;
import com.crycetruly.a4app.fragments.GroupsFragment;
import com.crycetruly.a4app.fragments.InvitesFragment;
import com.crycetruly.a4app.fragments.JourneysFragment;
import com.crycetruly.a4app.fragments.MembersFragment;

public class Pager2Adapter extends FragmentPagerAdapter {

    public Pager2Adapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new MembersFragment();
            case 1:
                return new AddMemberFragment();
            case 2:
                return new InvitesFragment();
                default:
                return null;
        }


    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Members";
            case 1:
                return "Add new";

            case 2:
                return "Invites";

            default:
                return super.getPageTitle(position);
        }


    }
}
