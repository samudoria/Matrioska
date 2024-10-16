package dev.sime1.patch;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lab.galaxy.yahfa.HookMain;
import mirror.android.app.ActivityThread;
import mirror.android.app.ResultInfo;

public class ResultSanitizer {

  private static final String T = "RES_SANIT";

  /**
   * since there is no reference to the activity that sent a certain result, we need to manually keep
   * track of all the intents used by a guest app to launch other activities, possibly of other apps
   * Since there is nothing preventing an app from using the same request code to launch different
   * activities, we need to keep a list of all the intents used to start an activity with a certain
   * request code. At the moment, we never remove any intent from the lists, since it would not be trivial
   * understanding which request is related. Potentially we could try to find a solution, or treat
   * the *hopefully* most common case of 1 element per list and remove the list from the map
   */
  public static final HashMap<Integer, List<Intent>> launched = new HashMap<>();

  private interface ISanitizer {
    Intent sanitizeResult(List<Intent> possibleLaunchIntents, Intent result);
  }

  public static void addSanitizer(Method sanitize) {
    m_sanitizers.add(new ISanitizer() {
      @Override
      public Intent sanitizeResult(List<Intent> possibleLaunchIntents, Intent result) {
        try {
          return (Intent) sanitize.invoke(null, possibleLaunchIntents, result);
        } catch (Exception e) {
          e.printStackTrace();
          return null;
        }
      }
    });
  }

  private static List<ISanitizer> m_sanitizers = new ArrayList<>();

  // this is called by the activityManager dynamic proxy
  public static void addLaunched(Intent i, int resultCode) {
    if(!launched.containsKey(resultCode)) {
      launched.put(resultCode, new ArrayList<>());
    }
    List l = launched.get(resultCode);
    l.add(i);
  }

  public static void init() {
    try {
      Log.v(T, "installing res sanitizer");
      for(Method m: Class.forName("android.app.ActivityThread").getDeclaredMethods()) {
        if(m.getName().equals("deliverResults")) {
          Log.v(T, "deliverResults found");
          for(Class c: m.getParameterTypes()) {
            Log.v(T, c.getName());
          }
        }

      }
      Class<?> ActivityClientRecord = Class.forName("android.app.ActivityThread$ActivityClientRecord");
      Method target = Class.forName("android.app.ActivityThread").getDeclaredMethod("deliverResults", ActivityClientRecord, List.class, String.class);
      Method hook = ResultSanitizer.class.getDeclaredMethod("deliverResultsHook", Object.class, Object.class, List.class, String.class);
      Method backup = ResultSanitizer.class.getDeclaredMethod("deliverResults", Object.class, Object.class, List.class, String.class);
      HookMain.backupAndHook(target, hook, backup);
    } catch(Exception e) {
      Log.e(T, e.toString());
      e.printStackTrace();
    }
  }

  public static void deliverResults(Object at, Object r, List<Object> results, String reason) {
    Log.e(T, "the backup method should not be called");
  }

  public static void deliverResultsHook(Object at, Object r, List<Object> results, String reason) {
    Log.d(T, "deliver result");
    Log.d(T, "reason: " + reason);
    for(Object res: results) {
      int req = ResultInfo.mRequestCode.get(res);
      if(launched.containsKey(req)) {
        // we can't know for sure which of the launch intents the result is related to,
        // so we return a list. Most of the times this list should contain only a single intent7
        List<Intent> possibleLaunch = launched.get(req);
        Intent i = sanitizeResult(possibleLaunch, ResultInfo.mData.get(res));
        ResultInfo.mData.set(res, i);
      } else {
        Log.e(T, "cannot find launch intent for request " + req);
      }
    }
    ActivityInfo info = ActivityThread.ActivityClientRecord.activityInfo.get(r);
    Log.d(T, "info:" + info);
    Log.d(T, "intent:" + ActivityThread.ActivityClientRecord.intent.get(r));
    deliverResults(at, r, results, reason);
  }

  static Intent sanitizeResult(List<Intent> possibleLaunchIntents, Intent result) {
    for (ISanitizer sanitizer: m_sanitizers) {
      result = sanitizer.sanitizeResult(possibleLaunchIntents, result);
    }
    return result;
  }
}

