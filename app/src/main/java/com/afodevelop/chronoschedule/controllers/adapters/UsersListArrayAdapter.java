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
import com.afodevelop.chronoschedule.controllers.fragments.UsersFragment;

import java.util.ArrayList;

/**
 * This class is the Tab adapter that generates the tabs on the main activity
 *
 * @author Alejandro Olivan Alvarez
 */
public class UsersListArrayAdapter extends ArrayAdapter<String> {

    // INTERNAL CLASS DEFINITIONS
    private static class UserHolder {
        ImageButton btnDelete;
        ImageButton btnEdit;
        TextView itemName;
    }

    // CLASS-WIDE VARIABLES
    Context context;
    UsersFragment parentFragment;
    int layoutResourceId;
    String user;
    ArrayList<String> data = new ArrayList<>();

    // CONSTRUCTOR
    public UsersListArrayAdapter(Context context, int layoutResourceId,
                             ArrayList<String> data, UsersFragment parentFragment) {
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

        user = data.get(position);

        holder.itemName.setText(user);
        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            final int userPosition = pos;
            final String userName = data.get(pos);
            @Override
            public void onClick(View v) {
                Log.d("UsersArrayAdapter", "click btnEdit at row " + userPosition);
                parentFragment.editUser(userName);
            }
        });
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            final int userPosition = pos;
            final String userName = data.get(pos);
            @Override
            public void onClick(View v) {
                Log.d("UsersArrayAdapter", "click btnDelete at row " + userPosition);
                parentFragment.deleteUser(userName);
            }
        });

        return row;
    }
}
