package com.afodevelop.chronoschedule.controllers.activities;

import android.app.DialogFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.afodevelop.chronoschedule.R;
import com.afodevelop.chronoschedule.controllers.fragments.TimePickerFragment;
import com.github.danielnilsson9.colorpickerview.dialog.ColorPickerDialogFragment;
import com.github.danielnilsson9.colorpickerview.dialog.ColorPickerDialogFragment.ColorPickerDialogListener;

public class ShiftFormActivity extends AppCompatActivity implements ColorPickerDialogListener{


    // DEMO DATA
    private final String[] DEMO_SHIFTS = {
            "libre",
            "mañana",
            "tarde",
            "noche",
    };

    private final String[] DEMO_COLORS = {
            "00FF00",
            "FF0000",
            "0000FF",
            "FF00FF"
    };

    // COSTANTS
    private static final int DIALOG_ID = 0;

    // CLASSWIDE VARIABLES
    private Button startTimeButton, endTimeButton, pickColorButton;
    private TextView startTimeTextView, endTimeTextView;
    private EditText shiftNameTextEdit;

    private String shiftName, startDate, endDate, shiftColor;
    private int idShift;
    private boolean isNew = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shift_form);

        shiftName = DEMO_SHIFTS[getIntent().getExtras().getInt("idShift")];
        shiftColor = "#" + DEMO_COLORS[getIntent().getExtras().getInt("idShift")];
        shiftNameTextEdit = (EditText) findViewById(R.id.edit_shift_edittext_name);
        shiftNameTextEdit.setText(shiftName);

        startTimeButton = (Button) findViewById(R.id.edit_shift_start_time_button);
        startTimeTextView = (TextView) findViewById(R.id.edit_shift_start_time_text);
        if (isNew){
            startTimeButton.setText("SET TIME");
        }else{
            startTimeButton.setText("EDIT TIME");
            startTimeTextView.setText("00:00 AM");
        }

        startTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = TimePickerFragment.newInstance("edit_shift_start_time_text");
                newFragment.show(getFragmentManager(), "TimePicker");
            }
        });


        endTimeButton = (Button) findViewById(R.id.edit_shift_end_time_button);
        endTimeTextView = (TextView) findViewById(R.id.edit_shift_end_time_text);
        if (isNew){
            endTimeButton.setText("SET TIME");
        }else{
            endTimeButton.setText("EDIT TIME");
            endTimeTextView.setText("00:00 AM");
        }

        endTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = TimePickerFragment.newInstance("edit_shift_end_time_text");
                newFragment.show(getFragmentManager(),"TimePicker");
            }
        });

        pickColorButton = (Button) findViewById(R.id.edit_shift_color_button);
        pickColorButton.setBackgroundColor(Color.parseColor(shiftColor));
        pickColorButton.setText(shiftColor);
        pickColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickColorPickerDialog();
            }
        });


    }

    public void onClickColorPickerDialog() {

        // The color picker menu item has been clicked. Show
        // a dialog using the custom ColorPickerDialogFragment class.

        ColorPickerDialogFragment f = ColorPickerDialogFragment
                .newInstance(DIALOG_ID, null, null, Color.BLACK, true);

        f.setStyle(DialogFragment.STYLE_NORMAL, R.style.LightPickerDialogTheme);
        f.show(getFragmentManager(), "d");

    }

    @Override
    public void onColorSelected(int dialogId, int color) {
        switch (dialogId) {
            case DIALOG_ID:
                // We got result from the other dialog, the one that is
                // shown when clicking on the icon in the action bar.
                pickColorButton.setBackgroundColor(color);
                pickColorButton.setText(String.format("#%06X", 0xFFFFFF & color));
                break;
        }
    }

    @Override
    public void onDialogDismissed(int dialogId) {

    }
}
