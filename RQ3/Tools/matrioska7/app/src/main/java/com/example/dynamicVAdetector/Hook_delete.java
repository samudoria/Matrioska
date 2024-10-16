package com.example.dynamicVAdetector;

import android.util.Log;
import java.io.File;
import java.io.FileInputStream;

public class Hook_delete {
    public static String className = "java.io.File";
    public static String methodName = "delete";
    public static String methodSig =  "()Z";

    public static boolean hook(Object thiz) {
        Log.d("VADetector", "[*] delete: "+((File)thiz).getName());
        try {
            if (MainActivity.isApk(new FileInputStream((File)thiz)))
                DataCollector.addFoundApk((File)thiz);
        } catch(Exception e) {}
        return backup(thiz);
    }

    public static boolean backup(Object thiz) {
        try {
            Log.w("VADetector", "Problem?");
        }
        catch (Exception e) {}
        return false;
    }
}