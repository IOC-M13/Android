package com.afodevelop.chronoschedule.controllers.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afodevelop.chronoschedule.R;
import com.afodevelop.chronoschedule.model.JdbcException;
import com.afodevelop.chronoschedule.controllers.mysqlControllers.MySQLAssistant;
import com.afodevelop.chronoschedule.controllers.mysqlControllers.MySQLConnectorFactory;
import com.afodevelop.chronoschedule.controllers.ormControllers.ORMAssistant;
import com.afodevelop.chronoschedule.model.OrmException;
import com.afodevelop.chronoschedule.controllers.sqliteControllers.SQLiteAssistant;
import com.afodevelop.chronoschedule.model.SQLiteException;
import com.afodevelop.chronoschedule.model.User;

import java.sql.SQLException;

/**
 * A login screen that offers login via username/password.
 *
 * @author Alejandro Olivan Alvarez
 */
public class LoginActivity extends AppCompatActivity {


    // CONSTANTS
    private static final int APP_CALL_ID = 0;
    private static final String SP_NAME = "Preferences";
    private static final String SP_KEY_DBHOST = "dbHost";
    private static final String SP_KEY_DBPORT = "dbPort";


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

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            renderUI();
        }
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     *
     * @author Alejandro Olivan Alvarez
     */
    private class InitializationTask extends AsyncTask<Void, Void, Void> {

        private Exception exceptionToBeThrown;
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute(){
            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setIndeterminate(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("Setting up environment... please wait.");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.d("inBackgroud","Iitialize MySQL");
            try {
                initializeMySQL();
            } catch (JdbcException e) {
                exceptionToBeThrown = e;
            }
            Log.d("inBackgroud", "Iitialize SQLite");
            try {
                initializeSQLite();
            } catch (SQLiteException e) {
                exceptionToBeThrown = e;
            }
            Log.d("inBackground", "Initialize ORM");
            try {
                initializeORM();
            } catch (OrmException e) {
                exceptionToBeThrown = e;
            }
            Log.d("inBackground", "Initialize JDBC Connectivity Watch Dog");
            initializeConnectivityWatchDog();
            Log.d("inBackgroud", "call checkConnectivity");
            connectivity = checkConnectivity();
            Log.d("inBackgroud", "END, returning");
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            if (exceptionToBeThrown == null) {
                Log.d("PostExecute", "calling logic exec method.");
                mainLogic();
            } else {
                printToast("Application environment initialization error.");
            }
        }
    }


    /**
     * This tasks perform a background process of syncing remote mysql database
     * with local, off-line possible, native, sqlite db.
     *
     * @author Alejandro Olivan Alvarez
     */
    private class ResyncTask extends AsyncTask<Void, Void, Void> {

        private Exception exceptionToBeThrown;
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute(){
            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setIndeterminate(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("Updating local database... please wait.");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.d("inBackgroud","try to resync Databases.");
            try {
                Thread.sleep(100);
                ormAssistant.launchResync();
            } catch (OrmException e) {
                exceptionToBeThrown = e;
            } catch (SQLiteException e) {
                exceptionToBeThrown = e;
            } catch (JdbcException e) {
                exceptionToBeThrown = e;
            } catch (SQLException e) {
                exceptionToBeThrown = e;
            } catch (ClassNotFoundException e) {
                exceptionToBeThrown = e;
            } catch (InterruptedException e) {
                exceptionToBeThrown = e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            if (exceptionToBeThrown == null) {
                Log.d("PostExecute", "rendering UI.");
                renderUI();
            } else {
                printToast("Database sync error.");
            }
        }
    }


    // CLASS-WIDE VARIABLES
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private boolean connectivity, firstExecution;
    private boolean uiRendered = false;
    private String dbHost, dbPort;
    private MySQLConnectorFactory mySQLConnectorFactory;
    private MySQLAssistant mySQLAssistant;
    private SQLiteAssistant sqLiteAssistant;
    private ORMAssistant ormAssistant;
    private AlarmManager alarmManager;
    private PendingIntent alarmPendingIntent;
    private JdbcStatusUpdateReceiver jdbcStatusUpdateReceiver;

    private Menu appMenu;
    private EditText mUsernameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private Button mUsernameSignInButton;


    // LOGIC

    /**
     * The onCreate method contains all initialization logic
     *
     * @author Alejandro Olivan Alvarez
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d("onCreate","APP STARTS HERE!!!!");

        // Initialize the shared preferences
        SharedPreferences lastState = getSharedPreferences(SP_NAME, MODE_PRIVATE);
        dbHost = lastState.getString(SP_KEY_DBHOST, "");
        dbPort = lastState.getString(SP_KEY_DBPORT, "");

        // Check Shared Preferences
        if (dbHost.equalsIgnoreCase("")) {
            Log.d("onCreate","first execution = true");
            firstExecution = true;

        } else {
            Log.d("onCreate","first execution = false");
            firstExecution = false;
        }

        renderUI();
    }

    /**
     * Here we got a reference to the action bar menu and its Items
     *
     * @author Alejandro Olivan Alvarez
     * @param menu the menu Item instance clicked by user
     * @return a fixed true value
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.login_menu, menu);
        appMenu = menu;

        if (uiRendered) {
            if (!connectivity) {
                Log.d("renderUI", "Rerendering to OFF-LINE mode menu");
                appMenu.findItem(R.id.login_refresh_menuitem).setIcon(R.drawable.stat_notify_sync_error);
                appMenu.findItem(R.id.login_refresh_menuitem).setEnabled(false);
            } else {
                Log.d("renderUI", "Rerendering to ON-LINE mode menu");
                appMenu.findItem(R.id.login_refresh_menuitem).setIcon(R.drawable.stat_notify_sync_anim0);
                appMenu.findItem(R.id.login_refresh_menuitem).setEnabled(true);
            }
        }
        return true;
    }

    /**
     * we override the onResume method to prompt for connectivity check
     *
     * @author Alejandro Olivan Alvarez
     */
    @Override
    protected void onResume() {
        super.onResume();
        if(!firstExecution) {
            InitializationTask mySQLInitializationTask = new InitializationTask();
            mySQLInitializationTask.execute();
        } else {
            launchSettings();
        }
    }

    /**
     * Initialize our MySQL conection assistant
     *
     * @author Alejandro Olivan Alvarez
     * @throws JdbcException
     */
    private void initializeMySQL() throws JdbcException {
        Log.d("initializeMySQL", "asked to initialize MySQL");
        mySQLConnectorFactory = new MySQLConnectorFactory(
                dbHost, dbPort, "dbChronoSchedule", "standard", "1234");
        mySQLAssistant = MySQLAssistant.getInstance();
        if (!mySQLAssistant.isInitialized()) {
            mySQLAssistant.initialize(mySQLConnectorFactory);
        }
     }

    /**
     * Initialize our SQLite local DB assistant
     *
     * @author Alejandro Olivan Alvarez
     * @throws SQLiteException
     */
    private void initializeSQLite() throws SQLiteException {
        Log.d("initializeSQLite", "asked to initialize SQLite");
        sqLiteAssistant = SQLiteAssistant.getInstance();
        if (!sqLiteAssistant.isInitialized()) {
            sqLiteAssistant.initialize(getApplicationContext());
        }
    }

    /**
     * Initialize a Memory cache for ORM transactions and cache
     *
     * @author Alejandro Olivan Alvarez
     * @throws OrmException
     */
    private void initializeORM() throws OrmException {
        Log.d("InitializeORM", "asked to initialize ORM");
        if (mySQLAssistant != null && sqLiteAssistant != null) {
            ormAssistant = ORMAssistant.getInstance();
            if (!ormAssistant.isInitialized()) {
                ormAssistant.initialize(mySQLAssistant, sqLiteAssistant);
            }
        } else {
            Log.d("InitializeORM", "asking to initialize with null parameters");
            throw new OrmException("Cannot be initialized with null MySQL or SQLite Assistants.");
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
        Intent intent = new Intent("com.afodevelop.chronoschedule.MY_TIMER");
        alarmPendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        long now = System.currentTimeMillis();
        long interval = 1 * 15 * 1000;
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, now + interval, interval,
                alarmPendingIntent);

        IntentFilter filter = new IntentFilter("com.afodevelop.chronoschedule.MY_TIMER");
        jdbcStatusUpdateReceiver = new JdbcStatusUpdateReceiver();
        registerReceiver(jdbcStatusUpdateReceiver, filter);
    }

    /**
     * This method takes charge of instantiating, and initializing the UI.
     *
     * @author Alejandro Olivan Alvarez
     */
    private void renderUI() {
        // Set up the login form.
        Log.d("renderUI", "Asked to render the UI");
        if (!uiRendered) {
            Log.d("renderUI", "UI still unrendered, rendering....");
            mUsernameView = (EditText) findViewById(R.id.username);
            mPasswordView = (EditText) findViewById(R.id.password);
            mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                    if (id == R.id.login || id == EditorInfo.IME_NULL) {
                        attemptLogin();
                        return true;
                    }
                    return false;
                }
            });

            mUsernameSignInButton = (Button) findViewById(R.id.sign_in_button);
            mUsernameSignInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    attemptLogin();
                }
            });

            mLoginFormView = findViewById(R.id.login_form);
            mProgressView = findViewById(R.id.login_progress);
            uiRendered = true;
        } else {
            Log.d("renderUI", "UI already rendered, Skipping Views re-render.");
        }

        Log.d("renderUI", "Asking to re-render the Menu");
        invalidateOptionsMenu();
    }

    /**
     * The main logic method is called separatelly, and after initialization,
     * because it simply controls the flow of program execution based on
     * all previous environment setup.
     *
     * @author Alejandro Olivan Alvarez
     */
    private void mainLogic(){
        Log.d("execute", "firstexecution: " + firstExecution + ", connectivity: " + connectivity);
        // Main logic flow happens here...
        if (firstExecution) {
            if (connectivity) {
                //launch MySQL -> SQLite Resync
                ResyncTask resyncTask = new ResyncTask();
                resyncTask.execute();
            } else {
                printToast("Fatal error: Neither cached data" +
                        " nor database connection available.\n" +
                        "Exiting now.");
                finish();
            }
        } else {
            if (connectivity) {
                //launch MySQL -> SQLite Resync
                ResyncTask resyncTask = new ResyncTask();
                resyncTask.execute();
            }
        }
    }

    /**
     * This method is used to persist connection details as
     * shared preferences
     *
     * @author Alejandro Olivan Alvarez
     * @param host a string with host name or IP address
     * @param port a string with MySQL listen port
     */
    protected void storePreferences(String host, String port){
        // Summon the SharedPreferences instance
        SharedPreferences lastState = getSharedPreferences(SP_NAME, MODE_PRIVATE);
        // get the preferences editor to manipulate them
        SharedPreferences.Editor editor = lastState.edit();
        // Tag and write the iD int value into SharedPreferences
        editor.putString(SP_KEY_DBHOST, host);
        editor.putString(SP_KEY_DBPORT, port);
        // Commit changes made through editor
        editor.commit();
    }

    /**
     * This method actually asks out MySQL JDBC assistant to check for
     * available connectivity.
     *
     * @author Alejandro Olivan Alvarez
     * @return a boolean true (connectivity OK) or false on teh contrary
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
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     *
     * @author Alejandro Olivan Alvarez
     */
    private void attemptLogin() {
        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid username.
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Perform login attempt.
            try {
                User candidate = sqLiteAssistant.getUserByUserName(username);
                if (candidate != null){
                    if (candidate.getPass().contentEquals(password)) {
                        signIn(candidate);
                    }
                } else {
                    printToast("Wrong username or password.");
                }
            } catch (SQLiteException e) {
                printToast("Error: Unable to access users list.");
            }
        }
    }

    /**
     * This method is responsible of gracefully pass all assistant and artifacts to the
     * main activity
     *
     * @author Alejandro Olivan Alvarez
     * @param user A User object instance representing the candidate
     */
    private void signIn(User user) {
        Bundle extras = new Bundle();
        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        extras.putString("user", user.getUserName());
        i.putExtras(extras);
        startActivity(i);
    }

    /**
     * This method launches the settings activity passing it the
     * current connection data.
     *
     * @author Alejandro Olivan Alvarez
     */
    private void launchSettings() {
        Log.d("launchSettings","setingsActivity");
        Intent i = new Intent(LoginActivity.this, SettingsActivity.class);
        Bundle extras = new Bundle();
        extras.putString(SP_KEY_DBHOST, dbHost);
        extras.putString(SP_KEY_DBPORT, dbPort);
        i.putExtras(extras);
        startActivityForResult(i, APP_CALL_ID);
    }

    /**
     * Manage return from Activity calls
     *
     * @author Alejandro Olivan Alvarez
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Bundle extras;
        if(requestCode == APP_CALL_ID)
        {
            if(resultCode == RESULT_OK)
            {
                Log.d("onActivityResult","RESULT_OK");
                extras = data.getExtras();
                dbPort = extras.getString(SP_KEY_DBPORT);
                dbHost = extras.getString(SP_KEY_DBHOST);
                //initialize
                firstExecution = false;
                InitializationTask mySQLInitializationTask = new InitializationTask();
                mySQLInitializationTask.execute();

            } else {
                if (firstExecution) {
                    finish();
                }
            }
        }
    }

    /**
     * Handle ActionBar Items press
     *
     * @author Alejandro Olivan Alvarez
     * @param item the menu item instance clicked by user
     * @return a true valued boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.login_refresh_menuitem:
                //launch MySQL -> SQLite Resync
                ResyncTask resyncTask = new ResyncTask();
                resyncTask.execute();
                break;

            case R.id.login_preferences_menuitem:
                launchSettings();
                break;
        }
        return true;
    }

    /**
     * Save las state using Android native SharedPreferences persistence
     *
     * @author Alejandro Olivan Alvarez
     */
    @Override
    protected void onStop() {
        super.onStop();
        if(!firstExecution) {
            storePreferences(dbHost, dbPort);
            unregisterReceiver(jdbcStatusUpdateReceiver);
        }
    }

    /**
     * An auxiliare method to ease Toast printing
     *
     * @author Alejandro Olivan Alvarez
     * @param s The string to be printed inside the toast
     */
    private void printToast(String s){
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
}

