package io.github.dromaia.mani;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

// Keeps a connection to the family topic on ntfy.sh while the phone is on, and shows
// a loud full-screen ring (or a message notification) when the app is closed.
public class FamilyRingService extends Service {
    private volatile boolean run = false;
    private Thread thread;
    private String code, me;
    private final Set<String> seen = new HashSet<>();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            code = intent.getStringExtra("code");
            me = intent.getStringExtra("me");
        }
        createChannels();
        if (Build.VERSION.SDK_INT >= 29) {
            startForeground(1, serviceNote(), ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC);
        } else {
            startForeground(1, serviceNote());
        }
        if (thread == null && code != null) {
            run = true;
            thread = new Thread(this::loop);
            thread.start();
        }
        return START_STICKY;
    }

    @Override public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onDestroy() {
        run = false;
        super.onDestroy();
    }

    private void loop() {
        while (run) {
            try {
                long since = System.currentTimeMillis() / 1000 - 60;
                URL u = new URL("https://ntfy.sh/mani-fam-" + code + "/json?since=" + since);
                HttpURLConnection c = (HttpURLConnection) u.openConnection();
                c.setReadTimeout(0);
                BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream(), "UTF-8"));
                String line;
                while (run && (line = br.readLine()) != null) handle(line);
                c.disconnect();
            } catch (Exception e) {
                // no internet or the server closed the stream: try again in a few seconds
            }
            try { Thread.sleep(5000); } catch (InterruptedException e) { return; }
        }
    }

    private void handle(String line) {
        try {
            JSONObject ev = new JSONObject(line);
            if (!"message".equals(ev.optString("event"))) return;
            String id = ev.optString("id");
            if (seen.contains(id)) return;
            seen.add(id);
            long at = ev.optLong("time") * 1000L;
            if (System.currentTimeMillis() - at > 120000L) return;          // old: not a ring now
            if (FamilyRingPlugin.inForeground()) return;                     // the app is open: it shows it itself
            JSONObject m = new JSONObject(ev.optString("message"));
            String f = m.optString("f"), to = m.optString("t");
            if (f.equals(me)) return;
            if (!to.equals("all") && !to.equals(me)) return;
            notifyPerson(f, m.optString("x"), m.optBoolean("r"));
        } catch (Exception e) {
            // not one of ours
        }
    }

    private static String name(String role) {
        switch (role) {
            case "mom": return "رند";
            case "dad": return "أميه";
            case "k1": return "حسان";
            case "k2": return "سلمى";
            case "k3": return "عبدالملك";
            case "maid": return "الخادمة";
            default: return role;
        }
    }

    private void notifyPerson(String from, String text, boolean ring) {
        PendingIntent open = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP),
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder b = new NotificationCompat.Builder(this, ring ? "ring" : "msg")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(name(from))
                .setContentText(ring ? "🔔 يرن عليكِ" : text)
                .setAutoCancel(true)
                .setContentIntent(open);
        if (ring) {
            Uri alarm = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            b.setPriority(NotificationCompat.PRIORITY_MAX)
             .setCategory(NotificationCompat.CATEGORY_CALL)
             .setFullScreenIntent(open, true)
             .setSound(alarm)
             .setVibrate(new long[]{0, 600, 300, 600, 300, 600});
        }
        try {
            NotificationManagerCompat.from(this).notify((int) (System.currentTimeMillis() % 100000), b.build());
        } catch (SecurityException e) {
            // notifications are not allowed for Mani: the user must allow them
        }
    }

    private void createChannels() {
        if (Build.VERSION.SDK_INT < 26) return;
        NotificationManager nm = getSystemService(NotificationManager.class);
        NotificationChannel ring = new NotificationChannel("ring", "رنين العائلة", NotificationManager.IMPORTANCE_HIGH);
        ring.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM), null);
        ring.enableVibration(true);
        nm.createNotificationChannel(ring);
        nm.createNotificationChannel(new NotificationChannel("msg", "رسائل العائلة", NotificationManager.IMPORTANCE_DEFAULT));
        nm.createNotificationChannel(new NotificationChannel("svc", "حالة الاتصال", NotificationManager.IMPORTANCE_MIN));
    }

    private Notification serviceNote() {
        return new NotificationCompat.Builder(this, "svc")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Mani · العائلة")
                .setContentText("مفعّل، عشان الرنين يوصل")
                .setOngoing(true)
                .build();
    }
}
