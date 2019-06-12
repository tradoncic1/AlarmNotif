package com.example.alarmnotif20;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LocationListViewAdapter extends BaseAdapter {
    static final String TAG = "LocationListViewAdapter";

    Context context;
    List<LocationTask> locationTasks = new ArrayList<>();

    public LocationListViewAdapter(Context context, List<LocationTask> locationTasks) {
        this.context = context;
        this.locationTasks = locationTasks;
    }

    @Override
    public int getCount() {
        return locationTasks.size();
    }

    @Override
    public Object getItem(int position) {
        return locationTasks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return locationTasks.get(position).getLocationTaskId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(this.context);
        convertView = inflater.inflate(R.layout.location_task_item, parent, false);
        final LocationTask task = locationTasks.get(position);

        TextView taskName = convertView.findViewById(R.id.title);
        TextView taskDescription = convertView.findViewById(R.id.description);
        TextView taskAddress = convertView.findViewById(R.id.address);

        Button deleteBtn = convertView.findViewById(R.id.deleteBtn);

        taskName.setText(task.getTaskName());
        taskDescription.setText(task.getTaskDescription());

        LatLng latLng = new LatLng(task.getLatitude(), task.getLongitude());

        String addressLine = getAddress(latLng);
        taskAddress.setText(addressLine);

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LocationAlertIntentService.class);
                intent.putExtra("taskId", task.getLocationTaskId());

                PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                pendingIntent.cancel();

                MyDatabase.getDatabase(context).locationDAO().deleteLocationTask(task);

                ((Activity)context).finish();
                ((Activity)context).startActivity(new Intent(context, MainActivity.class));
                ((Activity)context).overridePendingTransition(0, 0);
                notifyDataSetChanged();
            }
        });


        return convertView;
    }

    private String getAddress(LatLng latLng) {
        Log.d(TAG, "getAddress: \n" + latLng.latitude + ", " + latLng.longitude);

        StringBuilder result = new StringBuilder();
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                result.append(address.getAddressLine(0));
                //String addressLine[] = address.getAddressLine(0).split(",");
                //result.append(addressLine[0] + ", " + addressLine[1] + ", " + address.getCountryCode());
            }
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }

        Log.d(TAG, "getAddress: \n" + result.toString());

        return result.toString();
    }
}
