package com.glakshya2.pixer;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;

public class ScreenshotRetriever extends Activity {

    private static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("MainActivity", "New ScreenshotRetriever");
        checkPermission();
        retrieveScreenshot();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close ScreenshotRetriever
        Log.i("MainActivity", "Closing ScreenshotRetriever Activity");
        finish();
    }

    public void checkPermission() {
        // Check for reading storage permission
        Log.i("MainActivity", "Check for storage read permission");
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Request for permission to read storage
            Log.i("MainActivity", "Request storage read permission");
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                    READ_EXTERNAL_STORAGE_REQUEST_CODE);
        }
    }

    public void retrieveScreenshot() {
        // Retrieve Screenshot
        Log.i("MainActivity", "Looking for screenshot");
        Uri latestUri = null;
        long latestDate = 0;
        // Import screenshot directory
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/Screenshots/";
        File directory = new File(path);
        File[] files = directory.listFiles();
        Log.i("MainActivity", "No. of screenshots in directory = " + files.length);
        // Loop to find latest screenshot
        for (File file : files) {
            if (file.lastModified() > latestDate) {
                latestUri = Uri.fromFile(file);
                latestDate = file.lastModified();
            }
        }

        // Create intent to move to TextExtractor Class
        Log.i("MainActivity", "Creating intent to move to TextExtractor");
        Intent intent = new Intent(ScreenshotRetriever.this, ImageLabelling.class);
        intent.setData(latestUri);
        startActivity(intent);
    }
}