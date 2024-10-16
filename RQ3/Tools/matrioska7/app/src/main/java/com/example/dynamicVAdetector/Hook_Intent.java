package com.example.dynamicVAdetector;

import android.content.Intent;
import android.util.Log;

public class Hook_Intent {
    public static String className = "android.content.Intent";
    public static String methodName = "setClassName";
    public static String methodSig =  "(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;";

    public static Intent hook(Object thiz, String a, String b) {
        int uid = android.os.Process.myUid();
        Log.w("VADetector", "[*] setClassName - Param1: "+a+" Param2: "+b+" - UID: " + uid);
        Intent i = backup(thiz, a, b);
        Log.d("VADetector", i.toUri(0));
        return i;
    }

    public static Intent backup(Object thiz, String a, String b) {
        try {
            Log.w("VADetector", "Problem?");
        }
        catch (Exception e) {}
        return null;
    }
}
