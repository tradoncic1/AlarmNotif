package com.example.alarmnotif20;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class TimeListViewAdapter extends BaseAdapter {
    private Context context;
    private List<DateTimeTask> dateTimeTasks = new ArrayList<>();

    public TimeListViewAdapter(Context context, List<DateTimeTask> dateTimeTasks) {
        this.context = context;
        this.dateTimeTasks = dateTimeTasks;
    }

    @Override
    public int getCount() {
        return dateTimeTasks.size();
    }

    @Override
    public Object getItem(int position) {
        return dateTimeTasks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return dateTimeTasks.get(position).getDateTaskId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(this.context);
        convertView = inflater.inflate(R.layout.time_task_item, parent, false);
        final DateTimeTask dateTimeTask = dateTimeTasks.get(position);

        TextView taskName = convertView.findViewById(R.id.title);
        TextView taskDescription = convertView.findViewById(R.id.description);
        TextView taskDate = convertView.findViewById(R.id.time);
        Button deleteBtn = convertView.findViewById(R.id.deleteBtn);

        taskName.setText(dateTimeTask.getTaskName());
        taskDescription.setText(dateTimeTask.getTaskDescription());
        taskDate.setText("Date: " + dateTimeTask.getTaskDate() + " | Time: " + dateTimeTask.getTaskTime());
        //taskDate.setText("ID: " + dateTimeTask.getDateTaskId());

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent alarmIntent = new Intent(context, AlarmReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, dateTimeTask.getDateTaskId(), alarmIntent, 0);

                alarmManager.cancel(pendingIntent);


                MyDatabase.getDatabase(context).dateTimeDAO().deleteDateTask(dateTimeTask);

                ((Activity)context).finish();
                ((Activity)context).startActivity(new Intent(context, MainActivity.class));
                ((Activity)context).overridePendingTransition(0, 0);
                notifyDataSetChanged();
            }
        });

        return convertView;
    }
}
