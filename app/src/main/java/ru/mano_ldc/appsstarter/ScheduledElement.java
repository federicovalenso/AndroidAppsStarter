package ru.mano_ldc.appsstarter;

/**
 * Created by valera on 08.12.2017.
 */

public final class ScheduledElement {

    private String type = "";
    private String appName = "";
    private String appPackage = "";
    private String fileName = "";
    private long startTime = 0;

    public String getType() {
        return type;
    }

    public String getFileName() {
        return fileName;
    }

    public String getPackage() {
        return appPackage;
    }

    public ScheduledElement() {}

    public ScheduledElement(String type, String appName, String appPackage, String fileName, long startTime) {
        this.type = type;
        this.appName = appName;
        this.appPackage = appPackage;
        this.fileName = fileName;
        this.startTime = startTime;
    }
}
