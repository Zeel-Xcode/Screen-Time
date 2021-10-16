package services;

import static com.screentime.HomeActivity.NOTIFICATION_CHANNEL_ID;
import static com.screentime.HomeActivity.NOTIFICATION_CHANNEL_NAME;
import static com.screentime.HomeActivity.ONGOING_NOTIFICATION_ID;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.screentime.MyApplication;
import com.screentime.R;
import com.screentime.ServiceUtills;
import com.screentime.utils.AppConstant;
import com.screentime.utils.CommonUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
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
import java.util.concurrent.TimeUnit;

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
    String packagename;

    ArrayList<String> packageslist = new ArrayList<>();
    ArrayList<String> messagelist = new ArrayList<>();
    ArrayList<String> diallist = new ArrayList<>();

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
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        formatter = new DecimalFormat("00");

        packageslist.clear();
        messagelist.clear();
        diallist.clear();

        packageslist.add("com.facebook.katana");
        packageslist.add("com.instagram.android");
        packageslist.add("com.snapchat.android");
        packageslist.add("com.zhiliaoapp.musically");
        packageslist.add("com.google.android.youtube");
        packageslist.add("com.twitter.android");

        messagelist = getMessagingAppPackageNames(getApplicationContext());
        if (!messagelist.contains("com.google.android.apps.messaging")){
           messagelist.add("com.google.android.apps.messaging");
        }
        diallist = getPackagesOfDialerApps(getApplicationContext());
        packageslist.addAll(messagelist);

        packageslist.addAll(diallist);

        try {
            if (intent != null) {
                if (intent.getExtras() != null) {
                    databaseHandler2 = new DatabaseHandler2(this);
                    currentPackage = intent.getExtras().getString("package");

                    for (int i = 0; i < packageslist.size(); i++) {

                        if (packageslist.get(i).equals(currentPackage)) {
                            packagename = packageslist.get(i);
                            CommonUtils.savePreferencesString(getApplicationContext(), "packagename", packagename);
                        }
                    }

                    if (packagename.equals("com.facebook.katana")) {
                        appname = "facebook";
                    } else if (packagename.equals("com.instagram.android")) {
                        appname = "instagram";
                    } else if (packagename.equals("com.snapchat.android")) {
                        appname = "snapchat";
                    } else if (packagename.equals("com.zhiliaoapp.musically")) {
                        appname = "tiktok";
                    } else if (packagename.equals("com.google.android.youtube")) {
                        appname = "youtube";
                    } else if (packagename.equals("com.twitter.android")) {
                        appname = "twitter";
                    } else {
                        for (int i = 0; i < messagelist.size(); i++) {
                            if (packagename.equals(messagelist.get(i))) {
                                appname = "message";
                            }
                        }

                        for (int i = 0; i < diallist.size(); i++) {
                            if (packagename.equals(diallist.get(i))) {
                                appname = "phone";
                            }
                        }
                    }
                    CommonUtils.savePreferencesString(getApplicationContext(), "appname", appname);

                    startTimer();
                }
            }
            return START_NOT_STICKY;

        } catch (NullPointerException ex) {
            Log.e("Message", ex.getMessage());
            return START_NOT_STICKY;
        }


    }

    private ArrayList<String> getMessagingAppPackageNames(Context context) {
        ArrayList<String> messagingAppPackgeNameList = new ArrayList<>();
        final PackageManager pm = context.getPackageManager();
        //get metaData for installed apps
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo appInfo : packages) {
            String packageName = appInfo.packageName;
            if (packageName != null && (packageName.contains("sms")
                    || packageName.contains("mms") || packageName.contains("message")
                    || packageName.contains("SMS") || packageName.contains("MMS")
                    || packageName.contains("Message") || packageName.contains("media")
                    || packageName.contains("Media") || packageName.contains("messaging"))) {
                messagingAppPackgeNameList.add(packageName);
            }
        }
        return messagingAppPackgeNameList;
    }

    public ArrayList<String> getPackagesOfDialerApps(Context context) {

        ArrayList<String> packageNames = new ArrayList<>();

        // Declare action which target application listen to initiate phone call
        final Intent intent = new Intent();
        intent.setAction(Intent.ACTION_DIAL);
        // Query for all those applications
        List<ResolveInfo> resolveInfos = context.getPackageManager().queryIntentActivities(intent, 0);
        // Read package name of all those applications
        for (ResolveInfo resolveInfo : resolveInfos) {
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            packageNames.add(activityInfo.applicationInfo.packageName);
        }

        return packageNames;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
        stoptimertask();

    }

    public void startTimer() {
        if (timer == null) {
            //set a new Timer
            timer = new Timer();
            //initialize the TimerTask's job
            initializeTimerTask();
            long starttime = System.currentTimeMillis();

            startcal = Calendar.getInstance(Locale.getDefault());

            startcal.setTimeInMillis(starttime);

            String startdate = CommonUtils.getDateFormatInMillisecond(AppConstant.TIMEFORMATE, startcal.getTime());
            startdate1 = startcal.getTime();

            CommonUtils.savePreferencesString(getApplicationContext(), "starttime", startdate);
            CommonUtils.savePreferencesString(getApplicationContext(), "endtime", "");
            setdatanewdatabase(appname, packagename);
            //schedule the timer, to wake up every 10 second
            timer.schedule(timerTask, 0, 100);
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

            endcal = Calendar.getInstance(Locale.getDefault());
            endcal.setTimeInMillis(endtime);

            String enddate = CommonUtils.getDateFormatInMillisecond(AppConstant.TIMEFORMATE, endcal.getTime());
            enddate1 = endcal.getTime();
            CommonUtils.savePreferencesString(getApplicationContext(), "endtime", enddate);
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

    public void setdatanewdatabase(String title, String packagename) {
        NewModel newModel = new NewModel();

        String starttime1 = CommonUtils.getDateFormatInMillisecond(AppConstant.TIMEFORMATE, startcal.getTime());
        String currentdate = DateFormat.format("yyyy-MM-dd", startcal).toString();

        newModel.setDeviceid("");
        newModel.setPackagename(packagename);
        newModel.setAppname(title);
        newModel.setStarttime(starttime1);
        newModel.setEndtime("");
        newModel.setTotalsec(0);
        newModel.setCurrentdate(currentdate);
        databaseHandler2.insertRecord(newModel);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("HardwareIds")
    public void updatedatabase(String id) {
        NewModel newModel = new NewModel();

        String currentdate = DateFormat.format("yyyy-MM-dd", startcal).toString();

        String starttime1 = CommonUtils.getDateFormatInMillisecond(AppConstant.TIMEFORMATE, startcal.getTime());

        String endtime1 = CommonUtils.getDateFormatInMillisecond(AppConstant.TIMEFORMATE, endcal.getTime());


        long totalseconds = enddate1.getTime() - startdate1.getTime();

        int seconds = (int) (totalseconds / 1000) % 60;
        int minutes = (int) ((totalseconds / (1000 * 60)) % 60);
        int hours = (int) ((totalseconds / (1000 * 60 * 60)) % 24);

        newModel.setId(id);
        newModel.setDeviceid(Build.HARDWARE);
        newModel.setPackagename(packagename);
        newModel.setAppname(appname);
        newModel.setStarttime(starttime1);
        newModel.setEndtime(endtime1);
        newModel.setTotalsec(totalseconds);
        newModel.setCurrentdate(currentdate);
        databaseHandler2.updateRecord(newModel);

    }
}