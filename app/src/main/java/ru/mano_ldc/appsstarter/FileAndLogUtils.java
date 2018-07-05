package ru.mano_ldc.appsstarter;

import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

/**
 * Created by valera on 05.07.2018.
 */

public class FileAndLogUtils {

    static public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if ( Environment.MEDIA_MOUNTED.equals( state ) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals( state ) ) {
            return true;
        }
        return false;
    }

    static public void appendLog(String text)
    {
        if (isExternalStorageReadable() == false) {
            return;
        }
        File logFile = new File(Environment.getExternalStorageDirectory().toString() + "/appsStarter.log");
        if (!logFile.exists())
        {
            try
            {
                logFile.createNewFile();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        try
        {
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
