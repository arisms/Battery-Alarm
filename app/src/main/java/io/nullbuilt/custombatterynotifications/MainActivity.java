package io.nullbuilt.custombatterynotifications;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int REQUEST_CREATE_NOTIFICATION = 1;
    private static final int REQUEST_EDIT_NOTIFICATION = 2;
    private static HomeFragment homeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Add the Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        // Add the Floating Action Button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startModifyActivity(1);
                }
            });
        }

        if( savedInstanceState == null ) {
            homeFragment = new HomeFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container_main, homeFragment).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu
        getMenuInflater().inflate(R.menu.main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_information) {
            // Start the AboutActivity
            //startModifyActivity(REQUEST_EDIT_NOTIFICATION);
            homeFragment.clearAll();
            return true;
        }
        else if(id == R.id.action_sort) {
            testCBNCompareTo();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUEST_CREATE_NOTIFICATION:
                Log.d(TAG, "onActivityResult: REQUEST_CREATE_NOTIFICATION, result = " + resultCode);
                if(resultCode == Activity.RESULT_OK)
                    Toast.makeText(this, getString(R.string.notification_created), Toast.LENGTH_LONG).show();
                break;
            case REQUEST_EDIT_NOTIFICATION:
                Log.d(TAG, "onActivityResult: REQUEST_EDIT_NOTIFICATION, result = " + resultCode);
                if(resultCode == Activity.RESULT_OK)
                    Toast.makeText(this, getString(R.string.notification_updated), Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void startModifyActivity(int requestCode) {
        startActivityForResult(new Intent(this, ModifyActivity.class), requestCode);
    }

    public void testCBNCompareTo() {

        List<CustomBatteryNotification> notificationsList = new ArrayList<>();

        // Three active notifications
        CustomBatteryNotification customBatteryNotification = new CustomBatteryNotification(
                7,
                BatteryStatus.CHARGING,
                null,
                1,
                false,
                true,
                1
        );
        notificationsList.add(customBatteryNotification);

        customBatteryNotification = new CustomBatteryNotification(
                5,
                BatteryStatus.DISCHARGING,
                null,
                1,
                false,
                true,
                2
        );
        notificationsList.add(customBatteryNotification);

        customBatteryNotification = new CustomBatteryNotification(
                7,
                BatteryStatus.DISCHARGING,
                null,
                1,
                false,
                true,
                3
        );
        notificationsList.add(customBatteryNotification);

        // Three active notifications
        customBatteryNotification = new CustomBatteryNotification(
                7,
                BatteryStatus.CHARGING,
                null,
                1,
                false,
                false,
                1
        );
        notificationsList.add(customBatteryNotification);

        customBatteryNotification = new CustomBatteryNotification(
                5,
                BatteryStatus.DISCHARGING,
                null,
                1,
                false,
                false,
                2
        );
        notificationsList.add(customBatteryNotification);

        customBatteryNotification = new CustomBatteryNotification(
                7,
                BatteryStatus.DISCHARGING,
                null,
                1,
                false,
                false,
                3
        );
        notificationsList.add(customBatteryNotification);

        Log.d(TAG, "notificationsList Before Sorting: ");
        for (CustomBatteryNotification n : notificationsList) {
            Log.d(TAG, "Active: " + n.getActive() + ", Status: " + n.getBatteryStatus()
                    + ", Percentage: " + n.getPercentage());
        }

        Collections.sort(notificationsList);

        Log.d(TAG, "notificationsList After Sorting: ");
        for (CustomBatteryNotification n : notificationsList) {
            Log.d(TAG, "Active: " + n.getActive() + ", Status: " + n.getBatteryStatus()
                    + ", Percentage: " + n.getPercentage());
        }
    }
}
