package com.glakshya2.pixer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private Intent screenshotServiceIntent;
    private static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermission();
        Log.i("MainActivity", "App Start");
        // Start ScreenshotService to run in the background and detect screenshots
        screenshotServiceIntent = new Intent(this, ScreenshotService.class);
        startService(screenshotServiceIntent);
        Log.i("MainActivity", "Service Started");
    }

    public void checkPermission() {
        // Check for reading storage permission
        Log.i("MainActivity", "Check for storage read permission");
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Request for permission to read storage
            Log.i("MainActivity", "Request storage read permission");
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                    READ_EXTERNAL_STORAGE_REQUEST_CODE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Stop ScreenshotService
        stopService(screenshotServiceIntent);
        Log.i("MainActivity", "App closed");
    }

}
