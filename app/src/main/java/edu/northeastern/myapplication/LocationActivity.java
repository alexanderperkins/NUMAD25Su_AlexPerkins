package edu.northeastern.myapplication;

import android.util.Log;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

public class LocationActivity extends AppCompatActivity {

    private TextView latitudeTextView;
    private TextView longitudeTextView;
    private TextView distanceTextView;
    private Button resetDistanceButton;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback callback;
    private Location lastLocation;
    private double totalDistance = 0.0;

    private static final int LOCATION_REQUEST_CODE = 1;

    private static final String KEY_TOTAL_DISTANCE = "totalDistance";
    private static final String KEY_LAST_LOCATION = "lastLocation";
    private static final String REQUESTING_LOCATION_UPDATES_KEY = "requestingLocationUpdates";

    private boolean requestingLocationUpdates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        latitudeTextView = findViewById(R.id.latitudeTextView);
        longitudeTextView = findViewById(R.id.longitudeTextView);
        distanceTextView = findViewById(R.id.distanceTextView);
        resetDistanceButton = findViewById(R.id.resetDistanceButton);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        callback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) return;
                for (Location location : locationResult.getLocations()) {
                    Log.d("LOCATION_UPDATE", "New location: " + location.toString());
                    updateLocationUI(location);
                    calculateDistance(location);
                }
            }
        };

        createLocationRequest();

        resetDistanceButton.setOnClickListener(v -> {
            totalDistance = 0.0;
            lastLocation = null;
            distanceTextView.setText(String.format("Total Distance: %.1f m", totalDistance));
        });

        if (savedInstanceState != null) {
            totalDistance = savedInstanceState.getDouble(KEY_TOTAL_DISTANCE, 0.0);
            lastLocation = savedInstanceState.getParcelable(KEY_LAST_LOCATION);
            requestingLocationUpdates = savedInstanceState.getBoolean(REQUESTING_LOCATION_UPDATES_KEY, false);
            distanceTextView.setText(String.format("Total Distance: %.1f m", totalDistance));
        } else {
            requestingLocationUpdates = false;
        }

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                LOCATION_REQUEST_CODE);
    }

    private void createLocationRequest() {
        locationRequest = new LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,10000)
                .setMinUpdateIntervalMillis(5000)
                .build();
    }

    private void updateLocationUI(Location location) {
        if (location != null) {
            latitudeTextView.setText(String.format("Latitude: %.3f", location.getLatitude()));
            longitudeTextView.setText(String.format("Longitude: %.3f", location.getLongitude()));
        } else {
            latitudeTextView.setText("Latitude: N/A");
            longitudeTextView.setText("Longitude: N/A");
        }
    }

    private void calculateDistance(Location newLocation) {
        if (newLocation == null) return;

        if (lastLocation != null) {
            float[] results = new float[1];
            Location.distanceBetween(
                    lastLocation.getLatitude(), lastLocation.getLongitude(),
                    newLocation.getLatitude(), newLocation.getLongitude(),
                    results
            );

            float distanceInMeters = results[0];
            totalDistance += distanceInMeters;
            distanceTextView.setText(String.format("Total Distance: %.1f m", totalDistance));
        }

        lastLocation = newLocation;
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_REQUEST_CODE);
            return;
        }

        requestingLocationUpdates = true;
        fusedLocationClient.requestLocationUpdates(locationRequest, callback, Looper.getMainLooper());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestingLocationUpdates = true;
                startLocationUpdates();
            } else {
                requestingLocationUpdates = false;
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, requestingLocationUpdates);
        outState.putDouble(KEY_TOTAL_DISTANCE, totalDistance);
        if (lastLocation != null) {
            outState.putParcelable(KEY_LAST_LOCATION, lastLocation);
        }
        super.onSaveInstanceState(outState);
    }
}
