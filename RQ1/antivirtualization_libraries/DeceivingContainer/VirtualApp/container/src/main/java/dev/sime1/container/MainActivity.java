package dev.sime1.container;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.ipc.VActivityManager;
import com.lody.virtual.client.ipc.VPackageManager;
import com.lody.virtual.remote.InstallOptions;

import java.util.List;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Runnable r = new Runnable() {
            @Override
            public void run() {
                String pkg = BuildConfig.GUEST_ID;
                List<ApplicationInfo> apps = VPackageManager.get().getInstalledApplications(0, 0);
                if(!apps.contains(pkg)) {
                    PackageManager pm = VirtualCore.get().getUnHookPackageManager();
                    try {
                        PackageInfo info = pm.getPackageInfo(pkg, 0);
                        String apk = info.applicationInfo.sourceDir;
                        VirtualCore.get().installPackage(apk, InstallOptions.makeOptions(true));
                    } catch(Exception e) {
                        e.printStackTrace();
                        return;
                    }
                }
                VActivityManager.get().launchApp(0, pkg);
            }
        };

        new Thread(r).start();
    }
}