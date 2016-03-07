package com.afodevelop.chronoschedule.controllers.adapters;

import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.afodevelop.chronoschedule.controllers.fragments.CalendarFragment;
import com.afodevelop.chronoschedule.controllers.fragments.ShiftsFragment;
import com.afodevelop.chronoschedule.controllers.fragments.UsersFragment;

/**
 * Created by alex on 3/03/16.
 */
public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                CalendarFragment calendarFragment = new CalendarFragment();
                return calendarFragment;
            case 1:
                UsersFragment usersFragment = new UsersFragment();
                return usersFragment;
            case 2:
                ShiftsFragment shiftsFragment = new ShiftsFragment();
                return shiftsFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}