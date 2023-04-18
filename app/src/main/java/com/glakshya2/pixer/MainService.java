package com.glakshya2.pixer;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;

public class MainService extends Service {

    @Override
    public void onCreate() {
        new ScreenshotRetriever(getApplicationContext(), getContentResolver());
        stopSelf();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
