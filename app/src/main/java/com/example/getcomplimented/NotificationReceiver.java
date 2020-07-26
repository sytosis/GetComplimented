package com.example.getcomplimented;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import static android.content.Context.ALARM_SERVICE;
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
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
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
                NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), "compliment")
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle("Have a compliment!")
                        .setContentText(compliment)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setAutoCancel(true);
                notificationManager.notify(code,builder.build());
                Log.i("Notify", "Alarm");
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
            alarmManager.setExact(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
        }


    }


}
