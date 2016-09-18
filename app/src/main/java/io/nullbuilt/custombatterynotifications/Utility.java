package io.nullbuilt.custombatterynotifications;

import android.net.Uri;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.List;

public class Utility {

    public static class UriSerializeDeserialize implements JsonSerializer<Uri>, JsonDeserializer<Uri> {
        @Override
        public JsonElement serialize(Uri src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }

        @Override
        public Uri deserialize(final JsonElement src, final Type srcType,
                               final JsonDeserializationContext context) throws JsonParseException {
            return Uri.parse(src.getAsString());
        }
    }

    /**
     * Sorts a list of CustomBatteryNotification objects,
     * by active/inactive, charging status and percentage.
     * @param notificationList Array List of CustomBatteryNotification objects
     * @return the sorted list
     */
    public static List<CustomBatteryNotification> sortNotificationsList(
            List<CustomBatteryNotification> notificationList) {

        return notificationList;
    }

    /**
     * Returns true if the list of CustomBatteryNotification objects
     * contains at least 1 object with .active == true
     */
    public boolean notificationsListContainsActive(List<CustomBatteryNotification> notificationList) {
        // TODO implement notificationsListContainsActive
        return true;
    }

    /**
     * Returns true if the list of CustomBatteryNotification objects
     * contains at least 1 object with .active == false
     */
    public boolean notificationsListContainsInactive(List<CustomBatteryNotification> notificationList) {
        // TODO implement notificationsListContainsActive
        return true;
    }
}
