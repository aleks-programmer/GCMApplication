package com.gcm.aleks.gcmapplication;

import android.content.Intent;
import android.os.IBinder;

import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by User on 08.06.2015.
 */
public class InstanceIDListenerServiceImpl extends InstanceIDListenerService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        Intent intent = new Intent(this, RegisterIntentService.class);
        startService(intent);
    }
}
