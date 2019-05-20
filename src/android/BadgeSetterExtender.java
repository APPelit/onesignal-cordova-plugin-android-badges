package com.appelit.os_badges;

import android.content.Context;
import android.content.SharedPreferences;

import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotificationPayload;
import com.onesignal.OSNotificationReceivedResult;

import org.json.JSONObject;

import me.leolin.shortcutbadger.ShortcutBadger;

public class BadgeSetterExtender extends NotificationExtenderService {
    private static final String LAUNCHER_BADGE = "LAUNCHER_BADGE";
    private static final String CURRENT_BADGE = "CURRENT_BADGE";

    @Override
    protected boolean onNotificationProcessing(OSNotificationReceivedResult osNotificationReceivedResult) {
        Context context = getApplicationContext();

        OSNotificationPayload payload = osNotificationReceivedResult.payload;

        if (payload != null) {
            JSONObject additionalData = payload.additionalData;

            if (additionalData != null) {
                if (ShortcutBadger.isBadgeCounterSupported(context)) {
                    String type = additionalData.optString("android_badgeType");

                    if (type != null) {
                        if (type.toLowerCase().equals("none")) {
                            return additionalData.optBoolean("android_silent", false);
                        }

                        int count = additionalData.optInt("android_badgeCount");

                        SharedPreferences preferences = context.getSharedPreferences(LAUNCHER_BADGE, Context.MODE_PRIVATE);

                        if (type.toLowerCase().equals("setto")) {
                            ShortcutBadger.applyCount(context, count);
                        } else if (type.toLowerCase().equals("increment")) {
                            count += preferences.getInt(CURRENT_BADGE, 0);

                            if (count < 0) {
                                count = 0;
                            }

                            ShortcutBadger.applyCount(context, count);
                        }

                        preferences.edit().putInt(CURRENT_BADGE, count).apply();
                    }
                }

                return additionalData.optBoolean("android_silent", false);
            }
        }

        return false;
    }
}
