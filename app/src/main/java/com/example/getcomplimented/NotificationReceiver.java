package com.example.getcomplimented;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String description = "compliment notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("compliment", "Get Compliments", importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }
        Intent repeating_intent = new Intent(context,ComplimentActivity.class);
        repeating_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context,42,repeating_intent,PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "compliment")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Have a compliment!")
                .setContentText("You have very beautiful feet")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        if (intent.getAction().equals("MY_NOTIFICATION_MESSAGE")) {
            System.out.println("GOT TO BUILD");
            notificationManager.notify(42,builder.build());
            Log.i("Notify", "Alarm");
        }
    }


}
