package com.example.richa_764947_androidlab;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DurationAndDistance extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap mMap;
    double lat, longi, dest_lat, dest_long;
    final int radious = 1000;
    boolean isclicked = false;
    Location homelocation;
    List<Location> points;

    public static boolean directionRequested;
    private FusedLocationProviderClient fusedLocationProviderClient;
    LocationCallback locationCallback;
    LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duration_and_distance);
        initMap();
        getUserLocation();
        points = new ArrayList<>();

        if (!checkPermission()) {
            requestPermission();
        } else {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
            //getUserLocation();
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.duration_menu, menu);
        return super.onCreateOptionsMenu(menu);
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
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                if (points.size() == 2) {
                    mMap.clear();
                }


                Location location = new Location("You Will Be Here Soon");
                location.setLatitude(latLng.latitude);
                location.setLongitude(latLng.longitude);
                dest_lat = latLng.latitude;
                dest_long = latLng.longitude;
                points.add(location);
                //setMarker
                setMarker(location);

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

    private void setMarker(Location location) {
        LatLng userlatlong = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(userlatlong).title("Your Destination").snippet("you are going there").draggable(true).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
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


    private void setHomeMarker() {

        locationCallback = new LocationCallback() {
            // @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {

                    homelocation = location;
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


    private void home() {
        if (homelocation != null) {
            lat = homelocation.getLatitude();
            longi = homelocation.getLongitude();
            LatLng userLoaction = new LatLng(homelocation.getLatitude(), homelocation.getLongitude());
            CameraPosition cameraPosition = CameraPosition.builder().target(userLoaction).zoom(15).bearing(0).tilt(45).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            mMap.addMarker(new MarkerOptions().position(userLoaction).title("Your Location").icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.map)));
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private String getDirectionUrl() {
        StringBuilder googleDirectionUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        googleDirectionUrl.append("origin=" + lat + "," + longi);
        googleDirectionUrl.append("&destination=" + dest_lat + "," + dest_long);
        googleDirectionUrl.append("&key=");
        Log.d("", "getDirectionUrl: " + googleDirectionUrl);
        System.out.println(googleDirectionUrl.toString());
        return googleDirectionUrl.toString();
    }


    public void btnClick(View view) {
        Object[] dataTransfer;
        String url;

        switch (view.getId()) {


            case R.id.btn_duration:
                dataTransfer = new Object[4];

                if (isclicked) {
                    lat = points.get(0).getLatitude();
                    longi = points.get(0).getLongitude();
                    dest_long = points.get(1).getLongitude();
                    dest_lat = points.get(1).getLatitude();
                    url = getDirectionUrl();
                    dataTransfer[0] = mMap;
                    dataTransfer[1] = url;
                    System.out.println("richa ");
                    dataTransfer[2] = new LatLng(points.get(0).getLatitude(), points.get(0).getLongitude());
                    dataTransfer[3] = new LatLng(points.get(1).getLatitude(), points.get(1).getLongitude());
                } else {
                    url = getDirectionUrl();
                    dataTransfer[0] = mMap;
                    dataTransfer[1] = url;
                    dataTransfer[2] = new LatLng(dest_lat, dest_long);
                    dataTransfer[3] = new LatLng(homelocation.getLatitude(), homelocation.getLongitude());
                }
                GetDirectionsData getDirectionsData = new GetDirectionsData();
                getDirectionsData.execute(dataTransfer);
                break;
            case R.id.btn_direction:
                dataTransfer = new Object[4];


                if (isclicked) {
                    lat = points.get(0).getLatitude();
                    longi = points.get(0).getLongitude();
                    dest_long = points.get(1).getLongitude();
                    dest_lat = points.get(1).getLatitude();
                    url = getDirectionUrl();
                    dataTransfer[0] = mMap;
                    dataTransfer[1] = url;
                    System.out.println("richa ");
                    dataTransfer[2] = new LatLng(points.get(0).getLatitude(), points.get(0).getLongitude());
                    dataTransfer[3] = new LatLng(points.get(1).getLatitude(), points.get(1).getLongitude());

                } else {
                    System.out.println("not clicked");
                    url = getDirectionUrl();
                    dataTransfer[0] = mMap;
                    dataTransfer[1] = url;
                    dataTransfer[2] = new LatLng(dest_lat, dest_long);
                    dataTransfer[3] = new LatLng(homelocation.getLatitude(), homelocation.getLongitude());
                }
                getDirectionsData = new GetDirectionsData();
                // execute asynchronously
                getDirectionsData.execute(dataTransfer);
                System.out.println("get direction data");
                if (view.getId() == R.id.btn_direction)
                    directionRequested = true;
                else
                    directionRequested = false;
                break;
        }
    }

    public void onClick(View view) {
        isclicked = true;
        mMap.clear();

    }
}
