package services;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;


import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.screentime.AlertDialog2Activity;
import com.screentime.HomeActivity;
import com.screentime.R;
import com.screentime.WarninghDialogActivity;
import com.screentime.utils.AppConstant;
import com.screentime.utils.CommonUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

/**
 * This Service calculates time spend by user on each app.
 * It runs on background in every 10 seconds
 */

public class GetUsageService1 extends Service {

    private Timer timer;
    private TimerTask timerTask;
    String currentPackage;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        startTimer();
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        stoptimertask();
        Intent intent = new Intent("com.pause");
        sendBroadcast(intent);
    }


    /**
     * When app is killed it runs the service on foreground.
     */
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent notificationIntent = new Intent(getBaseContext(), HomeActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.pause)
                .setContentTitle("Pause")
                .setContentText("Counting time you spent on phone.")
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();

        startForeground(1337, notification);
    }


    /**
     * Calculates the usage of app if it is on foreground and generate popup.
     */
    private void getUsage(){
        if (isOwn(currentPackage)) {
             startService(new Intent(getApplicationContext(),OnforegroundService.class).putExtra("package", currentPackage));
            if (isEnable(currentPackage)) {
                if (isTimeOver(getTimes(currentPackage), getLimitTime(currentPackage))) {
                    setNewLimit(currentPackage);
                    startActivity(new Intent(getApplicationContext(), AlertDialog2Activity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    CommonUtils.savePreferencesString(getApplicationContext(), setAverageCount(currentPackage), "1");
                } else if (isAverageTime(getTimes(currentPackage), getAverageTime(currentPackage))) {
                    if (isOwn(currentPackage)) {
                        if (!getAverageCount(currentPackage).equals("1")) {
                            startActivity(new Intent(getApplicationContext(), WarninghDialogActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                            CommonUtils.savePreferencesString(getApplicationContext(), setAverageCount(currentPackage), "1");
                        }
                    }
                }
            }
        }
    }

    /**
     * Calculate if is limit time has exceeded.
     */
    private boolean isTimeOver(long count, String limit) {
        boolean time = false;
        long given = getTimeInMil(limit);
        if (count >= given) {
            time = true;
        } else {
            time = false;
        }
        return time;
    }

    /**
     * Calculate if is average time has exceeded.
     */
    private boolean isAverageTime(long count, String limit) {
        boolean time = false;
        long given = getTimeInMil(limit);
        if (count >= given) {
            time = true;
        } else {
            time = false;
        }
        return time;
    }


    /**
     * Convert time from String into milliseconds.
     */
    private long getTimeInMil(String time) {
        long mili = 0;
        String t[] = time.split(":");
        int h = Integer.parseInt(t[0]);
        int m = Integer.parseInt(t[1]);
        if (h > 0 && m == 0) {
            mili = h * 60 * 60 * 1000;
        } else if (h == 0 && m > 0) {
            mili = m * 60 * 1000;
        } else if (h > 0 && m > 0) {
            mili = (h * 60 * 60 * 1000) + (m * 60 * 1000);
        }
        return mili;
    }


    /**
     * Returns the package name of app on foreground.
     */
    public String getRecentApps(Context context) {
        String currentApp = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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
     * Starts the timer.
     */
    public void startTimer() {

        if (timer == null) {
            //set a new Timer
            timer = new Timer();

            //initialize the TimerTask's job
            initializeTimerTask();

            //schedule the timer, to wake up every 10 second
            timer.schedule(timerTask, 10000, 10000);
        }
    }


    /**
     * Initialize the timer.
     */
    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                if (Looper.myLooper() == null) {
                    Looper.prepare();
                }
                isReset();
                currentPackage = getRecentApps(getApplicationContext());
                CommonUtils.savePreferencesString(getApplicationContext(),"appname",currentPackage);
                getUsage();
            }
        };
    }

    /**
     * Stops the timer.
     */
    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }


    /**
     * Returns the limit time for each app.
     */
    private String getLimitTime(String packageName) {
        String time = "";
        if (packageName.equals("com.facebook.katana")) {
            if (CommonUtils.getPreferencesString(getApplicationContext(), AppConstant.FB_TIME).equals("")) {
                time = AppConstant.FB_TIME;
            } else {
                time = CommonUtils.getPreferencesString(getApplicationContext(), AppConstant.FB_TIME);
            }
            return time;
        } else if (packageName.equals("com.snapchat.android")) {
            if (CommonUtils.getPreferencesString(getApplicationContext(), AppConstant.SNAPCHAT_TIME).equals("")) {
                time = AppConstant.SNAPCHAT_TIME;
            } else {
                time = CommonUtils.getPreferencesString(getApplicationContext(), AppConstant.SNAPCHAT_TIME);
            }
            return time;
        } else {
            if (CommonUtils.getPreferencesString(getApplicationContext(), AppConstant.INSTA_TIME).equals("")) {
                time = AppConstant.INSTA_TIME;
            } else {
                time = CommonUtils.getPreferencesString(getApplicationContext(), AppConstant.INSTA_TIME);
            }
            return time;
        }
    }


    /**
     * Returns the average time for each app.
     */
    private String getAverageTime(String packageName) {
        if (packageName.equals("com.facebook.katana")) {
            return CommonUtils.getPreferencesString(this, AppConstant.FB_ATIME);
        } else if (packageName.equals("com.snapchat.android")) {
            return CommonUtils.getPreferencesString(this, AppConstant.SNAPCHAT_ATIME);
        } else {
            return CommonUtils.getPreferencesString(this, AppConstant.INSTA_ATIME);
        }
    }

    /**
     * Check if app on foreground need to calculated.
     */
    private boolean isOwn(String packageName) {
        if (packageName.equals("com.facebook.katana") || packageName.equals("com.snapchat.android") || packageName.equals("com.instagram.android")) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * Returns Average time for each app.
     */
    private String getAverageCount(String packageName) {
        if (packageName.equals("com.facebook.katana")){
            return CommonUtils.getPreferencesString(getApplicationContext(), AppConstant.FBAVERAGE_COUNT);
        }else if (packageName.equals("com.snapchat.android")){
            return CommonUtils.getPreferencesString(getApplicationContext(), AppConstant.SNAPAVERAGE_COUNT);
        }else {
            return CommonUtils.getPreferencesString(getApplicationContext(), AppConstant.INSTAAVERAGE_COUNT);
        }
    }

    private String setAverageCount(String packageName) {
        if (packageName.equals("com.facebook.katana")){
            return AppConstant.FBAVERAGE_COUNT;
        }else if (packageName.equals("com.snapchat.android")){
            return AppConstant.SNAPAVERAGE_COUNT;
        }else {
            return AppConstant.INSTAAVERAGE_COUNT;
        }
    }


    /**
     * Checks which app is enable and which is disable.
     */
    private boolean isEnable(String packageName) {
        boolean ret=false;
        if (packageName.equals("com.facebook.katana")){
            if (!CommonUtils.getPreferencesString(getApplicationContext(),AppConstant.FBACTIVE).equals("")){
                ret=true;
            }
        }else if (packageName.equals("com.snapchat.android")){
            if (!CommonUtils.getPreferencesString(getApplicationContext(),AppConstant.SNAPACTIVE).equals("")){
                ret=true;
            }
        }else {
            if (!CommonUtils.getPreferencesString(getApplicationContext(),AppConstant.INSTAACTIVE).equals("")){
                ret=true;
            }
        }

        return ret;
    }

    /**
     * Resets all the value at 12 am.
     */
    private void isReset() {
        String preDate = CommonUtils.getPreferencesString(getApplicationContext(), AppConstant.CURRENT_DATE);
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String date = df.format(new Date());
        if (preDate.equals("")) {
            CommonUtils.savePreferencesString(getApplicationContext(), AppConstant.CURRENT_DATE, date);
        } else if (preDate.equals(date)) {
        } else {
            CommonUtils.savePreferencesString(getApplicationContext(), AppConstant.CURRENT_DATE, date);
            CommonUtils.savePreferencesString(getApplicationContext(), AppConstant.FBAVERAGE_COUNT, "0");
            CommonUtils.savePreferencesString(getApplicationContext(), AppConstant.INSTAAVERAGE_COUNT, "0");
            CommonUtils.savePreferencesString(getApplicationContext(), AppConstant.SNAPAVERAGE_COUNT, "0");
            CommonUtils.savePreferencesString(getApplicationContext(), AppConstant.FB_TIME, "");
            CommonUtils.savePreferencesString(getApplicationContext(), AppConstant.INSTA_TIME, "");
            CommonUtils.savePreferencesString(getApplicationContext(), AppConstant.SNAPCHAT_TIME, "");
            CommonUtils.savePreferencesString(getApplicationContext(), AppConstant.FCURRENTTIME, "0");
            CommonUtils.savePreferencesString(getApplicationContext(), AppConstant.SCURRENTTIME, "0");
            CommonUtils.savePreferencesString(getApplicationContext(), AppConstant.ICURRENTTIME, "0");
        }
    }


    /**
     * Sets new time limit for each app.
     */
    private void setNewLimit(String packageName) {
        if (packageName.equals("com.facebook.katana")) {
            String pre = CommonUtils.getPreferencesString(getApplicationContext(), AppConstant.FB_TIME);
            long given = 0;
            if (pre.equals("")) {
                given = Long.parseLong(CommonUtils.getPreferencesString(getApplicationContext(),AppConstant.FCURRENTTIME));
            } else {
                given = getTimeInMil(pre);
            }
            given += 900000;
            int minutes = (int) ((given / (1000 * 60)) % 60);

            int hours = (int) ((given / (1000 * 60 * 60)) % 24);
            CommonUtils.savePreferencesString(getApplicationContext(), AppConstant.FB_TIME, hours + ":" + minutes);
        } else if (packageName.equals("com.snapchat.android")) {
            String pre = CommonUtils.getPreferencesString(getApplicationContext(), AppConstant.SNAPCHAT_TIME);
            long given = 0;
            if (pre.equals("")) {
                given = Long.parseLong(CommonUtils.getPreferencesString(getApplicationContext(),AppConstant.SCURRENTTIME));
            } else {
                given = getTimeInMil(pre);
            }
            given += 900000;
            int minutes = (int) ((given / (1000 * 60)) % 60);

            int hours = (int) ((given / (1000 * 60 * 60)) % 24);
            CommonUtils.savePreferencesString(getApplicationContext(), AppConstant.SNAPCHAT_TIME, hours + ":" + minutes);
        } else {
            String pre = CommonUtils.getPreferencesString(getApplicationContext(), AppConstant.INSTA_TIME);
            long given = 0;
            if (pre.equals("")) {
                given = Long.parseLong(CommonUtils.getPreferencesString(getApplicationContext(),AppConstant.ICURRENTTIME));
            } else {
                given = getTimeInMil(pre);
            }
            given += 900000;
            int minutes = (int) ((given / (1000 * 60)) % 60);

            int hours = (int) ((given / (1000 * 60 * 60)) % 24);
            CommonUtils.savePreferencesString(getApplicationContext(), AppConstant.INSTA_TIME, hours + ":" + minutes);
        }
    }


    /**
     * Gets current used time for each app.
     */
    private long getTimes(String packageName) {
        if (packageName.equals("com.facebook.katana")) {
            long l= Long.parseLong(CommonUtils.getPreferencesString(getApplicationContext(),AppConstant.FCURRENTTIME));
            return l;
        } else if (packageName.equals("com.snapchat.android")) {
            long l= Long.parseLong(CommonUtils.getPreferencesString(getApplicationContext(),AppConstant.SCURRENTTIME));
            return l;
        } else {
            long l= Long.parseLong(CommonUtils.getPreferencesString(getApplicationContext(),AppConstant.ICURRENTTIME));
            return l;
        }
    }
}
