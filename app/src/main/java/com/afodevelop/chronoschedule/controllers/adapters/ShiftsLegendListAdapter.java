package com.afodevelop.chronoschedule.controllers.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.afodevelop.chronoschedule.R;

/**
 * This class is the adapter that builds the Shifts List that appears on the legend
 *
 * @author Alejandro Olivan Alvarez
 */
public class ShiftsLegendListAdapter extends ArrayAdapter<String> {

    // CLASS_WIDE VARIABLES
    private final Activity context;
    private final String[] rgbColors;
    private final String[] shiftNames;

    // CONSTRUCTOR
    public ShiftsLegendListAdapter(Activity context, String[] rgbColors, String[] shiftNames) {
        super(context, R.layout.shiftslegend_listview, shiftNames);

        this.context = context;
        this.rgbColors = rgbColors;
        this.shiftNames = shiftNames;
    }

    /**
     * The getView method mandatory override.
     * Here the view is finally assembled before returning it back
     *
     * @author Alejandro Olivan Alvarez
     * @param position
     * @param view
     * @param parent
     * @return
     */
    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.shiftslegend_listview, null, true);

        Button shiftColor = (Button) rowView.findViewById(R.id.legend_shift_button);
        TextView shiftName = (TextView) rowView.findViewById(R.id.legend_shift_name);
        shiftColor.setBackgroundColor(Color.parseColor("#" + rgbColors[position]));
        shiftName.setText(shiftNames[position]);

        return rowView;
    }
}
