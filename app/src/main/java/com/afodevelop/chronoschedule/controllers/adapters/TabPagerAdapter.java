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

    // CLASS-WIDE VARIABLES
    int mNumOfTabs;
    HashMap<Integer,Fragment> fragments = new HashMap<>();

    // COSTRUCTOR
    public TabPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    // LOGIC

    /**
     * This method is the override of getItem... it generates and returns the
     * correspondant fragment based on position. It also stores a reference to the
     * fragment into a HashMap, in order to retrieve them as necessary.
     * @param position
     * @return
     */
    @Override
    public Fragment getItem(int position) {

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

    /**
     * Return the number of tabs handled by the adapter
     * @return
     */
    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    /**
     * This method is useful to retrieve areference to the instantiated fragment
     * at each position.
     * @param key the position (tab position)
     * @return the fragment instance referenced by the key.
     */
    public Fragment getfragments(int key){
        return fragments.get(key);
    }
}