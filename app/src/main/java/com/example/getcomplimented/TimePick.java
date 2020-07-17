package com.example.getcomplimented;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
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
import java.util.Objects;

import static android.content.Context.ALARM_SERVICE;

public class TimePick extends Fragment {
    TimePicker timePicker;
    DatePicker datePicker;
    boolean onDate;
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

        //finalize alarm button, which changes based on of theyre on date exact or repeating
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
                Date date = calendar.getTime();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhh.mm");
                intent.putExtra("nextDate",sdf.format(date));
                System.out.println(sdf.format(date));
                calendar.add(Calendar.DATE,-1);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(Objects.requireNonNull(getActivity()).getApplicationContext(),42,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);

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