package io.nullbuilt.custombatterynotifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    private MainActivity mainActivity;
    private static NotificationsAdapter notificationsAdapter;
    private static List<NotificationsListItem> notificationsListWithHeaders;
    private static TextView mainText;
    public static BroadcastReceiver batteryInfoReceiver;
    List<CustomBatteryNotification> notifications;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        Log.d(TAG, "onCreateView: HomeFragment");
        mainActivity = (MainActivity) getActivity();

        // Create list of items for the RecyclerView
        notificationsListWithHeaders = new ArrayList<NotificationsListItem>();

        // Retrieve the list of CustomBatterNotification
        // objects from the SharedPreferences file.
        notifications = getListOfCustomNotifications(mainActivity);
        if (notifications == null)
            notifications = new ArrayList<CustomBatteryNotification>();
        else {
            // Sort the list of CustomBatteryNotification objects
            Collections.sort(notifications);
        }

        // If the list of notification objects contains at least one notification set as active
        if(Utility.notificationsListContainsActive(notifications)) {
            // Create a header in the notificationsListWithHeaders for active notifications
            notificationsListWithHeaders.add(new NotificationsListItem(true, "Active", null));

            // Add all active objects
            for (CustomBatteryNotification n : notifications) {
                if(n.getActive())
                    notificationsListWithHeaders.add(new NotificationsListItem(false, "", n));

                // Since the list is sorted, all active objects are first
                if(!n.getActive())
                    break;
            }
        }
        // If the list of notification objects contains at least one notification set as INactive
        if(Utility.notificationsListContainsInactive(notifications)) {
            // Create a header in the notificationsListWithHeaders for INactive notifications
            notificationsListWithHeaders.add(new NotificationsListItem(true, "Deactivated", null));

            // Add all active objects
            for (CustomBatteryNotification n : notifications) {
                if(!n.getActive())
                    notificationsListWithHeaders.add(new NotificationsListItem(false, "", n));
            }
        }

        // Create the RecyclerView and set the adapter
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.notifications_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        notificationsAdapter = new NotificationsAdapter(this, notificationsListWithHeaders);
        recyclerView.setAdapter(notificationsAdapter);

        setHeader(rootView);

        mainText = (TextView) rootView.findViewById(R.id.text_main);
        if(notificationsListWithHeaders.size() > 0) {
            mainText.setVisibility(View.GONE);
        }
        else {
            mainText.setVisibility(View.VISIBLE);
        }

        return rootView;
    }

    private List<CustomBatteryNotification> getListOfCustomNotifications(Context context) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new GsonBuilder().registerTypeAdapter(Uri.class, new Utility.UriSerializeDeserialize()).create();

        List<CustomBatteryNotification> notificationsList = new ArrayList<CustomBatteryNotification>();
        String json = sharedPreferences.getString("notificationsJson", "");
        Log.d(TAG, "getListOfCustomNotifications: notificationsListWithHeaders JSON string from SP: " + json);

        // Convert from JSON string to list of CustomBatteryNotification objects
        Type type = new TypeToken<List<CustomBatteryNotification>>(){}.getType();
        notificationsList = gson.fromJson(json, type);
        if(notificationsList != null) {
            Log.d(TAG, "getListOfCustomNotifications: notificationsListWithHeaders.size = " + notificationsList.size());
            for (CustomBatteryNotification c : notificationsList) {
                Log.d(TAG, "getListOfCustomNotifications: notificationsListWithHeaders - " + c.toString());
            }
        }
        else
            Log.d(TAG, "getListOfCustomNotifications: notificationsListWithHeaders NULL");

        return notificationsList;
    }

    public void updateNotificationsList(List<NotificationsListItem> notificationsListWithHeaders) {

        // Copy all CustomBatteryNotification objects from
        // notificationsListWithHeaders to a new list
        List<CustomBatteryNotification> updatedNotificationsList = new ArrayList<CustomBatteryNotification>();
        for (NotificationsListItem n : notificationsListWithHeaders) {
            if(!n.isHeader)
                updatedNotificationsList.add(n.customBatteryNotification);
        }

        // Get the Shared Preferences file for writing.
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new GsonBuilder().registerTypeAdapter(Uri.class, new Utility.UriSerializeDeserialize()).create();

        // Convert the list of objects to a JSON string and then
        // write the updated list back to the SharedPreferences
        String json = gson.toJson(updatedNotificationsList);
        editor.putString("notificationsJson", json);
        editor.apply();

//        notificationsAdapter.swap(notificationsListWithHeaders);
        reload();
    }

    /**
     * Set the Header showing current battery percentage and charging status
     */
    public void setHeader(View view) {

        final TextView percentageText = (TextView) view.findViewById(R.id.text_header_percentage);
        final TextView statusText = (TextView) view.findViewById(R.id.text_header_status);

        batteryInfoReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int percentage = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                Log.d(TAG, "onReceive: level = " + percentage);
                int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -13);
                Log.d(TAG, "onReceive: status = " + status);


                percentageText.setText(Integer.toString(percentage) + "%");

                if(status == BatteryManager.BATTERY_STATUS_CHARGING)
                    statusText.setText(getString(R.string.status_charging));
                else if(status == BatteryManager.BATTERY_STATUS_DISCHARGING || status == BatteryManager.BATTERY_STATUS_NOT_CHARGING)
                    statusText.setText(getString(R.string.status_discharging));
                else if(status == BatteryManager.BATTERY_STATUS_FULL)
                    statusText.setText(getString(R.string.status_full));

                if(percentage < 20)
                    percentageText.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
                else if(percentage > 80)
                    percentageText.setTextColor(ContextCompat.getColor(getActivity(), R.color.green));
                else
                    percentageText.setTextColor(ContextCompat.getColor(getActivity(), R.color.blue));
            }
        };

        getActivity().registerReceiver(batteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    public void activeStateChanged(boolean active) {
        if(active)
            Toast.makeText(getActivity(), getString(R.string.toast_notification_activated), Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(getActivity(), getString(R.string.toast_notification_deactivated), Toast.LENGTH_SHORT).show();
    }

    public void notificationDeleted() {
        Toast.makeText(getActivity(), getActivity().getString(R.string.notification_deleted), Toast.LENGTH_SHORT)
                .show();
    }

    public void reload() {
        Log.d(TAG, "reload()");

        notificationsListWithHeaders.clear();

        // Retrieve the list of CustomBatterNotification
        // objects from the SharedPreferences file.
        notifications.clear();
        notifications = getListOfCustomNotifications(mainActivity);
        if (notifications == null)
            notifications = new ArrayList<CustomBatteryNotification>();
        else {
            // Sort the list of CustomBatteryNotification objects
            Collections.sort(notifications);
        }

        // If the list of notification objects contains at least one notification set as active
        if(Utility.notificationsListContainsActive(notifications)) {
            // Create a header in the notificationsListWithHeaders for active notifications
            notificationsListWithHeaders.add(new NotificationsListItem(true, "Active", null));

            // Add all active objects
            for (CustomBatteryNotification n : notifications) {
                if(n.getActive())
                    notificationsListWithHeaders.add(new NotificationsListItem(false, "", n));

                // Since the list is sorted, all active objects are first
                if(!n.getActive())
                    break;
            }
        }
        // If the list of notification objects contains at least one notification set as INactive
        if(Utility.notificationsListContainsInactive(notifications)) {
            // Create a header in the notificationsListWithHeaders for INactive notifications
            notificationsListWithHeaders.add(new NotificationsListItem(true, "Deactivated", null));

            // Add all active objects
            for (CustomBatteryNotification n : notifications) {
                if(!n.getActive())
                    notificationsListWithHeaders.add(new NotificationsListItem(false, "", n));
            }
        }

        // Refresh the RecyclerView adapter
        notificationsAdapter.swap(notificationsListWithHeaders);

        // Set visibility of text and icon for 0 notifications
        if(notificationsListWithHeaders.size() > 0) {
            mainText.setVisibility(View.GONE);
        }
        else {
            mainText.setVisibility(View.VISIBLE);
        }
    }

    public void editItem(int id) {
        mainActivity.startModifyActivity(mainActivity.REQUEST_EDIT_NOTIFICATION, id);
    }
}
