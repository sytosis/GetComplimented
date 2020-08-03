package com.HotBoyApps.ComplimentBuddy;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

public class StartAlarmsWorker extends Worker {
    public StartAlarmsWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
    }

    @Override
    public Result doWork() {

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("alarm", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> set = sharedPreferences.getStringSet("key", null);

        boolean working = sharedPreferences.getBoolean("working", false);
        working = !working;
        editor.putBoolean("working",working);
        editor.apply();

        if (set != null && set.size() != 0) {
            for (String eachAlarm : set) {
                String[] alarm = eachAlarm.split(",");
                System.out.println(alarm[0]);
                //setup the new intent
                Intent alarmIntent = new Intent(getApplicationContext(),NotificationReceiver.class);
                Calendar calendar = Calendar.getInstance();
                //repeating alarm
                if (alarm[0].equals("false")) {
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());

                    String[] time = alarm[1].split(":");
                    calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
                    calendar.set(Calendar.MINUTE, Integer.parseInt(time[1]));
                    calendar.set(Calendar.SECOND,0);

                    //checking if the date has already passed, if so add a date to it
                    Calendar testCal = Calendar.getInstance();
                    if (calendar.before(testCal)) {
                        calendar.add(Calendar.DATE, 1);
                    }
                    alarmIntent.putExtra("onDate", false);
                    calendar.add(Calendar.DATE, 1);
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddhh.mm");
                    Date date = calendar.getTime();
                    System.out.println(sdf.format(date));
                    alarmIntent.putExtra("nextDate", sdf2.format(date));
                    alarmIntent.putExtra("code", Integer.parseInt(alarm[2]));
                    calendar.add(Calendar.DATE, -1);
                    System.out.println("Repeating, " + calendar.getTime().toString());

                } else if (alarm[0].equals("true")) {

                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                    try {
                        Date date = sdf.parse(alarm[1]);
                        if (date != null) {
                            calendar.setTime(date);
                        }
                        System.out.println(sdf.format(calendar.getTime()));

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    alarmIntent.putExtra("onDate", true);
                    alarmIntent.putExtra("code", Integer.parseInt(alarm[2]));
                    System.out.println("Exact, " + calendar.getTime().toString());
                }

                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), Integer.parseInt(alarm[2]), alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);


            }
        }
        // Indicate whether the work finished successfully with the Result
        return Result.success();
    }
}
