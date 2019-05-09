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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.google.android.gms.maps.model.Marker;
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

public class RouteMap extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "RouteMap";

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

    //Get address being passed from one previous activity to another (intent)
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
                Intent myintent = new Intent(RouteMap.this, InstructionsActivity.class);

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

    private boolean listIsAtTop(ListView listView)   {
        if(listView.getChildCount() == 0) return true;
        return listView.getChildAt(0).getTop() == 0;
    }

    private void geoLocate(String location, boolean destination) {
        Log.d(TAG, "geoLocate: geoLocating");

        String searchString = location;

        Geocoder geocoder = new Geocoder(RouteMap.this);
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
    }

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
                            Toast.makeText(RouteMap.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
        catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    private void moveCameraRoute () {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        LatLng current_loc = new LatLng(mCurrentLatitude, mCurrentLongitude);
        LatLng destination_loc = new LatLng(mDestinationLatitude, mDestinationLongitude);
        builder.include(current_loc);
        builder.include(destination_loc);
        LatLngBounds bounds = builder.build();
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 200));

        MarkerOptions current = new MarkerOptions().position(current_loc);
        MarkerOptions destination = new MarkerOptions().position(destination_loc);
        mMap.addMarker(current);
        mMap.addMarker(destination);
    }

    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(RouteMap.this);
    }

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

    private void getRoutes (double origin_latitude, double origin_longitude,
                            double dest_latitude, double dest_longitude) {
        String api_key = getString(R.string.safezone_api_key);
        StringBuilder routes =
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
                        //*****************This will need to be adjusted to the actual json ***************************
                        try {
                            routesData.clear();
                            for (int i = 0; i< result.length(); i++) {
                                JSONObject jsonObject = (JSONObject) result.get(i); //Get each object in JSON array
                                String rating = jsonObject.getString("rating"); //get train station name
                                Log.d(TAG, "Rating: " + rating);
//
                                String leg = jsonObject.getString("leg");
                                JSONObject LEG=(JSONObject) jsonObject.getJSONObject("leg");
                                Log.d(TAG, "Leg: " + leg);

                                String startingPoint=LEG.getString("start_address");
                                Log.d(TAG, "Start Address: " + startingPoint);

                                String endingPoint=LEG.getString("end_address");
                                Log.d(TAG, "End Address: " + endingPoint);

                                JSONObject duration=LEG.getJSONObject("duration");
                                String durationTime=duration.getString("text");
                                Log.d(TAG, "Duration: " + durationTime);

                                Routes routes=new Routes(startingPoint, endingPoint, rating ,durationTime);
                                routesData.add(routes);

                                JSONArray steps=LEG.getJSONArray("steps");
                                Log.d(TAG, steps.toString());
                                for(int j=0; j<steps.length(); j++){
                                    JSONObject eachSteps = (JSONObject) steps.get(j);

                                    if(eachSteps.getString("travel_mode").equals("WALKING")) {
                                        String instructions=eachSteps.getString("html_instructions");
                                        Instructions instructions1=new Instructions("WALKING", instructions);
                                        routesData.get(i).addInstructions(instructions1);

                                    }
                                    else if(eachSteps.getString("travel_mode").equals("TRANSIT")) {
                                        JSONObject transit=eachSteps.getJSONObject("transit_details");
                                        JSONObject subwayLine=transit.getJSONObject("line");
                                        String lineName=subwayLine.getString("short_name");
                                        Instructions instructions1=new Instructions("TRANSIT", lineName);
                                        routesData.get(i).addInstructions(instructions1);
                                    }
                                }
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