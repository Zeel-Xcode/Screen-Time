package services;

import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.screentime.MyApplication;
import com.screentime.utils.AppConstant;
import com.screentime.utils.CommonUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import Model.NewModel;
import SQLiteDatabase.DatabaseHandler2;

/**
 * This Service add time for each app every second if it is on foreground.
 */

public class OnforegroundService extends Service {

    private Timer timer;
    private TimerTask timerTask;
    String currentPackage;
    String appname;
    String[] packages = {"com.facebook.katana", "com.instagram.android", "com.snapchat.android"};
    DatabaseHandler2 databaseHandler2;
    String id;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        try {
            if (intent != null) {
                if (intent.getExtras() != null) {
                    databaseHandler2 = new DatabaseHandler2(this);
                    currentPackage = intent.getExtras().getString("package");
                    if (currentPackage.equals(packages[0])) {
                        appname = "facebook";
                    } else if (currentPackage.equals(packages[1])) {
                        appname = "instagram";
                    } else if (currentPackage.equals(packages[2])) {
                        appname = "snapchat";
                    }
                    startTimer();
                }
            }
            return START_STICKY;
        } catch (NullPointerException ex) {
            Log.e("Message", ex.getMessage());
            return START_REDELIVER_INTENT;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stoptimertask();
    }

    public void startTimer() {
        if (timer == null) {
            //set a new Timer
            timer = new Timer();
            //initialize the TimerTask's job
            initializeTimerTask();
            long starttime = System.currentTimeMillis();
            CommonUtils.savePreferencesInteger(getApplicationContext(), "starttime", starttime);
            CommonUtils.savePreferencesInteger(getApplicationContext(), "endtime", 0);
            setdatanewdatabase(appname);
            //schedule the timer, to wake up every 10 second
            timer.schedule(timerTask, 500, 500);
        }
    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                if (Looper.myLooper() == null) {
                    Looper.prepare();
                }
                if (currentPackage.equals(getRecentApps(getApplicationContext()))) {
                    MyApplication.setCOUNT(MyApplication.getCOUNT() + 500);
                    setTimes(currentPackage);
                } else {
                    MyApplication.setCOUNT(0);
                    stopSelf();
                }
            }
        };
    }

    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
            long endtime = System.currentTimeMillis();
            CommonUtils.savePreferencesInteger(getApplicationContext(), "endtime", endtime);
            ArrayList<NewModel> getdata = databaseHandler2.getAllTime();

            if (getdata.size() > 0) {
                id = getdata.get(getdata.size() - 1).getId();
                updatedatabase(id);
            }
        }
    }

    /**
     * Returns the package name of app on foreground.
     */
    public String getRecentApps(Context context) {
        String currentApp = "";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager usm = (UsageStatsManager) getSystemService(USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,
                    time - 1000 * 1000, time);
            if (appList != null && appList.size() > 0) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                for (UsageStats usageStats : appList) {
                    mySortedMap.put(usageStats.getLastTimeUsed(),
                            usageStats);
                }
                if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    currentApp = mySortedMap.get(
                            mySortedMap.lastKey()).getPackageName();
                }
            }
        } else {
            ActivityManager am = (ActivityManager) getBaseContext().getSystemService(ACTIVITY_SERVICE);
            currentApp = am.getRunningTasks(1).get(0).topActivity.getPackageName();

        }
        return currentApp;
    }


    /**
     * Adds time for each app in every second.
     */
    private void setTimes(String packageName) {
        if (packageName.equals("com.facebook.katana")) {
            long l = Long.parseLong(CommonUtils.getPreferencesString(getApplicationContext(), AppConstant.FCURRENTTIME));
            l = l + 500;
            CommonUtils.savePreferencesString(getApplicationContext(), AppConstant.FCURRENTTIME, l + "");
        } else if (packageName.equals("com.snapchat.android")) {
            long l = Long.parseLong(CommonUtils.getPreferencesString(getApplicationContext(), AppConstant.SCURRENTTIME));
            l = l + 500;
            CommonUtils.savePreferencesString(getApplicationContext(), AppConstant.SCURRENTTIME, l + "");
        } else {
            long l = Long.parseLong(CommonUtils.getPreferencesString(getApplicationContext(), AppConstant.ICURRENTTIME));
            l = l + 500;
            CommonUtils.savePreferencesString(getApplicationContext(), AppConstant.ICURRENTTIME, l + "");
        }
    }

    public void setdatanewdatabase(String title) {
        NewModel newModel = new NewModel();
        long starttime = CommonUtils.getPreferencesInteger(getApplicationContext(), "starttime");
        long endtime = CommonUtils.getPreferencesInteger(getApplicationContext(), "endtime");

        SimpleDateFormat sdf1 = new SimpleDateFormat("ss");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        int start_sec = Integer.parseInt(sdf1.format(starttime));
        int end_sec = Integer.parseInt(sdf1.format(endtime));
        long totalseconds = endtime - starttime;
        int gettotal = Integer.parseInt(sdf1.format(totalseconds));
        String currentdate = sdf2.format(starttime);

        newModel.setAppname(title);
        newModel.setStarttime(starttime);
        newModel.setEndtime(endtime);
        newModel.setTotalsec(gettotal);
        newModel.setCurrentdate(currentdate);
        databaseHandler2.insertRecord(newModel);
    }

    public void updatedatabase(String id) {
        NewModel newModel = new NewModel();
        long starttime = CommonUtils.getPreferencesInteger(getApplicationContext(), "starttime");
        long endtime = CommonUtils.getPreferencesInteger(getApplicationContext(), "endtime");
        SimpleDateFormat sdf1 = new SimpleDateFormat("ss");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        int start_sec = Integer.parseInt(sdf1.format(starttime));
        int end_sec = Integer.parseInt(sdf1.format(endtime));
        String currentdate = sdf2.format(starttime);
        long totalseconds = endtime - starttime;
        int gettotal = Integer.parseInt(sdf1.format(totalseconds));
        newModel.setId(id);
        newModel.setAppname(appname);
        newModel.setStarttime(starttime);
        newModel.setEndtime(endtime);
        newModel.setTotalsec(gettotal);
        newModel.setCurrentdate(currentdate);

        databaseHandler2.updateRecord(newModel);

    }

    private String getCurrentDate() {
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        String dateInString = sdf2.format(System.currentTimeMillis());
        return dateInString;
    }
}
