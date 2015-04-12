package com.iwander.iwander;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class BackGroundAction extends Service implements GoogleApiClient.ConnectionCallbacks,
GoogleApiClient.OnConnectionFailedListener,
LocationListener
{
    Location storedLocation;
    GoogleApiClient googleApiClient;
    LocationRequest locationRequest;
    SharedPreferences sharedPreferences;

    int storedRadius = 0;

    @Override
    public void onCreate() {
        Toast.makeText(this, "Service created", Toast.LENGTH_LONG).show();

        googleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).
                addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sharedPreferences = getSharedPreferences("iwander", Context.MODE_PRIVATE);
        double storedLongitude = Double.parseDouble(sharedPreferences.getString("longitude", "0"));
        double storedLatitude = Double.parseDouble(sharedPreferences.getString("latitude", "0"));
        storedRadius = Integer.parseInt(sharedPreferences.getString("radius", "0"));

        storedLocation = new Location("Stored Location");
        storedLocation.setLongitude(storedLongitude);
        storedLocation.setLatitude(storedLatitude);
        Toast.makeText(this, "service started!!", Toast.LENGTH_SHORT).show();
        googleApiClient.connect();
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onConnected(Bundle bundle) {

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(3000);
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(this, location.toString(), Toast.LENGTH_SHORT).show();
        double distance = getDistance(location);

        //update location to server

        if(distance>storedRadius)
        {
            //user has crossed radius boundary
            //send notification to caretaker


        }


    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public double getDistance(Location newLocation)
    {
        return storedLocation.distanceTo(newLocation);
    }


}
