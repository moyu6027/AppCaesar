package com.seany.appcaesar.common.utils;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;

import com.seany.appcaesar.Config;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


public class AccessibilityUtil {


    private Long lastEventTimeStamp;
    private Long DELAY = 100L;

    public static boolean isAccessibilityEnabled(Context context, String id) {
        try {
            AccessibilityManager am = (AccessibilityManager) context
                    .getSystemService(Context.ACCESSIBILITY_SERVICE);
            if (am != null) {
                List<AccessibilityServiceInfo> runningServices = am.getEnabledAccessibilityServiceList(AccessibilityEvent.TYPES_ALL_MASK);
                if (runningServices != null) {
                    for (AccessibilityServiceInfo service : runningServices) {
                        if (id.equals(service.getId())) {
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void printAccessibilityEventLog(AccessibilityEvent event) {
        if (event == null) return;

        try {

            String className = "NA";
            AccessibilityNodeInfo nodeInfo;
            String nodeClass = "NA";
            String classContent = "NA";
            String classText = "NA";
            String nodeContent = "NA";
            String nodeId = "NA";
            String nodeText = "NA";
            String nodeFocus = "NA";

            if (event.getClassName() != null) className = event.getClassName().toString();

            if (event.getContentDescription() != null)
                classContent = event.getContentDescription().toString();

            if (event.getText() != null) classText = event.getText().toString();

            if (event.getSource() != null) {
                nodeInfo = event.getSource();

                if (nodeInfo.getClassName() != null) nodeClass = nodeInfo.getClassName().toString();

                if (nodeInfo.getContentDescription() != null)
                    nodeContent = nodeInfo.getContentDescription().toString();

                if (nodeInfo.getViewIdResourceName() != null)
                    nodeId = nodeInfo.getViewIdResourceName();

                if (nodeInfo.getText() != null) nodeText = nodeInfo.getText().toString();

                nodeFocus = String.valueOf(nodeInfo.isFocused());
            }
            System.out.println("//Event_info: package: " + event.getPackageName() + "  classname: " + className + "  classContent: " + classContent + "  classText: " + classText + "  nodeClass: " +
                    nodeClass + "  nodeId: " + nodeId + "  nodeContent: " + nodeContent + "  nodeText: [" + nodeText + "]  "
                    + getEventType(event) + "  nodeFocus: " + nodeFocus);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getEventType(AccessibilityEvent event) {
        switch (event.getEventType()) {
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                return "TYPE_NOTIFICATION_STATE_CHANGED";
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                return "TYPE_VIEW_CLICKED";
            case AccessibilityEvent.TYPE_VIEW_FOCUSED:
                return "TYPE_VIEW_FOCUSED";
            case AccessibilityEvent.TYPE_VIEW_LONG_CLICKED:
                return "TYPE_VIEW_LONG_CLICKED";
            case AccessibilityEvent.TYPE_VIEW_SELECTED:
                return "TYPE_VIEW_SELECTED";
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                return "TYPE_WINDOW_STATE_CHANGED";
            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
                return "TYPE_VIEW_TEXT_CHANGED";
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                return "TYPE_WINDOW_CONTENT_CHANGED";
            case AccessibilityEvent.TYPE_VIEW_SCROLLED:
                return "TYPE_VIEW_SCROLLED";
            case AccessibilityEvent.CONTENT_CHANGE_TYPE_UNDEFINED:
                return "CONTENT_CHANGE_TYPE_UNDEFINED";
            case AccessibilityEvent.TYPE_WINDOWS_CHANGED:
                return "TYPE_WINDOWS_CHANGED";
            case AccessibilityEvent.TYPE_TOUCH_INTERACTION_END:
                return "TYPE_TOUCH_INTERACTION_END";
            case AccessibilityEvent.TYPE_TOUCH_INTERACTION_START:
                return "TYPE_TOUCH_INTERACTION_START";
            case AccessibilityEvent.TYPE_GESTURE_DETECTION_END:
                return "TYPE_GESTURE_DETECTION_END";
            case AccessibilityEvent.TYPE_GESTURE_DETECTION_START:
                return "TYPE_GESTURE_DETECTION_START";
            case AccessibilityEvent.TYPE_VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY:
                return "TYPE_VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY";
            case AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUS_CLEARED:
                return "TYPE_VIEW_ACCESSIBILITY_FOCUS_CLEARED";
            case AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED:
                return "TYPE_VIEW_ACCESSIBILITY_FOCUSED";
            case AccessibilityEvent.TYPE_ANNOUNCEMENT:
                return "TYPE_ANNOUNCEMENT";
            case AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED:
                return "TYPE_VIEW_TEXT_SELECTION_CHANGED";
            case AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_END:
                return "TYPE_TOUCH_EXPLORATION_GESTURE_END";
            case AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_START:
                return "TYPE_TOUCH_EXPLORATION_GESTURE_START";
            case AccessibilityEvent.TYPE_VIEW_HOVER_EXIT:
                return "TYPE_VIEW_HOVER_EXIT";
            case AccessibilityEvent.TYPE_VIEW_HOVER_ENTER:
                return "TYPE_VIEW_HOVER_ENTER";
        }
        return "default";
    }

    public static Intent LaunchApp(PackageManager packageManager, String packageName) throws NullPointerException{
        Intent intent = packageManager.getLaunchIntentForPackage(packageName);
        assert intent != null;
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }

    public static void collapsingNotification(Context context) {
        @SuppressLint("WrongConstant") Object service = context.getSystemService("statusbar");
        if (null == service)
            return;
        try {
            Class<?> clazz = Class.forName("android.app.StatusBarManager");
            Method collapse = null;
            collapse = clazz.getMethod("collapsePanels");
            collapse.setAccessible(true);
            collapse.invoke(service);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }





    /**
     * 深度优先遍历整个tree
     */
    public static AccessibilityNodeInfo traverseNodeDFS(AccessibilityNodeInfo info) {
        Log.d("DFS", "traverseNodeDFS: ");
        return info;

    }


    /**
     * 广度优先遍历tree
     */
    public static void traverseNodeBFS(AccessibilityNodeInfo info) {
        Log.d("BFS", "traverseNodeBFS: ");
        Queue<AccessibilityNodeInfo> queue = new LinkedList<AccessibilityNodeInfo>();
        if (info == null)return;
        queue.add(info);

        while (!queue.isEmpty()) {
            info = queue.poll();
            Log.d("BFS", "traverseNodeBFS: " + info);
            assert info != null;
            if(info.getChildCount() >0) {
                for (int i=0; i< info.getChildCount();i++){
                    if(info.getChild(i)!=null) {queue.add(info.getChild(i));}
                }
            }else{
                queue.add(info);
            }

        }
    }

    private Boolean avoidSameEvent(AccessibilityEvent event) {
        if (lastEventTimeStamp + DELAY > System.currentTimeMillis() && event.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            //Log.d(TAG, "event from same node is ignored");
            return false;
        }
        lastEventTimeStamp = System.currentTimeMillis();
        return true;
    }



}
