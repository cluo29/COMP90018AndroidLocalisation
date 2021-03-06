package io.cluo29.github.activitylocation;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
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


public class MainActivity extends AppCompatActivity{

    private LocationManager lm;

    LocationListener locationListener;


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

    public void startLocalisation() {

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

        // cellular or WIFI network can localise me
        String providerNET = LocationManager.NETWORK_PROVIDER;

        // gps signal often naive
        String providerGPS = LocationManager.GPS_PROVIDER;


        // must call this before using getLastKnownLocation
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
            return;
        }



        boolean gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        Location net_loc = null, gps_loc = null, finalLoc = null;

        if (gps_enabled) {
            Log.d("haha", " gps_enabled");

            //requestLocationUpdates(String provider, long minTime, float minDistance, LocationListener listener)

            lm.requestLocationUpdates(providerGPS, 0, 0, locationListener);
            gps_loc = lm.getLastKnownLocation(providerGPS);
        }
        if (network_enabled){
            Log.d("haha", " net_enabled");
            lm.requestLocationUpdates(providerNET, 0, 0, locationListener);
            net_loc = lm.getLastKnownLocation(providerNET);
        }

        if (gps_loc != null && net_loc != null) {

            Log.d("haha", "both available location");

            //smaller the number more accurate result will
            if (gps_loc.getAccuracy() > net_loc.getAccuracy())
                finalLoc = net_loc;
            else
                finalLoc = gps_loc;

            // I used this just to get an idea (if both avail, its upto you which you want to take as I've taken location with more accuracy)

        } else {

            if (gps_loc != null) {
                finalLoc = gps_loc;
                Log.d("haha", "gps available location");
            } else if (net_loc != null) {
                finalLoc = net_loc;
                Log.d("haha", "net available location");
            }
        }
//        if (gps_loc != null) {
//                finalLoc = gps_loc;}

        if (finalLoc != null) {

            double latitude = finalLoc.getLatitude();

            double longitude = finalLoc.getLongitude();

            Log.d("haha", "latitude：" + latitude + "\nlongitude" + longitude);

            // if we are in melbourne, we get negative latitude.
            // it means south part of the earth.

        } else {
            Log.d("haha", "no available location");


            //startLocalisation();
        }


    }

    public void checkGPSSettings() {
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener()
        {

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProviderEnabled(String provider) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProviderDisabled(String provider) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onLocationChanged(Location location) {
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
                //location.getProvider();
                Log.d("haha", "" + location.getProvider() + " Location latitude " + latitude + "\nlongitude:" + longitude);
            }
        };



        // Location hardware setting enabled?
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
                } else {
                    // Permission has already been granted
                    Log.d("haha", "line 52");
                    startLocalisation();
                }


            } else {
                // no runtime check
                Log.d("haha", "line 74");
                startLocalisation();
            }
        } else {
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
