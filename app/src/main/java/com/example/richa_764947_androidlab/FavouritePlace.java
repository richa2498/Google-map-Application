package com.example.richa_764947_androidlab;

public class FavouritePlace {

    String placeName;
    String date;
    Double latitude;
    Double longitude;


    public FavouritePlace(String placeName, String date, Double latitude, Double longitude) {
        this.placeName = placeName;
        this.date = date;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getPlaceName() {
        return placeName;
    }

    public String getDate() {
        return date;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}
