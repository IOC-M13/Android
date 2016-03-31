package com.afodevelop.chronoschedule.controllers.activities;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afodevelop.chronoschedule.R;
import com.afodevelop.chronoschedule.controllers.mysqlControllers.MySQLAssistant;
import com.afodevelop.chronoschedule.controllers.sqliteControllers.SQLiteAssistant;
import com.afodevelop.chronoschedule.model.JdbcException;
import com.afodevelop.chronoschedule.model.SQLiteException;
import com.afodevelop.chronoschedule.model.Shift;
import com.afodevelop.chronoschedule.model.User;
import com.afodevelop.chronoschedule.model.UserShift;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * An activity class that handles the login process
 *
 * @author Alejandro Olivan Alvarez
 */
public class DayFormActivity extends AppCompatActivity {

    // INTERNAL CLASS DEFINITIONS
    /**
     * This class is a broadcast reciver. It handles what happens as our
     * activity receives periodically an alarm event: It will trigger a
     * connectivity check!
     *
     * @author Alejandro Olivan Alvarez
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
     *
     * @author Alejandro Olivan Alvarez
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
     *
     * @author Alejandro Olivan Alvarez
     */
    private class UpdateUserShiftTask extends AsyncTask<Void, Void, Void>{

        private Exception exceptionToBeThrown;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                mySQLAssistant.updateUserShift(userShift);
            } catch (SQLException | JdbcException | ClassNotFoundException e) {
                exceptionToBeThrown = e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (exceptionToBeThrown == null){
                printToast("User calendar successfully updated!");
            } else {
                exceptionToBeThrown.printStackTrace();
                printToast("Error occurred while updating user in DB");
            }
        }
    }

    /**
     * This class is an AsyncTask based task that performs a user data update.
     */
    private class CreateUserShiftTask extends AsyncTask<Void, Void, Void>{

        private Exception exceptionToBeThrown;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                mySQLAssistant.insertUserShift(userShift);
            } catch (SQLException | JdbcException | ClassNotFoundException e) {
                exceptionToBeThrown = e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (exceptionToBeThrown == null){
                printToast("User calendar successfully updated!");
            } else {
                printToast("Error occurred while created user in DB");
            }
        }
    }

    /**
     * This class is an AsyncTask based task that performs a user data update.
     *
     * @author Alejandro Olivan Alvarez
     */
    private class DeleteUserShiftTask extends AsyncTask<Void, Void, Void>{

        private Exception exceptionToBeThrown;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                mySQLAssistant.deleteUserShift(userShift);
            } catch (SQLException | JdbcException | ClassNotFoundException e) {
                exceptionToBeThrown = e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (exceptionToBeThrown == null){
                printToast("User calendar successfully updated!");
            } else {
                printToast("Error occurred while created user in DB");
            }
        }
    }

    // CLASSWIDE VARIABLES
    private SQLiteAssistant sqLiteAssistant;
    private MySQLAssistant mySQLAssistant;
    private JdbcStatusUpdateReceiver jdbcStatusUpdateReceiver;
    private TextView dateTextView, userTextView;
    private TextView spinnerLabelTextView, startTimeTextView, endTimeTextView;
    private Spinner chooseUserSpinner;
    private LinearLayout startTimeLayout, endTimeLayout;
    private FloatingActionButton saveButton;
    private DateFormat df;
    private Date date;
    private String mode, strDate;
    private User user;
    private UserShift userShift;
    private ArrayList<Shift> availableShifts;
    private String[] strAvailableShifts;
    private boolean connectivity;
    private boolean checkConnectivity;
    private boolean isUpdate, isDelete;
    private int newShiftId;


    // LOGIC
    /**
     * The overriden onCreate method
     *
     * @author Alejandro Olivan Alvarez
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_form);
        Log.d("DayFormActivity", "STARTING HERE!");

        mySQLAssistant = MySQLAssistant.getInstance();
        sqLiteAssistant = SQLiteAssistant.getInstance();
        try {
            availableShifts = sqLiteAssistant.getAllShifts();
            strAvailableShifts = sqLiteAssistant.getShiftNames();
            String[] tmp = new String[strAvailableShifts.length + 1];
            tmp[0] = "None";
            for (int i = 1 ; i < tmp.length ; i++){
                tmp[i] = strAvailableShifts[i-1];
            }
            strAvailableShifts = tmp;
        } catch (SQLiteException e) {
            printToast("Error accessing DB.");
            e.printStackTrace();
            finish();
        }
        df = new SimpleDateFormat("yyyy-MM-dd");

        mode = getIntent().getExtras().getString("mode");
        strDate = getIntent().getExtras().getString("date");
        switch (mode){
            case "new":
                newModeLogic();
                break;
            case "edit":
                editModeLogic();
                break;
            case "show":
                showModeLogic();
                break;
        }
    }

    /**
     * A method that prepares the class environment to act as a "new/creation" mode.
     *
     * @author Alejandro Olivan Alvarez
     */
    private void newModeLogic(){
        setTitle("Add day shift");
        isUpdate = false;
        checkConnectivity = true;
        CheckConnectivityTask checkConnectivity = new CheckConnectivityTask();
        checkConnectivity.execute();
        initializeConnectivityWatchDog();
        renderUI(true, true);
        try {
            user = sqLiteAssistant.getUserByUserName(getIntent().getExtras().getString("user"));
            date = df.parse(strDate);
            dateTextView.setText(strDate);
            userTextView.setText(user.getRealName());
            spinnerLabelTextView.setText("Select shift:");
            spinnerLabelTextView.setTextSize(12);
            chooseUserSpinner.setSelection(0);
            chooseUserSpinner.setBackgroundColor(Color.parseColor("#CCCCCC"));
            startTimeTextView.setText("--:--:--");
            endTimeTextView.setText("--:--:--");
        } catch (SQLiteException | ParseException e) {
            printToast("Error accessing DB.");
            e.printStackTrace();
            finish();
        }
    }

    /**
     * A method that prepares the class environemnt to act as "edit" mode.
     *
     * @author Alejandro Olivan Alvarez
     */
    private void editModeLogic(){
        setTitle("Edit day shift");
        isUpdate = true;
        checkConnectivity = true;
        CheckConnectivityTask checkConnectivity = new CheckConnectivityTask();
        checkConnectivity.execute();
        initializeConnectivityWatchDog();
        renderUI(true, true);
        try {
            user = sqLiteAssistant.getUserByUserName(getIntent().getExtras().getString("user"));
            date = df.parse(strDate);
            userShift = sqLiteAssistant.getUserShift(user, date);
            dateTextView.setText(strDate);
            userTextView.setText(user.getRealName());
            spinnerLabelTextView.setText("Select shift:");
            spinnerLabelTextView.setTextSize(12);
            chooseUserSpinner.setSelection(userShift.getShift().getIdShift());
            startTimeTextView.setText(userShift.getShift().getStartTime());
            endTimeTextView.setText(userShift.getShift().getEndTime());
        } catch (SQLiteException | ParseException e) {
            printToast("Error accessing DB.");
            e.printStackTrace();
            finish();
        }
    }

    /**
     * A method that sets the class environment for just "show" mode.
     *
     * @author Alejandro Olivan Alvarez
     */
    private void showModeLogic(){
        setTitle("Day shift");
        checkConnectivity = false;
        renderUI(false, false);
        try {
            user = sqLiteAssistant.getUserByUserName(getIntent().getExtras().getString("user"));
            date = df.parse(strDate);
            userShift = sqLiteAssistant.getUserShift(user, date);
            dateTextView.setText(strDate);
            userTextView.setText(user.getRealName());
            spinnerLabelTextView.setText("Assigned Shift: " + userShift.getShift().getName());
            spinnerLabelTextView.setTextSize(18);
            spinnerLabelTextView.setBackgroundColor(
                    Color.parseColor("#" + userShift.getShift().getColor()));
            startTimeTextView.setText(userShift.getShift().getStartTime());
            endTimeTextView.setText(userShift.getShift().getEndTime());
        } catch (SQLiteException | ParseException e) {
            printToast("Error accessing DB.");
            e.printStackTrace();
            finish();
        }
    }

    /**
     * This method renders all UI artifacts taking into account envirnment variables
     * previously set up, and also a pair of conditionals received as boolean
     * parameters
     *
     * @author Alejandro Olivan Alvarez
     * @param shiftEditable A boolean to set if shifts spinner is rendered
     * @param saveable A boolean to set if save button is rendered
     */
    private void renderUI(boolean shiftEditable, boolean saveable){
        dateTextView = (TextView) findViewById(R.id.edit_shiftday_text_date);
        userTextView = (TextView) findViewById(R.id.edit_shiftday_text_user);

        spinnerLabelTextView = (TextView) findViewById(R.id.edit_shiftday_spinner_label);

        if(shiftEditable){
            chooseUserSpinner = (Spinner) findViewById(R.id.edit_shiftday_spinner);
            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, strAvailableShifts);
            // Specify the layout to use when the list of choices appears
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            chooseUserSpinner.setAdapter(spinnerAdapter);
            chooseUserSpinner.setVisibility(View.VISIBLE);
            chooseUserSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position == 0){
                        isDelete = true;
                        chooseUserSpinner.setBackgroundColor(Color.parseColor("#CCCCCC"));
                        startTimeTextView.setText("--:--:--");
                        endTimeTextView.setText("--:--:--");
                    } else {
                        isDelete = false;
                        newShiftId = position;
                        try {
                            startTimeTextView.setText(sqLiteAssistant.getShiftById(newShiftId).
                                    getStartTime());
                            endTimeTextView.setText(sqLiteAssistant.getShiftById(newShiftId).
                                    getEndTime());
                            chooseUserSpinner.setBackgroundColor(Color.parseColor(
                                    "#" + sqLiteAssistant.getShiftById(newShiftId).getColor()));
                        } catch (SQLiteException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

        startTimeLayout = (LinearLayout) findViewById(R.id.edit_shiftday_startime_layout);
        startTimeTextView = (TextView) findViewById(R.id.edit_shiftday_text_starttime);

        endTimeLayout = (LinearLayout) findViewById(R.id.edit_shiftday_endtime_layout);
        endTimeTextView = (TextView) findViewById(R.id.edit_shiftday_text_endtime);

        if(saveable){
            saveButton = (FloatingActionButton) findViewById(R.id.confirm_dayshift_button);
            updateButton(true);
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveUserShift();
                }
            });
        }else{
            saveButton = (FloatingActionButton) findViewById(R.id.confirm_dayshift_button);
            updateButton(false);
        }
    }

    /**
     * This method actually asks out MySQL JDBC assistant to check for
     * available connectivity.
     *
     * @author Alejandro Olivan Alvarez
     * @return a boolean with ether true (coonectivity OK) or false
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
     *
     * @author Alejandro Olivan Alvarez
     */
    private void initializeConnectivityWatchDog(){
        Log.d("DayFormActivity","Initialize connectivity watchdog.");

        IntentFilter filter = new IntentFilter("com.afodevelop.chronoschedule.MY_TIMER");
        jdbcStatusUpdateReceiver = new JdbcStatusUpdateReceiver();
        registerReceiver(jdbcStatusUpdateReceiver, filter);
    }

    /**
     * This method enables to dynamically enable/show disable/hide the save button
     * So, basing on connectivity status, the button is controlled.
     *
     * @author Alejandro Olivan Alvarez
     * @param enable a boolean tho control enabled or not
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
     * This method handles what happens when user clicks the save button.
     * It will evaluate the flow conditions to perform the correct CRUD action
     * against DB or do nothing if unnecessary. It holds the finish method call.
     *
     * @author Alejandro Olivan Alvarez
     */
    private void saveUserShift(){
        if(isUpdate){
            if (isDelete){
                Log.d("DayFormActivity","Tryng to insert a new UserShift");
                DeleteUserShiftTask deleteUserShiftTask = new DeleteUserShiftTask();
                deleteUserShiftTask.execute();
                finish();
            } else {
                Log.d("DayFormActivity","Trying to update the UserShift");
                try {
                    userShift.setShift(sqLiteAssistant.getShiftById(newShiftId));
                    UpdateUserShiftTask updateUserShiftTask = new UpdateUserShiftTask();
                    updateUserShiftTask.execute();
                } catch (SQLiteException e) {
                    e.printStackTrace();
                }
                finish();
            }
        } else {
            if(isDelete){
                Log.d("DayFormActivity","Nothing to do... finishing");
                finish();
            } else {
                Log.d("DayFormActivity","Trying to Insert new UserShift");
                try {
                    userShift = new UserShift(user, sqLiteAssistant.getShiftById(newShiftId),
                            new java.sql.Date(date.getTime()));
                    CreateUserShiftTask createUserShiftTask = new CreateUserShiftTask();
                    createUserShiftTask.execute();
                } catch (SQLiteException e) {
                    e.printStackTrace();
                }
                finish();
            }
        }
    }

    /**
     * Handle AlarmDeactivation and Broadcast receiver de-activation before leaving
     *
     * @author Alejandro Olivan Alvarez
     */
    @Override
    protected void onStop() {
        super.onStop();
        Log.d("dayformactivity","stopping...");
        if (checkConnectivity) {
            Log.d("dayformactivity","disabling connectivity watchdog");
            unregisterReceiver(jdbcStatusUpdateReceiver);
        }
    }

    /**
     * An auxiliar method to ease Toast printing
     *
     * @author Alejandro Olivan Alvarez
     * @param s the stream to be printed inside the toast
     */
    private void printToast(String s){
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
}
