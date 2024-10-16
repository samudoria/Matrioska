package io.virtualapp.deceivingContainer;

import android.util.Log;

import java.io.File;

import io.virtualapp.splash.SplashActivity;

public class Hook_getDataDir {
    public static String className = "android.app.ContextImpl";
    public static String methodName = "getDataDir";
    public static String methodSig =  "()Ljava/io/File;";

    public static File hook(Object thiz) {
        Log.d(SplashActivity.TAG, "[HOOK] Context.getDataDir");
        File f = new File("/data/user/0/com.matrioska.vedetection");
        return f;
    }

    public static File backup(Object thiz) {
        Log.w(SplashActivity.TAG, "Problem?");
        return null;
    }
}
