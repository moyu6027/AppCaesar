package com.seany.appcaesar;

import android.accessibilityservice.AccessibilityService;
import androidx.annotation.NonNull;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.seany.appcaesar.charts.CpuActivity;
import com.seany.appcaesar.event.EventService;
import com.seany.appcaesar.event.accessibility.AccessibilityServiceImpl;

public class MainFunctions {
    public Handler handler;
    private AccessibilityService service;
    private PackageManager packageManager;
    private String packageName;
    private WindowManager windowManager;
    public static long testStartTime;

    public MainFunctions(AccessibilityService service) {
        this.service = service;
        windowManager = (WindowManager) service.getSystemService(AccessibilityService.WINDOW_SERVICE);
    }

    public void onServiceConnected() {
        packageName = service.getPackageName();
        try{
            handler = new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(@NonNull Message msg) {
                    switch (msg.what) {
                        case 0x00:
                            mainUI();
                            break;
                        case 0x04:
                            service.getSystemService(EventService.class).stopSelf();
                            final Intent startIntent = new Intent(service, EventService.class);
                            service.stopService(startIntent);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                service.disableSelf();
                            }
                            break;
                    }
                    return true;
                }
            });
        }catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查包是否存在
     *
     * @param packname
     * @return
     */
    private boolean checkPackInfo(String packname) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = service.getPackageManager().getPackageInfo(packname, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageInfo != null;
    }

    /**
     * 用于设置主要UI界面
     */
    private void mainUI() {
        final DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getRealMetrics(metrics);
        final boolean b = metrics.heightPixels > metrics.widthPixels;
        final int width = b ? metrics.widthPixels : metrics.heightPixels;
        final int height = b ? metrics.heightPixels : metrics.widthPixels;
        final LayoutInflater inflater = LayoutInflater.from(service);
        @SuppressLint("InflateParams") final View view_main = inflater.inflate(R.layout.main_dialog, null);
        final AlertDialog dialog_main = new AlertDialog.Builder(service).setTitle(R.string.simple_name).setIcon(R.drawable.caesar).setCancelable(false).setView(view_main).create();
        TextView bt_set = view_main.findViewById(R.id.set);
        TextView bt_look = view_main.findViewById(R.id.look);
        TextView bt_cancel = view_main.findViewById(R.id.cancel);
        TextView bt_charts = view_main.findViewById(R.id.charts);
        Button bt_start_test = view_main.findViewById(R.id.start_button);
        Button bt_monkey_test = view_main.findViewById(R.id.monkey_button);
        final Context context = service.getApplicationContext();

        //测试相关按钮绑定事件
        bt_start_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Start walking through the test immediately", Toast.LENGTH_SHORT).show();
                //启动 EventService
                final Intent startIntent = new Intent(service, EventService.class);
                service.startService(startIntent);
                //启动待测app
                new Handler().post(new Runnable() {
                    @SuppressLint({"WrongConstant", "ShowToast"})
                    @Override
                    public void run() {
                        packageManager = service.getPackageManager();
                        String packname = Config.packageName;
                        if (checkPackInfo(packname)) {
                            testStartTime = System.currentTimeMillis();
                            Intent intent = packageManager.getLaunchIntentForPackage(packname);
                            service.startActivity(intent);
                        } else {
                            Toast.makeText(service, "没有安装" + packname, 1).show();
                            AccessibilityServiceImpl.mainFunctions.handler.sendEmptyMessage(0x04);
                        }
                    }
                });
                dialog_main.dismiss();
            }
        });

        bt_monkey_test.setOnClickListener(new View.OnClickListener() {
            @SuppressLint({"WrongConstant", "ShowToast"})
            @Override
            public void onClick(View v) {
                Toast.makeText(service, "Coming Soon!",1).show();
            }
        });


        //功能按钮绑定事件

        bt_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + packageName));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                service.startActivity(intent);
                dialog_main.dismiss();
            }
        });
        bt_look.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(service, HelpActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                service.startActivity(intent);
                dialog_main.dismiss();
            }
        });
        bt_charts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(service, ChartActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                service.startActivity(intent);
                dialog_main.dismiss();
            }
        });
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_main.dismiss();
            }
        });

        Window win = dialog_main.getWindow();
        assert win != null;
        win.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        win.setBackgroundDrawableResource(R.drawable.dialog_shape);
        win.setType(WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY);
        win.setDimAmount(0);
        dialog_main.show();
        WindowManager.LayoutParams params = win.getAttributes();
        params.width = (width / 6) * 5;
        win.setAttributes(params);
    }
}
