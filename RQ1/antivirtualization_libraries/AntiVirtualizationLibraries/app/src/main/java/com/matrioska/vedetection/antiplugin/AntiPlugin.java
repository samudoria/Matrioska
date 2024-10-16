package com.matrioska.vedetection.antiplugin;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.os.SystemClock;
import android.util.Log;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AntiPlugin {

    private static final String RTAG ="VE-DETECTION-ANTIPLUGIN";
    protected static final String dynACTION  = "com.android.broadcast.ANTI_DYN";
    protected static final String staticACTION = "com.android.broadcast.ANTI_STATIC";
    TheReceiver rhelper;

    public List<String> detect(Context c){

        rhelper=new TheReceiver(c);
        Log.v(RTAG, "      [DummyReceiver] Register Action [" + dynACTION + "]");
        rhelper.registerAction(dynACTION);

        String pkgName = c.getApplicationContext().getPackageName();

        Log.i(RTAG,"Package name: " + pkgName);

        List<String> res = new ArrayList<String>();
        res.add(undeclaredPermissionCheck(c,c.getPackageManager()));
        ApplicationInfo ai = listAll(c,c.getPackageManager());
        res.add("SourceDir: " + ai.sourceDir + "\nprocessName: " + ai.processName + "\ndataDir: " + ai.dataDir + "\npublicSourceDir: " + ai.publicSourceDir + "\nnativeLibraryDir: " + ai.nativeLibraryDir);
        res.add(getCurrentProcessInfo3(c));
        res.add(runningServices((ActivityManager) c.getSystemService(Context.ACTIVITY_SERVICE)));
        res.add(checkUIDProcess(c,(ActivityManager) c.getSystemService(Context.ACTIVITY_SERVICE),pkgName));
        checkUnregisteredFilter(c);
        checkEnabledComp(c);

        return res;
    }

    /**DETECTION MECHANISM 1*/

    private String undeclaredPermissionCheck(Context context, PackageManager pm){
        Log.w(RTAG, " ============================== [Package-Check] [undeclaredPermissionCheck] Begin ============================== ");
        boolean found_undeclared = false;
        List<String> requestedPerms = getDeclaredPermissions(context, pm);
        List<String> allPerms = getAllPermissions(pm);
        allPerms.removeAll(requestedPerms);
        for (String perm : allPerms) {
            if(ContextCompat.checkSelfPermission(context, perm) == 0) {
                Log.d(RTAG, "     permission:" + perm + "[UNDECLARED!]");
                found_undeclared = true;
            }
        }
        return found_undeclared ? "virtualization" : "real";
    }

    /**DETECTION MECHANISM 2*/

    private List<String> getDeclaredPermissions(Context ctx, PackageManager pm){
        ArrayList<String> perms = new ArrayList<String>();
        String pkgname = ctx.getApplicationContext().getPackageName();
        try {
            PackageInfo PI = pm.getPackageInfo(pkgname,
                    PackageManager.GET_CONFIGURATIONS |
                            PackageManager.GET_PERMISSIONS |
                            PackageManager.GET_ACTIVITIES |
                            PackageManager.GET_SERVICES |
                            PackageManager.GET_META_DATA
            );
            if(PI.permissions != null) {
                String perm_str = "";
                for (int i = 0; i < PI.permissions.length; i++) {
                    perms.add(PI.permissions[i].toString());
                    perm_str += PI.permissions[i].toString()+"\n";
                }
            }
            if(PI.requestedPermissions != null) {
                String perm_str = "";
                for (int i = 0; i < PI.requestedPermissions.length; i++) {
                    perms.add(PI.requestedPermissions[i]);
                    perm_str += PI.requestedPermissions[i]+"\n";
                }
            }
        } catch (Exception e) {
            Log.i(RTAG, "Error To Get Declared Permissions of App");
        }
        return perms;
    }
    private List<String> getAllPermissions(PackageManager pm){
        List<String> perms = new ArrayList<String>();
        CharSequence csPermissionGroupLabel;
        CharSequence csPermissionLabel;
        List<PermissionGroupInfo> lstGroups = pm.getAllPermissionGroups(0);
        for (PermissionGroupInfo pgi : lstGroups) {
            csPermissionGroupLabel = pgi.loadLabel(pm);
            try {
                List<PermissionInfo> lstPermissions = pm.queryPermissionsByGroup(pgi.name, 0);
                for (PermissionInfo pi : lstPermissions) {
                    csPermissionLabel = pi.loadLabel(pm);
                    Log.i(RTAG, "   " + pi.name + ": " + csPermissionLabel.toString());
                    perms.add(pi.name);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return perms;
    }

    /**DETECTION MECHANISMS 4,9,10,14*/

    public ApplicationInfo listAll(Context context,
                           PackageManager pm){
        Log.w(RTAG, " ============================== [Package-Check] [PackageCheckInfo] Begin ============================== ");
        String pkgname = context.getApplicationContext().getPackageName();
        Map<String, String> PkgInfo = new HashMap<String, String>();
        Map<String, String> AppInfo = new HashMap<String, String>();
        getCurrrentAppInfo(pm, pkgname);
        try {
            PackageInfo PI = pm.getPackageInfo(pkgname,
                    PackageManager.GET_CONFIGURATIONS |
                            PackageManager.GET_PERMISSIONS |
                            PackageManager.GET_ACTIVITIES |
                            PackageManager.GET_SERVICES|
                            PackageManager.GET_META_DATA
            );
            Log.i(RTAG, "+[Package-Check] [PackageInfoCheck] Begin");
            PkgInfo.put("packageName", PI.packageName);
            PkgInfo.put("sharedUserId", PI.sharedUserId);
            PkgInfo.put("versionName", PI.versionName);
            if(PI.activities != null) {
                String activity_str = "";
                for (int i = 0; i < PI.activities.length; i++) {
                    activity_str += PI.activities[i].toString()+"\n";
                }
                PkgInfo.put("activities", activity_str);
            }
            if(PI.permissions != null) {
                String perm_str = "";
                for (int i = 0; i < PI.permissions.length; i++) {
                    perm_str += PI.permissions[i].toString()+"\n";
                }
                PkgInfo.put("permissions", perm_str);
            }
            if(PI.requestedPermissions != null) {
                String perm_str = "";
                for (int i = 0; i < PI.requestedPermissions.length; i++) {
                    perm_str += PI.requestedPermissions[i]+"\n";
                }
                PkgInfo.put("requestedPermissions", perm_str);
            }
            if(PI.services != null) {
                String _str = "";
                for (int i = 0; i < PI.services.length; i++) {
                    _str += PI.services[i].toString()+"\n";
                }
                PkgInfo.put("services", _str);
            }
            for (Map.Entry<String, String> entry : PkgInfo.entrySet()) {
                Log.i(RTAG, "  "+entry.getKey()+" : "+entry.getValue());
            }

            List<PackageInfo> apps;
            apps = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(RTAG, "Error To Get PackageInfo");
        }
        ApplicationInfo AI = null;
        try {
            AI = pm.getApplicationInfo(pkgname, PackageManager.GET_META_DATA | PackageManager.GET_SHARED_LIBRARY_FILES);
            AppInfo.put("backupAgentName", AI.backupAgentName);
            AppInfo.put("className", AI.className);
            AppInfo.put("dataDir", AI.dataDir);
            AppInfo.put("manageSpaceActivityName", AI.manageSpaceActivityName);
            AppInfo.put("nativeLibraryDir", AI.nativeLibraryDir);
            AppInfo.put("permission", AI.permission);
            AppInfo.put("publicSourceDir", AI.publicSourceDir);
            AppInfo.put("sourceDir", AI.sourceDir);
            AppInfo.put("processName", AI.processName);
            for (Map.Entry<String, String> entry : AppInfo.entrySet()) {
                Log.i(RTAG, entry.getKey() + " : " + entry.getValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(RTAG, "Error To Get ApplicationInfo");
        }
        return AI;
    }

    private PackageInfo getCurrrentAppInfo(PackageManager pm, String pkgName){
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo applicationInfo : packages) {
            if(applicationInfo.packageName.equals(pkgName)) {
                Log.i(RTAG, "===");
                try {
                    PackageInfo packageInfo = pm.getPackageInfo(pkgName, PackageManager.GET_PERMISSIONS);
                    String[] requestedPermissions = packageInfo.requestedPermissions;
                    if(requestedPermissions != null) {
                        for (int i = 0; i < requestedPermissions.length; i++) {
                            Log.d(RTAG, requestedPermissions[i]);
                        }
                    }
                }  catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }catch (Exception e) {
                    Log.i(RTAG, "EEEE");
                }
            }
        }
        return null;
    }

    /**DETECTION MECHANISM 5*/

    protected String getCurrentProcessInfo3(Context context) {
        Log.w(RTAG, " ============================== [Package-Check] [CurrentProcessInfo] Begin ============================== ");
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningTaskInfo> recentTasks = activityManager.getRunningTasks(Integer.MAX_VALUE);
        String str = "";
        String res = "real";
        for (int i = 0; i < recentTasks.size(); i++) {
            String activityToCheck = recentTasks.get(i).baseActivity.toShortString();
            str += "\n\tApplication executed : " + activityToCheck + "\t\t ID: "+recentTasks.get(i).id+"";
            Log.i(RTAG, str);
            if(!activityToCheck.contains("com.matrioska.vedetection/com.matrioska.vedetection") && !activityToCheck.contains("com.google.android"))
                res = "virtualization";
        }
        return res;
    }


    /**DETECTION MECHANISMS 6,13*/

    public static String runningServices(ActivityManager manager){
        Log.w(RTAG, " ============================== [Services-Check] [RunningServices] Begin ============================== ");
        List<ActivityManager.RunningServiceInfo> serviceList = manager.getRunningServices(100);
        String result = "real";
        for (Iterator<ActivityManager.RunningServiceInfo> iterator = serviceList.iterator(); iterator.hasNext();) {
            ActivityManager.RunningServiceInfo serviceInfo = iterator.next();
            if(!serviceInfo.service.toString().contains("com.google")
                    && !serviceInfo.service.toString().contains("com.android")
                    && !serviceInfo.service.toString().contains("android.hardware")) {
                Log.v(RTAG,"      ServiceChecker - ScanServices:" + serviceInfo.service.toString());
                if(!serviceInfo.service.toString().contains("com.matrioska.vedetection.antiplugin.TheService"))
                    result = "virtualization";
            }
        }
        return result;
    }

    /**DETECTION MECHANISM 7*/

    protected String checkUIDProcess(Context context, ActivityManager am, String pkgName){
        Log.w(RTAG, " ============================== [Package-Check] [UIDProcessCheck] Begin ============================== ");
        int pid = android.os.Process.myPid();
        Log.i(RTAG, "All Processes with the same UID");
        List<String> unknown_proc = new ArrayList<>();
        String result = "real";
        for (ActivityManager.RunningAppProcessInfo appProcess : am.getRunningAppProcesses()){
            Log.i(RTAG, "\t\tuid:("+appProcess.uid+")|pid("+appProcess.pid+")|Name("+appProcess.processName+")" + " [CHECKUIDPROCESS!]");
            if(!appProcess.processName.contains(pkgName)){
                unknown_proc.add(appProcess.uid+"_"+appProcess.pid+"_"+appProcess.processName);
                Log.i(RTAG, "\t\tuid:("+appProcess.uid+")|pid("+appProcess.pid+")|Name("+appProcess.processName+")" + " [UNKNOWN!]");
                result = "virtualization";
            }
        }

        Log.i(RTAG, ""+(unknown_proc.size() > 0));
        return result;
    }

    /**DETECTION MECHANISM 15*/

    public void checkUnregisteredFilter(Context ctx) {
        Log.w(RTAG, " ============================== [Broadcast-Check] [CheckUnregisteredFilter] Begin ============================== ");
        rhelper.unregisterAction_all();
        Intent static_intent = new Intent(staticACTION);
        ctx.sendBroadcast(static_intent);

        SystemClock.sleep(5000);

        Log.i(RTAG," CheckUnregisteredFilter " + staticACTION);


    }

    /**DETECTION MECHANISM 16*/

    public static void checkEnabledComp(Context ctx) {
        Log.w(RTAG, " ============================== [BroadCast-Check] [CheckEnabledComponent] Begin ============================== ");

        final ComponentName compName =
                new ComponentName(ctx,
                        TheService.class);

        ctx.getPackageManager().setComponentEnabledSetting(
                compName,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

    }
}
