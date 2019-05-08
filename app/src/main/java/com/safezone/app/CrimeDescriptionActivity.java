package com.safezone.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;

import androidx.appcompat.app.AppCompatActivity;
import de.codecrafters.tableview.SortableTableView;
import de.codecrafters.tableview.model.TableColumnWeightModel;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;

public class CrimeDescriptionActivity extends AppCompatActivity {

    private static final String TAG = "CrimeDescription";
    private ArrayList<CrimeDescription> crimeDescriptions=new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime);

        getCrimeDescription();
        populateView();
    }

    //Retreive crime description passed by the previous activity
    public void getCrimeDescription()
    {
        Log.d(TAG, "Entering");
        Intent intent = getIntent();
        if(intent.hasExtra("BUNDLE")) {

            Log.d(TAG, "Entering 2");
            Bundle bundle=intent.getBundleExtra("BUNDLE");
            crimeDescriptions = (ArrayList<CrimeDescription>) bundle.getSerializable("Crime Description"); //Gets the crime description
        }

        for(int i=0; i<crimeDescriptions.size(); i++)
        {
            Log.d(TAG, crimeDescriptions.get(i).getDate_());
        }
    }

    //Populate the view with crime description and date using costume adapter
    public void populateView()
    {
        final String[] TableHeader={"Description", "Date"};
        SortableTableView<CrimeDescription> crimeDescriptionSortableTableView=
                (SortableTableView<CrimeDescription>)findViewById(R.id.tableViewCrimeDescription);
        CrimeDescriptionTableAdapter crimeDescriptionTableAdapter=new CrimeDescriptionTableAdapter(this, crimeDescriptions);
        TableColumnWeightModel columnModel = new TableColumnWeightModel(2);
        columnModel.setColumnWeight(0, 3);
        columnModel.setColumnWeight(1, 1);
        crimeDescriptionSortableTableView.setColumnModel(columnModel);
        crimeDescriptionSortableTableView.setDataAdapter(crimeDescriptionTableAdapter);
        crimeDescriptionSortableTableView.setHeaderAdapter(new SimpleTableHeaderAdapter(this, TableHeader));

        crimeDescriptionSortableTableView.setColumnComparator(1, new DateComparator());
//        CrimeDescriptionAdapter adapter=new CrimeDescriptionAdapter(this, crimeDescriptions);
//        ListView listView = (ListView) findViewById(R.id.crime);
//        listView.setAdapter(adapter);
    }

    private static class DateComparator implements Comparator<CrimeDescription> {
        @Override
        public int compare(CrimeDescription o1, CrimeDescription o2) {
            DateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
            try {
                return dateFormat.parse(o1.getDate_()).compareTo(dateFormat.parse(o2.getDate_()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return 0;
        }
    }
}
