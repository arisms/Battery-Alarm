package io.nullbuilt.custombatterynotifications;

import android.net.Uri;

/**
 * Class for the custom battery notifications created by the user.
 */
public class CustomBatteryNotification implements Comparable<CustomBatteryNotification> {

    private int percentage;
    private BatteryStatus batteryStatus;
    private Uri ringtoneUri;
    private int volume;
    private boolean vibrate;
    private boolean active;
    private int id;

    public CustomBatteryNotification(int percentage, BatteryStatus batteryStatus,
                                     Uri ringtoneUri, int volume, boolean vibrate,
                                     boolean active, int id) {
        this.percentage = percentage;
        this.batteryStatus = batteryStatus;
        this.ringtoneUri = ringtoneUri;
        this.volume = volume;
        this.vibrate = vibrate;
        this.active = active;
        this.id = id;
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
    public boolean getActive() {
        return this.active;
    }
    public int getId() {
        return this.id;
    }

    public void modify(int percentage, BatteryStatus batteryStatus,
                       Uri ringtoneUri, int volume, boolean vibrate, boolean active) {
        this.percentage = percentage;
        this.batteryStatus = batteryStatus;
        this.ringtoneUri = ringtoneUri;
        this.volume = volume;
        this.vibrate = vibrate;
        this.active = active;
    }

    @Override
    public int compareTo(CustomBatteryNotification other) {

        // If the current notification is active and the other is inactive
        if (this.getActive() && !other.getActive())
            return -1;
        else if (!this.getActive() && other.getActive())
            return 1;
        // If both notifications have the same active state
        else {
            // Compare by battery charging status
            if (this.getBatteryStatus().equals(BatteryStatus.DISCHARGING)
                    && other.getBatteryStatus().equals(BatteryStatus.CHARGING))
                return -1;
            else if (this.getBatteryStatus().equals(BatteryStatus.CHARGING)
                    && other.getBatteryStatus().equals(BatteryStatus.DISCHARGING))
                return 1;
            // If both notifications have the same active and charging state
            else {
                // Compare by battery percentage
                if (this.getPercentage() > other.getPercentage())
                    return -1;
                else if (this.getPercentage() < other.getPercentage())
                    return 1;
                else
                    return 0;
            }
        }
    }
}