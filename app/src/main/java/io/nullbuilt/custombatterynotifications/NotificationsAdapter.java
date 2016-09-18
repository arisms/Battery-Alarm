package io.nullbuilt.custombatterynotifications;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Adapter for the RecyclerView containing
 * the custom battery notifications
 */
public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder>{


    public NotificationsAdapter() {
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView percentage;
        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item, parent, false));
            percentage = (TextView) itemView.findViewById(R.id.text_percentage_title);
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