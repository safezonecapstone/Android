package com.safezone.app;

import android.content.Context;
import android.support.v4.app.INotificationSideChannel;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

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

        //Get route
        Routes route = getItem(position);

        TextView routepostion=(TextView)convertView.findViewById(R.id.routeNumber);
        routepostion.setText(""+ (position+1) + ".");

        //Get the duration of the route
        TextView duration = (TextView) convertView.findViewById(R.id.duration);
        String durationTime = getContext().getString(R.string.duration, route.getDuration());
        duration.setText(durationTime);

        Log.d(TAG, durationTime);

        //Displays the safety rating of the route
        TextView rating = (TextView) convertView.findViewById(R.id.rating);
        String rate=getContext().getString(R.string.percentile, route.getRating());
        rating.setText(rate);

        Log.d(TAG, rate);

        return convertView;
    }
}
