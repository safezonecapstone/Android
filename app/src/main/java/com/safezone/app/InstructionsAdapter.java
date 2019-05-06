package com.safezone.app;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class InstructionsAdapter extends ArrayAdapter<Instructions> {

    private static final String TAG="Instructions Adapter";
    private Context acitivity;

    public InstructionsAdapter(Context context, ArrayList<Instructions> instructions){
        super(context,0,instructions);
        acitivity=context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.steps_layout, parent, false);
        }

        Log.d(TAG, "Inside Adapter");

        Instructions instructions=getItem(position);

        Log.d(TAG, "Inside Adapter" + instructions.getTransit_Mode());
        LinearLayout linearLayout=(LinearLayout)convertView.findViewById(R.id.steps);
        if (instructions.getTransit_Mode().equals("WALKING")) {
            TextView textView = new TextView(acitivity);
            textView.setText(instructions.getInstruction());
            linearLayout.addView(textView);
        }
        else if (instructions.getTransit_Mode().equals("TRANSIT")) {
            ImageView imageView = new ImageView(acitivity);
            Log.d(TAG, instructions.getInstruction());
            if (instructions.getInstruction().equals("A")) {
                imageView.setImageResource(R.drawable.ic_nycs_bull_trans_a);
            } else if (instructions.getInstruction().equals("B")) {
                imageView.setImageResource(R.drawable.ic_nycs_bull_trans_b);
            } else if (instructions.getInstruction().equals("C")) {
                imageView.setImageResource(R.drawable.ic_nycs_bull_trans_c);
            } else if (instructions.getInstruction().equals("D")) {
                    imageView.setImageResource(R.drawable.ic_nycs_bull_trans_d);
                } else if (instructions.getInstruction().equals("E")) {
                    imageView.setImageResource(R.drawable.ic_nycs_bull_trans_e);
                } else if (instructions.getInstruction().equals("F")) {
                    imageView.setImageResource(R.drawable.ic_nycs_bull_trans_f);
                } else if (instructions.getInstruction().equals("G")) {
                    imageView.setImageResource(R.drawable.ic_nycs_bull_trans_g);
                } else if (instructions.getInstruction().equals("J")) {
                    imageView.setImageResource(R.drawable.ic_nycs_bull_trans_j);
                } else if (instructions.getInstruction().equals("L")) {
                    imageView.setImageResource(R.drawable.ic_nycs_bull_trans_l);
                } else if (instructions.getInstruction().equals("M")) {
                    imageView.setImageResource(R.drawable.ic_nycs_bull_trans_m);
                } else if (instructions.getInstruction().equals("N")) {
                    imageView.setImageResource(R.drawable.ic_nycs_bull_trans_n);
                } else if (instructions.getInstruction().equals("Q")) {
                    imageView.setImageResource(R.drawable.ic_nycs_bull_trans_q);
                } else if (instructions.getInstruction().equals("R")) {
                    imageView.setImageResource(R.drawable.ic_nycs_bull_trans_r);
                } else if (instructions.getInstruction().equals("S")) {
                    imageView.setImageResource(R.drawable.ic_nycs_bull_trans_s);
                } else if (instructions.getInstruction().equals("W")) {
                    imageView.setImageResource(R.drawable.ic_nycs_bull_trans_w);
                } else if (instructions.getInstruction().equals("Z")) {
                    imageView.setImageResource(R.drawable.ic_nycs_bull_trans_z);
                } else if (instructions.getInstruction().equals("1")) {
                    imageView.setImageResource(R.drawable.ic_nycs_bull_trans_1);
                } else if (instructions.getInstruction().equals("2")) {
                    imageView.setImageResource(R.drawable.ic_nycs_bull_trans_2);
                } else if (instructions.getInstruction().equals("3")) {
                    imageView.setImageResource(R.drawable.ic_nycs_bull_trans_3);
                } else if (instructions.getInstruction().equals("4")) {
                    imageView.setImageResource(R.drawable.ic_nycs_bull_trans_4);
                } else if (instructions.getInstruction().equals("5")) {
                    imageView.setImageResource(R.drawable.ic_nycs_bull_trans_5);
                } else if (instructions.getInstruction().equals("6")) {
                    imageView.setImageResource(R.drawable.ic_nycs_bull_trans_6);
                } else if( instructions.getInstruction().equals("6x")) {
                    imageView.setImageResource(R.drawable.ic_nycs_bull_trans_6d);
                } else if (instructions.getInstruction().equals("7")) {
                    imageView.setImageResource(R.drawable.ic_nycs_bull_trans_7);
                }
                linearLayout.addView(imageView);
            }
        return convertView;
    }


}
