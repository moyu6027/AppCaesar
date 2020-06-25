package com.seany.appcaesar.node;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.content.Context;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;

import static android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_BACK;
import static android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_HOME;

public class PerformAction {

    private static String TAG = "PerformAction: ";
    private static AccessibilityEvent mAccessibilityEvent = null;
    private static AccessibilityService mAccessibilityService = null;

    private static Context context;

    public static ArrayList<String> keyWordList = new ArrayList<>();

    public enum ActionType
    {
        BACK,  //返回键
        HOME,  //home
        SETTING,  //设置
        POWER,  //锁屏
        RECENTS,  //应用列表
        NOTIFICATIONS, //通知
        SCROLL_BACKWARD,  //下滑
        SCROLL_FORWARD, //上划

    }

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        PerformAction.context = context;
    }

    /**
     * 设置数据
     *
     * @param service
     * @param
     */
    public static void setAccessibilityService(AccessibilityService service) {
        synchronized (PerformAction.class)
        {
            if (service != null && mAccessibilityService == null)
            {
                mAccessibilityService = service;
            }

        }

    }

    public static void setAccessibilityEvent(AccessibilityEvent event) {
        synchronized (PerformAction.class)
        {
            if (event != null && mAccessibilityEvent == null)
            {
                mAccessibilityEvent = event;
            }

        }

    }

    //=================================================系统操作=================================================
    /**
     * 模拟点击系统相关操作
     */
    public static void performSysAction(ActionType action) {
        if (mAccessibilityService == null)
        {
            return;
        }

        sleep(500);

        switch (action)
        {
            case BACK:
                mAccessibilityService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);

                break;
            case HOME:
                mAccessibilityService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);

                break;
            case RECENTS:
                mAccessibilityService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS);

                break;
            case NOTIFICATIONS:
                mAccessibilityService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS);

                break;

            case POWER:
                mAccessibilityService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_POWER_DIALOG);

                break;

            case SETTING:
                mAccessibilityService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_QUICK_SETTINGS);


                break;
            case SCROLL_BACKWARD:
                mAccessibilityService.performGlobalAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);


                break;
            case SCROLL_FORWARD:
                mAccessibilityService.performGlobalAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);


                break;
        }
    }

   //==================================================common================================================
    // =======================================================================================================

    public static void sleep(){
        sleep(500);
    }

    public static void sleep(long millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //click 操作
    public static void clickNodeInfo(AccessibilityNodeInfo nodeInfo){
        if (nodeInfo != null){
            if (nodeInfo.isClickable()){
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }else {

            }
        }
    }

    // Edit Text 操作
    public static void inputNodeText(AccessibilityNodeInfo nodeInfo, String input) {
        Bundle arguments = new Bundle();
        arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, input);
        nodeInfo.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
        nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT,arguments);
    }

    /**
     * 手势点击区域
     * @param nodeInfo
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void dispatchGestureClickNode(AccessibilityNodeInfo nodeInfo) {
        if(nodeInfo != null){
            Rect rect = new Rect();
            nodeInfo.getBoundsInScreen(rect);
            Log.d(TAG, "dispatchGestureClickNode: " + rect.centerX() + " " + rect.centerY());
            dispatchGestureView(rect.centerX(),rect.centerY());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private static void dispatchGestureView(int x, int y) {
        try {
            GestureDescription.Builder builder = new GestureDescription.Builder();
            Path p = new Path();
            p.moveTo(x, y);
            p.lineTo(x, y);
            builder.addStroke(new GestureDescription.StrokeDescription(p, 0L, 100L));
            GestureDescription gesture = builder.build();
            Log.d("", "点击了位置" + "(" + x + "," + y + ")");
            sleep(200);
            mAccessibilityService.dispatchGesture(gesture, new AccessibilityService.GestureResultCallback() {
            }, null);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 点击指定位置
     * 注意7.0以上的手机才有此方法，请确保运行在7.0手机上
     */
    @RequiresApi(24)
    public static void dispatchGestureClick(int x, int y) {
        Path path = new Path();
        path.moveTo(x, y);
        mAccessibilityService.dispatchGesture(new GestureDescription.Builder().addStroke(new GestureDescription.StrokeDescription
                (path, 0, 100)).build(), null, null);
    }

    /**
     * 手势滑动
     * @param sx
     * @param sy
     * @param ex
     * @param ey
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private static void dispatchGestureScroll(int sx, int sy, int ex, int ey) {
        GestureDescription.Builder builder = new GestureDescription.Builder();
        Path p = new Path();
        p.moveTo(sx, sy);
        p.lineTo(ex, ey);
        builder.addStroke(new GestureDescription.StrokeDescription(p, 0L, 100L));
        GestureDescription gesture = builder.build();
        mAccessibilityService.dispatchGesture(gesture, new AccessibilityService.GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);
                Log.d("", "onCompleted: 完成..........");
            }

            @Override
            public void onCancelled(GestureDescription gestureDescription) {
                super.onCancelled(gestureDescription);
                Log.d("", "onCompleted: 取消..........");
            }
        }, null);
    }

    /**
     * 立即发送移动的手势
     * 注意7.0以上的手机才有此方法，请确保运行在7.0手机上
     *
     * @param path  移动路径
     * @param mills 持续总时间
     * Path path = new Path();
     * path.moveTo(0, 400);
     * path.lineTo(400, 400);
     */
    @RequiresApi(24)
    public static void dispatchGestureMove(Path path, long mills) {
        mAccessibilityService.dispatchGesture(new GestureDescription.Builder().addStroke(new GestureDescription.StrokeDescription
                (path, 0, mills)).build(), null, null);
    }


    /**
     * Scroll Node
     */
    public static void scrollNode(AccessibilityNodeInfo nodeInfo) {
        while (nodeInfo != null) {
            if(nodeInfo.isScrollable()) {
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                break;
            }
            nodeInfo = nodeInfo.getParent();
        }

    }

    /**
     * Force Click Node
     */
    public static boolean performViewClick(AccessibilityNodeInfo nodeInfo) {
        boolean flg = false;
        if (nodeInfo == null)
        {
            return flg;
        }
        while (nodeInfo != null)
        {
            if (nodeInfo.isClickable())
            {
                flg = nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                break;
            }
            nodeInfo = nodeInfo.getParent();
        }
        return flg;
    }

    /**
     * 关闭软件盘,需要7.0版本
     */
    public static void closeKeyBoard() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            if (mAccessibilityService != null)
            {
                AccessibilityService.SoftKeyboardController softKeyboardController = mAccessibilityService.getSoftKeyboardController();
                int mode = softKeyboardController.getShowMode();
                if (mode == AccessibilityService.SHOW_MODE_AUTO)
                {
                    //如果软键盘开启,就关闭软件拍
                    softKeyboardController.setShowMode(AccessibilityService.SHOW_MODE_HIDDEN);
                }
            }
        }
    }


    /**
     * 获取节点所有包含的动作类型
     *
     * @param node
     */
    public static void getNodeActions(AccessibilityNodeInfo node) {

        List<AccessibilityNodeInfo.AccessibilityAction> accessibilityActions = node.getActionList();

        for (AccessibilityNodeInfo.AccessibilityAction action : accessibilityActions)
        {
            System.out.print(action.toString());
        }
    }


    //==================================================包装后的操作===============================

    /**
     * 根据Text搜索所有符合条件的节点, 模糊搜索方式
     */
    public static List<AccessibilityNodeInfo> findNodesByText(AccessibilityNodeInfo info, String text) {
        if (info != null)
        {
            List<AccessibilityNodeInfo> list = info.findAccessibilityNodeInfosByText(text);
            if (list != null || list.size() > 0)
            {
                return list;
            }
        }
        return null;

    }

    /**
     * 根据text查找并点击该节点
     *
     * @param text
     */
    public static boolean clickTextViewByText(AccessibilityNodeInfo info, String text) {
        boolean flg = false;
        if (info == null)
        {
            return flg;
        }
        List<AccessibilityNodeInfo> nodeInfoList = info.findAccessibilityNodeInfosByText(text);
        if (nodeInfoList != null && !nodeInfoList.isEmpty())
        {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList)
            {
                if (nodeInfo != null)
                {
                    flg = performViewClick(nodeInfo);
                    break;
                }
            }
        }
        return flg;
    }


    /**
     * 根据Id查找并点击该节点
     *
     * @param id
     * @return
     */
    public static boolean clickTextViewByID(AccessibilityNodeInfo info, String id) {
        boolean flg = false;
        if (info == null)
        {
            return flg;
        }
        List<AccessibilityNodeInfo> nodeInfoList = info.findAccessibilityNodeInfosByViewId(id);
        if (nodeInfoList != null && !nodeInfoList.isEmpty())
        {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList)
            {
                if (nodeInfo != null)
                {
                    flg = performViewClick(nodeInfo);
                    break;
                }
            }
        }

        return flg;
    }

    /**
     * 查找对应 viewName(className)的控件
     * @param info
     * @param viewName
     * @return
     */
    public static AccessibilityNodeInfo findNodeByViewName(AccessibilityNodeInfo info, String viewName) {
        String name = info.getClassName().toString();
        String[] split = name.split("\\.");
        name = split[split.length - 1];
        if (name.equals(viewName)) {
            return info;
        } else {
            int count = info.getChildCount();
            if (count > 0) {
                for (int i = 0; i < count; i++) {
                    AccessibilityNodeInfo inf = findNodeByViewName(info.getChild(i), viewName);
                    if (inf != null) {
                        return inf;
                    }
                }
            } else {
                return null;
            }
        }
        return null;
    }

    /**
     * 查找对应viewName 所有的结果
     * @param nodeInfoList
     * @param info
     * @param viewName
     */
    public static void findNodeListByViewName(List<AccessibilityNodeInfo> nodeInfoList, AccessibilityNodeInfo info, String viewName) {
        String name = info.getClassName().toString();
        String[] split = name.split("\\.");
        name = split[split.length - 1];
        if (name.equals(viewName)) {
            nodeInfoList.add(info);
        } else {
            int count = info.getChildCount();
            if (count > 0) {
                for (int i = 0; i < count; i++) {
                    findNodeListByViewName(nodeInfoList, info.getChild(i), viewName);
                }
            }
        }
    }


    /**
     * 查找对应文本的View
     *
     * @param text text
     * @return View
     */
    public static AccessibilityNodeInfo findViewByText(AccessibilityNodeInfo info, String text) {

        if (info == null)
        {
            return null;
        }
        List<AccessibilityNodeInfo> nodeInfoList = info.findAccessibilityNodeInfosByText(text);
        if (nodeInfoList != null && !nodeInfoList.isEmpty())
        {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList)
            {
                if (nodeInfo != null && nodeInfo.getText() != null && nodeInfo.getText().toString().equals(text))
                {
                    return nodeInfo;
                }
            }
        }
        return null;
    }


    /**
     * 查找对应ID的View
     *
     * @param id id
     * @return View
     */
    public static AccessibilityNodeInfo findViewByID(AccessibilityNodeInfo info, String id) {

        if (info == null)
        {
            return null;
        }
        List<AccessibilityNodeInfo> nodeInfoList = info.findAccessibilityNodeInfosByViewId(id);
        if (nodeInfoList != null && !nodeInfoList.isEmpty())
        {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList)
            {
                if (nodeInfo != null)
                {
                    return nodeInfo;
                }
            }
        }
        return null;
    }

    /**
     * 根据描述查找控件
     *
     * @param des
     * @return
     */
    public static AccessibilityNodeInfo findViewByDes(AccessibilityNodeInfo info, String des) {
        if (des == null || "".equals(des))
        {
            return null;
        }
        String desc = info.getContentDescription().toString();
        if (desc.equals(des)) {
            return info;
        } else {
            int count = info.getChildCount();
            if (count > 0) {
                for (int i = 0; i < count; i++) {
                    AccessibilityNodeInfo inf = findViewByDes(info.getChild(i), des);
                    if (inf != null) {
                        return inf;
                    }
                }
            } else {
                return null;
            }
        }
        return null;
    }


    //==================================================权限弹窗,广告弹窗等处理===============================

    /**
     * 自动查找启动广告的
     * “跳过”的控件
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void findSkipButtonByText(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo == null) return;
        for (int n = 0; n < keyWordList.size(); n++) {
            List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText(keyWordList.get(n));
            if (!list.isEmpty()) {
                for (AccessibilityNodeInfo info : list) {
                    if (!info.performAction(AccessibilityNodeInfo.ACTION_CLICK)) {
                        if (!info.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK)) {
                            PerformAction.dispatchGestureClickNode(info);
                        }
                    }
                    info.recycle();
                }
                return;
            }else{
                return;
            }

        }
//        nodeInfo.recycle();
    }


    /**
     * 自动处理权限申请的弹窗
     */
    public static void findSkipPermissionAlert() {


    }

















}
