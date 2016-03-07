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
 * Created by alex on 7/03/16.
 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{


    String targetView;

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



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        //Use the current time as the default values for the time picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        targetView = getArguments().getString("targetView");

        TimePickerDialog tpd = new TimePickerDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT
                ,this, hour, minute, DateFormat.is24HourFormat(getActivity()));

        //You can set a simple text title for TimePickerDialog
        //tpd.setTitle("Title Of Time Picker Dialog");

        /*.........Set a custom title for picker........*/
        TextView tvTitle = new TextView(getActivity());
        tvTitle.setText("Select time");
        tvTitle.setBackgroundColor(Color.parseColor("#EEE8AA"));
        tvTitle.setPadding(5, 3, 5, 3);
        tvTitle.setGravity(Gravity.CENTER_HORIZONTAL);
        tpd.setCustomTitle(tvTitle);
        /*.........End custom title section........*/

        return tpd;
    }

    //onTimeSet() callback method
    public void onTimeSet(TimePicker view, int hourOfDay, int minute){
        //Do something with the user chosen time

        //Get reference of host activity (XML Layout File) TextView widget
        int id = getResources().getIdentifier(targetView, "id", getActivity().getPackageName());
        TextView tv = (TextView) getActivity().findViewById(id);
        //Set a message for user

        //Get the AM or PM for current time
        String aMpM = "AM";
        if(hourOfDay >11)
        {
            aMpM = "PM";
        }

        //Make the 24 hour time format to 12 hour time format
        int currentHour;
        if(hourOfDay>11)
        {
            currentHour = hourOfDay - 12;
        }
        else
        {
            currentHour = hourOfDay;
        }

        tv.setText(String.valueOf(currentHour) + " : " + String.valueOf(minute) + " " + aMpM );

    }
}
