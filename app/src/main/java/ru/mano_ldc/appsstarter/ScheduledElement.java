package ru.mano_ldc.appsstarter;

import android.support.annotation.NonNull;

/**
 * Created by valera on 08.12.2017.
 */
public final class ScheduledElement {

    private String type = "";
    private String appName = "";
    private String appPackage = "";
    private String fileName = "";
    private long startTime;

    public synchronized String getType() {
        return type;
    }

    public synchronized String getFileName() {
        return fileName;
    }

    public synchronized String getPackage() {
        return appPackage;
    }

    public ScheduledElement() {}

    public ScheduledElement(@NonNull String type, String appName, String appPackage, String fileName, @NonNull long startTime) {
        this.type = type;
        if (appName != null && appPackage != null) {
            this.appName = appName;
            this.appPackage = appPackage;
        }
        if (fileName != null) {
            this.fileName = fileName;
        }
        this.startTime = startTime;
    }
}
