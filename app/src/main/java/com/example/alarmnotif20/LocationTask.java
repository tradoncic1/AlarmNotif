package com.example.alarmnotif20;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "locationTask")
public class LocationTask {
    @PrimaryKey(autoGenerate = true)
    int locationTaskId;

    String taskName;
    String taskDescription;
    double latitude;
    double longitude;

    public LocationTask(int locationTaskId, String taskName, String taskDescription, double latitude, double longitude) {
        this.locationTaskId = locationTaskId;
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Ignore
    public LocationTask(String taskName, String taskDescription, double latitude, double longitude) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getLocationTaskId() {
        return locationTaskId;
    }

    public void setLocationTaskId(int locationTaskId) {
        this.locationTaskId = locationTaskId;
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

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }


    @Override
    public String toString() {
        String returnString = "\n--------------\n";
        returnString += taskName + "\n"
                + taskDescription + "\n"
                + latitude + "\n"
                + longitude + "\n"
                + "--------------";

        return returnString;
    }
}
