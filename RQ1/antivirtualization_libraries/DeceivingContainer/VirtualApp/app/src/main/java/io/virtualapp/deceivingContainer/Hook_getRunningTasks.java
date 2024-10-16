package io.virtualapp.deceivingContainer;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.util.Log;

import java.util.List;

import io.virtualapp.splash.SplashActivity;

public class Hook_getRunningTasks {
    public static String className = "android.app.ActivityManager";
    public static String methodName = "getRunningTasks";
    public static String methodSig =  "(I)Ljava/util/List;";

    public static List<ActivityManager.RunningTaskInfo> hook(Object thiz, int maxNum) {
        Log.d(SplashActivity.TAG, "[HOOK] getRunningTasks");
        List<ActivityManager.RunningTaskInfo> list = backup(thiz, maxNum);
        for(ActivityManager.RunningTaskInfo task : list) {
            if(!task.baseActivity.getPackageName().equals("com.matrioska.vedetection")) {
                task.baseActivity = new ComponentName("com.matrioska.vedetection", "com.matrioska.vedetection.MainActivity");
            }
        }
        return list;
    }

    public static List<ActivityManager.RunningTaskInfo> backup(Object thiz, int maxNum) {
        Log.w(SplashActivity.TAG, "Problem?");
        return null;
    }
}
