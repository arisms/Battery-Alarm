package io.nullbuilt.custombatterynotifications;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
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
import java.util.List;

/**
 * Created by Aris on 13/09/16.
 */
public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    private MainActivity mainActivity;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        Log.d(TAG, "onCreateView: HomeFragment");
        mainActivity = (MainActivity) getActivity();

        // Add the Floating Action Button
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.startModifyActivity(1);
            }
        });

        /* Retrieve the list of CustomBatterNotification
           objects from the SharedPreferences file. */
        List<CustomBatteryNotification> notificationList = getListOfCustomNotifications(mainActivity);

//        RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.id.notifications_recycler, container, false);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.notifications_recycler);
        NotificationsAdapter notificationsAdapter = new NotificationsAdapter();
        recyclerView.setAdapter(notificationsAdapter);

        return rootView;
    }

    private List<CustomBatteryNotification> getListOfCustomNotifications(Context context) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new GsonBuilder().registerTypeAdapter(Uri.class, new Utility.UriSerializeDeserialize()).create();

        List<CustomBatteryNotification> notificationsList = new ArrayList<CustomBatteryNotification>();
        String json = sharedPreferences.getString("notificationsJson", "");
        Log.d(TAG, "getListOfCustomNotifications: notificationsList JSON string from SP: " + json);

        // Convert from JSON string to list of CustomBatteryNotification objects
        Type type = new TypeToken<List<CustomBatteryNotification>>(){}.getType();
        notificationsList = gson.fromJson(json, type);
        if(notificationsList != null) {
            Log.d(TAG, "getListOfCustomNotifications: notificationsList.size = " + notificationsList.size());
            for (CustomBatteryNotification c : notificationsList) {
                Log.d(TAG, "getListOfCustomNotifications: notificationsList - " + c.toString());
            }
        }
        else
            Log.d(TAG, "getListOfCustomNotifications: notificationsList NULL");

        return notificationsList;
    }
}
