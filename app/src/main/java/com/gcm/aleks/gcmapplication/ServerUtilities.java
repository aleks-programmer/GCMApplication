package com.gcm.aleks.gcmapplication;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import static com.gcm.aleks.gcmapplication.CommonUtilities.SERVER_URL;
import static com.gcm.aleks.gcmapplication.CommonUtilities.TAG;
import static com.gcm.aleks.gcmapplication.CommonUtilities.displayMessage;

/**
 * Created by User on 08.06.2015.
 */
public class ServerUtilities {
    private static final int MAX_ATTEMPTS = 5;
    private static final int BACKOFF_MILLI_SECONDS = 2000;
    private static final Random random = new Random();
    /**
     * Register this account/device pair within the server.
     *
     */
    static void register(final Context context, final String name, final String email, final String iid, final String regId) {
        Log.i(TAG, "registering device (regId = " + regId + ")");
        String serverUrl = SERVER_URL;
        Log.i(TAG, "URL: " + SERVER_URL);
        Map<String, String> params = new HashMap<String, String>();
        Log.i(TAG, "params1: " + params);
        params.put("iid", iid);
        Log.i(TAG, "params2: " + params);
        params.put("regId", regId);
        Log.i(TAG, "params3: " + params);
        params.put("name", name);
        Log.i(TAG, "params4: " + params);
        params.put("email", email);
        Log.i(TAG, "params1: " + params);

        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
        // Once GCM returns a registration id, we need to register on our server
        // As the server might be down, we will retry it a couple
        // times.
        Intent intent = new Intent(context, GCMListenerService.class);
        context.startService(intent);
        for (int i = 1; i <= MAX_ATTEMPTS; i++) {
            Log.d(TAG, "Attempt #" + i + " to register");
            try {
                displayMessage(context, context.getString(
                        R.string.server_registering, i, MAX_ATTEMPTS));
                Log.i(TAG, "params: " + params);
                post(serverUrl, params);
                //GCMRegistrar.setRegisteredOnServer(context, true);
                String message = context.getString(R.string.server_registered);
                displayMessage(context, message);
                return;
            } catch (IOException e) {
                // Here we are simplifying and retrying on any error; in a real
                // application, it should retry only on unrecoverable errors
                // (like HTTP error code 503).
                Log.e(TAG, "Failed to register on attempt " + i + ":" + e);
                if (i == MAX_ATTEMPTS) {
                    break;
                }
                try {
                    Log.d(TAG, "Sleeping for " + backoff + " ms before retry");
                    Thread.sleep(backoff);
                } catch (InterruptedException e1) {
                    // Activity finished before we complete - exit.
                    Log.d(TAG, "Thread interrupted: abort remaining retries!");
                    Thread.currentThread().interrupt();
                    return;
                }
                // increase backoff exponentially
                backoff *= 2;
            }
        }
        String message = context.getString(R.string.server_register_error,
                MAX_ATTEMPTS);
        displayMessage(context, message);
    }

    /**
     * Update toket ID on the server
     */
    static void update(final Context context, final String iid, final String regId) {
        Log.i(TAG, "Updating device (regId = " + regId + ")");
        String serverUrl = SERVER_URL;
        Map<String, String> params = new HashMap<String, String>();
        params.put("iid", iid);
        params.put("regId", regId);

        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
        // Once GCM returns a registration id, we need to register on our server
        // As the server might be down, we will retry it a couple
        // times.
        Intent intent = new Intent(context, GCMListenerService.class);
        context.startService(intent);
        for (int i = 1; i <= MAX_ATTEMPTS; i++) {
            Log.d(TAG, "Attempt #" + i + " to register");
            try {
                displayMessage(context, context.getString(
                        R.string.server_registering, i, MAX_ATTEMPTS));
                post(serverUrl, params);
                //GCMRegistrar.setRegisteredOnServer(context, true);
                String message = context.getString(R.string.server_registered);
                displayMessage(context, message);
                return;
            } catch (IOException e) {
                // Here we are simplifying and retrying on any error; in a real
                // application, it should retry only on unrecoverable errors
                // (like HTTP error code 503).
                Log.e(TAG, "Failed to register on attempt " + i + ":" + e);
                if (i == MAX_ATTEMPTS) {
                    break;
                }
                try {
                    Log.d(TAG, "Sleeping for " + backoff + " ms before retry");
                    Thread.sleep(backoff);
                } catch (InterruptedException e1) {
                    // Activity finished before we complete - exit.
                    Log.d(TAG, "Thread interrupted: abort remaining retries!");
                    Thread.currentThread().interrupt();
                    return;
                }
                // increase backoff exponentially
                backoff *= 2;
            }
        }
        String message = context.getString(R.string.server_register_error,
                MAX_ATTEMPTS);
        displayMessage(context, message);
    }

    /**
     * Unregister this account/device pair within the server.
     */
    static void unregister(final Context context, final String regId) {
        Log.i(TAG, "unregistering device (regId = " + regId + ")");
        String serverUrl = SERVER_URL + "/unregister";
        Map<String, String> params = new HashMap<String, String>();
        params.put("regId", regId);
        try {
            post(serverUrl, params);
            //GCMRegistrar.setRegisteredOnServer(context, false);
            String message = context.getString(R.string.server_unregistered);
            displayMessage(context, message);
        } catch (IOException e) {
            // At this point the device is unregistered from GCM, but still
            // registered in the server.
            // We could try to unregister again, but it is not necessary:
            // if the server tries to send a message to the device, it will get
            // a "NotRegistered" error message and should unregister the device.
            String message = context.getString(R.string.server_unregister_error,
                    e.getMessage());
            displayMessage(context, message);
        }
    }

    /**
     * Issue a POST request to the server.
     *
     * @param endpoint POST address.
     * @param params request parameters.
     *
     * @throws IOException propagated from POST.
     */
    private static void post(String endpoint, Map<String, String> params)
            throws IOException {

        URL url;
        try {
            url = new URL(endpoint);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid url: " + endpoint);
        }
        StringBuilder bodyBuilder = new StringBuilder();
        Log.i(ServerUtilities.class.getName() + ", post", "params: " + params);
        Log.i(ServerUtilities.class.getName() + ", post", "params size: " + params.size());
        Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
        // constructs the POST body using the parameters
        while (iterator.hasNext()) {
            Map.Entry<String, String> param = iterator.next();
            Log.i(TAG, "param.getKey(): '" + param.getKey() + "', param.getValue(): " + param.getValue());
            bodyBuilder.append(param.getKey()).append('=')
                    .append(param.getValue());
            if (iterator.hasNext()) {
                bodyBuilder.append('&');
            }
        }
        String body = bodyBuilder.toString();
        Log.v(TAG, "Posting '" + body + "' to " + url);
        byte[] bytes = body.getBytes();
        HttpURLConnection conn = null;
        try {
            Log.i(ServerUtilities.class.getName(), "URl: " + url);
            conn = (HttpURLConnection) url.openConnection();
            Log.i(ServerUtilities.class.getName(), "1 ");
            conn.setDoOutput(true);
            Log.i(ServerUtilities.class.getName(), "2 ");
            conn.setUseCaches(false);
            conn.setFixedLengthStreamingMode(bytes.length);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded;charset=UTF-8");
            Log.i(ServerUtilities.class.getName(), "3 ");
            // post the request
            OutputStream out = conn.getOutputStream();
            Log.i(ServerUtilities.class.getName(), "4 ");
            out.write(bytes);
            Log.i(ServerUtilities.class.getName(), "5 ");
            out.close();
            // handle the response
            int status = conn.getResponseCode();
            Log.i(ServerUtilities.class.getName(), "connection status: " + status);
            if (status != 200) {
                throw new IOException("Post failed with error code " + status);
            }
            conn.connect();

        } catch (IOException e) {
            Log.e(ServerUtilities.class.getName(), "IOException error: " + e.getMessage());
        }
        finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}
