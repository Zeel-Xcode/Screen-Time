package com.screentime;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.LinearLayout;

import com.google.android.material.tabs.TabLayout;

import java.util.Calendar;

import Fragments.AllDataFragment;
import Fragments.DateFragment;

public class ResultActivity extends AppCompatActivity {

    TabLayout tabLayout;
    LinearLayout viewpager;

    private int mYear, mMonth, mDay;
    String currentdate;
    String appname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        tabLayout = findViewById(R.id.tablayout);
        viewpager = findViewById(R.id.viewpager);

        appname = getIntent().getStringExtra("which");

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        currentdate =  String.format("%d-%02d-%02d", mYear, (mMonth + 1), mDay);

        tabLayout.addTab(tabLayout.newTab().setText("All Data"));
        tabLayout.addTab(tabLayout.newTab().setText(currentdate));


        Fragment fragment = new AllDataFragment();
        Bundle bundle = new Bundle();
        bundle.putString("which",appname);
        fragment.setArguments(bundle);
        replaceFragment(fragment, false);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                final Fragment[] fragment = {null};
                switch (tab.getPosition()) {
                    case 0:
                        fragment[0] = new AllDataFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("which",appname);
                        fragment[0].setArguments(bundle);
                        replaceFragment(fragment[0], false);
                        break;
                    case 1:

                        DatePickerDialog datePickerDialog = new DatePickerDialog(ResultActivity.this, R.style.DialogTheme,
                                new DatePickerDialog.OnDateSetListener() {

                                    @Override
                                    public void onDateSet(DatePicker view, int year,
                                                          int monthOfYear, int dayOfMonth) {

                                        mYear = year;
                                        mMonth = monthOfYear;
                                        mDay = dayOfMonth;

                                       currentdate =  String.format("%d-%02d-%02d", year, (monthOfYear + 1), dayOfMonth);
                                       tab.setText(currentdate);

                                        fragment[0] = new DateFragment();
                                        Bundle bundle1 = new Bundle();
                                        bundle1.putString("which",appname);
                                        bundle1.putString("date",currentdate);
                                        fragment[0].setArguments(bundle1);
                                        replaceFragment(fragment[0],false);

                                    }
                                }, mYear, mMonth, mDay);

                        datePickerDialog.show();
                        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);

                        break;
                }

            }

            @SuppressLint("ResourceAsColor")
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                final Fragment[] fragment = {null};
                switch (tab.getPosition()) {
                    case 1:

                        DatePickerDialog datePickerDialog = new DatePickerDialog(ResultActivity.this, R.style.DialogTheme,
                                new DatePickerDialog.OnDateSetListener() {

                                    @Override
                                    public void onDateSet(DatePicker view, int year,
                                                          int monthOfYear, int dayOfMonth) {

                                        mYear = year;
                                        mMonth = monthOfYear;
                                        mDay = dayOfMonth;

                                        currentdate =  String.format("%d-%02d-%02d", year, (monthOfYear + 1), dayOfMonth);
                                        tab.setText(currentdate);

                                        fragment[0] = new DateFragment();
                                        Bundle bundle1 = new Bundle();
                                        bundle1.putString("which",appname);
                                        bundle1.putString("date",currentdate);
                                        fragment[0].setArguments(bundle1);
                                        replaceFragment(fragment[0],false);

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