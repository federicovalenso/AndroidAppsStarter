package ru.mano_ldc.appsstarter;

/**
 * Created by valera on 08.12.2017.
 */

public final class ScheduledApp extends App {
    private long startTime;

    public ScheduledApp(String appName, String appPackage, long startTime) {
        super(appName, appPackage);
        this.startTime = startTime;
    }
}
