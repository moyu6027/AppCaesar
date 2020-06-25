package com.seany.appcaesar.event;

import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import com.seany.appcaesar.event.accessibility.AccessibilityEventTracker;
import com.seany.appcaesar.event.bean.UniversalEventBean;
import com.seany.appcaesar.event.constant.Constant;

public class EventProxy implements AccessibilityEventTracker.AccessibilityListener {
    private static final String TAG = "EventProxy";

    public EventProxy() {

    }

    @Override
    public void onAccessibilityEvent(long time, int eventType, AccessibilityNodeInfo node, String sourcePackage) {
        UniversalEventBean eventBean = new UniversalEventBean();
        eventBean.setEventType(Constant.EVENT_ACCESSIBILITY_EVENT);
        eventBean.setTime(time);
        eventBean.setParam(Constant.KEY_ACCESSIBILITY_TYPE, eventType);
        eventBean.setParam(Constant.KEY_ACCESSIBILITY_SOURCE, sourcePackage);
        eventBean.setParam(Constant.KEY_ACCESSIBILITY_NODE, node);
//        Log.d(TAG, "发送辅助功能事件type="+eventBean.getEventType()+" nodeInfo="+eventBean.getParam(Constant.KEY_ACCESSIBILITY_NODE));

    }

    @Override
    public void onGesture(int gesture) {
        UniversalEventBean eventBean = new UniversalEventBean();
        eventBean.setEventType(Constant.EVENT_ACCESSIBILITY_GESTURE);
        eventBean.setTime(System.currentTimeMillis());
        eventBean.setParam(Constant.KEY_GESTURE_TYPE, gesture);

    }


    public void destroy() {
        Log.d(TAG, "Proxy destroy");
    }
}
