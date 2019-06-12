package com.example.alarmnotif20;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import java.util.Date;

import static com.example.alarmnotif20.MainActivity.CHANNEL_ID;

public class AlarmReceiver extends BroadcastReceiver {
    static final String TAG = "AlarmReceiver";

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: Date Alarm Received");
        Bundle extras = intent.getExtras();

        DateTimeTask task = MyDatabase.getDatabase(context).dateTimeDAO().getDateTask(extras.getInt("dateTaskId"));

        if (task == null) {
            return;
        }

        Log.d(TAG, "onReceive: Received Date Task: " + task.toString());

        Intent mainIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, mainIntent, 0);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification.Builder builder = new Notification.Builder(context);
        builder.setSmallIcon(R.drawable.ic_access_time_black_24dp)
                .setContentTitle(task.getTaskName())
                .setContentText(task.getTaskDescription())
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setChannelId(CHANNEL_ID)
                .setContentIntent(contentIntent);

        int uniqueID = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);

        notificationManager.notify(uniqueID, builder.build());

        MyDatabase.getDatabase(context).dateTimeDAO().deleteDateTask(task);
    }
}
