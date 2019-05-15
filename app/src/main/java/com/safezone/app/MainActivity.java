//**************Alex Bortoc**************
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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

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
    private ImageView mHelp;
    private TextView mSearchFieldHelp;
    private TextView mNearbyButtonHelp;
    private TextView mRoutesButtonHelp;
    private TextView mTradeMark;

    //vars
    private double mLat;
    private double mLng;
    LocationManager mLocationManager;
    LocationListener mLocationListener;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mCurrentLocation;
    private boolean mLocationPermissionGranted = false;
    private boolean mClicked = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAddress = (EditText) findViewById(R.id.input_search_main);
        mDestinationAddress = (EditText) findViewById(R.id.input_search_main2);
        mNearby = (Button) findViewById(R.id.buttonNearby);
        mRoutes = (Button) findViewById(R.id.buttonRoutes);
        mHelp = (ImageView) findViewById(R.id.ic_help);
        mSearchFieldHelp = (TextView) findViewById(R.id.textview_help_edittext);
        mNearbyButtonHelp = (TextView) findViewById(R.id.textview_help_button_nearby);
        mRoutesButtonHelp = (TextView) findViewById(R.id.textview_help_button_routes);
        mTradeMark = (TextView) findViewById(R.id.trade_mark);

        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

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
            if(isServicesOK()) {
                init();
            }
        }
        else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    //Called from onCreate. On fresh install, when permissions have not been granted yet, this function
    //will track whether or not the user agreed to grant them. If the user declines, buttons will not
    //function, otherwise the app will be initialized and become functional.
    @Override
    public void onRequestPermissionsResult(int requestCode, @androidx.annotation.NonNull String[] permissions,
                                           @androidx.annotation.NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called");

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
                    //run init function
                    if(isServicesOK()) {
                        init();
                    }
                }
            }
        }
    }

    //Snackbar appears on the screen if the location is not enabled. Allows the user to click on text
    //"Enable" to bring up a dialog to turn on location services.
    @SuppressLint("WrongConstant")
    private void showLocationBar() {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.main_activity), "Location disabled", Snackbar.LENGTH_LONG)
                .setAction("Enable", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        displayLocationSettingsRequest();
                    }
                });
        snackbar.setActionTextColor(Color.WHITE);
        snackbar.setDuration(Snackbar.LENGTH_INDEFINITE);

        View snackView = snackbar.getView();
        TextView textView = (TextView) snackView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);

        snackbar.show();
    }

    //Called from showLocationBar() to bring up the location services dialog. Allows the user to turn
    //on location services from within the app
    private void displayLocationSettingsRequest() {

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> results = LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());
        results.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                }
                catch (ApiException exception) {
                    switch (exception.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                resolvable.startResolutionForResult(MainActivity.this,
                                        REQUEST_CHECK_SETTINGS);
                            }
                            catch (IntentSender.SendIntentException e) {
                                //Error ignored
                            }
                            catch (ClassCastException e) {
                                //Error ignored
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            //Can't fix location settings
                            break;
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        Toast.makeText(MainActivity.this, "Sorry, but the " +
                                "application requires location services",Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    private void init() {
        mNearby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocation(true);
            }
        });

        mRoutes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDestinationAddress.getText().toString().trim().length() > 0) {
                    getCurrentLocation(false);
                }
                else {
                    Toast.makeText(MainActivity.this, "Destination cannot be empty", Toast.LENGTH_LONG).show();
                }
            }
        });

        //Help button controls visibility of textviews with helpful information
        mHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mClicked) {
                    mSearchFieldHelp.setVisibility(View.VISIBLE);
                    mNearbyButtonHelp.setVisibility(View.VISIBLE);
                    mRoutesButtonHelp.setVisibility(View.VISIBLE);
                    mTradeMark.setVisibility(View.VISIBLE);
                    mClicked = true;
                }
                else if (mClicked) {
                    mSearchFieldHelp.setVisibility(View.INVISIBLE);
                    mNearbyButtonHelp.setVisibility(View.INVISIBLE);
                    mRoutesButtonHelp.setVisibility(View.INVISIBLE);
                    mTradeMark.setVisibility(View.INVISIBLE);
                    mClicked = false;
                }
            }
        });
    }

    //Buttons nearby and routes call this function and based on the boolean value provided in the
    //argument will launch either startNearbySearch or startRoutesSearch. Function attempts to get
    //current location and passes its lat and lng on to the functions mentioned above.
    private void getCurrentLocation(boolean nearby) {
        if (nearby) {
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
                                Log.d(TAG, "getCurrentLocation: can't get a location from fused");
                                startNearbySearch(mLat, mLng);
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
                                Log.d(TAG, "getCurrentLocation: can't get a location from fused");
                                startRoutesSearch(mLat, mLng);
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

    //Called from getCurrentLocation to initiate the MapActivity. Passes address, lat and lng strings
    //to MapActivity, which are necessary for API calls and geolocation
    private void startNearbySearch(double lat, double lng) {
        Intent intent = new Intent(MainActivity.this, MapActivity.class);
        String address = mAddress.getText().toString();
        intent.putExtra("Address", address);
        intent.putExtra("Latitude", lat);
        intent.putExtra("Longitude", lng);
        startActivity(intent);
    }

    //Called from getCurrentLocation to initiate the RouteActivity. Passes address, lat, lng, and
    //destination strings to RouteActivity, which are necessary for API calls and geolocation
    private void startRoutesSearch(double lat, double lng) {
        Intent intent = new Intent(MainActivity.this, RouteActivity.class);
        String address = mAddress.getText().toString();
        String destination_address = mDestinationAddress.getText().toString();
        intent.putExtra("Address", address);
        intent.putExtra("Latitude", lat);
        intent.putExtra("Longitude", lng);
        intent.putExtra("Destination", destination_address);
        startActivity(intent);
    }

    //Checks for google play services
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
