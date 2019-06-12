package com.example.alarmnotif20;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {DateTimeTask.class, LocationTask.class}, version = 1)
public abstract class MyDatabase extends RoomDatabase {
    private static MyDatabase INSTANCE = null;

    public abstract DateTimeDAO dateTimeDAO();
    public abstract LocationDAO locationDAO();

    public static MyDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context, MyDatabase.class, "alarmNotifDB").allowMainThreadQueries().build();
        }

        return INSTANCE;
    }
}
