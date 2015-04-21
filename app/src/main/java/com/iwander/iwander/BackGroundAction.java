package com.iwander.iwander;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class BackGroundAction extends Service implements GoogleApiClient.ConnectionCallbacks,
GoogleApiClient.OnConnectionFailedListener,
LocationListener
{
    Location storedLocation;
    GoogleApiClient googleApiClient;
    LocationRequest locationRequest;
    SharedPreferences sharedPreferences;

    final Context context = this;

    double longitude;
    double latitude;
    int storedRadius = 0;

    String username;

    int locationInterval = 10000;
    boolean hasUserCrossed = false;


    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "Service created", Toast.LENGTH_LONG).show();



        googleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).
                addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sharedPreferences = getSharedPreferences("iwander", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username", "");
        double storedLongitude = Double.parseDouble(sharedPreferences.getString("longitude", "0"));
        double storedLatitude = Double.parseDouble(sharedPreferences.getString("latitude", "0"));
        storedRadius = Integer.parseInt(sharedPreferences.getString("radius", "0"));
        Toast.makeText(this, "service started!!", Toast.LENGTH_SHORT).show();
        storedLocation = new Location("Stored Location");
        storedLocation.setLongitude(storedLongitude);
        storedLocation.setLatitude(storedLatitude);
        Toast.makeText(this, "Location value set!!", Toast.LENGTH_SHORT).show();
        googleApiClient.connect();
        Toast.makeText(this, "connected to google api!!", Toast.LENGTH_SHORT).show();
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onConnected(Bundle bundle) {

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(locationInterval);
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

        hasUserCrossed = false;

        double distance = getDistance(location);

        //update location to server
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        Toast.makeText(this,"Distance is "+ String.valueOf(distance), Toast.LENGTH_SHORT).show();
        if(distance>storedRadius)
        {
            hasUserCrossed = true;
            Toast.makeText(this,"you have wandered"+ String.valueOf(distance), Toast.LENGTH_LONG).show();
            //user has crossed radius boundary
            //send notification to caretaker




        }
        new UploadAsync().execute(new ApiConnector());



    }



    public class UploadAsync extends AsyncTask<ApiConnector, Void, String>
    {
        @Override
        protected String doInBackground(ApiConnector... apiConnectors) {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd:MMMM:yyyy HH:mm:ss");
            String strDate = sdf.format(c.getTime());

            return apiConnectors[0].uploadData(String.valueOf(longitude), String.valueOf(latitude), username, strDate, hasUserCrossed);
        }

        @Override
        protected void onPostExecute(String s) {
            checkResult(s);
        }
    }

    private void checkResult(String s)
    {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
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
