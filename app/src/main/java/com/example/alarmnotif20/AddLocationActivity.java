package com.example.alarmnotif20;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AddLocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    static final String TAG = "AddLocationActivity";
    static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";

    MapView mapView;
    GoogleMap gmap;

    EditText taskNameInput;
    EditText taskDescInput;

    Button addBtn;
    Button cancelBtn;

    ImageView myLocationBtn;
    ImageView searchImageView;

    EditText searchInput;

    FusedLocationProviderClient fusedLocationProviderClient;

    double latitude;
    double longitude;

    Marker marker;

    GeofencingClient geofencingClient;
    LocationManager mLocationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        mapView = findViewById(R.id.mapView);

        searchInput = findViewById(R.id.locationSearchInput);
        taskNameInput = findViewById(R.id.taskNameInput);
        taskDescInput = findViewById(R.id.taskDescInput);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        } else {
            mapViewBundle = new Bundle();
        }

        //fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        geofencingClient = LocationServices.getGeofencingClient(this);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

        addBtn = findViewById(R.id.addLocationBtn);
        cancelBtn = findViewById(R.id.cancelBtn);

        myLocationBtn = findViewById(R.id.myLocationBtn);
        searchImageView = findViewById(R.id.searchImageView);

        latitude = getLastKnownLocation().getLatitude();
        longitude = getLastKnownLocation().getLongitude();

        myLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                latitude = getLastKnownLocation().getLatitude();
                longitude = getLastKnownLocation().getLongitude();

                LatLng latLng = new LatLng(latitude, longitude);
                marker.setPosition(latLng);

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(latLng)
                        .zoom(12)
                        .build();

                CameraUpdate cu = CameraUpdateFactory.newCameraPosition(cameraPosition);
                gmap.animateCamera(cu, 512, null);
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (taskNameInput.getText().toString().equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddLocationActivity.this);

                    builder.setTitle("Enter a task title")
                            .setMessage("In order to create a task, it must have a title")
                            .setPositiveButton("Okay", null)
                            .create()
                            .show();
                } else {
                    if (taskDescInput.getText().toString().equals("")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(AddLocationActivity.this);

                        builder.setTitle("No task description!")
                                .setMessage("You can create a task without a description, but it helps to have one!")
                                .setPositiveButton("Create anyway", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        createLocationTask();
                                    }
                                })
                                .setNegativeButton("Write description", null)
                                .create()
                                .show();
                    } else {
                        createLocationTask();
                    }
                }
            }
        });

        searchImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                geolocate();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        if (ActivityCompat.checkSelfPermission(AddLocationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(AddLocationActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AddLocationActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        if (latitude == 0 && longitude == 0) {
            finish();
            startActivity(getIntent());
        }

        gmap = googleMap;
        gmap.setMinZoomPreference(12);
        gmap.setMaxZoomPreference(30);

        final UiSettings uiSettings = gmap.getUiSettings();
        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setCompassEnabled(true);
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setZoomGesturesEnabled(true);
        uiSettings.setRotateGesturesEnabled(false);

        init();

        LatLng ny = new LatLng(latitude, longitude);

        final MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(ny);

        marker = gmap.addMarker(markerOptions);

        gmap.moveCamera(CameraUpdateFactory.newLatLng(ny));

        Log.d(TAG, "onMapReady: latitude: " + latitude + "\nlongitude: " + longitude);

        gmap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (marker != null) {
                    marker.setPosition(latLng);

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(latLng)
                            .zoom(gmap.getCameraPosition().zoom)
                            .build();

                    CameraUpdate cu = CameraUpdateFactory.newCameraPosition(cameraPosition);
                    gmap.animateCamera(cu, 512, null);

                    Log.d(TAG, "onMapClick: Address: " + getAddress(latLng));
                }
            }
        });
    }

    private void createLocationTask() {
        LatLng latLng = marker.getPosition();

        LocationTask task =
                new LocationTask(taskNameInput.getText().toString(),
                        taskDescInput.getText().toString(),
                        latLng.latitude,
                        latLng.longitude);

        long addedId = MyDatabase.getDatabase(getApplicationContext()).locationDAO().addLocationTask(task);

        //Log.d(TAG, "onClick: address: \n" + task.toString());

        addLocationAlert((int) addedId);

        finish();
        Intent backIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(backIntent);
    }

    @SuppressLint("MissingPermission")
    private void addLocationAlert(int taskId) {
        double lat = marker.getPosition().latitude;
        double lng = marker.getPosition().longitude;

        Geofence geofence = getGeofence(lat, lng, taskId + "");


        geofencingClient.addGeofences(getGeofencingRequest(geofence),
                createGeofencePendingIntent(taskId))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(AddLocationActivity.this,
                                    "Location reminder added!",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AddLocationActivity.this,
                                    "Location alter could not be added",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private PendingIntent createGeofencePendingIntent(int taskId) {
        Intent intent = new Intent(this, GeofenceReceiver.class);
        intent.putExtra("taskId", taskId);

        return PendingIntent.getBroadcast(this, taskId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent getGeofencePendingIntent(int taskId) {
        Intent intent = new Intent(this, LocationAlertIntentService.class);
        intent.putExtra("taskId", taskId);

        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private GeofencingRequest getGeofencingRequest(Geofence geofence) {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL | GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofence(geofence);
        return builder.build();
    }

    private Geofence getGeofence(double lat, double lang, String key) {
        return new Geofence.Builder()
                .setRequestId(key)
                .setCircularRegion(lat, lang, 200)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_DWELL)
                .setLoiteringDelay(100)
                .build();
    }

    private void init() {
        Log.d(TAG, "init: initializing");

        searchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        event.getAction() == KeyEvent.ACTION_DOWN ||
                        event.getAction() == KeyEvent.KEYCODE_ENTER) {
                    geolocate();
                }

                return false;
            }
        });
    }

    private void geolocate() {
        Log.d(TAG, "geolocate: geolocating...");

        String searchString = searchInput.getText().toString();

        Geocoder geocoder = new Geocoder(getApplicationContext());

        List<Address> addressList = new ArrayList<>();

        try {
            addressList = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
            Log.e(TAG, "geolocate: IOException: " + e.getMessage());
        }

        if (addressList.size() > 0) {
            Address address = addressList.get(0);

            latitude = address.getLatitude();
            longitude = address.getLongitude();
            LatLng lng = new LatLng(latitude, longitude);

            gmap.moveCamera(CameraUpdateFactory.newLatLng(lng));
            marker.setPosition(lng);

            Log.d(TAG, "geolocate: Address found: " + address.toString());
        }
    }

    private Location getLastKnownLocation() {
        mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;

        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return null;
            }
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }
        return bestLocation;
    }


    private String getAddress(LatLng latLng) {

        StringBuilder result = new StringBuilder();
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                //result.append(address.getSubThoroughfare() + ", ");
                //result.append(address.getThoroughfare());
                result.append(address.getAddressLine(0));
            }
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }

        Log.d(TAG, "getAddress: \n" + result.toString());

        return result.toString();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
