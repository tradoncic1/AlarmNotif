package com.example.alarmnotif20;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.Date;
import java.util.List;

import static com.example.alarmnotif20.MainActivity.CHANNEL_ID;

public class LocationAlertIntentService extends IntentService {

    private static final String IDENTIFIER = "LocationAlertIS";

    private static final String TAG = "LocationAlertIS";

    public LocationAlertIntentService() {
        super(IDENTIFIER);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        Bundle extras = intent.getExtras();

        final LocationTask task = MyDatabase.getDatabase(getApplicationContext()).locationDAO().getLocationTask(extras.getInt("taskId"));

        Log.d(TAG, "onHandleIntent: " + task.toString());

        if (geofencingEvent.hasError()) {
            Log.e(IDENTIFIER, "error");
            return;
        }

        Log.i(IDENTIFIER, geofencingEvent.toString());

        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {

            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(this, CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_location_on_black_24dp)
                            .setContentTitle(task.getTaskName())
                            .setContentText(task.getTaskDescription());

            builder.setAutoCancel(true);

            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


            int uniqueID = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);

            mNotificationManager.notify(uniqueID, builder.build());

            MyDatabase.getDatabase(getApplicationContext()).locationDAO().deleteLocationTask(task);
        }
    }
}