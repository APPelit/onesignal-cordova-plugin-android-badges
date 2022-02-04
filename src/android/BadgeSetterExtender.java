package com.appelit.os_badges;

import android.content.Context;
import android.content.SharedPreferences;

import com.onesignal.OSNotification;
import com.onesignal.OSNotificationReceivedEvent;
import com.onesignal.OneSignal.OSRemoteNotificationReceivedHandler;

import org.json.JSONObject;

import me.leolin.shortcutbadger.ShortcutBadger;

public class BadgeSetterExtender implements OSRemoteNotificationReceivedHandler {
    private static final String LAUNCHER_BADGE = "LAUNCHER_BADGE";
    private static final String CURRENT_BADGE = "CURRENT_BADGE";

    @Override
    public void remoteNotificationReceived(Context context, OSNotificationReceivedEvent notificationReceivedEvent) {
        OSNotification notification = notificationReceivedEvent.getNotification();

        boolean silent = false;
        try {
            JSONObject additionalData = notification.getAdditionalData();
            if (additionalData != null) {
                if (ShortcutBadger.isBadgeCounterSupported(context)) {
                    String type = "none";
                    if (additionalData.has("android_badgeType")) {
                        type = additionalData.getString("android_badgeType");
                    }

                    if (!type.equalsIgnoreCase("none")) {
                        int count = additionalData.optInt("android_badgeCount");

                        SharedPreferences preferences = context.getSharedPreferences(LAUNCHER_BADGE, Context.MODE_PRIVATE);

                        if (type.equalsIgnoreCase("setto")) {
                            ShortcutBadger.applyCount(context, count);
                        } else if (type.equalsIgnoreCase("increment")) {
                            count += preferences.getInt(CURRENT_BADGE, 0);

                            if (count < 0) {
                                count = 0;
                            }

                            ShortcutBadger.applyCount(context, count);
                        }

                        preferences.edit().putInt(CURRENT_BADGE, count).apply();
                    }
                }

                silent = additionalData.optBoolean("android_silent", false);
            }
        } catch (org.json.JSONException ignored) {
        }

        if (silent) {
            notificationReceivedEvent.complete(null);
        } else {
            notificationReceivedEvent.complete(notification);
        }
    }
}
