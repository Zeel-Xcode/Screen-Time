package services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * This reciever starts usage service when device is rebooted or service is killed forcely.
 */

public class ScreeOnReciever extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Alarm", Toast.LENGTH_SHORT).show();
        context.startService(new Intent(context,GetUsageService1.class));
    }
}
