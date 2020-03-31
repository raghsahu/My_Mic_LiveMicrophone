package dev.microphone.mymic.fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import dev.microphone.mymic.BuildConfig;
import dev.microphone.mymic.R;
import dev.microphone.mymic.activity.AboutActivity;
import dev.microphone.mymic.activity.ConnectBtActivity;
import dev.microphone.mymic.activity.Issue_Activity;

import static dev.microphone.mymic.activity.MainActivity.card_live;
import static dev.microphone.mymic.activity.MainActivity.card_record;
import static dev.microphone.mymic.activity.MainActivity.ll_bottom;
import static dev.microphone.mymic.activity.MainActivity.md5;
import static dev.microphone.mymic.activity.MainActivity.toolbar;
import static dev.microphone.mymic.activity.MainActivity.tv_live;
import static dev.microphone.mymic.activity.MainActivity.tv_record;
import static dev.microphone.mymic.activity.MainActivity.tv_title;


public class HelpFragment extends Fragment {

    LinearLayout ll_feedback, ll_about_us, ll_share_app, ll_rate_app;
    private AdView mAdView;
    AdRequest adRequest;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_help, container, false);

        toolbar.setBackgroundColor(getResources().getColor(R.color.whiteColor));
        ll_bottom.setBackgroundColor(getResources().getColor(R.color.whiteColor));
        tv_title.setTextColor(getResources().getColor(R.color.blackColor));

        card_live.setCardBackgroundColor(getResources().getColor(R.color.blackColor));
        card_record.setCardBackgroundColor(getResources().getColor(R.color.blackColor));
        tv_live.setTextColor(getResources().getColor(R.color.whiteColor));
        tv_record.setTextColor(getResources().getColor(R.color.whiteColor));

        getActivity().setTitle("Help");
        // tv_live.setVisibility(View.GONE);

        ll_about_us = root.findViewById(R.id.ll_about_us);
        // tv_conect_bt=root.findViewById(R.id.tv_conect_bt);
        ll_feedback = root.findViewById(R.id.ll_feedback);
        ll_share_app = root.findViewById(R.id.ll_share_app);
        ll_rate_app = root.findViewById(R.id.ll_rate_app);

        //banner ads initialize
        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = (AdView) root.findViewById(R.id.ad_view);
        adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        //***************1st ads
        mAdView = new AdView(getActivity());
        mAdView.setAdSize(AdSize.SMART_BANNER);
        mAdView.setAdUnitId(getString(R.string.banner_ad_unit_id));
        AdRequest.Builder adRequest = new AdRequest.Builder();

        String ANDROID_ID = Settings.Secure.getString(getContext().getContentResolver(),
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


        ll_about_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AboutActivity.class);
                startActivity(intent);

            }
        });

        ll_rate_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
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
                            Uri.parse("http://play.google.com/store/apps/details?id=" + getActivity().getPackageName())));
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
                Intent intent = new Intent(getActivity(), Issue_Activity.class);
                startActivity(intent);

            }
        });


        return root;

    }
}
