package io.nullbuilt.custombatterynotifications;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.IBinder;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.List;


public class NotificationService extends Service {
    private static final String TAG = "NotificationService";
    private static int PERCENTAGE_NULL = -1;
    private static int STATUS_NULL = -13;
    public static BroadcastReceiver batteryInfoReceiver;
    private static AlarmManager alarm = null;
    private static boolean receiverSet;
    private static int currentPercentage;
    private static int lastCheckedPercentage;
    private static int currentStatus;
    private static int lastCheckedStatus;
    private static List<CustomBatteryNotification> notificationsList;


    public NotificationService() {
        super();
//        Log.d(TAG, "NotificationService: constructor");
        currentPercentage = PERCENTAGE_NULL;     // Random negative numbers for initial value.
        lastCheckedPercentage = PERCENTAGE_NULL;
        currentStatus = STATUS_NULL;
        lastCheckedStatus = STATUS_NULL;
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        Log.d(TAG, "onCreate");

        if(!isAlarmUp()) {
            scheduleAlarm();
//            Log.d(TAG, "onCreate: isAlarmUp = false");
        }
//        else
//            Log.d(TAG, "onCreate: isAlarmUp = true");

        receiverSet = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            String trigger = intent.getStringExtra("trigger");
//            if (trigger != null)
//                Log.d(TAG, "onStartCommand: trigger = " + trigger);
//            else
//                Log.d(TAG, "onStartCommand: trigger = null");
        }


        if (!receiverSet) {
            batteryInfoReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    // Store current values in local variables
                    currentPercentage = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, PERCENTAGE_NULL);
                    currentStatus = intent.getIntExtra(BatteryManager.EXTRA_STATUS, STATUS_NULL);
//                    Log.d(TAG, "onReceive: currentPercentage = " + currentPercentage
//                            + "%, currentStatus = " + currentStatus);

                    CustomBatteryNotification notification = checkForNotification();
                    if (notification != null) {
//                        Log.d(TAG, "onReceive: notification percentage - status: "
//                                + notification.getPercentage() + " - " + notification.getBatteryStatus());
                        createNotification(notification);
                    }
//                    else
//                        Log.d(TAG, "onReceive: notification is null in Receiver");
                }
            };
            registerReceiver(batteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            receiverSet = true;
        }

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {

//        Log.d(TAG, "onBind");

        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
//        Log.d(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        Log.d(TAG, "onDestroy");
    }

    private void scheduleAlarm() {
//        Log.d(TAG, "scheduleAlarm");

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        // Construct an intent that will execute the AlarmReceiver
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);

        // Create a PendingIntent to be triggered when the alarm goes off
        final PendingIntent pIntent = PendingIntent.getBroadcast(getApplicationContext(), AlarmReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarm = (AlarmManager) getApplicationContext().getSystemService(getApplicationContext().ALARM_SERVICE);

        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                60 * 1000, pIntent);

    }

    private boolean isAlarmUp() {
        boolean alarmUp = (PendingIntent.getBroadcast(getApplicationContext(), AlarmReceiver.REQUEST_CODE,
                new Intent(getApplicationContext(), AlarmReceiver.class),
                PendingIntent.FLAG_NO_CREATE) != null);

        return alarmUp;
    }

    private CustomBatteryNotification checkForNotification() {

        // Return false if the current battery values have not been retrieved yet
//        if (currentPercentage == PERCENTAGE_NULL || currentStatus == STATUS_NULL) {
//            return null;
//        }

        // Get the Shared Preferences file for writing.
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Get battery values for last time checked
        lastCheckedPercentage = sharedPreferences.getInt("lastCheckedPercentage", PERCENTAGE_NULL);
        lastCheckedStatus = sharedPreferences.getInt("lastCheckedStatus", STATUS_NULL);

        // Write current battery values to Shared Preferences
        editor.putInt("lastCheckedPercentage", currentPercentage);
        editor.putInt("lastCheckedStatus", currentStatus);
        editor.apply();

        if (currentPercentage == lastCheckedPercentage && currentStatus == lastCheckedStatus) {
//            Log.d(TAG, "checkForNotification: currentPercentage == lastCheckedPercentage && currentStatus == lastCheckedStatus");
            return null;
        }
        else {

            int minPercentage, maxPercentage;

            if (currentPercentage < lastCheckedPercentage) {
                minPercentage = currentPercentage;
                maxPercentage = --lastCheckedPercentage;
            }
            else if (currentPercentage > lastCheckedPercentage) {
                minPercentage = ++lastCheckedPercentage;
                maxPercentage = currentPercentage;
            }
            else // currentPercentage == lastCheckedPercentage, but currentStatus != lastCheckedStatus
            {
                minPercentage = currentPercentage;
                maxPercentage = currentPercentage;
            }
//            Log.d(TAG, "currentPercentage = " + currentPercentage + ", lastCheckedPercentage = " + lastCheckedPercentage);
//            Log.d(TAG, "minPercentage = " + minPercentage + ", maxPercentage = " + maxPercentage);

            Gson gson = new GsonBuilder().registerTypeAdapter(Uri.class, new Utility.UriSerializeDeserialize())
                    .create();
            String json = sharedPreferences.getString("notificationsJson", "");
            // Convert from JSON string to list of CustomBatteryNotification objects
            Type type = new TypeToken<List<CustomBatteryNotification>>(){}.getType();
            notificationsList = gson.fromJson(json, type);

            // Check every object on the list for percentage and
            // status conditions that match the current ones.
            if (notificationsList != null) {
                for (CustomBatteryNotification n : notificationsList) {
//                    Log.d(TAG, "checkForNotification: " + n.getPercentage() + ", " + n.getBatteryStatus() + ", " + n.getActive());
                    boolean sameStatus;
                    if ((currentStatus == BatteryManager.BATTERY_STATUS_CHARGING
                            && n.getBatteryStatus().equals(BatteryStatus.CHARGING)) ||
                            ((currentStatus == BatteryManager.BATTERY_STATUS_DISCHARGING
                                    || currentStatus == BatteryManager.BATTERY_STATUS_NOT_CHARGING)
                                    && n.getBatteryStatus().equals(BatteryStatus.DISCHARGING)) ||
                            n.getPercentage() == 100) {
//                        Log.d(TAG, "checkForNotification: Setting sameStatus to true");
                        sameStatus = true;
                    }
                    else
                        sameStatus = false;

                    if (n.getPercentage() >= minPercentage
                            && n.getPercentage() <= maxPercentage
                            && sameStatus
                            && n.getActive()) {
//                        Log.d(TAG, "checkForNotification: found notification object");
                        return n;
                    }
                }
            }
//            else
//                Log.d(TAG, "checkForNotification: notificationsList is NULL");
        }
        return null;
    }

    public void createNotification(CustomBatteryNotification notification) {
//        Log.d(TAG, "createNotification: " + notification.getPercentage() + ", "
//                + notification.getBatteryStatus() + ", " + notification.getActive());

        String notificationTitle = "Battery Alarm";
        String status = "";
        switch (notification.getBatteryStatus()) {
            case CHARGING:
                status = "charging";
                break;
            case DISCHARGING:
                status = "discharging";
            default:
                break;
        }

        String notificationText = "";
        if (notification.getPercentage() < 100)
            notificationText = "Battery at " + notification.getPercentage() + "% - " + status + ".";
        else if (notification.getPercentage() == 100)
            notificationText = "Battery full.";

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(notificationTitle)
                        .setContentText(notificationText);

        // Create an intent to start the MainActivity
        Intent mainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);


        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        // Add the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Add the Intent that starts MainActivity to the top of the stack
        stackBuilder.addNextIntent(mainActivityIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setLights(Color.BLUE, 500, 500);
        mBuilder.setAutoCancel(true);

        // TODO set volume, vibration, ringtone
        //mBuilder.setSound(notification.getRingtoneUri());
        playRingtone(notification.getRingtoneUri(), notification.getVolume());
        if (notification.getVibrate()) {
            Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
            long[] pattern = {0, 100, 100, 80};
            vibrator.vibrate(pattern, -1);
        }

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(notification.getPercentage(), mBuilder.build());
    }

    private void playRingtone(Uri ringtoneUri, int volume) {

        float volumeFloat = (float) volume * (0.25f);
        try {
            final MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setVolume(volumeFloat, volumeFloat);

            if(ringtoneUri != null) {
//                Log.d(TAG, "playRingtone: ringtoneUri = " + ringtoneUri);

                mediaPlayer.setDataSource(getApplicationContext(), ringtoneUri);
                mediaPlayer.prepare();
                mediaPlayer.start();
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        mediaPlayer.release();
                    }
                });
            }
            else {
//                Log.d(TAG, "playRingtone: ringtoneUri is null");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
