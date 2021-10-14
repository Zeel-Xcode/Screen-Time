package com.screentime;

import android.app.ActivityManager;
import android.content.Context;

public class ServiceUtills {

    public static boolean isServiceRunning(Context context, Class<?> serviceClass) {

        final ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        assert manager != null;

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }

        return false;
    }
}
