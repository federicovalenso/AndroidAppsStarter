package ru.mano_ldc.appsstarter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class Receiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent serviceStartIntent = new Intent(context, AppsStarterService.class);
            serviceStartIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startService(serviceStartIntent);
        }
    }
}
