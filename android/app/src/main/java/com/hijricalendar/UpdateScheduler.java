package com.hijricalendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class UpdateScheduler {
    private static final String ACTION_UPDATE = "com.hijricalendar.UPDATE";

    private final Context context;

    public UpdateScheduler(Context context) {
        this.context = context;
    }

    public void schedule(int intervalMinutes) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am == null) return;

        Intent intent = new Intent(context, UpdateReceiver.class);
        intent.setAction(ACTION_UPDATE);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        long intervalMs = intervalMinutes * 60 * 1000L;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + intervalMs, pendingIntent);
        } else {
            am.setRepeating(AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + intervalMs, intervalMs, pendingIntent);
        }
    }

    public void cancel() {
        Intent intent = new Intent(context, UpdateReceiver.class);
        intent.setAction(ACTION_UPDATE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am != null) am.cancel(pendingIntent);
    }

    public static class UpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == null) return;
            if (intent.getAction().equals(ACTION_UPDATE)) {
                // Send a broadcast to the WebView to refresh
                Intent refreshIntent = new Intent("com.hijricalendar.REFRESH");
                context.sendBroadcast(refreshIntent);
                // Also re-schedule for next interval
                new UpdateScheduler(context).schedule(60);
            }
        }
    }
}
