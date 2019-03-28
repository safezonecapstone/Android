package com.safezone.app;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class TrainInformationAdapter extends ArrayAdapter<TrainInformation> {

    private Context acitivity;
    public TrainInformationAdapter(Context context, ArrayList<TrainInformation> trains){
        super(context, 0, trains);
        acitivity=context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_location, parent, false);
        }

        final TrainInformation location = getItem(position);

        // Lookup view for data population
        TextView place = convertView.findViewById(R.id.locationName);


        String location_name=getContext().getString(R.string.location_name, location.getName());
        // Populate the data into the template view using the data object
        place.setText(location_name);

        TextView trains = convertView.findViewById(R.id.trains);

        StringBuilder builder = new StringBuilder();
        for(int i=0; i<location.getTrainStop().size(); i++)
        {
            builder.append(location.getTrainStop().get(i)+ " ");
        }
        String listOfTrains=getContext().getString(R.string.trains, builder.toString());

        trains.setText(listOfTrains);

        TextView percentile = convertView.findViewById(R.id.percentile);

        String percent=getContext().getString(R.string.percentile, location.getPercentile());
        // Populate the data into the template view using the data object
        percentile.setText(percent);

        Button viewCrimeButton= (Button) convertView.findViewById(R.id.viewCrimes);
        viewCrimeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent myintent = new Intent(acitivity, StatsActivity.class);

                double longitude;
                double latitude;
                longitude=location.getLongitude();
                latitude=location.getLatitude();
                myintent.putExtra("Latitude", latitude);
                myintent.putExtra("Longitude", longitude);
                myintent.putExtra("Station Name", location.getName());

                acitivity.startActivity(myintent);
            }
        });

        // Return the completed view to render on screen
        return convertView;
    }
}