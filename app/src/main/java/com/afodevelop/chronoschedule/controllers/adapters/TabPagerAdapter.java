package com.afodevelop.chronoschedule.controllers.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.afodevelop.chronoschedule.controllers.fragments.CalendarFragment;
import com.afodevelop.chronoschedule.controllers.fragments.ShiftsFragment;
import com.afodevelop.chronoschedule.controllers.fragments.UsersFragment;

import java.util.HashMap;

/**
 * Created by alex on 3/03/16.
 */
public class TabPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    HashMap<Integer,Fragment> fragments = new HashMap<>();

    public TabPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        int fragmentId;
        switch (position) {
            case 0:
                CalendarFragment calendarFragment = new CalendarFragment();
                fragments.put(0,calendarFragment);
                return calendarFragment;
            case 1:
                UsersFragment usersFragment = new UsersFragment();
                fragments.put(1,usersFragment);
                return usersFragment;
            case 2:
                ShiftsFragment shiftsFragment = new ShiftsFragment();
                fragments.put(2,shiftsFragment);
                return shiftsFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    public Fragment getfragments(int key){
        return fragments.get(key);
    }
}