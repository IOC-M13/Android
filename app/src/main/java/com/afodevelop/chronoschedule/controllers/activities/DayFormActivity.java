package com.afodevelop.chronoschedule.controllers.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
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

    // LOGIC

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_form);


        dateTextView = (TextView) findViewById(R.id.edit_shiftday_text_date);
        userTextView = (TextView) findViewById(R.id.edit_shiftday_text_user);


        date = getIntent().getExtras().getString("date");
        user = getIntent().getExtras().getString("user");

        dateTextView.setText(date);
        userTextView.setText(user);

        Spinner spinner = (Spinner) findViewById(R.id.edit_shiftday_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, DEMO_SHIFTS);
        // Specify the layout to use when the list of choices appears
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(spinnerAdapter);


    }
}
