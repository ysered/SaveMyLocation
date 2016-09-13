package com.ysered.savemylocation.map;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public interface MapContract {

    interface View {

        void requestPermissions();

        void displayContent();

    }

    interface Presenter {

        void checkLocationPermissions();

        void saveData();

        void addMarker(LatLng latLng);

        void setCurrentMarker(Marker marker);

        void removeCurrentMarker();

        void logout();

    }


}
