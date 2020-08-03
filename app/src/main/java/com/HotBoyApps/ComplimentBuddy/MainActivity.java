package com.HotBoyApps.ComplimentBuddy;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import android.os.StrictMode;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class MainActivity extends AppCompatActivity {
    List<String> complimentList = new ArrayList<>();
    refillCompliments rc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        //restart alarms if it hasnt already
        startSavedAlarms();
        Timer timer = new Timer();
        timer.schedule(rc = new refillCompliments(), 0, 100);
        rc.setMain(this);
        complimentList = rc.getComplimentList();



    }


    public void startSavedAlarms() {
        WorkRequest uploadWorkRequest =
                new OneTimeWorkRequest.Builder(StartAlarmsWorker.class)
                        .build();
        WorkManager
                .getInstance(this)
                .enqueue(uploadWorkRequest);
    }
    public void toggleCompliment(View view) {
        if (complimentList.size() > 0) {
            final String compliment = complimentList.remove(0);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView textView = findViewById(R.id.textview_first);
                    textView.setText(compliment);
                }
            });
            SharedPreferences sharedPreferences = getSharedPreferences("alarm", MODE_PRIVATE);
            final boolean working = sharedPreferences.getBoolean("working", false);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView textView = findViewById(R.id.textview_first);
                    textView.setText(String.valueOf(working));
                }
            });

        }
    }

    public void toggleCompliment() {

        if (complimentList.size() > 0) {
            final String compliment = complimentList.get(0);
            complimentList.remove(0);
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    TextView textView = findViewById(R.id.textview_first);
                    if (textView != null) {
                        textView.setText(compliment);
                    }

                }
            });
        }
    }

    //currently unused
    public String popCompliment() {
        if (complimentList.size() > 0) {
            return complimentList.remove(0);
        } else {
            return "0";
        }
    }


    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String jsonText = readAll(rd);
            return new JSONObject(jsonText);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}