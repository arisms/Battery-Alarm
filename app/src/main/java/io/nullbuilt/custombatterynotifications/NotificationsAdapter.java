package io.nullbuilt.custombatterynotifications;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for the RecyclerView containing
 * the custom battery notifications
 */
public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder>{
    private static final String TAG = "NotificationsAdapter";
    private static HomeFragment homeFragment;

    List<CustomBatteryNotification> mNotificationsList;

    public NotificationsAdapter(Fragment fragment, List<CustomBatteryNotification> notificationList) {
        if(notificationList != null)
            this.mNotificationsList = new ArrayList<>(notificationList);
        else
            this.mNotificationsList = new ArrayList<>();
        Log.d(TAG, "NotificationsAdapter: mNotificationsList size = " + mNotificationsList.size());

        this.homeFragment = (HomeFragment) fragment;
    }

    @Override
    public NotificationsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new NotificationsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: percentage text = " + mNotificationsList.get(position).getPercentage()
                + ", " + mNotificationsList.get(position).getActive());

        // Set Battery Percentage text
        int percentage = (mNotificationsList.get(position).getPercentage()) * 5;
        String percentageText = String.valueOf(percentage) + "%";
        holder.percentage.setText(percentageText);
        holder.percentage.setMinEms(2);

        // Set Battery Status text
        String statusText;
        if(mNotificationsList.get(position).getBatteryStatus().equals(BatteryStatus.CHARGING)) {
            statusText = "Charging";
        }
        else
            statusText = "Discharging";
        holder.status.setText(statusText);

        // Set Active switch
        holder.active.setChecked(mNotificationsList.get(position).getActive());
        holder.active.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: id = " + mNotificationsList.get(position).getId());
                if(holder.active.isChecked()) {
                    Log.d(TAG, "swtich is now checked");
                    //mNotificationsList.get(position).ac
                    homeFragment.updateNotificationsList(mNotificationsList);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNotificationsList.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView percentage;
        public TextView status;
        public SwitchCompat active;

        public ViewHolder(View view) {
            super(view);
            percentage = (TextView) view.findViewById(R.id.text_notification_percentage);
            status = (TextView) view.findViewById(R.id.text_notification_status);
            active = (SwitchCompat) view.findViewById(R.id.switch_active);
        }
    }

    public void clearAll() {
        Log.d(TAG, "clearAll: size = " + mNotificationsList.size());
        mNotificationsList.clear();
        this.notifyDataSetChanged();
    }
}

