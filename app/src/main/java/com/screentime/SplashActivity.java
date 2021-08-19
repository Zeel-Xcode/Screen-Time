package com.screentime;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;

public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final Handler handler = new Handler();
        final Runnable doNextActivity = new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent(SplashActivity.this, SettingActivity.class);
                startActivity(intent);
                finish();
            }
        };
        new Thread() {
            @Override
            public void run() {
                SystemClock.sleep(2000);
                handler.post(doNextActivity);
            }
        }.start();

    }

}