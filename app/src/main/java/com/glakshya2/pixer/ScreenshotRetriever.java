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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("Main Activity", "New Screenshot Retriever");
        checkPermission();
        retrieveScreenshot();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
    private static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 123;
    public void checkPermission(){
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_REQUEST_CODE );
        }
    }

    public void retrieveScreenshot() {
        Log.i("Main Activity", "Looking for screenshot");
            Uri latestUri = null;
            long latestDate = 0;
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/Screenshots/";
            Log.i("Main Activity", "Step 1");
            File directory = new File(path);
            File[] files = directory.listFiles();
            Log.i("Main Activity", Integer.toString(files.length));
            for (File file : files) {
                if (file.lastModified() > latestDate) {
                    latestUri = Uri.fromFile(file);
                    latestDate = file.lastModified();
                    Log.i("Main Activity", "Found another");
                }
            }
            Intent intent = new Intent(ScreenshotRetriever.this, TextExtractor.class);
            intent.setData(latestUri);
            startActivity(intent);
        }


}

