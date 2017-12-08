package ru.mano_ldc.appsstarter;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Created by valera on 20.11.2017.
 */


public final class InstalledApps {

    public static final String APP_NAME_KEY = "AppName";
    public static final String APP_PACKAGE_KEY = "Package";
    private List<ApplicationInfo> appsList;
    private PackageManager pm;

    InstalledApps(Context context) {
        pm = context.getPackageManager();
        List<ApplicationInfo> allAppsList = pm.getInstalledApplications(0);
        appsList = filterAppsList(allAppsList);
    }

    public ArrayList<HashMap<String, String>> convertToArrayList() {
        ArrayList<HashMap<String, String>> resultList = new ArrayList<>();
        int size = appsList.size();

        for (int i = 0; i < size; i++) {
            HashMap<String, String> map = new HashMap<>();
            ApplicationInfo curApp = appsList.get(i);
            map.put(APP_NAME_KEY, pm.getApplicationLabel(curApp).toString());
            map.put(APP_PACKAGE_KEY, curApp.packageName);
            resultList.add(map);
        }
        Collections.sort(resultList, new Comparator<HashMap<String, String>>() {
            @Override
            public int compare(HashMap<String, String> hm1, HashMap<String, String> hm2) {
                return hm1.get("AppName").compareTo(hm2.get("AppName"));
            }
        });
        return resultList;
    }

    private ArrayList<ApplicationInfo> filterAppsList(List<ApplicationInfo> list) {
        ArrayList<ApplicationInfo> resultList = new ArrayList<>();
        for (ApplicationInfo app : list) {
            if ((app.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                resultList.add(app);
            }
        }
        return resultList;
    }
}
