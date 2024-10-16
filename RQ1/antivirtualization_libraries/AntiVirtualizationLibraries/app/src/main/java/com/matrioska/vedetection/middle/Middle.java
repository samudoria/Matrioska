package com.matrioska.vedetection.middle;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class Middle {

    private static final String RTAG ="VE-DETECTION-APP-IN-THE-MIDDLE" ;

    static String stacktrace = "";

    Middle(){
        stacktrace = getStackTrace();
    }

    public List<String> detect(Context c){

        List<String> res = new ArrayList<String>();

        res.add(stacktrace);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            res.add(checkDataDirectory(c));
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
