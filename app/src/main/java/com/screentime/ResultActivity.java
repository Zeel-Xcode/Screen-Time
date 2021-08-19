package com.screentime;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TableLayout;

import com.google.android.material.tabs.TabLayout;

import java.util.Calendar;

import Fragments.AllDataFragment;
import Fragments.DateFragment;
import Model.UsageModel;

public class ResultActivity extends AppCompatActivity {

    TabLayout tabLayout;
    LinearLayout viewpager;

    private int mYear, mMonth, mDay;
    String currentdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        tabLayout = findViewById(R.id.tablayout);
        viewpager = findViewById(R.id.viewpager);

        replaceFragment(new AllDataFragment(), false);

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        currentdate = mYear + "-" + (mMonth + 1) + "-" + mDay;

        tabLayout.addTab(tabLayout.newTab().setText("All Data"));
        tabLayout.addTab(tabLayout.newTab().setText(currentdate));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                final Fragment[] fragment = {null};
                switch (tab.getPosition()) {
                    case 0:
                        fragment[0] = new AllDataFragment();
                        replaceFragment(fragment[0], false);
                        break;
                    case 1:

                        DatePickerDialog datePickerDialog = new DatePickerDialog(ResultActivity.this, R.style.DialogTheme,
                                new DatePickerDialog.OnDateSetListener() {

                                    @Override
                                    public void onDateSet(DatePicker view, int year,
                                                          int monthOfYear, int dayOfMonth) {

                                        currentdate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                                        tab.setText(currentdate);

                                    }
                                }, mYear, mMonth, mDay);
                        datePickerDialog.show();

                        fragment[0] = new DateFragment();
                        replaceFragment(fragment[0],false);

                        break;
                }

            }

            @SuppressLint("ResourceAsColor")
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 1:

                        DatePickerDialog datePickerDialog = new DatePickerDialog(ResultActivity.this, R.style.DialogTheme,
                                new DatePickerDialog.OnDateSetListener() {

                                    @Override
                                    public void onDateSet(DatePicker view, int year,
                                                          int monthOfYear, int dayOfMonth) {

                                        currentdate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                                        tab.setText(currentdate);

                                    }
                                }, mYear, mMonth, mDay);
                        datePickerDialog.show();

                        break;
                }
            }
        });

    }

    public void replaceFragment(androidx.fragment.app.Fragment someFragment, boolean isshow) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.viewpager, someFragment);
        transaction.commit();
    }
}