package com.afodevelop.chronoschedule.controllers.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afodevelop.chronoschedule.R;
import com.afodevelop.chronoschedule.controllers.mysqlControllers.MySQLAssistant;
import com.afodevelop.chronoschedule.controllers.sqliteControllers.SQLiteAssistant;
import com.afodevelop.chronoschedule.model.JdbcException;
import com.afodevelop.chronoschedule.model.SQLiteException;
import com.afodevelop.chronoschedule.model.User;

import java.sql.SQLException;

/**
 * This class holds a form to either create, edit or show User data information.
 * It acts basically as a form holder.
 *
 * @author Alejandro Olivan Alvarez
 */
public class UserFormActivity extends AppCompatActivity {

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
    private class UpdateUserTask extends AsyncTask<Void, Void, Void>{

        private Exception exceptionToBeThrown;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                mySQLAssistant.updateUser(user);
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
     *
     * @author Alejandro Olivan Alvarez
     */
    private class CreateUserTask extends AsyncTask<Void, Void, Void>{

        private Exception exceptionToBeThrown;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                mySQLAssistant.insertUser(user);
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

    // CLASS-WIDE VARIABLES
    private SQLiteAssistant sqLiteAssistant;
    private MySQLAssistant mySQLAssistant;
    private JdbcStatusUpdateReceiver jdbcStatusUpdateReceiver;
    private TextView userNameText;
    private EditText userNameEditText;
    private TextView passwordText;
    private EditText passwordEditText;
    private TextView dniText;
    private EditText dniEditText;
    private TextView fullNameText;
    private EditText fullNameEditText;
    private CheckBox adminCheckBox;
    private LinearLayout edit_user_labellayout_admin;
    private LinearLayout edit_user_checkboxlayout_admin;
    private FloatingActionButton saveButton;
    private String mode;
    private User user;
    private boolean connectivity;
    private boolean checkConnectivity;
    private boolean isUpdate;

    // LOGIC
    /**
     * The onCreate method override is fairly simple, it initializes classwide variables
     * and switches logic flow upon Intent data evaluation
     *
     * @author Alejandro Olivan Alvarez
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_form);

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
            case "show":
                showModeLogic();
                break;
        }
    }

    /**
     * Handle here logic for new user activity logic flow
     *
     * @author Alejandro Olivan Alvarez
     */
    private void newModeLogic(){
        setTitle("Create User");
        isUpdate = false;
        checkConnectivity = true;
        CheckConnectivityTask checkConnectivity = new CheckConnectivityTask();
        checkConnectivity.execute();
        initializeConnectivityWatchDog();
        renderUI(true, true, true);
        userNameEditText.setText("");
        passwordEditText.setText("");
        dniEditText.setText("");
        fullNameEditText.setText("");
        adminCheckBox.setChecked(false);
    }

    /**
     * Handle here logic for edit user activity logic flow
     *
     * @author Alejandro Olivan Alvarez
     */
    private void editModeLogic(){
        setTitle("Edit User");
        isUpdate = true;
        checkConnectivity = true;
        CheckConnectivityTask checkConnectivity = new CheckConnectivityTask();
        checkConnectivity.execute();
        initializeConnectivityWatchDog();
        renderUI(false, true, true);
        try {
            user = sqLiteAssistant.getUserByUserName(getIntent().getExtras().getString("user"));
            userNameText.setText(user.getUserName());
            passwordEditText.setText(user.getPass());
            dniEditText.setText(user.getDniUser());
            fullNameEditText.setText(user.getRealName());
            adminCheckBox.setChecked(user.isAdmin());
        } catch (SQLiteException e) {
            printToast("Error accessing DB.");
            e.printStackTrace();
            finish();
        }
    }

    /**
     * Handle here logic for show user details activity logic flow
     *
     * @author Alejandro Olivan Alvarez
     */
    private void showModeLogic(){
        setTitle("User Details");
        checkConnectivity = false;
        renderUI(false, false, false);
        try {
            user = sqLiteAssistant.getUserByUserName(getIntent().getExtras().getString("user"));
            userNameText.setText(user.getUserName());
            passwordText.setText(user.getPass());
            dniText.setText(user.getDniUser());
            fullNameText.setText(user.getRealName());
        } catch (SQLiteException e) {
            printToast("Error accessing DB.");
            e.printStackTrace();
            finish();
        }
    }

    /**
     * Instantiate all dynamically altered Views. The method receives a boolean to
     * indicate either userName editable or not.
     *
     * @author Alejandro Olivan Alvarez
     * @param userNameEditable a boolean to decide if the UI shows editable userName
     *                         or not
     */
    private void renderUI(boolean userNameEditable, boolean dataEditable, boolean saveable){
        if (userNameEditable) {
            userNameEditText = (EditText) findViewById(R.id.edit_user_username_edittext);
            userNameEditText.setVisibility(View.VISIBLE);
        } else {
            userNameText = (TextView) findViewById(R.id.edit_user_username_text);
            userNameText.setVisibility(View.VISIBLE);
        }
        if (dataEditable) {
            passwordEditText = (EditText) findViewById(R.id.edit_user_password_edittext);
            passwordEditText.setVisibility(View.VISIBLE);
            dniEditText = (EditText) findViewById(R.id.edit_user_dni_edittext);
            dniEditText.setVisibility(View.VISIBLE);
            fullNameEditText = (EditText) findViewById(R.id.edit_user_fullname_edittext);
            fullNameEditText.setVisibility(View.VISIBLE);
            edit_user_labellayout_admin = (LinearLayout) findViewById(R.id.edit_user_labellayout_admin);
            edit_user_labellayout_admin.setVisibility(View.VISIBLE);
            edit_user_checkboxlayout_admin = (LinearLayout) findViewById(R.id.edit_user_checkboxlayout_admin);
            edit_user_checkboxlayout_admin.setVisibility(View.VISIBLE);
            adminCheckBox = (CheckBox) findViewById(R.id.edit_user_Admin_checkbox);
            adminCheckBox.setVisibility(View.VISIBLE);
        } else {
            passwordText = (TextView) findViewById(R.id.edit_user_password_text);
            passwordText.setVisibility(View.VISIBLE);
            dniText = (TextView) findViewById(R.id.edit_user_dni_text);
            dniText.setVisibility(View.VISIBLE);
            fullNameText = (TextView) findViewById(R.id.edit_user_fullname_text);
            fullNameText.setVisibility(View.VISIBLE);
        }
        if (saveable) {
            saveButton = (FloatingActionButton) findViewById(R.id.edit_user_savebutton);
            updateButton(true);
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveUser();
                }
            });
        } else {
            saveButton = (FloatingActionButton) findViewById(R.id.edit_user_savebutton);
            updateButton(false);
        }
    }

    /**
     * This method enables to dynamically enable/show disable/hide the save button
     * So, basing on connectivity status, the button is controlled.
     *
     * @author Alejandro Olivan Alvarez
     * @param enable a boolean stating whether the fab button has to be rendered
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
     *
     * @author Alejandro Olivan Alvarez
     */
    private void saveUser(){
        if (isUpdate){
            user.setRealName(fullNameEditText.getText().toString());
            user.setDniUser(dniEditText.getText().toString());
            user.setPass(passwordEditText.getText().toString());
            user.setAdmin(adminCheckBox.isChecked());
            UpdateUserTask updateUserTask = new UpdateUserTask();
            updateUserTask.execute();
        } else {
            user = new User(0,
                    adminCheckBox.isChecked(),
                    dniEditText.getText().toString(),
                    userNameEditText.getText().toString(),
                    fullNameEditText.getText().toString(),
                    passwordEditText.getText().toString());
            CreateUserTask createUserTask = new CreateUserTask();
            createUserTask.execute();
        }
        finish();
    }

    /**
     * This method actually asks out MySQL JDBC assistant to check for
     * available connectivity.
     *
     * @author Alejandro Olivan Alvarez
     * @return a boolean evaluating connectivity up (true) or down (false)
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
        IntentFilter filter = new IntentFilter("com.afodevelop.chronoschedule.MY_TIMER");
        jdbcStatusUpdateReceiver = new JdbcStatusUpdateReceiver();
        registerReceiver(jdbcStatusUpdateReceiver, filter);
    }

    /**
     * Handle AlarmDeactivation and Broadcast receiver de-activation before leaving
     *
     * @author Alejandro Olivan Alvarez
     */
    @Override
    protected void onStop() {
        super.onStop();
        if (checkConnectivity) {
            unregisterReceiver(jdbcStatusUpdateReceiver);
        }
    }

    /**
     * An auxiliar method to ease Toast printing
     *
     * @author Alejandro Olivan Alvarez
     * @param s the string text we want printed on the toast
     */
    private void printToast(String s){
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
}
