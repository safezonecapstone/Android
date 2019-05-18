package com.safezone.app;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TrainInformationAdapter extends ArrayAdapter<TrainInformation> {

    private static final String TAG = "StatsActivity";
    private Context acitivity;
    public TrainInformationAdapter(Context context, ArrayList<TrainInformation> trains){
        super(context, 0, trains);
        acitivity=context;
    }

    //Yi Tong
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_location, parent, false);
        }

        final TrainInformation location = getItem(position);

        //Gets the location name for each train station
        TextView place = convertView.findViewById(R.id.locationName);
        String location_name=location.getName();
        place.setText(location_name);

        StringBuilder builder = new StringBuilder();

        ArrayList<String> trainstops=location.getTrainStop();

        ArrayList<Integer> images=new ArrayList<>();

        //Adds all the trains to the images array
        for(int i=0; i<location.getTrainStop().size(); i++) {
            Log.d(TAG, trainstops.get(i));
            if(trainstops.get(i).equals("A")){
                images.add(R.drawable.ic_nycs_bull_trans_a);
            }
            else if(trainstops.get(i).equals("B")){
                images.add(R.drawable.ic_nycs_bull_trans_b);
            }
            else if(trainstops.get(i).equals("C")){
                images.add(R.drawable.ic_nycs_bull_trans_c);
            }
            else if(trainstops.get(i).equals("D")){
                images.add(R.drawable.ic_nycs_bull_trans_d);
            }
            else if(trainstops.get(i).equals("E")){
                images.add(R.drawable.ic_nycs_bull_trans_e);
            }
            else if(trainstops.get(i).equals("F")){
                images.add(R.drawable.ic_nycs_bull_trans_f);
            }
            else if(trainstops.get(i).equals("G")){
                images.add(R.drawable.ic_nycs_bull_trans_g);
            }
            else if(trainstops.get(i).equals("J")){
                images.add(R.drawable.ic_nycs_bull_trans_j);
            }
            else if(trainstops.get(i).equals("L")){
                images.add(R.drawable.ic_nycs_bull_trans_l);
            }
            else if(trainstops.get(i).equals("M")){
                images.add(R.drawable.ic_nycs_bull_trans_m);
            }
            else if(trainstops.get(i).equals("N")){
                images.add(R.drawable.ic_nycs_bull_trans_n);
            }
            else if(trainstops.get(i).equals("Q")){
                images.add(R.drawable.ic_nycs_bull_trans_q);
            }
            else if(trainstops.get(i).equals("R")){
                images.add(R.drawable.ic_nycs_bull_trans_r);
            }
            else if(trainstops.get(i).equals("S")){
                images.add(R.drawable.ic_nycs_bull_trans_s);
            }
            else if(trainstops.get(i).equals("W")){
                images.add(R.drawable.ic_nycs_bull_trans_w);
            }
            else if(trainstops.get(i).equals("Z")){
                images.add(R.drawable.ic_nycs_bull_trans_z);
            }
            else if(trainstops.get(i).equals("1")){
                images.add(R.drawable.ic_nycs_bull_trans_1);
            }
            else if(trainstops.get(i).equals("2")){
                images.add(R.drawable.ic_nycs_bull_trans_2);
            }
            else if(trainstops.get(i).equals("3")){
                images.add(R.drawable.ic_nycs_bull_trans_3);
            }
            else if(trainstops.get(i).equals("4")){
                images.add(R.drawable.ic_nycs_bull_trans_4);
            }
            else if(trainstops.get(i).equals("5")){
                images.add(R.drawable.ic_nycs_bull_trans_5);
            }
            else if(trainstops.get(i).equals("6")){
                images.add(R.drawable.ic_nycs_bull_trans_6);
            }
            else if( trainstops.get(i).equals("6X")) {
                images.add(R.drawable.ic_nycs_bull_trans_6d);
            }
            else if(trainstops.get(i).equals("7")){
                images.add(R.drawable.ic_nycs_bull_trans_7);
            }
        }

        //Displays all the train stop images in a gridview
        GridView layout = (GridView) convertView.findViewById(R.id.grid_view);
        layout.setFocusable(false);
        layout.setClickable(false);
        layout.setNumColumns(images.size());
        Log.d(TAG, String.format("width "+ layout.getColumnWidth()));
        layout.setAdapter(new ImageAdapter(getContext(), images));

        //Gets percentile for each train station
        TextView percentile = convertView.findViewById(R.id.percentile);
        String percent=getContext().getString(R.string.percentile, location.getPercentile());
        percentile.setText(percent);

        // Return the completed view to render on screen
        return convertView;
    }
}