package dev.sime1.container;

import static android.os.ParcelFileDescriptor.MODE_CREATE;
import static android.os.ParcelFileDescriptor.MODE_READ_WRITE;
import static android.os.ParcelFileDescriptor.MODE_TRUNCATE;

import android.app.Application;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;

import com.lody.virtual.client.NativeEngine;
import com.lody.virtual.client.core.AppDefaultConfig;
import com.lody.virtual.client.core.SettingConfig;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.env.Constants;
import com.lody.virtual.client.ipc.VActivityManager;
import com.lody.virtual.client.ipc.VPackageManager;
import com.lody.virtual.client.stub.InstallerSetting;
import com.xdja.zs.BoxProvider;
import com.xdja.zs.VServiceKeepAliveManager;

import java.io.File;
import java.util.List;

import jonathanfinerty.once.Once;

public class App extends Application {

    private SettingConfig cfg = new SettingConfig() {
        @Override
        public String getHostPackageName() {
            return BuildConfig.APPLICATION_ID;
        }

        @Override
        public String getPluginEnginePackageName() {
            return null;
        }

        @Override
        public boolean isEnableIORedirect() {
            return true;
        }

        @Override
        public boolean isUseRealDataDir(String packageName) {
            return false;
        }

        @Override
        public AppLibConfig getAppLibConfig(String packageName) {
            return AppLibConfig.UseRealLib;
        }

        @Override
        public boolean isAllowCreateShortcut() {
            return false;
        }

        @Override
        public boolean isAllowStartByReceiver(String packageName, int userId, String action) {
            if(!BoxProvider.isCurrentSpace()){
                return false;
            }
            if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
                return VServiceKeepAliveManager.get().inKeepAliveServiceList(packageName)
                        || "com.android.providers.media".equals(packageName);//扫描铃声
            }
            return "com.example.demo2".equals(packageName) || InstallerSetting.privApps.contains(packageName);
        }

        @Override
        public void startPreviewActivity(int userId, ActivityInfo info, VirtualCore.UiCallback callBack) {
            super.startPreviewActivity(userId, info, callBack);
        }

        @Override
        public boolean isForceVmSafeMode(String packageName) {
            return "com.tencent.mm".equals(packageName);
        }

        @Override
        public boolean IsServiceCanRestart(ServiceInfo serviceInfo) {
            //方案2
            return "com.xdja.swbg".equals(serviceInfo.packageName);
        }

        @Override
        public void onPreLunchApp() {
            if (VirtualCore.get().shouldLaunchApp("com.xdja.actoma")) {
            }
        }

        @Override
        public boolean isClearInvalidTask() {
            return false;
        }

        @Override
        public boolean isCanShowNotification(String packageName, boolean currentSpace) {
            //无论哪个域，都显示NFC通知栏
            return "com.android.nfc".equals(packageName);
        }

        @Override
        public void onDarkModeChange(boolean isDarkMode) {
            String pkg = "com.tencent.mm";
            Log.e("kk-test", "change darkMode="+isDarkMode);
            boolean needStartWeixin = false;
            if(VActivityManager.get().isAppRunning("com.tencent.mm", 0, true)){
                needStartWeixin = true;
            }
            VActivityManager.get().finishAllActivities();
            if(needStartWeixin) {
                VActivityManager.get().startActivity(VirtualCore.get().getLaunchIntent(pkg, 0), 0);
            }
        }

        @Override
        public void onFirstInstall(String packageName, boolean isClearData) {
            //running in server process.
            AppDefaultConfig.setDefaultData(packageName);
        }

        @Override
        public boolean onHandleView(Intent intent, String packageName, int userId) {
            if (Intent.ACTION_VIEW.equals(intent.getAction()) && intent.getType() != null) {
                if (intent.getType().startsWith("image/")) {
                    if (VirtualCore.get().isAppInstalled(InstallerSetting.GALLERY_PKG)) {
                        intent.setPackage(InstallerSetting.GALLERY_PKG);
                        return false;
                    }
                } else if (intent.getType().startsWith("video/")) {
                    if (VirtualCore.get().isAppInstalled(InstallerSetting.VIDEO_PLAYER_PKG)) {
                        intent.setPackage(InstallerSetting.VIDEO_PLAYER_PKG);
                        return false;
                    }
                }
            }
            return super.onHandleView(intent, packageName, userId);
        }

        @Override
        public Intent getChooserIntent(Intent orgIntent, IBinder resultTo, String resultWho, int requestCode, Bundle options, int userId) {
            return super.getChooserIntent(orgIntent, resultTo, resultWho, requestCode, options, userId);
        }

        @Override
        public boolean isClearInvalidProcess() {
            return true;
        }

        @Override
        public boolean isFloatOnLockScreen(String className) {
            return "com.tencent.av.ui.VideoInviteActivity".equals(className) || super.isFloatOnLockScreen(className);
        }

        @Override
        public int getWallpaperHeightHint(String packageName, int userId) {
            return Resources.getSystem().getDisplayMetrics().heightPixels;
        }

        @Override
        public int getWallpaperWidthHint(String packageName, int userId) {
            return Resources.getSystem().getDisplayMetrics().widthPixels;
        }

        @Override
        public boolean isNeedRealRequestInstall(String packageName) {
            return super.isNeedRealRequestInstall(packageName);
        }

        @Override
        public boolean isHideForegroundNotification() {
            return true;
        }

        @Override
        public Notification getForegroundNotification() {
            return super.getForegroundNotification();
        }

        @Override
        public boolean isAllowServiceStartForeground(String packageName) {
            if(!BoxProvider.isCurrentSpace()){
                return false;
            }
            return super.isAllowServiceStartForeground(packageName);
        }
    };

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        try {
            VirtualCore.get().startup(base, cfg);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        VirtualCore virtualCore = VirtualCore.get();
        //virtualCore.registerActivityLifecycleCallbacks(this);
        if (!VirtualCore.get().isEngineLaunched()) {
            VirtualCore.get().waitForEngine();
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        virtualCore.initialize(new VirtualCore.VirtualInitializer() {

            @Override
            public void onMainProcess() {
                Once.initialise(App.this);
            }

            @Override
            public void onVirtualProcess() {
            }

            @Override
            public void onServerProcess() {}

        });
    }
}
