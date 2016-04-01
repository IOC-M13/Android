package com.afodevelop.chronoschedule.controllers.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.afodevelop.chronoschedule.R;
import com.afodevelop.chronoschedule.controllers.fragments.ShiftsFragment;

import java.util.ArrayList;

/**
 * This class is the adapter that builds the Shifts List on the Shifts Fragment
 *
 * @author Alejandro Olivan Alvarez
 */
public class ShiftsListArrayAdapter extends ArrayAdapter<String> {

    // INTERNAL-CLASS DEFINITIONS
    private static class UserHolder {
        ImageButton btnDelete;
        ImageButton btnEdit;
        TextView itemName;
    }

    // CLASS-WIDE VARIABLES
    Context context;
    ShiftsFragment parentFragment;
    int layoutResourceId;
    String shiftName;
    ArrayList<String> data = new ArrayList<String>();

    // CONSTRUCTOR
    public ShiftsListArrayAdapter(Context context, int layoutResourceId,
                                  ArrayList<String> data, ShiftsFragment parentFragment) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        this.parentFragment = parentFragment;
    }

    // LOGIC

    /**
     * The getView method mandatory override.
     * Here the view is finally assembled before returning it back
     *
     * @author Alejandro Olivan Alvarez
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        View row = convertView;
        UserHolder holder = null;
        final int pos = position;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new UserHolder();
            holder.btnDelete = (ImageButton) row.findViewById(R.id.users_shift_deleteitem);
            holder.btnEdit = (ImageButton) row.findViewById(R.id.users_shift_edititem);
            holder.itemName = (TextView) row.findViewById(R.id.users_shift_itemname);
            row.setTag(holder);
        } else {
            holder = (UserHolder) row.getTag();
        }

        shiftName = data.get(position);

        holder.itemName.setText(shiftName);
        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            final int shiftPosition = pos;
            @Override
            public void onClick(View v) {
                Log.d("ShitfsArrayAdapter", "click btnEdit at row " + shiftPosition);
                parentFragment.editShift(shiftPosition);
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            final int shiftPosition = pos;
            @Override
            public void onClick(View v) {
                Log.d("ShitfsArrayAdapter", "click btnDelete at row " + shiftPosition);
                parentFragment.deleteShift(shiftPosition);
            }
        });
        return row;
    }
}
