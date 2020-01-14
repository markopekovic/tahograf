package com.example.tahograf;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

public class LocationService extends Service
        implements  LocationListener,
                    GoogleApiClient.ConnectionCallbacks,
                    GoogleApiClient.OnConnectionFailedListener {

    private static final long INTERVAL = 1000*1;
    private static final long FASTEST_INTERVAL = 500*1;

    LocationRequest mlLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location currentLocation, startLocation, endLocation;
    static double distance = 0;
    double speed;

    private final IBinder mBinder = new LocalBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
        return mBinder;
    }

    protected void createLocationRequest() {
        mlLocationRequest = new LocationRequest();
        mlLocationRequest.setInterval(INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mlLocationRequest, this);
        } catch (SecurityException ex){

        }

    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        distance = 0;
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        MainActivity.locateDialog.dismiss();
        currentLocation = location;
        endLocation = currentLocation;
        if (startLocation == null) {
            startLocation = currentLocation;
        }

        speed = location.getSpeed() * 18 / 5;
        Log.w("MAPE"," speed = "+speed);
        updateUI();


    }

    public class LocalBinder extends Binder {

        public  LocationService getService() {
            return LocationService.this;
        }

    }

    private void updateUI() {
        if (MainActivity.p == 0 ) {
//            distance = distance + (startLocation.distanceTo(endLocation) / 1000.00);
            distance = (startLocation.distanceTo(endLocation));
            Log.i("MAPE"," distance = "+distance);
            MainActivity.endTime = System.currentTimeMillis();
            long diff = MainActivity.endTime - MainActivity.startTime;
            diff = TimeUnit.MILLISECONDS.toMinutes(diff);

            if (speed > 0.0) {
               MainActivity.tv_speed.setText("Current speed: " + new DecimalFormat("#.###").format(speed) + " km/h");
            } else {
                MainActivity.tv_speed.setText("............." );
            }
            startLocation = endLocation;

        }
    }
}
