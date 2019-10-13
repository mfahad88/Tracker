package com.example.tracker;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Telephony;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private  GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.wtf("LOcation Service","started...");
        initLocation(getApplicationContext());

    }

    @Override
    public void onDestroy() {
        googleApiClient.disconnect();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!googleApiClient.isConnected()){
            googleApiClient.connect();
        }
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
//        unregisterReceiver(mReceiver);
        return super.onUnbind(intent);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest=LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000);
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,locationRequest,this);
//        fusedLocationClient= LocationServices.getFusedLocationProviderClient(this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if(location!=null){
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addresses=geocoder.getFromLocation(
                        location.getLatitude(),
                        location.getLongitude(),
                        1);
                StringBuilder message=new StringBuilder();
                message.append("Latitude: ");
                message.append(location.getLatitude());
                message.append("\n");
                message.append("Longitude: ");
                message.append(location.getLongitude());
                message.append("\n");
                message.append("Address: ");
                message.append(addresses.get(0).getAddressLine(0));

                Utils.sendSMS(this,Utils.getPreferences(this,MainActivity.MOBILE),
                        message.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }


            Utils.stopService(this);
        }
    }

    public void initLocation(Context context){
        googleApiClient=new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }



}
