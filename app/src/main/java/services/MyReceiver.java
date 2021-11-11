package services;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import Model.UsagesModel;

public class MyReceiver extends BroadcastReceiver {

    private boolean screenOff;
    KeyguardManager myKM ;

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "" + intent.getAction(), Toast.LENGTH_SHORT).show();

        myKM = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);


        if(myKM.inKeyguardRestrictedInputMode()) {
            screenOff = true;

        } else {
            screenOff = false;
        }

        Intent i = new Intent(context, GetUsageService1.class);
        i.putExtra("screen_state", screenOff);
        i.putExtra("from_receiver",true);
        context.startService(i);
    }
}