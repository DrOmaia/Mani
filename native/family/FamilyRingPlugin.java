package io.github.dromaia.mani;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

// Lets the web part start/stop the family listener service (rings and messages when the app is closed).
@CapacitorPlugin(name = "FamilyRing")
public class FamilyRingPlugin extends Plugin {
    // how many of our screens are visible right now (0 = app in background)
    static int started = 0;
    static boolean inForeground() { return started > 0; }

    @Override
    public void load() {
        getActivity().getApplication().registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            public void onActivityStarted(Activity a) { started++; }
            public void onActivityStopped(Activity a) { started = Math.max(0, started - 1); }
            public void onActivityCreated(Activity a, Bundle b) {}
            public void onActivityResumed(Activity a) {}
            public void onActivityPaused(Activity a) {}
            public void onActivitySaveInstanceState(Activity a, Bundle b) {}
            public void onActivityDestroyed(Activity a) {}
        });
    }

    @PluginMethod
    public void start(PluginCall call) {
        String code = call.getString("code");
        String me = call.getString("me");
        if (code == null || code.length() < 12 || me == null) { call.reject("bad code"); return; }
        if (Build.VERSION.SDK_INT >= 33 && getContext().checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != 0) {
            getActivity().requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1001);
        }
        Intent i = new Intent(getContext(), FamilyRingService.class);
        i.putExtra("code", code);
        i.putExtra("me", me);
        if (Build.VERSION.SDK_INT >= 26) getContext().startForegroundService(i); else getContext().startService(i);
        call.resolve();
    }

    @PluginMethod
    public void stop(PluginCall call) {
        getContext().stopService(new Intent(getContext(), FamilyRingService.class));
        call.resolve();
    }
}
