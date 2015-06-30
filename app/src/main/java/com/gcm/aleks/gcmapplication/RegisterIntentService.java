package com.gcm.aleks.gcmapplication;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

/**
 * Created by User on 08.06.2015.
 */
public class RegisterIntentService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public RegisterIntentService(String name) {
        super(name);
    }
    public RegisterIntentService() {
        super("RegisterIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        InstanceID instanceID = InstanceID.getInstance(this);
        String iid = null;
        String token = null;
        try {
            iid = instanceID.getId();
            token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
        } catch (IOException e) {
            Log.e("IOException", "IOException error while getToket: " + e.getMessage());
        }
        String name = intent.getStringExtra("name");
        String email = intent.getStringExtra("email");
        Log.i(RegisterIntentService.class.getName(), "Vars:\nName: " + name + "\n Email: " + email);
        if(name == null && email == null)
            ServerUtilities.update(getApplicationContext(), iid, token);
        else
            ServerUtilities.register(getApplicationContext(), name, email, iid, token);

    }
}
