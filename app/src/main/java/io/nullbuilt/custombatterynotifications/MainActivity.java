package io.nullbuilt.custombatterynotifications;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int REQUEST_CREATE_NOTIFICATION = 1;
    private static final int REQUEST_EDIT_NOTIFICATION = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Add the Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        if( savedInstanceState == null ) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container_main, new HomeFragment()).commit();
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
            startModifyActivity(REQUEST_EDIT_NOTIFICATION);
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

}
