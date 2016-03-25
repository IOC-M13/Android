package com.afodevelop.chronoschedule.controllers.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afodevelop.chronoschedule.R;
import com.afodevelop.chronoschedule.controllers.activities.DayFormActivity;
import com.afodevelop.chronoschedule.controllers.activities.MainActivity;
import com.afodevelop.chronoschedule.controllers.activities.UserFormActivity;
import com.afodevelop.chronoschedule.controllers.adapters.ShiftsLegendListAdapter;
import com.afodevelop.chronoschedule.controllers.sqliteControllers.SQLiteException;
import com.afodevelop.chronoschedule.model.User;
import com.imanoweb.calendarview.CalendarListener;
import com.imanoweb.calendarview.CustomCalendarView;
import com.imanoweb.calendarview.DayDecorator;
import com.imanoweb.calendarview.DayView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by alex on 3/03/16.
 */
public class CalendarFragment extends Fragment {

    // COSTANTS


    // CLASSWIDE VARIABLES
    private MainActivity mainActivity;
    private CustomCalendarView calendarView;
    private View myFragmentView;
    private Spinner spinner;
    private ArrayAdapter<String> spinnerAdapter;
    private LinearLayout selectedUserLayout;
    private TextView selectedUserText;
    private SimpleDateFormat df, dm;
    private Calendar currentCalendar;

    private String[] userNames;
    private String[] shiftNames;
    private String[] shiftColors;
    private HashMap<Date, Boolean> filledDays;
    private User calendarUser;

    //INTERNAL CLASS DEFINITIONS

    /**
     * This internal class defines the calendar day decorator object
     */
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

        // Initialize a variable pointing to parent Activity
        mainActivity = (MainActivity) getActivity();
        calendarUser = mainActivity.getUser();
        filledDays = new HashMap<>();

        // Initialize early values
        refreshData();

        // Initialize either spinner or user bar
        if (mainActivity.isAdmin()) {
            setupSpinner();
        } else {
            setupUserBar();
        }

        // Initialize the calendar and initial decoration
        setupCalendar();
        refreshCalendarDecoration();
        refreshCalendarLegend();

        return myFragmentView;
    }

    /**
     * We are using the onResume flow call to prompt for calendar refreshing:
     * Refreshthe data, the decoration, and apply all this again to the calendar
     */
    @Override
    public void onResume() {
        super.onResume();
        refreshData();
        refreshCalendarDecoration();
        refreshCalendarLegend();
    }

    /**
     * A method that creates the non-admin user name clickable bar
     */
    private void setupUserBar(){
        selectedUserText = (TextView) myFragmentView.findViewById(R.id.calendar_user_text);
        selectedUserText.setText(mainActivity.getUser().getRealName());
        selectedUserLayout = (LinearLayout) myFragmentView.findViewById(R.id.selected_user_layout);
        selectedUserLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getActivity(), UserFormActivity.class);
                Bundle extras = new Bundle();
                extras.putString("mode", "show");
                extras.putString("user", mainActivity.getUser().getUserName());
                i.putExtras(extras);
                startActivity(i);
            }
        });
        selectedUserLayout.setVisibility(View.VISIBLE);
    }

    /**
     * Setup a spinner for admin users so they can choose which user they want to
     * work onto.
     */
    private void setupSpinner(){
        spinner = (Spinner) myFragmentView.findViewById(R.id.calendar_user_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        spinnerAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, userNames);
        // Specify the layout to use when the list of choices appears
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(spinnerAdapter);
        int selectedPosition = spinnerAdapter.getPosition(calendarUser.getUserName());
        spinner.setSelection(selectedPosition);
        spinner.setVisibility(View.VISIBLE);
        spinnerAdapter.notifyDataSetChanged();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    calendarUser = mainActivity.getSqLiteAssistant().
                            getUserByUserName(userNames[position]);
                    refreshCalendarDecoration();
                } catch (SQLiteException e) {
                    printToast("Error fetching data from DB.");
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * This method launches the FormDay activity, It does it with dynamic Bundle data
     * based on received arguments
     * @param date Date of the Day
     * @param mode mode (new, edit, show)
     */
    public void launchDayFormActivity(Date date, String mode){

        Intent i = new Intent(getActivity(), DayFormActivity.class);
        Bundle extras = new Bundle();
        extras.putString("date", df.format(date));
        extras.putString("user", calendarUser.getUserName());
        extras.putString("mode", mode);
        i.putExtras(extras);
        startActivity(i);
    }

    /**
     * Get/Refresh data from DB to fill arrays of strings with most used data
     * usernames, shiftnames and shift colors
     */
    public void refreshData(){
        try {
            Log.d("calendarFragment","refreshing data");
            userNames = mainActivity.getUserNames();
            shiftNames = mainActivity.getSqLiteAssistant().getShiftNames();
            shiftColors = mainActivity.getShiftColors();
            if (spinnerAdapter != null) {
                setupSpinner();
            }
        } catch (SQLiteException e) {
            printToast("Error fetching data from DB.");
            e.printStackTrace();
        }
    }

    /**
     * This method actually instantiates and sets up the calendar View on the
     * fragment, initializing dateformats, week format, current date, and setting
     * the preceptive click listener.
     */
    public void setupCalendar(){
        calendarView = (CustomCalendarView) myFragmentView.findViewById(R.id.calendar_view);
        currentCalendar = Calendar.getInstance(Locale.getDefault());
        df = new SimpleDateFormat("yyyy-MM-dd");
        dm = new SimpleDateFormat("MM-yyyy");

        calendarView.setFirstDayOfWeek(Calendar.MONDAY);
        calendarView.setShowOverflowDate(false);
        calendarView.refreshCalendar(currentCalendar);
        calendarView.setCalendarListener(new CalendarListener() {
            @Override
            public void onDateSelected(Date date) {
                if (mainActivity.isAdmin()){
                    if (filledDays.get(date)){
                        launchDayFormActivity(date, "edit");
                    } else {
                        launchDayFormActivity(date, "new");
                    }
                } else {
                    if (filledDays.get(date)){
                        launchDayFormActivity(date, "show");
                    }
                }
            }
            @Override
            public void onMonthChanged(Date date) {

            }
        });
    }

    /**
     * This method regenerates the calendar colors
     */
    public void refreshCalendarDecoration(){
        //adding calendar day decorators
        Log.d("calendarFragment","refreshing calendar decorators");
        filledDays.clear();
        List<DayDecorator> decorators = new ArrayList<>();
        decorators.add(new ColorDecorator());
        calendarView.setDecorators(decorators);
        calendarView.refreshCalendar(currentCalendar);
    }

    /**
     * Encapsulate the calendar generation method
     */
    public void refreshCalendarLegend(){
        Log.d("calendarFragment","refreshing calendar legend");
        ShiftsLegendListAdapter adapter = new ShiftsLegendListAdapter(getActivity(), shiftColors, shiftNames);
        ListView list = (ListView) myFragmentView.findViewById(R.id.shifts_legend_list);
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    /**
     * This method queries the selected user's calendar UserShift entries for a given
     * data. It returns the Shift color for that day
     * @param cell The Day Cell (a Calendar Date) to be queried
     * @return a String with hex color
     */
    private String queryDayColor(DayView cell){
        Date dayDate = cell.getDate();
        String result;
        try {
            result = mainActivity.getShiftColor(calendarUser, dayDate);
            if (result == null){
                filledDays.put(dayDate, false);
            } else {
                filledDays.put(dayDate, true);
            }
            return result;
        } catch (SQLiteException | ParseException e) {
            printToast("Error fetching data from DB.");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * An auxiliar method to ease Toast printing
     * @param s
     */
    private void printToast(String s){
        Toast.makeText(mainActivity, s, Toast.LENGTH_SHORT).show();
    }
}