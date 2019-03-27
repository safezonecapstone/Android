package com.safezone.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CrimeDescriptionAdapter extends ArrayAdapter<CrimeDescription> {
    public CrimeDescriptionAdapter(Context context, ArrayList<CrimeDescription> crimes) {
        super(context, 0, crimes);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.crime_description, parent, false);
        }

        CrimeDescription crime = getItem(position);

        // Lookup view for data population
        TextView description = convertView.findViewById(R.id.Description);


        String description_=crime.getDescription_();
        // Populate the data into the template view using the data object
        description.setText(description_);

        TextView date = convertView.findViewById(R.id.Date);

        String date_=crime.getDate_();

        date.setText(date_);

        // Return the completed view to render on screen
        return convertView;
    }
}
