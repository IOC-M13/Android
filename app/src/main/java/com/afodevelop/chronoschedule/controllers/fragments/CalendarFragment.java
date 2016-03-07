package com.afodevelop.chronoschedule.controllers.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.afodevelop.chronoschedule.R;
import com.afodevelop.chronoschedule.controllers.activities.DayFormActivity;
import com.afodevelop.chronoschedule.controllers.adapters.ShiftsLegendListAdapter;
import com.imanoweb.calendarview.CalendarListener;
import com.imanoweb.calendarview.CustomCalendarView;
import com.imanoweb.calendarview.DayDecorator;
import com.imanoweb.calendarview.DayView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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
    private SimpleDateFormat df, dm;

    private class ColorDecorator implements DayDecorator {

        @Override
        public void decorate(DayView cell) {
            String hexColor = queryDayColor(cell);
            if (hexColor != null){
                int color = Color.parseColor("#" + hexColor);
                    Log.d("decorate", "color = " + color);
                    cell.setBackgroundColor(color);
            }
        }
    }


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
        df = new SimpleDateFormat("dd-MM-yyyy");
        dm = new SimpleDateFormat("MM-yyyy");
        calendarView.setCalendarListener(new CalendarListener() {
            @Override
            public void onDateSelected(Date date) {
                // HANDLE CLICK ON CALENDAR DAY

                Intent i = new Intent(getActivity(), DayFormActivity.class);
                Bundle extras = new Bundle();
                extras.putString("date", df.format(date));
                extras.putString("user", DEMO_USERS[0]);
                i.putExtras(extras);
                startActivity(i);
            }

            @Override
            public void onMonthChanged(Date date) {
                // HANDLE SWITCH TO NEXT/PREV MONTH

            }
        });

        //adding calendar day decorators
        List<DayDecorator> decorators = new ArrayList<>();
        decorators.add(new ColorDecorator());
        calendarView.setDecorators(decorators);
        calendarView.refreshCalendar(currentCalendar);

        ShiftsLegendListAdapter adapter = new ShiftsLegendListAdapter(getActivity(), DEMO_COLORS, DEMO_SHIFTS);
        ListView list = (ListView) myFragmentView.findViewById(R.id.shifts_legend_list);
        list.setAdapter(adapter);


        return myFragmentView;
    }

    private String queryDayColor(DayView cell){

        Date dayDate = cell.getDate();
        switch (df.format(dayDate)){
            case "08-03-2016":
                return "ff0000";
            case "18-03-2016":
                return "00FF00";
            case "28-03-2016":
                return "0000ff";
            default:
                return null;
        }
    }
}