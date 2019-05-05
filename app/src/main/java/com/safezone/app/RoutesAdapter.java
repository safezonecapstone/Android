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

        Routes route = getItem(position);

        TextView startAddress = (TextView) convertView.findViewById(R.id.startingAddress);
        String start = route.getStartingAddress();
        Log.d(TAG, start);
        startAddress.setText(start);

        TextView endingAddress = (TextView) convertView.findViewById(R.id.endingAddress);
        String end = route.getEndingAddress();
        endingAddress.setText(end);

        Log.d(TAG, end);

        TextView duration = (TextView) convertView.findViewById(R.id.duration);
        String durationTime = route.getDuration();
        duration.setText(durationTime);

        Log.d(TAG, durationTime);

        TextView rating = (TextView) convertView.findViewById(R.id.rating);
        String rate = route.getRating();
        rating.setText(rate);

        Log.d(TAG, rate);
        LinearLayout linearLayout = (LinearLayout) convertView.findViewById(R.id.instructions);
        ArrayList<Instructions> instructions = route.getInstructions();

        for (int i = 0; i < instructions.size(); i++) {
            if (instructions.get(i).getTransit_Mode().equals("WALKING")) {
                TextView textView = new TextView(getContext());
                textView.setText(instructions.get(i).getInstruction());
                linearLayout.addView(textView);
            } else if (instructions.get(i).getTransit_Mode().equals("TRANSIT")) {
                ImageView imageView = new ImageView(acitivity);
                if (instructions.get(i).getInstruction().equals("A")) {
                    imageView.setImageResource(R.drawable.ic_nycs_bull_trans_a);
                } else if (instructions.get(i).getInstruction().equals("B")) {
                    imageView.setImageResource(R.drawable.ic_nycs_bull_trans_b);
                } else if (instructions.get(i).getInstruction().equals("C")) {
                    imageView.setImageResource(R.drawable.ic_nycs_bull_trans_c);
                } else if (instructions.get(i).getInstruction().equals("D")) {
                    imageView.setImageResource(R.drawable.ic_nycs_bull_trans_d);
                } else if (instructions.get(i).getInstruction().equals("E")) {
                    imageView.setImageResource(R.drawable.ic_nycs_bull_trans_e);
                } else if (instructions.get(i).getInstruction().equals("F")) {
                    imageView.setImageResource(R.drawable.ic_nycs_bull_trans_f);
                } else if (instructions.get(i).getInstruction().equals("G")) {
                    imageView.setImageResource(R.drawable.ic_nycs_bull_trans_g);
                } else if (instructions.get(i).getInstruction().equals("J")) {
                    imageView.setImageResource(R.drawable.ic_nycs_bull_trans_j);
                } else if (instructions.get(i).getInstruction().equals("L")) {
                    imageView.setImageResource(R.drawable.ic_nycs_bull_trans_l);
                } else if (instructions.get(i).getInstruction().equals("M")) {
                    imageView.setImageResource(R.drawable.ic_nycs_bull_trans_m);
                } else if (instructions.get(i).getInstruction().equals("N")) {
                    imageView.setImageResource(R.drawable.ic_nycs_bull_trans_n);
                } else if (instructions.get(i).getInstruction().equals("Q")) {
                    imageView.setImageResource(R.drawable.ic_nycs_bull_trans_q);
                } else if (instructions.get(i).getInstruction().equals("R")) {
                    imageView.setImageResource(R.drawable.ic_nycs_bull_trans_r);
                } else if (instructions.get(i).getInstruction().equals("S")) {
                    imageView.setImageResource(R.drawable.ic_nycs_bull_trans_s);
                } else if (instructions.get(i).getInstruction().equals("W")) {
                    imageView.setImageResource(R.drawable.ic_nycs_bull_trans_w);
                } else if (instructions.get(i).getInstruction().equals("Z")) {
                    imageView.setImageResource(R.drawable.ic_nycs_bull_trans_z);
                } else if (instructions.get(i).getInstruction().equals("1")) {
                    imageView.setImageResource(R.drawable.ic_nycs_bull_trans_1);
                } else if (instructions.get(i).getInstruction().equals("2")) {
                    imageView.setImageResource(R.drawable.ic_nycs_bull_trans_2);
                } else if (instructions.get(i).getInstruction().equals("3")) {
                    imageView.setImageResource(R.drawable.ic_nycs_bull_trans_3);
                } else if (instructions.get(i).getInstruction().equals("4")) {
                    imageView.setImageResource(R.drawable.ic_nycs_bull_trans_4);
                } else if (instructions.get(i).getInstruction().equals("5")) {
                    imageView.setImageResource(R.drawable.ic_nycs_bull_trans_5);
                } else if (instructions.get(i).getInstruction().equals("6")) {
                    imageView.setImageResource(R.drawable.ic_nycs_bull_trans_6);
                } else if (instructions.get(i).getInstruction().equals("7")) {
                    imageView.setImageResource(R.drawable.ic_nycs_bull_trans_7);
                }
                linearLayout.addView(imageView);
            }
        }
        return convertView;
    }
}
