package com.screentime;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import Fragments.AllDataFragment;
import Fragments.DateFragment;

public class Tabadapter extends FragmentPagerAdapter {

    private Context context;
    int totalTabs;


    public Tabadapter(Context context, @NonNull FragmentManager fm, int totalTabs) {
        super(fm);
        context = context;
        this.totalTabs = totalTabs;

    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                AllDataFragment allDataFragment = new AllDataFragment();
                return allDataFragment;
            case 1:
               DateFragment dateFragment = new DateFragment();
                return dateFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {

        return totalTabs;
    }
}
