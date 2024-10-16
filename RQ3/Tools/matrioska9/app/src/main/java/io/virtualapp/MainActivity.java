package io.virtualapp;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.ipc.VActivityManager;
import com.lody.virtual.helper.utils.VLog;
import com.lody.virtual.remote.InstallOptions;

import java.io.File;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    public String getPathOfFile(File dir) {
        String pdfPattern = ".apk";
        String filename = "";
        File[] listFile = dir.listFiles();

        if (listFile != null) {
            for (File file : listFile) {
                if (file.getName().endsWith(pdfPattern)) {
                    filename = file.getName();
                    break;
                }
            }
        }
        return filename;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_main);

        // Retrieve the asset and write into a file in an internal storage
        String relativePath = getPathOfFile(Environment.getExternalStorageDirectory());
        if (relativePath == null || relativePath.equals("")) {
            Log.e(TAG, "No apks was found in externalStorageDirectory");
            return;
        }

        @SuppressLint("SdCardPath") String apkPath = "/sdcard/" + relativePath; // TODO: retrieve from external memory

        System.out.println("Load file in path : " + apkPath);

        // Install and launch
        info("Start installing: " + apkPath);
        InstallOptions options = InstallOptions.makeOptions(false);
        VirtualCore.get().installPackage(apkPath, options, res -> {
            if (res.isSuccess) {
                info("Install " + res.packageName + " success.");
                boolean success = VActivityManager.get().launchApp(0, res.packageName);
                info("launch app " + (success ? "success." : "fail."));
            } else {
                info("Install " + res.packageName + " fail, reason: " + res.error);
            }
        });
    }

    private static void info(String msg) {
        VLog.e("AppInstaller", msg);
    }

}
