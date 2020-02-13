package com.example.richa_764947_androidlab;

import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class GetNearPlaces extends AsyncTask<Object,String,String> {

    String placeData;

    DatabaseHelper mDatabase;
    GoogleMap mMap;
    String locationUrl;


    @Override
    protected String doInBackground(Object... objects) {
        mMap = (GoogleMap) objects[0];
        locationUrl = (String) objects[1];

        GetURL getURL = new GetURL();
        try {
            placeData = getURL.readURL(locationUrl);

        } catch (IOException e) {
            e.printStackTrace();
        }


        //returning object of json
        return placeData;
    }

    @Override
    protected void onPostExecute(String s) {
       List<HashMap<String,String>> naerbyplaceList = null;
       ParseData parseData = new ParseData();
       naerbyplaceList = parseData.parse(s);
       showNearByPlace(naerbyplaceList);
    }

    private void showNearByPlace(List<HashMap<String,String>> nearPlacesList){
        for (int i = 0;i<nearPlacesList.size();i++){
            MarkerOptions options = new MarkerOptions();
            HashMap<String,String> mapPlace = nearPlacesList.get(i);

            final String name = mapPlace.get("placeName");
            final String vicinity = mapPlace.get("vicinity");
            final double lat = Double.parseDouble(mapPlace.get("lat"));
            final double longi = Double.parseDouble(mapPlace.get("lng"));

            LatLng latLng = new LatLng(lat,longi);
            options.position(latLng);

            options.title(name+":"+vicinity);

            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
                    String addDate = simpleDateFormat.format(calendar.getTime());

                    if ( mDatabase.addFavPlace(name, addDate, vicinity, lat, longi)) {

                        System.out.println("printed");
                        //Toast.makeText(, "added", Toast.LENGTH_SHORT).show();

                    } else {

                    }
                    return true;
                }
            });
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            mMap.addMarker(options);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.moveCamera(CameraUpdateFactory.zoomTo(10));



        }
    }
}
