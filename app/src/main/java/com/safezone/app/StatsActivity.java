package com.safezone.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import androidx.appcompat.app.AppCompatActivity;
import de.codecrafters.tableview.SortableTableView;
import de.codecrafters.tableview.listeners.TableDataClickListener;
import de.codecrafters.tableview.model.TableColumnModel;
import de.codecrafters.tableview.model.TableColumnWeightModel;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;

public class StatsActivity extends AppCompatActivity {

    private static final String TAG = "StatsActivity";

    private Spinner spinner;
    private String address;
    double mCurrentLatitude;
    double mCurrentLongitude;
    private ArrayList<CrimeStationInformation> cat = new ArrayList<CrimeStationInformation>();
    String [] category = new String[]{"Murder", "Burglary", "Felony Assault", "Grand Larceny", "Kidnapping",
            "Misdemeanor Assault", "Offenses against Public Order", "Misdemeanor Sex Crimes",
            "Petit Larceny",  "Rape", "Robbery", "Shootings"};

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        getAddress();
        spinner=(Spinner)findViewById(R.id.spinner1);
        spinner.setSelection(5);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String filter=spinner.getSelectedItem().toString();
                Log.d(TAG, "You selected: "+filter);
                if(filter.equals("Last Week")){
                    getCrimes("week");
                }
                else if(filter.equals("Last Month")){
                    getCrimes("month");
                }
                else if(filter.equals("Last 3 Months")){
                    getCrimes("3 month");
                }
                else if(filter.equals("Last 6 Months")){
                    getCrimes("6 month");
                }
                else if(filter.equals("Last 9 Months")){
                    getCrimes("9 month");
                }
                else if(filter.equals("Last 12 Months")){
                    getCrimes("year");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    //Gets address of station user wants to view
    private void getAddress() {
        Intent intent = getIntent();
        if (intent.hasExtra("Station Name")) {
            address = intent.getStringExtra("Station Name");
            mCurrentLatitude = intent.getDoubleExtra("Latitude", 0.0);
            Log.d(TAG, "Latitude " + mCurrentLatitude);
            mCurrentLongitude = intent.getDoubleExtra("Longitude", 0.0);
            Log.d(TAG, "Longitude " + mCurrentLongitude);
        }
    }

    //get crimes associated with that station
    private void getCrimes(String filter) {
        Log.d(TAG, "getCrimes: lat long " + mCurrentLatitude + " " + mCurrentLongitude);
        Log.d(TAG, "getCrimes: entered");
        String api_key = getString(R.string.safezone_api_key);
        StringBuilder crimes =
                new StringBuilder("https://api-dot-united-triode-233023.appspot.com/api/crimes/nearby?");
        crimes.append("latitude=").append(mCurrentLatitude);
        crimes.append("&longitude=").append(mCurrentLongitude);
        crimes.append("&timeSpan=").append(filter);
        crimes.append("&API_KEY=" + api_key);

        String crimesURL = crimes.toString();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, crimesURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject result) {
                        Log.i(TAG, "onResponse: Result= " + result.toString());
                        try {
                            cat.clear();
                            JSONObject newjsonObject = (JSONObject) result.getJSONObject("frequencies");
                            //Retrieve each frequency of each crime with built in json methods
                            for(int i=0; i<category.length; i++)
                            {
                                int frequency=newjsonObject.getInt(category[i]);
                                Log.d(TAG, category[i] + frequency);
                                CrimeStationInformation crimeStationInformation= new CrimeStationInformation(category[i],frequency );
                                cat.add(crimeStationInformation);
                            }

                            //Get crime descriptions of each crime, along with date of occurance
                            JSONArray jsonArray= (JSONArray) result.getJSONArray("results");
                            for (int i = 0; i< jsonArray.length();i++) {
                                JSONObject jsonresultsObject = (JSONObject) jsonArray.get(i);
                                String category=jsonresultsObject.getString("category");
                                String cdescription=jsonresultsObject.getString("pd_desc");
                                String dates=jsonresultsObject.getString("date");
                                CrimeDescription description=new CrimeDescription(cdescription, dates);
                                for(int j=0; j<cat.size(); j++)
                                {
                                    if(cat.get(j).getCrime_category().equals(category))
                                    {
                                        cat.get(j).addCrimeDescription(description);
                                        break;
                                    }
                                }
                            }

                            populateCrimeListView();
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG, "getSubways: Error = " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse: Error= " + error);
                        Log.e(TAG, "onErrorResponse: Error= " + error.getMessage());
                    }
                });
        AppController.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    //Rosa
    private void populateCrimeListView() {

        TextView textView=(TextView) findViewById(R.id.Address);

        textView.setText(address);
        final String[] TableHeader={"Category", "Frequency"};
        SortableTableView<CrimeStationInformation> crimeStationInformationSortableTableView=
                (SortableTableView<CrimeStationInformation>)findViewById(R.id.tableView);
        StatsTableAdapter statsTableAdapter=new StatsTableAdapter(this, cat);

        TableColumnWeightModel columnModel = new TableColumnWeightModel(2);
        columnModel.setColumnWeight(0, 2);
        columnModel.setColumnWeight(1, 1);

        crimeStationInformationSortableTableView.setColumnModel(columnModel);
        crimeStationInformationSortableTableView.setDataAdapter(statsTableAdapter);
        crimeStationInformationSortableTableView.setHeaderAdapter(new SimpleTableHeaderAdapter(this, TableHeader));

        crimeStationInformationSortableTableView.setColumnComparator(1, new FreuencyComparator());
        crimeStationInformationSortableTableView.addDataClickListener(new TableDataClickListener<CrimeStationInformation>() {
            @Override
            public void onDataClicked(int rowIndex, CrimeStationInformation clickedData) {
                Intent myintent = new Intent(StatsActivity.this, CrimeDescriptionActivity.class);

                //Pass crime description to the crime description activity
                Bundle bundle = new Bundle();
                bundle.putSerializable("Crime Description",(Serializable) cat.get(rowIndex).getC_description());
                myintent.putExtra("BUNDLE", bundle);
                startActivity(myintent);
            }
        });
    }

    //comparator to sort by frequency
    private static class FreuencyComparator implements Comparator<CrimeStationInformation> {
        @Override
        public int compare(CrimeStationInformation o1, CrimeStationInformation o2) {
            return ((Integer)o1.getFrequency()).compareTo(o2.getFrequency());
        }
    }
}
