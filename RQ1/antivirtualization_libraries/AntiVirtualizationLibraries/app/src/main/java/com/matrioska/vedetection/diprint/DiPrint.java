package com.matrioska.vedetection.diprint;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class DiPrint {

    private static final String RTAG ="VE-DETECTION-DIPRINT" ;

    static StringBuffer libpath = new StringBuffer();
    static String hostapkpath = "null";
    static String suspiciousproc = "";
    static String apkpath = "null";
    static String stacktrace = "";

    static String resStack;

    DiPrint(){
        resStack = getStackTrace();
    }

    public List<String> detect(Context c){

        List<String> res = new ArrayList<String>();
        res.add(checkSuspiciousLib("/proc/self/maps"));
        res.add(checkHostAPK("/proc/self/maps"));
        res.add(runShell("ps"));
        res.add(checkUndeclaredPermission(c));
        res.add(checkAPKCodeLoadingPath(c));
        res.add(resStack);

        if (res.get(0).equals("virtualization")||res.get(1).equals("virtualization")||res.get(2).equals("virtualization")
                ||res.get(3).equals("virtualization")||res.get(4).equals("virtualization")||res.get(5).equals("virtualization")){
            res.add("virtualization");
        }else{
            res.add("real");
        }

        return res;
    }

    /**DETECTION MECHANISM 12*/

    public static String checkSuspiciousLib(String filePath) {
        String res = "";
        try {
            String encoding = "GBK";
            File file = new File(filePath);
            if (file.isFile() && file.exists()) {
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), encoding);
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt;
                int flag = 0;
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    if (lineTxt.contains(".so") && !lineTxt.contains("/system/lib/") && !lineTxt.contains("libmylibrary")
                            && !lineTxt.contains("/system/vendor/lib/") && !lineTxt.contains("/system/lib64/")&& !lineTxt.contains("/system/vendor/lib64/") && !lineTxt.contains("/vendor/lib64/") ) {
                        libpath.append(lineTxt);
                        Log.i(RTAG,"hostAPK--"+lineTxt);
                        flag = 1;
                    }
                }
                if (flag == 0) {
                    res = "real";
                    Log.i(RTAG, "suspicious lib:TRUE in real");
                } else {
                    res = "virtualization";
                    Log.i(RTAG, "suspicious lib:FALSE in virtualization");
                }
                read.close();
            } else {
                Log.i(RTAG, "no such file.");
            }
        } catch (Exception e) {
            Log.i(RTAG, "read file error.");
            e.printStackTrace();
        }
        return res;
    }

    /**DETECTION MECHANISM 11*/

    public static String checkHostAPK(String filePath) {
        String res = "";
        try {
            String encoding = "GBK";
            File file = new File(filePath);
            if (file.isFile() && file.exists()) {
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), encoding);
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt;
                String no = "no";
                int flag = 0;
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    if ((lineTxt.contains("base.apk") && !lineTxt.contains("com.matrioska.vedetection") )) {
                        hostapkpath = lineTxt;
                        flag = 1;
                        Log.i(RTAG, "hostAPK--"+hostapkpath);
                    }
                }
                if (flag == 0) {
                    res = "real";
                    Log.i(RTAG, "checkHostAPK :TRUE in real");
                } else {
                    res = "virtualization";
                    Log.i(RTAG, "checkHostAPK :FALSE in virtualization");
                }
                read.close();
            } else {
                Log.i(RTAG, "no such file.");
            }
        } catch (Exception e) {
            Log.i(RTAG, "read file error.");
            e.printStackTrace();
        }
        return res;
    }

    /**DETECTION MECHANISM 8*/

    public static String runShell(String cmd) {
        String res = "real";
        int count = 0;
        Runtime mRuntime = Runtime.getRuntime();
        try {
            Process mProcess = mRuntime.exec(cmd);
            mProcess.getOutputStream().close();
            InputStream stdin = mProcess.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(stdin));
            String currentLine = "";
            String findline = "";
            String doubleproc = "";
            String uid = "null";
            while ((currentLine = br.readLine()) != null) {
                Log.i(RTAG, "process--"+currentLine);
                if (currentLine.contains("com.matrioska.vedetection")) {
                    uid = currentLine.split("   ")[0];
                    break;
                }
            }

            Process mProcess1 = mRuntime.exec(cmd);
            mProcess.getOutputStream().close();
            InputStream stdin1 = mProcess1.getInputStream();
            BufferedReader br1 = new BufferedReader(new InputStreamReader(stdin1));

            while ((findline = br1.readLine()) != null) {
                Log.i(RTAG, "process2--"+findline);
                Log.i(RTAG, "uid--"+uid);
                if (findline.contains(uid) && !findline.contains("com.matrioska.vedetection")&& !findline.contains("R ps")) {
                    res = "virtualization";
                    suspiciousproc = suspiciousproc + "\n" + findline;
                }
            }

            br.close();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return res;
    }

    /**DETECTION MECHANISM 3*/

    public String hasReadContactsPermission(Context c) {
        String res = "";
        int perm = c.checkCallingOrSelfPermission("android.permission.READ_CONTACTS");

        if (perm == PackageManager.PERMISSION_GRANTED) {
            Log.i(RTAG, "hasReadContactsPermission:TRUE. in virtualization.");
            res = "virtualization";
        } else {
            Log.i(RTAG, "hasReadContactsPermission:FALSE. in real system.");
            res = "real";
        }

        return res;
    }

    public String readAllContacts(Context c) {
        String res = "";
        try {
            Cursor cursor = c.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                    null, null, null, null);
        } catch (Exception e) {
            Log.i(TAG, "call directly permissions：" + "- ");
            Log.i(RTAG, "readAllContacts:FALSE. in real system .");
            res = "real";
            return res;
        }
        Log.i(TAG, "call directly permissions：" + "call successfully");
        Log.i(RTAG, "readAllContacts:TRUE. in virtualization . ");
        res = "virtualization";
        return res;
    }

    public String checkUndeclaredPermission(Context c) {
        String res = "";
        String query = hasReadContactsPermission(c); //query
        String execute = readAllContacts(c); //execute
        if (query.equals("virtualization") || execute.equals("virtualization")){
            res = "virtualization";
        }else{
            res = "real";
        }
        return res;
    }

    /**DETECTION MECHANISM 8*/

    public String checkAPKCodeLoadingPath(Context c) {
        String res = "";
        apkpath = c.getPackageCodePath();
        Log.i(TAG, apkpath);

        if ( (apkpath.matches("(/data/app/com\\.matrioska\\.vedetection-)[\\w]+(==)?(/base\\.apk)"))) {
            res = "real";
            Log.i(RTAG, "checkAPKCodeLoadingPath:" + apkpath + "  TRUE. in real system. ");
        } else {
            res = "virtualization";
            Log.i(RTAG, "checkAPKCodeLoadingPath:" + apkpath + "  FALSE. in virtualization. ");
        }
        return res;
    }

    /**DETECTION MECHANISM 18*/

    public String getStackTrace() {
        String res = "...";

        int count =0;
        try {
            throw new Exception("blah");
        }catch (Exception e) {

            for(StackTraceElement stackTraceElement : e.getStackTrace()) {
                String stacktracetmp = "";
                Log.i(TAG, "call stack： " + stackTraceElement.getClassName() + "->" + stackTraceElement.getMethodName());
                stacktracetmp = stackTraceElement.getClassName() + "->" + stackTraceElement.getMethodName();
                Log.i(RTAG, "stack--"+stacktracetmp);
                stacktrace = stacktrace + "\n" + stacktracetmp;
                if(stacktracetmp.contains("callActivityOnCreate")){
                    count = count +1;
                }
            }
            if(count >1){
                res = "virtualization";
                Log.i(RTAG, "checkstackTrace: " + "FALSE, in virtualization. ");
            }
            else{
                res = "real";
                Log.i(RTAG, "checkstackTrace: " + "TRUE, in real system. ");
            }
            Log.i(RTAG, "GetStackTrace: " +  stacktrace);
        }

        return res;
    }


}
