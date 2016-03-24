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
import android.widget.Toast;

import com.afodevelop.chronoschedule.R;
import com.afodevelop.chronoschedule.controllers.adapters.TabPagerAdapter;
import com.afodevelop.chronoschedule.controllers.fragments.CalendarFragment;
import com.afodevelop.chronoschedule.controllers.fragments.ShiftsFragment;
import com.afodevelop.chronoschedule.controllers.fragments.UsersFragment;
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
            Log.d("mainactivity","connectivity status update: " + connectivity);
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
                refreshFragments();
            } else {
                printToast("Database sync error.");
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

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TabPagerAdapter tabPagerAdapter;
    private int currentTab;

    private boolean menuRendered, isAdmin, connectivity;
    private boolean UIrendered = false;
    private User user;

    // LOGIC

    /**
     * The onCreate method
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

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
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

        viewPager = (ViewPager) findViewById(R.id.pager);
        tabPagerAdapter = new TabPagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(tabPagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTab = tab.getPosition();
                viewPager.setCurrentItem(currentTab);
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
     * Here we got a reference to the action bar menu and its Items
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
     * Handle Items clicking on the menu...
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

    /**
     * An external handler to allow for manually trigegring a full DB Connectivity check
     */
    public void forceConnectivityCheck(){
        Log.d("mainactivity","asked to force a data resync.");
        CheckConnectivityTask checkConnectivityTask = new CheckConnectivityTask();
        checkConnectivityTask.execute();
    }

    /**
     * An external handler to allow for manually triggering a full DB resync
     */
    public void forceResync(){
        if (connectivity) {
            Log.d("mainactivity","asked to force a data resync.");
            ResyncTask resyncTask = new ResyncTask();
            resyncTask.execute();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("mainactivity", "resuming...");
        forceConnectivityCheck();
        forceResync();
        if (UIrendered) {
            initializeConnectivityWatchDog();
        } else {
            UIrendered = true;
        }
    }

    /**
     * Save las state using Android native SharedPreferences persistence
     */
    @Override
    protected void onStop() {
        super.onStop();
        Log.d("mainactivity","stopping");
        unregisterReceiver(jdbcStatusUpdateReceiver);
        alarmManager.cancel(alarmPendingIntent);
    }

    /**
     * This method triggers refresh method inside current displayed fragment.
     */
    private void refreshFragments() {
        Log.d("refreshFragments", "asked to refresh fragment " + currentTab);
        CalendarFragment calendarFragment = (CalendarFragment) tabPagerAdapter.
                getfragments(0);
        switch (currentTab) {
            case 0:
                Log.d("refreshFragments", "refresh calendar fragment");
                calendarFragment.refreshData();
                calendarFragment.refreshCalendarDecoration();
                calendarFragment.refreshCalendarLegend();
                break;
            case 1:
                Log.d("refreshFragments", "refresh users fragment");
                UsersFragment usersFragment = (UsersFragment) tabPagerAdapter.
                        getfragments(1);
                usersFragment.refreshData();
                calendarFragment.refreshData();
                break;
            case 2:
                Log.d("refreshFragments", "refresh shifts fragment");
                ShiftsFragment shiftsFragment = (ShiftsFragment) tabPagerAdapter.
                        getfragments(2);
                break;
        }
    }

    /**
     * Returns wether or not loged user has administrative rights.
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

    /**
     * This method is responsable of instantiate, initialize and start both an
     * AlrManager driven periodic broadcasting event, and an broadcast listener that,
     * on receiving the advise, trigges connectivity status check.
     */
    private void initializeConnectivityWatchDog(){
        Log.d("mainactivity","Setting the connectivity watchdog");
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
     * This method calls for user deletion on DB. Target user is passed as argument
     * @param user User to be deleted
     * @throws SQLiteException
     * @throws JdbcException
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public void deleteUser(String user) throws SQLiteException, JdbcException,
            SQLException, ClassNotFoundException {
        User targetUser = sqLiteAssistant.getUserByUserName(user);
        mySQLAssistant.deleteUser(targetUser);
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

    /**
     * An auxiliare method to ease Toast printing
     * @param s
     */
    private void printToast(String s){
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
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

    public boolean hasConnectivty() {
        return connectivity;
    }
}