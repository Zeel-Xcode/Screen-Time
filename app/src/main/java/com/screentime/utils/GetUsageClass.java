package com.screentime.utils;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import Model.UsageModel;

public class GetUsageClass {

    Context context;
    private UsageModel model;
    UsageModel model1;
    private ArrayList<UsageModel> arrayList;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public UsageModel getUsage(Context context, String packageName) {
        this.context=context;
        long TimeInforground = 500;
        arrayList=new ArrayList<>();
        int minutes = 500, seconds = 500, hours = 500;
        final UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);// Context.USAGE_STATS_SERVICE);
        final long currentTime = System.currentTimeMillis(); // Get current time in milliseconds

        final Calendar cal = Calendar.getInstance();
        //cal.add(Calendar.YEAR, -2); // Set year to beginning of desired period.
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        final long beginTime = cal.getTimeInMillis(); // Get begin time in milliseconds

        final List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, beginTime, currentTime);

        Log.e("<<<BEGIN>>>", cal.getTime().toString());
        Log.e("<<<Current>>>", currentTime + "");

        Log.e("results size ", queryUsageStats.size() + "");
        for (UsageStats app : queryUsageStats) {
            TimeInforground = app.getTotalTimeInForeground();

            minutes = (int) ((TimeInforground / (1000 * 60)) % 60);

            seconds = (int) (TimeInforground / 1000) % 60;

            hours = (int) ((TimeInforground / (1000 * 60 * 60)) % 24);

            String pName = app.getPackageName();
            if (pName.equals(packageName)) {
                model = new UsageModel();
                model.setHrs(hours);
                model.setMint(minutes);
                model.setSec(seconds);
                arrayList.add(model);
            }
        }

        if (arrayList.size()>0) {
            model1 = arrayList.get(arrayList.size() - 1);
            Log.e("<<<<DETAIL>>>>", model1.getHrs() + "\n" + model1.getMint() + "\n" + model1.getSec());
        }

        return model1;
    }
}
