package io.nullbuilt.custombatterynotifications;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class ModifyActivity extends AppCompatActivity {
    private static final String TAG = "ModifyActivity";
    private static ModifyFragment modifyFragment = null;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);

        // Add and Modify the Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_modify);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_cancel);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Create the fragment
        if( savedInstanceState == null ) {
            modifyFragment = new ModifyFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container_modify, modifyFragment).commit();
        }

        // Save Button
        Button saveButton = (Button) findViewById(R.id.button_save);
        if (saveButton != null) {
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    modifyFragment.saveCustomBatteryNotification();
                    setResult(Activity.RESULT_OK);
                    finish();
                }
            });
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        setResult(Activity.RESULT_CANCELED);
        finish();

        return false;
    }
}
