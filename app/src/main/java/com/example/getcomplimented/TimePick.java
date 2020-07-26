package com.example.getcomplimented;

import android.annotation.SuppressLint;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
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
        timePicker = (TimePicker) view.findViewById(R.id.time_picker_clock);
        datePicker = (DatePicker) view.findViewById(R.id.date_picker);

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
                final Date date = calendar.getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhh.mm",Locale.getDefault());
                intent.putExtra("nextDate",sdf.format(date));
                System.out.println(sdf.format(date));
                calendar.add(Calendar.DATE,-1);

                //the code is used for notification and pending intent
                final int code = r.nextInt();
                intent.putExtra("code",code);
                final PendingIntent pendingIntent = PendingIntent.getBroadcast(Objects.requireNonNull(getActivity()).getApplicationContext(),code,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);

                //store into the Set on mainactivity for future references
                ((MainActivity) getActivity()).addNotification( new ArrayList<Object>() {{

                    add(onDate);

                    if (onDate) {
                        add(new SimpleDateFormat("dd/MM/yyyy hh:mm", Locale.getDefault()).format(date));
                    } else {
                        add(new SimpleDateFormat("hh:mm",Locale.getDefault()).format(date));
                    }

                    add(code);
                }});
                System.out.println(((MainActivity) getActivity()).getNotifications());
                //store into database
                SharedPreferences.Editor alarmEditor = getActivity().getPreferences(MODE_PRIVATE).edit();
                alarmEditor.clear();
                Set<String> set = new HashSet<>();
                List<List<Object>> notificationList = ((MainActivity) getActivity()).getNotifications();
                for (int i = 0; i < notificationList.size(); i++) {
                    String store = notificationList.get(i).get(0) + "," + notificationList.get(i).get(1) + "," + notificationList.get(i).get(2);
                    set.add(store);
                }
                alarmEditor.putStringSet("key", set);
                alarmEditor.apply();

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