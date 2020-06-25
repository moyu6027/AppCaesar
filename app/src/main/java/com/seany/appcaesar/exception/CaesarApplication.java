package com.seany.appcaesar.exception;

import androidx.annotation.NonNull;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CaesarApplication extends Application implements Thread.UncaughtExceptionHandler {

    public static final String TAG = "Caesar CrashHandler";
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private Context context;
    //用来存储设备信息和异常信息
    private Map<String, String> infos = new HashMap<String, String>();
    protected static CaesarApplication appInstance;

    //用于格式化日期,作为日志文件名的一部分
    @SuppressLint("SimpleDateFormat")
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");


    @Override
    public void onCreate() {
        super.onCreate();
        appInstance = this;
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public static CaesarApplication getInstance() {
        return appInstance;
    }

    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable ex) {

        Intent intent = new Intent(this, getTopActivity());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    /**
     * 获取栈中最顶部的Activity，即最后发生崩溃的Activity。
     * 如果你只需要打开MainActivity等固定的Activity则无需使用此方法
     */
    public Class getTopActivity() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        String className = Objects.requireNonNull(manager.getRunningTasks(1).get(0).topActivity).getClassName();
        Class cls = null;
        try {
            cls = Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return cls;
    }
}
