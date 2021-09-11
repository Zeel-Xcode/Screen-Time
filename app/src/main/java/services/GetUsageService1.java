package services;

import static androidx.core.app.AppOpsManagerCompat.noteOpNoThrow;
import static com.screentime.HomeActivity.NOTIFICATION_CHANNEL_ID;
import static com.screentime.HomeActivity.NOTIFICATION_CHANNEL_NAME;
import static com.screentime.HomeActivity.ONGOING_NOTIFICATION_ID;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.screentime.BackupAndRestore1;
import com.screentime.HomeActivity;
import com.screentime.R;
import com.screentime.utils.AppConstant;
import com.screentime.utils.CommonUtils;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import Model.NewModel;
import SQLiteDatabase.DatabaseHandler2;

/**
 * This Service calculates time spend by user on each app.
 * It runs on background in every 10 seconds
 */

public class  GetUsageService1 extends Service {

    private final static String TAG = "BroadcastService";

    public static final String COUNTDOWN_BR = "com.screentime.countdown_br";
    Intent bi = new Intent(COUNTDOWN_BR);

    CountDownTimer countDownTimer = null;

    private Timer timer;
    private TimerTask timerTask;
    String currentPackage;

    BackupAndRestore1 backupAndRestore1;
    DatabaseHandler2 databaseHandler2;

    String id;
    NumberFormat formatter;
    Calendar startcal;
    Calendar endcal;

    Date startdate1;
    Date enddate1;

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

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        backupAndRestore1 = new BackupAndRestore1();
        databaseHandler2 = new DatabaseHandler2(this);

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

//        countDownTimer.cancel();
//        Log.i(TAG, "Timer cancelled");

    }

    /**
     * Calculates the usage of app if it is on foreground and generate popup.
     */
    private void getUsage() {
        startService(new Intent(getApplicationContext(), OnforegroundService.class).putExtra("package", currentPackage));

    }

    /**
     * Returns the package name of app on foreground.
     * @param applicationContext
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public String getRecentApps(Context applicationContext) {
        String currentApp = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager usm = (UsageStatsManager) getSystemService(USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,
                    time - 1000 * 1000, time);

            if (appList != null && appList.size() > 0) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();

                for (UsageStats usageStats : appList) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }

                if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                }
            }
        } else {
            ActivityManager am = (ActivityManager) getBaseContext().getSystemService(ACTIVITY_SERVICE);
            currentApp = am.getRunningTasks(1).get(0).topActivity.getPackageName();

        }

        System.out.println("##################### " + currentApp);
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

            //schedule the timer, to wake up every 1 second
            timer.schedule(timerTask, 0, 100);
        }
    }

    /**
     * Initialize the timer.
     */
    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            public void run() {
                if (Looper.myLooper() == null) {
                    Looper.prepare();
                }

//                registerBroadcastReceiver();
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
            backupAndRestore1.exportDB(this, databaseHandler2);
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

//    private void registerBroadcastReceiver() {
//
//        final IntentFilter theFilter = new IntentFilter();
///** System Defined Broadcast */
//        theFilter.addAction(Intent.ACTION_SCREEN_ON);
//        theFilter.addAction(Intent.ACTION_SCREEN_OFF);
//        theFilter.addAction(Intent.ACTION_USER_PRESENT);
//
//        BroadcastReceiver screenOnOffReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//
//                String strAction = intent.getAction();
//
//                KeyguardManager myKM = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
//                if (strAction.equals(Intent.ACTION_USER_PRESENT) || strAction.equals(Intent.ACTION_SCREEN_OFF) || strAction.equals(Intent.ACTION_SCREEN_ON))
//                    if (myKM.inKeyguardRestrictedInputMode()) {
//                        System.out.println("Screen off " + "LOCKED");
//                        Toast.makeText(getApplicationContext(), "Screen off " + "LOCKED", Toast.LENGTH_SHORT).show();
//                    } else {
//
//                        Toast.makeText(getApplicationContext(), "Screen off " + "UNLOCKED", Toast.LENGTH_SHORT).show();
//
//                        Log.i(TAG, "Starting timer...");
//                        Toast.makeText(getApplicationContext(), "Starting timer....", Toast.LENGTH_SHORT).show();
//
//                        countDownTimer = new CountDownTimer(10000, 1000) {
//                            @Override
//                            public void onTick(long millisUntilFinished) {
//
//                                Log.i(TAG, "Countdown seconds remaining: " + millisUntilFinished / 1000);
//                                Toast.makeText(getApplicationContext(), "Countdown seconds remaining: " + millisUntilFinished / 1000, Toast.LENGTH_SHORT).show();
//                                bi.putExtra("countdown", millisUntilFinished);
//                                sendBroadcast(bi);
//                            }
//
//                            @Override
//                            public void onFinish() {
//                                Log.i(TAG, "Timer finished");
//                                Toast.makeText(getApplicationContext(), "Timer finished", Toast.LENGTH_SHORT).show();
//
//                            }
//                        };
//
//                        countDownTimer.start();
//
//                    }
//
//            }
//        };
//
//        getApplicationContext().registerReceiver(screenOnOffReceiver, theFilter);
//    }
//
//    public void startTimer() {
//        if (timer == null) {
//            //set a new Timer
//            timer = new Timer();
//            //initialize the TimerTask's job
//            long starttime = System.currentTimeMillis();
//
//            startcal = Calendar.getInstance(Locale.getDefault());
//
//            startcal.setTimeInMillis(starttime);
//
//            String startdate = android.text.format.DateFormat.format("yyyy-MM-dd HH:mm:ss", startcal).toString();
//
//            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//            try {
//                startdate1 = dateFormat.parse(startdate);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//
//            CommonUtils.savePreferencesString(getApplicationContext(), "starttime", startdate);
//            CommonUtils.savePreferencesString(getApplicationContext(), "endtime", "");
//            setdatanewdatabase(appname, packagename);
//            //schedule the timer, to wake up every 10 second
//            timer.schedule(timerTask, 0, 100);
//        }
//    }
//
//    public void stoptimer() {
//        //stop the timer, if it's not already null
//        if (timer != null) {
//            timer.cancel();
//            timer = null;
//            long endtime = System.currentTimeMillis();
//
//            endcal = Calendar.getInstance(Locale.getDefault());
//            endcal.setTimeInMillis(endtime);
//
//            String enddate = android.text.format.DateFormat.format("yyyy-MM-dd HH:mm:ss", endcal).toString();
//
//            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//            try {
//                enddate1 = dateFormat.parse(enddate);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//
//            CommonUtils.savePreferencesString(getApplicationContext(), "endtime", enddate);
//            ArrayList<NewModel> getdata = databaseHandler2.getAllTime();
//
//            if (getdata.size() > 0) {
//                id = getdata.get(getdata.size() - 1).getId();
//                updatedatabase(id);
//            }
//        }
//    }
//
//    public void setdatanewdatabase(String title, String packagename) {
//        NewModel newModel = new NewModel();
//
//        String starttime1 = android.text.format.DateFormat.format("yyyy-MM-dd hh:mm:ss aa", startcal).toString();
//
//        String currentdate = android.text.format.DateFormat.format("yyyy-MM-dd", startcal).toString();
//
//        newModel.setDeviceid(Settings.Secure.getString(getContentResolver(),
//                Settings.Secure.ANDROID_ID));
//        newModel.setPackagename(packagename);
//        newModel.setAppname(title);
//        newModel.setStarttime(starttime1);
//        newModel.setEndtime("");
//        newModel.setTotalsec(0);
//        newModel.setCurrentdate(currentdate);
//        databaseHandler2.insertRecord(newModel);
//    }
//
//    public void updatedatabase(String id) {
//        NewModel newModel = new NewModel();
//
//        String currentdate = android.text.format.DateFormat.format("yyyy-MM-dd", startcal).toString();
//
//        String starttime1 = android.text.format.DateFormat.format("yyyy-MM-dd hh:mm:ss aa", startcal).toString();
//
//        String endtime1 = android.text.format.DateFormat.format("yyyy-MM-dd hh:mm:ss aa", endcal).toString();
//
//
//        long totalseconds = enddate1.getTime() - startdate1.getTime();
//
//        int seconds = (int) (totalseconds / 1000) % 60 ;
//        int minutes = (int) ((totalseconds / (1000*60)) % 60);
//        int hours   = (int) ((totalseconds / (1000*60*60)) % 24);
//
//        newModel.setId(id);
//        newModel.setDeviceid(Settings.Secure.getString(getContentResolver(),
//                Settings.Secure.ANDROID_ID));
//        newModel.setPackagename(packagename);
//        newModel.setAppname(appname);
//        newModel.setStarttime(starttime1);
//        newModel.setEndtime(endtime1);
//        newModel.setTotalsec(totalseconds);
//        newModel.setCurrentdate(currentdate);
//        databaseHandler2.updateRecord(newModel);
//    }

}
