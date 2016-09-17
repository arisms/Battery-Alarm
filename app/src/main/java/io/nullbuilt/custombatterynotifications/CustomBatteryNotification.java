package io.nullbuilt.custombatterynotifications;

import android.net.Uri;

/**
 * Class for the custom battery notifications created by the user.
 */
public class CustomBatteryNotification {

    private int percentage;
    private BatteryStatus batteryStatus;
    private Uri ringtoneUri;
    private int volume;
    private boolean vibrate;

    public CustomBatteryNotification(int percentage, BatteryStatus batteryStatus,
                                     Uri ringtoneUri, int volume, boolean vibrate) {
        this.percentage = percentage;
        this.batteryStatus = batteryStatus;
        this.ringtoneUri = ringtoneUri;
        this.volume = volume;
        this.vibrate = vibrate;
    }

    public int getPercentage() {
        return this.percentage;
    }
    public BatteryStatus getBatteryStatus() {
        return this.batteryStatus;
    }
    public Uri getRingtoneUri() {
        return this.ringtoneUri;
    }
    public int getVolume() {
        return this.volume;
    }
    public boolean getVibrate() {
        return this.vibrate;
    }

}