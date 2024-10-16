package io.virtualapp.deceivingContainer;

import android.util.Log;

import io.virtualapp.splash.SplashActivity;

public class Hook_RuntimeExec {
    public static String className = "java.lang.Runtime";
    public static String methodName = "exec";
    public static String methodSig =  "(Ljava/lang/String;)Ljava/lang/Process;";

    public static Process hook(Object thiz, String command) {
        Log.d(SplashActivity.TAG, "[HOOK] Runtime.exec: "+command);
        return backup(thiz, "ls");
    }

    public static Process backup(Object thiz, String command) {
        Log.w(SplashActivity.TAG, "Problem?");
        return null;
    }
}
