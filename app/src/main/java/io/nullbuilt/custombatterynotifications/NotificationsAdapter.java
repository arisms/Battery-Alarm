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
public class NotificationsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static final String TAG = "NotificationsAdapter";
    private static HomeFragment homeFragment;
    private static final int TYPE_HEADER = 1;
    private static final int TYPE_ITEM = 2;

    List<NotificationsListItem> mNotificationsList;

    public NotificationsAdapter(Fragment fragment, List<NotificationsListItem> notificationList) {
        if(notificationList != null)
            this.mNotificationsList = new ArrayList<>(notificationList);
        else
            this.mNotificationsList = new ArrayList<>();
        Log.d(TAG, "NotificationsAdapter: mNotificationsList size = " + mNotificationsList.size());

        homeFragment = (HomeFragment) fragment;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // Inflate the ViewHolder with the correct layout
        // depending on whether the item is a header or a notification
        View view;
        if(viewType == TYPE_HEADER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_header, parent, false);
            return new ViewHolderHeader(view);
        }
        else if (viewType == TYPE_ITEM) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
                return new ViewHolderItem(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        if(holder instanceof ViewHolderHeader) {
            final ViewHolderHeader holderHeader = (ViewHolderHeader) holder;
            holderHeader.headerText.setText(mNotificationsList.get(position).headerText);
        }
        else if (holder instanceof ViewHolderItem) {
            final ViewHolderItem holderItem = (ViewHolderItem) holder;

            Log.d(TAG, "onBindViewHolder: percentage text = " + mNotificationsList.get(position)
                    .customBatteryNotification.getPercentage() + ", "
                    + mNotificationsList.get(position).customBatteryNotification.getActive());

            // Set Battery Percentage text
            int percentage = (mNotificationsList.get(position).customBatteryNotification.getPercentage()) * 5;
            String percentageText = String.valueOf(percentage) + "%";
            holderItem.percentage.setText(percentageText);
            holderItem.percentage.setMinEms(2);

            // Set Battery Status text
            String statusText;
            if (mNotificationsList.get(position).customBatteryNotification.getBatteryStatus()
                    .equals(BatteryStatus.CHARGING)) {
                statusText = "Charging";
            } else
                statusText = "Discharging";
            holderItem.status.setText(statusText);

            // Set Active switch
            holderItem.active.setChecked(mNotificationsList.get(position).customBatteryNotification.getActive());
            holderItem.active.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mNotificationsList.get(position).customBatteryNotification.setActive(holderItem.active.isChecked());
                    homeFragment.updateNotificationsList(mNotificationsList);
                    homeFragment.activeStateChanged(holderItem.active.isChecked());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mNotificationsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(mNotificationsList.get(position).isHeader)
            return TYPE_HEADER;
        else
            return TYPE_ITEM;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public class ViewHolderItem extends RecyclerView.ViewHolder {
        public TextView percentage;
        public TextView status;
        public SwitchCompat active;

        public ViewHolderItem(View view) {
            super(view);
            percentage = (TextView) view.findViewById(R.id.text_notification_percentage);
            status = (TextView) view.findViewById(R.id.text_notification_status);
            active = (SwitchCompat) view.findViewById(R.id.switch_active);
        }
    }

    public class ViewHolderHeader extends RecyclerView.ViewHolder {
        public TextView headerText;

        public ViewHolderHeader(View view) {
            super(view);
            headerText = (TextView) view.findViewById(R.id.text_list_header);
        }
    }

    public void swap(List<NotificationsListItem> updatedList) {
        if(mNotificationsList != null) {
            mNotificationsList.clear();
            mNotificationsList.addAll(updatedList);
        }
        else
            mNotificationsList = updatedList;

        notifyDataSetChanged();


    }

    public void clearAll() {
        Log.d(TAG, "clearAll: size = " + mNotificationsList.size());
        mNotificationsList.clear();
        this.notifyDataSetChanged();
    }
}

