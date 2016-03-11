package com.afodevelop.chronoschedule.controllers.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.afodevelop.chronoschedule.R;
import com.afodevelop.chronoschedule.controllers.activities.ShiftFormActivity;
import com.afodevelop.chronoschedule.controllers.adapters.ShiftsListArrayAdapter;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by alex on 3/03/16.
 */
public class ShiftsFragment extends Fragment {

    //DEMO_DATA
    ArrayList<String> DEMO_SHIFTS = new ArrayList<String>(Arrays.asList(
            "libre",
            "mañana",
            "tarde",
            "noche"));

    // COSTANTS


    // CLASSWIDE VARIABLES
    private View myFragmentView;
    private ListView listView;
    private ShiftsListArrayAdapter arrayAdapter;
    private FloatingActionButton addShiftButton;
    private boolean isNew;

    // LOGIC

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myFragmentView = inflater.inflate(R.layout.shifts_fragment, container, false);

        listView = (ListView) myFragmentView.findViewById(R.id.shifts_list);
        arrayAdapter = new ShiftsListArrayAdapter(getActivity(), R.layout.users_shifts_listview, DEMO_SHIFTS);
        listView.setAdapter(arrayAdapter);




        addShiftButton = (FloatingActionButton) myFragmentView.findViewById(R.id.add_shift_button);
        addShiftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ShiftFormActivity.class);
                Bundle extras = new Bundle();
                extras.putBoolean("isNew", true);
                i.putExtras(extras);
                startActivity(i);
            }
        });

        return myFragmentView;

    }
}