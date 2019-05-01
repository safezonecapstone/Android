package com.safezone.app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MotionEventCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "MapActivity";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private static final float DEFAULT_ZOOM = 15f;

    //widgets
    private ImageView mGPS;



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
    private ArrayList<TrainInformation> subwayData=new ArrayList<TrainInformation>();

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
                if (!mDestinationAddress.isEmpty()){
                    geoLocate(mDestinationAddress, true);
                }
            }
            else {
                Log.d(TAG, "address_not_null: " + mAddress);
                geoLocate(mAddress, false);
                if (!mDestinationAddress.isEmpty()){
                    geoLocate(mDestinationAddress, true);
                }
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

            init();
        }
    }

    @Override
    protected void onCreate(@androidx.annotation.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mGPS = (ImageView) findViewById(R.id.ic_gps);
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
        else {
            mAddress = intent.getStringExtra("Address");
            mCurrentLatitude = intent.getDoubleExtra("Latitude", 0.0);
            mCurrentLongitude = intent.getDoubleExtra("Longitude", 0.0);
            Log.d(TAG, "address: " + mAddress);
        }
    }

    private void init() {
        Log.d(TAG, "init: initializing");

        getSubways(mCurrentLatitude, mCurrentLongitude);

        mGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked GPS icon");
                getDeviceLocation();
            }
        });
    }

    //Api Request (get nearby stations based of address, returns subway stations)
    private void getSubways(double latitude, double longitude) {
        Log.d(TAG, "getSubways: lat long " + mCurrentLatitude + " " + mCurrentLongitude);
        Log.d(TAG, "getSubways: entered");
        String api_key = getString(R.string.safezone_api_key);
        StringBuilder subways =
                new StringBuilder("https://api-dot-united-triode-233023.appspot.com/api/stations/nearby?");
        subways.append("latitude=").append(latitude);
        subways.append("&longitude=").append(longitude);
        subways.append("&API_KEY=" + api_key);

        String subwaysURL = subways.toString();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, subwaysURL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray result) {

                        Log.i(TAG, "onResponse: Result= " + result.toString());
                        try {
                            subwayData.clear();
                            for (int i = 0; i< result.length();i++) {
                                JSONObject jsonObject = (JSONObject) result.get(i); //Get each object in JSON array
                                String name=jsonObject.getString("name"); //get train station name
                                Log.d(TAG, "Station Name " + name);

                                String line=jsonObject.getString("lines"); //get all the lines in the station
                                Log.d(TAG, "Lines " + line);

                                String percentile=jsonObject.getString("percentile"); //get crime percentile
                                Log.d(TAG, "Percentile " + percentile);

                                double longitude1=jsonObject.getDouble("longitude"); //get coordinates
                                double latitude1=jsonObject.getDouble("latitude");

                                TrainInformation trainInformation2=new TrainInformation(name, "High",  percentile, latitude1, longitude1);
                                String [] eachline=line.split("\"");
                                for(int j=0; j<eachline.length; j++)
                                {
                                    trainInformation2.addTrainStop(eachline[j]);
                                }
                                subwayData.add(trainInformation2); //Add the subways to array
                            }
                            populateListView();
                            Log.d(TAG, "jsonData: happened" + subwayData);
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

    //Populate the view with traininformation
    private void populateListView() {

        LinearLayout linearLayout=(LinearLayout)findViewById(R.id.bottom_sheet);

        TrainInformationAdapter adapter=new TrainInformationAdapter(this, subwayData);

        final ListView listView = (ListView) findViewById(R.id.list_item);
        listView.setAdapter(adapter);

        final BottomSheetBehavior sheetBehavior = BottomSheetBehavior.from(linearLayout);

        // change the state of the bottom sheet
        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);


        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                if (i==BottomSheetBehavior.STATE_EXPANDED){
                    Log.d(TAG, "Expanded");
                }
                else if (i==BottomSheetBehavior.STATE_DRAGGING){
                    if(listIsAtTop(listView)){
                        Log.d(TAG, "Dragging");
                        sheetBehavior.setState(BottomSheetBehavior.STATE_DRAGGING);
                    }
                    else{
                        Log.d(TAG, "Expanded");
                        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }

                }
                else if (i==BottomSheetBehavior.STATE_COLLAPSED){
                    Log.d(TAG, "Collapse");
                }
                else if (i==BottomSheetBehavior.STATE_HALF_EXPANDED){
                    Log.d(TAG, "Half Expanded");
                }
                else if (i==BottomSheetBehavior.STATE_SETTLING){
                    Log.d(TAG, "Settling");
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

        Geocoder geocoder = new Geocoder(MapActivity.this);
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
                moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM, address.getAddressLine(0));
            }
            else {
                Address address = list.get(0);

                mDestinationLatitude = address.getLatitude();
                mDestinationLongitude = address.getLongitude();

                Log.d(TAG, "geoLocate: found a location: " + address.toString());
                //moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM, address.getAddressLine(0));
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
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM, "My Location");
                        }
                        else {
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(MapActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
        catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    private void moveCamera(LatLng latLng, float zoom, String title) {
        Log.d(TAG, "moveCamera: moving camera to: late: " + latLng.latitude + ", lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if(!title.equals("My Location")) {
            MarkerOptions options = new MarkerOptions().position(latLng).title(title);
            mMap.addMarker(options);
        }
    }

    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapActivity.this);
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
        routes.append("&dest_latitude=").append(dest_longitude);
        routes.append("&dest_longitude=").append(dest_longitude);
        routes.append("&API_KEY=" + api_key);

        String routesUrl = routes.toString();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, routesUrl, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray result) {

                        Log.i(TAG, "onResponse: Result= " + result.toString());
                        //*****************This will need to be adjusted to the actual json ***************************
                        /*try {
                            subwayData.clear();
                            for (int i = 0; i< result.length();i++) {
                                JSONObject jsonObject = (JSONObject) result.get(i); //Get each object in JSON array
                                String name=jsonObject.getString("name"); //get train station name
                                Log.d(TAG, "Station Name " + name);

                                String line=jsonObject.getString("lines"); //get all the lines in the station
                                Log.d(TAG, "Lines " + line);

                                String percentile=jsonObject.getString("percentile"); //get crime percentile
                                Log.d(TAG, "Percentile " + percentile);

                                double longitude1=jsonObject.getDouble("longitude"); //get coordinates
                                double latitude1=jsonObject.getDouble("latitude");

                                TrainInformation trainInformation2=new TrainInformation(name, "High",  percentile, latitude1, longitude1);
                                String [] eachline=line.split("\"");
                                for(int j=0; j<eachline.length; j++)
                                {
                                    trainInformation2.addTrainStop(eachline[j]);
                                }
                                subwayData.add(trainInformation2); //Add the subways to array
                            }
                            populateListView();
                            Log.d(TAG, "jsonData: happened" + subwayData);
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG, "getSubways: Error = " + e.getMessage());
                        }*/
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
