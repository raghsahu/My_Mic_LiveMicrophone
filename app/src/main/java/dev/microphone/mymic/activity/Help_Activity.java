package dev.microphone.mymic.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import dev.microphone.mymic.BuildConfig;
import dev.microphone.mymic.R;

import static dev.microphone.mymic.activity.MainActivity.md5;

public class Help_Activity extends AppCompatActivity {

    LinearLayout ll_feedback, ll_about_us, ll_share_app, ll_rate_app;
    private AdView mAdView;
    AdRequest adRequest;
    ImageView iv_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_);

        iv_back = findViewById(R.id.iv_back);

        ll_about_us = findViewById(R.id.ll_about_us);
        ll_feedback = findViewById(R.id.ll_feedback);
        ll_share_app = findViewById(R.id.ll_share_app);
        ll_rate_app = findViewById(R.id.ll_rate_app);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //banner ads initialize
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = (AdView) findViewById(R.id.ad_view);
        adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mAdView = new AdView(this);
        mAdView.setAdSize(AdSize.SMART_BANNER);
        mAdView.setAdUnitId(getString(R.string.banner_ad_unit_id));
        AdRequest.Builder adRequest = new AdRequest.Builder();

        @SuppressLint("HardwareIds") String ANDROID_ID = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        String deviceId = md5(ANDROID_ID).toUpperCase();
        Log.e("ANDROID_ID", "" + ANDROID_ID);
        Log.e("deviceId", "" + deviceId);

        adRequest.addTestDevice(deviceId);

        AdRequest request = adRequest.build();
        mAdView.loadAd(request);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                Log.e("ban_adsload", "Banner");
                // Toast.makeText(getActivity(), "Banner ads", Toast.LENGTH_SHORT).show();
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                Log.e("ban_ads", "" + errorCode);
                //Toast.makeText(getActivity(), "Banner fail", Toast.LENGTH_SHORT).show();
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                Log.e("ban_adsopen", "Banner");
                // Toast.makeText(getActivity(), "Banner open", Toast.LENGTH_SHORT).show();
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdClicked() {
                Log.e("ban_adsclick", "Banner");
                //Toast.makeText(getActivity(), "Banner click", Toast.LENGTH_SHORT).show();
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                Log.e("ban_adsleft", "Banner");
                //  Toast.makeText(getActivity(), "Banner left", Toast.LENGTH_SHORT).show();
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                Log.e("ban_adsclose", "Banner");
                //Toast.makeText(getActivity(), "Banner close", Toast.LENGTH_SHORT).show();
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });

        //************************************

        ll_about_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Help_Activity.this, AboutActivity.class);
                startActivity(intent);

            }
        });

        ll_rate_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                // To count with Play market backstack, After pressing back button,
                // to taken back to our application, we need to add following flags to intent.
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName())));
                }

            }
        });

        ll_share_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My Mic");
                    String shareMessage = "\nLet me recommend you this application\n\n";
                    shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "choose one"));
                } catch (Exception e) {
                    //e.toString();
                }

            }
        });

        ll_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Help_Activity.this, Issue_Activity.class);
                startActivity(intent);

            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
