package io.nullbuilt.custombatterynotifications;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
        List<CustomBatteryNotification> notifications = getListOfCustomNotifications(mainActivity);
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

    public void updateNotificationsList(List<CustomBatteryNotification> notificationsList) {
        // Get the Shared Preferences file for writing.
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new GsonBuilder().registerTypeAdapter(Uri.class, new Utility.UriSerializeDeserialize()).create();

        // Convert the list of objects to a JSON string and then
        // write the updated list back to the SharedPreferences
        String json = gson.toJson(notificationsList);
        editor.putString("notificationsJson", json);
        editor.apply();
    }

    public void clearAll() {
        notificationsAdapter.clearAll();
    }
}
