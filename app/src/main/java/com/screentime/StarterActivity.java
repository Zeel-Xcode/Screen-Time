package com.screentime;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import services.GetUsageService1;

public class StarterActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("<<<<NEW ACTIVITY>>>>","<<<<NEW ACTIVITY>>>>");
        startService(new Intent(this, GetUsageService1.class));
    }
}
