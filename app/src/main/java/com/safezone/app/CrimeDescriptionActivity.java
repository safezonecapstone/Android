package com.safezone.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

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

    public void getCrimeDescription()
    {
        Log.d(TAG, "Entering");
        Intent intent = getIntent();
        if(intent.hasExtra("BUNDLE")) {

            Log.d(TAG, "Entering 2");
            Bundle bundle=intent.getBundleExtra("BUNDLE");
            crimeDescriptions = (ArrayList<CrimeDescription>) bundle.getSerializable("Crime Description");
        }
        for(int i=0; i<crimeDescriptions.size(); i++)
        {
            Log.d(TAG, crimeDescriptions.get(i).getDate_());
        }
    }

    public void populateView()
    {
        CrimeDescriptionAdapter adapter=new CrimeDescriptionAdapter(this, crimeDescriptions);
        ListView listView = (ListView) findViewById(R.id.crime);
        listView.setAdapter(adapter);
    }
}
