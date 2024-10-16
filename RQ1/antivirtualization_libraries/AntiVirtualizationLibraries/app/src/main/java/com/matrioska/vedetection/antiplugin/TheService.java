package com.matrioska.vedetection.antiplugin;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class TheService extends Service {

    private static final String RTAG ="VE-DETECTION-ANTIPLUGIN" ;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "Service created!", Toast.LENGTH_LONG).show();
        Log.v(RTAG,"Service created!");
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service stopped", Toast.LENGTH_LONG).show();
        Log.v(RTAG,"Service stopped!");
    }

    @Override
    public void onStart(Intent intent, int startid) {
        Toast.makeText(this, "Service started by user.", Toast.LENGTH_LONG).show();
        Log.v(RTAG,"Service started by user!");
    }
}