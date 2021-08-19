package com.screentime;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

import Model.UsageModel;

public class StatisticsActivity extends AppCompatActivity {

    Toolbar toolbar;
    ImageView iv_back, iv_setting, iv_image;
    TextView tvTitle, tv_name, tv_totalTime, tv_averageTime, tv_timeReduce;

    String app_name, limit, average;
    UsageModel model;
    int image;
    NumberFormat formatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        toolbar = findViewById(R.id.toolbar);
        iv_back = findViewById(R.id.iv_back);
        iv_setting = findViewById(R.id.iv_setting);
        tvTitle = findViewById(R.id.tvTitle);
        tv_name = findViewById(R.id.tv_name);
        tv_totalTime = findViewById(R.id.tv_totalTime);
        tv_averageTime = findViewById(R.id.tv_averageTime);
        tv_timeReduce = findViewById(R.id.tv_timeReduce);
        iv_image = findViewById(R.id.iv_image);

        setToolbar();

        formatter = new DecimalFormat("00");
        if (getIntent().getExtras() != null) {
            app_name = (String) getIntent().getExtras().get("which");
            image = (int) getIntent().getExtras().get("image");
            model = (UsageModel) getIntent().getExtras().get("data");
            limit = (String) getIntent().getExtras().get("limit");
            average = (String) getIntent().getExtras().get("average");
            if (model != null && app_name != null && !app_name.equals("")) {
                setData();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        iv_setting.setVisibility(View.INVISIBLE);
        iv_setting.setEnabled(false);
    }

    private void setToolbar() {
        setSupportActionBar(toolbar);
        tvTitle.setText("Statistics");
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StatisticsActivity.super.onBackPressed();
            }
        });
    }

    private void setData() {
        tv_name.setText(app_name);
        iv_image.setImageResource(image);
        tv_totalTime.setText(model.getHrs() + ":" + formatter.format(model.getMint()) + " Hours");
        tv_averageTime.setText(average);
        //tv_extraTime.setText(getExtraTime());
        tv_timeReduce.setText(getExtraTime());
    }

    /**
     * Calculate extra time spend by user.
     */
    private String getExtraTime() {
        String time = "0:00";
        java.text.DateFormat df = new java.text.SimpleDateFormat("HH:mm");
        java.util.Date date1 = null;
        java.util.Date date2 = null;
        long diff = 0;
        try {
            date1 = df.parse(model.getHrs() + ":" + model.getMint());
            date2 = df.parse(limit);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date1 != null && date2 != null) {
            if (date1.getTime() > date2.getTime()) {
                diff = date1.getTime() - date2.getTime();
                time = convertTime(diff);
            }
        }
        return time;
    }

    /**
     * Convert time from millisecond to String.
     */
    private String convertTime(long time) {
        int minutes = (int) ((time / (1000 * 60)) % 60);

        int hours = (int) ((time / (1000 * 60 * 60)) % 24);

        return hours + ":" + formatter.format(minutes);
    }
}