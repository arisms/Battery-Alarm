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

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public static final int REQUEST_CREATE_NOTIFICATION = 1;
    public static final int REQUEST_EDIT_NOTIFICATION = 2;
    private static HomeFragment homeFragment;
    private static Bundle mSavedInstanceState;

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
                    startModifyActivity(REQUEST_CREATE_NOTIFICATION);
                }
            });
        }

        mSavedInstanceState = savedInstanceState;
        if( savedInstanceState == null ) {
            homeFragment = new HomeFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container_main, homeFragment, "HomeFragment").commit();
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

//        if(id == R.id.action_information) {
//            // Start the AboutActivity
//            //startModifyActivity(REQUEST_EDIT_NOTIFICATION);
//            return true;
//        }
//        else if(id == R.id.action_refresh) {
//            reloadHomeFragment();
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUEST_CREATE_NOTIFICATION:
                Log.d(TAG, "onActivityResult: REQUEST_CREATE_NOTIFICATION, result = " + resultCode);
                if(resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, getString(R.string.notification_created), Toast.LENGTH_LONG).show();
                    reloadHomeFragment();
                }
                break;
            case REQUEST_EDIT_NOTIFICATION:
                Log.d(TAG, "onActivityResult: REQUEST_EDIT_NOTIFICATION, result = " + resultCode);
                if(resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, getString(R.string.notification_updated), Toast.LENGTH_LONG).show();
                    reloadHomeFragment();
                }
                break;
            default:
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(homeFragment.batteryInfoReceiver);
    }

    private void reloadHomeFragment() {
//        HomeFragment homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag("HomeFragment");
        homeFragment.reload();
//        HomeFragment newHomeFragment = new HomeFragment();
//        getSupportFragmentManager().beginTransaction().detach(homeFragment)
//                .replace(R.id.container_main, newHomeFragment);
    }

    public void startModifyActivity(int requestCode) {

        startActivityForResult(new Intent(this, ModifyActivity.class), requestCode);
    }

    public void startModifyActivity(int requestCode, int id) {
        Intent intent = new Intent(this, ModifyActivity.class);
        intent.putExtra("id", id);

        startActivityForResult(intent, requestCode);
    }
}
