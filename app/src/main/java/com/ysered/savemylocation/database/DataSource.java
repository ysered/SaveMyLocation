package com.ysered.savemylocation.database;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public interface DataSource {

    void saveUser(String email);

    List<LatLng> getLocations(String user);

    void overrideLocations(String user, List<LatLng> locations);

}
