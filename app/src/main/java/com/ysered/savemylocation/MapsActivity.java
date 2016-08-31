package com.ysered.savemylocation;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ysered.savemylocation.database.DataSource;
import com.ysered.savemylocation.database.SqliteDataSource;
import com.ysered.savemylocation.task.SimpleBackgroundTask;
import com.ysered.savemylocation.task.SimpleBackgroundTaskWithResult;
import com.ysered.savemylocation.utils.PreferenceUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements
        View.OnClickListener,
        OnMapReadyCallback,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMarkerDragListener {

    private static final int ACCESS_FINE_LOCATION_PERMISSION_REQUEST = 1;

    private GoogleMap mMap;
    private ImageButton mRemoveMarkerButton;

    private String mCurrentUser;
    private Marker mCurrentMarker;
    private HashSet<Marker> mMarkers;
    private DataSource mDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            initialize();
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATION_PERMISSION_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == ACCESS_FINE_LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initialize();
            } else {
                Toast.makeText(this, "App cannot work without location permission",
                        Toast.LENGTH_LONG).show();
                finish();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void initialize() {
        mCurrentUser = PreferenceUtils.getCurrentUser(this);

        setContentView(R.layout.activity_maps);
        mRemoveMarkerButton = (ImageButton) findViewById(R.id.remove_marker_button);
        mRemoveMarkerButton.setOnClickListener(this);
        mDataSource = new SqliteDataSource(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        setUpMap(googleMap);
        moveToCurrentPosition(googleMap);
        mMap = googleMap;

        new SimpleBackgroundTaskWithResult<List<LatLng>>(this) {
            @Override
            protected List<LatLng> onRun() {
                return mDataSource.getLocations(mCurrentUser);
            }

            @Override
            protected void onSuccess(List<LatLng> result) {
                mMarkers = new HashSet<>();
                for (LatLng latLng : result) {
                    final Marker marker = mMap.addMarker(new MarkerOptions().position(latLng));
                    marker.setDraggable(true);
                    mMarkers.add(marker);
                }
            }
        }.execute();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mMarkers != null) {
            final List<LatLng> locations = new ArrayList<>(mMarkers.size());
            for (Marker marker : mMarkers) {
                locations.add(marker.getPosition());
            }
            new SimpleBackgroundTask() {
                @Override
                protected Void onRun() {
                    mDataSource.overrideLocations(mCurrentUser, locations);
                    return null;
                }
            }.execute();
        }
    }

    @SuppressWarnings({"MissingPermission"})
    private void setUpMap(GoogleMap map) {
        map.setOnMapClickListener(this);
        map.setOnMarkerClickListener(this);
        map.setOnMarkerDragListener(this);
        map.setMyLocationEnabled(true);
        map.getUiSettings().setMapToolbarEnabled(false);
    }

    @SuppressWarnings({"MissingPermission"})
    private void moveToCurrentPosition(GoogleMap googleMap) {
        final LocationManager locationManager = (LocationManager) getSystemService(
                Context.LOCATION_SERVICE);
        final Criteria criteria = new Criteria();
        final Location location = locationManager.getLastKnownLocation(
                locationManager.getBestProvider(criteria, false));
        final LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(current));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(current,
                Constants.DEFAULT_CAMERA_ZOOM));
    }

    @Override
    public void onMapClick(LatLng latLng) {
        final Marker marker = mMap.addMarker(new MarkerOptions().position(latLng));
        marker.setDraggable(true);
        mMarkers.add(marker);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (mRemoveMarkerButton.getVisibility() == View.GONE) {
            mRemoveMarkerButton.setVisibility(View.VISIBLE);
        } else {
            mRemoveMarkerButton.setVisibility(View.GONE);
        }
        mCurrentMarker = marker;
        return true;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
    }

    @Override
    public void onMarkerDrag(Marker marker) {
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.remove_marker_button && mCurrentMarker != null) {
            mMarkers.remove(mCurrentMarker);
            mCurrentMarker.remove();
            mRemoveMarkerButton.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item_logout) {
            PreferenceUtils.clearCurrentUser(this);
            final Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
