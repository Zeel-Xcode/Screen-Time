package com.screentime;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

import SQLiteDatabase.DatabaseHandler2;

public class BackupAndRestore1 {

    public BackupAndRestore1() {

    }

    public static void importDB(Context context, DatabaseHandler2 databaseHandler2) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            if (sd.canWrite()) {
                File backupDB = context.getDatabasePath(databaseHandler2.DATABASE_NAME);
                String backupDBPath = String.format("%s.bak", databaseHandler2.DATABASE_NAME);
                File currentDB = new File(sd, backupDBPath);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();

                Toast.makeText(context, "Import Successful!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void exportDB(Context context, DatabaseHandler2 databaseHandler2) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String backupDBPath = String.format("%s.bak", databaseHandler2.DATABASE_NAME);
                File currentDB = context.getDatabasePath(databaseHandler2.DATABASE_NAME);
                File backupDB = new File(sd, backupDBPath);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();

                Toast.makeText(context, "Backup Successful!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
