package com.example.dynamicVAdetector;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.lody.virtual.client.core.InstallStrategy;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.ipc.VActivityManager;
import com.lody.virtual.remote.InstallResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;


public class MainActivity extends AppCompatActivity {

    public static final String TAG = "VADetector";

    private DataCollector data;
    private boolean done = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //auto_analyze("/sdcard/" + getSample(new File("/sdcard/")));
    }

    public void auto_analyze(String path) {
        File f = new File(DataCollector.PATH);
        if (f.isFile()) f.delete();
        InstallResult r = install_plugin(path);
        if (r.isSuccess) {
            data = new DataCollector();
            data.addProcessesSample(nProcesses());
            run_plugin(r.packageName);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    data.addProcessesSample(nProcesses());
                    File[] apks = searchApk(r.packageName);
                    for(int i = 0; i < apks.length; i++)
                        data.addFoundApk(apks[i]);
                    //uninstall_plugin(r.packageName);
                    data.readAll();
                    verdict(r.packageName, path);
                    data = null;
                    Log.d(TAG, "COMPLETED! "+path);
                    finish();
                }
            }, 30000);
            Log.d(TAG, "Waiting...");
        }
        else {
            Log.e(TAG, "Error: Cannot install plugin! - "+r.error);
            data = null;
            Log.d(TAG, "COMPLETED! "+path);
            finish();
        }
    }

    public void analyze() {
        File f = new File(DataCollector.PATH);
        if (f.isFile()) f.delete();
        EditText editText = (EditText) findViewById(R.id.text);
        //TextView result = (TextView) findViewById(R.id.result);
        String path = editText.getText().toString();
        InstallResult r = install_plugin(path);
        if (r.isSuccess) {
            data = new DataCollector();
            data.addProcessesSample(nProcesses());
            run_plugin(r.packageName);
            while(!done) {
                data.addProcessesSample(nProcesses());
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                }
            }
            data.addProcessesSample(nProcesses());
            File[] apks = searchApk(r.packageName);
            for(int i = 0; i < apks.length; i++)
                data.addFoundApk(apks[i]);
            uninstall_plugin(r.packageName);
            data.readAll();
            String v = verdict(r.packageName, path);
            //result.setText(v);
        }
        else {
            Log.e(TAG, "Error: Cannot install plugin! - "+r.error);
            //result.setText("Error: Cannot install plugin!");
        }
        data = null;
        Log.d(TAG, "COMPLETED! "+path);
    }

    public void start(View view) {
        done = false;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                analyze();
            }
        });
        t.start();
    }
    public void stop(View view) {
        done = true;
    }

    private String verdict(String packageName, String apkpath) {
        // Stub Analysis
        double Ta = 0.9; // alike Threshold
        int Ts = 5; // stub Threshold
        String[][] components = data.getComponents();
        String[] plist = new String[components.length];
        int pSize = 0;
        String[] pslist = new String[components.length];
        int psSize = 0;
        String[] stubList = new String[components.length];
        int stubSize = 0;
        for (int i = 0; i < components.length; i++) {
            String name = components[i][0];
            String process = components[i][1];
            if (!isIn(process, plist, pSize)) {
                plist[pSize++] = process;
            }
            if (process.split(":").length > 1) {
                int alike = -1;
                for (int j = 0; j < components.length; j++) {
                    Log.d(TAG, "Please Wait: "+i+" - "+j);
                    String[] comp = components[Math.max(0, (i-(2*Ts)+j) % components.length)];
                    if (comp[1].split(":").length > 1) {
                        //int lseq = longest_match(name, components[j][0]);
                        int lseq = lcs(name, comp[0], name.length(), comp[0].length());
                        if (((double) lseq / (double) name.length()) > Ta) alike += 1;
                        if (alike > Ts) break;
                    }
                }
                if (alike > Ts) { //if component is a stub component
                    stubList[stubSize++] = name;
                    if (!isIn(process, pslist, psSize)) {
                        pslist[psSize++] = process;
                    }
                }
            }
        }
        // spawn processes
        boolean spawnprc = false;
        int[] psamples = data.getProcessesSamples();
        for (int i = 1; i < psamples.length; i++) {
            if (psamples[i] - psamples[0] > 1) spawnprc = true;
        }
        // Certificates Analysis
        String[] apks = data.getApks();
        String issuer = parseCert(apkpath);
        boolean sameCert = true;
        for(int i = 0; i < apks.length; i++) {
            if (!parseCert(apks[i]).equals(issuer))
                sameCert = false;
        }
        // Delete APKs
        deleteDir(new File(DataCollector.FOLDERPATH));
        // Intent Analysis
        boolean stubint = false;
        String[][] intents = data.getIntents();
        String[] unknown = new String[intents.length];
        int uSize = 0;
        for(int i = 0; i < intents.length; i++) {
            //if(intents[i][0].equals("android.intent.action.MAIN") && intents[i][1].equals("android.intent.category.LAUNCHER") && !intents[i][2].equals("null")) {
            if((!intents[i][2].equals("null")) && (!intents[i][2].equals(packageName)) && (!intents[i][2].equals("com.example.dynamicVAdetector"))) {
                if(!isPackageInstalled(getApplicationContext(), intents[i][2])) unknown[uSize++] = intents[i][2];
            }
            //unknown[uSize++] = intents[i][2];
            String cname = intents[i][3];
            for(int j = 0; j < stubSize; j++) {
                Log.d(TAG, "Please Wait: " + cname + ", " + stubList[j]);
                if(cname.equals(stubList[j])) {
                    stubint = true;
                    break;
                }
            }
        }
        String[] appNames = data.getApplications();
        boolean comb = false;
        if(spawnprc) {
            for(int i = 0; i < uSize; i++) {
                for(int j = 0; j < appNames.length; j++) {
                    if(unknown[i].equals(appNames[j])) comb = true;
                }
            }
        }
        // print Log
        String str = "--- DATA ---"+
                "\nAssetsApk: "+(data.hasAssetsApk() ? 1 : 0)+
                "\nStubComponents: "+stubSize+
                "\nStubProcesses: "+psSize+
                "\n#Permissions: "+data.getPermissions()+
                "\n#Activities: "+data.getActivities()+
                "\n#Components: "+components.length+
                "\n#Processes: "+pSize+
                "\n--- DYNAMIC DATA ---"+
                "\nSpawnApk: "+(data.getSpawnApk() ? 1 : 0)+
                "\nSpawnProcesses: "+(spawnprc ? 1 : 0)+
                "\nSameCertificates: "+(sameCert ? 1 : 0)+
                "\nStubIntent: "+(stubint ? 1 : 0)+
                "\n#Packages: "+appNames.length+
                "\nUnknownIntent: "+(uSize>0 ? 1 : 0)+
                "\nCombinedIntentAnalysis: "+(comb ? 1 : 0);
        Log.d(TAG, str);
        return str;
    }

//    private static int longest_match(String first, String second) {
//        if (first == null || second == null || first.length() == 0 || second.length() == 0) return 0;
//        int maxLen = 0;
//        int fl = first.length();
//        int sl = second.length();
//        int[][] table = new int[fl][sl];
//        for (int i = 0; i < fl; i++) {
//            for (int j = 0; j < sl; j++) {
//                if (first.charAt(i) == second.charAt(j)) {
//                    if (i == 0 || j == 0) {
//                        table[i][j] = 1;
//                    }
//                    else {
//                        table[i][j] = table[i - 1][j - 1] + 1;
//                    }
//                    if (table[i][j] > maxLen) {
//                        maxLen = table[i][j];
//                    }
//                }
//            }
//        }
//        return maxLen;
//    }

    private static int lcs(String s,String t, int n,int m) {
        int dp[][]=new int[2][m+1];
        int res=0;
        for(int i=1;i<=n;i++) {
            for(int j=1;j<=m;j++) {
                if(s.charAt(i-1)==t.charAt(j-1)) {
                    dp[i%2][j]=dp[(i-1)%2][j-1]+1;
                    if(dp[i%2][j]>res)
                        res=dp[i%2][j];
                }
                else dp[i%2][j]=0;
            }
        }
        return res;
    }

    public static boolean isIn(String str, String[] s, int l) {
        for(int i = 0; i < l; i++) {
            if(s[i].equals(str)) return true;
        }
        return false;
    }

    public static boolean isPackageInstalled(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(packageName);
        if (intent == null) {
            return false;
        }
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    private InstallResult install_plugin(String path) {
        InstallResult res = VirtualCore.get().installPackage(path, InstallStrategy.UPDATE_IF_EXIST);
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

    private int nProcesses() {
        ActivityManager am = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appList = am.getRunningAppProcesses();
        if(appList == null) return 0;
        return appList.size();
    }

    private String parseCert(String path) {
        String issuer = "";
        try {
            ZipFile zip = new ZipFile(new File(path));
            InputStream is = zip.getInputStream(zip.getEntry("META-INF/CERT.RSA"));
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            Iterator i = cf.generateCertificates(is).iterator();
            while (i.hasNext()) {
                X509Certificate c = (X509Certificate) i.next();
                issuer = c.getIssuerX500Principal().getName();
            }
        } catch(Exception e) {}
        Log.d(TAG, "CertificateIssuer: "+issuer);
        return issuer;
    }

    private File[] searchApk(String packageName) {
        String path = "/data/user/0/com.example.dynamicVAdetector/virtual/data/user/0/"+packageName+"/files";
        return searchApk(new File(path));
    }
    private static File[] searchApk(File file) {
        File[] list = new File[0];
        if (file.isDirectory()) {
            //Log.d(TAG, file.getName()+" is directory");
            File[] files = file.listFiles();
            for (File f : files) {
                //Log.d(TAG, "File: "+f.getName());
                File[] res = searchApk(f);
                File[] tmp = list;
                list = new File[tmp.length + res.length];
                System.arraycopy(tmp, 0, list, 0, tmp.length);
                System.arraycopy(res, 0, list, tmp.length, res.length);
            }
        } else {
            try {
                FileInputStream in = new FileInputStream(file);
                if (isApk(in)) {
                    list = new File[1];
                    list[0] = file;
                }
            } catch(Exception e) {}
        }
        return list;
    }

    public static boolean isApk(InputStream file) {
        try {
            //byte[] head = new byte[4];
            //file.read(head);
            //if(head[0] == 0x50 && head[1] == 0x4b && head[2] == 0x03 && head[3] == 0x04) {
            ZipInputStream zip = new ZipInputStream(file);
            ZipEntry entry = null;
            while ((entry = zip.getNextEntry()) != null) {
                //Log.d(TAG, "Entry: "+entry.getName());
                if (entry.getName().equals("AndroidManifest.xml")) return true;
            }
            file.close();
        } catch(Exception e) {}
        return false;
    }

    private void deleteDir(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                deleteDir(f);
            }
        }
        file.delete();
    }

    private String getSample(File dir) {
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

}
