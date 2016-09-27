package io.nullbuilt.custombatterynotifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


/**
 * Receives broadcast for system boot and schedules the alarm
 * which wakes the service that checks for notifications
 */
public class BootBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "BootBroadcastReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG, "onReceive");
        Intent serviceIntent = new Intent(context, NotificationService.class);
        serviceIntent.putExtra("trigger", "BootBroadcastReceiver");
        context.startService(serviceIntent);
    }
}
