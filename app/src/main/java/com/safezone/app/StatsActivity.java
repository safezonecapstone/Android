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
        // fills the table
    }

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

    private void getCrimes()
    {
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
                            //Retrieve each frequency with built in json methods

                            for(int i=0; i<category.length; i++)
                            {
                                int frequency=newjsonObject.getInt(category[i]);
                                Log.d(TAG, category[i] + frequency);
                                CrimeStationInformation crimeStationInformation= new CrimeStationInformation(category[i],frequency );
                                cat.add(crimeStationInformation);
                            }

//                            int frequency2=newjsonObject.getInt("Burglary");
//                            Log.d(TAG, "Burglary " + frequency2);
//                            int frequency3=newjsonObject.getInt("Felony Assault");
//                            Log.d(TAG, "Felony Assault " + frequency3);
//                            int frequency4=newjsonObject.getInt("Grand Larceny");
//                            Log.d(TAG, "Grand Larceny " + frequency4);
//                            int frequency5=newjsonObject.getInt("Kidnapping");
//                            Log.d(TAG, "Kidnapping" + frequency5);
//                            int frequency6=newjsonObject.getInt("Misdemeanor Assault");
//                            Log.d(TAG, "Misdemeanor Assault " + frequency6);
//                            int frequency7=newjsonObject.getInt("Misdemeanor Sex Crimes");
//                            Log.d(TAG, "Misdemeanor Sex Crimes " + frequency7);
//                            int frequency8=newjsonObject.getInt("Offenses against Public Order");
//                            Log.d(TAG, "Offenses against Public Order " + frequency8);
//                            int frequency9=newjsonObject.getInt("Petit Larceny");
//                            Log.d(TAG, "Petit Larceny " + frequency9);
//                            int frequency10=newjsonObject.getInt("Rape");
//                            Log.d(TAG, "Rape " + frequency10);
//                            int frequency11=newjsonObject.getInt("Robbery");
//                            Log.d(TAG, "Robbery" + frequency11);
//                            int frequency12=newjsonObject.getInt("Shootings");
//                            Log.d(TAG, "Shootings" + frequency12);
//                            CrimeStationInformation crimeStationInformation= new CrimeStationInformation("Murder",frequency );
//                            CrimeStationInformation crimeStationInformation2= new CrimeStationInformation("Burglary",frequency2 );
//                            CrimeStationInformation crimeStationInformation3= new CrimeStationInformation("Felony Assault",frequency3);
//                            CrimeStationInformation crimeStationInformation4= new CrimeStationInformation("Kidnapping",frequency4);
//                            CrimeStationInformation crimeStationInformation5= new CrimeStationInformation("Misdemeanor Assault",frequency5);
//                            CrimeStationInformation crimeStationInformation6= new CrimeStationInformation("Misdemeanor Sex Crimes",frequency6);
//                            CrimeStationInformation crimeStationInformation7= new CrimeStationInformation("Offenses against Public Order",frequency7);
//                            CrimeStationInformation crimeStationInformation8= new CrimeStationInformation("Petit Larceny",frequency8);
//                            CrimeStationInformation crimeStationInformation9= new CrimeStationInformation("Felony Assault",frequency9);
//                            CrimeStationInformation crimeStationInformation10= new CrimeStationInformation("Rape",frequency10);
//                            CrimeStationInformation crimeStationInformation11= new CrimeStationInformation("Robbery",frequency11);
//                            CrimeStationInformation crimeStationInformation12= new CrimeStationInformation("Shootings",frequency12);
//                            cat.add(crimeStationInformation);
//                            cat.add(crimeStationInformation2);
//                            cat.add(crimeStationInformation3);
//                            cat.add(crimeStationInformation4);
//                            cat.add(crimeStationInformation5);
//                            cat.add(crimeStationInformation6);
//                            cat.add(crimeStationInformation7);
//                            cat.add(crimeStationInformation8);
//                            cat.add(crimeStationInformation9);
//                            cat.add(crimeStationInformation10);
//                            cat.add(crimeStationInformation11);
//                            cat.add(crimeStationInformation12);

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
                            //fillTable(cat);
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

        CrimeStationInformationAdapter adapter=new CrimeStationInformationAdapter(this, cat);

        ListView listView = (ListView) findViewById(R.id.crime_item);

        listView.setAdapter(adapter);

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
