package com.example.new_puppy.model;

import com.google.android.gms.maps.model.LatLng;

public class MapLocation {
    private LatLng latLng;
    private String locationName;

    public MapLocation() {
    }

    public MapLocation(LatLng latLng, String locationName) {
        this.latLng = latLng;
        this.locationName = locationName;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }
}
