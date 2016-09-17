package io.nullbuilt.custombatterynotifications;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

        return rootView;
    }
}
