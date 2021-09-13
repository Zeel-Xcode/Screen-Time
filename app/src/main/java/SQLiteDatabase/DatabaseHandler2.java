package SQLiteDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import Model.NewModel;

public class DatabaseHandler2<insertRecord> extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    // Database Name
    public static final String DATABASE_NAME = "TimeManager2";
    // Contacts table name
    private static final String TABLE_CONTACTS = "socialMedia_Timemanager2";
    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_DEVICEID = "deviceid";
    private static final String KEY_PACKAGENAME = "packagename";
    private static final String KEY_APPNAME = "appname";
    private static final String KEY_STARTTIME = "starttime";
    private static final String KEY_ENDTIME = "endtime";
    private static final String KEY_TOTALSEC = "totalsec";
    private static final String KEY_CURRENTDATE = "currentdate";


    public DatabaseHandler2(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_DEVICEID + " TEXT,"
                + KEY_PACKAGENAME + " TEXT,"
                + KEY_APPNAME + " TEXT,"
                + KEY_STARTTIME + " TEXT,"
                + KEY_ENDTIME + " TEXT,"
                + KEY_TOTALSEC + " TEXT,"
                + KEY_CURRENTDATE + " TEXT" + ")";

        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        onCreate(db);
    }

    public void insertRecord(NewModel newModel) {
        SQLiteDatabase database = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_DEVICEID, newModel.getDeviceid());
        values.put(KEY_PACKAGENAME, newModel.getPackagename());
        values.put(KEY_APPNAME, newModel.getAppname());
        values.put(KEY_STARTTIME, newModel.getStarttime());
        values.put(KEY_ENDTIME, newModel.getEndtime());
        values.put(KEY_TOTALSEC, newModel.getTotalsec());
        values.put(KEY_CURRENTDATE, getCurrentDate());

        database.insert(TABLE_CONTACTS, null, values);
        database.close();
    }

    public void updateRecord(NewModel newModel) {
        SQLiteDatabase database = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_DEVICEID, newModel.getDeviceid());
        values.put(KEY_PACKAGENAME, newModel.getPackagename());
        values.put(KEY_APPNAME, newModel.getAppname());
        values.put(KEY_STARTTIME, newModel.getStarttime());
        values.put(KEY_ENDTIME, newModel.getEndtime());
        values.put(KEY_TOTALSEC, newModel.getTotalsec());
        values.put(KEY_CURRENTDATE, getCurrentDate());

        database.update(TABLE_CONTACTS, values, KEY_ID + "= ?", new String[]{newModel.getId()});
        database.close();
    }

    public ArrayList<NewModel> getAllTime() {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.query(TABLE_CONTACTS, null, null, null, null, null, null, null);
        ArrayList<NewModel> contacts = new ArrayList<NewModel>();
        NewModel contactModel;
        if (cursor.getCount() > 0) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                contactModel = new NewModel();
                contactModel.setId(cursor.getString(0));
                contactModel.setDeviceid(cursor.getString(1));
                contactModel.setPackagename(cursor.getString(2));
                contactModel.setAppname(cursor.getString(3));
                contactModel.setStarttime(cursor.getString(4));
                contactModel.setEndtime(cursor.getString(5));
                contactModel.setTotalsec(cursor.getLong(6));
                contactModel.setCurrentdate(cursor.getString(7));

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

    public void exportappdata(String appname,NumberFormat formatter) {

        String path = Environment.getExternalStorageDirectory() + "/" + DATABASE_NAME;
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String fullName = path + "/" + appname + "_" + System.currentTimeMillis() + ".csv";
        File file = new File(fullName);
        try {
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            SQLiteDatabase database = this.getReadableDatabase();
            String qery = "SELECT * FROM socialMedia_Timemanager2 WHERE appname='" + appname + "'";
            Cursor curCSV = database.rawQuery(qery, null);
            csvWrite.writeNext(curCSV.getColumnNames());

            while (curCSV.moveToNext()) {
                //Which column you want to exprort
                String arrStr[] = new String[curCSV.getColumnCount()];
                for (int i = 0; i < curCSV.getColumnCount(); i++){
                    if (i == 6){
                        long t = Long.parseLong(curCSV.getString(6));
                        String seconds = formatter.format((t / 1000) % 60) ;
                        String minutes =  formatter.format(((t / (1000*60)) % 60));
                        String hours   =  formatter.format(((t / (1000*60*60)) % 24));

                        String total = hours + ":" + minutes + ":" + seconds;
                        arrStr[6] = total;
                    }

                    else {
                        arrStr[i] = curCSV.getString(i);
                    }
                }
                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
            curCSV.close();

        } catch (Exception sqlEx) {
            Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
        }
    }

    public void exportdatedata(String appname, String date,NumberFormat formatter){
        String path = Environment.getExternalStorageDirectory() + "/" + DATABASE_NAME;
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String fullName = path + "/" + appname + "_" + System.currentTimeMillis() + ".csv";
        File file = new File(fullName);
        try {
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            SQLiteDatabase database = this.getReadableDatabase();
            String qery = "SELECT * FROM socialMedia_Timemanager2 WHERE currentdate='" + date + "'  AND  appname='" + appname + "'";
            Cursor curCSV = database.rawQuery(qery, null);
            csvWrite.writeNext(curCSV.getColumnNames());

            while (curCSV.moveToNext()) {
                //Which column you want to exprort
                String arrStr[] = new String[curCSV.getColumnCount()];
                for (int i = 0; i < curCSV.getColumnCount(); i++){
                    if (i == 6){
                        long t = Long.parseLong(curCSV.getString(6));
                        String seconds = formatter.format((t / 1000) % 60) ;
                        String minutes =  formatter.format(((t / (1000*60)) % 60));
                        String hours   =  formatter.format(((t / (1000*60*60)) % 24));

                        String total = hours + ":" + minutes + ":" + seconds;
                        arrStr[6] = total;
                    }

                    else {
                        arrStr[i] = curCSV.getString(i);
                    }
                }
                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
            curCSV.close();

        } catch (Exception sqlEx) {
            Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
        }
    }

    public void exportallappdata(String date, boolean isEmail, Context context, NumberFormat formatter) {

        String path = Environment.getExternalStorageDirectory() + "/" + DATABASE_NAME;
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String fullName = path + "/" + UUID.randomUUID()+ "_" + System.currentTimeMillis() + ".csv";
        File file = new File(fullName);
        try {
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            SQLiteDatabase database = this.getReadableDatabase();
            String qery = "SELECT * FROM socialMedia_Timemanager2 WHERE currentdate='" + date + "'";
            Cursor curCSV = database.rawQuery(qery, null);
            csvWrite.writeNext(curCSV.getColumnNames());

            while (curCSV.moveToNext()) {
                //Which column you want to exprort
                String arrStr[] = new String[curCSV.getColumnCount()];
                for (int i = 0; i < curCSV.getColumnCount(); i++){

                    if (i == 6){
                        long t = Long.parseLong(curCSV.getString(6));
                        String seconds = formatter.format((t / 1000) % 60) ;
                        String minutes =  formatter.format(((t / (1000*60)) % 60));
                        String hours   =  formatter.format(((t / (1000*60*60)) % 24));

                        String total = hours + ":" + minutes + ":" + seconds;
                        arrStr[6] = total;
                    }

                    else {
                        arrStr[i] = curCSV.getString(i);
                    }

                }
                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
            curCSV.close();


            if (isEmail){

                Uri u1 = FileProvider.getUriForFile(
                        context,
                        "com.screentime.provider", //(use your app signature + ".provider" )
                        file);;

                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Yours ScreenTime");
                sendIntent.putExtra(Intent.EXTRA_STREAM, u1);
                sendIntent.setType("text/html");
                context.startActivity(sendIntent);
            }


        } catch (Exception sqlEx) {
            Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
        }
    }


    public static class CSVWriter {

        private PrintWriter pw;

        private char separator;

        private char quotechar;

        private char escapechar;

        private String lineEnd;

        /**
         * The character used for escaping quotes.
         */
        public static final char DEFAULT_ESCAPE_CHARACTER = '"';

        /**
         * The default separator to use if none is supplied to the constructor.
         */
        public static final char DEFAULT_SEPARATOR = ',';

        /**
         * The default quote character to use if none is supplied to the
         * constructor.
         */
        public static final char DEFAULT_QUOTE_CHARACTER = '"';

        /**
         * The quote constant to use when you wish to suppress all quoting.
         */
        public static final char NO_QUOTE_CHARACTER = '\u0000';

        /**
         * The escape constant to use when you wish to suppress all escaping.
         */
        public static final char NO_ESCAPE_CHARACTER = '\u0000';

        /**
         * Default line terminator uses platform encoding.
         */
        public static final String DEFAULT_LINE_END = "\n";

        /**
         * Constructs CSVWriter using a comma for the separator.
         *
         * @param writer the writer to an underlying CSV source.
         */
        public CSVWriter(Writer writer) {
            this(writer, DEFAULT_SEPARATOR, DEFAULT_QUOTE_CHARACTER,
                    DEFAULT_ESCAPE_CHARACTER, DEFAULT_LINE_END);
        }

        /**
         * Constructs CSVWriter with supplied separator, quote char, escape char and line ending.
         *
         * @param writer     the writer to an underlying CSV source.
         * @param separator  the delimiter to use for separating entries
         * @param quotechar  the character to use for quoted elements
         * @param escapechar the character to use for escaping quotechars or escapechars
         * @param lineEnd    the line feed terminator to use
         */
        public CSVWriter(Writer writer, char separator, char quotechar, char escapechar, String lineEnd) {
            this.pw = new PrintWriter(writer);
            this.separator = separator;
            this.quotechar = quotechar;
            this.escapechar = escapechar;
            this.lineEnd = lineEnd;
        }

        /**
         * Writes the next line to the file.
         *
         * @param nextLine a string array with each comma-separated element as a separate
         *                 entry.
         */
        public void writeNext(String[] nextLine) {

            if (nextLine == null)
                return;

            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < nextLine.length; i++) {

                if (i != 0) {
                    sb.append(separator);
                }

                String nextElement = nextLine[i];
                if (nextElement == null)
                    continue;
                if (quotechar != NO_QUOTE_CHARACTER)
                    sb.append(quotechar);
                for (int j = 0; j < nextElement.length(); j++) {
                    char nextChar = nextElement.charAt(j);
                    if (escapechar != NO_ESCAPE_CHARACTER && nextChar == quotechar) {
                        sb.append(escapechar).append(nextChar);
                    } else if (escapechar != NO_ESCAPE_CHARACTER && nextChar == escapechar) {
                        sb.append(escapechar).append(nextChar);
                    } else {
                        sb.append(nextChar);
                    }
                }
                if (quotechar != NO_QUOTE_CHARACTER)
                    sb.append(quotechar);
            }

            sb.append(lineEnd);
            pw.write(sb.toString());

        }

        /**
         * Flush underlying stream to writer.
         *
         * @throws IOException if bad things happen
         */
        public void flush() throws IOException {

            pw.flush();

        }

        /**
         * Close the underlying stream writer flushing any buffered content.
         *
         * @throws IOException if bad things happen
         */
        public void close() throws IOException {
            pw.flush();
            pw.close();
        }

    }

}

