package com.iwander.iwander;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;


public class TempActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks,
GoogleApiClient.OnConnectionFailedListener,
com.google.android.gms.location.LocationListener
{
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;

    final String TAG = "IWander debug";


    final Context context = this;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    CheckBox cbLocation;
    EditText etRadius;
    Button btnSave;

    double longitude;
    double latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);

        cbLocation = (CheckBox) findViewById(R.id.cbLocation);
        etRadius = (EditText) findViewById(R.id.etRadius);
        btnSave = (Button) findViewById(R.id.btnSave);

        sharedPreferences = getSharedPreferences("iwander", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("radius",etRadius.getText().toString());
                editor.commit();
                startService(new Intent(getBaseContext(), BackGroundAction.class));
                finish();

            }
        });

        /*
        int resofAlgo1=0,resofAlgo2=0;

        double age= Double.parseDouble(sharedPreferences.getString("age", "0"));               //your input
        double timeofday=12;            // your input (select only current hour as system input)
        if(timeofday>=22 )//scale time
            timeofday=80;
        if(timeofday<=6 )
            timeofday=90;
        // System.out.println("start");
        keith k = new keith(age,timeofday);
        double avg= k.average();


        double temperature,levelofdementia;
        temperature=32;                 //your input in degree celsius
        temperature=temperature*2;

        levelofdementia = Double.parseDouble(sharedPreferences.getString("dementiaLevel", "1"));

       // levelofdementia=4;            //your input on scale of 1-4
        levelofdementia = levelofdementia*10*2;
        k = new keith(temperature,levelofdementia);
        double avg1= k.average();
        temperature=temperature/2;//scale back for next algo
        levelofdementia = levelofdementia/(10*2);


        if(((avg1+avg)/2)>40)
        {
            Toast.makeText(context, "send warning message", Toast.LENGTH_LONG).show();

            System.out.println("send warning message");//your Toast this message
            resofAlgo1=1;//
        }

        //naive bayes
        String age1="",inc1="",time1="",lod1="";
        if(age<=30)
            age1="<=30";
        if(age>30 && age<45)
            age1="31-45";
        if(age>=45 && age<=60)
            age1="45-60";
        if(age>60)
            age1=">=60";

        if(temperature<=20)
            inc1="10";
        if(temperature>10 && temperature<=20)
            inc1="20";
        if(temperature>20 && temperature<=25)
            inc1="25";
        if(temperature>25 && temperature<=30)
            inc1="30";
        if(temperature>30 && temperature<=90)
            inc1="40";

        if(timeofday>=22)
            time1="n";
        if(timeofday<=7)
            time1="n";
        if(timeofday >7 && timeofday<22)
            time1="d";

        if( levelofdementia ==20)

            lod1="1";
        if( levelofdementia ==40)

            lod1="2";
        if( levelofdementia ==60)

            lod1="3";
        if( levelofdementia ==80)

            lod1="4";

        Naive27 n = new Naive27(age1,inc1,time1,lod1);
        String[] args1={};
        try{
            n.main(args1);
        }

        catch(IOException e){}
        String result2 = n.display();
        System.out.println(result2);
        if(result2.equals("yes"))
        {
            Toast.makeText(context," second warning message", Toast.LENGTH_LONG).show();
            //String url="https://www.google.co.in/maps/place/St.+Francis+D'Assisi+High+School/@19.243079,72.8531095,18z/data=!4m2!3m1!1s0x0000000000000000:0xbcb37be822b58dae";
            String url ="http://map.google.com?q=19.243461,72.855475";
            String phoneNumber = "+918108745130";
            String message = "Patient has crossed safe zone  last location is "+url;

            //SmsManager smsManager = SmsManager.getDefault();
            Log.d("debug", "reached SMS 1");
            //smsManager.sendTextMessage(phoneNumber, null, message, null, null);

            ///System.out.println("send 2nd warning message");//your Toast this message

            Log.d("debug", "reached SMS 2");
            resofAlgo2=1;//
        }
        if(resofAlgo1==1 && resofAlgo2==1) {
            Toast.makeText(context,"YOU ARE WARNED TWICE", Toast.LENGTH_LONG).show();

            System.out.println("you r warned twicee!!");// your Toast this message

        }
        */
        //ENDS HERE
    }

    @Override
    public void onConnected(Bundle bundle) {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000); // Update location every second

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "GoogleApiClient connection has been suspend");
    }

    @Override
    public void onLocationChanged(Location location) {
        //Toast.makeText(context, location.toString(), Toast.LENGTH_SHORT).show();
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        editor.putString("latitude", String.valueOf(latitude));
        editor.putString("longitude", String.valueOf(longitude));
        editor.commit();

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        // Connect the client.
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_temp, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
