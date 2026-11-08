package io.github.dromaia.mani;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.widget.RemoteViews;
import android.widget.Toast;
import org.json.JSONObject;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

// Home-screen button: one tap rings the family phone (Mom, or the maid if the phone is Mom's).
public class RingWidget extends AppWidgetProvider {
    static final String ACTION = "io.github.dromaia.mani.RING_WIDGET";

    @Override
    public void onUpdate(Context c, AppWidgetManager am, int[] ids) {
        for (int id : ids) {
            RemoteViews v = new RemoteViews(c.getPackageName(), R.layout.ring_widget);
            Intent i = new Intent(c, RingWidget.class).setAction(ACTION);
            PendingIntent p = PendingIntent.getBroadcast(c, id, i, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            v.setOnClickPendingIntent(R.id.ring_btn, p);
            am.updateAppWidget(id, v);
        }
    }

    @Override
    public void onReceive(Context c, Intent in) {
        super.onReceive(c, in);
        if (!ACTION.equals(in.getAction())) return;
        final PendingResult pr = goAsync();
        new Thread(() -> {
            String msg = ring(c);
            new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(c, msg, Toast.LENGTH_SHORT).show());
            pr.finish();
        }).start();
    }

    // returns a short message for the user
    static String ring(Context c) {
        SharedPreferences sp = c.getSharedPreferences("mani_fam", Context.MODE_PRIVATE);
        String code = sp.getString("code", null), me = sp.getString("me", null);
        if (code == null || me == null) return "افتحي Mani مرة وحدة أول";
        String target = me.equals("mom") ? "maid" : "mom";
        try {
            JSONObject body = new JSONObject();
            body.put("f", me); body.put("t", target); body.put("x", "🔔"); body.put("r", true); body.put("l", "ar");
            HttpURLConnection con = (HttpURLConnection) new URL("https://ntfy.sh/mani-fam-" + code).openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setRequestProperty("X-Priority", "5");
            con.setRequestProperty("X-Tags", "bell");
            byte[] data = body.toString().getBytes("UTF-8");
            try (OutputStream os = con.getOutputStream()) { os.write(data); }
            int code2 = con.getResponseCode();
            con.disconnect();
            return code2 == 200 ? "تم الرنين 🔔" : "ما انرنّ، تأكدي من الإنترنت";
        } catch (Exception e) {
            return "ما انرنّ، تأكدي من الإنترنت";
        }
    }
}
