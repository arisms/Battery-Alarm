package io.nullbuilt.custombatterynotifications;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ModifyActivity extends AppCompatActivity {
    private static final String TAG = "ModifyActivity";
    private static ModifyFragment modifyFragment = null;
    private static ModifyActivity modifyActivity;

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

        Intent intent = getIntent();
        int id = intent.getIntExtra("id", 0);
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        final boolean edit = id > 0;

        // Create the fragment
        if( savedInstanceState == null ) {
            modifyFragment = new ModifyFragment();
            modifyFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container_modify, modifyFragment).commit();
        }

        modifyActivity = this;

        // Save Button
        Button saveButton = (Button) findViewById(R.id.button_save);
        if (saveButton != null) {
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (modifyFragment.saveCustomBatteryNotification(edit)) {
                        setResult(Activity.RESULT_OK);
                        finish();
                    }
                    else
                        Toast.makeText(modifyActivity,
                                "A notification with the same percentage and charging status already exists.",
                                Toast.LENGTH_LONG).show();
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
