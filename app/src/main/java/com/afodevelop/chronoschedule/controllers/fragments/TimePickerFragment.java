package com.afodevelop.chronoschedule.controllers.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;


/**
 * This is the Fragment that generates our time-picker dialog.
 *
 * @author Alejandro Olivan Alvarez
 */
public class TimePickerFragment extends DialogFragment implements
        TimePickerDialog.OnTimeSetListener{

    // CLASS_WIDE VARIABLES
    String targetView;

    //LOGIC

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    public static TimePickerFragment newInstance(String targetView) {
        TimePickerFragment f = new TimePickerFragment();
        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("targetView", targetView);
        f.setArguments(args);

        return f;
    }

    /**
     * The mandatory onCreateDialog override where we define the dialog settings
     * and contents.
     *
     * @Alejandro Olivan Alvarez
     * @param savedInstanceState
     * @return A Doalg class instance with the generated Dialog
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        //Use the current time as the default values for the time picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        targetView = getArguments().getString("targetView");

        TimePickerDialog tpd = new TimePickerDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT
                ,this, hour, minute, DateFormat.is24HourFormat(getActivity()));

        TextView tvTitle = new TextView(getActivity());
        tvTitle.setText("Select time");
        tvTitle.setBackgroundColor(Color.parseColor("#EEE8AA"));
        tvTitle.setPadding(5, 3, 5, 3);
        tvTitle.setGravity(Gravity.CENTER_HORIZONTAL);
        tpd.setCustomTitle(tvTitle);

        return tpd;
    }

    /**
     * This method handles what to do once the user has selected certain time on the
     * time picker dialog.
     * Basically it ensures time format follows the correct string format.
     *
     * @Alejandro Olivan Alvarez
     * @param view
     * @param hourOfDay
     * @param minute
     */
    public void onTimeSet(TimePicker view, int hourOfDay, int minute){

        //Get reference of host activity (XML Layout File) TextView widget
        int id = getResources().getIdentifier(targetView, "id", getActivity().getPackageName());
        TextView tv = (TextView) getActivity().findViewById(id);

        String hour, minutes;

        if(hourOfDay < 10){
            hour = "0" + String.valueOf(hourOfDay);
        } else {
            hour = String.valueOf(hourOfDay);
        }

        if(minute < 10){
            minutes = "0" + String.valueOf(minute);
        }else{
            minutes = String.valueOf(minute);
        }

        tv.setText(hour + ":" + minutes + ":00");
    }
}
