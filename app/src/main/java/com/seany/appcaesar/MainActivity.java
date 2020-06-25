package com.seany.appcaesar;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;
import com.seany.appcaesar.common.utils.AdbUtils;
import com.seany.appcaesar.event.accessibility.AccessibilityServiceImpl;


public class MainActivity extends Activity {

    private PackageManager mPackageManager;
    private String packname;
    static String TAG = "AppCaesar MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        try {

            Context context = getApplicationContext();
            AdbUtils.grantAdbPermission(context);
//            String line = AdbUtils.execAdbCmd("cat /proc/stat", 0);
//            String result = line.substring(0, line.indexOf('\n')).trim();
//            Log.d(TAG, "CPU Line: " + result);

            if (AccessibilityServiceImpl.mainFunctions == null) {
                Intent intent_abs = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                intent_abs.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent_abs);
                Toast.makeText(context, "请打开无障碍服务(AppCaesar)", Toast.LENGTH_LONG).show();
            }else{
                AccessibilityServiceImpl.mainFunctions.handler.sendEmptyMessage(0x00);
//                Toast.makeText(context, "已打开无障碍服务(AppCaesar)", Toast.LENGTH_SHORT).show();
            }

            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                Toast.makeText(context, "请授予读写手机存储权限，,并设置允许后台运行", Toast.LENGTH_SHORT).show();
            }

            if (!Settings.canDrawOverlays(context)) {
                Intent intent_dol = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                intent_dol.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ResolveInfo resolveInfo = getPackageManager().resolveActivity(intent_dol, PackageManager.MATCH_ALL);
                if (resolveInfo != null) {
                    startActivity(intent_dol);
                } else {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                Toast.makeText(context, "请授予应用悬浮窗权限,并设置允许后台运行", Toast.LENGTH_SHORT).show();
            }

            if (!((PowerManager) getSystemService(POWER_SERVICE)).isIgnoringBatteryOptimizations(getPackageName())) {
                @SuppressLint("BatteryLife") Intent intent_ibo = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, Uri.parse("package:" + getPackageName()));
                intent_ibo.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ResolveInfo resolveInfo = getPackageManager().resolveActivity(intent_ibo, PackageManager.MATCH_ALL);
                if (resolveInfo != null)
                    startActivity(intent_ibo);
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        finishAndRemoveTask();
    }
}
