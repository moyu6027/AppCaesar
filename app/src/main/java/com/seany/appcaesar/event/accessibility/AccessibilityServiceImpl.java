package com.seany.appcaesar.event.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import androidx.annotation.RequiresApi;

import com.seany.appcaesar.MainFunctions;
import com.seany.appcaesar.common.utils.StringUtil;
import com.seany.appcaesar.node.PerformAction;

import java.lang.ref.WeakReference;


public class AccessibilityServiceImpl extends AccessibilityService {
    public static final int MODE_BLOCK = 0;
    public static final int MODE_NORMAL = 1;

    private static final String TAG = "AccessibilityService";

    private PackageManager mPackageManager;
    private static WeakReference<AccessibilityEventTracker> accessibilityEventTrackerRef = null;
    public static MainFunctions mainFunctions;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "AccessibilityService on create");
        PerformAction.setAccessibilityService(this);
        performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "AccessibilityService on start command");
        mainFunctions = new MainFunctions(this);
        mainFunctions.onServiceConnected();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public static void setAccessibilityEventTracker(AccessibilityEventTracker tracker) {
        accessibilityEventTrackerRef = new WeakReference<>(tracker);
    }

    @Override
    protected void onServiceConnected() {
        Log.v(TAG, "Accessibility Service connected");
        super.onServiceConnected();
        mainFunctions = new MainFunctions(this);
        mainFunctions.onServiceConnected();
        try {
            mPackageManager = this.getPackageManager();
            AccessibilityServiceInfo info = new AccessibilityServiceInfo();
            info.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED;
            info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
            info.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK;
            info.flags |= AccessibilityServiceInfo.DEFAULT;
            info.flags |= AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS;
            info.flags |= AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS;
            info.flags |= AccessibilityServiceInfo.FLAG_REQUEST_ENHANCED_WEB_ACCESSIBILITY;
//            info.flags |= AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS;
//            info.flags |= AccessibilityServiceInfo.FLAG_REQUEST_ACCESSIBILITY_BUTTON;
            info.notificationTimeout = 100;
            info.packageNames = null;
            setServiceInfo(info);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if(event == null){return;}
        PerformAction.setAccessibilityEvent(event);
        //不对自身事件监控
        if (StringUtil.equals(event.getPackageName(), getPackageName())) {
            return;
        }
        if (accessibilityEventTrackerRef == null) {
            return;
        }
        AccessibilityEventTracker tracker = accessibilityEventTrackerRef.get();
        Context context = getApplicationContext();
        // 由tracker通知
        if (tracker != null) {
            tracker.handleAccessibilityEvent(event,context,this);
        }
    }

    @Override
    protected boolean onGesture(int gestureId) {
        if (accessibilityEventTrackerRef == null) {
            return super.onGesture(gestureId);
        }
        AccessibilityEventTracker tracker = accessibilityEventTrackerRef.get();

        // 由tracker通知
        if (tracker != null) {
            tracker.handleGestureEvent(gestureId);
        }

        return super.onGesture(gestureId);
    }



    @Override
    public void onInterrupt() {
        Log.e(TAG, "onInterrupt() is Called...");
    }

    private void setServiceInfoToTouchBlockMode() {
        AccessibilityServiceInfo info = getServiceInfo();
        if (info == null) {
            Log.e(TAG, "ServiceInfo为空");
            return;
        }
        info.flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS |
                AccessibilityServiceInfo.FLAG_REQUEST_ENHANCED_WEB_ACCESSIBILITY |
                AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS |
                AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS |
                AccessibilityServiceInfo.DEFAULT |
                AccessibilityServiceInfo.FLAG_REQUEST_TOUCH_EXPLORATION_MODE;

        Log.d(TAG, "辅助功能进入触摸监控模式");
        setServiceInfo(info);
    }

    private void setServiceToNormalMode() {
        AccessibilityServiceInfo info = getServiceInfo();
        if (info == null) {
            Log.e(TAG, "ServiceInfo为空");
            return;
        }
        info.flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS |
                AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS |
                AccessibilityServiceInfo.FLAG_REQUEST_ENHANCED_WEB_ACCESSIBILITY |
                AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS |
                AccessibilityServiceInfo.DEFAULT;

        Log.d(TAG, "辅助功能进入正常模式");
        setServiceInfo(info);
    }
}
