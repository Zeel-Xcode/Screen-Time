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

public class DatabaseHandler<insertRecord> extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "TimeManager";
    // Contacts table name
    private static final String TABLE_CONTACTS = "socialMedia_Timemanager";
    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_CURRENTDATE = "currentdate";
    private static final String KEY_FACEBOOKTIME = "facebook";
    private static final String KEY_INSTAGRAMTIME = "instagram";
    private static final String KEY_SNAPCHAT = "snapchat";


    public DatabaseHandler( Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_CURRENTDATE + " TEXT,"
                + KEY_FACEBOOKTIME + " TEXT,"
                + KEY_INSTAGRAMTIME + " TEXT,"
                + KEY_SNAPCHAT + " TEXT" + ")";
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
        values.put(KEY_CURRENTDATE, timeStamp(getCurrentDate()));
        values.put(KEY_FACEBOOKTIME, sqLiteDatabaseModel.getFacebookTime());
        values.put(KEY_INSTAGRAMTIME, sqLiteDatabaseModel.getInstagramTime());
        values.put(KEY_SNAPCHAT, sqLiteDatabaseModel.getSnapChatTime());

        database.insert(TABLE_CONTACTS, null, values);
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
                contactModel.setFacebookTime(cursor.getString(2));
                contactModel.setInstagramTime(cursor.getString(3));
                contactModel.setSnapChatTime(cursor.getString(4));

                contacts.add(contactModel);
            }
        }
        cursor.close();
        database.close();
        return contacts;
    }

    public void updateFacebookTime(SqLiteDatabaseModel sqLiteDatabaseModel) {
        SQLiteDatabase database = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_FACEBOOKTIME, sqLiteDatabaseModel.getFacebookTime());
        values.put(KEY_CURRENTDATE, timeStamp(getCurrentDate()));
        database.update(TABLE_CONTACTS, values, KEY_ID + " = ?", new String[]{sqLiteDatabaseModel.getId()});
        database.close();
    }


    public void updateInstagramTime(SqLiteDatabaseModel sqLiteDatabaseModel) {
        SQLiteDatabase database = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_INSTAGRAMTIME, sqLiteDatabaseModel.getInstagramTime());
        values.put(KEY_CURRENTDATE, timeStamp(getCurrentDate()));
        database.update(TABLE_CONTACTS, values, KEY_ID + " = ?", new String[]{sqLiteDatabaseModel.getId()});
        database.close();
    }

    public void updateSnapChatTime(SqLiteDatabaseModel sqLiteDatabaseModel) {
        SQLiteDatabase database = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_SNAPCHAT, sqLiteDatabaseModel.getSnapChatTime());
        values.put(KEY_CURRENTDATE, timeStamp(getCurrentDate()));
        database.update(TABLE_CONTACTS, values, KEY_ID + " = ?", new String[]{sqLiteDatabaseModel.getId()});
        database.close();
    }

    // Deleting single contact
    public void deleteTime(SqLiteDatabaseModel sqLiteDatabaseModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, KEY_ID + " = ?", new String[]{String.valueOf(sqLiteDatabaseModel.getId())});
        db.close();
    }

    public ArrayList<SqLiteDatabaseModel> dateQuery(String minDate, String maxDate) {
        SQLiteDatabase database = this.getReadableDatabase();
        String[] columns = new String[]{KEY_ID, KEY_CURRENTDATE, KEY_FACEBOOKTIME, KEY_INSTAGRAMTIME, KEY_SNAPCHAT};
        Cursor cursor = database.query(TABLE_CONTACTS + " WHERE " + KEY_CURRENTDATE + ">=" + minDate + " AND " + KEY_CURRENTDATE + "<=" + maxDate, null, null, null, null, null, null);
        ArrayList<SqLiteDatabaseModel> contacts = new ArrayList<SqLiteDatabaseModel>();
        SqLiteDatabaseModel contactModel;
        if (cursor.getCount() > 0) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                contactModel = new SqLiteDatabaseModel();
                contactModel.setId(cursor.getString(0));
                contactModel.setCurentDate(cursor.getString(1));
                contactModel.setFacebookTime(cursor.getString(2));
                contactModel.setInstagramTime(cursor.getString(3));
                contactModel.setSnapChatTime(cursor.getString(4));
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

    public void deleteDatabase() {
        SQLiteDatabase database = this.getReadableDatabase();
        onUpgrade(database, 1, 1);
    }
}
