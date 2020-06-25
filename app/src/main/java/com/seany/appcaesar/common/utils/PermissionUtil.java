package com.seany.appcaesar.common.utils;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import java.util.Objects;

public class PermissionUtil {
    private static final String TAG = "PermissionUtil";

    /**
     * 检查Accessibility权限
     * */
    public static boolean isAccessibilitySettingsOn(Context context) {
        int accessibilityEnabled = 0;
        try {
            accessibilityEnabled = Settings.Secure.getInt(context.getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            Log.d(TAG, "isAccessibilitySettingsOn: " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
        }

        if (accessibilityEnabled == 1) {
            String services = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            Log.d(TAG, "isAccessibilitySettingsOn: "+services);
            if (services != null) {
                return services.toLowerCase().contains(context.getPackageName().toLowerCase());
            }
        }

        return false;
    }

}
