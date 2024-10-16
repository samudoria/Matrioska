package io.virtualapp.deceivingContainer;

import android.content.Context;
import android.util.Log;

import io.virtualapp.splash.SplashActivity;

public class Hook_PackageCodePath {
    public static String className = "android.app.ContextImpl";
    public static String methodName = "getPackageCodePath";
    public static String methodSig =  "()Ljava/lang/String;";

    public static String hook(Object thiz) {
        Log.d(SplashActivity.TAG, "[HOOK] Context.getPackageCodePath");
        return "/data/app/com.matrioska.vedetection-1/base.apk";
    }

    public static String backup(Object thiz) {
        Log.w(SplashActivity.TAG, "Problem?");
        return null;
    }
}

//public class Hook_PackageCodePath {
//    public static String className = "com.matrioska.vedetection.diprint.DiPrint";
//    public static String methodName = "checkAPKCodeLoadingPath";
//    public static String methodSig =  "(Landroid/content/Context;)Ljava/lang/String;";
//
//    public static String hook(Object thiz, Context c) {
//        Log.d(MainActivity.TAG, "[HOOK] checkAPKCodeLoadingPath "+c.getClass());
//        return backup(thiz, c);
//    }
//
//    public static String backup(Object thiz, Context c) {
//        Log.w(MainActivity.TAG, "Problem?");
//        return null;
//    }
//}
