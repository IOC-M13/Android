package com.afodevelop.chronoschedule.controllers.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
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
import com.afodevelop.chronoschedule.common.JdbcException;
import com.afodevelop.chronoschedule.common.OrmCache;
import com.afodevelop.chronoschedule.controllers.mysqlControllers.MySQLAssistant;
import com.afodevelop.chronoschedule.controllers.mysqlControllers.MySQLConnectorFactory;
import com.afodevelop.chronoschedule.controllers.sqliteControllers.SQLiteAssistant;

import java.sql.SQLException;

/**
 * A login screen that offers login via username/password.
 */
public class LoginActivity extends AppCompatActivity {


    // CONSTANTS
    private static final int APP_CALL_ID = 0;
    private static final String SP_NAME = "Preferences";
    private static final String SP_KEY_DBHOST = "dbHost";
    private static final String SP_KEY_DBPORT = "dbPort";

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "alex:123456", "oscar:678910", "admin:123456"
    };

    // INTERNAL CLASS DEFINITIONS
    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUsername;
        private final String mPassword;

        UserLoginTask(String username, String password) {
            mUsername = username;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mUsername)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                signIn(mUsername);
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }


    // CLASS-WIDE VARIABLES
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private boolean connectivity, firstExecution;
    private String dbHost, dbPort;
    MySQLConnectorFactory mySQLConnectorFactory;
    MySQLAssistant mySQLAssistant;
    SQLiteAssistant mySQLiteAssistant;
    private UserLoginTask mAuthTask = null;

    private EditText mUsernameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;


    // LOGIC

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize the shared preferences
        SharedPreferences lastState = getSharedPreferences(SP_NAME, MODE_PRIVATE);
        dbHost = lastState.getString(SP_KEY_DBHOST, "");
        dbPort = lastState.getString(SP_KEY_DBPORT, "");

        // Check Shared Preferences
        if (dbHost.equalsIgnoreCase("")) {
            firstExecution = true;
            launchSettings();
        } else {
            firstExecution = false;

            initializeMySQL();
            initializeSQLite();
            connectivity = checkConnectivity();
            execute();
        }
    }

    /**
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.login_menu, menu);
        return true;
    }

    private void initializeMySQL(){
        Log.d("initializeMySQL", "asked to initialize MySQL");
        try {
            mySQLConnectorFactory = new MySQLConnectorFactory(
                    dbHost, dbPort, "dbChronoSchedule", "standard", "1234");
            mySQLAssistant = MySQLAssistant.getInstance();
            mySQLAssistant.initialize(mySQLConnectorFactory);
        } catch (JdbcException e) {
            e.printStackTrace();
            printToast("JDBC initialization error");
        }
    }

    private void initializeSQLite(){
        Log.d("initializeSQLite","asked to initialize SQLite");
        mySQLiteAssistant = SQLiteAssistant.getInstance();
    }

    private void renderUI(){
        // Set up the login form.
        Log.d("renderUI","Asked to render the UI");
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

        Button mUsernameSignInButton = (Button) findViewById(R.id.sign_in_button);
        mUsernameSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void execute(){
        Log.d("execute","firstexecution: " + firstExecution + ", connectivity: " + connectivity);
        // Main logic flow happens here...
        if (firstExecution) {
            if (connectivity) {
                launchResync();
            } else {
                printToast("Fatal error: Neither cached data" +
                        " nor database connection available.\n" +
                        "Exiting now.");
                finish();
            }
        } else {
            if (connectivity) {
                launchResync();
                renderUI();
            }
        }
    }

    /**
     * This method is used to persist connection details as
     * shared preferences
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
     *
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
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        } else if (!isUsernameValid(email)) {
            mUsernameView.setError(getString(R.string.error_invalid_username));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    /**
     *
     * @param username
     * @return
     */
    private boolean isUsernameValid(String username) {
        //TODO: Replace this with your own logic
        return true;
    }

    /**
     *
     * @param password
     * @return
     */
    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     *
     * @param user
     */
    private void signIn(String user) {
        Bundle extras = new Bundle();
        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        extras.putString("user", user);
        if (user.equals("admin")){
            extras.putBoolean("isAdmin", true);
        } else {
            extras.putBoolean("isAdmin", false);
        }
        i.putExtras(extras);
        startActivity(i);
    }

    /**
     * This method triggers MySQL -> SQLite sync
     */
    private void launchResync() {
        //TODO all this should be done in an Async task?
        Log.d("launchResync", "resync called");
        OrmCache dataCache = new OrmCache();
        try {
            // Read and Cache MySQL data in memory
            dataCache.setShiftsList(mySQLAssistant.getShiftsResultSet());
            dataCache.setUsersList(mySQLAssistant.getUserResultSet());
            dataCache = mySQLAssistant.InitializeUserShiftCache(dataCache);

            //Dump cached data into SQLite
            mySQLiteAssistant.persistOrmCache(dataCache);

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (JdbcException e) {
            e.printStackTrace();
        }

    }

    /**
     * This method launches the settings activity passing it the
     * current connection data.
     */
    private void launchSettings() {
        Log.d("launchSettings","setingsActivity");
        Intent i = new Intent(LoginActivity.this, SettingsActivity.class);
        Bundle extras = new Bundle();
        extras.putString(SP_KEY_DBHOST, dbHost);
        extras.putString(SP_KEY_DBPORT, dbPort);
        i.putExtras(extras);
        startActivityForResult(i,APP_CALL_ID);
    }

    /**
     * Manage return from Activity calls
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

                initializeMySQL();
                initializeSQLite();
                connectivity = checkConnectivity();
                execute();
            } else {
                if (firstExecution) {
                    finish();
                }
            }
        }
    }



    /**
     * Handle ActionBar Items press
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.refresh:
                launchResync();
                break;

            case R.id.preferences:
                launchSettings();
                break;
        }
        return true;
    }

    /**
     * An auxiliare method to ease Toast printing
     * @param s
     */
    private void printToast(String s){
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
}

