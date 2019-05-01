package com.safezone.app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    //widgets
    private EditText mAddress;
    private EditText mDestinationAddress;
    private Button mNearby;
    private Button mRoutes;

    //vars
    private double mLat;
    private double mLng;
    LocationManager mLocationManager;
    LocationListener mLocationListener;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mCurrentLocation;
    private boolean mLocationPermissionGranted = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAddress = (EditText) findViewById(R.id.input_search_main);
        mDestinationAddress = (EditText) findViewById(R.id.input_search_main2);
        mNearby = (Button) findViewById(R.id.buttonNearby);
        mRoutes = (Button) findViewById(R.id.buttonRoutes);

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showLocationBar();
        }

        // Define a listener that responds to location updates
        mLocationListener = new LocationListener() {

            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.

                if (location != null) {
                    mLat = location.getLatitude();
                    mLng = location.getLongitude();
                    Log.d(TAG, "onLocationChanged: mLat mLng" + mLat + " " + mLng);

                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0, mLocationListener);
            mLocationPermissionGranted = true;
        }
        else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }

        if(isServicesOK()) {
            init();
        }
    }

    @SuppressLint("WrongConstant")
    private void showLocationBar() {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.main_activity), "Location disabled", Snackbar.LENGTH_LONG)
                .setAction("Enable", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        displayLocationSettingsRequest(getApplicationContext());
                    }
                });
        snackbar.setActionTextColor(Color.WHITE);
        snackbar.setDuration(Snackbar.LENGTH_INDEFINITE);

        View snackView = snackbar.getView();
        TextView textView = (TextView) snackView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);

        snackbar.show();
    }

    private void displayLocationSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i(TAG, "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }

    private void init() {
        /*mAddress.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        event.getAction() == KeyEvent.ACTION_DOWN ||
                        event.getAction() == KeyEvent.KEYCODE_ENTER) {
                    //start MapActivity
                    getCurrentLocation();
                }
                return false;
            }
        });*/
        mNearby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocation(true);
            }
        });
        mRoutes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocation(false);
            }
        });
    }

    private void getCurrentLocation(boolean nearby) {
        if (nearby == true) {
            try {
                if(mLocationPermissionGranted) {
                    Task<Location> task = mFusedLocationProviderClient.getLastLocation();
                    task.addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                mCurrentLocation = location;
                                startNearbySearch(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                            }
                            else {
                                Log.d(TAG, "getCurrentLocation: can't get a location");
                            }
                        }
                    });
                }
            }
            catch (SecurityException e) {
                Log.e(TAG, "getCurrentLocation: SecurityException: " + e.getMessage());
            }
        }
        else {
            try {
                if(mLocationPermissionGranted) {
                    Task<Location> task = mFusedLocationProviderClient.getLastLocation();
                    task.addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                mCurrentLocation = location;
                                startRoutesSearch(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                            }
                            else {
                                Log.d(TAG, "getCurrentLocation: can't get a location");
                            }
                        }
                    });
                }
            }
            catch (SecurityException e) {
                Log.e(TAG, "getCurrentLocation: SecurityException: " + e.getMessage());
            }
        }
    }

    private void startNearbySearch(double lat, double lng) {
        Intent intent = new Intent(MainActivity.this, MapActivity.class);
        String address = mAddress.getText().toString();
        intent.putExtra("Address", address);
        intent.putExtra("Latitude", lat);
        intent.putExtra("Longitude", lng);
        startActivity(intent);
    }

    private void startRoutesSearch(double lat, double lng) {
        Intent intent = new Intent(MainActivity.this, MapActivity.class);
        String address = mAddress.getText().toString();
        String destination_address = mDestinationAddress.getText().toString();
        intent.putExtra("Address", address);
        intent.putExtra("Latitude", lat);
        intent.putExtra("Longitude", lng);
        intent.putExtra("Destination", destination_address);
        startActivity(intent);
    }

    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if(available == ConnectionResult.SUCCESS) {
            //everything is fine
            Log.d(TAG, "isServicesOK: Google Play services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //error can be resolved
            Log.d(TAG, "isServicesOK: an error occurred but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }

        return false;
    }
}
