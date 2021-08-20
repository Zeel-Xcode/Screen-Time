package services;

import static com.screentime.HomeActivity.NOTIFICATION_CHANNEL_ID;
import static com.screentime.HomeActivity.NOTIFICATION_CHANNEL_NAME;
import static com.screentime.HomeActivity.ONGOING_NOTIFICATION_ID;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.screentime.HomeActivity;
import com.screentime.R;
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
        final Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        createNotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME,
                NotificationManagerCompat.IMPORTANCE_HIGH, this);
        final NotificationCompat.Builder notification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setAutoCancel(false)
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText("Background Service")
                .setSound(soundUri)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setLights(Color.RED, 3000, 3000)
                .setPriority(Notification.PRIORITY_LOW);

        Notification notification1 = notification.build();
        notification1.flags = Notification.FLAG_ONGOING_EVENT;
        startForeground(ONGOING_NOTIFICATION_ID, notification1);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        startTimer();

        return START_REDELIVER_INTENT;
    }

    private void createNotificationChannel(@NonNull String aChannelId, @NonNull String aChannelName,
                                           int aImportance, Context aContext) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(aChannelId, aChannelName, aImportance);

            final NotificationManager manager = aContext.getSystemService(NotificationManager.class);

            assert manager != null;
            manager.createNotificationChannel(channel);
        }
    }


    @Override
    public void onDestroy() {
        stoptimertask();

    }

    /**
     * Calculates the usage of app if it is on foreground and generate popup.
     */
    private void getUsage(){
             startService(new Intent(getApplicationContext(),OnforegroundService.class).putExtra("package", currentPackage));

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

        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%% " + currentApp);
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


}
