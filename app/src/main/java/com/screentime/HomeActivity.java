package com.screentime;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AppOpsManager;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.appevents.AppEventsLogger;
import com.screentime.utils.AppConstant;
import com.screentime.utils.CommonUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import Model.SqLiteDatabaseModel;
import Model.UsageModel;
import SQLiteDatabase.DatabaseHandler;
import SQLiteDatabase.DatabaseHandler2;
import services.GetUsageService1;

public class HomeActivity extends AppCompatActivity {

    Toolbar toolbar;
    ImageView iv_back, iv_setting;
    TextView tvTitle, tvFacebook, tvInsta, tvSnapChat, tvfbTime, tvinstaTime, tvsnapchatTime, datepicker;
    LinearLayout llFacebook, llSnapchat, llInsta;

    String[] packages = {"com.facebook.katana", "com.instagram.android", "com.snapchat.android"};
    NumberFormat formatter;
    private AppEventsLogger logger;
    private DatabaseHandler db;
    private DatabaseHandler2 databaseHandler2;
    private String id;

    private int mYear, mMonth, mDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        datepicker = findViewById(R.id.datepicker);
        toolbar = findViewById(R.id.toolbar);
        iv_back = findViewById(R.id.iv_back);
        iv_setting = findViewById(R.id.iv_setting);
        tvTitle = findViewById(R.id.tvTitle);
        tvFacebook = findViewById(R.id.tvFacebook);
        tvInsta = findViewById(R.id.tvInsta);
        tvSnapChat = findViewById(R.id.tvSnapChat);
        tvfbTime = findViewById(R.id.tvfbTime);
        tvinstaTime = findViewById(R.id.tvinstaTime);
        tvsnapchatTime = findViewById(R.id.tvsnapchatTime);
        llFacebook = findViewById(R.id.llFacebook);
        llSnapchat = findViewById(R.id.llSnapchat);
        llInsta = findViewById(R.id.llInsta);

        formatter = new DecimalFormat("00");
        setToolbar();
        setAverageTime();


        db = new DatabaseHandler(this);
        databaseHandler2 = new DatabaseHandler2(this);
        logger = AppEventsLogger.newLogger(this);

        llFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                facebook();
            }
        });

        llInsta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                instagram();
            }
        });

        llSnapchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snapchat();
            }
        });

//        final Calendar c = Calendar.getInstance();
//        mYear = c.get(Calendar.YEAR);
//        mMonth = c.get(Calendar.MONTH);
//        mDay = c.get(Calendar.DAY_OF_MONTH);
//
//        datepicker.setText(new StringBuilder()
//                // Month is 0 based, just add 1
//                .append(mYear).append(" ").append("-").append(mMonth + 1).append("-")
//                .append(mDay));

//        datepicker.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                DatePickerDialog datePickerDialog = new DatePickerDialog(HomeActivity.this, R.style.DialogTheme,
//                        new DatePickerDialog.OnDateSetListener() {
//
//                            @Override
//                            public void onDateSet(DatePicker view, int year,
//                                                  int monthOfYear, int dayOfMonth) {
//
//                                datepicker.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
//
//                                String datepickerstamp = datepicker.getText().toString();
//
//                            }
//                        }, mYear, mMonth, mDay);
//                datePickerDialog.show();
//            }
//        });

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onResume() {
        super.onResume();
        iv_back.setVisibility(View.INVISIBLE);
        iv_back.setEnabled(false);

        if (checkPermission()) {
            setData();
            startService(new Intent(this, GetUsageService1.class));
        } else {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(intent);
        }
    }

    private void setToolbar() {
        setSupportActionBar(toolbar);
        tvTitle.setText("Home screen");
        iv_setting.setVisibility(View.INVISIBLE);

    }

    /**
     * Navigate to Statistics Screen with Facebook's details.
     */
    private void facebook() {
        long time = getTimes(packages[0]);
        int minutes = (int) ((time / (1000 * 60)) % 60);
        int hours = (int) ((time / (1000 * 60 * 60)) % 24);
        UsageModel model = new UsageModel();
        model.setHrs(hours);
        model.setMint(minutes);
        if (time > 0) {
            startActivity(new Intent(this, StatisticsActivity.class)
                    .putExtra("which", "Facebook")
                    .putExtra("image", R.drawable.screen_facebook)
                    .putExtra("data", model)
                    .putExtra("limit", "0:40")
                    .putExtra("average", CommonUtils.getPreferencesString(this, AppConstant.FB_ATIME)));
        } else {
            CommonUtils.snackBar(tvFacebook, getResources().getString(R.string.no_app));
        }
    }

    private void snapchat() {
        long time = getTimes(packages[2]);
        int minutes = (int) ((time / (1000 * 60)) % 60);
        int hours = (int) ((time / (1000 * 60 * 60)) % 24);
        UsageModel model = new UsageModel();
        model.setHrs(hours);
        model.setMint(minutes);
        if (time > 0) {
            startActivity(new Intent(this, StatisticsActivity.class)
                    .putExtra("which", "SnapChat")
                    .putExtra("image", R.drawable.screen_snapchat)
                    .putExtra("data", model)
                    .putExtra("limit", "0:25")
                    .putExtra("average", CommonUtils.getPreferencesString(this, AppConstant.SNAPCHAT_ATIME)));
        } else {
            CommonUtils.snackBar(tvFacebook, getResources().getString(R.string.no_app));
        }
    }

    private void instagram() {
        long time = getTimes(packages[1]);
        int minutes = (int) ((time / (1000 * 60)) % 60);
        int hours = (int) ((time / (1000 * 60 * 60)) % 24);
        UsageModel model = new UsageModel();
        model.setHrs(hours);
        model.setMint(minutes);

        if (time > 0) {
            startActivity(new Intent(this, ResultActivity.class)
                    .putExtra("which", "Instagram")
                    .putExtra("image", R.drawable.screen_instagram)
                    .putExtra("data", model)
                    .putExtra("limit", "0:15")
                    .putExtra("average", CommonUtils.getPreferencesString(this, AppConstant.INSTA_ATIME)));
        } else {
            CommonUtils.snackBar(tvFacebook, getResources().getString(R.string.no_app));
        }
    }

    /**
     * Checks Usage Stats permission.
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private boolean checkPermission() {
        try {
            PackageManager packageManager = getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);
            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    /**
     * To set data  on Screen.
     */
    private void setData(){
        setDatafb();
        setDatainsta();
        setDatasnap();

    }

    private void setDatafb() {

        long time = getTimes(packages[0]);
        int minutes = (int) ((time / (1000 * 60)) % 60);
        int hours = (int) ((time / (1000 * 60 * 60)) % 24);
        int seconds = (int) ((time / (1000) % 60));

        UsageModel model = new UsageModel();
        model.setHrs(hours);
        model.setMint(minutes);
        model.setSec(seconds);

        tvfbTime.setText(model.getHrs() + ":" + formatter.format(model.getMint()) + ":" + formatter.format(model.getSec()));

    }

    private void setDatainsta(){
        long time = getTimes(packages[1]);
        int minutes = (int) ((time / (1000 * 60)) % 60);
        int hours = (int) ((time / (1000 * 60 * 60)) % 24);
        int seconds = (int) ((time / (1000) % 60));

//        // formula for conversion for
//        // milliseconds to minutes.
//        long minutes1 = (time / 1000) / 60;
//
//        // formula for conversion for
//        // milliseconds to seconds
//        long seconds1 = (time / 1000) % 60;
//
//        Toast.makeText(this, " " + time + " Milliseconds = "
//                + minutes1 + " minutes and "
//                + seconds1 + " seconds.", Toast.LENGTH_LONG).show();

        UsageModel model = new UsageModel();
        model.setHrs(hours);
        model.setMint(minutes);
        model.setSec(seconds);

        tvinstaTime.setText(model.getHrs() + ":" + formatter.format(model.getMint()) + ":" + formatter.format(model.getSec()));

    }

    private void setDatasnap(){
        long time = getTimes(packages[2]);
        int minutes = (int) ((time / (1000 * 60)) % 60);
        int hours = (int) ((time / (1000 * 60 * 60)) % 24);
        int seconds = (int) ((time / (1000) % 60));

        UsageModel model = new UsageModel();
        model.setHrs(hours);
        model.setMint(minutes);
        model.setSec(seconds);

        tvsnapchatTime.setText(model.getHrs() + ":" + formatter.format(model.getMint()) + ":" + formatter.format(model.getSec()));
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
     * Gets current used time for each app.
     */
    private long getTimes(String packageName) {
        if (packageName.equals("com.facebook.katana")) {
            long l = Long.parseLong(CommonUtils.getPreferencesString(getApplicationContext(), AppConstant.FCURRENTTIME));
            CommonUtils.savePreferencesInteger(getApplicationContext(), AppConstant.FWEEKTTIME, CommonUtils.getPreferencesInteger(getApplicationContext(), AppConstant.FWEEKTTIME) + l);
            getSetDataInSqLite(l, "facebook");
            return l;

        } else if (packageName.equals("com.snapchat.android")) {
            long l = Long.parseLong(CommonUtils.getPreferencesString(getApplicationContext(), AppConstant.SCURRENTTIME));
            CommonUtils.savePreferencesInteger(getApplicationContext(), AppConstant.SWEEKTTIME, CommonUtils.getPreferencesInteger(getApplicationContext(), AppConstant.SWEEKTTIME) + l);

            getSetDataInSqLite(l, "snapchat");
            return l;
        } else {
            long l = Long.parseLong(CommonUtils.getPreferencesString(getApplicationContext(), AppConstant.ICURRENTTIME));
            CommonUtils.savePreferencesInteger(getApplicationContext(), AppConstant.IWEEKTIME, CommonUtils.getPreferencesInteger(getApplicationContext(), AppConstant.IWEEKTIME) + l);
            getSetDataInSqLite(l, "instagram");

            return l;
        }
    }

    public void getSetDataInSqLite(long l, String title) {
        SqLiteDatabaseModel sqLiteDatabaseModel = new SqLiteDatabaseModel();
        boolean isDateAvailabel = false;
//        db.deleteDatabase();
        ArrayList<SqLiteDatabaseModel> getAllData = db.getAllTime();
        if (getAllData.size() > 0) {
            for (int i = 0; i < getAllData.size(); i++) {
                String date = getAllData.get(i).getCurentDate();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                String dateString = formatter.format(new Date(Long.parseLong(date)));
                if (getAllData.get(i).getCurentDate().equals(timeStamp())) {
                    isDateAvailabel = true;
                    id = getAllData.get(i).getId();
                    break;
                }
            }
            if (isDateAvailabel) {
                if (title.equals("facebook")) {
                    sqLiteDatabaseModel.setId(id);
                    sqLiteDatabaseModel.setFacebookTime(CommonUtils.getPreferencesString(getApplicationContext(), AppConstant.FCURRENTTIME));
                    db.updateFacebookTime(new SqLiteDatabaseModel(id, CommonUtils.getPreferencesString(getApplicationContext(), AppConstant.FCURRENTTIME)));
                } else if (title.equals("snapchat")) {

                    sqLiteDatabaseModel.setId(id);
                    sqLiteDatabaseModel.setFacebookTime(CommonUtils.getPreferencesString(getApplicationContext(), AppConstant.FCURRENTTIME));
                    sqLiteDatabaseModel.setSnapChatTime(CommonUtils.getPreferencesString(getApplicationContext(), AppConstant.SCURRENTTIME));
                    db.updateSnapChatTime(new SqLiteDatabaseModel(id, CommonUtils.getPreferencesString(getApplicationContext(), AppConstant.FCURRENTTIME),
                            CommonUtils.getPreferencesString(getApplicationContext(), AppConstant.SCURRENTTIME)));
                } else {

                    sqLiteDatabaseModel.setId(id);
                    sqLiteDatabaseModel.setFacebookTime(CommonUtils.getPreferencesString(getApplicationContext(), AppConstant.FCURRENTTIME));
                    sqLiteDatabaseModel.setSnapChatTime(CommonUtils.getPreferencesString(getApplicationContext(), AppConstant.SCURRENTTIME));
                    sqLiteDatabaseModel.setInstagramTime(CommonUtils.getPreferencesString(getApplicationContext(), AppConstant.ICURRENTTIME));
                    db.updateInstagramTime(new SqLiteDatabaseModel(id, CommonUtils.getPreferencesString(getApplicationContext(), AppConstant.FCURRENTTIME),
                            CommonUtils.getPreferencesString(getApplicationContext(), AppConstant.SCURRENTTIME),
                            CommonUtils.getPreferencesString(getApplicationContext(), AppConstant.ICURRENTTIME)));
                }

            }
            else {
                if (title.equals("facebook")) {

                    sqLiteDatabaseModel.setId(id);
                    sqLiteDatabaseModel.setFacebookTime(CommonUtils.getPreferencesString(getApplicationContext(), AppConstant.FCURRENTTIME));
                    db.insertRecord(new SqLiteDatabaseModel(id, CommonUtils.getPreferencesString(getApplicationContext(), AppConstant.FCURRENTTIME)));
                } else if (title.equals("snapchat")) {

                    sqLiteDatabaseModel.setId(id);
                    sqLiteDatabaseModel.setFacebookTime(CommonUtils.getPreferencesString(getApplicationContext(), AppConstant.FCURRENTTIME));
                    sqLiteDatabaseModel.setSnapChatTime(CommonUtils.getPreferencesString(getApplicationContext(), AppConstant.SCURRENTTIME));
                    db.insertRecord(new SqLiteDatabaseModel(id, CommonUtils.getPreferencesString(getApplicationContext(), AppConstant.FCURRENTTIME),
                            CommonUtils.getPreferencesString(getApplicationContext(), AppConstant.SCURRENTTIME)));
                } else {
                    sqLiteDatabaseModel.setId(id);
                    sqLiteDatabaseModel.setFacebookTime(CommonUtils.getPreferencesString(getApplicationContext(), AppConstant.FCURRENTTIME));
                    sqLiteDatabaseModel.setSnapChatTime(CommonUtils.getPreferencesString(getApplicationContext(), AppConstant.SCURRENTTIME));
                    sqLiteDatabaseModel.setInstagramTime(CommonUtils.getPreferencesString(getApplicationContext(), AppConstant.ICURRENTTIME));
                    db.insertRecord(new SqLiteDatabaseModel(id, CommonUtils.getPreferencesString(getApplicationContext(), AppConstant.FCURRENTTIME),
                            CommonUtils.getPreferencesString(getApplicationContext(), AppConstant.SCURRENTTIME),
                            CommonUtils.getPreferencesString(getApplicationContext(), AppConstant.ICURRENTTIME)));
                }


            }
        }
        else {
            if (title.equals("facebook")) {
                sqLiteDatabaseModel.setId(id);
                sqLiteDatabaseModel.setFacebookTime(CommonUtils.getPreferencesString(getApplicationContext(), AppConstant.FCURRENTTIME));
                db.insertRecord(new SqLiteDatabaseModel(id, CommonUtils.getPreferencesString(getApplicationContext(), AppConstant.FCURRENTTIME)));

            } else if (title.equals("snapchat")) {

                sqLiteDatabaseModel.setId(id);
                sqLiteDatabaseModel.setFacebookTime(CommonUtils.getPreferencesString(getApplicationContext(), AppConstant.FCURRENTTIME));
                sqLiteDatabaseModel.setSnapChatTime(CommonUtils.getPreferencesString(getApplicationContext(), AppConstant.SCURRENTTIME));
                db.insertRecord(new SqLiteDatabaseModel(id, CommonUtils.getPreferencesString(getApplicationContext(), AppConstant.FCURRENTTIME),
                        CommonUtils.getPreferencesString(getApplicationContext(), AppConstant.SCURRENTTIME)));
            } else {
                sqLiteDatabaseModel.setId(id);
                sqLiteDatabaseModel.setFacebookTime(CommonUtils.getPreferencesString(getApplicationContext(), AppConstant.FCURRENTTIME));
                sqLiteDatabaseModel.setSnapChatTime(CommonUtils.getPreferencesString(getApplicationContext(), AppConstant.SCURRENTTIME));
                sqLiteDatabaseModel.setInstagramTime(CommonUtils.getPreferencesString(getApplicationContext(), AppConstant.ICURRENTTIME));
                db.insertRecord(new SqLiteDatabaseModel(id, CommonUtils.getPreferencesString(getApplicationContext(), AppConstant.FCURRENTTIME),
                        CommonUtils.getPreferencesString(getApplicationContext(), AppConstant.SCURRENTTIME),
                        CommonUtils.getPreferencesString(getApplicationContext(), AppConstant.ICURRENTTIME)));
            }
        }
    }

    private String getCurrentDate() {
        String pattern = "dd-mm-yyyy";
        String dateInString = new SimpleDateFormat(pattern).format(new Date());
        return dateInString;
    }


    public String timeStamp() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sdf.parse(getCurrentDate());
            return String.valueOf(date.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    private void setAverageTime() {
        CommonUtils.savePreferencesString(this, AppConstant.FB_ATIME, "0:35");
        CommonUtils.savePreferencesString(this, AppConstant.SNAPCHAT_ATIME, "0:20");
        CommonUtils.savePreferencesString(this, AppConstant.INSTA_ATIME, "0:10");

        if (CommonUtils.getPreferencesString(this, AppConstant.CURRENTDATE).equals("")) {
            String timeStamp = new SimpleDateFormat("yyyy.MM.dd").format(new Date());
            CommonUtils.savePreferencesString(this, AppConstant.CURRENTDATE, timeStamp);
        }

        if (CommonUtils.getPreferencesString(this, AppConstant.FCURRENTTIME).equals("")) {
            CommonUtils.savePreferencesString(this, AppConstant.FCURRENTTIME, "0");
        }
        if (CommonUtils.getPreferencesString(this, AppConstant.SCURRENTTIME).equals("")) {
            CommonUtils.savePreferencesString(this, AppConstant.SCURRENTTIME, "0");
        }
        if (CommonUtils.getPreferencesString(this, AppConstant.ICURRENTTIME).equals("")) {
            CommonUtils.savePreferencesString(this, AppConstant.ICURRENTTIME, "0");
        }
    }
}