package com.safezone.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class CrimeStationInformationAdapter  extends ArrayAdapter<CrimeStationInformation> {
    public CrimeStationInformationAdapter(Context context, ArrayList<CrimeStationInformation> crimes) {
        super(context, 0, crimes);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.rows, parent, false);
        }

        CrimeStationInformation crime = getItem(position);

        // Lookup view for data population
        TextView category = convertView.findViewById(R.id.Name);


        String name=crime.getCrime_category();
        // Populate the data into the template view using the data object
        category.setText(name);

        TextView frequency = convertView.findViewById(R.id.Frequency);

        int frequency1=crime.getFrequency();

        frequency.setText(Integer.toString(frequency1));

        // Return the completed view to render on screen
        return convertView;
    }
}
