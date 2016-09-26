package io.nullbuilt.custombatterynotifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Aris on 26/09/16.
 */

public class TickReceiver extends BroadcastReceiver {
    private static final String TAG = "TickReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");
    }
}
