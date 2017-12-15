package ru.mano_ldc.appsstarter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.text.format.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by valera on 21.11.2017.
 */

public final class ApplicationsScheduler {

    public static final String TYPE_FILE ="File";
    public static final String TYPE_APP ="App";
    public static final String APP_ID = "id";
    public static final String TYPE = "type";
    public static final String FILE_NAME = "file_name";
    public static final String APP_NAME = "name";
    public static final String APP_PACKAGE = "package";
    public static final String APP_START_TIME = "startTime";
    public static String SCHEDULE_TABLE = "schedule";
    public static final String[] columns = {
            APP_ID,
            TYPE,
            FILE_NAME,
            APP_NAME,
            APP_PACKAGE,
            APP_START_TIME};
    @NonNull
    public static String millisToTimeString(String time) {
        return DateFormat.format("HH.mm", Long.parseLong(time)).toString();
    }

    @NonNull
    public static String millisToTimeString(long time) {
        return DateFormat.format("HH.mm", time).toString();
    }

    private ArrayList<HashMap<String, String>> scheduledAppsList = new ArrayList<>();
    private SQLiteDatabase db;
    private Context context;

    ApplicationsScheduler(Context context) throws SQLException {
        this.context = context;
        db = new SQLiteOpener(context).getWritableDatabase();
        if (db == null) {
            throw new SQLException("Не удаётся подключиться к локальной базе данных...");
        }
        updateScheduleList();
    }

    @Override
    protected void finalize() throws Throwable {
        db.close();
    }

    private void updateScheduleList() {
        scheduledAppsList.clear();
        Cursor c = db.query(SCHEDULE_TABLE, columns, null, null, null, null, APP_START_TIME);

        if (c.getCount() != 0) {
            while (c.moveToNext() == true) {
                HashMap<String, String> map = new HashMap<>();
                for (String curCol : columns) {
                    if (curCol == APP_START_TIME) {
                        map.put(curCol, millisToTimeString(c.getString(c.getColumnIndex(curCol))));
                    }
                    else {
                        map.put(curCol, c.getString(c.getColumnIndex(curCol)));
                    }
                }
                scheduledAppsList.add(map);
            }
        }
    }

    private long getMillisFromDayStart(long time) throws ParseException {
        String timeString = millisToTimeString(time);
        return timeStringToMillis(timeString);
    }

    private long timeStringToMillis(String time) throws ParseException {
        return new SimpleDateFormat("HH.mm").parse(time).getTime();
    };

    public String[] getColumnsForAdapter() {
        return columns;
    }

    public ArrayList<HashMap<String, String>> getApps() {
        return scheduledAppsList;
    }

    public HashMap<String, String> getAppByID(String id) throws SQLException {
        HashMap<String, String> result = new HashMap<>();
        String selection = APP_ID + " = ?";
        String[] selectionArgs = new String[] {id};
        Cursor c = db.query(SCHEDULE_TABLE, columns, selection, selectionArgs, null, null, null);
        if (c.moveToFirst() == true) {
            for (String curCol : columns) {
                result.put(curCol, c.getString(c.getColumnIndex(curCol)));
            }
        }
        else {
            throw new SQLException("Не удаётся найти приложение с указанным id...");
        }
        return result;
    }

    public ScheduledElement getScheduledElementByTime(String time) throws ParseException {
        return getScheduledElementByTime(timeStringToMillis(time));
    }

    public ScheduledElement getScheduledElementByTime(long time) throws ParseException {
        ScheduledElement scheduledElement = null;
        String selection = APP_START_TIME + " <= ?";
        String[] selectionArgs = new String[] { Long.toString(getMillisFromDayStart(time)) };
        Cursor c = db.query(SCHEDULE_TABLE, columns, selection, selectionArgs,null, null, APP_START_TIME + " DESC");
        if (c.moveToFirst() == true) {
            scheduledElement = new ScheduledElement(
                    c.getString(c.getColumnIndex(TYPE)),
                    c.getString(c.getColumnIndex(APP_NAME)),
                    c.getString(c.getColumnIndex(APP_PACKAGE)),
                    c.getString(c.getColumnIndex(FILE_NAME)),
                    c.getShort(c.getColumnIndex(APP_START_TIME)));
        }
        return scheduledElement;
    }

    public void addApp(String appName, String appPackage, String time) throws Exception {
        addApp(appName, appPackage, timeStringToMillis(time));
    }

    public void addApp(String appName, String appPackage, long time) throws Exception {
        ContentValues cv = new ContentValues();
        cv.put(TYPE, TYPE_APP);
        cv.put(APP_NAME, appName);
        cv.put(APP_PACKAGE, appPackage);
        cv.put(APP_START_TIME, time);
        if (db.insert(SCHEDULE_TABLE, null, cv) == -1) {
            throw new SQLException("Не удаётся добавить приложение в расписание...");
        }
        updateScheduleList();
    }

    public void addFile(String fileName, String time) throws ParseException {
        addFile(fileName, timeStringToMillis(time));
    }

    public void addFile(String fileName, long time) {
        ContentValues cv = new ContentValues();
        cv.put(TYPE, TYPE_FILE);
        cv.put(FILE_NAME, fileName);
        cv.put(APP_START_TIME, time);
        if (db.insert(SCHEDULE_TABLE, null, cv) == -1) {
            throw new SQLException("Не удалось добавить файл в расписание...");
        }

    }

    public void updateApp(String id, String appName, String appPackage, String time) throws SQLException, ParseException {
        ContentValues cv = new ContentValues();
        cv.put(APP_ID, id);
        cv.put(APP_NAME, appName);
        cv.put(APP_PACKAGE, appPackage);
        cv.put(FILE_NAME, "");
        cv.put(APP_START_TIME, timeStringToMillis(time));
        int updRowsCount = db.update(SCHEDULE_TABLE, cv, APP_ID + " = ?", new String[] {id});
        if (updRowsCount == 0) {
            throw new SQLException("Ошибка обновления данных. Пакет: " + appPackage);
        }
    }

    public void updateFile(String id, String fileName, String time) throws SQLException, ParseException {
        ContentValues cv = new ContentValues();
        cv.put(APP_ID, id);
        cv.put(APP_NAME, "");
        cv.put(APP_PACKAGE, "");
        cv.put(FILE_NAME, fileName);
        cv.put(APP_START_TIME, timeStringToMillis(time));
        int updRowsCount = db.update(SCHEDULE_TABLE, cv, APP_ID + " = ?", new String[] {id});
        if (updRowsCount == 0) {
            throw new SQLException("Ошибка обновления данных. Файл: " + fileName);
        }
    }

}
