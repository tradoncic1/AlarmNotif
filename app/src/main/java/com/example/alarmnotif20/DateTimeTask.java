package com.example.alarmnotif20;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "dateTask")
public class DateTimeTask {
    @PrimaryKey(autoGenerate = true)
    int dateTaskId;

    String taskName;
    String taskDescription;
    String taskTime;
    String taskDate;

    public DateTimeTask(int dateTaskId, String taskName, String taskDescription, String taskTime, String taskDate) {
        this.dateTaskId = dateTaskId;
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskTime = taskTime;
        this.taskDate = taskDate;
    }

    @Ignore
    public DateTimeTask(String taskName, String taskDescription, String taskTime, String taskDate) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskTime = taskTime;
        this.taskDate = taskDate;
    }

    public int getDateTaskId() {
        return dateTaskId;
    }

    public void setDateTaskId(int dateTaskId) {
        this.dateTaskId = dateTaskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public String getTaskTime() {
        return taskTime;
    }

    public void setTaskTime(String taskTime) {
        this.taskTime = taskTime;
    }

    public String getTaskDate() {
        return taskDate;
    }

    public void setTaskDate(String taskDate) {
        this.taskDate = taskDate;
    }

    @Override
    public String toString() {
        String returnString = "\n--------------\n";
        returnString += taskName + "\n"
                    + taskDescription + "\n"
                    + taskTime + "\n"
                    + taskDate + "\n"
                    + "--------------";

        return returnString;
    }
}
