package com.afodevelop.chronoschedule.controllers.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.afodevelop.chronoschedule.R;
import com.afodevelop.chronoschedule.controllers.activities.UserFormActivity;

import java.util.ArrayList;

/**
 * Created by alex on 10/03/16.
 */
public class UsersListArrayAdapter extends ArrayAdapter<String> {

    Context context;
    int layoutResourceId;
    String user;
    ArrayList<String> data = new ArrayList<String>();


    public UsersListArrayAdapter(Context context, int layoutResourceId,
                             ArrayList<String> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        UserHolder holder = null;

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

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent i = new Intent(context, UserFormActivity.class);
                Bundle extras = new Bundle();
                extras.putBoolean("isNew", false);
                extras.putString("user", user);
                i.putExtras(extras);
                context.startActivity(i);

            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(context);
                myAlertDialog.setTitle("DELETE WARNING!");
                myAlertDialog.setMessage("Please confirm deletion of item");
                myAlertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        // do something when the OK button is clicked
                    }});
                myAlertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        // do something when the Cancel button is clicked
                    }});
                myAlertDialog.show();


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
