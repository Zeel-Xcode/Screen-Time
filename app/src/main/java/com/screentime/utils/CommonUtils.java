package com.screentime.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.material.snackbar.Snackbar;
import com.screentime.R;

public class CommonUtils {

    public static void snackBar(View parent, String msg) {
        Snackbar.make(parent, msg, Snackbar.LENGTH_SHORT).show();
    }

    public static void savePreferencesString(Context context, String key, String value) {
        try {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(key, value);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getPreferencesString(Context context, String key) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(key, "");
    }

    public static void savePreferencesBoolean(Context context, String key, boolean value) {
        try {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(key, value);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean getPreferencesBoolean(Context context, String key) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(key, false);
    }


    public static void savePreferencesInteger(Context context, String key, long value) {
        try {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong(key, value);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static long getPreferencesInteger(Context context, String key) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getLong(key, 0);
    }

    public static void showDialog(final Context context) {
        final ImageView iv_cancel;
        final LayoutInflater inflater = LayoutInflater.from(context);
        final Dialog mDialog = new Dialog(context,
                android.R.style.Theme_Translucent_NoTitleBar);
        mDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.setCancelable(true);
        mDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        mDialog.getWindow().setGravity(Gravity.CENTER);

        WindowManager.LayoutParams lp = mDialog.getWindow().getAttributes();
        lp.dimAmount = 0.75f;
        mDialog.getWindow()
                .addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow();
        mDialog.getWindow().setAttributes(lp);
        final View dialoglayout = inflater.inflate(R.layout.activity_alert_dialog2, null);
        mDialog.setContentView(dialoglayout);
        mDialog.setCanceledOnTouchOutside(false);
        iv_cancel = dialoglayout.findViewById(R.id.ivCancel);
        iv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }
}
