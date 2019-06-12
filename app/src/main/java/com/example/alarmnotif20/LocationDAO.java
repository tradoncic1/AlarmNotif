package com.example.alarmnotif20;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface LocationDAO {
    @Insert
    public long addLocationTask (LocationTask locationTask);

    @Query("SELECT * FROM locationTask")
    public List<LocationTask> getAllTasks();

    @Query("SELECT * FROM locationTask WHERE locationTaskId = :locationTaskId LIMIT 1")
    public LocationTask getLocationTask(int locationTaskId);

    @Delete
    public void deleteLocationTask(LocationTask locationTask);

    @Update
    public void updateLocationTask(LocationTask locationTask);

    @Delete
    public void deleteAll(List<LocationTask> locationTasks);
}
