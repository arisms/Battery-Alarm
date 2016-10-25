package io.nullbuilt.custombatterynotifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver";
    public static final int REQUEST_CODE = 12345;

    // Triggered by the Alarm periodically (starts the service to run task)
    @Override
    public void onReceive(Context context, Intent intent) {
//        Log.d(TAG, "onReceive");
        Intent i = new Intent(context, NotificationService.class);
        i.putExtra("trigger", "Alarm");
        context.startService(i);
    }
}