package com.screentime;

import android.annotation.SuppressLint;
import android.app.AppOpsManager;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationManagerCompat;

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
import java.util.List;

import Model.NewModel;
import SQLiteDatabase.DatabaseHandler2;
import services.GetUsageService1;

public class HomeActivity extends AppCompatActivity {

    Toolbar toolbar;
    ImageView iv_back, ic_export;
    TextView tvTitle, tvFacebook, tvInsta, tvSnapChat, tvfbTime, tvinstaTime, tvsnapchatTime, datepicker, tvMessages, tvmessageTime, tvTiktok, tvtiktokTime, tvPhone, tvphoneTime, tvtwitterTime, tvyoutubeTime;
    LinearLayout llFacebook, llSnapchat, llInsta, llMessages, llTktok, llPhone,llyoutube,lltwitter;

    NumberFormat formatter;
    private AppEventsLogger logger;
    private DatabaseHandler2 databaseHandler2;
    private String id;
    String datepickerstamp;

    public static final String NOTIFICATION_CHANNEL_NAME = "App usages Tracking";
    public static final String NOTIFICATION_CHANNEL_ID = "Screentimer_background_service_channel";
    public static final int ONGOING_NOTIFICATION_ID = 100;

    private int mYear, mMonth, mDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        tvphoneTime = findViewById(R.id.tvphoneTime);
        tvPhone = findViewById(R.id.tvPhone);
        tvtiktokTime = findViewById(R.id.tvtiktokTime);
        tvTiktok = findViewById(R.id.tvTiktok);
        tvmessageTime = findViewById(R.id.tvmessageTime);
        tvMessages = findViewById(R.id.tvMessages);
        datepicker = findViewById(R.id.datepicker);
        toolbar = findViewById(R.id.toolbar);
        iv_back = findViewById(R.id.iv_back);
        ic_export = findViewById(R.id.ic_export);
        tvTitle = findViewById(R.id.tvTitle);
        tvFacebook = findViewById(R.id.tvFacebook);
        tvInsta = findViewById(R.id.tvInsta);
        tvSnapChat = findViewById(R.id.tvSnapChat);
        tvfbTime = findViewById(R.id.tvfbTime);
        tvinstaTime = findViewById(R.id.tvinstaTime);
        tvsnapchatTime = findViewById(R.id.tvsnapchatTime);
        tvtwitterTime = findViewById(R.id.tvtwitterTime);
        tvyoutubeTime = findViewById(R.id.tvyoutubeTime);
        llFacebook = findViewById(R.id.llFacebook);
        llSnapchat = findViewById(R.id.llSnapchat);
        llInsta = findViewById(R.id.llInsta);
        llMessages = findViewById(R.id.llMessages);
        llTktok = findViewById(R.id.llTktok);
        llPhone = findViewById(R.id.llPhone);
        llyoutube = findViewById(R.id.llyoutube);
        lltwitter = findViewById(R.id.lltwitter);

        formatter = new DecimalFormat("00");
        setToolbar();

        databaseHandler2 = new DatabaseHandler2(this);
        logger = AppEventsLogger.newLogger(this);

        llFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                facebook();
            }
        });

        llyoutube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                youtube();
            }
        });

        lltwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                twitter();
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

        llMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                messages();
            }
        });

        llTktok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tiktok();
            }
        });

        llPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phone();
            }
        });

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        datepickerstamp =  String.format("%d-%02d-%02d", mYear, (mMonth + 1), mDay);
        datepicker.setText(datepickerstamp);

        datepicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(HomeActivity.this, R.style.DialogTheme,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                mYear = year;
                                mMonth = monthOfYear;
                                mDay = dayOfMonth;

                                datepickerstamp =  String.format("%d-%02d-%02d", year, (monthOfYear + 1), dayOfMonth);
                                datepicker.setText(datepickerstamp);

                                setData(datepickerstamp);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onResume() {
        super.onResume();
        iv_back.setVisibility(View.INVISIBLE);
        iv_back.setEnabled(false);

        if (checkPermission()) {
            setData(getCurrentDate());
            startService(new Intent(this, GetUsageService1.class));
        } else {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(intent);
        }
    }

    private void setToolbar() {
        setSupportActionBar(toolbar);
        tvTitle.setText("Home screen");
        ic_export.setVisibility(View.INVISIBLE);

    }

    /**
     * Navigate to Statistics Screen with Facebook's details.
     */
    private void facebook() {

            startActivity(new Intent(this, ResultActivity.class)
                    .putExtra("which", "facebook"));

    }

    private void twitter() {

            startActivity(new Intent(this, ResultActivity.class)
                    .putExtra("which", "twitter"));

    }

    private void youtube() {

        startActivity(new Intent(this, ResultActivity.class)
                .putExtra("which", "youtube"));

    }

    private void snapchat() {

        startActivity(new Intent(this, ResultActivity.class)
                .putExtra("which", "snapchat"));

    }

    private void instagram() {

            startActivity(new Intent(this, ResultActivity.class)
                    .putExtra("which", "instagram"));

    }

    private void messages(){
        startActivity(new Intent(this, ResultActivity.class)
             .putExtra("which","message"));
    }

    private void tiktok(){
        startActivity(new Intent(this, ResultActivity.class)
                .putExtra("which","tiktok"));
    }

    private void phone(){
        startActivity(new Intent(this, ResultActivity.class)
                .putExtra("which","phone"));
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
     * @param currentDate
     */
    @SuppressLint("SetTextI18n")
    private void setData(String currentDate) {

        long totalfb = 0;
        long totalinsta = 0;
        long totalsnap = 0;
        long totalmessage = 0;
        long totaltiktok = 0;
        long totalphone = 0;
        long totaltwitter = 0;
        long totalyoutube = 0;

        ArrayList<NewModel> getdata = databaseHandler2.getAllTime();

        if (getdata.size() > 0) {
            for (int i = 0; i < getdata.size(); i++) {
                if (getdata.get(i).getCurrentdate().equals(currentDate)) {
                    if (getdata.get(i).getAppname().equals("facebook")) {
                        totalfb = totalfb + (int) getdata.get(i).getTotalsec();
                    } else if (getdata.get(i).getAppname().equals("snapchat")) {
                        totalsnap = totalsnap + (int) getdata.get(i).getTotalsec();
                    } else if (getdata.get(i).getAppname().equals("instagram")) {
                        totalinsta = totalinsta + (int) getdata.get(i).getTotalsec();
                    } else if (getdata.get(i).getAppname().equals("message")) {
                        totalmessage = totalmessage + (int) getdata.get(i).getTotalsec();
                    } else if (getdata.get(i).getAppname().equals("tiktok")) {
                        totaltiktok = totaltiktok + (int) getdata.get(i).getTotalsec();
                    } else if (getdata.get(i).getAppname().equals("phone")) {
                        totalphone = totalphone + (int) getdata.get(i).getTotalsec();
                    } else if (getdata.get(i).getAppname().equals("youtube")) {
                        totalyoutube = totalyoutube + (int) getdata.get(i).getTotalsec();
                    } else if (getdata.get(i).getAppname().equals("twitter")) {
                        totaltwitter = totaltwitter + (int) getdata.get(i).getTotalsec();
                    }
                }
            }
        }

        tvfbTime.setText(totalfb / 3600 + ":" + formatter.format((totalfb % 3600) / 60) + ":" + formatter.format(totalfb % 60));
        tvinstaTime.setText(totalinsta / 3600 + ":" + formatter.format((totalinsta % 3600) / 60) + ":" + formatter.format(totalinsta % 60));
        tvsnapchatTime.setText(totalsnap / 3600 + ":" + formatter.format((totalsnap % 3600) / 60) + ":" + formatter.format(totalsnap % 60));
        tvmessageTime.setText(totalmessage / 3600 + ":" + formatter.format(totalmessage % 3600 / 60) + ":" + formatter.format(totalmessage % 60));
        tvtiktokTime.setText(totaltiktok / 3600 + ":" + formatter.format(totaltiktok % 3600 / 60) + ":" + formatter.format(totaltiktok % 60));
        tvphoneTime.setText(totalphone / 3600 + ":" + formatter.format(totalphone % 3600 / 60) + ":" + formatter.format(totalphone % 60));
        tvtwitterTime.setText(totaltwitter / 3600 + ":" + formatter.format(totaltwitter % 3600 / 60) + ":" + formatter.format(totaltwitter % 60));
        tvyoutubeTime.setText(totalyoutube / 3600 + ":" + formatter.format(totalyoutube % 3600 / 60) + ":" + formatter.format(totalyoutube % 60));

    }

    private String getCurrentDate() {
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        String dateInString = sdf2.format(System.currentTimeMillis());
        return dateInString;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NotificationManagerCompat.from(HomeActivity.this).cancel(ONGOING_NOTIFICATION_ID);
    }
}
