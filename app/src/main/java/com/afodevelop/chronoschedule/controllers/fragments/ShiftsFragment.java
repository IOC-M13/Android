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
import com.afodevelop.chronoschedule.controllers.activities.ShiftFormActivity;
import com.afodevelop.chronoschedule.controllers.adapters.ShiftsListArrayAdapter;
import com.afodevelop.chronoschedule.model.JdbcException;
import com.afodevelop.chronoschedule.model.SQLiteException;
import com.afodevelop.chronoschedule.model.Shift;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This is the Fragment Class that holds the Calendar.
 *
 * @author Alejandro Olivan Alvarez
 */
public class ShiftsFragment extends Fragment {

    // INTERNAL CLASS DEFINITIONS
    /**
     * This class is an AsyncTask based task that performs a user data update.
     *
     * @author Alejandro Olivan Alvarez
     */
    private class DeleteShiftTask extends AsyncTask<Void, Void, Void> {

        private Exception exceptionToBeThrown;
        private int shiftId;

        DeleteShiftTask(int targetShift){
            this.shiftId = targetShift;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                mainActivity.deleteShift(shiftId);
            } catch (SQLException | JdbcException | ClassNotFoundException | SQLiteException e) {
                exceptionToBeThrown = e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (exceptionToBeThrown == null){
                printToast("Shift successfully deleted!");
                mainActivity.forceResync();
                refreshData();
            } else {
                printToast("Error occurred while updating shift in DB");
            }
        }
    }

    // CLASS-WIDE VARIABLES
    private MainActivity mainActivity;
    private View myFragmentView;
    private ListView listView;
    private ShiftsListArrayAdapter arrayAdapter;
    private FloatingActionButton addShiftButton;
    private HashMap<Integer, Integer> shiftsPositionIDMap;
    private ArrayList<String> shifts;
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
        myFragmentView = inflater.inflate(R.layout.shifts_fragment, container, false);

        // Initialize a variable pointing to parent Activity
        mainActivity = (MainActivity) getActivity();

        // Initialize early values
        shifts = new ArrayList<>();
        shiftsPositionIDMap = new HashMap<>();

        listView = (ListView) myFragmentView.findViewById(R.id.shifts_list);
        arrayAdapter = new ShiftsListArrayAdapter(getActivity(), R.layout.users_shifts_listview,
                shifts, ShiftsFragment.this);
        listView.setAdapter(arrayAdapter);

        // Early refresh data
        refreshData();

        addShiftButton = (FloatingActionButton) myFragmentView.findViewById(R.id.add_shift_button);
        addShiftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ShiftFormActivity.class);
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
        if (addShiftButton != null) {
            if (enable) {
                addShiftButton.setVisibility(View.VISIBLE);
                addShiftButton.setClickable(true);
                addShiftButton.setEnabled(true);
            } else {
                addShiftButton.setVisibility(View.GONE);
                addShiftButton.setClickable(false);
                addShiftButton.setEnabled(false);
            }
        }
    }

    /**
     * We are using the onResume flow call to prompt for calendar refreshing:
     * Refreshthe data, the decoration, and apply all this again to the calendar
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
            Log.d("ShiftsFragment", "refreshing data");
            connectivity = mainActivity.hasConnectivty();
            Log.d("ShiftsFragment", "connectivity is :" + connectivity);
            shifts.clear();
            shiftsPositionIDMap.clear();
            ArrayList<Shift> tmp = mainActivity.getSqLiteAssistant().getAllShifts();
            Log.d("ShiftsFragment", "Retrieved " + tmp.size() + " shifts from DB");
            int i = 0;
            for (Shift s: tmp){
                shifts.add(s.getName());
                shiftsPositionIDMap.put(i, s.getIdShift());
                Log.d("ShiftsFragment", "mapping position " + i + " to ID " + s.getIdShift());
                i++;
            }

            arrayAdapter = new ShiftsListArrayAdapter(getActivity(), R.layout.users_shifts_listview,
                    shifts, ShiftsFragment.this);
            listView.setAdapter(arrayAdapter);

            updateButton(connectivity);

        } catch (SQLiteException e) {
            printToast("Error fetching data from DB.");
            e.printStackTrace();
        }
    }

    /**
     * Call delete shift in our MySQLAssistant. we got the shiftId passed as
     * argument. So we first get the target shift, and then pass it to
     * MySQLAssistant delete method
     *
     * @author Alejandro Olivan Alvarez
     * @param position
     */
    public void deleteShift(final int position){
        if (connectivity) {
            Log.d("shiftsfragment", "asked to edit position: " + position);
            Log.d("shitfsfragment", "position maps to ID; " + shiftsPositionIDMap.get(position));
            AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(getActivity());
            myAlertDialog.setTitle("DELETE WARNING!");
            myAlertDialog.setMessage("Please confirm deletion of item");
            myAlertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    DeleteShiftTask deleteShiftTask = new DeleteShiftTask(
                            shiftsPositionIDMap.get(position));
                    deleteShiftTask.execute();
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
     * This method prepares and launches the shiftForm activity to edit a shift
     * whose shift ID is passed as integer param
     *
     * @author Alejandro Olivan Alvarez
     * @param position an nteger with the ID of shift to be edited
     */
    public void editShift(int position){
        Log.d("shiftsfragment", "asked to edit position: " + position);
        Log.d("shitfsfragment", "position maps to ID; " + shiftsPositionIDMap.get(position));
        Intent i = new Intent(getActivity(), ShiftFormActivity.class);
        Bundle extras = new Bundle();
        extras.putString("mode", "edit");
        extras.putInt("shiftId", shiftsPositionIDMap.get(position));
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