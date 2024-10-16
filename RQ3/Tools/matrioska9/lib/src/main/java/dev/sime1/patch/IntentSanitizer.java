package dev.sime1.patch;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public abstract class IntentSanitizer {
    private static List<IntentSanitizer> filters = new ArrayList();

    public static void addSanitizer(IntentSanitizer s) {
        filters.add(s);
    }

    public static void addSanitizer(Method m) {
        addSanitizer(new MethodIntentFilter(m));
    }

    public static List<IntentSanitizer> getFilters() {
        List<IntentSanitizer> copy = new ArrayList<>();
        for(IntentSanitizer f: filters) {
            copy.add(f.cloneSanitizer());
        }
        return copy;
    }

    public static void clearFilters() {
        filters.clear();
    }

    public static class MethodIntentFilter extends IntentSanitizer {

        Method mF;

        public MethodIntentFilter(Method m) {
            mF = m;
        }

        @Override
        public Intent sanitizeIntent(Intent intent) {
            try {
                return (Intent) mF.invoke(null, intent);
            } catch (Exception e) {
                Log.e("SANITIZE_INTENT", "error sanitizing intent with method " + mF.getName());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected IntentSanitizer cloneSanitizer() {
            return new MethodIntentFilter(mF);
        }
    }

    /**
     * Method called when starting an activity to sanitize the intent
     * @param intent the intent that should be sanitized
     * @return the sanitized intent, or null if the intent should be blocked
     */
    abstract public Intent sanitizeIntent(Intent intent);

    abstract protected IntentSanitizer cloneSanitizer();
}
