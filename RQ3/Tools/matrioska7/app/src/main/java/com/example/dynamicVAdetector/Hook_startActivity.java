package com.example.dynamicVAdetector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;

import java.util.List;

public class Hook_startActivity {
    public static String className = "android.app.Activity";
    public static String methodName = "startActivity";
    public static String methodSig =  "(Landroid/content/Intent;)V";

    public static void hook(Object thiz, Intent i) {
        Log.d("VADetector", "[*] startActivity: "+i.toUri(0));
        DataCollector.addIntent(i.getAction(), i.getCategories(), i.getPackage(), i.getComponent().getPackageName(), i.getComponent().getClassName());
//        Context c = ((Activity)thiz).getApplicationContext();
//        List<ResolveInfo> res = c.getPackageManager().queryIntentActivities(i, PackageManager.MATCH_ALL);
//        if(res.isEmpty()) {
//            DataCollector.addIntent(i.getAction(), i.getCategories(), i.getPackage(), i.getComponent().getPackageName());
//        }
//        else {
//            for(ResolveInfo r : res) {
//                Log.d("VADetector", "[*] queryIntentActivities: " + r.activityInfo.packageName);
//            }
//        }
        backup(thiz, i);
    }

    public static void backup(Object thiz, Intent i) {
        try {
            Log.w("VADetector", "Problem?");
        }
        catch (Exception e) {}
    }
}
