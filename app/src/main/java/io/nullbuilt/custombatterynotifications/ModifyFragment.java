package io.nullbuilt.custombatterynotifications;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;

/**
 * Created by Aris on 16/09/16.
 */
public class ModifyFragment extends Fragment {
    private static final String TAG = "ModifyFragment";
    private static Spinner spinner = null;

    public static final int REQUEST_RINGTONE = 3;
    private static TextView percentageValueText = null;
    private static TextView textRingtoneValue = null;
    public static final int VOLUME_MIN = 0;
    public static final int VOLUME_MAX = 4;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_modify, container, false);

        // Set Views
        percentageValueText = (TextView) rootView.findViewById(R.id.text_percentage_value);
        textRingtoneValue = (TextView) rootView.findViewById(R.id.text_ringtone_value);

        setPercentageSeekBar(rootView);

        setStatusSpinner(rootView);

        setUpClickableLayouts(rootView);

        return rootView;
    }

    private void setPercentageSeekBar(View rootView) {
        // Seek Bar
        SeekBar percentageSeekBar = (SeekBar) rootView.findViewById(R.id.seekbar_percentage);
        percentageSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                String value = Integer.toString(5*seekBar.getProgress()) + "%";
                percentageValueText.setText(value);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(seekBar.getProgress() == 0)
                    seekBar.setProgress(1);
            }
        });
    }

    private void setStatusSpinner(View rootView) {
        spinner = (Spinner) rootView.findViewById(R.id.spinner_status);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.status_values_array, R.layout.status_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
    }

    private void setUpClickableLayouts(View rootView) {

        // Set spinner layout to show the spinner when clicked
        LinearLayout statusLayout = (LinearLayout) rootView.findViewById(R.id.layout_status);
        statusLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinner.performClick();
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

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult: requestCode - resultCode: " + requestCode + " - " + resultCode);
        if(requestCode == ModifyFragment.REQUEST_RINGTONE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if(uri != null) {
                setRingtone(uri);
            }
        }
    }


    private void setRingtone(Uri uri) {
        Ringtone ringtone = RingtoneManager.getRingtone(getActivity(), uri);
        String ringtoneName = ringtone.getTitle(getActivity());
        Log.i(TAG, "setRingtone: uri = " + ringtoneName);
        textRingtoneValue.setText(ringtoneName);
    }

    private void playRingtoneForFiveSeconds(Uri ringtoneUri, int volume) {

        try {
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setVolume(0.1f, 0.1f);
            mediaPlayer.setDataSource(getActivity(), ringtoneUri);
            mediaPlayer.prepare();
            mediaPlayer.start();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
