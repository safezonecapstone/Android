package com.safezone.app;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TrainInformationAdapter extends ArrayAdapter<TrainInformation> {
    public TrainInformationAdapter(Context context, ArrayList<TrainInformation> trains){
        super(context, 0, trains);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_location, parent, false);
        }

        TrainInformation location = getItem(position);

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

        // Return the completed view to render on screen
        return convertView;
    }
}