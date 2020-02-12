package com.example.richa_764947_androidlab;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap mMap;
    double lat, longi, dest_lat, dest_long;
    final int radious = 1000;
    List<Address> addresses;
    String address;
    Geocoder geocoder;
    Location location;
    Spinner maptype;

    DatabaseHelper mDatabase;
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
     mDatabase = new DatabaseHelper(this);
     maptype = findViewById(R.id.choose_map);
        if (!checkPermission()) {
            requestPermission();
        } else {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
            //getUserLocation();
        }

        maptype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (maptype.getSelectedItem() == "Satelite") {
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    Toast.makeText(MainActivity.this, "Satelite", Toast.LENGTH_SHORT).show();


                }
                if (maptype.getSelectedItem() == "Hybride") {
                    mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                    Toast.makeText(MainActivity.this, "Hybride", Toast.LENGTH_SHORT).show();
                } if(maptype.getSelectedItem() == "Default")
                {
                    mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                    Toast.makeText(MainActivity.this, "Terrain", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }


        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setHomeMarker();
                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
            }
        }
    }

    private boolean checkPermission() {
        int status = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        return status == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }

    private void initMap() {

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                location = new Location("You Will Be Here Soon");
                location.setLatitude(latLng.latitude);
                location.setLongitude(latLng.longitude);
                dest_lat = latLng.latitude;
                dest_long = latLng.longitude;
                Toast.makeText(MainActivity.this, "hi", Toast.LENGTH_SHORT).show();
                setMarker(location);
                getAddress(location);

            }
        });
//        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//            @Override
//            public boolean onMarkerClick(Marker marker) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//                builder.setTitle("You want to add it as Favourite?");
//                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                        getAddress(location);
//
//                    }
//                });
//                builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                });
//                AlertDialog alertDialog = builder.create();
//                alertDialog.show();
//                return true;
//
//            }
//        });




    }

    private void getUserLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10);
        setHomeMarker();

    }

    private void getAddress(Location location) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        String addDate = simpleDateFormat.format(calendar.getTime());
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (!addresses.isEmpty()) {
                address = addresses.get(0).getLocality() + " " + addresses.get(0).getAddressLine(0);
                System.out.println(addresses.get(0).getAddressLine(0));


                if (mDatabase.addFavPlace(addresses.get(0).getLocality(),addDate,addresses.get(0).getAddressLine(0),location.getLatitude(),location.getLongitude())){
                    Toast.makeText(MainActivity.this, "plce"+addresses.get(0).getAddressLine(0), Toast.LENGTH_SHORT).show();
                   // System.out.println(mDatabase.getAllPlace().toString());
                   // loadEmployees();
                }else {
                    Toast.makeText(MainActivity.this, "Employee is not addaed", Toast.LENGTH_SHORT).show();
                }
                // Toast.makeText(this, "Address:"+addresses.get(0).getAddressLine(0), Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void setMarker(Location location) {
        LatLng userlatlong = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(userlatlong).title("Your Destination");
       // markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        mMap.addMarker(markerOptions);

    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectordrawableResourse) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectordrawableResourse);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);


    }

    public void btnClick(View view) {
        Object[] dataTransfer = new Object[3];
        GetNearPlaces getNearByPlaceData = new GetNearPlaces();
        switch (view.getId()) {

            case R.id.btn_restaurants:
                mMap.clear();
                String url = getUrl(lat, longi, "restaurant");
                Log.i("tag", "btnClick: " + url);
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;
                dataTransfer[2] = "resturent";
                getNearByPlaceData.execute(dataTransfer);
                Toast.makeText(this, "restaurant", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btn_museum:
                // mMap.clear();
                url = getUrl(lat, longi, "museum");
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;
                dataTransfer[2] = "museum";
                getNearByPlaceData.execute(dataTransfer);
                Toast.makeText(this, "museum", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btn_cafe:
                //mMap.clear();
//                url = getUrl(lat,longi,"cafe");
//                dataTransfer[0] = mMap;
//                dataTransfer[1] = url;
//                dataTransfer[2] = "cafe";
//                getNearByPlaceData.execute(dataTransfer);
//                Toast.makeText(this, "cafe", Toast.LENGTH_SHORT).show();
//                break;

                Intent intent = new Intent(this, FavPlaces.class);
                startActivity(intent);

        }
    }


    private String getDistanceURL() {
        //https://maps.googleapis.com/maps/api/directions/json?origin=Disneyland&destination=Universal+Studios+Hollywood&key=
        StringBuilder placeurl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        placeurl.append("origin=" + lat + "," + longi);
        placeurl.append("&destination=" + dest_lat + "," + dest_long);
        // placeurl.append("&type:"+nearplace);
        // placeurl.append("&key=AIzaSyCJzqczAn4CG-wEgdlbdAbIxeHGta012rI");
        // https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=43.647845,-79.3888367&radius=1000$typerestaurant&key=AIzaSyDK2Du7rvxW4d4NQmKg8qAyxaZ0dGgaY5k
        // System.out.println(placeurl.toString());
        return placeurl.toString();
    }

    private String getUrl(double lat, double longi, String nearplace) {

        StringBuilder placeurl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        placeurl.append("location=" + lat + "," + longi);
        placeurl.append("&radius=" + radious);
        placeurl.append("&type:" + nearplace);
        placeurl.append("&key=AIzaSyDjxAVT7FnqkR8vyPxMIwzRSVoQHDtOab4");
        // https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=43.647845,-79.3888367&radius=1000$typerestaurant&key=AIzaSyDK2Du7rvxW4d4NQmKg8qAyxaZ0dGgaY5k
        //System.out.println(placeurl.toString());
        return placeurl.toString();
    }

    private void setHomeMarker() {
        locationCallback = new LocationCallback() {
            // @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {

                    lat = location.getLatitude();
                    longi = location.getLongitude();
                    LatLng userLoaction = new LatLng(location.getLatitude(), location.getLongitude());

                    CameraPosition cameraPosition = CameraPosition.builder().target(userLoaction).zoom(15).bearing(0).tilt(45).build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    mMap.addMarker(new MarkerOptions().position(userLoaction).title("Your Location").icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.map)));
                }
            }
        };
    }


    private void loadPlaces() {
        Cursor cursor = mDatabase.getAllPlace();
        if (cursor.moveToFirst()){

            do {
                System.out.println(cursor.getString(1));



            }while (cursor.moveToNext());

            cursor.close();
        }

    }

    }

