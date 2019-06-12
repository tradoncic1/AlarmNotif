package com.example.alarmnotif20;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface DateTimeDAO {
    @Insert
    public long addDateTask (DateTimeTask dateTimeTask);

    @Query("SELECT * FROM dateTask")
    public List<DateTimeTask> getAllTasks();

    @Query("SELECT * FROM dateTask WHERE dateTaskId = :dateTaskId LIMIT 1")
    public DateTimeTask getDateTask(int dateTaskId);

    @Delete
    public void deleteDateTask(DateTimeTask taskDateTime);

    @Update
    public void updateDateTask(DateTimeTask dateTimeTask);

    @Delete
    public void deleteAll(List<DateTimeTask> dateTimeTaskList);
}
