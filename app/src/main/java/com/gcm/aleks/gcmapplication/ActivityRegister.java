package com.gcm.aleks.gcmapplication;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import static com.gcm.aleks.gcmapplication.CommonUtilities.SENDER_ID;
import static com.gcm.aleks.gcmapplication.CommonUtilities.SERVER_URL;


public class ActivityRegister extends Activity {
    // alert dialog manager
    AlertDialogManager alert = new AlertDialogManager();

    // Internet detector
    ConnectionDetector cd;

    // UI elements
    EditText txtName;
    EditText txtEmail;

    // Register button
    Button btnRegister;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_register);

        cd = new ConnectionDetector(getApplicationContext());

        // Check if Internet present
        if (!cd.isConnectingToInternet()) {
            // Internet Connection is not present
            alert.showAlertDialog(ActivityRegister.this,
                    "Internet Connection Error",
                    "Please connect to working Internet connection", false);
            // stop executing code by return
            return;
        }

        // Check if GCM configuration is set
        if (SERVER_URL == null || SENDER_ID == null || SERVER_URL.length() == 0
                || SENDER_ID.length() == 0) {
            // GCM sernder id / server url is missing
            alert.showAlertDialog(ActivityRegister.this, "Configuration Error!",
                    "Please set your Server URL and GCM Sender ID", false);
            // stop executing code by return
            return;
        }

        txtName = (EditText) findViewById(R.id.txtName);
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        btnRegister = (Button) findViewById(R.id.btnRegister);

        /*
         * Click event on Register button
         * */
        btnRegister.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Log.i(ActivityRegister.class.getName(), "111");
                // Read EditText dat
                String name = txtName.getText().toString();
                String email = txtEmail.getText().toString();

                // Check if user filled the form
                if(name.trim().length() > 0 && email.trim().length() > 0){
                    // Launch Main Activity
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    Log.i(ActivityRegister.class.getName(), "String.valueOf(R.id.txtName) " + String.valueOf(R.id.txtName));
                    Log.i(ActivityRegister.class.getName(), "String.valueOf(R.id.txtEmail) " + String.valueOf(R.id.txtEmail));
                    Log.i(ActivityRegister.class.getName(), "name " + name);
                    Log.i(ActivityRegister.class.getName(), "email " + email);

                    // Registering user on our server
                    // Sending registraiton details to MainActivity
                    i.putExtra("name", name);
                    i.putExtra("email", email);
                    Log.i(ActivityRegister.class.getName(), "startActivity " + i.getComponent().getClassName());
                    startActivity(i);
                    finish();
                }else{
                    // user doen't filled that data
                    // ask him to fill the form
                    alert.showAlertDialog(ActivityRegister.this, "Registration Error!", "Please enter your details", false);
                }
            }
        });
    }
}
