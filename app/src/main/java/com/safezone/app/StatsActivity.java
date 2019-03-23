package com.safezone.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.content.Intent;

public class StatsActivity extends Activity {
    private Spinner spinner2;
    private TextView address;

    ListView listView;
    double longitude;
    double latitude;
    double [] long_lat=new double[2];
    String station_name;
    // there are 12 crime categories, when importing dataset array to size of 12
    String crimeCategories[] = {"Murder", "Rape", "Robbery", "Felony Assault", "Burgalary",
            "Grand Larceny", "Petit Larceny", "Kidnapping", "Offenses Against", "Public Order",
            "Shootings", "Misdemeanor Sex Crimes", "Misdemeanor Assault"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        listView = (ListView) findViewById(R.id.listView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, crimeCategories);
        listView.setAdapter(adapter);
        // Register the ListView  for Context menu
        registerForContextMenu(listView);

        address=(TextView) findViewById(R.id.address);
        Intent intent=getIntent();
        if(intent.hasExtra("Coordinates"))
        {
            long_lat=intent.getDoubleArrayExtra("Coordinates");
        }
        if(intent.hasExtra("Station Name"))
        {
            station_name=intent.getStringExtra("Station Name");
        }
        //Bundle extras = getIntent().getExtras();
        address.setText(station_name);

        // create the drop down menu for dates
        //addItemsOnSpinner2();
    }

//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//        super.onCreateContextMenu(menu, v, menuInfo);
////        MenuInflater inflater = getMenuInflater();
////        inflater.inflate(R.menu.menu_main, menu);
////        menu.setHeaderTitle("Select The Action");
//    }
}