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
import com.afodevelop.chronoschedule.controllers.activities.UserFormActivity;
import com.afodevelop.chronoschedule.controllers.adapters.UsersListArrayAdapter;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by alex on 3/03/16.
 */
public class UsersFragment extends Fragment {

    ArrayList<String> DEMO_USERS = new ArrayList<String>(Arrays.asList(
            "administrador",
            "Alejandro Olivan",
            "Oscar Membrilla",
            "Armando Bronca"));

    // COSTANTS


    // CLASSWIDE VARIABLES
    private View myFragmentView;
    private ListView listView;
    private UsersListArrayAdapter arrayAdapter;
    private FloatingActionButton addUserButton;
    private boolean isNew;


    // LOGIC

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myFragmentView = inflater.inflate(R.layout.users_fragment, container, false);

        listView = (ListView) myFragmentView.findViewById(R.id.users_list);
        arrayAdapter = new UsersListArrayAdapter(getActivity(), R.layout.users_shifts_listview, DEMO_USERS);
        listView.setAdapter(arrayAdapter);

        addUserButton = (FloatingActionButton) myFragmentView.findViewById(R.id.add_user_button);
        addUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), UserFormActivity.class);
                Bundle extras = new Bundle();
                extras.putBoolean("isNew", true);
                i.putExtras(extras);
                startActivity(i);
            }
        });


        return myFragmentView;
    }
}