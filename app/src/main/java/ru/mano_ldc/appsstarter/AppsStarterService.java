package ru.mano_ldc.appsstarter;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import java.io.File;
import java.text.ParseException;
import java.util.Timer;
import java.util.TimerTask;

public class AppsStarterService extends Service {

    final String JPG_TYPE = "jpg";
    final String AVI_TYPE = "avi";
    final String MKV_TYPE = "mkv";
    ScheduledElement curScheduledElement;
    Notification notification;
    ApplicationsScheduler scheduler;
    Timer appsStarterTimer;
    TimerTask taskForAppsStarterTimer;
    final long timerInterval = 1000;

    /**
     * Called by the system when the service is first created.  Do not call this method directly.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        curScheduledElement = new ScheduledElement();
        Notification.Builder notifBuilder = new Notification.Builder(this);
        notifBuilder.setAutoCancel(false);
        notifBuilder.setTicker("Сервис запуска приложений");
        notifBuilder.setContentTitle("Сервис запуска приложений");
        notifBuilder.setContentText("Сервис запущен");
        notifBuilder.setSmallIcon(R.mipmap.ic_launcher);
        notifBuilder.setOngoing(true);
        notification = notifBuilder.getNotification();
        scheduler = new ApplicationsScheduler(this);
    }

    /**
     * Called by the system every time a client explicitly starts the service by calling
     * {@link Context#startService}, providing the arguments it supplied and a
     * unique integer token representing the start request.  Do not call this method directly.
     * <p>
     * <p>For backwards compatibility, the default implementation calls
     * {@link #onStart} and returns either {@link #START_STICKY}
     * or {@link #START_STICKY_COMPATIBILITY}.
     * <p>
     * <p>If you need your application to run on platform versions prior to API
     * level 5, you can use the following model to handle the older {@link #onStart}
     * callback in that case.  The <code>handleCommand</code> method is implemented by
     * you as appropriate:
     * <p>
     * {@sample development/samples/ApiDemos/src/com/example/android/apis/app/ForegroundService.java
     * start_compatibility}
     * <p>
     * <p class="caution">Note that the system calls this on your
     * service's main thread.  A service's main thread is the same
     * thread where UI operations take place for Activities running in the
     * same process.  You should always avoid stalling the main
     * thread's event loop.  When doing long-running operations,
     * network calls, or heavy disk I/O, you should kick off a new
     * thread, or use {@link AsyncTask}.</p>
     *
     * @param intent  The Intent supplied to {@link Context#startService},
     *                as given.  This may be null if the service is being restarted after
     *                its process has gone away, and it had previously returned anything
     *                except {@link #START_STICKY_COMPATIBILITY}.
     * @param flags   Additional data about this start request.
     * @param startId A unique integer representing this specific request to
     *                start.  Use with {@link #stopSelfResult(int)}.
     * @return The return value indicates what semantics the system should
     * use for the service's current started state.  It may be one of the
     * constants associated with the {@link #START_CONTINUATION_MASK} bits.
     * @see #stopSelfResult(int)
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(1, notification);
        startAppsByTimer();
        return super.onStartCommand(intent, flags, startId);
    }

    private void startAppsByTimer() {
        appsStarterTimer = new Timer();
        if (taskForAppsStarterTimer != null) {
            taskForAppsStarterTimer.cancel();
        }
        taskForAppsStarterTimer = new TimerTask() {
            @Override
            public void run() {
                if (curScheduledElement.getType().isEmpty() == false) {
                    if ((System.currentTimeMillis() / 1000) % 60 != 0) {
                        return;
                    }
                }
                ScheduledElement scheduledElement = null;
                try {
                    scheduledElement = scheduler.getScheduledElementByTime(System.currentTimeMillis());
                }
                catch (ParseException e) {
                    Toast.makeText(getApplicationContext(), e.getMessage().toString(), Toast.LENGTH_LONG).show();
                }
                if (scheduledElement == null) {
                    return;
                }
                String type = scheduledElement.getType();
                switch (type) {
                    case ApplicationsScheduler.TYPE_APP : {
                        String curPackage = curScheduledElement.getPackage();
                        if (curPackage != null) {
                            if (curPackage.equals(scheduledElement.getPackage())) {
                                FileAndLogUtils.appendLog(ApplicationsScheduler.millisToTimeString(System.currentTimeMillis()) + ": текущее приложение уже запущено: " + scheduledElement.getPackage());
                                break;
                            }
                        }
                        Intent intent = getPackageManager().getLaunchIntentForPackage(scheduledElement.getPackage());
                        if (intent == null) {
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                                intent = getPackageManager().getLeanbackLaunchIntentForPackage(scheduledElement.getPackage());
                            }
                        }
                        if (intent == null) {
                            FileAndLogUtils.appendLog(ApplicationsScheduler.millisToTimeString(System.currentTimeMillis()) + ": Не удаётся запустить приложение");
                            Toast.makeText(getApplicationContext(), "Не удаётся запустить приложение", Toast.LENGTH_LONG).show();
                        }
                        else {
                            FileAndLogUtils.appendLog(ApplicationsScheduler.millisToTimeString(System.currentTimeMillis()) + ": запущено приложение: " + scheduledElement.getPackage());
                            startActivity(intent);
                        }
                        break;
                    }
                    case ApplicationsScheduler.TYPE_FILE : {
                        String curFileName = curScheduledElement.getFileName();
                        if (curFileName != null) {
                            if (curFileName.equals(scheduledElement.getFileName())) {
                                FileAndLogUtils.appendLog(ApplicationsScheduler.millisToTimeString(System.currentTimeMillis()) + ": текущий файл уже запущен " + scheduledElement.getFileName());
                                break;
                            }
                        }
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        String fileName = scheduledElement.getFileName();
                        File file = new File(fileName);
                        String Extension = fileName.substring(fileName.lastIndexOf('.')+1);
                        Uri uri = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider",file);
                        switch (Extension) {
                            case JPG_TYPE :
                                intent.setDataAndType(uri, "image/*");
                                break;
                            case AVI_TYPE :
                            case MKV_TYPE :
                                intent.setDataAndType(uri, "video/*");
                                break;
                        }
                        try {
                            startActivity(intent);
                        }
                        catch (Exception e) {
                            FileAndLogUtils.appendLog(ApplicationsScheduler.millisToTimeString(System.currentTimeMillis()) +
                                    ": не удалось открыть файл: " + scheduledElement.getFileName() + ", ошибка: " + e.getMessage());
                        }
                        FileAndLogUtils.appendLog(ApplicationsScheduler.millisToTimeString(System.currentTimeMillis()) + ": открыт файл: " + scheduledElement.getFileName());
                        break;
                    }
                }
                curScheduledElement = scheduledElement;
            }
        };
        appsStarterTimer.schedule(taskForAppsStarterTimer, 0, timerInterval);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
