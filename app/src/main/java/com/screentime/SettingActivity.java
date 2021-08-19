package com.screentime;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.screentime.utils.AppConstant;
import com.screentime.utils.CommonUtils;

public class SettingActivity extends AppCompatActivity {

    String fbActive, instaActive, snapActive;
    Toolbar toolbar;
    ImageView iv_back, iv_setting, iv_fbalert, iv_instaalert, iv_snapchatalert;
    TextView tvTitle, tv_done;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        toolbar = findViewById(R.id.toolbar);
        iv_back = findViewById(R.id.iv_back);
        iv_setting = findViewById(R.id.iv_setting);
        tvTitle = findViewById(R.id.tvTitle);
        iv_fbalert = findViewById(R.id.iv_fbalert);
        iv_instaalert = findViewById(R.id.iv_instaalert);
        iv_snapchatalert = findViewById(R.id.iv_snapchatalert);
        tv_done = findViewById(R.id.tv_done);

        setToolbar();

        iv_fbalert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CommonUtils.getPreferencesString(SettingActivity.this, AppConstant.FBACTIVE).equals("")) {
                    CommonUtils.savePreferencesString(SettingActivity.this, AppConstant.FBACTIVE, "active");
                } else {
                    CommonUtils.savePreferencesString(SettingActivity.this, AppConstant.FBACTIVE, "");
                }
                setData();
            }
        });

        iv_instaalert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CommonUtils.getPreferencesString(SettingActivity.this, AppConstant.INSTAACTIVE).equals("")) {
                    CommonUtils.savePreferencesString(SettingActivity.this, AppConstant.INSTAACTIVE, "active");
                } else {
                    CommonUtils.savePreferencesString(SettingActivity.this, AppConstant.INSTAACTIVE, "");
                }
                setData();
            }
        });

        iv_snapchatalert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CommonUtils.getPreferencesString(SettingActivity.this, AppConstant.SNAPACTIVE).equals("")) {
                    CommonUtils.savePreferencesString(SettingActivity.this, AppConstant.SNAPACTIVE, "active");
                } else {
                    CommonUtils.savePreferencesString(SettingActivity.this, AppConstant.SNAPACTIVE, "");
                }
                setData();
            }
        });


        tv_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startActivity(new Intent(SettingActivity.this, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                Intent intent = new Intent(SettingActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        iv_setting.setVisibility(View.INVISIBLE);
        iv_back.setVisibility(View.INVISIBLE);
        iv_setting.setEnabled(false);
        iv_back.setEnabled(false);
        setData();
    }

    private void setToolbar() {
        setSupportActionBar(toolbar);
        tvTitle.setText("Setting");
    }

    private void setData() {
        fbActive = CommonUtils.getPreferencesString(this, AppConstant.FBACTIVE);
        instaActive = CommonUtils.getPreferencesString(this, AppConstant.INSTAACTIVE);
        snapActive = CommonUtils.getPreferencesString(this, AppConstant.SNAPACTIVE);
        if (fbActive.equals("")) {
            iv_fbalert.setImageResource(R.drawable.pause_disable);
        } else {
            iv_fbalert.setImageResource(R.drawable.pause_enable);
        }
        if (instaActive.equals("")) {
            iv_instaalert.setImageResource(R.drawable.pause_disable);
        } else {
            iv_instaalert.setImageResource(R.drawable.pause_enable);
        }
        if (snapActive.equals("")) {
            iv_snapchatalert.setImageResource(R.drawable.pause_disable);
        } else {
            iv_snapchatalert.setImageResource(R.drawable.pause_enable);
        }
    }
}