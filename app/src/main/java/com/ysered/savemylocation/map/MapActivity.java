package com.ysered.savemylocation.map;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.ysered.savemylocation.Injector;
import com.ysered.savemylocation.R;
import com.ysered.savemylocation.login.LoginActivity;
import com.ysered.savemylocation.map.MapContract;
import com.ysered.savemylocation.map.MapPresenter;

public class MapActivity extends AppCompatActivity implements
        MapContract.View,
        View.OnClickListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMarkerDragListener {

    private static final int ACCESS_FINE_LOCATION_PERMISSION_REQUEST = 1;

    private MapContract.Presenter mPresenter;
    private ImageButton mRemoveMarkerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Context context = getApplicationContext();
        mPresenter = new MapPresenter(
                context,
                this,
                Injector.provideDataSource(context)
        );
        mPresenter.checkLocationPermissions();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                ACCESS_FINE_LOCATION_PERMISSION_REQUEST);
    }

    @Override
    public void displayContent() {
        setContentView(R.layout.activity_maps);
        mRemoveMarkerButton = (ImageButton) findViewById(R.id.remove_marker_button);
        mRemoveMarkerButton.setOnClickListener(this);
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync((OnMapReadyCallback) mPresenter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == ACCESS_FINE_LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                displayContent();
            } else {
                Toast.makeText(this, "App cannot work without location permission",
                        Toast.LENGTH_LONG).show();
                finish();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPresenter.saveData();
    }

    @Override
    public void onMapClick(LatLng latLng) {
        mPresenter.addMarker(latLng);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (mRemoveMarkerButton.getVisibility() == View.GONE) {
            mRemoveMarkerButton.setVisibility(View.VISIBLE);
        } else {
            mRemoveMarkerButton.setVisibility(View.GONE);
        }
        mPresenter.setCurrentMarker(marker);
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
        if (view.getId() == R.id.remove_marker_button) {
            mPresenter.removeCurrentMarker();
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
            mPresenter.logout();
            final Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
