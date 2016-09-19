package io.nullbuilt.custombatterynotifications;

public class NotificationsListItem {
    public boolean isHeader;
    public String headerText;
    public CustomBatteryNotification customBatteryNotification;

    public NotificationsListItem(boolean isHeader, String headerText,
                                 CustomBatteryNotification customBatteryNotification) {
        this.isHeader = isHeader;
        this.headerText = headerText;
        this.customBatteryNotification = customBatteryNotification;
    }
}
