package com.afodevelop.chronoschedule;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by alex on 4/03/16.
 */
public class ShiftsLegendListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] rgbColors;
    private final String[] shiftNames;

    public ShiftsLegendListAdapter(Activity context, String[] rgbColors, String[] shiftNames) {
        super(context, R.layout.shiftslegend_listview, shiftNames);
        // TODO Auto-generated constructor stub

        this.context = context;
        this.rgbColors = rgbColors;
        this.shiftNames = shiftNames;
    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.shiftslegend_listview, null, true);

        Button shiftColor = (Button) rowView.findViewById(R.id.legend_shift_button);
        TextView shiftName = (TextView) rowView.findViewById(R.id.legend_shift_name);
        shiftColor.setBackgroundColor(Color.parseColor("#" + rgbColors[position]));
        shiftName.setText(shiftNames[position]);

        return rowView;
    };

}
