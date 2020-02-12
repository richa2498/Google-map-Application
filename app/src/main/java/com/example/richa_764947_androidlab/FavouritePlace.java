package com.example.richa_764947_androidlab;

public class FavouritePlace {

    int id;
    String placeName;
    String address;
    String date;
    Double latitude;
    Double longitude;


    public FavouritePlace(int id, String placeName, String address, String date, Double latitude, Double longitude) {
        this.id = id;
        this.placeName = placeName;
        this.address = address;
        this.date = date;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getId() {
        return id;
    }

    public String getPlaceName() {
        return placeName;
    }

    public String getAddress() {
        return address;
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
