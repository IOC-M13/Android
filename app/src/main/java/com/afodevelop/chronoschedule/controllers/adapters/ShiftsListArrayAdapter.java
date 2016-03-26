package com.afodevelop.chronoschedule.controllers.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.afodevelop.chronoschedule.R;
import com.afodevelop.chronoschedule.controllers.activities.ShiftFormActivity;
import com.afodevelop.chronoschedule.controllers.fragments.ShiftsFragment;
import com.afodevelop.chronoschedule.controllers.fragments.UsersFragment;
import com.afodevelop.chronoschedule.model.Shift;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by alex on 10/03/16.
 */
public class ShiftsListArrayAdapter extends ArrayAdapter<String> {

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

    static class UserHolder {
        ImageButton btnDelete;
        ImageButton btnEdit;
        TextView itemName;
    }
}
