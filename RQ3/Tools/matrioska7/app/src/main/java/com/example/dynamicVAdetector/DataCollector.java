package com.example.dynamicVAdetector;

import android.annotation.SuppressLint;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Scanner;
import java.util.Set;

public class DataCollector {

    public static final String PATH = Environment.getExternalStorageDirectory().getPath()+"/VADetector_tmp.txt";
    public static final String FOLDERPATH = Environment.getExternalStorageDirectory().getPath()+"/VAD_APKS";

    private int permissions;
    private int activities;
    private String[][] components;
    private boolean assetsApk;
    private boolean spawnApk;
    private int[] processesSamples;
    private int psSize;
    private String[] applications;
    private int aSize;
    private String[][] intents;
    private int iSize;
    private String[] apks;
    private int apksSize;

    public DataCollector() {
        permissions = -1;
        activities = -1;
        components = null;
        assetsApk = false;
        spawnApk = false;
        processesSamples = new int[2];
        psSize = 0;
        applications = new String[2];
        aSize = 0;
        intents = new String[2][];
        iSize = 0;
        apks = new String[2];
        apksSize = 0;
    }

    public void readAll() {
        File data = new File(PATH);
        try {
            Scanner s = new Scanner(data);
            int c = 0;
            while (s.hasNextLine()) {
                String line = s.nextLine();
                if(line.startsWith("#Permissions: ")) {
                    String value = line.substring(14);
                    if(permissions < 0) permissions = Integer.parseInt(value);
                }
                if(line.startsWith("Application: ")) {
                    String value = line.substring(13);
                    if((!value.equals("com.example.dynamicVAdetector")) && (!MainActivity.isIn(value, applications, aSize))) {
                        applications[aSize++] = value;
                        if(aSize == applications.length) {
                            String[] tmp = applications;
                            applications = new String[tmp.length*2];
                            System.arraycopy(tmp, 0, applications, 0, tmp.length);
                        }
                    }
                }
                if(line.startsWith("#Activities: ")) {
                    String value = line.substring(13);
                    if(activities < 0) activities = Integer.parseInt(value);
                }
                if(line.startsWith("#Components: ")) {
                    String value = line.substring(13);
                    if(components == null) components = new String[Integer.parseInt(value)][];
                }
                if(line.startsWith("Component: ")) {
                    String[] value = line.substring(11).split(" @process: ");
                    if(components != null) {
                        if(c < components.length) {
                            components[c++] = value;
                        }
                    }
                }
                if(line.startsWith("Intent: ")) {
                    String[] value = line.substring(8).split(" - ");
                    intents[iSize++] = value;
                    if(iSize == intents.length) {
                        String[][] tmp = intents;
                        intents = new String[tmp.length*2][];
                        System.arraycopy(tmp, 0, intents, 0, tmp.length);
                    }
                }
                if(line.startsWith("AssetsApk: ")) {
                    String value = line.substring(11);
                    if(value.equals("true")) assetsApk = true;
                }
                if(line.startsWith("FoundApk: ")) {
                    spawnApk = true;
                    String value = line.substring(10);
                    apks[apksSize++] = value;
                    if(apksSize == apks.length) {
                        String[] tmp = apks;
                        apks = new String[tmp.length*2];
                        System.arraycopy(tmp, 0, apks, 0, tmp.length);
                    }
                }
            }
            s.close();
        } catch(Exception e) {
            Log.d("VADetector_Collector", "read error");
        }
        data.delete();
    }

    public static void addPermissions(int data) {
        try {
            Writer output = new BufferedWriter(new FileWriter(PATH, true));
            output.append("#Permissions: "+data+"\n");
            output.close();
        } catch (Exception e) {
            Log.d("VADetector_Collector", "write error (Permissions)");
        }
    }

    public int getPermissions() {
        return permissions;
    }

    public static void addApplication(String data) {
        try {
            Writer output = new BufferedWriter(new FileWriter(PATH, true));
            output.append("Application: "+data+"\n");
            output.close();
        } catch (Exception e) {
            Log.d("VADetector_Collector", "write error (Application)");
        }
    }
    public String[] getApplications() {
        String[] app = new String[aSize];
        System.arraycopy(applications, 0, app, 0, app.length);
        return app;
    }

    public static void addComponents(int na, int l) {
        try {
            Writer output = new BufferedWriter(new FileWriter(PATH, true));
            output.append("#Activities: "+na+"\n");
            output.append("#Components: "+l+"\n");
            output.close();
        } catch (Exception e) {
            Log.d("VADetector_Collector", "write error (Components)");
        }
    }

    public static void addIntent(String action, Set<String> cat, String pckg, String cp, String cname) {
        try {
            Writer output = new BufferedWriter(new FileWriter(PATH, true));
            String cat0 = "null";
            if(cat != null && !cat.isEmpty()) {
                cat0 = cat.toArray()[0].toString();
            }
            if(action == null) action = "null";
            if(cname == null) cname = "null";
            if(cp == null) cp = "null";
            if(pckg == null) pckg = cp;
            output.append("Intent: "+action+" - "+cat0+" - "+pckg+" - "+cname+"\n");
            output.close();
        } catch (Exception e) {
            Log.d("VADetector_Collector", "write error (Intent)");
        }
    }
    public String[][] getIntents() {
        String[][] i = new String[iSize][];
        System.arraycopy(intents, 0, i, 0, i.length);
        return i;
    }

    public static void addComponent(String name, String process) {
        try {
            Writer output = new BufferedWriter(new FileWriter(PATH, true));
            output.append("Component: "+name+" @process: "+process+"\n");
            output.close();
        } catch (Exception e) {
            Log.d("VADetector_Collector", "write error (Component)");
        }
    }
    public String[][] getComponents() {
        if(components != null) return components.clone();
        return new String[0][0];
    }
    public int getActivities() {
        return activities;
    }

    public static void addAssetsApk(AssetManager assets) {
        boolean b = checkAssetsApk(assets, "");
        try {
            Writer output = new BufferedWriter(new FileWriter(PATH, true));
            output.append("AssetsApk: "+b+"\n");
            output.close();
        } catch (Exception e) {
            Log.d("VADetector_Collector", "write error (Assets)");
        }
    }
    private static boolean checkAssetsApk(AssetManager am, String path) {
        try {
            String[] list = am.list(path);
            if (list.length > 0) {
                // This is a folder
                boolean b = false;
                for (String file : list) {
                    String s = "";
                    if(!path.equals(s)) s = path + "/";
                    if(checkAssetsApk(am, s + file)) b = true;
                }
                return b;
            }
            return MainActivity.isApk(am.open(path));
        } catch (Exception e) {}
        return false;
    }
    public boolean hasAssetsApk() {
        return assetsApk;
    }

    public void addProcessesSample(int n) {
        processesSamples[psSize++] = n;
        if(psSize == processesSamples.length) {
            int[] tmp = processesSamples;
            processesSamples = new int[tmp.length*2];
            System.arraycopy(tmp, 0, processesSamples, 0, tmp.length);
        }
    }
    public int[] getProcessesSamples() {
        int[] samples = new int[psSize];
        System.arraycopy(processesSamples, 0, samples, 0, psSize);
        return samples;
    }

    public static void addFoundApk(File file) {
        File dir = new File(FOLDERPATH);
        if(!dir.exists()) dir.mkdir();
        File dst = new File(FOLDERPATH+"/"+file.getName());
        int i = 1;
        while(dst.exists()) {
            dst = new File(FOLDERPATH+"/"+i+file.getName());
            i++;
        }
        try {
            Writer output = new BufferedWriter(new FileWriter(PATH, true));
            output.append("FoundApk: "+dst.getAbsolutePath()+"\n");
            output.close();
            copyFileUsingStream(file, dst);
        } catch (Exception e) {
            Log.d("VADetector_Collector", "write error (FoundApk)");
        }
    }
    public String[] getApks() {
        String[] a = new String[apksSize];
        System.arraycopy(apks, 0, a, 0, apksSize);
        return a;
    }

    public void setSpawnApk(boolean b) {
        if(b) spawnApk = true;
    }
    public boolean getSpawnApk() {
        return spawnApk;
    }


    private static void copyFileUsingStream(File source, File dest) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            is.close();
            os.close();
        }
    }

}
