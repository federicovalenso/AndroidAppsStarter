package ru.mano_ldc.appsstarter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by valera on 21.11.2017.
 */

public final class SQLiteOpener extends SQLiteOpenHelper {

    public SQLiteOpener(Context context) {
        super(context, "AppsScheduler", null, 1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("" +
                "CREATE TABLE " + ApplicationsScheduler.SCHEDULE_TABLE + " (" +
                ApplicationsScheduler.APP_ID + " INTEGER PRIMARY KEY autoincrement," +
                ApplicationsScheduler.TYPE + " TEXT," +
                ApplicationsScheduler.FILE_NAME + " TEXT," +
                ApplicationsScheduler.APP_NAME + " TEXT," +
                ApplicationsScheduler.APP_PACKAGE + " TEXT," +
                ApplicationsScheduler.APP_START_TIME + " INTEGER);");
    }
}
