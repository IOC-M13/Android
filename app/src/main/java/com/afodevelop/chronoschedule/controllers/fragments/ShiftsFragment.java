package com.afodevelop.chronoschedule.controllers.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.afodevelop.chronoschedule.R;
import com.afodevelop.chronoschedule.controllers.activities.ShiftFormActivity;

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
    private boolean isNew;

    // LOGIC

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myFragmentView = inflater.inflate(R.layout.shifts_fragment, container, false);

        listView = (ListView) myFragmentView.findViewById(R.id.shifts_list);
        arrayAdapter = new ArrayAdapter(getActivity(), R.layout.users_shifts_listview, R.id.users_shift_itemname, DEMO_SHIFTS);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent i = new Intent(getActivity(), ShiftFormActivity.class);
                Bundle extras = new Bundle();
                extras.putInt("idShift", position);
                i.putExtras(extras);
                startActivity(i);

            }
        });

        return myFragmentView;

    }
}