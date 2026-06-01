package com.hijricalendar;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationHelper {
    private static final String CHANNEL_ID = "hijri_events";
    private static final String CHANNEL_NAME = "المناسبات الدينية";
    private static final String CHANNEL_DESC = "إشعارات المناسبات الدينية والتذكيرات";

    private final Context context;
    private int notificationId = 1000;

    public NotificationHelper(Context context) {
        this.context = context;
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription(CHANNEL_DESC);
            channel.enableVibration(true);
            channel.setShowBadge(true);

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    public void showEventNotification(String eventName, String eventDate, String eventType) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        int iconRes;
        String title;
        switch (eventType) {
            case "green":
                iconRes = R.drawable.ic_notification_event;
                title = "🕌 مناسبة دينية";
                break;
            case "red":
                iconRes = R.drawable.ic_notification_event;
                title = "🤲 مناسبة دينية";
                break;
            case "gold":
                iconRes = R.drawable.ic_notification_event;
                title = "✨ مناسبة دينية";
                break;
            default:
                iconRes = R.drawable.ic_notification_event;
                title = "📅 تذكير";
                break;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(iconRes)
                .setContentTitle(title)
                .setContentText(eventName + " - " + eventDate)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(eventName + "\n" + eventDate))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setColor(0xffd4af37)
                .setCategory(NotificationCompat.CATEGORY_EVENT);

        NotificationManagerCompat.from(context).notify(notificationId++, builder.build());
    }

    public void showDailyReminder(String hijriDate, String gregDate, String events) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 1, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification_event)
                .setContentTitle("📅 التقويم الهجري")
                .setContentText(hijriDate)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(hijriDate + "\n" + gregDate + "\n\n" + events))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setColor(0xffd4af37);

        NotificationManagerCompat.from(context).notify(1, builder.build());
    }
}
