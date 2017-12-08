package ru.mano_ldc.appsstarter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by valera on 21.11.2017.
 */

public final class SQLiteOpener extends SQLiteOpenHelper {

    public static final String APP_ID = "id";
    public static final String APP_NAME_COL = "name";
    public static final String APP_PACKAGE_COL = "package";
    public static final String APP_START_TIME = "startTime";
    public static String APPS_TABLE = "applications";

    public SQLiteOpener(Context context) {
        super(context, "AppsScheduler", null, 1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("" +
                "CREATE TABLE " + APPS_TABLE + " (" +
                APP_ID + " INTEGER PRIMARY KEY autoincrement," +
                APP_NAME_COL + " TEXT," +
                APP_PACKAGE_COL + " TEXT," +
                APP_START_TIME + " TEXT);");
    }
}
