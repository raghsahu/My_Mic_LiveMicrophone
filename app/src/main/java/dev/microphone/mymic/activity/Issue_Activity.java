package dev.microphone.mymic.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;


import dev.microphone.mymic.R;

import static dev.microphone.mymic.activity.MainActivity.md5;


public class Issue_Activity extends AppCompatActivity {

    ImageView iv_back;
    private AdView mAdView,ad_view1;
    AdRequest adRequest;
    EditText et_subject, et_comments;
    Button btn_send;
    RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_);

        iv_back = findViewById(R.id.iv_back);
        et_subject = findViewById(R.id.et_subject);
        et_comments = findViewById(R.id.et_desc);
        btn_send = findViewById(R.id.btn_send);
        ratingBar = findViewById(R.id.ratingBar);

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
        ad_view1 = (AdView) findViewById(R.id.ad_view1);
        adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        ad_view1.loadAd(adRequest);

        mAdView = new AdView(this);
        mAdView.setAdSize(AdSize.SMART_BANNER);
        mAdView.setAdUnitId(getString(R.string.banner_ad_unit_id));
        AdRequest.Builder adRequest = new AdRequest.Builder();

        String ANDROID_ID = Settings.Secure.getString(getApplicationContext().getContentResolver(),
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


        //***************************************************
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Subject = et_subject.getText().toString();
                String Description = et_comments.getText().toString();
                float rating_bar_value = ratingBar.getRating();
                if (!Subject.isEmpty() && !Description.isEmpty()) {

                    Intent intent = new Intent(Intent.ACTION_SEND);
                    String[] recipients = {"rs.logical2019@gmail.com"};
                    intent.putExtra(Intent.EXTRA_EMAIL, recipients);
                    intent.putExtra(Intent.EXTRA_SUBJECT, Subject);
                    intent.putExtra(Intent.EXTRA_TEXT, "Rating: " + rating_bar_value + "\n\n" + Description);
                    //intent.putExtra(Intent.EXTRA_CC,"mailcc@gmail.com");
                    //intent.setType("text/html");
                    intent.setType("message/rfc822");
                    intent.setPackage("com.google.android.gm");


                    try {

                        // startActivity(Intent.createChooser(intent, "Send mail"));
                        startActivityForResult(Intent.createChooser(intent, "Send mail"), 800);
                        Log.e("Finished sending email", "");
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(Issue_Activity.this, "There is no gmail client installed.", Toast.LENGTH_SHORT).show();
                    }


                } else {
                    Toast.makeText(Issue_Activity.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                }


            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 800) {
            //Called when returning from your email intent
            et_comments.setText("");
            et_subject.setText("");
            if (resultCode == Activity.RESULT_OK) {
                // Do something
                //  Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();

            } else {
                // Do something else
                // Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
            }
            // Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();

            if (resultCode == Activity.RESULT_CANCELED) {
                // Do something
                // Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();

            }

        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
