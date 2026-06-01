package com.hijricalendar;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HijriInterface {
    private static final String PREFS_NAME = "hijri_calendar";
    private static final String TAG = "HijriInterface";

    private final Context context;
    private final NotificationHelper notificationHelper;
    private final UpdateScheduler updateScheduler;

    public HijriInterface(Context context) {
        this.context = context;
        this.notificationHelper = new NotificationHelper(context);
        this.updateScheduler = new UpdateScheduler(context);
    }

    @android.webkit.JavascriptInterface
    public void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @android.webkit.JavascriptInterface
    public void notifyEvent(String eventName, String eventDate, String eventType) {
        notificationHelper.showEventNotification(eventName, eventDate, eventType);
    }

    @android.webkit.JavascriptInterface
    public void scheduleUpdate(int intervalMinutes) {
        updateScheduler.schedule(intervalMinutes);
    }

    @android.webkit.JavascriptInterface
    public String getDeviceInfo() {
        try {
            JSONObject info = new JSONObject();
            info.put("model", Build.MODEL);
            info.put("brand", Build.BRAND);
            info.put("sdk", Build.VERSION.SDK_INT);
            info.put("version", Build.VERSION.RELEASE);
            return info.toString();
        } catch (Exception e) {
            return "{}";
        }
    }

    @android.webkit.JavascriptInterface
    public void savePreference(String key, String value) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(key, value).apply();
    }

    @android.webkit.JavascriptInterface
    public String getPreference(String key, String defaultValue) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(key, defaultValue);
    }

    @android.webkit.JavascriptInterface
    public void clearPreferences() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }

    @android.webkit.JavascriptInterface
    public String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        return sdf.format(new Date());
    }

    public NotificationHelper getNotificationHelper() {
        return notificationHelper;
    }
}
