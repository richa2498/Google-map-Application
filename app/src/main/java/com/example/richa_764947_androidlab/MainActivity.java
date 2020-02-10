package com.example.richa_764947_androidlab;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap mMap;
    Spinner sp;
    double lat,longi,dest_lat,dest_long;
    final int radious = 1000;
    //get user lopcation

    private FusedLocationProviderClient fusedLocationProviderClient;
    LocationCallback locationCallback;
    LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initMap();
        getUserLocation();

      //sp = findViewById(R.id.spinnr_place);
        if(!checkPermission()){
            requestPermission();
        }else {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper());
            //getUserLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1){
            if (grantResults.length >0  && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                setHomeMarker();
                fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback,Looper.myLooper());
            }
        }
    }

    private boolean checkPermission(){
        int status = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        return  status == PackageManager.PERMISSION_GRANTED;
    }
    private void requestPermission(){
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
    }

    private void initMap(){

        SupportMapFragment mapFragment  = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                Location location = new Location("You Will Be Here Soon");
                location.setLatitude(latLng.latitude);
                location.setLongitude(latLng.longitude);
                dest_lat = latLng.latitude;
                dest_long = latLng.longitude;

                //setMarker
                setMarker(location);
            }
        });
    }
    private void getUserLocation(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10);
        setHomeMarker();

    }

    private void setMarker(Location location){
        LatLng userlatlong = new LatLng(location.getLatitude(),location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(userlatlong).title("Your Destination").snippet("you are going there").draggable(true).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        mMap.addMarker(markerOptions);

    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectordrawableResourse){
        Drawable vectorDrawable = ContextCompat.getDrawable(context,vectordrawableResourse);
        vectorDrawable.setBounds(0,0,vectorDrawable.getIntrinsicWidth(),vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),vectorDrawable.getIntrinsicHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return  BitmapDescriptorFactory.fromBitmap(bitmap);


    }
//    public void btnClick(View view){
//        Object[] dataTransfer;
//        switch (view.getId()){
//
//            case R.id.btn_clear:
////                String url = getUrl(lat,longi,"restaurant");
////                Log.i("tag", "btnClick: " + url);
////                dataTransfer = new Object[2];
////                dataTransfer[0] = mMap;
////                dataTransfer[1] = url;
////                getNearByPlaceData getNearByPlaceData = new getNearByPlaceData();
////                getNearByPlaceData.execute(dataTransfer);
////                Toast.makeText(this, "resturents", Toast.LENGTH_SHORT).show();
//                break;
//
//            case  R.id.btn_distance:
//                String url2 = getDistanceURL();
//                dataTransfer = new Object[3];
//                dataTransfer[0] = mMap;
//                dataTransfer[1] = url2;
//                dataTransfer[2] = new LatLng(dest_lat,dest_long);
//
//                break;
//
//
//        }
//    }




    private String getDistanceURL(){
        //https://maps.googleapis.com/maps/api/directions/json?origin=Disneyland&destination=Universal+Studios+Hollywood&key=
        StringBuilder placeurl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        placeurl.append("origin="+lat+","+longi);
        placeurl.append("&destination="+dest_lat+","+dest_long);
        // placeurl.append("&type:"+nearplace);
        placeurl.append("&key=AIzaSyDK2Du7rvxW4d4NQmKg8qAyxaZ0dGgaY5k");
        // https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=43.647845,-79.3888367&radius=1000$typerestaurant&key=AIzaSyDK2Du7rvxW4d4NQmKg8qAyxaZ0dGgaY5k
        System.out.println(placeurl.toString());
        return placeurl.toString();
    }

    private String getUrl(double lat,double longi,String nearplace){

        StringBuilder placeurl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        placeurl.append("location="+lat+","+longi);
        placeurl.append("&radius="+radious);
        placeurl.append("&type:"+nearplace);
        placeurl.append("&key=AIzaSyCJzqczAn4CG-wEgdlbdAbIxeHGta012rI");
        // https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=43.647845,-79.3888367&radius=1000$typerestaurant&key=AIzaSyDK2Du7rvxW4d4NQmKg8qAyxaZ0dGgaY5k
        System.out.println(placeurl.toString());
        return placeurl.toString();
    }

    private void setHomeMarker(){
        locationCallback = new LocationCallback(){
            // @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()){

                    lat = location.getLatitude();
                    longi = location.getLongitude();
                    LatLng userLoaction = new  LatLng(location.getLatitude(),location.getLongitude());

                    CameraPosition cameraPosition = CameraPosition.builder().target(userLoaction).zoom(15).bearing(0).tilt(45).build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    mMap.addMarker(new MarkerOptions().position(userLoaction).title("Your Location").icon(bitmapDescriptorFromVector(getApplicationContext(),R.drawable.map)));
                }
            }
        };
    }


}
