package com.example.alarmnotif20;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.LocationServices;

import java.util.Date;

import static com.example.alarmnotif20.MainActivity.CHANNEL_ID;

public class GeofenceReceiver extends BroadcastReceiver {

    static final String TAG = "GeofenceReceiver";

    GeofencingClient geofencingClient;

    @Override
    public void onReceive(Context context, Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        Bundle extras = intent.getExtras();

        geofencingClient = LocationServices.getGeofencingClient(context);

        final LocationTask task = MyDatabase.getDatabase(context).locationDAO().getLocationTask(extras.getInt("taskId"));

        if (geofencingEvent.hasError()) {
            Log.e(TAG, "");
            return;
        }

        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER || geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(context, CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_location_on_black_24dp)
                            .setContentTitle(task.getTaskName())
                            .setContentText(task.getTaskDescription());

            builder.setAutoCancel(true);

            NotificationManager mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


            int uniqueID = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);

            mNotificationManager.notify(uniqueID, builder.build());

            MyDatabase.getDatabase(context).locationDAO().deleteLocationTask(task);

            Log.i(TAG, "");
        } else {
            // Log the error.
            Log.e(TAG, "");
        }
    }
}
