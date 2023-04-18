package com.glakshya2.pixer;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

public class ScreenshotService extends Service {

    private ContentObserver contentObserver;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("MainActivity", "New ScreenshotService");
        // Create new ScreenshotObserver to detect change in screenshot directory
        contentObserver = new ScreenshotObserver();
        // Register ScreenshotObserver
        Log.i("MainActivity", "Registering ScreenshotObserver");
        getContentResolver().registerContentObserver(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                true,
                contentObserver);
        Log.i("MainActivity", "ScreenshotObserver Registered");
        // Create a notification Channel
        createNotificationChannel();
        Log.i("MainActivity", "Notification Channel Created");
    }

    private void createNotificationChannel() {
        Log.i("MainActivity", "Creating Notification Channel");
        NotificationChannel channel = new NotificationChannel(
                "default",
                "Default Channel",
                NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("Default channel for notifications");
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Unregister ScreenshotObserver
        getContentResolver().unregisterContentObserver(contentObserver);
        // CLosing ScreenshotService
        Log.i("MainActivity", "Closing ScreenshotService");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class ScreenshotObserver extends ContentObserver {

        private static final int PERMISSIONS_REQUEST_NOTIFICATION = 1;

        public ScreenshotObserver() {
            super(new Handler());
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            Log.i("MainActivity", "screenshot detected");
            // Check if change detected was in Screenshot Directory
            if (uri.toString().matches(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString() + "/\\d+")) {
                // Send notification if detected change was in Screenshot Directory
                Log.i("MainActivity", "Sending Notification");
                // Creating intent for notification to open ScreenshotRetriever
                Log.i("MainActivity", "Creating Intent");
                Intent intent = new Intent(getApplicationContext(), MainService.class);
                PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 0, intent,
                        PendingIntent.FLAG_IMMUTABLE);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "default")
                        .setSmallIcon(R.drawable.ic_launcher_background_notification)
                        .setContentTitle("Screenshot Detected")
                        .setContentText("Tap to get a description")
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
                // Check for notification permission
                Log.i("MainActivity", "Checking for notification permission");
                if (ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    // Request for notification Permission
                    Log.i("MainActivity", "Requesting notification permission");
                    ActivityCompat.requestPermissions((Activity) getApplicationContext(),
                            new String[] { Manifest.permission.POST_NOTIFICATIONS }, PERMISSIONS_REQUEST_NOTIFICATION);
                }
                notificationManager.notify(0, builder.build());
                Log.i("MainActivity", "Notification Sent");
            }

        }
    }
}