package com.screentime;

import android.app.Application;

/**
 * Created by intel on 05-12-2017.
 */

public class MyApplication extends Application {

    public static long COUNT;

    public static long getCOUNT() {
        return COUNT;
    }

    public static void setCOUNT(long COUNT) {
        com.screentime.MyApplication.COUNT = COUNT;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
