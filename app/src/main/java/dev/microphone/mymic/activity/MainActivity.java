package dev.microphone.mymic.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.appbar.AppBarLayout;

import dev.microphone.mymic.R;
import dev.microphone.mymic.fragment.HomeFragment;
import dev.microphone.mymic.fragment.LiveFragment;
import dev.microphone.mymic.fragment.RecordFragment;

public class MainActivity extends AppCompatActivity {

    public static CardView card_live, card_record;
    public static AppBarLayout toolbar;
    public static LinearLayout ll_bottom;
    public static TextView tv_title;
    public static ImageView iv_help;
    public static TextView tv_live, tv_record;

    private AdView mAdView;
    AdRequest adRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        card_live = findViewById(R.id.card_live);
        card_record = findViewById(R.id.card_record);
        iv_help = findViewById(R.id.iv_help);
        tv_live = findViewById(R.id.tv_live);
        tv_record = findViewById(R.id.tv_record);

        toolbar = findViewById(R.id.toolbar);
        ll_bottom = findViewById(R.id.ll_bottom);
        tv_title = findViewById(R.id.tv_title);

        setUpHomeFragment();
        setAdmob();

        card_live.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                card_record.setCardBackgroundColor(getResources().getColor(R.color.whiteColor));
                card_live.setCardBackgroundColor(getResources().getColor(R.color.blackColor));

                tv_live.setTextColor(getResources().getColor(R.color.whiteColor));
                tv_record.setTextColor(getResources().getColor(R.color.blackColor));

                LiveFragment homefragment = new LiveFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.replace(R.id.containt_main_frame, homefragment);
                ft.addToBackStack(null);
                ft.commit();
            }

        });


        card_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                card_live.setCardBackgroundColor(getResources().getColor(R.color.whiteColor));
                card_record.setCardBackgroundColor(getResources().getColor(R.color.blackColor));

                tv_live.setTextColor(getResources().getColor(R.color.blackColor));
                tv_record.setTextColor(getResources().getColor(R.color.whiteColor));

                RecordFragment homefragment = new RecordFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.replace(R.id.containt_main_frame, homefragment);
                ft.addToBackStack(null);
                ft.commit();
            }
        });


        iv_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, Help_Activity.class);
                startActivity(intent);
            }
        });


    }

    private void setAdmob() {

        //banner ads initialize
        MobileAds.initialize(MainActivity.this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = (AdView) findViewById(R.id.ad_view);
        adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        //***************1st ads
        mAdView = new AdView(MainActivity.this);
        mAdView.setAdSize(AdSize.SMART_BANNER);
        mAdView.setAdUnitId(getString(R.string.banner_ad_unit_id));
        AdRequest.Builder adRequest = new AdRequest.Builder();

        String ANDROID_ID = Settings.Secure.getString(MainActivity.this.getContentResolver(),
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

    }

    private void setUpHomeFragment() {

        card_live.setCardBackgroundColor(getResources().getColor(R.color.whiteColor));
        card_record.setCardBackgroundColor(getResources().getColor(R.color.whiteColor));

        HomeFragment homefragment = new HomeFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.containt_main_frame, homefragment, "HOME_FRAGMENT");
        ft.addToBackStack(null);
        ft.commit();
    }


    public static String md5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }


    @Override
    public void onBackPressed() {
        HomeFragment myFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag("HOME_FRAGMENT");
        if (myFragment != null && myFragment.isVisible()) {
            // add your code here
            finish();
        } else {
            super.onBackPressed();
        }


    }
}
