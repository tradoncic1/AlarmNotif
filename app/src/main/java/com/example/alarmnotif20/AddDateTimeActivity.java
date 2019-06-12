package com.example.alarmnotif20;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class AddDateTimeActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    static final String TAG = "AddDateTimeActivity";

    Button addBtn;
    Button cancelBtn;
    Button setTimeBtn;

    EditText taskNameInput;
    EditText taskDescInput;

    TextView timeTextView;

    CalendarView calendarView;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyy");
    String dateStr;

    SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm");
    String taskTime;

    int hours;
    int mins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_date_time);

        addBtn = findViewById(R.id.addBtn);
        cancelBtn = findViewById(R.id.cancelBtn);
        setTimeBtn = findViewById(R.id.setTime);

        taskNameInput = findViewById(R.id.taskNameInput);
        taskDescInput = findViewById(R.id.taskDescInput);
        timeTextView = findViewById(R.id.time);

        calendarView = findViewById(R.id.calendarView);

        timeTextView.setText(simpleTimeFormat.format(new Date().getTime()));

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                Calendar calendar = new GregorianCalendar(year, month, dayOfMonth);
                dateStr = simpleDateFormat.format(calendar.getTime());
            }
        });

        setTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogFragment = new TimePickerFragment();
                dialogFragment.show(getSupportFragmentManager(), "Time Picker");
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (taskNameInput.getText().toString().equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddDateTimeActivity.this);

                    builder.setTitle("Enter a task title")
                            .setMessage("In order to create a task, it must have a title")
                            .setPositiveButton("Okay", null)
                            .create()
                            .show();
                } else {
                    if (taskDescInput.getText().toString().equals("")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(AddDateTimeActivity.this);

                        builder.setTitle("No task description!")
                                .setMessage("You can create a task without a description, but it helps to have one!")
                                .setPositiveButton("Create anyway", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        createDateTask();
                                    }
                                })
                                .setNegativeButton("Write description", null)
                                .create()
                                .show();
                    } else {
                        createDateTask();
                    }
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void createDateTask() {
        if (dateStr == null)
            dateStr = simpleDateFormat.format(Calendar.getInstance().getTime());

        if (taskTime == null) {
            taskTime = simpleTimeFormat.format(Calendar.getInstance().getTime());
            String[] parsedTime = taskTime.split(":");

            hours = Integer.parseInt(parsedTime[0]);
            mins = Integer.parseInt(parsedTime[1]);
        }

        DateTimeTask task =
                new DateTimeTask(taskNameInput.getText().toString(), taskDescInput.getText().toString(), taskTime, dateStr);

        long addedID = MyDatabase.getDatabase(getApplicationContext()).dateTimeDAO().addDateTask(task);

        Log.d(TAG, "createDateTask: added dateTime task: ID: " + addedID + "\n" + task.toString());


        long timeInMillis = Calendar.getInstance().getTimeInMillis();
        SimpleDateFormat returnFormat = new SimpleDateFormat("HH:mm dd/MM/yyy");
        try {
            Date finalDate = returnFormat.parse(hours+":"+mins + " " + dateStr);
            timeInMillis = finalDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "createDateTask: date to convert formatted : " + new SimpleDateFormat().format(new Date(timeInMillis)));
        Log.d(TAG, "createDateTask: date to convert : " + hours+":"+mins);

        AlarmManager manager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        Intent alarmIntent = new Intent(this.getApplicationContext(), AlarmReceiver.class);
        alarmIntent.putExtra("dateTaskId", (int) addedID);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), (int) addedID, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        manager.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);

        Toast.makeText(AddDateTimeActivity.this,
                "Timed reminder added!",
                Toast.LENGTH_SHORT).show();

        finish();
        Intent backIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(backIntent);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        timeTextView.setText(String.format("%02d:%02d", hourOfDay, minute));

        taskTime = hourOfDay + ":" + minute;
        hours = hourOfDay;
        mins = minute;
    }
}
