package com.amt.raidmaquitracker;

import android.Manifest;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


public class BackgroundLocationService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    IBinder mBinder = new LocalBinder();

    private static final String TAG = "BGLocationSvc";
    private String INTERVAL_MILLIS = "";


    private GoogleApiClient mGoogleApiClient;

    private PowerManager.WakeLock mWakeLock;

    public static final String PREFS_NAME = "GPS_PREFS";

    public class LocalBinder extends Binder {
        public BackgroundLocationService getServerInstance() {
            return BackgroundLocationService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "On Create");
        buildGoogleApiClient();
        Log.i(TAG, "OnstartCommand GoogleApiConect");
        SharedPreferences sharedPref = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        INTERVAL_MILLIS = sharedPref.getString("interval_millis", "NULL");
        Log.i(TAG, "Interval : " + INTERVAL_MILLIS);
        mGoogleApiClient.connect();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Log.i(TAG, "OnstartCommand");
        PowerManager mgr = (PowerManager) getSystemService(Context.POWER_SERVICE);

    /*
    WakeLock is reference counted so we don't want to create multiple WakeLocks. So do a check before initializing and acquiring.
    This will fix the "java.lang.Exception: WakeLock finalized while still held: MyWakeLock" error that you may find.
    */
        if (this.mWakeLock == null) { //**Added this
            this.mWakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakeLock");
        }

        if (!this.mWakeLock.isHeld()) { //**Added this
            this.mWakeLock.acquire();
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "OnDestroy");
        Intent intent = new Intent(this, LocationReceiver.class);
        PendingIntent pendingIntent = PendingIntent
                .getBroadcast(this, 54321, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,pendingIntent);
        this.mGoogleApiClient.disconnect();

        if (this.mWakeLock != null) {
            this.mWakeLock.release();
            this.mWakeLock = null;
        }
        super.onDestroy();
    }

    /*
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
    @Override
    public void onConnected(Bundle bundle) {
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        Integer int_millis = Integer.parseInt(INTERVAL_MILLIS);
        mLocationRequest.setInterval(int_millis);  // milis
        mLocationRequest.setFastestInterval(int_millis/2);

        Intent intent = new Intent(this, LocationReceiver.class);
        PendingIntent pendingIntent = PendingIntent
                .getBroadcast(this, 54321, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            //ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},123);
            //123 és un int per capturar en onRequestPermissionsResult
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(this.mGoogleApiClient,
                mLocationRequest, pendingIntent);


    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "GoogleApiClient connection has been suspend");
    }

    /*
     * Called by Location Services if the attempt to
     * Location Services fails.
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "GoogleApiClient connection has failed");
    }

    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        this.mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }
}
