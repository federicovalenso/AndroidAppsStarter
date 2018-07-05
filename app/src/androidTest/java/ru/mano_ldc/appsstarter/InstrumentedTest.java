package ru.mano_ldc.appsstarter;

import android.content.Context;
import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class InstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("ru.mano_ldc.appsstarter", appContext.getPackageName());
    }

    @Test
    public  void testStoragePermision() throws Exception {
        assertEquals(true, FileAndLogUtils.isExternalStorageReadable());
    }

    @Test
    public void testReadingDirectory() throws Exception {
        File file = new File(Environment.getExternalStorageDirectory().toString());
        boolean result = file.listFiles().length > 0;
        assertEquals(true, result);
    }
}