package io.virtualapp.deceivingContainer;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.util.Log;

import java.util.List;

import io.virtualapp.splash.SplashActivity;

public class Hook_getRunningServices {
    public static String className = "android.app.ActivityManager";
    public static String methodName = "getRunningServices";
    public static String methodSig =  "(I)Ljava/util/List;";

    public static List<ActivityManager.RunningServiceInfo> hook(Object thiz, int maxNum) {
        Log.d(SplashActivity.TAG, "[HOOK] getRunningServices");
        List<ActivityManager.RunningServiceInfo> list = backup(thiz, maxNum);
        for(ActivityManager.RunningServiceInfo service : list) {
            if(!service.service.toString().contains("com.matrioska.vedetection.antiplugin.TheService")) {
                service.service = new ComponentName("com.matrioska.vedetection", "com.matrioska.vedetection.antiplugin.TheService");
            }
        }
        return list;
    }

    public static List<ActivityManager.RunningServiceInfo> backup(Object thiz, int maxNum) {
        Log.w(SplashActivity.TAG, "Problem?");
        return null;
    }
}
