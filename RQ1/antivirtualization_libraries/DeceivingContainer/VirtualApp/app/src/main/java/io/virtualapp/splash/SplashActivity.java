package io.virtualapp.splash;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.ipc.VActivityManager;
import com.lody.virtual.remote.InstallOptions;
import com.lody.virtual.remote.InstallResult;
import com.xdja.utils.Stirrer;

import java.io.File;
import java.io.FileOutputStream;

import io.virtualapp.R;
import io.virtualapp.VCommends;
import io.virtualapp.abs.ui.VActivity;
import io.virtualapp.abs.ui.VUiKit;
import io.virtualapp.home.HomeActivity;
import jonathanfinerty.once.Once;

public class SplashActivity extends VActivity {

    public static final String TAG = "DeceivingContainer";
    public static final String DECOY_FILE = "/sdcard/decoyFile.txt";
    private String pluginName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        File decoy = new File(DECOY_FILE);
        try {
            FileOutputStream stream = new FileOutputStream(decoy);
            stream.write("/vendor/lib64/ base.apk com.matrioska.vedetection".getBytes());
            Log.i("DECOY", "created file");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void install_plugin(View view) {
        EditText editText = (EditText) findViewById(R.id.text);
        String path = editText.getText().toString();
        InstallResult r = install_plugin(path);
        if (r.isSuccess) {
            pluginName = r.packageName;
        }
        else {
            Log.e(TAG, "Error: Cannot install plugin! - "+r.error);
        }
    }

    public void run_plugin(View view) {
        if (pluginName.length() > 0) {
            run_plugin(pluginName);
        }
    }

    public void uninstall_plugin(View view) {
        if (pluginName.length() > 0) {
            uninstall_plugin(pluginName);
        }
        pluginName = "";
    }

    private InstallResult install_plugin(String path) {
        InstallResult res = VirtualCore.get().installPackage(path, InstallOptions.makeOptions(true));
        Log.d(TAG, "InstallPlugin: "+res.isSuccess);
        return res;
    }

    private void run_plugin(String packageName) {
        Intent intent = VirtualCore.get().getLaunchIntent(packageName, 0);
        VActivityManager.get().startActivity(intent, 0);
    }

    private void uninstall_plugin(String packageName) {
        VirtualCore.get().killAllApps();
        if (!VirtualCore.get().uninstallPackage(packageName)) {
            Log.e(TAG, "Uninstallation Error");
        }
    }

}
