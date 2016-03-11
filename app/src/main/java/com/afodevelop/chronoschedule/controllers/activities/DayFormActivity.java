package com.afodevelop.chronoschedule.controllers.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.afodevelop.chronoschedule.R;

public class DayFormActivity extends AppCompatActivity {

    // DEMO DATA
    private final String[] DEMO_SHIFTS = {
            "libre",
            "mañana",
            "tarde",
            "noche"
    };

    // CLASSWIDE VARIABLES
    private String date, user;
    private TextView dateTextView, userTextView;
    private boolean isAdmin;
    private TextView spinnerLabelTextView, startTimeTextView, endTimeTextView;
    private Spinner chooseUserSpinner;
    private LinearLayout startTimeLayout, endTimeLayout;


    // LOGIC

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_form);

        isAdmin = checkAdminUser();

        dateTextView = (TextView) findViewById(R.id.edit_shiftday_text_date);
        userTextView = (TextView) findViewById(R.id.edit_shiftday_text_user);


        date = getIntent().getExtras().getString("date");
        user = getIntent().getExtras().getString("user");

        dateTextView.setText(date);
        userTextView.setText(user);


        if (isAdmin) {

            spinnerLabelTextView = (TextView) findViewById(R.id.edit_shiftday_spinner_label);
            spinnerLabelTextView.setVisibility(View.VISIBLE);

            chooseUserSpinner = (Spinner) findViewById(R.id.edit_shiftday_spinner);
            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, DEMO_SHIFTS);
            // Specify the layout to use when the list of choices appears
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            chooseUserSpinner.setAdapter(spinnerAdapter);
            chooseUserSpinner.setVisibility(View.VISIBLE);

        }

        startTimeLayout = (LinearLayout) findViewById(R.id.edit_shiftday_startime_layout);
        startTimeLayout.setVisibility(View.VISIBLE);
        startTimeTextView = (TextView) findViewById(R.id.edit_shiftday_text_starttime);
        startTimeTextView.setVisibility(View.VISIBLE);

        endTimeLayout = (LinearLayout) findViewById(R.id.edit_shiftday_endtime_layout);
        endTimeLayout.setVisibility(View.VISIBLE);
        endTimeTextView = (TextView) findViewById(R.id.edit_shiftday_text_endtime);
        endTimeTextView.setVisibility(View.VISIBLE);

    }

    private boolean checkAdminUser(){
        return false;
    }
}
