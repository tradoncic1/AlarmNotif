package com.example.alarmnotif20;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;

public class LocationFragment extends Fragment {

    ListView listView;

    Button deleteAllBtn;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView = view.findViewById(R.id.locationListView);

        deleteAllBtn = view.findViewById(R.id.deleteAllLocation);
        deleteAllBtn.setVisibility(View.GONE);

        final List<LocationTask> locationTasks = MyDatabase.getDatabase(getContext()).locationDAO().getAllTasks();

        final LocationListViewAdapter adapter = new LocationListViewAdapter(getContext(), locationTasks);
        listView.setAdapter(adapter);

        if (!locationTasks.isEmpty())
            deleteAllBtn.setVisibility(View.VISIBLE);

        deleteAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                builder.setTitle("Delete All?")
                        .setMessage("Are you sure you want to delete ALL location reminders?")
                        .setPositiveButton("Yes, Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MyDatabase.getDatabase(getContext()).locationDAO().deleteAll(locationTasks);

                                ((Activity)getContext()).finish();
                                ((Activity)getContext()).startActivity(new Intent(getContext(), MainActivity.class));
                                ((Activity)getContext()).overridePendingTransition(0, 0);
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("No, Don't Delete", null)
                        .create()
                        .show();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.location_fragment_layout, container, false);
    }
}
