package io.virtualapp.deceivingContainer;

import android.app.ActivityManager;
import android.util.Log;

import java.util.List;

import io.virtualapp.splash.SplashActivity;

public class Hook_getRunningAppProcesses {
    public static String className = "android.app.ActivityManager";
    public static String methodName = "getRunningAppProcesses";
    public static String methodSig =  "()Ljava/util/List;";

    public static List<ActivityManager.RunningAppProcessInfo> hook(Object thiz) {
        Log.d(SplashActivity.TAG, "[HOOK] getRunningAppProcesses");
        List<ActivityManager.RunningAppProcessInfo> list = backup(thiz);
        for(ActivityManager.RunningAppProcessInfo appProcess : list) {
            appProcess.processName = "com.matrioska.vedetection";
        }
        return list;
    }

    public static List<ActivityManager.RunningAppProcessInfo> backup(Object thiz) {
        Log.w(SplashActivity.TAG, "Problem?");
        return null;
    }
}