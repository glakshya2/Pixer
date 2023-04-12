package com.glakshya2.pixer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Intent screenshotServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("MainActivity", "App Start");
        // Start ScreenshotService to run in the background and detect screenshots
        screenshotServiceIntent = new Intent(this, ScreenshotService.class);
        startService(screenshotServiceIntent);
        Log.i("MainActivity", "Service Start");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Stop ScreenshotService
        stopService(screenshotServiceIntent);
        Log.i("MainActivity", "App closed");
    }

}
