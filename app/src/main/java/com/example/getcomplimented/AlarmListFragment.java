package com.example.getcomplimented;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;


public class AlarmListFragment extends Fragment {
    TableLayout alarmTableLayout;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        return inflater.inflate(R.layout.alarm_list_fragment, container, false);

    }

    @SuppressLint("SetTextI18n")
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final List<List<Object>> alarmList = new ArrayList<>(((MainActivity) Objects.requireNonNull(getActivity())).getNotifications());
        alarmTableLayout = Objects.requireNonNull(getView()).findViewById(R.id.alarmTableLayout);
        // Inflate the layout for this fragment
        for (int i = 0; i < alarmList.size(); i++) {
            final TableRow alarmRow = new TableRow(getContext());
            alarmRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));

            final TextView alarmInfo = new TextView(getContext());
            if (alarmList.get(i).get(0).toString().equals("false")) {
                alarmInfo.setText("Repeating: " + alarmList.get(i).get(1).toString());
            } else {
                alarmInfo.setText("Single :" + alarmList.get(i).get(1).toString());
            }
            alarmInfo.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT,Gravity.CENTER_HORIZONTAL));

            final Button alarmDeleteButton = new Button(getContext());
            alarmDeleteButton.setText("delete");
            alarmDeleteButton.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL));
            final int z = i;
            final int requestCode = Integer.parseInt(alarmList.get(z).get(2).toString());
            alarmDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alarmTableLayout.removeView((TableRow) view.getParent());
                    AlarmManager alarmManager = (AlarmManager) Objects.requireNonNull(getActivity()).getSystemService(Context.ALARM_SERVICE);
                    Intent intent = new Intent(getActivity().getApplicationContext(),NotificationReceiver.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(
                            getContext(), requestCode, intent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.cancel(pendingIntent);
                    List<List<Object>> realAlarmList = ((MainActivity) Objects.requireNonNull(getActivity())).getNotifications();
                    //remove the entry with the deleted pending intent
                    for (int i = 0; i < realAlarmList.size(); i++) {
                        if (Integer.parseInt(realAlarmList.get(i).get(2).toString()) == requestCode) {
                            realAlarmList.remove(i);
                            System.out.println(alarmList.get(z).get(1).toString() + " Alarm removed");
                            System.out.println(realAlarmList);

                            //store into database
                            SharedPreferences.Editor alarmEditor = getActivity().getPreferences(MODE_PRIVATE).edit();
                            alarmEditor.clear();
                            Set<String> set = new HashSet<>();
                            for (int j = 0; j < realAlarmList.size(); j++) {
                                String store = realAlarmList.get(j).get(0) + "," + realAlarmList.get(i).get(1) + "," + realAlarmList.get(i).get(2);
                                set.add(store);
                            }
                            alarmEditor.putStringSet("key", set);
                            alarmEditor.apply();
                            break;
                        }
                    }



                }
            });

            alarmRow.addView(alarmInfo);
            alarmRow.addView(alarmDeleteButton);
            alarmTableLayout.addView(alarmRow);

        }

        view.findViewById(R.id.alarm_list_close_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(AlarmListFragment.this)
                        .navigate(R.id.action_AlarmListFragment_to_FirstFragment);
            }
        });


    }


}