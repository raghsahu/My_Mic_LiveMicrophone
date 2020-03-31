package dev.microphone.mymic.utils;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class AlwaysOnService  extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        registerReceiver(new MyReceiver(), new IntentFilter("android.intent.action.ACTION_HEADSET_PLUG"));
        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLowMemory() {  // rem this if you want it always----
        stopSelf();
        super.onLowMemory();
    }
}
