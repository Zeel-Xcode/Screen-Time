package com.screentime;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.facebook.appevents.AppEventsLogger;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import Model.NewModel;
import SQLiteDatabase.DatabaseHandler2;
import services.GetUsageService1;

public class HomeActivity extends AppCompatActivity {

    FloatingActionButton fab;
    String date;
    Toolbar toolbar;
    ImageView iv_back, ic_share;
    TextView tvTitle, tvFacebook, tvInsta, tvSnapChat, tvfbTime, tvinstaTime, tvsnapchatTime, datepicker, tvMessages, tvmessageTime, tvTiktok, tvtiktokTime, tvPhone, tvphoneTime, tvtwitterTime, tvyoutubeTime;
    LinearLayout llFacebook, llSnapchat, llInsta, llMessages, llTktok, llPhone, llyoutube, lltwitter;

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

        fab = findViewById(R.id.fab);
        tvphoneTime = findViewById(R.id.tvphoneTime);
        tvPhone = findViewById(R.id.tvPhone);
        tvtiktokTime = findViewById(R.id.tvtiktokTime);
        tvTiktok = findViewById(R.id.tvTiktok);
        tvmessageTime = findViewById(R.id.tvmessageTime);
        tvMessages = findViewById(R.id.tvMessages);
        datepicker = findViewById(R.id.datepicker);
        toolbar = findViewById(R.id.toolbar);
        iv_back = findViewById(R.id.iv_back);
        ic_share = findViewById(R.id.ic_share);
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

        checkStoragePermission();

        ic_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseHandler2.exportallappdata(datepicker.getText().toString(),true,HomeActivity.this,formatter);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(HomeActivity.this);
                dialog.setContentView(R.layout.custom_export_dialog);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                TextView export = dialog.findViewById(R.id.export);
                export.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        databaseHandler2.exportallappdata(datepicker.getText().toString(),false,HomeActivity.this, formatter);

                    }
                });

                TextView cancel = dialog.findViewById(R.id.cancel);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

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

        datepickerstamp = String.format("%d-%02d-%02d", mYear, (mMonth + 1), mDay);
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

                                date = String.format("%d-%02d-%02d", year, (monthOfYear + 1), dayOfMonth);
                                datepicker.setText(date);

                                setData(date);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

    }

    private void checkStoragePermission() {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(HomeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(HomeActivity.this);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("Write Storage permission is necessary to export App data!");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123);
                }

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==123){
            if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "All permission Granted", Toast.LENGTH_SHORT).show();
                    } else {
                        //code for deny
                        checkAgain();
                    }
                }
        }
    }

    private void checkAgain() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(HomeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(HomeActivity.this);
            alertBuilder.setCancelable(true);
            alertBuilder.setTitle("Permission necessary");
            alertBuilder.setMessage("Write Storage permission is necessary to export App Data");
            alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123);
                }
            });
            AlertDialog alert = alertBuilder.create();
            alert.show();
        } else {
            ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onResume() {
        super.onResume();
        iv_back.setVisibility(View.INVISIBLE);
        iv_back.setEnabled(false);

        if (checkPermission()) {
            setData(datepicker.getText().toString());
            startService(new Intent(this, GetUsageService1.class));
        } else {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(intent);
        }
    }

    private void setToolbar() {
        setSupportActionBar(toolbar);
        tvTitle.setText("Home screen");
//        ic_export.setVisibility(View.INVISIBLE);

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

    private void messages() {
        startActivity(new Intent(this, ResultActivity.class)
                .putExtra("which", "message"));
    }

    private void tiktok() {
        startActivity(new Intent(this, ResultActivity.class)
                .putExtra("which", "tiktok"));
    }

    private void phone() {
        startActivity(new Intent(this, ResultActivity.class)
                .putExtra("which", "phone"));
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
     *
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

        if (databaseHandler2.getAllTime().size() > 0){
            fab.setVisibility(View.VISIBLE);
        }else {
            fab.setVisibility(View.GONE);
        }

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

        tvfbTime.setText(formatter.format(((totalfb / (1000*60*60)) % 24))  + ":" + formatter.format(((totalfb / (1000*60)) % 60)) + ":" + formatter.format((totalfb / 1000) % 60));
        tvinstaTime.setText(formatter.format(((totalinsta / (1000*60*60)) % 24)) + ":" + formatter.format(((totalinsta / (1000*60)) % 60)) + ":" +  formatter.format((totalinsta / 1000) % 60));
        tvmessageTime.setText(formatter.format(((totalmessage / (1000*60*60)) % 24))  + ":" + formatter.format(((totalmessage / (1000*60)) % 60)) + ":" + formatter.format((totalmessage / 1000) % 60));
        tvtiktokTime.setText(formatter.format(((totaltiktok / (1000*60*60)) % 24)) + ":" + formatter.format(((totaltiktok / (1000*60)) % 60)) + ":" +  formatter.format((totaltiktok / 1000) % 60));
        tvphoneTime.setText(formatter.format(((totalphone / (1000*60*60)) % 24)) + ":" + formatter.format(((totalphone / (1000*60)) % 60)) + ":" +  formatter.format((totalphone / 1000) % 60));
        tvsnapchatTime.setText(formatter.format(((totalsnap / (1000*60*60)) % 24)) + ":" + formatter.format(((totalsnap / (1000*60)) % 60)) + ":" +  formatter.format((totalsnap / 1000) % 60));
        tvtwitterTime.setText(formatter.format(((totaltwitter / (1000*60*60)) % 24)) + ":" + formatter.format(((totaltwitter / (1000*60)) % 60)) + ":" +  formatter.format((totaltwitter / 1000) % 60));
        tvyoutubeTime.setText(formatter.format(((totalyoutube / (1000*60*60)) % 24)) + ":" + formatter.format(((totalyoutube / (1000*60)) % 60)) + ":" +  formatter.format((totalyoutube / 1000) % 60));

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