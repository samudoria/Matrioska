package com.matrioska.vedetection.antiplugin;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class TheReceiver extends BroadcastReceiver {

    private static final String RTAG ="VE-DETECTION-ANTIPLUGIN" ;

    public TheReceiver() {}

    public TheReceiver(Context c) {
        ct=c;
        receiver=this;
    }
    NotificationManager mn=null;
    Notification notification=null;
    Context ct=null;
    TheReceiver receiver;

    public void registerAction(String action){
        IntentFilter filter=new IntentFilter();
        filter.addAction(action);
        ct.registerReceiver(receiver, filter);
    }

    public void unregisterAction_all() {
        ct.unregisterReceiver(receiver);
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        Log.v(RTAG, "      DummyReceiver::onReceive: Action[" + intent.getAction().toString() + "] Data[" + intent.getData() + "]");
    }

}
