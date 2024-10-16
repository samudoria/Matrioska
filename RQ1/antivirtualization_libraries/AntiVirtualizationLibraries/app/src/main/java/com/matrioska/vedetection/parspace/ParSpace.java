package com.matrioska.vedetection.parspace;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class ParSpace {

    private static final String RTAG ="VE-DETECTION-PARALLEL-SPACE-TRAVELING" ;

    static String stacktrace = "";
    static String hostapkpath = "null";

    ParSpace(){
        stacktrace = getStackTrace();
    }


    public List<String> detect(Context c){

        List<String> res = new ArrayList<String>();

        res.add(stacktrace);
        res.add(checkHostAPK("/proc/self/maps"));

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


    /**DETECTION MECHANISM 19*/

    @RequiresApi(api = Build.VERSION_CODES.N)
    public String checkDataDirectory(Context c){

        String dataDir = c.getDataDir().getAbsolutePath();
        Log.i(RTAG, "Check Data Directory: " + dataDir);
        if(dataDir.matches("(/data/user/)[0-2](/)[\\w.]+")){
            Log.i(RTAG, "Check Data Directory: real");
            return "real";
        }else{
            Log.i(RTAG, "Check Data Directory: virtualization");
            return "virtualization";
        }
    }
}
