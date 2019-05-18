//**********Rosa*********//
package com.safezone.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import de.codecrafters.tableview.TableDataAdapter;

public class StatsTableAdapter extends TableDataAdapter<CrimeStationInformation> {
    public StatsTableAdapter(Context context, ArrayList<CrimeStationInformation> data){
        super(context, data);
    }

    @Override
    public View getCellView(int rowIndex, int columnIndex, ViewGroup parentView) {
        CrimeStationInformation crimeDescription=getRowData(rowIndex);
        View renderedView= LayoutInflater.from(getContext()).inflate(R.layout.rows, parentView, false);
        switch (columnIndex){
            case 0:
                TextView crime=(TextView)renderedView.findViewById(R.id.Name);
                if(crime.getParent()!=null){
                    ((ViewGroup)crime.getParent()).removeView(crime);
                }
                crime.setText(crimeDescription.getCrime_category());
                renderedView=crime;
                break;
            case 1:
                TextView frequency=(TextView)renderedView.findViewById(R.id.Frequency);
                if(frequency.getParent()!=null){
                    ((ViewGroup)frequency.getParent()).removeView(frequency);
                }
                frequency.setText(Integer.toString(crimeDescription.getFrequency()));
                renderedView=frequency;
                break;
        }
        return renderedView;
    }
}