package io.virtualapp.deceivingContainer;

import android.util.Log;

import java.io.File;

import io.virtualapp.splash.SplashActivity;

public class Hook_File {
    public static String className = "java.io.File";
    public static String methodName = "<init>";
    public static String methodSig =  "(Ljava/lang/String;)V";

    public static void hook(File thiz, String path) {
        Log.d(SplashActivity.TAG, "[HOOK] File constructor: "+path);
        if(path.equals("/proc/self/maps"))
            backup(thiz, SplashActivity.DECOY_FILE);
        else
            backup(thiz, path);
    }

    public static void backup(File thiz, String path) {
        Log.w(SplashActivity.TAG, "Problem?");
    }
}