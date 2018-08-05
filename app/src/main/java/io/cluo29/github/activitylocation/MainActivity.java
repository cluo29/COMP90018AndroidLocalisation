package io.cluo29.github.activitylocation;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;


import static android.content.pm.PackageManager.PERMISSION_GRANTED;


public class MainActivity extends AppCompatActivity {

    private LocationManager lm;



    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

    }

    public static boolean checkPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void startLocalisation(){

        // parameters of location service
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);

        // localisation uses a lot of power, consider your task cycle

        //String provider = lm.getBestProvider(criteria, true);

        // can also use a specific provider

        String provider = LocationManager.NETWORK_PROVIDER;

        // gps signal often naive
        //String provider = LocationManager.GPS_PROVIDER;


        // must call this before using getLastKnownLocation
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
            return;
        }
        Location location = lm.getLastKnownLocation(provider);

        if (location != null) {

            double latitude = location.getLatitude();

            double longitude = location.getLongitude();

            Log.d("haha","latitude：" + latitude + "\nlongitude" + longitude);

            // if we are in melbourne, we get negative latitude.
            // it means south part of the earth.

        } else {
            Log.d("haha","no available location");
            startLocalisation();
        }

    }

    public void checkGPSSettings(){
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        boolean GPSEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

        String[] permissionsArray = {
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
        };

        if (GPSEnabled) {
            // Android 6.0+
            if (Build.VERSION.SDK_INT >= 23) {
                if (!checkPermissions(this, permissionsArray)) {
                    // request code 1

                    Log.d("haha", "request");

                    ActivityCompat.requestPermissions(this, permissionsArray,
                            1);
                }
                else {
                    // Permission has already been granted
                    Log.d("haha", "line 52");
                    startLocalisation();
                }


            }
            else {
                // no runtime check
                Log.d("haha", "line 74");
                startLocalisation();
            }
        }else {
            Log.d("haha", "line 82");
            Toast.makeText(this, "GPS Not Enabled", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            // request code 2
            startActivityForResult(intent, 2);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkGPSSettings();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // request code 2
            case 2:
                checkGPSSettings();
                break;

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            // request code 1
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Log.d("haha", "line 90");
                    startLocalisation();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            default:
                break;

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
}
