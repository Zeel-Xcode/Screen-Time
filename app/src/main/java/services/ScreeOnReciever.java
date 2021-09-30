package services;

import android.app.KeyguardManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import com.screentime.HomeActivity;
import com.screentime.R;

/**
 * This reciever starts usage service when device is rebooted or service is killed forcely.
 */

public class ScreeOnReciever extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
                Intent activityIntent = new Intent(context, HomeActivity.class);
                activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Toast.makeText(context, "sucess", Toast.LENGTH_SHORT).show();
                context.startActivity(activityIntent);
            }
        } else{
            Intent i = new Intent(context, HomeActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }



    }
}
