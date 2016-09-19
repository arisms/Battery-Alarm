package io.nullbuilt.custombatterynotifications;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for the RecyclerView containing
 * the custom battery notifications
 */
public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder>{
    private static final String TAG = "NotificationsAdapter";

    List<CustomBatteryNotification> mNotificationsList;

    public NotificationsAdapter(List<CustomBatteryNotification> notificationList) {
        if(notificationList != null)
            this.mNotificationsList = new ArrayList<>(notificationList);
        else
            this.mNotificationsList = new ArrayList<>();
        Log.i(TAG, "NotificationsAdapter: mNotificationsList size = " + mNotificationsList.size());
    }

    @Override
    public NotificationsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new NotificationsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.i(TAG, "onBindViewHolder: percentage text = " + mNotificationsList.get(position).getPercentage());

        // Set Battery Percentage text
        int percentage = (mNotificationsList.get(position).getPercentage()) * 5;
        String percentageText = String.valueOf(percentage) + "%";
        holder.percentage.setText(percentageText);
        holder.percentage.setMinEms(2);

        // Set Battery Status text
        String statusText;
        if(mNotificationsList.get(position).getBatteryStatus().equals(BatteryStatus.CHARGING)) {
            statusText = "- Charging";
        }
        else
            statusText = "- Discharging";
        holder.status.setText(statusText);

        // Set Active switch
        holder.active.setChecked(mNotificationsList.get(position).getActive());


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
        public Switch active;

        public ViewHolder(View view) {
            super(view);
            percentage = (TextView) view.findViewById(R.id.text_notification_percentage);
            status = (TextView) view.findViewById(R.id.text_notification_status);
            active = (Switch) view.findViewById(R.id.switch_active);
        }
    }

    private class ListItem {
        public boolean isHeader;
        public CustomBatteryNotification notification;

        public ListItem(boolean isHeader, CustomBatteryNotification notification) {
            this.isHeader = isHeader;
            this.notification = notification;
        }
    }
}