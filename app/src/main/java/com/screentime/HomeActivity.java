package com.screentime;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.facebook.appevents.AppEventsLogger;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import Model.NewModel;
import Model.UsagesModel;
import SQLiteDatabase.DatabaseHandler2;
import services.GetUsageService1;

public class HomeActivity extends AppCompatActivity {
    FloatingActionButton fab;
    String date;
    Toolbar toolbar;
    ImageView setting, ic_share;
    TextView tvTitle, tvFacebook, tvInsta, tvSnapChat, tvfbTime, tvinstaTime, tvsnapchatTime, datepicker, tvMessages, tvmessageTime, tvTiktok, tvtiktokTime, tvPhone, tvphoneTime, tvtwitterTime, tvyoutubeTime, Phoneusage, phoneTime;
    LinearLayout llFacebook, llSnapchat, llInsta, llMessages, llTktok, llPhone, llyoutube, lltwitter, llphoneusage;

    NumberFormat formatter;
    private AppEventsLogger logger;
    private DatabaseHandler2 databaseHandler2;
    private String id;
    String datepickerstamp;

    public static final String NOTIFICATION_CHANNEL_NAME = "App usages Tracking";
    public static final String NOTIFICATION_CHANNEL_ID = "Screentimer_background_service_channel";
    public static final int ONGOING_NOTIFICATION_ID = 100;

    private int mYear, mMonth, mDay;

    BackupAndRestore1 backupAndRestore;
    boolean restoredata = false;

    long startusages, endusages;
    long totalseconds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        phoneTime = findViewById(R.id.phoneTime);
        fab = findViewById(R.id.fab);
        tvphoneTime = findViewById(R.id.tvphoneTime);
        tvPhone = findViewById(R.id.tvPhone);
        tvtiktokTime = findViewById(R.id.tvtiktokTime);
        tvTiktok = findViewById(R.id.tvTiktok);
        tvmessageTime = findViewById(R.id.tvmessageTime);
        tvMessages = findViewById(R.id.tvMessages);
        datepicker = findViewById(R.id.datepicker);
        toolbar = findViewById(R.id.toolbar);
        setting = findViewById(R.id.setting);
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

        backupAndRestore = new BackupAndRestore1();

        SharedPreferences preferences1 = getSharedPreferences("sharedrestore", MODE_PRIVATE);
        restoredata = preferences1.getBoolean("restore", false);

        boolean firstrun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("firstrun", true);
        if (firstrun) {

            if (!restoredata) {
                File sd = Environment.getExternalStorageDirectory();
                String backupDBPath = String.format("%s.bak", databaseHandler2.DATABASE_NAME);
                File backupDB = new File(sd, backupDBPath);

                File file = new File(backupDB.getAbsolutePath());
                if (file.exists()) {
                    Dialog dialog1 = new Dialog(HomeActivity.this);
                    dialog1.setContentView(R.layout.custom_backup_dialog);
                    dialog1.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                    TextView title = dialog1.findViewById(R.id.title);
                    title.setText("Restore");

                    TextView msg = dialog1.findViewById(R.id.msg);
                    msg.setText("Are you sure you want to restore?");

                    TextView backup = dialog1.findViewById(R.id.backup);
                    backup.setText("Restore");

                    backup.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog1.dismiss();
                            if (checkStoragePermission()) {
                                backupAndRestore.importDB(HomeActivity.this, databaseHandler2);
                                restoredata = true;
                                setData(datepicker.getText().toString());
                                SharedPreferences preferences = getSharedPreferences("sharedrestore", MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putBoolean("restore", restoredata);
                                editor.apply();
                            } else {
                                requestPermission("restore_start");
                            }

                        }
                    });

                    TextView cancel = dialog1.findViewById(R.id.cancel);
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog1.dismiss();
                        }
                    });

                    dialog1.show();
                } else {

                }
            }

            getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                    .edit()
                    .putBoolean("firstrun", false)
                    .commit();
        }


        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(HomeActivity.this);
                dialog.setContentView(R.layout.custom_backup_dialog);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                TextView title = dialog.findViewById(R.id.title);
                title.setText("Backup or Restore");

                TextView msg = dialog.findViewById(R.id.msg);
                msg.setText("Are you sure you want to backup or restore?");

                TextView backup = dialog.findViewById(R.id.backup);

                backup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        if (checkStoragePermission()) {
                            backupAndRestore.exportDB(HomeActivity.this, databaseHandler2);
                        }else {
                            requestPermission("backup_setting");
                        }



                    }
                });

                TextView cancel = dialog.findViewById(R.id.cancel);
                cancel.setText("restore");
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialog.dismiss();

                        if (checkStoragePermission()) {
                            backupAndRestore.importDB(HomeActivity.this, databaseHandler2);
                        }else {
                            requestPermission("restore_setting");
                        }


                    }
                });

                dialog.show();
            }
        });

        ic_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkStoragePermission()) {
                    databaseHandler2.exportallappdata(datepicker.getText().toString(), true, HomeActivity.this, formatter);
                } else {
                    requestPermission("restore_start");
                }

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
                        databaseHandler2.exportallappdata(datepicker.getText().toString(), false, HomeActivity.this, formatter);

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
                datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
            }
        });

        SharedPreferences preferences = getSharedPreferences("Usagestime", MODE_PRIVATE);

        totalseconds = preferences.getLong("totalusages", 0);

        phoneTime.setText(formatter.format(((totalseconds / (1000 * 60 * 60)) % 24)) + ":" + formatter.format(((totalseconds / (1000 * 60)) % 60)) + ":" + formatter.format((totalseconds / 1000) % 60));

    }

    private boolean checkStoragePermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {

            return Environment.isExternalStorageManager();

        } else {
            int result = ContextCompat.checkSelfPermission(HomeActivity.this, READ_EXTERNAL_STORAGE);
            int result1 = ContextCompat.checkSelfPermission(HomeActivity.this, WRITE_EXTERNAL_STORAGE);

            return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestPermission(String str) {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
                if (str.equalsIgnoreCase("restore_start")){
                    startActivityForResult(intent, 2296);
                }else if (str.equalsIgnoreCase("backup_setting")){
                    startActivityForResult(intent,2297);
                }else if (str.equalsIgnoreCase("restore_setting")){
                    startActivityForResult(intent,2298);
                }

            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                if (str.equalsIgnoreCase("restore_start")){
                    startActivityForResult(intent, 2296);
                }else if (str.equalsIgnoreCase("backup_setting")){
                    startActivityForResult(intent,2297);
                }else if (str.equalsIgnoreCase("restore_setting")){
                    startActivityForResult(intent,2298);
                }
            }
        } else {
            //below android 11

            if (str.equalsIgnoreCase("restore_start")){
                ActivityCompat.requestPermissions(HomeActivity.this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, 123);
            }else if (str.equalsIgnoreCase("backup_setting")){
                ActivityCompat.requestPermissions(HomeActivity.this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, 124);
            }else if (str.equalsIgnoreCase("restore_setting")){
                ActivityCompat.requestPermissions(HomeActivity.this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, 125);
            }


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2296) {
            if (SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    // perform action when allow permission success
                    backupAndRestore.importDB(HomeActivity.this, databaseHandler2);
                    restoredata = true;
                    setData(datepicker.getText().toString());
                    SharedPreferences preferences = getSharedPreferences("sharedrestore", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("restore", restoredata);
                    editor.apply();
                } else {
                    Dialog dialog = new Dialog(this);
                    dialog.setContentView(R.layout.files_permissiondialog);
                    TextView ok = dialog.findViewById(R.id.ok);
                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            try {
                                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                                intent.addCategory("android.intent.category.DEFAULT");
                                intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
                                startActivityForResult(intent, 2296);
                            } catch (Exception e) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                                startActivityForResult(intent, 2296);
                            }
                        }
                    });
                    if (!dialog.isShowing()) {
                        dialog.show();
                    }else {
                        dialog.dismiss();
                    }
                }
            }
        }
        else if (requestCode == 2297){
            if (SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    // perform action when allow permission success
                    backupAndRestore.exportDB(HomeActivity.this, databaseHandler2);
                } else {
                    Dialog dialog = new Dialog(this);
                    dialog.setContentView(R.layout.files_permissiondialog);
                    TextView ok = dialog.findViewById(R.id.ok);
                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            try {
                                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                                intent.addCategory("android.intent.category.DEFAULT");
                                intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
                                startActivityForResult(intent, 2296);
                            } catch (Exception e) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                                startActivityForResult(intent, 2296);
                            }
                        }
                    });
                    if (!dialog.isShowing()) {
                        dialog.show();
                    }else {
                        dialog.dismiss();
                    }
                }
            }
        }
        else if (requestCode == 2298){
            if (SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    // perform action when allow permission success
                    backupAndRestore.importDB(HomeActivity.this, databaseHandler2);
                } else {
                    Dialog dialog = new Dialog(this);
                    dialog.setContentView(R.layout.files_permissiondialog);
                    TextView ok = dialog.findViewById(R.id.ok);
                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            try {
                                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                                intent.addCategory("android.intent.category.DEFAULT");
                                intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
                                startActivityForResult(intent, 2296);
                            } catch (Exception e) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                                startActivityForResult(intent, 2296);
                            }
                        }
                    });
                    if (!dialog.isShowing()) {
                        dialog.show();
                    }else {
                        dialog.dismiss();
                    }
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    backupAndRestore.importDB(HomeActivity.this, databaseHandler2);
                    restoredata = true;
                    setData(datepicker.getText().toString());
                    SharedPreferences preferences = getSharedPreferences("sharedrestore", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("restore", restoredata);
                    editor.apply();
                } else {
                    //code for deny
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(HomeActivity.this);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("Write Storage permission is necessary to export App Data");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(HomeActivity.this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, 123);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                }
            }
        } else if (requestCode == 124){
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    backupAndRestore.exportDB(HomeActivity.this, databaseHandler2);
                } else {
                    //code for deny
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(HomeActivity.this);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("Write Storage permission is necessary to export App Data");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(HomeActivity.this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, 123);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                }
            }
        }else if (requestCode == 125){
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    backupAndRestore.importDB(HomeActivity.this, databaseHandler2);
                } else {
                    //code for deny
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(HomeActivity.this);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("Write Storage permission is necessary to export App Data");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(HomeActivity.this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, 123);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                }
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onResume() {
        super.onResume();

        if (checkPermission()) {
            setData(datepicker.getText().toString());
            if (SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(new Intent(this, GetUsageService1.class));
            } else {
                startService(new Intent(this, GetUsageService1.class));
            }
        } else {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(intent);
        }
    }

    private void RequestPermission() {
        // Check if Android M or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Show alert dialog to the user saying a separate permission is needed
            // Launch the settings activity if the user prefers
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 2210);
        }
    }

    private void setToolbar() {
        setSupportActionBar(toolbar);
        tvTitle.setText("PASTime");
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
        long totalphoneusages = 0;

        ArrayList<NewModel> getdata = databaseHandler2.getAllTime();
        ArrayList<UsagesModel> getdatausages = databaseHandler2.getAllTimeUsages();

        if (databaseHandler2.getAllTime().size() > 0) {
            fab.setVisibility(View.GONE);
        } else {
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

        if (getdatausages.size() > 0) {
            for (int i = 0; i < getdatausages.size(); i++) {
                if (getdatausages.get(i).getCurrentdate().equals(currentDate)) {
                    totalphoneusages = totalphoneusages + getdatausages.get(i).getTotalsec();
                }
            }
        }

        phoneTime.setText(formatter.format(((totalphoneusages / (1000 * 60 * 60)) % 24)) + ":" + formatter.format(((totalphoneusages / (1000 * 60)) % 60)) + ":" + formatter.format((totalphoneusages / 1000) % 60));


        tvfbTime.setText(formatter.format(((totalfb / (1000 * 60 * 60)) % 24)) + ":" + formatter.format(((totalfb / (1000 * 60)) % 60)) + ":" + formatter.format((totalfb / 1000) % 60));
        tvinstaTime.setText(formatter.format(((totalinsta / (1000 * 60 * 60)) % 24)) + ":" + formatter.format(((totalinsta / (1000 * 60)) % 60)) + ":" + formatter.format((totalinsta / 1000) % 60));
        tvmessageTime.setText(formatter.format(((totalmessage / (1000 * 60 * 60)) % 24)) + ":" + formatter.format(((totalmessage / (1000 * 60)) % 60)) + ":" + formatter.format((totalmessage / 1000) % 60));
        tvtiktokTime.setText(formatter.format(((totaltiktok / (1000 * 60 * 60)) % 24)) + ":" + formatter.format(((totaltiktok / (1000 * 60)) % 60)) + ":" + formatter.format((totaltiktok / 1000) % 60));
        tvphoneTime.setText(formatter.format(((totalphone / (1000 * 60 * 60)) % 24)) + ":" + formatter.format(((totalphone / (1000 * 60)) % 60)) + ":" + formatter.format((totalphone / 1000) % 60));
        tvsnapchatTime.setText(formatter.format(((totalsnap / (1000 * 60 * 60)) % 24)) + ":" + formatter.format(((totalsnap / (1000 * 60)) % 60)) + ":" + formatter.format((totalsnap / 1000) % 60));
        tvtwitterTime.setText(formatter.format(((totaltwitter / (1000 * 60 * 60)) % 24)) + ":" + formatter.format(((totaltwitter / (1000 * 60)) % 60)) + ":" + formatter.format((totaltwitter / 1000) % 60));
        tvyoutubeTime.setText(formatter.format(((totalyoutube / (1000 * 60 * 60)) % 24)) + ":" + formatter.format(((totalyoutube / (1000 * 60)) % 60)) + ":" + formatter.format((totalyoutube / 1000) % 60));

    }

    private String getCurrentDate() {
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        String dateInString = sdf2.format(System.currentTimeMillis());
        return dateInString;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}