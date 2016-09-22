package io.nullbuilt.custombatterynotifications;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aris on 16/09/16.
 */
public class ModifyFragment extends Fragment {
    private static final String TAG = "ModifyFragment";
    private static Spinner statusSpinner = null;

    public static final int REQUEST_RINGTONE = 3;
    private static EditText percentageValueText = null;
    private static TextView textRingtoneValue = null;
    private static final int VOLUME_MIN = 0;
    private static final int VOLUME_MAX = 4;
    private static CheckBox vibrateCheckbox;
    private Uri ringtoneUri;
    private int idEdit;
    private boolean playRingtoneFlag;
    SeekBar percentageSeekBar, volumeSeekBar;
    ModifyActivity modifyActivity = null;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_modify, container, false);

        Bundle args = getArguments();
        idEdit = args.getInt("id");
        if (idEdit > 0)
            playRingtoneFlag = false;

        setViews(rootView, idEdit);

        return rootView;
    }

    private void setViews(View rootView, final int idEdit) {
        ringtoneUri = null;

        // Instantiate View variables
        percentageValueText = (EditText) rootView.findViewById(R.id.text_percentage_value);
        percentageValueText.setSelectAllOnFocus(true);
        percentageSeekBar = (SeekBar) rootView.findViewById(R.id.seekbar_percentage);
        percentageValueText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)
                    percentageValueText.setSelected(false);
                percentageSeekBar.setProgress(Integer.parseInt(percentageValueText.getText().toString()));
                return false;
            }
        });
        percentageValueText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (percentageValueText.getText().length() > 0) {
                    int value = Integer.parseInt(percentageValueText.getText().toString());
                    if (value > 0 && value <= 100)
                        percentageSeekBar.setProgress(value);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // Percentage Seek Bar
        percentageSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                String value = Integer.toString(seekBar.getProgress());
                percentageValueText.setText(value);
                percentageValueText.setSelection(percentageValueText.getText().length());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(seekBar.getProgress() == 0)
                    seekBar.setProgress(1);
            }
        });

        vibrateCheckbox = (CheckBox) rootView.findViewById(R.id.checkbox_vibrate);
        textRingtoneValue = (TextView) rootView.findViewById(R.id.text_ringtone_value);

        // Volume Seek Bar
        volumeSeekBar = (SeekBar) rootView.findViewById(R.id.seekbar_volume);
        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                // Get the volume value from the seekbar
                int volume = volumeSeekBar.getProgress();
                if(playRingtoneFlag)
                    playRingtoneForFiveSeconds(ringtoneUri, volume);
                playRingtoneFlag = true;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Volume buttons
        ImageView volumeMin = (ImageView) rootView.findViewById(R.id.button_volume_min);
        volumeMin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                volumeSeekBar.setProgress(volumeSeekBar.getProgress() - 1);
            }
        });

        ImageView volumeMax = (ImageView) rootView.findViewById(R.id.button_volume_max);
        volumeMax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                volumeSeekBar.setProgress(volumeSeekBar.getProgress() + 1);
            }
        });

        // Battery Status Spinner
        statusSpinner = (Spinner) rootView.findViewById(R.id.spinner_status);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.status_values_array, R.layout.status_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        statusSpinner.setAdapter(adapter);

        /* Clickable Layouts */
        // Set statusSpinner layout to show the statusSpinner when clicked
        LinearLayout statusLayout = (LinearLayout) rootView.findViewById(R.id.layout_status);
        statusLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statusSpinner.performClick();
            }
        });

        // Set ringtone layout to open RingtoneManager Activity
        LinearLayout ringtoneLayout = (LinearLayout) rootView.findViewById(R.id.layout_ringtone);
        ringtoneLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select sound");
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
                startActivityForResult(intent, REQUEST_RINGTONE);
            }
        });

        // Set vibration layout to toggle the checkbox when clicked
        RelativeLayout vibrateLayout = (RelativeLayout) rootView.findViewById(R.id.layout_vibration);
        vibrateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: isChecked = " + vibrateCheckbox.isChecked());
                if(vibrateCheckbox.isChecked())
                    vibrateCheckbox.setChecked(false);
                else
                    vibrateCheckbox.setChecked(true);
            }
        });

        // If editing an existing item, load the object from SharedPreferences
        // and edit the values in corresponding input fields accordingly
        if(idEdit > 0) {
            CustomBatteryNotification editItem = getItemWithId(idEdit);
            percentageSeekBar.setProgress(editItem.getPercentage());
//            statusSpinner.setSelection();
            BatteryStatus status = editItem.getBatteryStatus();
            if(status.equals(BatteryStatus.DISCHARGING))
                statusSpinner.setSelection(0);
            else
                statusSpinner.setSelection(1);

            setRingtoneName(editItem.getRingtoneUri());
            volumeSeekBar.setProgress(editItem.getVolume());
            vibrateCheckbox.setChecked(editItem.getVibrate());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode - resultCode: " + requestCode + " - " + resultCode);
        if(requestCode == ModifyFragment.REQUEST_RINGTONE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if(uri != null) {
                playRingtoneFlag = true;
                setRingtoneName(uri);
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(context instanceof ModifyActivity) {
            Log.d(TAG, "onAttach: context instanceof ModifyActivity");
            modifyActivity = (ModifyActivity) context;
        }
    }

    private void setRingtoneName(Uri uri) {
        Ringtone ringtone = RingtoneManager.getRingtone(getActivity(), uri);
        try {
            final MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(getActivity(), uri);
            String ringtoneName = ringtone.getTitle(getActivity());
            Log.d(TAG, "setRingtoneName: uri = " + ringtoneName);
            textRingtoneValue.setText(ringtoneName);
            ringtoneUri = uri;

        } catch (IOException e) {
            Toast.makeText(modifyActivity, modifyActivity.getString(R.string.invalid_ringtone),
                    Toast.LENGTH_LONG).show();

            e.printStackTrace();
        }
    }

    private void playRingtoneForFiveSeconds(Uri ringtoneUri, int volume) {

        float volumeFloat = (float) volume * (0.25f);
        try {
            final MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setVolume(volumeFloat, volumeFloat);

            if(ringtoneUri != null) {
                Log.d(TAG, "playRingtoneForFiveSeconds: ringtoneUri = " + ringtoneUri);

                mediaPlayer.setDataSource(getActivity(), ringtoneUri);
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
                Log.d(TAG, "playRingtoneForFiveSeconds: ringtoneUri is null");
            }

        } catch (IOException e) {
            Toast.makeText(modifyActivity, modifyActivity.getString(R.string.invalid_ringtone),
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }

    /**
     * Get custom values from Views and create
     * the CustomBatteryNotification object.
     */
    public CustomBatteryNotification getValuesFromViews(int id) {
        int percentage = percentageSeekBar.getProgress();
        BatteryStatus batteryStatus = statusSpinner.getSelectedItem().toString().equals("Charging")
                ? BatteryStatus.CHARGING : BatteryStatus.DISCHARGING;
        int volume = volumeSeekBar.getProgress();
        boolean vibrate = vibrateCheckbox.isChecked();

        // If the ringtone was not selected, add the default ringtone
        if(ringtoneUri == null) {
            ringtoneUri = RingtoneManager.getActualDefaultRingtoneUri(getActivity(),
                    RingtoneManager.TYPE_NOTIFICATION);
        }

        CustomBatteryNotification customBatteryNotification = new CustomBatteryNotification(
                percentage,
                batteryStatus,
                ringtoneUri,
                volume,
                vibrate,
                true,
                id
        );

        return customBatteryNotification;
    }

    /**
     * Create a CustomBatteryNotification object and save it
     * to the list of objects in the SharedPreferences file.
     */
    public boolean saveCustomBatteryNotification(boolean edit) {

        // Get the Shared Preferences file for writing.
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new GsonBuilder().registerTypeAdapter(Uri.class, new Utility.UriSerializeDeserialize()).create();

        // Read the list of objects from the SharedPreferences
        List<CustomBatteryNotification> notificationsList = new ArrayList<CustomBatteryNotification>();
        String json = sharedPreferences.getString("notificationsJson", "");
        Log.d(TAG, "saveCustomBatteryNotification: JSON string from SP: " + json);

        // Convert from JSON string to list of CustomBatteryNotification objects
        Type type = new TypeToken<List<CustomBatteryNotification>>(){}.getType();
        notificationsList = gson.fromJson(json, type);
        if(notificationsList != null) {
            Log.d(TAG, "saveCustomBatteryNotification: notificationsList.size = " + notificationsList.size());
            for (CustomBatteryNotification c : notificationsList) {
                Log.d(TAG, "saveCustomBatteryNotification: notificationsList - " + c.toString());
            }
        }
        else
            Log.d(TAG, "saveCustomBatteryNotification: notificationsList NULL");


        // Create the new object
        int maxId;
        if(notificationsList.size() == 0)
            maxId = 1;
        else
            maxId = sharedPreferences.getInt("maxId", 0) + 1;
        editor.putInt("maxId", maxId);

        int id;
        if(edit)
            id = idEdit;
        else
            id = maxId;

        CustomBatteryNotification customBatteryNotification = getValuesFromViews(id);

        // Search the list for existing objects
        // with the same percentage and status
        for (CustomBatteryNotification n : notificationsList) {
            if (n.getPercentage() == customBatteryNotification.getPercentage()
                    && n.getBatteryStatus().equals(customBatteryNotification.getBatteryStatus())
                    && n.getId() != customBatteryNotification.getId())
                return false;
        }

        // If editing existing notification
        if (edit) {
            for (CustomBatteryNotification n : notificationsList) {
                if (n.getId() == customBatteryNotification.getId()) {
                    notificationsList.remove(n);
                    break;
                }
            }
        }

        // Add the new object to the list
        if(notificationsList == null)
            notificationsList = new ArrayList<>();
        notificationsList.add(customBatteryNotification);

        // Convert the list of objects to a JSON string and then
        // write the updated list back to the SharedPreferences
        json = gson.toJson(notificationsList);
        editor.putString("notificationsJson", json);
        editor.apply();

        return true;
    }

    /**
     * Retrieves the CustomBatteryNotification item
     * from the list of objects in SharedPreferences
     */
    public CustomBatteryNotification getItemWithId(int id) {
        // Get the Shared Preferences file for writing.
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Gson gson = new GsonBuilder().registerTypeAdapter(Uri.class, new Utility.UriSerializeDeserialize()).create();

        // Read the list of objects from the SharedPreferences
        List<CustomBatteryNotification> notificationsList = new ArrayList<CustomBatteryNotification>();
        String json = sharedPreferences.getString("notificationsJson", "");

        // Convert from JSON string to list of CustomBatteryNotification objects
        Type type = new TypeToken<List<CustomBatteryNotification>>(){}.getType();
        notificationsList = gson.fromJson(json, type);

        if(notificationsList != null) {
            for (CustomBatteryNotification n : notificationsList) {
                if(n.getId() == id)
                    return n;
            }
        }
        return null;
    }
}
