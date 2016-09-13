package com.ysered.savemylocation.map;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ysered.savemylocation.Constants;
import com.ysered.savemylocation.database.DataSource;
import com.ysered.savemylocation.task.SimpleBackgroundTask;
import com.ysered.savemylocation.task.SimpleBackgroundTaskWithResult;
import com.ysered.savemylocation.utils.PreferenceUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class MapPresenter implements MapContract.Presenter, OnMapReadyCallback {

    private final Context mContext;
    private final MapContract.View mMapView;
    private final DataSource mDataSource;
    private final String mCurrentUser;
    private GoogleMap mMap;
    private HashSet<Marker> mMarkers;
    private Marker mCurrentMarker;

    public MapPresenter(Context context, MapContract.View mapView, DataSource dataSource) {
        mContext = context;
        mMapView = mapView;
        mDataSource = dataSource;
        mCurrentUser = PreferenceUtils.getCurrentUser(mContext);
    }

    public void checkLocationPermissions() {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMapView.displayContent();
        } else {
            mMapView.requestPermissions();
        }
    }

    @Override
    public void saveData() {
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        setUpMap(googleMap);
        moveToCurrentPosition(googleMap);
        mMap = googleMap;

        new SimpleBackgroundTaskWithResult<List<LatLng>>(mContext) {
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

    @SuppressWarnings({"MissingPermission"})
    private void setUpMap(GoogleMap map) {
        map.setOnMapClickListener((GoogleMap.OnMapClickListener) mMapView);
        map.setOnMarkerClickListener((GoogleMap.OnMarkerClickListener) mMapView);
        map.setOnMarkerDragListener((GoogleMap.OnMarkerDragListener) mMapView);
        map.setMyLocationEnabled(true);
        map.getUiSettings().setMapToolbarEnabled(false);
    }

    @SuppressWarnings({"MissingPermission"})
    private void moveToCurrentPosition(GoogleMap googleMap) {
        // TODO: initialize once
        final LocationManager locationManager = (LocationManager) mContext.getSystemService(
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
    public void addMarker(LatLng latLng) {
        final Marker marker = mMap.addMarker(new MarkerOptions().position(latLng));
        marker.setDraggable(true);
        mMarkers.add(marker);
    }

    @Override
    public void setCurrentMarker(Marker marker) {
        mCurrentMarker = marker;
    }

    @Override
    public void removeCurrentMarker() {
        if (mCurrentMarker != null) {
            mMarkers.remove(mCurrentMarker);
            mCurrentMarker.remove();
        }
    }

    @Override
    public void logout() {
        PreferenceUtils.clearCurrentUser(mContext);
    }

}
