package services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.screentime.HomeActivity;

/**
 * This reciever starts usage service when device is rebooted or service is killed forcely.
 */

public class ScreeOnReciever extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
//        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
//
//            Intent startService = new Intent(context, GetUsageService1.class);
//            context.startService(startService);
//        }

        Toast.makeText(context, "Success", Toast.LENGTH_LONG).show();
        Intent i = new Intent(context, HomeActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);



    }
}
