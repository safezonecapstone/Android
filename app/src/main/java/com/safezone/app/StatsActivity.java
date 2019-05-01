package com.safezone.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
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
import java.util.HashMap;

import androidx.appcompat.app.AppCompatActivity;

public class StatsActivity extends AppCompatActivity {

    private static final String TAG = "StatsActivity";

    private Spinner spinner2;
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
        getCrimes();
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
    private void getCrimes() {
        Log.d(TAG, "getCrimes: lat long " + mCurrentLatitude + " " + mCurrentLongitude);
        Log.d(TAG, "getCrimes: entered");
        String api_key = getString(R.string.safezone_api_key);
        StringBuilder crimes =
                new StringBuilder("https://api-dot-united-triode-233023.appspot.com/api/crimes/nearby?");
        crimes.append("latitude=").append(mCurrentLatitude);
        crimes.append("&longitude=").append(mCurrentLongitude);
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

    private void populateCrimeListView() {

        TextView textView=(TextView) findViewById(R.id.Address);

        textView.setText(address);

        CrimeStationInformationAdapter adapter=new CrimeStationInformationAdapter(this, cat);

        ListView listView = (ListView) findViewById(R.id.crime_item);

        listView.setAdapter(adapter);

        //Pass crime description to next activity so user can view each specific crime
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
                Intent myintent = new Intent(StatsActivity.this, CrimeDescriptionActivity.class);

                Bundle bundle = new Bundle();
                bundle.putSerializable("Crime Description",(Serializable) cat.get(position).getC_description());
                myintent.putExtra("BUNDLE", bundle);
                startActivity(myintent);
            }
        });
    }
}
