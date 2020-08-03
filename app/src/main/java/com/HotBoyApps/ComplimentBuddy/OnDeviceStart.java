package com.HotBoyApps.ComplimentBuddy;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;


public class OnDeviceStart extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("test");
        //start service for setting up alarms
        //StartSavedAlarms.enqueueWork(context, new Intent());

        ((MainActivity) context.getApplicationContext()).startSavedAlarms();
    }
}