package com.afodevelop.chronoschedule.controllers;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.afodevelop.chronoschedule.R;

/**
 * Created by alex on 3/03/16.
 */
public class ShiftsFragment extends Fragment {

    // DEMO DATA
    private final String[] DEMO_SHIFTS = {
            "libre",
            "mañana",
            "tarde",
            "noche",
    };

    // COSTANTS


    // CLASSWIDE VARIABLES
    private View myFragmentView;
    private ListView listView;
    private ArrayAdapter arrayAdapter;


    // LOGIC

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myFragmentView = inflater.inflate(R.layout.shifts_fragment, container, false);

        listView = (ListView) myFragmentView.findViewById(R.id.shifts_list);
        arrayAdapter = new ArrayAdapter(getActivity(), R.layout.users_shifts_listview, R.id.users_shift_itemname, DEMO_SHIFTS);
        listView.setAdapter(arrayAdapter);

        return myFragmentView;

    }
}