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
        Log.i("Main Activity", "App Start");
        screenshotServiceIntent = new Intent(this, ScreenshotService.class);
        startService(screenshotServiceIntent);
        Log.i("Main Activity", "Service Start");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(screenshotServiceIntent);
    }

}

