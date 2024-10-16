package com.example.dynamicVAdetector;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.app.Activity;

public class Hook_onCreate {
    public static String className = "android.app.Activity";
    public static String methodName = "onCreate";
    public static String methodSig =  "(Landroid/os/Bundle;)V";

    public static void hook(Object thiz, Bundle b) {
        backup(thiz, b);
        Activity c = (Activity)thiz;
        Log.d("VADetector", "[*] onCreate: "+c.getPackageName());
        DataCollector.addApplication(c.getPackageName());
        try {
            PackageInfo pi = c.getPackageManager().getPackageInfo(c.getPackageName(), PackageManager.GET_PERMISSIONS);
            int n = 0;
            if(pi.requestedPermissions != null) n = pi.requestedPermissions.length;
            //Log.d("VADetector", "[*] Permissions: "+n);
            DataCollector.addPermissions(n);

            PackageInfo pia = c.getPackageManager().getPackageInfo(c.getPackageName(), PackageManager.GET_ACTIVITIES);
            PackageInfo pis = c.getPackageManager().getPackageInfo(c.getPackageName(), PackageManager.GET_SERVICES);
            PackageInfo pir = c.getPackageManager().getPackageInfo(c.getPackageName(), PackageManager.GET_RECEIVERS);
            int na = 0; int ns = 0; int nr = 0;
            if(pia.activities != null) na = pia.activities.length;
            if(pis.services != null) ns = pis.services.length;
            if(pir.receivers != null) nr = pir.receivers.length;
            DataCollector.addComponents(na, na+ns+nr);
            for(int i = 0; i < na; i++) {
                //Log.d("VADetector", "[*] Activity: "+pia.activities[i].name+" - "+pia.activities[i].processName);
                DataCollector.addComponent(pia.activities[i].name, pia.activities[i].processName);
            }
            for(int i = 0; i < ns; i++) {
                DataCollector.addComponent(pis.services[i].name, pis.services[i].processName);
            }
            for(int i = 0; i < nr; i++) {
                DataCollector.addComponent(pir.receivers[i].name, pir.receivers[i].processName);
            }

            DataCollector.addAssetsApk(c.getAssets());
        } catch(Exception e) {
            Log.e("VADetector", "exception: ", e);
        }
    }

    public static void backup(Object thiz, Bundle b) {
        try {
            Log.w("VADetector", "Problem?");
        }
        catch (Exception e) {}
    }
}
