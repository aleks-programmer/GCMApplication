package com.gcm.aleks.gcmapplication;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmListenerService;
import com.google.android.gms.iid.InstanceID;

import static com.gcm.aleks.gcmapplication.CommonUtilities.EXTRA_MESSAGE;
import static com.gcm.aleks.gcmapplication.CommonUtilities.SENDER_ID;
import static com.gcm.aleks.gcmapplication.CommonUtilities.displayMessage;

/**
 * Created by User on 08.06.2015.
 */
public class GCMListenerService extends GcmListenerService {
    private static final String TAG = "GCMListenerService";
    private InstanceID instanceID = null;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(GCMListenerService.class.getName() + "." + "onCreate()",
                "onCreate");
    }


    /**
     * Issues a notification to inform the user that server has sent a message.
     */

    private static void generateNotification(Context context, String message) {
        Log.i("generateNotification", "message: " + message);
        //int icon = R.drawable.ic_launcher;
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        Log.i("generateNotification", "notificationManager: " + notificationManager);
        String title = context.getString(R.string.app_name);

        Intent notificationIntent = new Intent(context, MainActivity.class);
        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        Log.i("generateNotification", "notificationIntent: " + notificationIntent);
        PendingIntent intent =
                PendingIntent.getActivity(context, 0, notificationIntent, 0);
        Log.i("generateNotification", "PendingIntent: " + intent);
        Notification notification = new NotificationCompat.Builder(context)
                .setWhen(when)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(intent)
                .build();
        Log.i("generateNotification", "notification: " + notification);
        Log.i("GCM Notify", "notification message: " + message);

        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        // Play default notification sound
        notification.defaults |= Notification.DEFAULT_SOUND;

        // Vibrate if vibrate is enabled
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        Log.i("generateNotification", "notification.defaults: " + notification.defaults);
        notificationManager.notify(0, notification);

    }
    private static void makeNotification(Context context, String message) {
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        Notification notification;
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        String title = context.getString(R.string.app_name);
        Intent notificationIntent = new Intent(context, MainActivity.class);
        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        Log.i("makeNotification", "notificationIntent: " + notificationIntent);
        PendingIntent intent =
                PendingIntent.getActivity(context, 0, notificationIntent, 0);
// To support 2.3 os, we use "Notification" class and 3.0+ os will use
// "NotificationCompat.Builder" class.
        if (currentapiVersion < android.os.Build.VERSION_CODES.HONEYCOMB) {
            Log.i("makeNotification", "currentapiVersion: " + currentapiVersion);
            notification = new Notification(R.mipmap.ic_launcher, message, 0);
            notification.setLatestEventInfo(context, title, message,
                    intent);
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            notificationManager.notify(0, notification);

        } else {
            Log.i("makeNotification", "currentapiVersion: " + currentapiVersion);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(
                    context);
            notification = builder.setContentIntent(intent)
                    .setSmallIcon(R.mipmap.ic_launcher).setTicker(title).setWhen(0)
                    .setAutoCancel(true).setContentTitle(title)
                    .setContentText(message).build();

            notificationManager.notify(0 , notification);
        }
    }

    @Override
    public void onMessageReceived(String from, Bundle data) {
        //super.onMessageReceived(from, data);
        Log.i(GCMListenerService.class.getName() + "." + "onMessageReceived()",
                "onMessageReceived");
        String message = data.getString("price");
        Log.i(GCMListenerService.class.getName() + "." + "onMessageReceived()",
                "message is " + message);

       // generateNotification(getApplicationContext(), message);
        if(message != null)
            makeNotification(getApplicationContext(), message);
        /*Intent intentToBroadCast = new Intent(this, MainActivity.class);
        Log.i(GCMListenerService.class.getName() + "." + "onMessageReceived()",
                "intentToBroadCast is " + intentToBroadCast.getComponent().getClassName());*/
        //sendBroadcast(intentToBroadCast);

    }


}
