package com.example.getcomplimented;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.POWER_SERVICE;
import static com.example.getcomplimented.MainActivity.readJsonFromUrl;

public class NotificationReceiver extends BroadcastReceiver {
    public Context getContext() {
        return context;
    }

    String compliment = "";
    Context context = null;
    NotificationManager notificationManager;
    @Override
    public void onReceive(Context context, Intent intent) {
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String description = "compliment notification";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("compliment", "Get Compliments", importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }
        boolean onDate = intent.getBooleanExtra("onDate",false);
        final int code = intent.getIntExtra("code",0);
        this.context = context;

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Running thread to get compliment!");
                JSONObject json;
                try {
                    json = readJsonFromUrl("https://complimentr.com/api");
                    compliment = json.get("compliment").toString();
                } catch (Exception e) {
                    compliment = "No internet connection for this compliment :(";
                    e.printStackTrace();
                }
                System.out.println("compliment finalized here: " + compliment);
                System.out.println(compliment);
                SharedPreferences sharedPreferences = getContext().getSharedPreferences("alarm", MODE_PRIVATE);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), "compliment")
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle(sharedPreferences.getString("name","Unknown") + " has a compliment for you")
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(compliment))
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL);
                notificationManager.notify(code,builder.build());
                Log.i("Notify", "Alarm");
                PowerManager power = (PowerManager) getContext().getSystemService(POWER_SERVICE);
                PowerManager.WakeLock wl = power.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "myapp:notificationWake");
                wl.acquire(3000);
            }

        });
        thread.start();

        if (!onDate) {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhh.mm");
            Calendar calendar = Calendar.getInstance();
            try {
                Date date = sdf.parse(Objects.requireNonNull(intent.getStringExtra("nextDate")));
                assert date != null;
                calendar.setTime(date);
                System.out.println(calendar.getTime().toString());
            } catch (ParseException e) {
                e.printStackTrace();
                return;
            }
            intent.putExtra("onDate",false);
            calendar.add(Calendar.DATE,1);
            Date date = calendar.getTime();
            intent.putExtra("nextDate",sdf.format(date));
            System.out.println(sdf.format(date));
            calendar.add(Calendar.DATE,-1);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(Objects.requireNonNull(context).getApplicationContext(),code,intent,PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else {
            final List<List<String>> alarmList = new ArrayList<>();
            SharedPreferences sharedPreferences = Objects.requireNonNull(getContext().getSharedPreferences("alarm", MODE_PRIVATE));
            Set<String> set = sharedPreferences.getStringSet("key", new HashSet<String>());
            for (String alarm : set) {
                String alarmItems[] = alarm.split(",");
                List<String> convertedItems = new ArrayList<>();
                convertedItems.addAll(Arrays.asList(alarmItems));
                alarmList.add(convertedItems);
            }
            //remove the entry with the deleted pending intent
            for (int i = 0; i < alarmList.size(); i++) {
                if (Integer.parseInt(alarmList.get(i).get(2)) == code) {
                    System.out.println(alarmList.remove(i).get(1) + " Alarm removed");
                    System.out.println(alarmList);
                    System.out.println(alarmList.size());
                    //store into database
                    SharedPreferences.Editor alarmEditor = getContext().getSharedPreferences("alarm", MODE_PRIVATE).edit();
                    alarmEditor.clear();
                    Set<String> finalSet = new HashSet<>();
                    for (int j = 0; j < alarmList.size(); j++) {
                        System.out.println(alarmList.get(j));
                        String store = alarmList.get(j).get(0) + "," + alarmList.get(j).get(1) + "," + alarmList.get(j).get(2);
                        finalSet.add(store);
                    }
                    alarmEditor.putStringSet("key", finalSet);
                    alarmEditor.apply();
                    break;
                }
            }



        }


    }


}
