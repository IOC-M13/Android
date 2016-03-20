package com.afodevelop.chronoschedule.controllers.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.afodevelop.chronoschedule.R;
import com.afodevelop.chronoschedule.controllers.adapters.TabPagerAdapter;
import com.afodevelop.chronoschedule.controllers.mysqlControllers.JdbcException;
import com.afodevelop.chronoschedule.controllers.mysqlControllers.MySQLAssistant;
import com.afodevelop.chronoschedule.controllers.ormControllers.ORMAssistant;
import com.afodevelop.chronoschedule.controllers.ormControllers.OrmException;
import com.afodevelop.chronoschedule.controllers.sqliteControllers.SQLiteAssistant;
import com.afodevelop.chronoschedule.controllers.sqliteControllers.SQLiteException;
import com.afodevelop.chronoschedule.model.Shift;
import com.afodevelop.chronoschedule.model.User;
import com.afodevelop.chronoschedule.model.UserShift;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

    // CONSTANTS


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

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            invalidateOptionsMenu();
        }
    }

    /**
     * This tasks perform a background process of syncing remote mysql database
     * with local, off-line possible, native, sqlite db.
     */
    private class ResyncTask extends AsyncTask<Void, Void, Void> {

        private Exception exceptionToBeThrown;
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute(){
            progressDialog = new ProgressDialog(MainActivity.this); // -> should target a fragment!
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
                //renderUI(); -> but on active fragment!
            } else {
                //printToast("Database sync error."); -> but on active fragment!
            }
        }
    }



    // CLASS-WIDE VARIABLES
    private MySQLAssistant mySQLAssistant;
    private SQLiteAssistant sqLiteAssistant;
    private ORMAssistant ormAssistant;
    private Menu appMenu;
    private AlarmManager alarmManager;
    private PendingIntent alarmPendingIntent;
    private JdbcStatusUpdateReceiver jdbcStatusUpdateReceiver;

    private boolean menuRendered, isAdmin, connectivity;
    private User user;

    // LOGIC

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mySQLAssistant = MySQLAssistant.getInstance();
        sqLiteAssistant = SQLiteAssistant.getInstance();
        ormAssistant = ORMAssistant.getInstance();
        menuRendered = false;
        initializeConnectivityWatchDog();

        try {
            user = sqLiteAssistant.getUserByUserName(getIntent().getExtras().getString("user"));
        } catch (SQLiteException e) {
            e.printStackTrace();
        }

        isAdmin = user.isAdmin();

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        TabLayout.Tab tmpTab;
        tmpTab = tabLayout.newTab().setText("Calendar");
        tmpTab.setIcon(R.drawable.ic_menu_month);
        tabLayout.addTab(tmpTab);
        if (isAdmin) {
            tmpTab = tabLayout.newTab().setText("Users");
            tmpTab.setIcon(R.drawable.ic_menu_allfriends);
            tabLayout.addTab(tmpTab);
            tmpTab = tabLayout.newTab().setText("Shifts");
            tmpTab.setIcon(R.drawable.ic_menu_recent_history);
            tabLayout.addTab(tmpTab);
        }
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final TabPagerAdapter tabPagerAdapter = new TabPagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(tabPagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    /**
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        appMenu = menu;
        if (menuRendered) {
            if (!connectivity) {
                Log.d("renderUI", "Rerendering to OFF-LINE mode menu");
                appMenu.findItem(R.id.main_refresh_menuitem).setIcon(R.drawable.stat_notify_sync_error);
                appMenu.findItem(R.id.main_refresh_menuitem).setEnabled(false);
            } else {
                Log.d("renderUI", "Rerendering to ON-LINE mode menu");
                appMenu.findItem(R.id.main_refresh_menuitem).setIcon(R.drawable.stat_notify_sync_anim0);
                appMenu.findItem(R.id.main_refresh_menuitem).setEnabled(true);
            }
        } else {
            menuRendered = true;
        }
        return true;
    }

    /**
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_refresh_menuitem:
                //launch MySQL -> SQLite Resync
                ResyncTask resyncTask = new ResyncTask();
                resyncTask.execute();
                break;

        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeConnectivityWatchDog();
    }

    /**
     * Save las state using Android native SharedPreferences persistence
     */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(jdbcStatusUpdateReceiver);
        alarmManager.cancel(alarmPendingIntent);
    }

    /**
     *
     * @return
     */
    public boolean isAdmin(){
        return isAdmin;
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

    private void initializeConnectivityWatchDog(){
        Intent intent = new Intent("com.afodevelop.chronoschedule.MY_TIMER");
        alarmPendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        long now = System.currentTimeMillis();
        long interval = 1 * 60 * 1000; // 1 hour
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, now + interval, interval,
                alarmPendingIntent);

        IntentFilter filter = new IntentFilter("com.afodevelop.chronoschedule.MY_TIMER");
        jdbcStatusUpdateReceiver = new JdbcStatusUpdateReceiver();
        registerReceiver(jdbcStatusUpdateReceiver, filter);
    }


    /**
     * This method builds an array of strings with all usernames in DB
     * @return String full fith username strings
     * @throws SQLiteException
     */
    public String[] getUserNames() throws SQLiteException {
        ArrayList<User> users = sqLiteAssistant.getAllUsers();
        if (!users.isEmpty()) {
            String[] result = new String[users.size()];
            for (int i = 0; i < users.size(); i++) {
                result[i] = users.get(i).getUserName();
            }
            return result;
        } else {
            return null;
        }
    }

    /**
     * This method builds an array of strings with all shift names in DB
     * @return
     * @throws SQLiteException
     */
    public String[] getShiftNames() throws SQLiteException {
        ArrayList<Shift> shifts = sqLiteAssistant.getAllShifts();
        if (!shifts.isEmpty()) {
            String[] result = new String[shifts.size()];
            for (int i = 0; i < shifts.size(); i++) {
                result[i] = shifts.get(i).getName();
            }
            return result;
        } else {
            return null;
        }
    }

    /**
     * This method builds an array of strings with all shift colors in DB
     * @return
     * @throws SQLiteException
     */
    public String[] getShiftColors() throws SQLiteException {
        ArrayList<Shift> shifts = sqLiteAssistant.getAllShifts();
        if (!shifts.isEmpty()) {
            String[] result = new String[shifts.size()];
            for (int i = 0; i < shifts.size(); i++) {
                result[i] = shifts.get(i).getColor();
            }
            return result;
        } else {
            return null;
        }
    }

    /**
     * This method retrieves the color of an user work day by means of
     * asigned shift to him/her. The method needs the User object and the date.
     * @param u User to which query the calendar
     * @param d Date at which Shift color has to be queried.
     * @return
     * @throws SQLiteException
     * @throws ParseException
     */
    public String getShiftColor(User u, Date d) throws SQLiteException, ParseException {
        UserShift userShift = sqLiteAssistant.getUserShift(u, d);
        if (userShift != null) {
            return userShift.getShift().getColor();
        } else {
            return null;
        }
    }


    // SETTERS & GETTERS

    public MySQLAssistant getMySQLAssistant() {
        return mySQLAssistant;
    }

    public User getUser() {
        return user;
    }

    public ORMAssistant getOrmAssistant() {
        return ormAssistant;
    }

    public SQLiteAssistant getSqLiteAssistant() {
        return sqLiteAssistant;
    }
}