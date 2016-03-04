package com.afodevelop.chronoschedule.controllers;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.afodevelop.chronoschedule.R;
import com.afodevelop.chronoschedule.ShiftsLegendListAdapter;
import com.imanoweb.calendarview.CalendarListener;
import com.imanoweb.calendarview.CustomCalendarView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by alex on 3/03/16.
 */
public class CalendarFragment extends Fragment {

    // DEMO DATA
    private final String[] DEMO_USERS = {
            "administrador",
            "Alejandro Olivan",
            "Oscar Membrilla",
            "Armando Bronca",
    };

    private final String[] DEMO_COLORS = {
            "00FF00",
            "FF0000",
            "0000FF",
            "FF00FF"
    };

    private final String[] DEMO_SHIFTS = {
            "libre",
            "mañana",
            "tarde",
            "noche"
    };

    // COSTANTS


    // CLASSWIDE VARIABLES
    private CustomCalendarView calendarView;
    private View myFragmentView;


    // LOGIC

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myFragmentView = inflater.inflate(R.layout.calendar_fragment, container, false);


        Spinner spinner = (Spinner) myFragmentView.findViewById(R.id.calendar_user_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, DEMO_USERS);
        // Specify the layout to use when the list of choices appears
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(spinnerAdapter);


        //Initialize CustomCalendarView from layout
        calendarView = (CustomCalendarView) myFragmentView.findViewById(R.id.calendar_view);
        //Initialize calendar with date
        Calendar currentCalendar = Calendar.getInstance(Locale.getDefault());
        //Show monday as first date of week
        calendarView.setFirstDayOfWeek(Calendar.MONDAY);
        //Show/hide overflow days of a month
        calendarView.setShowOverflowDate(false);
        //call refreshCalendar to update calendar the view
        calendarView.refreshCalendar(currentCalendar);
        //Handling custom calendar events
        calendarView.setCalendarListener(new CalendarListener() {
            @Override
            public void onDateSelected(Date date) {
                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                Toast.makeText(getActivity(), df.format(date), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMonthChanged(Date date) {
                SimpleDateFormat df = new SimpleDateFormat("MM-yyyy");
                Toast.makeText(getActivity(), df.format(date), Toast.LENGTH_SHORT).show();
            }
        });

        ShiftsLegendListAdapter adapter = new ShiftsLegendListAdapter(getActivity(), DEMO_COLORS, DEMO_SHIFTS);
        ListView list = (ListView) myFragmentView.findViewById(R.id.shifts_legend_list);
        list.setAdapter(adapter);




        return myFragmentView;
    }
}