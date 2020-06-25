package com.seany.appcaesar.event;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.Nullable;

import com.seany.appcaesar.common.service.BaseService;
import com.seany.appcaesar.common.utils.PermissionUtil;
import com.seany.appcaesar.event.accessibility.AccessibilityEventTracker;
import com.seany.appcaesar.event.accessibility.AccessibilityServiceImpl;

import java.lang.ref.WeakReference;

import static com.seany.appcaesar.Config.TAG;

public class EventService extends Service {
    private AccessibilityEventTracker accessibilityTracker;
    private EventProxy proxy;
    private WeakReference<Context> contextRef;


    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate: ");
        Context context = getApplicationContext();
        this.contextRef = new WeakReference<>(context);
        proxy = new EventProxy();
        startTrackAccessibilityEvent();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service on start command");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        // 清理各个引用
        if (accessibilityTracker != null) {
            accessibilityTracker.stopTrackEvent();
            accessibilityTracker = null;
        }

        if (proxy != null) {
            proxy.destroy();
            proxy = null;
        }

    }

    public void startTrackAccessibilityEvent() {
        Context context = contextRef.get();
        if (context == null) {
            return;
        }

        // 检查Accessibility
        if (!PermissionUtil.isAccessibilitySettingsOn(context)) {
            Log.d(TAG, "startTrackAccessibilityEvent: no permission");
//            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
//                    | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
//            context.startActivity(intent);
            // 当AccessibilityService已启动，修改运行模式
        } else {
            Log.d(TAG, "startTrackAccessibilityEvent: ");
            if (accessibilityTracker == null) {
                accessibilityTracker = new AccessibilityEventTracker();
            }

            //设置listener,开启代理
            accessibilityTracker.setAccessibilityListener(proxy);
            //开启抓取
            accessibilityTracker.startTrackEvent();
            //开启onAccessibilityEvent
            AccessibilityServiceImpl.setAccessibilityEventTracker(accessibilityTracker);
        }
    }

    public void stopTrackAccessibilityEvent() {
        if (accessibilityTracker != null) {
            accessibilityTracker.stopTrackEvent();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
