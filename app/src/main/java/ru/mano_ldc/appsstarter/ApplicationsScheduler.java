package ru.mano_ldc.appsstarter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.format.DateFormat;
import android.util.ArrayMap;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by valera on 21.11.2017.
 */

public final class ApplicationsScheduler {

    public static final String[] columns = {
            SQLiteOpener.APP_ID,
            SQLiteOpener.APP_NAME_COL,
            SQLiteOpener.APP_PACKAGE_COL,
            SQLiteOpener.APP_START_TIME};

    private ArrayList<HashMap<String, String>> scheduledAppsList = new ArrayList<>();
    private SQLiteDatabase db;
    private Context context;

    ApplicationsScheduler(Context context) throws SQLException {
        this.context = context;
        db = new SQLiteOpener(context).getWritableDatabase();
        if (db == null) {
            throw new SQLException("Не удаётся подключиться к локальной базе данных...");
        }
        updateAppsList();
    }

    @Override
    protected void finalize() throws Throwable {
        db.close();
    }

    private void updateAppsList() {
        scheduledAppsList.clear();
        Cursor c = db.query(SQLiteOpener.APPS_TABLE, columns, null, null, null, null, SQLiteOpener.APP_START_TIME);

        if (c.getCount() != 0) {
            while (c.moveToNext() == true) {
                HashMap<String, String> map = new HashMap<>();
                for (String curCol : columns) {
                    map.put(curCol, c.getString(c.getColumnIndex(curCol)));
                }
                scheduledAppsList.add(map);
            }
        }
    }

    public String[] getColumnsForAdapter() {
        return columns;
    }

    public ArrayList<HashMap<String, String>> getApps() {
        return scheduledAppsList;
    }

    public HashMap<String, String> getAppByID(String id) throws SQLException {
        HashMap<String, String> result = new HashMap<>();
        String selection = SQLiteOpener.APP_ID + " = ?";
        String[] selectionArgs = new String[] {id};
        Cursor c = db.query(SQLiteOpener.APPS_TABLE, columns, selection, selectionArgs, null, null, null);
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

    public ScheduledApp getAppByTime(long time) {
        ScheduledApp app = null;
        String timeString = DateFormat.format("HH:mm", time).toString();
        String selection = SQLiteOpener.APP_START_TIME + " = ?";
        String[] selectionArgs = new String[] {timeString};
        String[] selectionCols = new String[] {
                SQLiteOpener.APP_NAME_COL,
                SQLiteOpener.APP_PACKAGE_COL,
                SQLiteOpener.APP_START_TIME};
        Cursor c = db.query(SQLiteOpener.APPS_TABLE, selectionCols, selection, selectionArgs,null, null, null);
        if (c.moveToFirst() == true) {
            app = new ScheduledApp(
                    c.getString(c.getColumnIndex(SQLiteOpener.APP_NAME_COL)),
                    c.getString(c.getColumnIndex(SQLiteOpener.APP_PACKAGE_COL)),
                    c.getShort(c.getColumnIndex(SQLiteOpener.APP_START_TIME)));
        }
        return app;
    }

    public void addApp(String appName, String appPackage, String time) throws Exception {
        ContentValues cv = new ContentValues();
        cv.put(SQLiteOpener.APP_NAME_COL, appName);
        cv.put(SQLiteOpener.APP_PACKAGE_COL, appPackage);
        cv.put(SQLiteOpener.APP_START_TIME, time);
        if (db.insert(SQLiteOpener.APPS_TABLE, null, cv) == -1) {
            throw new SQLException("Не удаётся добавить приложение в расписание...");
        }
        updateAppsList();
    }

    public void updateApp(String id, String appName, String appPackage, String time) throws SQLException {
        ContentValues cv = new ContentValues();
        cv.put(SQLiteOpener.APP_ID, id);
        cv.put(SQLiteOpener.APP_NAME_COL, appName);
        cv.put(SQLiteOpener.APP_PACKAGE_COL, appPackage);
        cv.put(SQLiteOpener.APP_START_TIME, time);
        int updRowsCount = db.update(SQLiteOpener.APPS_TABLE, cv, SQLiteOpener.APP_ID + " = ?", new String[] {id});
        if (updRowsCount == 0) {
            throw new SQLException("Ошибка обновления данных. Пакет: " + appPackage);
        }
    }

}
