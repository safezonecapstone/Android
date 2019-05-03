package com.safezone.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import de.codecrafters.tableview.TableDataAdapter;

public class CrimeDescriptionTableAdapter extends TableDataAdapter<CrimeDescription> {
    public CrimeDescriptionTableAdapter(Context context, ArrayList<CrimeDescription> detailed_data){
        super(context, detailed_data);
    }

    @Override
    public View getCellView(int rowIndex, int columnIndex, ViewGroup parentView) {
        CrimeDescription detailedCrimeDescription = getRowData(rowIndex);
        // go to activity_crimes.xml
        View renderedView= LayoutInflater.from(getContext()).inflate(R.layout.crime_description, parentView, false);
        switch (columnIndex){
            case 0:
                TextView detailedCrime=(TextView)renderedView.findViewById(R.id.Description);
                if(detailedCrime.getParent()!=null){
                    ((ViewGroup)detailedCrime.getParent()).removeView(detailedCrime);
                }
                detailedCrime.setText(detailedCrimeDescription.getDescription_());
                renderedView=detailedCrime;
                break;
            case 1:
                TextView detailedFrequency=(TextView)renderedView.findViewById(R.id.Date);
                if(detailedFrequency.getParent()!=null){
                    ((ViewGroup)detailedFrequency.getParent()).removeView(detailedFrequency);
                }
                detailedFrequency.setText(detailedCrimeDescription.getDate_());
                renderedView=detailedFrequency;
                break;
        }
        return renderedView;
    }
}