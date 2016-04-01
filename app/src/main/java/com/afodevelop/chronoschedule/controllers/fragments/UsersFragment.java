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
import com.afodevelop.chronoschedule.model.JdbcException;
import com.afodevelop.chronoschedule.model.SQLiteException;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * This is the Fragment Class that holds the Calendar.
 *
 * @author Alejandro Olivan Alvarez
 */
public class UsersFragment extends Fragment {

    // INTERNAL CLASS DEFINITIONS
    /**
     * This class is an AsyncTask based task that performs a user data update.
     *
     * @author Alejandro Olivan Alvarez
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

    /**
     * The Fragment's onCreateView mandatory override that holds Vies initialization
     *
     * @author Alejandro Olivan Alvarez
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myFragmentView = inflater.inflate(R.layout.users_fragment, container, false);

        // Initialize a variable pointing to parent Activity
        mainActivity = (MainActivity) getActivity();

        // Initialize early values
        userNames = new ArrayList<>();

        listView = (ListView) myFragmentView.findViewById(R.id.users_list);
        arrayAdapter = new UsersListArrayAdapter(getActivity(), R.layout.users_shifts_listview,
                userNames, UsersFragment.this);
        listView.setAdapter(arrayAdapter);

        // Early data refresh
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
        updateButton(connectivity);

        return myFragmentView;
    }

    /**
     * This method enables to dynamically enable/show disable/hide the + button
     * So, basing on connectivity status, the button is controlled.
     *
     * @author Alejandro Olivan Alvarez
     * @param enable a boolean stating whether the fab button has to be rendered
     */
    private void updateButton(boolean enable){
        if (addUserButton != null) {
            if (enable) {
                addUserButton.setVisibility(View.VISIBLE);
                addUserButton.setClickable(true);
                addUserButton.setEnabled(true);
            } else {
                addUserButton.setVisibility(View.GONE);
                addUserButton.setClickable(false);
                addUserButton.setEnabled(false);
            }
        }
    }

    /**
     * We are using the onResume flow call to prompt for calendar refreshing:
     * Refresh the data, the decoration, and apply all this again to the calendar
     *
     * @author Alejandro Olivan Alvarez
     */
    @Override
    public void onResume() {
        super.onResume();
        mainActivity.forceResync();
    }

    /**
     * Get/Refresh data from DB to fill arrays of strings with most used data
     * usernames, shiftnames and shift colors.
     *
     * @author Alejandro Olivan Alvarez
     */
    public void refreshData() {
        try {
            Log.d("usersFragment", "refreshing data");
            connectivity = mainActivity.hasConnectivty();
            Log.d("usersFragment", "connectivity is :" + connectivity);
            userNames.clear();

            for (String userName: mainActivity.getSqLiteAssistant().getUserNames()){
                userNames.add(userName);
            }

            arrayAdapter = new UsersListArrayAdapter(getActivity(), R.layout.users_shifts_listview,
                    userNames, UsersFragment.this);
            listView.setAdapter(arrayAdapter);

            updateButton(connectivity);

        } catch (SQLiteException e) {
            printToast("Error fetching data from DB.");
            e.printStackTrace();
        }
    }

    /**
     * Call delete user in our MySQLAssistant. we got the username passed as
     * argument. So we first get the target user, and then pass it to
     * MySQLAssistant delete method
     *
     * @author Alejandro Olivan Alvarez
     * @param userName The filtering userName attribute
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
                public void onClick(DialogInterface arg0, int arg1) {}
            });
            myAlertDialog.show();
        } else {
            printToast("Impossible to comply: can't delete user while in OFF-LINE mode.");
        }
    }

    /**
     * This method prepares and launches the userForm activity to edit a user
     * whose userName is passed as string param
     * @param user a String with the username of user to be edited
     *
     * @author Alejandro Olivan Alvarez
     */
    public void editUser(String user){
        Intent i = new Intent(getActivity(), UserFormActivity.class);
        Bundle extras = new Bundle();
        extras.putString("mode", "edit");
        extras.putString("user", user);
        i.putExtras(extras);
        startActivity(i);
    }

    /**
     * An auxiliar method to ease Toast printing
     *
     * @author Alejandro Olivan Alvarez
     * @param s A string with the text we want to print in the toast
     */
    private void printToast(String s){
        Toast.makeText(mainActivity, s, Toast.LENGTH_SHORT).show();
    }

    /**
     * This method is used by the adapter (or anyone else!) to query its containing fragment
     * about connectivity status
     *
     * @author Alejandro Olivan Alvarez
     * @return a boolean indicating connectivity is possible (true) or not (false)
     */
    public boolean hasConnectivity(){
        return connectivity;
    }
}