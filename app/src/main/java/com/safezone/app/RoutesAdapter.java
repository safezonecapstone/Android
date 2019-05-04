package com.safezone.app;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class RoutesAdapter extends ArrayAdapter<Routes> {

    private static final String TAG = "RouteAdapter";
    private Context acitivity;

    public RoutesAdapter(Context context, ArrayList<Routes> trains) {
        super(context, 0, trains);
        acitivity = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.routes_layout, parent, false);
        }

        Routes route = getItem(position);

        TextView startAddress=(TextView)convertView.findViewById(R.id.startingAddress);
        String start=route.getStartingAddress();
        Log.d(TAG, start);
        startAddress.setText(start);

        TextView endingAddress=(TextView)convertView.findViewById(R.id.endingAddress);
        String end=route.getEndingAddress();
        endingAddress.setText(end);

        Log.d(TAG, end);

        TextView duration=(TextView)convertView.findViewById(R.id.duration);
        String durationTime=route.getDuration();
        duration.setText(durationTime);

        Log.d(TAG, durationTime);

        TextView rating=(TextView)convertView.findViewById(R.id.rating);
        String rate=route.getRating();
        rating.setText(rate);

        Log.d(TAG, rate);

        return convertView;
    }
}
