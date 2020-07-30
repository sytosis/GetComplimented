package com.HotBoyApps.ComplimentBuddy;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.MODE_PRIVATE;

public class TimePick extends Fragment {
    TimePicker timePicker;
    DatePicker datePicker;
    boolean onDate;
    Random r = new Random();
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.time_picker, container, false);

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        timePicker = view.findViewById(R.id.time_picker_clock);
        datePicker = view.findViewById(R.id.date_picker);

        //close button
        view.findViewById(R.id.date_close_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(TimePick.this)
                        .navigate(R.id.timePicker_To_FirstFragment2);
            }
        });

        //finalize alarm button, which changes based on weather they're on date exact or repeating
        view.findViewById(R.id.date_select_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY,timePicker.getHour());
                calendar.set(Calendar.MINUTE,timePicker.getMinute());
                calendar.set(Calendar.SECOND,0);
                if (onDate) {
                    calendar.set(Calendar.DAY_OF_MONTH,datePicker.getDayOfMonth());
                    calendar.set(Calendar.MONTH,datePicker.getMonth());
                    calendar.set(Calendar.YEAR,datePicker.getYear());
                }
                Intent intent = new Intent(Objects.requireNonNull(getActivity()).getApplicationContext(),NotificationReceiver.class);
                intent.putExtra("onDate",onDate);

                calendar.add(Calendar.DATE,1);
                final Date nextDate = calendar.getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhh.mm",Locale.getDefault());
                intent.putExtra("nextDate",sdf.format(nextDate));
                System.out.println(sdf.format(nextDate));
                calendar.add(Calendar.DATE,-1);
                final Date date = calendar.getTime();
                //the code is used for notification and pending intent
                final int code = r.nextInt();
                intent.putExtra("code",code);
                final PendingIntent pendingIntent = PendingIntent.getBroadcast(Objects.requireNonNull(getActivity()).getApplicationContext(),code,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);

                //store into the Set on mainactivity for future references
                String stringOnDate = String.valueOf(onDate);
                String stringDate = "";
                if (onDate) {
                    stringDate = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(date);
                } else {
                    stringDate = new SimpleDateFormat("HH:mm",Locale.getDefault()).format(date);
                }

                String stringCode = String.valueOf(code);
                //store into database, set up getting alarms from database
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("alarm", MODE_PRIVATE);
                Set<String> set = sharedPreferences.getStringSet("key", new HashSet<String>());
                Set<String> set2 = new HashSet<>(set);
                SharedPreferences.Editor alarmEditor = sharedPreferences.edit();
                String store = stringOnDate + "," + stringDate + "," + stringCode;
                set2.add(store);
                alarmEditor.putStringSet("key",set2);
                alarmEditor.apply();

                System.out.println(sharedPreferences.getStringSet("key", new HashSet<String>()));
                System.out.println(calendar.getTime().toString());
                NavHostFragment.findNavController(TimePick.this)
                        .navigate(R.id.timePicker_To_FirstFragment2);
            }
        });

        //switch alarm type button
        view.findViewById(R.id.switch_alarm_type_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!onDate) {
                    timePicker.setVisibility(View.GONE);
                    datePicker.setVisibility(View.VISIBLE);
                } else {
                    timePicker.setVisibility(View.VISIBLE);
                    datePicker.setVisibility(View.GONE);
                }
                onDate = !onDate;
            }
        });
    }

}