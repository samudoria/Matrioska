package com.example.dynamicVAdetector;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

public class Hook_Application {
    public static String className = "android.app.Application";
    public static String methodName = "attachBaseContext";
    public static String methodSig =  "(Landroid/content/Context;)V";

    public static void hook(Object thiz, Context c) {
        ParallelHook ph = new ParallelHook(c);
        ph.start();
        backup(thiz, c);
    }

    public static void backup(Object thiz, Context c) {
        try {
            Log.w("VADetector", "Problem?");
        }
        catch (Exception e) {}
    }


    private static class ParallelHook extends Thread {
        private Context c;
        public ParallelHook(Context c) {
            this.c = c;
        }
        public void run() {
            Log.d("VADetector", "[*] attachBaseContext: "+c.getPackageName());
            DataCollector.addApplication(c.getPackageName());
            PackageInfo pip = null;
            PackageInfo pia = null;
            PackageInfo pis = null;
            PackageInfo pir = null;
            try {
                pip = c.getPackageManager().getPackageInfo(c.getPackageName(), PackageManager.GET_PERMISSIONS);
                pia = c.getPackageManager().getPackageInfo(c.getPackageName(), PackageManager.GET_ACTIVITIES);
                pis = c.getPackageManager().getPackageInfo(c.getPackageName(), PackageManager.GET_SERVICES);
                pir = c.getPackageManager().getPackageInfo(c.getPackageName(), PackageManager.GET_RECEIVERS);
            } catch(Exception e) {
                Log.e("VADetector", "Exception: ", e);
            }
            int np = 0; int na = 0; int ns = 0; int nr = 0;
            if(pip != null && pip.requestedPermissions != null) np = pip.requestedPermissions.length;
            DataCollector.addPermissions(np);
            if(pia != null && pia.activities != null) na = pia.activities.length;
            if(pis != null && pis.services != null) ns = pis.services.length;
            if(pir != null && pir.receivers != null) nr = pir.receivers.length;
            DataCollector.addComponents(na, na+ns+nr);
            for(int i = 0; i < na; i++) {
                DataCollector.addComponent(pia.activities[i].name, pia.activities[i].processName);
            }
            for(int i = 0; i < ns; i++) {
                DataCollector.addComponent(pis.services[i].name, pis.services[i].processName);
            }
            for(int i = 0; i < nr; i++) {
                DataCollector.addComponent(pir.receivers[i].name, pir.receivers[i].processName);
            }
            DataCollector.addAssetsApk(c.getAssets());
        }
    }

}
