package dev.microphone.mymic.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
            int state = intent.getIntExtra("state", -1);
            switch (state) {
                case 0:
                    Log.e("broadcast123", "Headset unplugged");

                    Intent i = new Intent("android.intent.action.ACTION_HEADSET_PLUG")
                            .putExtra("msgContent", "unplugged");
                    context.sendBroadcast(i);

                    break;
                case 1:
                    Log.e("broadcast12", "Headset plugged");

                    Intent ii = new Intent("android.intent.action.ACTION_HEADSET_PLUG").putExtra("msgContent", "plugged");
                    context.sendBroadcast(ii);

                    break;
            }
        }

    }



}
