package dev.microphone.mymic.utils;

import android.app.Application;

import com.google.android.gms.ads.MobileAds;


import dev.microphone.mymic.R;

public class MyApplication  extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // initialize the AdMob app
        MobileAds.initialize(this, getString(R.string.admob_app_id));

    }


    }
