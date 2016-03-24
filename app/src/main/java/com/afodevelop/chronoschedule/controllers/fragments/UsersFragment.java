package com.afodevelop.chronoschedule.controllers.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.afodevelop.chronoschedule.R;
import com.afodevelop.chronoschedule.controllers.activities.MainActivity;
import com.afodevelop.chronoschedule.controllers.activities.UserFormActivity;
import com.afodevelop.chronoschedule.controllers.adapters.UsersListArrayAdapter;
import com.afodevelop.chronoschedule.controllers.mysqlControllers.JdbcException;
import com.afodevelop.chronoschedule.controllers.sqliteControllers.SQLiteException;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by alex on 3/03/16.
 */
public class UsersFragment extends Fragment {

    // COSTANTS

    // INTERNAL CLASS DEFINITIONS
    /**
     * This class is an AsyncTask based task that performs a user data update.
     */
    private class DeleteUserTask extends AsyncTask<Void, Void, Void> {

        private Exception exceptionToBeThrown;
        private String user;

        DeleteUserTask(String targetUser){
            this.user = targetUser;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                mainActivity.deleteUser(user);
            } catch (SQLException | JdbcException | ClassNotFoundException | SQLiteException e) {
                exceptionToBeThrown = e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (exceptionToBeThrown == null){
                printToast("User successfully deleted!");
                mainActivity.forceResync();
                refreshData();
            } else {
                printToast("Error occurred while updating user in DB");
            }
        }
    }

    // CLASS-WIDE VARIABLES
    private MainActivity mainActivity;
    private View myFragmentView;
    private ListView listView;
    private UsersListArrayAdapter arrayAdapter;
    private FloatingActionButton addUserButton;
    private ArrayList<String> userNames;
    private boolean connectivity;

    // LOGIC

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myFragmentView = inflater.inflate(R.layout.users_fragment, container, false);

        // Initialize a variable pointing to parent Activity
        mainActivity = (MainActivity) getActivity();

        // Initialize early values
        userNames = new ArrayList<>();

        listView = (ListView) myFragmentView.findViewById(R.id.users_list);
        arrayAdapter = new UsersListArrayAdapter(getActivity(), R.layout.users_shifts_listview, userNames, UsersFragment.this);
        listView.setAdapter(arrayAdapter);

        refreshData();

        addUserButton = (FloatingActionButton) myFragmentView.findViewById(R.id.add_user_button);
        addUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), UserFormActivity.class);
                Bundle extras = new Bundle();
                extras.putString("mode", "new");
                i.putExtras(extras);
                startActivity(i);
            }
        });

        return myFragmentView;
    }

    /**
     * We are using the onResume flow call to prompt for calendar refreshing:
     * Refreshthe data, the decoration, and apply all this again to the calendar
     */
    @Override
    public void onResume() {
        super.onResume();
        mainActivity.forceResync();
    }

    /**
     * Get/Refresh data from DB to fill arrays of strings with most used data
     * usernames, shiftnames and shift colors
     */
    public void refreshData() {
        try {
            Log.d("usersFragment", "refreshing data");
            connectivity = mainActivity.hasConnectivty();
            Log.d("usersFragment", "connectivity is :" + connectivity);
            userNames.clear();

            for (String userName: mainActivity.getUserNames()){
                userNames.add(userName);
            }

            listView.invalidateViews();
            arrayAdapter.notifyDataSetChanged();

        } catch (SQLiteException e) {
            printToast("Error fetching data from DB.");
            e.printStackTrace();
        }
    }

    /**
     * Call delete user in our MySQLAssistant. we got the username passed as
     * argument. So we first get the target user, and then pass it to
     * MySQLAssistant delete method
     * @param userName
     */
    public void deleteUser(final String userName){
        if (connectivity) {

            AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(getActivity());
            myAlertDialog.setTitle("DELETE WARNING!");
            myAlertDialog.setMessage("Please confirm deletion of item");
            myAlertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    DeleteUserTask deleteUserTask = new DeleteUserTask(userName);
                    deleteUserTask.execute();
                }
            });
            myAlertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface arg0, int arg1) {
                    // do something when the Cancel button is clicked
                }
            });
            myAlertDialog.show();
        } else {
            printToast("Impossible to comply: can't delete user while in OFF-LINE mode.");
        }

    }

    public void editUser(String user){
        Intent i = new Intent(getActivity(), UserFormActivity.class);
        Bundle extras = new Bundle();
        extras.putString("mode", "edit");
        extras.putString("user", user);
        i.putExtras(extras);
        startActivity(i);
    }

    /**
     * An auxiliare method to ease Toast printing
     * @param s
     */
    private void printToast(String s){
        Toast.makeText(mainActivity, s, Toast.LENGTH_SHORT).show();
    }

}