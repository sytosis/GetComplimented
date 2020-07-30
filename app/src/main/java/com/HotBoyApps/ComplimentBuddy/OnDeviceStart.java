package com.HotBoyApps.ComplimentBuddy;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class OnDeviceStart extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("test");
        //start service for setting up alarms
        StartSavedAlarms.enqueueWork(context, new Intent());
    }
}