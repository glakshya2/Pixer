package com.glakshya2.pixer;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import java.io.File;

public class ScreenshotRetriever {

    private final Context context;
    private ContentResolver contentResolver;

    ScreenshotRetriever(Context context, ContentResolver contentResolver) {
        Log.i("MainActivity", "New ScreenshotRetriever");
        this.context = context;
        this.contentResolver = contentResolver;
        retrieveScreenshot();
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

        // Pass to ObjectDetect
        new ObjectDetect(latestUri, contentResolver, context);
    }
}