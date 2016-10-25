package io.nullbuilt.custombatterynotifications;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

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
    private static MainActivity mainActivity;
    private static boolean isTouched;

    List<NotificationsListItem> mNotificationsList;

    public NotificationsAdapter(Fragment fragment, List<NotificationsListItem> notificationList) {
        if(notificationList != null)
            this.mNotificationsList = new ArrayList<>(notificationList);
        else
            this.mNotificationsList = new ArrayList<>();
//        Log.d(TAG, "NotificationsAdapter: mNotificationsList size = " + mNotificationsList.size());

        homeFragment = (HomeFragment) fragment;
        mainActivity = (MainActivity) fragment.getActivity();
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

            // Set Battery Percentage text
            int percentage = (mNotificationsList.get(position).customBatteryNotification.getPercentage());
            String percentageText = String.valueOf(percentage) + "%";
            holderItem.percentage.setText(percentageText);
            holderItem.percentage.setMinEms(2);

            // Set Battery Status text
            String statusText;
            if (mNotificationsList.get(position).customBatteryNotification.getBatteryStatus()
                    .equals(BatteryStatus.CHARGING)) {
                statusText = mainActivity.getString(R.string.status_charging);
            } else
                statusText = mainActivity.getString(R.string.status_discharging);
            holderItem.status.setText(statusText);

            // Set Active switch
            holderItem.active.setChecked(mNotificationsList.get(position).customBatteryNotification.getActive());
            isTouched = false;
            holderItem.active.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    isTouched = true;
                    return false;
                }
            });
//            holderItem.active.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    mNotificationsList.get(position).customBatteryNotification.setActive(holderItem.active.isChecked());
//                    homeFragment.updateNotificationsList(mNotificationsList);
//                    homeFragment.activeStateChanged(holderItem.active.isChecked());
//                }
//            });
            holderItem.active.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(isTouched) {
                        mNotificationsList.get(position).customBatteryNotification.setActive(holderItem.active.isChecked());
                        homeFragment.updateNotificationsList(mNotificationsList);
                        homeFragment.activeStateChanged(holderItem.active.isChecked());
                    }
                }
            });

            // Set Click Listeners
            holderItem.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Log.d(TAG, "id = " + mNotificationsList.get(position).customBatteryNotification.getId());
                    Toast.makeText(mainActivity, mainActivity.getString(R.string.info_edit),
                            Toast.LENGTH_SHORT).show();
                }
            });
            holderItem.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    AlertDialog dialog = showOptionsDialog(position);
                    dialog.show();
                    return true;
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

    private AlertDialog showOptionsDialog(final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        int itemsId;
        if(mNotificationsList.get(position).customBatteryNotification.getActive())
            itemsId = R.array.longclick_options_array_active;
        else
            itemsId = R.array.longclick_options_array_deactivated;

        builder.setItems(itemsId, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 0:
                        boolean newStatus = !mNotificationsList.get(position).customBatteryNotification.getActive();
                        // Change active state
                        mNotificationsList.get(position).customBatteryNotification.setActive(newStatus);
                        homeFragment.updateNotificationsList(mNotificationsList);
                        homeFragment.activeStateChanged(newStatus);
                        break;
                    case 1:
                        homeFragment.editItem(mNotificationsList.get(position).customBatteryNotification.getId());
                        break;
                    case 2:
                        AlertDialog deleteDialog = deleteNotification(position);
                        deleteDialog.show();
                        break;
                    default:
                        break;
                }
            }
        });
        return builder.create();
    }

    private AlertDialog deleteNotification(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        builder.setTitle(mainActivity.getString(R.string.dialog_delete_title));
        builder.setMessage(mainActivity.getString(R.string.dialog_delete_message));
        builder.setPositiveButton(mainActivity.getString(R.string.dialog_delete_positive),
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mNotificationsList.remove(position);
                homeFragment.notificationDeleted();
                homeFragment.updateNotificationsList(mNotificationsList);
            }
        });
        builder.setNegativeButton(mainActivity.getString(R.string.dialog_delete_negative),
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                Log.d(TAG, "onClick: Delete - Cancel");
            }
        });
        return builder.create();
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

    public void swap(final List<NotificationsListItem> updatedList) {

        AsyncTask asyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {

                if(mNotificationsList != null) {
                    mNotificationsList.clear();
                    mNotificationsList.addAll(updatedList);
                }
                else
                    mNotificationsList = updatedList;

                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                notifyDataSetChanged();
            }
        };
        asyncTask.execute();

//        if(mNotificationsList != null) {
//            mNotificationsList.clear();
//            mNotificationsList.addAll(updatedList);
//        }
//        else
//            mNotificationsList = updatedList;
//
//        this.notifyDataSetChanged();
    }

    public void clearAll() {
//        Log.d(TAG, "clearAll: size = " + mNotificationsList.size());
        mNotificationsList.clear();
        this.notifyDataSetChanged();
    }
}

