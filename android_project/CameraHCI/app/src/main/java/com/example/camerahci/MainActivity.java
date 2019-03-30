package com.example.camerahci;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
//import android.text.Layout;
//import android.view.Gravity;
import android.support.v7.widget.PopupMenu;
import android.view.MenuInflater;
import android.view.View;
//import android.widget.LinearLayout;
//import android.widget.PopupWindow;
import android.widget.Toast;
import android.util.Log;

import com.example.EventUtilities.allEventsActivity;
import com.example.LocationAPI.LocationControl;
import com.example.Utilities.PermissionUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import static com.example.Utilities.Constants.ERROR_DIALOG_REQUEST;
import static com.example.Utilities.Constants.PERMISSIONS_REQUEST_ENABLE_GPS;

public class MainActivity extends AppCompatActivity {
    // adding logger to the code
    private static final String LOG_TAG =
            MainActivity.class.getSimpleName();
    /**
     * Id to identify a camera permission request.
     */
    private static final int REQUEST_CAMERA = 0;
    private static final int REQUEST_LOCATION = 1;
    private static final int REQUEST_AUDIO = 0;

    private static String[] PERMISSIONS_CAMERA = {Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO};

    private  static String[] PERMISSIONS_LOCATION = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.INTERNET};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void ShowCamera(View view) {
        Log.d(LOG_TAG, "--> Camera Button clicked! Checking for camera permissions");

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED
        || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED
        || ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED){
            //Camera Permission has not been Granted so throw a snackbar saying that we need a permission
            requestCameraPermission();
        }else{
        Log.d(LOG_TAG, "--> Clicked on record view, fetching Events");
        Intent intnt = new Intent(this, allEventsActivity.class);
        startActivity(intnt);
        /**
        Log.d(LOG_TAG, "--> All Camera Permissions Granted! Opening Camera");
        Toast tst = Toast.makeText(this, R.string.toast_camera, Toast.LENGTH_SHORT);
        tst.show();
        Intent intnt = new Intent(this, CameraControl.class);
        startActivity(intnt);
         */
        }
    }
    /** Popup Menu Displaying event details;
    *  user needs to select any one of the menu items
    *  after selecting the camera opens and user can record the video.
    */
    /**
    public void showPopupMenu(View v){
        PopupMenu popup = new PopupMenu(this,v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu., popup.getMenu());
    }
    */
    /**
     * Requests the Camera permission.
     * If the permission has been denied previously, a SnackBar will prompt the user to grant the
     * permission, otherwise it is requested directly.
     */
    private void requestCameraPermission(){
       Log.d(LOG_TAG, "--> There were No Camera Permissions granted so Proceeding further by asking user to grant permission");
        // BEGIN_INCLUDE(camera_permission_request)
        // Camera permission has not been granted yet. Request it directly.
        ActivityCompat.requestPermissions(this,
                PERMISSIONS_CAMERA,
                REQUEST_CAMERA);
        // END_INCLUDE(camera_permission_request)
        /*
        ----------- Code Commented As SnackBAr is not working ----------------
        PopupWindow popUp = new PopupWindow(this);
        LinearLayout layout = new LinearLayout(this);
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CAMERA)){
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            Log.d(LOG_TAG,"--> Displaying camera permission rationale to provide additional context.");
            //popUp.showAtLocation(View, Gravity.BOTTOM,);
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA);
        }else{
            // Camera permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA);
            // Write External Storage Permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_EXT_STORAGE);
            // Request Audio Storage
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_AUDIO);
        }*/
    }

    public void showLocation(View view){
        Log.d(LOG_TAG, "--> Locations Button clicked! Checking for Locations permissions");
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET)!= PackageManager.PERMISSION_GRANTED){
            //Location Permissions has not been Granted so throw a snackbar saying that we need a permission
            requestLocationPermissions();
        }else{
            Log.d(LOG_TAG,"--> All Permissions Granted Finding Location using GPS");
            Toast tst = Toast.makeText(this, R.string.toast_location, Toast.LENGTH_SHORT);
            tst.show();
            Intent intnt = new Intent(this, LocationControl.class);
            startActivity(intnt);
        }
    }

    private void requestLocationPermissions() {
        Log.d(LOG_TAG, "--> There were No Location Permissions granted so Proceeding further by asking user to grant permission");
        // BEGIN_INCLUDE(location_permission_request)
        // Camera permission has not been granted yet. Request it directly.
        ActivityCompat.requestPermissions(this,
                PERMISSIONS_LOCATION,
                REQUEST_LOCATION);
        // END_INCLUDE(location_permission_request)
    }

    /**
            * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CAMERA) {
            // BEGIN_INCLUDE(permission_result)
            // Received permission result for camera permission.
            Log.d(LOG_TAG, "--> Received response for Camera permission request.");
            // We have requested multiple permissions for contacts, so all of them need to be
            // checked.
            if (PermissionUtil.verifyPermissions(grantResults)) {
                // All required permissions have been granted, display contacts fragment.
                Log.d(LOG_TAG, "--> Camera permissions were granted.");
                /*Snackbar.make(mLayout, R.string.permision_available_contacts,
                        Snackbar.LENGTH_SHORT)
                        .show();*/
            } else {
                Log.d(LOG_TAG, "--> Camera permissions were NOT granted.");
                /* Snackbar.make(mLayout, R.string.permissions_not_granted,
                        Snackbar.LENGTH_SHORT)
                        .show(); */
            }
        } else if(requestCode == REQUEST_LOCATION){
            // BEGIN_INCLUDE(permission_result)
            // Received permission result for camera permission.
            Log.d(LOG_TAG, "--> Received response for Location permission request.");
            // We have requested multiple permissions for contacts, so all of them need to be
            // checked.
            if (PermissionUtil.verifyPermissions(grantResults)) {
                // All required permissions have been granted, display contacts fragment.
                Log.d(LOG_TAG, "--> Location permissions were granted.");
                /*Snackbar.make(mLayout, R.string.permision_available_contacts,
                        Snackbar.LENGTH_SHORT)
                        .show();*/
            } else {
                Log.d(LOG_TAG, "--> Location permissions were NOT granted.");
                /* Snackbar.make(mLayout, R.string.permissions_not_granted,
                        Snackbar.LENGTH_SHORT)
                        .show(); */
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /*
    * Creates a Map Activity when clicked on the Map Button
    * Displays Maps on phone Screen
    * */
    public void showMap(View view) {
        Log.d(LOG_TAG, "--> Locations Button clicked! Checking for Locations permissions");
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET)!= PackageManager.PERMISSION_GRANTED){
            //Location Permissions has not been Granted so throw a snackbar saying that we need a permission
            requestLocationPermissions();
        }else{
            if(checkMapServices()){
                Log.d(LOG_TAG,"--> All Permissions Granted Finding Location using GPS");
                Toast tst = Toast.makeText(this, R.string.toast_location, Toast.LENGTH_SHORT);
                tst.show();
                Intent intnt = new Intent(this, MapsActivity.class);
                startActivity(intnt);
            }
        }
    }

    private boolean checkMapServices(){
        if(isServicesOK()){
            if(isMapsEnabled()){
                return true;
            }
        }
        return false;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public boolean isMapsEnabled(){
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    public boolean isServicesOK(){
        Log.d(LOG_TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(LOG_TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(LOG_TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}
