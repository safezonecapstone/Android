//**************Alex Bortoc and Yi Tong where mentioned**************
package com.safezone.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RouteActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "RouteActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    //vars
    private boolean mLocationPermissionGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private String mAddress;
    private String mDestinationAddress;
    private double mCurrentLatitude;
    private double mCurrentLongitude;
    private double mDestinationLatitude;
    private double mDestinationLongitude;
    private ArrayList<Routes> routesData=new ArrayList<Routes>();

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: called");
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;

        if (mLocationPermissionGranted) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            //Based on whether or not the user left address field empty, to get current location or
            //search for a specific location, will either call getDeviceLocation or geoLocate respectively.
            //Geolocates destination afterwards
            if (mAddress.isEmpty()) {
                Log.d(TAG, "address_null: " + mAddress);
                getDeviceLocation();
                Log.d(TAG, "onMapReady empty: lat long " + mCurrentLatitude + " " + mCurrentLongitude);
                geoLocate(mDestinationAddress, true);
            }
            else {
                Log.d(TAG, "address_not_null: " + mAddress);
                geoLocate(mAddress, false);
                geoLocate(mDestinationAddress, true);
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

            init();
        }
    }

    @Override
    protected void onCreate(@androidx.annotation.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_map);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        getLocationPermission();
        getAddress();
    }

    //Started from onCreate, retrieves strings passed from MainActivity and sets global variables
    private void getAddress() {
        Intent intent = getIntent();
        if (intent.hasExtra("Destination")) {
            mAddress = intent.getStringExtra("Address");
            mCurrentLatitude = intent.getDoubleExtra("Latitude", 0.0);
            mCurrentLongitude = intent.getDoubleExtra("Longitude", 0.0);
            mDestinationAddress = intent.getStringExtra("Destination");
            Log.d(TAG, "address: " + mAddress + " destination: " + mDestinationAddress);
        }
    }

    private void init() {
        Log.d(TAG, "init: initializing");

        getRoutes(mCurrentLatitude, mCurrentLongitude, mDestinationLatitude, mDestinationLongitude);
    }

    //Yi Tong
    //Populate the view with routes
    private void populateListView() {

        LinearLayout linearLayout=(LinearLayout) findViewById(R.id.route_bottom_sheet);

        RoutesAdapter routesAdapter=new RoutesAdapter(this, routesData);

        final ListView listView = (ListView) findViewById(R.id.routes);
        listView.setAdapter(routesAdapter);

        final BottomSheetBehavior sheetBehavior = BottomSheetBehavior.from(linearLayout);

        // change the state of the bottom sheet
        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        sheetBehavior.setPeekHeight(175);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "Clicked and entering");
                Intent myintent = new Intent(RouteActivity.this, InstructionsActivity.class);

                Bundle bundle = new Bundle();
                bundle.putSerializable("Instruction",(Serializable) routesData.get(position).getInstructions());
                myintent.putExtra("BUNDLE", bundle);
                myintent.putExtra("Source", routesData.get(position).getStartingAddress());
                myintent.putExtra("Destination", routesData.get(position).getEndingAddress());
                startActivity(myintent);
            }
        });


        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                if (i==BottomSheetBehavior.STATE_DRAGGING){
                    if(listIsAtTop(listView)){
                        Log.d(TAG, "Dragging");
                        sheetBehavior.setState(BottomSheetBehavior.STATE_DRAGGING);
                    }
                    else{
                        Log.d(TAG, "Expanded");
                        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                }
            }
            @Override
            public void onSlide(@NonNull View view, float v) {
            }
        });
    }

    //Yi Tong
    private boolean listIsAtTop(ListView listView)   {
        if(listView.getChildCount() == 0) return true;
        return listView.getChildAt(0).getTop() == 0;
    }

    //Called from onMapReady. Finds and sets location coordinates based on a string passed from MainActivity.
    //Calls moveCameraRoute to set camera view on the map
    private void geoLocate(String location, boolean destination) {
        Log.d(TAG, "geoLocate: geoLocating");

        String searchString = location;

        Geocoder geocoder = new Geocoder(RouteActivity.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString, 1);
        }
        catch (IOException e) {
            Log.e(TAG, "geoLocate: IOException: " + e.getMessage());
        }

        if(list.size() > 0) {
            if (destination == false) {
                Address address = list.get(0);

                mCurrentLatitude = address.getLatitude();
                mCurrentLongitude = address.getLongitude();

                Log.d(TAG, "geoLocate: found a location: " + address.toString());
            }
            else {
                Address address = list.get(0);

                mDestinationLatitude = address.getLatitude();
                mDestinationLongitude = address.getLongitude();

                Log.d(TAG, "geoLocate: found a location: " + address.toString());
                moveCameraRoute ();
            }
        }
        else {
            Log.d(TAG, "geoLocate: location not found");
            Toast.makeText(RouteActivity.this, "Invalid location, please use a valid address", Toast.LENGTH_LONG).show();
            Intent go_back = new Intent(RouteActivity.this, MainActivity.class);
            startActivity(go_back);
        }
    }
    //Called from onMapReady. Finds user's current location
    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        try {
            if(mLocationPermissionGranted) {
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful() && task.getResult() != null) {
                            Log.d(TAG, "onComplete: found location");
                            Location currentLocation = (Location) task.getResult();
                            Log.d(TAG, "onComplete:location: " + currentLocation.getLatitude() + currentLocation.getLongitude());
                        }
                        else {
                            Log.d(TAG, "onComplete: current location is null");
                        }
                    }
                });
            }
        }
        catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    //Called from geoLocate. Zoomes out map camera to accommodate both origin and destination
    //coordinates and puts markers on the map
    private void moveCameraRoute () {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        LatLng current_loc = new LatLng(mCurrentLatitude, mCurrentLongitude);
        LatLng destination_loc = new LatLng(mDestinationLatitude, mDestinationLongitude);
        builder.include(current_loc);
        builder.include(destination_loc);
        LatLngBounds bounds = builder.build();
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 200));

        MarkerOptions current = new MarkerOptions().position(current_loc).title("Starting address");
        MarkerOptions destination = new MarkerOptions().position(destination_loc);
        mMap.addMarker(current).showInfoWindow();
        mMap.addMarker(destination);// Shows destination marker
    }

    //Map initialization
    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(RouteActivity.this);
    }

    //Location permissions
    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permission");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            initMap();
        }
        else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @androidx.annotation.NonNull String[] permissions, @androidx.annotation.NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called");
        mLocationPermissionGranted = false;

        switch(requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if(grantResults.length > 0) {
                    for(int i = 0; i < grantResults.length; i++) {
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionGranted = true;
                    //initialize map
                    initMap();
                }
            }
        }
    }

    //Alex Bortoc + Yi Tong
    //Called from init function with the argument values set either by geolocating or by getAddress(origin)
    //and geolocating(destination).Retrieves routes information by making an API call to the API server via URL
    private void getRoutes (double origin_latitude, double origin_longitude, double dest_latitude, double dest_longitude) {
        String api_key = getString(R.string.safezone_api_key);
        final StringBuilder routes =
                new StringBuilder("https://api-dot-united-triode-233023.appspot.com/api/route?");
        routes.append("origin_latitude=").append(origin_latitude);
        routes.append("&origin_longitude=").append(origin_longitude);
        routes.append("&dest_latitude=").append(dest_latitude);
        routes.append("&dest_longitude=").append(dest_longitude);
        routes.append("&API_KEY=" + api_key);

        Log.d(TAG, "Source " + origin_latitude + origin_longitude);
        Log.d(TAG, "Destination " + dest_latitude + dest_longitude);

        String routesUrl = routes.toString();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, routesUrl, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray result) {
                        Log.d(TAG, "onResponse: Result= " + result.toString());
                        try {
                            routesData.clear();
                            for (int i = 0; i< result.length(); i++) {
                                JSONObject jsonObject = (JSONObject) result.get(i); //Get each object in JSON array
                                String rating = jsonObject.getString("rating"); //get route rating
                                Log.d(TAG, "Rating: " + rating);

                                String leg = jsonObject.getString("leg");
                                JSONObject LEG=(JSONObject) jsonObject.getJSONObject("leg");
                                Log.d(TAG, "Leg: " + leg);

                                String startingPoint=LEG.getString("start_address"); //Get starting address for route
                                Log.d(TAG, "Start Address: " + startingPoint);

                                String endingPoint=LEG.getString("end_address"); //Get ending address for route
                                Log.d(TAG, "End Address: " + endingPoint);

                                JSONObject duration=LEG.getJSONObject("duration"); //Get duration of the route
                                String durationTime=duration.getString("text");
                                Log.d(TAG, "Duration: " + durationTime);

                                Routes routes=new Routes(startingPoint, endingPoint, rating ,durationTime);

                                JSONArray steps=LEG.getJSONArray("steps"); //Get the instructions for this route
                                Log.d(TAG, steps.toString());
                                for(int j=0; j<steps.length(); j++){
                                    JSONObject eachSteps = (JSONObject) steps.get(j);

                                    if(eachSteps.getString("travel_mode").equals("WALKING")) { //If travel is walking
                                        String instructions=eachSteps.getString("html_instructions"); //Get the instruction
                                        Instructions instructions1=new Instructions("WALKING", instructions);
                                        routes.addInstructions(instructions1);
                                    }
                                    else if(eachSteps.getString("travel_mode").equals("TRANSIT")) { //If travel is transit
                                        JSONObject transit=eachSteps.getJSONObject("transit_details");
                                        String instructions=eachSteps.getString("html_instructions"); //Get instruction
                                        JSONObject subwayLine=transit.getJSONObject("line");
                                        JSONObject arrivalStop=transit.getJSONObject("arrival_stop"); //Get the name of place to stop at
                                        String stopName=arrivalStop.getString("name");
                                        JSONArray agency=subwayLine.getJSONArray("agencies"); //Get transit agencies
                                        String lineName="";
                                        for(int size=0; size<agency.length(); size++){
                                            JSONObject anAgency=(JSONObject)agency.get(size);
                                            Log.d(TAG, anAgency.getString("name"));
                                            if(anAgency.getString("name").equals("Long Island Rail Road")){ //If agency is the LIRR
                                                lineName="LIRR";
                                            }
                                            else if(anAgency.getString("name").equals("MTA New York City Transit")){ //If agency is MTA
                                                lineName=subwayLine.getString("short_name"); //Get the train we take, i.e A or F or 6 etc...
                                            }
                                        }
                                        Instructions instructions1=new Instructions("TRANSIT", instructions); //create that instruction
                                        instructions1.setSubway(lineName);
                                        instructions1.setDestinationStop(stopName);
                                        routes.addInstructions(instructions1);
                                    }
                                }
                                routesData.add(routes);
                            }
                            populateListView();
                            Log.d(TAG, "jsonData: happened" + routesData);
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
        AppController.getInstance().addToRequestQueue(jsonArrayRequest);
    }
}