package com.example.getcomplimented;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import static com.example.getcomplimented.MainActivity.readJsonFromUrl;

public class refillCompliments extends TimerTask {
   public List<String> complimentList = new ArrayList();
   MainActivity ma;
   boolean firstRun = false;

    public void run() {
            Thread thread = new Thread(new Runnable(){
                @Override
                public void run() {
                    if (complimentList.size() < 40) {
                    try {
                        JSONObject json = readJsonFromUrl("https://complimentr.com/api");
                        System.out.println(complimentList.size());
                        System.out.println(json.get("compliment"));
                        final String compliment = json.get("compliment").toString();
                        complimentList.add(compliment);
                        if (!firstRun) {
                            ma.toggleCompliment();
                            System.out.println("Toggling compliment");
                            firstRun = true;
                        }
                    } catch (Exception ignored) {

                    }
                }
            }
        });
            thread.start();
    }

    public void setMain(MainActivity main) {
        ma = main;
    }

    public List<String> getComplimentList() {
        return this.complimentList;
    }
}
