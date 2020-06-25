package com.seany.appcaesar.event.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.util.SparseArray;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;

import androidx.annotation.RequiresApi;
import androidx.collection.SimpleArrayMap;

import com.seany.appcaesar.Config;
import com.seany.appcaesar.MainFunctions;
import com.seany.appcaesar.common.utils.AccessibilityUtil;
import com.seany.appcaesar.common.utils.StringUtil;
import com.seany.appcaesar.event.bean.UniversalNodeBean;
import com.seany.appcaesar.node.PerformAction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static com.seany.appcaesar.BuildConfig.DEBUG;

public class AccessibilityEventTracker {
    private static final String TAG ="AccessEventTracker";

    private WeakReference<AccessibilityListener> accessibilityListenerRef;

    private WeakReference<AccessibilityService> serviceRef;

//    private WeakReference<OperationService> operationRef;
    private long deviceStartTime;

    private boolean trackEvent = false;

    public static String currentPackageName = "com.seany.appcaesar";
    public String res = "";

    private PackageManager mPackageManager;

    private Long lastEventTimeStamp;
    private Long DELAY = 100L;
    public Handler handler = new Handler();
    public AccessibilityNodeInfo nodeInfo = null;

    private String mLastTraversedText = "";
    private final Object mLock = new Object();
    private String mLastActivityName = null;

    private ArrayList<Integer> pathList = new ArrayList<Integer>();

    /**
     * Accessibility事件监听器
     */
    public interface AccessibilityListener {
        void onAccessibilityEvent(long time, int eventType, AccessibilityNodeInfo node, String sourcePackage);
        void onGesture(int gesture);
    }

    /**
     * 获取系统启动的时间
     * @return
     */
    private long getMilliSecondsSinceDeviceStart() {
        if (deviceStartTime == 0L) {
            deviceStartTime = System.currentTimeMillis() - SystemClock.uptimeMillis();
        }

        return deviceStartTime;
    }

    public void setAccessibilityListener(AccessibilityListener accessibilityListener) {
        this.accessibilityListenerRef = new WeakReference<>(accessibilityListener);
    }

    public void startTrackEvent() {
        this.trackEvent = true;
    }

    public void stopTrackEvent() {
        this.trackEvent = false;
    }
    /**
     * 处理手势事件
     * @param gesture
     */
    protected void handleGestureEvent(int gesture) {
        if (trackEvent && accessibilityListenerRef.get() != null) {
            accessibilityListenerRef.get().onGesture(gesture);
        }
    }


    /**
     * 处理AccessibilityEvent
     * @param event
     */
    @SuppressLint("LongLogTag")
    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void handleAccessibilityEvent(final AccessibilityEvent event, final Context context, final AccessibilityService service) {
        //变量赋值
        String packageName = (String) event.getPackageName();
        int eventType = event.getEventType();
        mPackageManager = context.getPackageManager();
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("dd MMM, hh:mm ");
        String time = df.format(Calendar.getInstance().getTime());
        long currentTime = System.currentTimeMillis();
        PerformAction.keyWordList.add("跳过");

        //满足测试时间 停止service
        if ((currentTime - MainFunctions.testStartTime) > Config.testTime) {
            AccessibilityServiceImpl.mainFunctions.handler.sendEmptyMessage(0x04);
        }

        if (packageName == null || !trackEvent){return;}

        if (!packageName.equals(currentPackageName)) {
            currentPackageName = packageName;
            Log.e("currentPackageName :", currentPackageName);

        }

        //保持待测App在前台
        if(!packageName.equals(Config.packageName)){
            Log.e(Config.TAG, "package is't " + Config.packageName);
            AccessibilityUtil.collapsingNotification(context);
            PendingIntent pendingIntent =
                    PendingIntent.getActivity(context,0,AccessibilityUtil.LaunchApp(mPackageManager,Config.packageName),0);
            try{
                pendingIntent.send();
            }catch (PendingIntent.CanceledException e){
                e.printStackTrace();
            }
            return;
        }


        //找到跳过 并点击
        PerformAction.findSkipButtonByText(service.getRootInActiveWindow());
        //关闭软键盘
        PerformAction.closeKeyBoard();
        //处理权限弹窗

        //debug 所有 event
//        AccessibilityUtil.printAccessibilityEventLog(event);
        // 如果是等待窗口变化模式
        // 需要手动处理弹窗
        // 监听事件并代理->EventBean
//        if (trackEvent && accessibilityListenerRef.get() != null) {
//            accessibilityListenerRef.get().onAccessibilityEvent(
//                    event.getEventTime() + getMilliSecondsSinceDeviceStart(),
//                    eventType, event.getSource(), StringUtil.toString(event.getPackageName()));
//        }
        if(service !=null){
            readWindow(service);
        }
        ArrayMap<String, UniversalNodeBean> nodeInfoArrayMap = new ArrayMap<String, UniversalNodeBean>();
        StringBuilder path = new StringBuilder();
        if (nodeInfo != null) {
            traverseNodeDFS(nodeInfo,path,nodeInfoArrayMap);
//            traverseNodePathDFS(nodeInfo, path, performAction);
            nodeInfo.recycle();
        }
        Log.d(TAG, "handleAccessibilityEvent: " + nodeInfoArrayMap);
        if (nodeInfoArrayMap.size()>0) {
            for (int i=0;i<nodeInfoArrayMap.size();i++) {
                AccessibilityNodeInfo curNodeInfo = getNodeInfoWeightRandom(nodeInfoArrayMap);
                Log.d(TAG, "CurrentRandomNodeInfo: " + curNodeInfo);
                if (!curNodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK)) {
                    PerformAction.dispatchGestureClickNode(curNodeInfo);
                }
                PerformAction.sleep();
                curNodeInfo.recycle();
            }
        }

        nodeInfoArrayMap.clear();

        switch (eventType) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                // don't trust event.getText(), check for nulls
                if (event.getText() != null && event.getText().size() > 0) {
                    if(event.getText().get(0) != null)
                        mLastActivityName = event.getText().get(0).toString();
                }
                break;
            case AccessibilityEvent.TYPE_VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY:
                // don't trust event.getText(), check for nulls
                if (event.getText() != null && event.getText().size() > 0)
                    if(event.getText().get(0) != null)
                        mLastTraversedText = event.getText().get(0).toString();
                if (DEBUG)
                    Log.d(TAG, "Last text selection reported: " +
                            mLastTraversedText);
                break;
            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED: {
                String data = event.getText().toString();

                data=time + "|(TEXT)|" + data;
                res = res + data + "\n";

                Log.v("TYPE_VIEW_TEXT_CHANGED: ", time + "|(TEXT)|" + data);
                break;
            }
            case AccessibilityEvent.TYPE_VIEW_FOCUSED: {
                String data = event.getText().toString();
                data=time + "|(FOCUSED)|" + data;
                res = res + data + "\n";

                Log.v("TYPE_VIEW_FOCUSED: ", time + "|(FOCUSED)|" + data);
                break;
            }
            case AccessibilityEvent.TYPE_VIEW_LONG_CLICKED:
            case AccessibilityEvent.TYPE_VIEW_CONTEXT_CLICKED:
            case AccessibilityEvent.TYPE_VIEW_CLICKED: {
                String data = event.getText().toString();
                data=time + "|(CLICKED)|" + event.getText().toString() + data;
                res = res + data + "\n";

                Log.v("TYPE_VIEW_CLICKED: ", time + "|(CLICKED)|" + event.getPackageName().toString() + data);

                if (res.length() > 0) {
                    try {
                        File file = new File(context.getExternalFilesDir(null), "Log.txt");
                        FileOutputStream fos = new FileOutputStream(file, true);
                        fos.write(res.getBytes());
                        fos.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    res = "";
                }

                break;
            }
            default:
                break;
        }


    }

    private void readWindow(AccessibilityService service) {
        try {
            nodeInfo = service.getRootInActiveWindow();
            if (nodeInfo == null) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //判断条件?
                    }
                },DELAY);
                nodeInfo = service.getRootInActiveWindow();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 路径唯一性算法
     * @param nodeInfo
     * @param path
     * @param performAction
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void traverseNodePathDFS(AccessibilityNodeInfo nodeInfo, StringBuilder path, PerformAction performAction) {
        if (TextUtils.isEmpty(nodeInfo.getClassName()))return;
        if (nodeInfo.getChildCount() == 0){
            Log.d(TAG, "traverseNodeDFS: " + nodeInfo.getText() + "  "+ nodeInfo.getClassName() + " "+nodeInfo.getTraversalBefore());
            Log.i(TAG, "Click"+",isClick:"+nodeInfo.isClickable());
            path.append(nodeInfo.getClassName().toString());
            int pathHashCode = path.toString().hashCode() & Integer.MAX_VALUE;
//            Log.d(TAG, "path: " + path);
            Log.d(TAG, "pathHash: " + pathHashCode);
            if (!pathList.contains(pathHashCode)) {
                pathList.add(pathHashCode);
//                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                performAction.dispatchGestureClickNode(nodeInfo);
            }
            AccessibilityNodeInfo parent = nodeInfo.getParent();
            String parentPath = path.substring(0,path.lastIndexOf("/"));
            while(parent != null){
                Log.d(TAG, "traverseNodeDFS-parent: " + parent.getClassName());
                Log.i(TAG, "parent isClick:" + parent.isClickable());
//                Log.d(TAG, "parent path: " + parentPath);
                int parentPathHashCode = parentPath.hashCode() & Integer.MAX_VALUE;
                if(parent.isClickable() && !pathList.contains(parentPathHashCode)){
                    pathList.add(parentPathHashCode);
//                    performAction.dispatchGestureClickNode(parent);
                    parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    break;
                }
                parent = parent.getParent();
                if (parent != null) {
                    parentPath = parentPath.substring(0, parentPath.lastIndexOf("/"));
                }
            }
        }else {
            for (int i = 0; i < nodeInfo.getChildCount(); i++) {
                path.append(nodeInfo.getClassName().toString()).append(i).append("/");
                if (nodeInfo.getChild(i) != null) {
                    traverseNodePathDFS(nodeInfo.getChild(i),path, performAction);
                }
            }
        }
    }

    /**
     * 添加权重属性
     * @param nodeInfo
     * @param path
     * @param nodeMap
     */
    private void traverseNodeDFS(AccessibilityNodeInfo nodeInfo, StringBuilder path, ArrayMap<String, UniversalNodeBean> nodeMap) {
        if (nodeInfo==null || TextUtils.isEmpty(nodeInfo.getClassName()))return;
        if (nodeInfo.getChildCount() == 0){
            path.append(nodeInfo.getClassName().toString());

//            universalNodeBean.setWeight(1);
            if(nodeInfo.isClickable() || nodeInfo.isCheckable() || nodeInfo.isEditable() || nodeInfo.isLongClickable() || nodeInfo.isVisibleToUser()) {
                UniversalNodeBean universalNodeBean = new UniversalNodeBean();
                universalNodeBean.setNodeInfo(nodeInfo);
                universalNodeBean.setWeight(1);
                nodeMap.put(path.toString(),universalNodeBean);
            }

            AccessibilityNodeInfo parent = nodeInfo.getParent();
            String parentPath = path.substring(0,path.lastIndexOf("/"));
            while(parent != null){
                if(parent.isClickable() || parent.isCheckable() || parent.isEditable() || parent.isLongClickable() || parent.isVisibleToUser()){
                    UniversalNodeBean parentNodeBean = new UniversalNodeBean();
                    parentNodeBean.setNodeInfo(parent);
                    parentNodeBean.setWeight(1);
                    nodeMap.put(parentPath,parentNodeBean);
                    break;
                }
                parent = parent.getParent();
                if (parent != null) {
                    parentPath = parentPath.substring(0, parentPath.lastIndexOf("/"));
                }
            }
        }else {
            for (int i = 0; i < nodeInfo.getChildCount(); i++) {
                path.append(nodeInfo.getClassName().toString()).append(i).append("/");
                if (nodeInfo.getChild(i) != null) {
                    traverseNodeDFS(nodeInfo.getChild(i),path,nodeMap);
                    nodeInfo.recycle();
                }
            }
        }
    }

    private AccessibilityNodeInfo getNodeInfoWeightRandom(ArrayMap<String, UniversalNodeBean> nodeMap) {
        ArrayMap<String, UniversalNodeBean> weightMap = new ArrayMap<String, UniversalNodeBean>();
        weightMap.putAll(nodeMap);
        Iterator<String> it = weightMap.keySet().iterator();
        List<String> pathList = new ArrayList<String>();
        while (it.hasNext()) {
            String path = it.next();
            Integer weight = Objects.requireNonNull(weightMap.get(path)).getWeight();
            for (int i=0; i<weight; i++) {
                pathList.add(path);
            }
        }
        Random random = new Random();
        int randomPos = random.nextInt(pathList.size());

        String path = pathList.get(randomPos);
        return Objects.requireNonNull(weightMap.get(path)).getNodeInfo();
    }

    private void generateUniquelyIdentifiable (AccessibilityNodeInfo info) {
        StringBuilder uni = new StringBuilder();
        if (info == null)return;
        Rect rect = new Rect();
        info.getBoundsInScreen(rect);
        uni.append(info.getClassName().toString()).append(String.valueOf(rect.hashCode()));
        Log.d(TAG, "generateUniquelyIdentifiable: " + uni);

    }


}
