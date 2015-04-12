package com.iwander.iwander;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import java.io.IOException;


public class LoginActivity extends ActionBarActivity {

    Button btnRegister;
    Button btnLogin;

    EditText etUsername;
    EditText etPassword;

    String username;
    String password;
    String userType;

    final Context context = this;
    boolean isTypeSelected = false;

    RadioButton radioPatient;
    RadioButton radioCaretaker;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences("iwander", Context.MODE_PRIVATE);

        if(sharedPreferences.getString("isLogged", "").equals("true"))
        {
            startActivity(new Intent(getApplicationContext(), TempActivity.class));
            finish();

        }

        radioPatient = (RadioButton) findViewById(R.id.radioPatient);
        radioCaretaker = (RadioButton) findViewById(R.id.radioCaretaker);

        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);

        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLogin = (Button) findViewById(R.id.btnSubmit);
        //STARTS HERE

        int resofAlgo1=0,resofAlgo2=0;
        double age= 70;               //your input
        double timeofday=12;            // your input (select only current hour as system input)
        if(timeofday>=22 )//scale time
            timeofday=80;
        if(timeofday<=6 )
            timeofday=90;
        System.out.println("start");
        keith k = new keith(age,timeofday);
        double avg= k.average();

        double temperature,levelofdementia;
        temperature=32;                 //your input in degree celsius
        temperature=temperature*2;
        levelofdementia=4;            //your input on scale of 1-4
        levelofdementia = levelofdementia*10*2;
        k = new keith(temperature,levelofdementia);
        double avg1= k.average();
        temperature=temperature/2;//scale back for next algo

        if(((avg1+avg)/2)>40)
        {
            Toast.makeText(context,"send warning message", Toast.LENGTH_LONG).show();

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

        //ENDS HERE
        radioPatient.setChecked(true);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(radioPatient.isChecked())
                    userType = "Patient";
                else
                    userType = "Caretaker";

                username = etUsername.getText().toString();
                password = etPassword.getText().toString();

                new LoginAsync().execute(new ApiConnector());
            }
        });


    }


    private void checkResult(String result)
    {
        /*if(result.equals("Success"))
        {
            editor= sharedPreferences.edit();
            editor = sharedPreferences.edit();
            editor.putString("isLoggedIn" , "true");
            editor.putString("username" , username);
            editor.commit();
            startActivity(new Intent(context , WelcomeActivity.class));
            finish();
        }
        else
        {
            Toast.makeText(context, result, Toast.LENGTH_LONG).show();
        }*/
        Toast.makeText(context, result, Toast.LENGTH_LONG).show();

    }



    private class LoginAsync extends AsyncTask<ApiConnector, Void, String>
    {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            pd= new ProgressDialog(context);
            pd.setMessage("Please Wait");
            pd.setCancelable(false);
            pd.setIndeterminate(false);
            pd.show();
        }

        @Override
        protected String doInBackground(ApiConnector... params) {
            return params[0].checkLogin(username, password, userType);
        }

        @Override
        protected void onPostExecute(String s) {
            pd.dismiss();
            checkResult(s);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
