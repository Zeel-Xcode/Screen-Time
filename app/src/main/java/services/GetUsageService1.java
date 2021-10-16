package services;

import static com.screentime.HomeActivity.NOTIFICATION_CHANNEL_ID;
import static com.screentime.HomeActivity.NOTIFICATION_CHANNEL_NAME;
import static com.screentime.HomeActivity.ONGOING_NOTIFICATION_ID;

import android.app.ActivityManager;
import android.app.IntentService;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.screentime.BackupAndRestore1;
import com.screentime.HomeActivity;
import com.screentime.R;
import com.screentime.ServiceUtills;
import com.screentime.utils.AppConstant;
import com.screentime.utils.CommonUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import Model.UsagesModel;
import SQLiteDatabase.DatabaseHandler2;

/**
 * This Service calculates time spend by user on each app.
 * It runs on background in every 10 seconds
 */

public class GetUsageService1 extends Service {

    private Timer timer;
    private TimerTask timerTask;
    String currentPackage;

    BackupAndRestore1 backupAndRestore1;
    DatabaseHandler2 databaseHandler2;

    Calendar startcal;
    Calendar endcal;

    Date startdate1;
    Date enddate1;
    String id;

    Boolean aBoolean = false;
    BroadcastReceiver mReceiver = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        backupAndRestore1 = new BackupAndRestore1();
        databaseHandler2 = new DatabaseHandler2(this);

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
//        broadcast();

        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        mReceiver = new MyReceiver();
        registerReceiver(mReceiver, filter);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        boolean screenOn = false;
        boolean from_receiver = false;

        try {
            screenOn = intent.getBooleanExtra("screen_state", false);
            from_receiver = intent.getBooleanExtra("from_receiver", false);

        } catch (Exception e) {

        }

        if (from_receiver) {
            if (!screenOn) {
                Log.d("Screen", "`onReceive`: on");
                Toast.makeText(getApplicationContext(), "time is tracking", Toast.LENGTH_SHORT).show();
                aBoolean = true;
                long starttime = System.currentTimeMillis();
                startcal = Calendar.getInstance(Locale.getDefault());
                startcal.setTimeInMillis(starttime);
                startdate1 = startcal.getTime();
                setdatanewdatabase();

            } else {

                if (aBoolean) {
                    Log.d("Screen", "onReceive: off");
                    long endtime = System.currentTimeMillis();
                    endcal = Calendar.getInstance(Locale.getDefault());
                    endcal.setTimeInMillis(endtime);
                    enddate1 = endcal.getTime();

                    if (enddate1.getTime() > startdate1.getTime()) {
                        ArrayList<UsagesModel> getdata = databaseHandler2.getAllTimeUsages();

                        if (getdata.size() > 0) {
                            id = getdata.get(getdata.size() - 1).getId();
                            updatedatabase(id);
                        }
                    }
                }
            }
        }


    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        startTimer();

        return START_STICKY;
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
    private void getUsage() {

        Intent intent = new Intent(getBaseContext(), OnforegroundService.class);
        intent.putExtra("package", currentPackage);
        startService(intent);


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

                    if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.P) {
                        int eventtype = 0;
                        try {
                            eventtype = (int) UsageStats.class.getDeclaredField("mLastEvent").get(usageStats);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (NoSuchFieldException e) {
                            e.printStackTrace();
                        }
                        if (eventtype == UsageEvents.Event.ACTIVITY_RESUMED) {
                            mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                        }

                    } else {
                        mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                    }
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

            //schedule the timer, to wake up every 1 second
            timer.schedule(timerTask, 0, 1000);
        }
    }

    /**
     * Initialize the timer.
     */
    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
        String preDate = CommonUtils.getPreferencesString(getApplicationContext(), AppConstant.CURRENT_DATE);
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String date = df.format(new Date());
        if (preDate.equals("") || timeOfDay >= 12 && timeOfDay < 16) {
//            backupAndRestore1.exportDB(this, databaseHandler2);
            CommonUtils.savePreferencesString(getApplicationContext(), AppConstant.CURRENT_DATE, date);
        } else if (preDate.equals(date)) {

        } else {

//            backupAndRestore1.exportDB(this, databaseHandler2);
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

    private void broadcast() {

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
//        intentFilter.addAction(Intent.ACTION_USER_PRESENT);

        KeyguardManager myKM = (KeyguardManager) getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);

        BroadcastReceiver screenreceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                Log.d("Screen", "onReceive: brodcast");


                if (myKM.inKeyguardRestrictedInputMode()) {
//                    it is locked
                    if (aBoolean == true) {
                        Log.d("Screen", "onReceive: off");
                        long endtime = System.currentTimeMillis();
                        endcal = Calendar.getInstance(Locale.getDefault());
                        endcal.setTimeInMillis(endtime);
                        enddate1 = endcal.getTime();
                        Toast.makeText(context, "time tracking is closed", Toast.LENGTH_SHORT).show();

                        if (enddate1.getTime() > startdate1.getTime()) {
                            ArrayList<UsagesModel> getdata = databaseHandler2.getAllTimeUsages();

                            if (getdata.size() > 0) {
                                id = getdata.get(getdata.size() - 1).getId();
                                updatedatabase(id);
                            }
                        }
                    }

                } else {

                    //it is not locked
                    Toast.makeText(context, "time is tracking", Toast.LENGTH_SHORT).show();
                    aBoolean = true;
                    Log.d("Screen", "onReceive: on");
                    long starttime = System.currentTimeMillis();
                    startcal = Calendar.getInstance(Locale.getDefault());
                    startcal.setTimeInMillis(starttime);
                    startdate1 = startcal.getTime();
                    setdatanewdatabase();
                }

            }
        };

        getApplicationContext().registerReceiver(screenreceiver, intentFilter);
    }

    public void setdatanewdatabase() {
        UsagesModel usagesModel = new UsagesModel();

        String starttime1 = CommonUtils.getDateFormatInMillisecond(AppConstant.TIMEFORMATE, startcal.getTime());
        String currentdate = android.text.format.DateFormat.format("yyyy-MM-dd", startcal).toString();

        usagesModel.setDeviceid(Build.HARDWARE);
        usagesModel.setStarttime(starttime1);
        usagesModel.setEndtime("");
        usagesModel.setTotalsec(0);
        usagesModel.setCurrentdate(currentdate);
        databaseHandler2.insertUsages(usagesModel);
    }

    public void updatedatabase(String id) {
        UsagesModel usagesModel = new UsagesModel();

        String currentdate = android.text.format.DateFormat.format("yyyy-MM-dd", startcal).toString();

        String starttime1 = CommonUtils.getDateFormatInMillisecond(AppConstant.TIMEFORMATE, startcal.getTime());

        String endtime1 = CommonUtils.getDateFormatInMillisecond(AppConstant.TIMEFORMATE, endcal.getTime());


        long totalseconds = enddate1.getTime() - startdate1.getTime();

        int seconds = (int) (totalseconds / 1000) % 60;
        int minutes = (int) ((totalseconds / (1000 * 60)) % 60);
        int hours = (int) ((totalseconds / (1000 * 60 * 60)) % 24);

        usagesModel.setId(id);
        usagesModel.setDeviceid(Build.HARDWARE);
        usagesModel.setStarttime(starttime1);
        usagesModel.setEndtime(endtime1);
        usagesModel.setTotalsec(totalseconds);
        usagesModel.setCurrentdate(currentdate);
        databaseHandler2.updateUsages(usagesModel);
        aBoolean = false;
    }

}
