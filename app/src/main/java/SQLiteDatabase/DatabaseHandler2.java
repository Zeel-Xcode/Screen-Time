package SQLiteDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.renderscript.Sampler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import Model.SqLiteDatabaseModel;

public class DatabaseHandler2<insertRecord> extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    // Database Name
    private static final String DATABASE_NAME = "TimeManager2";
    // Contacts table name
    private static final String TABLE_CONTACTS = "socialMedia_Timemanager2";
    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_APPNAME = "appname";
    private static final String KEY_STARTTIME = "starttime";
    private static final String KEY_ENDTIME = "endtime";
    private static final String KEY_TOTALSEC = "totalsec";
    private static final String KEY_CURRENTDATE = "currentdate";


    public DatabaseHandler2(@Nullable Context context) {
        super(context, DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_APPNAME + " TEXT,"
                + KEY_STARTTIME + " TEXT,"
                + KEY_ENDTIME + " TEXT,"
                + KEY_TOTALSEC + " TEXT,"
                + KEY_CURRENTDATE + " TEXT"+ ")";

        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        onCreate(db);
    }

    public void insertRecord(SqLiteDatabaseModel sqLiteDatabaseModel) {
        SQLiteDatabase database = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_APPNAME, sqLiteDatabaseModel.getAppname());
        values.put(KEY_CURRENTDATE, timeStamp(getCurrentDate()));
        values.put(KEY_STARTTIME, sqLiteDatabaseModel.getStarttime());
        values.put(KEY_ENDTIME, sqLiteDatabaseModel.getEndtime());
        values.put(KEY_TOTALSEC, sqLiteDatabaseModel.getTotalsec());

        database.insert(TABLE_CONTACTS, null, values);
        database.close();
    }

    public void updateRecord(SqLiteDatabaseModel sqLiteDatabaseModel){
        SQLiteDatabase database = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_APPNAME, sqLiteDatabaseModel.getAppname());
        values.put(KEY_STARTTIME, sqLiteDatabaseModel.getStarttime());
        values.put(KEY_ENDTIME, sqLiteDatabaseModel.getEndtime());
        values.put(KEY_TOTALSEC, sqLiteDatabaseModel.getTotalsec());

       database.update(TABLE_CONTACTS, values, KEY_ID + "= ?", new String[]{sqLiteDatabaseModel.getId()});
       database.close();
    }

    public ArrayList<SqLiteDatabaseModel> getAllTime() {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.query(TABLE_CONTACTS, null, null, null, null, null, null);
        ArrayList<SqLiteDatabaseModel> contacts = new ArrayList<SqLiteDatabaseModel>();
        SqLiteDatabaseModel contactModel;
        if (cursor.getCount() > 0) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                contactModel = new SqLiteDatabaseModel();
                contactModel.setId(cursor.getString(0));
                contactModel.setCurentDate(cursor.getString(1));
                contactModel.setStarttime(cursor.getString(2));
                contactModel.setEndtime(cursor.getString(3));
                contactModel.setTotalsec(cursor.getString(4));
                contactModel.setAppname(cursor.getString(5));

                contacts.add(contactModel);
            }
        }
        cursor.close();
        database.close();
        return contacts;
    }

    public String getCurrentDate() {
        String pattern = "yyyy-MM-dd";
        String dateInString = new SimpleDateFormat(pattern).format(new Date());
        return dateInString;
    }

    public String timeStamp(String dateFormat) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sdf.parse(dateFormat);
            return String.valueOf(date.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

}

