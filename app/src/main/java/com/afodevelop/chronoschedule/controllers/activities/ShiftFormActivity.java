package com.afodevelop.chronoschedule.controllers.activities;

import android.app.AlarmManager;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afodevelop.chronoschedule.R;
import com.afodevelop.chronoschedule.controllers.fragments.TimePickerFragment;
import com.afodevelop.chronoschedule.controllers.mysqlControllers.MySQLAssistant;
import com.afodevelop.chronoschedule.controllers.sqliteControllers.SQLiteAssistant;
import com.afodevelop.chronoschedule.model.JdbcException;
import com.afodevelop.chronoschedule.model.SQLiteException;
import com.afodevelop.chronoschedule.model.Shift;
import com.afodevelop.chronoschedule.model.User;
import com.github.danielnilsson9.colorpickerview.dialog.ColorPickerDialogFragment;
import com.github.danielnilsson9.colorpickerview.dialog.ColorPickerDialogFragment.ColorPickerDialogListener;

import java.sql.SQLException;

public class ShiftFormActivity extends AppCompatActivity implements ColorPickerDialogListener{

    // COSTANTS
    private static final int DIALOG_ID = 0;

    // INTERNAL CLASS DEFINITIONS
    /**
     * This class is a broadcast reciver. It handles what happens as our
     * activity receives periodically an alarm event: It will trigger a
     * connectivity check!
     */
    private class JdbcStatusUpdateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            CheckConnectivityTask checkConnectivity = new CheckConnectivityTask();
            checkConnectivity.execute();
            updateButton(connectivity);
            Log.d("onReceive", "Updating conectivity!");
        }
    }

    /**
     * This class is an AsyncTask based task that performs a connectivity check.
     */
    private class CheckConnectivityTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Log.d("inBackgroud", "call checkConnectivity");
            connectivity = checkConnectivity();
            Log.d("inBackgroud", "END, returning");
            return null;
        }
    }

    /**
     * This class is an AsyncTask based task that performs a user data update.
     */
    private class UpdateShiftTask extends AsyncTask<Void, Void, Void>{

        private Exception exceptionToBeThrown;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                mySQLAssistant.updateShift(shift);
            } catch (SQLException | JdbcException | ClassNotFoundException e) {
                exceptionToBeThrown = e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (exceptionToBeThrown == null){
                printToast("User data successfully updated!");
            } else {
                printToast("Error occurred while updating user in DB");
            }
        }
    }

    /**
     * This class is an AsyncTask based task that performs a user data update.
     */
    private class CreateShiftTask extends AsyncTask<Void, Void, Void>{

        private Exception exceptionToBeThrown;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                mySQLAssistant.insertShift(shift);
            } catch (SQLException | JdbcException | ClassNotFoundException e) {
                exceptionToBeThrown = e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (exceptionToBeThrown == null){
                printToast("User successfully created!");
            } else {
                printToast("Error occurred while created user in DB");
            }
        }
    }

    // CLASSWIDE VARIABLES
    private SQLiteAssistant sqLiteAssistant;
    private MySQLAssistant mySQLAssistant;
    private AlarmManager alarmManager;
    private PendingIntent alarmPendingIntent;
    private JdbcStatusUpdateReceiver jdbcStatusUpdateReceiver;

    private Button startTimeButton, endTimeButton, pickColorButton;
    private TextView startTimeTextView, endTimeTextView;
    private EditText shiftNameTextEdit;
    private FloatingActionButton saveButton;

    private String mode;
    private Shift shift;
    private boolean connectivity;
    private boolean checkConnectivity;
    private boolean isUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shift_form);

        mySQLAssistant = MySQLAssistant.getInstance();
        sqLiteAssistant = SQLiteAssistant.getInstance();

        mode = getIntent().getExtras().getString("mode");
        switch (mode){
            case "new":
                newModeLogic();
                break;
            case "edit":
                editModeLogic();
                break;
        }
    }

    /**
     * Handle here logic for new shift activity logic flow
     */
    private void newModeLogic(){
        setTitle("Create Shift");
        isUpdate = false;
        checkConnectivity = true;
        CheckConnectivityTask checkConnectivity = new CheckConnectivityTask();
        checkConnectivity.execute();
        initializeConnectivityWatchDog();
        renderUI();
        shiftNameTextEdit.setText("");
        startTimeButton.setText("SET TIME");
        startTimeTextView.setText("--:-- XX");
        endTimeButton.setText("SET TIME");
        endTimeTextView.setText("--:-- XX");
        pickColorButton.setBackgroundColor(Color.parseColor("#888888"));
        pickColorButton.setText("#888888");
    }

    /**
     * Handle here logic for edit shift activity logic flow
     */
    private void editModeLogic(){
        setTitle("Edit User");
        isUpdate = true;
        checkConnectivity = true;
        CheckConnectivityTask checkConnectivity = new CheckConnectivityTask();
        checkConnectivity.execute();
        initializeConnectivityWatchDog();
        renderUI();
        try {
            shift = sqLiteAssistant.getShiftById(getIntent().getExtras().getInt("shiftId"));
            shiftNameTextEdit.setText(shift.getName());
            startTimeButton.setText("EDIT TIME");
            startTimeTextView.setText(shift.getStartTime());
            endTimeButton.setText("EDIT TIME");
            endTimeTextView.setText(shift.getEndTime());
            pickColorButton.setBackgroundColor(Color.parseColor("#" + shift.getColor()));
            pickColorButton.setText("#" + shift.getColor());
        } catch (SQLiteException e) {
            printToast("Error accessing DB.");
            e.printStackTrace();
            finish();
        }
    }

    /**
     * Instantiate all dynamically altered Views.
     */
    private void renderUI(){

        shiftNameTextEdit = (EditText) findViewById(R.id.edit_shift_edittext_name);
        shiftNameTextEdit.setVisibility(View.VISIBLE);

        startTimeButton = (Button) findViewById(R.id.edit_shift_start_time_button);
        startTimeTextView = (TextView) findViewById(R.id.edit_shift_start_time_text);
        startTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = TimePickerFragment.newInstance("edit_shift_start_time_text");
                newFragment.show(getFragmentManager(), "TimePicker");
            }
        });

        endTimeButton = (Button) findViewById(R.id.edit_shift_end_time_button);
        endTimeTextView = (TextView) findViewById(R.id.edit_shift_end_time_text);
        endTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = TimePickerFragment.newInstance("edit_shift_end_time_text");
                newFragment.show(getFragmentManager(), "TimePicker");
            }
        });

        pickColorButton = (Button) findViewById(R.id.edit_shift_color_button);
        pickColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickColorPickerDialog();
            }
        });

        saveButton = (FloatingActionButton) findViewById(R.id.edit_user_savebutton);
        updateButton(true);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveShift();
            }
        });
    }

    /**
     * Opens a dialog with a color picker to choose a color
     */
    public void onClickColorPickerDialog() {

        // The color picker menu item has been clicked. Show
        // a dialog using the custom ColorPickerDialogFragment class.

        ColorPickerDialogFragment f = ColorPickerDialogFragment
                .newInstance(DIALOG_ID, null, null, Color.BLACK, true);

        f.setStyle(DialogFragment.STYLE_NORMAL, R.style.LightPickerDialogTheme);
        f.show(getFragmentManager(), "d");

    }

    /**
     * This method catches a user color picker selection and captures the
     * selected color value
     * @param dialogId This param identifies the dialog instance
     * @param color The color actually selectedd by user
     */
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

    /**
     * A mandatory override for Dialog
     * @param dialogId
     */
    @Override
    public void onDialogDismissed(int dialogId) {

    }

    /**
     * This method enables to dynamically enable/show disable/hide the save button
     * So, basing on connectivity status, the button is controlled.
     * @param enable
     */
    private void updateButton(boolean enable){
        if (enable){
            saveButton.setVisibility(View.VISIBLE);
            saveButton.setClickable(true);
            saveButton.setEnabled(true);
        } else {
            saveButton.setVisibility(View.GONE);
            saveButton.setClickable(false);
            saveButton.setEnabled(false);
        }
    }

    /**
     * This method triggers the final sentences against DB to persist the new user
     * Or to persist current user changes.
     */
    private void saveShift(){
        if (isUpdate){
            shift.setName(shiftNameTextEdit.getText().toString());
            shift.setStartTime(startTimeTextView.getText().toString());
            shift.setEndTime(endTimeTextView.getText().toString());
            shift.setColor(pickColorButton.getText().toString());
            UpdateShiftTask updateShiftTask = new UpdateShiftTask();
            updateShiftTask.execute();
        } else {
            shift = new Shift(0,
                    shiftNameTextEdit.getText().toString(),
                    startTimeTextView.getText().toString(),
                    endTimeTextView.getText().toString(),
                    pickColorButton.getText().toString());
            CreateShiftTask createShiftTask = new CreateShiftTask();
            createShiftTask.execute();
        }
        finish();
    }

    /**
     * This method actually asks out MySQL JDBC assistant to check for
     * available connectivity.
     * @return
     */
    private boolean checkConnectivity(){
        try {
            Log.d("checkConnectivity","check connectivity");
            return mySQLAssistant.checkConnectivity();
        } catch (JdbcException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * This method is responsable of instantiate, initialize and start both an
     * AlrManager driven periodic broadcasting event, and an broadcast listener that,
     * on receiving the advise, trigges connectivity status check.
     */
    private void initializeConnectivityWatchDog(){
        Intent intent = new Intent("com.afodevelop.chronoschedule.MY_TIMER");
        alarmPendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        long now = System.currentTimeMillis();
        long interval = 1 * 60 * 1000;
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, now + interval, interval,
                alarmPendingIntent);

        IntentFilter filter = new IntentFilter("com.afodevelop.chronoschedule.MY_TIMER");
        jdbcStatusUpdateReceiver = new JdbcStatusUpdateReceiver();
        registerReceiver(jdbcStatusUpdateReceiver, filter);
    }

    /**
     * Handle AlarmDeactivation and Broadcast receiver de-activation before leaving
     */
    @Override
    protected void onStop() {
        super.onStop();
        if (checkConnectivity) {
            unregisterReceiver(jdbcStatusUpdateReceiver);
            alarmManager.cancel(alarmPendingIntent);
        }
    }


    /**
     * An auxiliar method to ease Toast printing
     * @param s
     */
    private void printToast(String s){
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

}
