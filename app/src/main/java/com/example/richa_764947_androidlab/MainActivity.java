package com.example.richa_764947_androidlab;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.annotation.WorkerThread;
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
import android.view.Menu;
import android.view.MenuItem;
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
    Spinner maptype;
    Location location;
    boolean isMrkerClick = false;

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
//        maptype = findViewById(R.id.choose_map);
//        maptype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//                switch (position){
//                    case 0:
//                        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
//                       Toast.makeText(MainActivity.this, "Satelite", Toast.LENGTH_SHORT).show();
//                       break;
//                    case 1:
//                        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
//                        Toast.makeText(MainActivity.this, "Terrrain", Toast.LENGTH_SHORT).show();
//                        break;
//                    case 2:
//                        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
//                        Toast.makeText(MainActivity.this, "Hybride", Toast.LENGTH_SHORT).show();
//                        break;
//                    default:
//                        mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
//                        Toast.makeText(MainActivity.this, "none", Toast.LENGTH_SHORT).show();
//                        break;
//
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//                mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
//            }
//
//
//        });

        getUserLocation();
        mDatabase = new DatabaseHelper(this);

        if (!checkPermission()) {
            requestPermission();
        } else {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
            //getUserLocation();
        }



    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map_type, menu);
        return super.onCreateOptionsMenu(menu);
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
       // mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                builder1.setMessage("You want to add this place as Favourite?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //dialog.cancel();
                                try {
                                    isMrkerClick = true;
                                    getAddress(location);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                isMrkerClick = false;
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();

                return true;

            }
        });
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

            @Override
            public void onMapLongClick(LatLng latLng) {

                 location = new Location("You Will Be Here Soon");
                location.setLatitude(latLng.latitude);
                location.setLongitude(latLng.longitude);
                dest_lat = latLng.latitude;
                dest_long = latLng.longitude;
                setMarker(location);
                try {
                    getAddress(location);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });





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

    private void getAddress(Location location) throws IOException {
        System.out.println("In Get Address");
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        String addDate = simpleDateFormat.format(calendar.getTime());

        geocoder = new Geocoder(this, Locale.getDefault());

        System.out.println("in geocoder");

            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            if (!addresses.isEmpty()) {
                address = addresses.get(0).getLocality() + " " + addresses.get(0).getAddressLine(0);
                System.out.println(addresses.get(0).getAddressLine(0));

                if (isMrkerClick  && mDatabase.addFavPlace(addresses.get(0).getLocality(),addDate,addresses.get(0).getAddressLine(0),location.getLatitude(),location.getLongitude())){
                    isMrkerClick = false;
                    Toast.makeText(MainActivity.this, "added", Toast.LENGTH_SHORT).show();
                    loadPlaces();
                }else {
                   // Toast.makeText(MainActivity.this, "Employee is not addaed", Toast.LENGTH_SHORT).show();
                }
                 //Toast.makeText(this, "Address:"+addresses.get(0).getAddressLine(0), Toast.LENGTH_SHORT).show();
            }


        }



    private void setMarker(Location location) {
        System.out.println("In SetMarker");
        LatLng userlatlong = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(userlatlong).title("Your Destination");
        markerOptions.draggable(true);
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

        return placeurl.toString();
    }

    private String getUrl(double lat, double longi, String nearplace) {

        StringBuilder placeurl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        placeurl.append("location=" + lat + "," + longi);
        placeurl.append("&radius=" + radious);
        placeurl.append("&type:" + nearplace);
        placeurl.append("&key=AIzaSyDMSqImPXApQdSZ43EqemUgq2m1U_GfYok");
        // https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=43.647845,-79.3888367&radius=1000$typerestaurant&key=AIzaSyDK2Du7rvxW4d4NQmKg8qAyxaZ0dGgaY5k
        System.out.println(placeurl.toString());
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.maptypeHYBRID:
                if(mMap != null){
                    mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                    return true;
                }
            case R.id.maptypeNONE:
                if(mMap != null){
                    mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                    return true;
                }
            case R.id.maptypeNORMAL:
                if(mMap != null){
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    return true;
                }
            case R.id.maptypeSATELLITE:
                if(mMap != null){
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    return true;
                }
            case R.id.maptypeTERRAIN:
                if(mMap != null){
                    mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                    return true;
                }


                return true;
        }
        return super.onOptionsItemSelected(item);
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

