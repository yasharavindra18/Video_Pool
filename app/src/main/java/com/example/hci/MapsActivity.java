package com.example.hci;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.EventUtilities.NewEventActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import static com.example.hci.EventsData.id;
import static com.example.hci.EventsData.lats;
import static com.example.hci.EventsData.longs;
import static com.example.hci.EventsData.rec;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    // adding logger to the code
    private static final String LOG_TAG =
            MapsActivity.class.getSimpleName();

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 5445;
    private static final int REQUEST_CAMERA = 0;
    private static String[] PERMISSIONS_CAMERA = {Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO};
    public Marker marker[] = null;
    String latitude_current = "";//String.valueOf(currentLocation.getLatitude());
    String longitude_current = "";//String.valueOf(currentLocation.getLongitude());
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Marker currentLocationMarker;
    private Location currentLocation;
    private final View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.currentLocationImageButton && googleMap != null && currentLocation != null)
                MapsActivity.this.animateCamera(currentLocation);
        }
    };
    //currentLocation = locationResult.getLastLocation();
    private boolean firstTimeFlag = true;
    private final LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (locationResult.getLastLocation() == null)
                return;
            currentLocation = locationResult.getLastLocation();
            if (firstTimeFlag && googleMap != null) {
                animateCamera(currentLocation);
                firstTimeFlag = false;
            }
            //showMarker(currentLocation);
            latitude_current = String.valueOf(currentLocation.getLatitude());
            longitude_current = String.valueOf(currentLocation.getLongitude());
            new EventsData().execute(latitude_current, longitude_current);

            //startService(new Intent(MapsActivity.this, MyService.class));
            if (!lats.isEmpty() && !longs.isEmpty()) {
                Log.i("lats", String.valueOf(lats));
                /*if (marker!=null){
                    marker.remove();
                    Log.i("Marker", String.valueOf(marker));
                }*/
                //googleMap.clear();
                //showMarker(currentLocation);
                for (int i = 0; i < lats.size(); i++) {
                    //Log.i("Rec", String.valueOf(rec));
                    if (rec.get(i).equals("0")) {
                        Log.i("Rec", String.valueOf(rec));
                        showHousesMarker(lats.get(i), longs.get(i), rec.get(i), id.get(i));
                    } else {
                        showNonHousesMarker(lats.get(i), longs.get(i), rec.get(i), id.get(i));
                    }
                }

            } else {
                googleMap.clear();
            }
            showMarker(currentLocation);


        }
    };

    //------------------------------Markers-------------------------------------//
    private void showHousesMarker(String latitude, String longitude, String title, String id) {
        LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
        //googleMap.clear();


        googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.pointer)).position(latLng).title(title).snippet(id));
    }

    private void showNonHousesMarker(String latitude, String longitude, String title, String id) {
        LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
        //googleMap.clear();


        googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.pointer)).position(latLng).title(title).snippet(id));
    }

    private void showMarker(@NonNull Location currentLocation) {


        //new HomesData().execute("1","2");
        //Log.i("data", String.valueOf(lats));
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        //LatLng latLng = new LatLng(36.88728, -76.30372);
        /*for (int i =0;i<10;i++){
            LatLng latLng1 = new LatLng(Integer.parseInt(lats.get(i)), Integer.parseInt(longs.get(i)));
            googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.pointer)).position(latLng1));
        }*/
        //if (currentLocationMarker == null)
        currentLocationMarker = googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).position(latLng));
//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(latLng)
//                .title("Snowqualmie Falls")
//                .snippet("Snoqualmie Falls is located 25 miles east of Seattle.")
//                .icon(BitmapDescriptorFactory.defaultMarker( BitmapDescriptorFactory.HUE_BLUE)).position(latLng);
//
//
//        InfoWindowData info = new InfoWindowData();
//        info.setImage("snowqualmie");
//        info.setHotel("Hotel : excellent hotels available");
//        info.setFood("Food : all types of restaurants available");
//        info.setTransport("Reach the site by bus, car and train.");

        CustomInfoWindowGoogleMap customInfoWindow = new CustomInfoWindowGoogleMap(this);
        googleMap.setInfoWindowAdapter(customInfoWindow);


//        Marker m = googleMap.addMarker(markerOptions);
//        m.setTag(info);
//        m.showInfoWindow();

        //googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        //else
        // MarkerAnimation.animateMarkerToGB(currentLocationMarker, latLng, new LatLngInterpolator.Spherical());
    }

    //--------------------------Markers----------------------------------------------------//
//-----------------open Camera on clicking on record video button ----------------------//
//--------------------------------------------------------------------------------------//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        //Log.i("lat", String.valueOf(lats));

        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        supportMapFragment.getMapAsync(this);
        findViewById(R.id.currentLocationImageButton).setOnClickListener(clickListener);


    }


    @Override
    public void onMapReady(GoogleMap Map) {
        this.googleMap = Map;


        googleMap.setOnInfoWindowClickListener(this);
    }


    private void startCurrentLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(100);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MapsActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                return;
            }
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status)
            return true;
        else {
            if (googleApiAvailability.isUserResolvableError(status))
                Toast.makeText(this, "Please Install google play services to use this application", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED)
                Toast.makeText(this, "Permission denied by uses", Toast.LENGTH_SHORT).show();
            else if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                startCurrentLocationUpdates();
        }
    }

    private void animateCamera(@NonNull Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(getCameraPositionWithBearing(latLng)));
    }

    @NonNull
    private CameraPosition getCameraPositionWithBearing(LatLng latLng) {
        return new CameraPosition.Builder().target(latLng).zoom(16).build();
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (fusedLocationProviderClient != null)
            fusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isGooglePlayServicesAvailable()) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            startCurrentLocationUpdates();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fusedLocationProviderClient = null;
        googleMap = null;
    }

    /*
     * This function opens camera on clicking on the event*/
    @Override
    public void onInfoWindowClick(Marker marker) {
        Log.i("result1", marker.getSnippet());
        Log.i("Opening camera", marker.getSnippet());
        Log.d(LOG_TAG, "--> Camera Button clicked! Checking for camera permissions");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            //Camera Permission has not been Granted so throw a snackbar saying that we need a permission
            requestCameraPermission();
        } else {
            Log.d(LOG_TAG, "--> Clicked on record view, fetching Events");
            Intent intent = new Intent(this, MainActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("e_id", marker.getId());
            bundle.putString("e_name", marker.getTitle());
            bundle.putDouble("e_lat", marker.getPosition().latitude);
            bundle.putDouble("e_lng", marker.getPosition().longitude);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }


    /**
     * Requests the Camera permission.
     * If the permission has been denied previously, a SnackBar will prompt the user to grant the
     * permission, otherwise it is requested directly.
     */
    private void requestCameraPermission() {
        Log.i(LOG_TAG, "--> There were No Camera Permissions granted so Proceeding further by asking user to grant permission");
        // BEGIN_INCLUDE(camera_permission_request)
        // Camera permission has not been granted yet. Request it directly.
        ActivityCompat.requestPermissions(this,
                PERMISSIONS_CAMERA,
                REQUEST_CAMERA);
        // END_INCLUDE(camera_permission_request)
    }


    // Add NEw Events
    public void addEvent(View view) {
        if (view.getId() == R.id.addEventImageButton)
            Log.i(LOG_TAG, "Clicked on add Event button");
        //mLocationCallback.onLocationResult();
        //String current
        Intent intnt = new Intent(this, NewEventActivity.class);
        intnt.putExtra("current_lat", latitude_current);
        intnt.putExtra("current_long", longitude_current);
        startActivity(intnt);

    }
/*
    public void openCamera(View view) {
        Log.i("opening Camera","Opening Camera");
        Intent intnt = new Intent(this, MainActivity.class);
        startActivity(intnt);

    }
*/


   /* @Override
    public void onTaskComplete(ArrayList result) {
        Log.i("result", "hello");
    }*/
}

