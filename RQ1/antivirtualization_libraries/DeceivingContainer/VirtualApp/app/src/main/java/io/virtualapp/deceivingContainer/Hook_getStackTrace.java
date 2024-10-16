package io.virtualapp.deceivingContainer;

import android.util.Log;

import io.virtualapp.splash.SplashActivity;

public class Hook_getStackTrace {
    public static String className = "java.lang.Throwable";
    public static String methodName = "getStackTrace";
    public static String methodSig =  "()[Ljava/lang/StackTraceElement;";

    public static StackTraceElement[] hook(Object thiz) {
        Log.d(SplashActivity.TAG, "[HOOK] getStackTrace");
        StackTraceElement[] st = backup(thiz);
        for(int i = 0; i < st.length; i++) {
            if (st[i].getClassName().contains("lody.virtual"))
                st[i] = new StackTraceElement(st[0].getClassName(), st[0].getMethodName(), st[0].getFileName(), st[0].getLineNumber());
        }
        return st;
    }

    public static StackTraceElement[] backup(Object thiz) {
        Log.w(SplashActivity.TAG, "Problem?");
        return null;
    }
}
