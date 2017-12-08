package ru.mano_ldc.appsstarter;

/**
 * Created by valera on 08.12.2017.
 */

public class App {

    private String appName;
    private String appPackage;

    public App(String appName, String appPackage) {
        this.appName = appName;
        this.appPackage = appPackage;
    }

    public String getName() {
        return appName;
    }

    public String getPackage() {
        return appPackage;
    }
}