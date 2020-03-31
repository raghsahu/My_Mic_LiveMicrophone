package dev.microphone.mymic.activity;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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

public class ConnectBtActivity extends AppCompatActivity {


    private ImageView iv_back;
    private AdView mAdView;
    AdRequest adRequest;
    TextView tv_details;

    String connect_bt="<p>(1).</p>\n" +
            "<p>Place your Bluetooth speaker near your iPhone. In order for Bluetooth technology to work properly, the two devices need to be within range of each other.</p>\n" +
            "<p><span style=\"white-space:pre;\">&nbsp; &nbsp;&nbsp;</span>•<span style=\"white-space:pre;\">&nbsp; &nbsp;&nbsp;</span>If your iPhone and speaker end up too far apart, you may have to reconnect them.</p>\n" +
            "<p>\n" +
            "  <br>\n" +
            "</p>\n" +
            "<p>(2).</p>\n" +
            "<p>Turn on the speaker and invoke \"pairing\" mode. After powering on the speaker, put it in \"pairing\" or \"discoverable\" mode, which typically involves pressing or holding down a button on the outside of the speaker.</p>\n" +
            "<p><span style=\"white-space:pre;\">&nbsp; &nbsp;&nbsp;</span>•<span style=\"white-space:pre;\">&nbsp; &nbsp;&nbsp;</span>Consult your Bluetooth speaker's manual if you do not know how to invoke \"pairing\" mode.</p>\n" +
            "<p>\n" +
            "  <br>\n" +
            "</p>\n" +
            "<p>(3). &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;</p>\n" +
            "<p>Open your iPhone's Settings. This is a grey app with gears on it; you'll likely find it on the Home Screen. &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;&nbsp;</p>\n" +
            "<p>\n" +
            "  <br>\n" +
            "</p>\n" +
            "<p>(4) .</p>\n" +
            "<p>Tap Bluetooth. It's near the top of the \"Settings\" page. &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;&nbsp;</p>\n" +
            "<p>\n" +
            "  <br>\n" +
            "</p>\n" +
            "<p>(5).</p>\n" +
            "<p>Slide \"Bluetooth\" right to the \"On\" position. Doing so will enable your iPhone's Bluetooth feature; you should see a list of Bluetooth devices with which your iPhone can pair emerge below the \"Devices\" heading.</p>\n" +
            "<p><span style=\"white-space:pre;\">&nbsp; &nbsp;&nbsp;</span>•<span style=\"white-space:pre;\">&nbsp; &nbsp;&nbsp;</span>Your speaker will appear here. Their name will likely reflect the brand name, the model number, or a mixture of both.</p>\n" +
            "<p>\n" +
            "  <br>\n" +
            "</p>\n" +
            "<p>(6).</p>\n" +
            "<p>Tap your speaker's name. Doing so will begin pairing your iPhone with your speaker. The pairing process may take up to a couple of minutes.</p>\n" +
            "<p><span style=\"white-space:pre;\">&nbsp; &nbsp;&nbsp;</span>•<span style=\"white-space:pre;\">&nbsp; &nbsp;&nbsp;</span>If you don't see your speaker's name in the list of Bluetooth devices, disable and re-enable Bluetooth on your iPhone to reset the devices list.</p>\n" +
            "<p><span style=\"white-space:pre;\">&nbsp; &nbsp;&nbsp;</span>•<span style=\"white-space:pre;\">&nbsp; &nbsp;&nbsp;</span>Some speakers come with a default password. If prompted to enter a password after pairing, consult the speaker's manual.</p>\n" +
            "<p>\n" +
            "  <br>\n" +
            "</p>\n" +
            "<p>(7).</p>\n" +
            "<p>Play audio on your Bluetooth speaker. Any audio you listen to should play on your Bluetooth speaker.</p>\n" +
            "<p>\n" +
            "  <br>\n" +
            "</p>";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_bt);

        iv_back=findViewById(R.id.iv_back);
        tv_details=findViewById(R.id.tv_details);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //banner ads initialize
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });

        mAdView = (AdView)findViewById(R.id.ad_view);
        adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mAdView = new AdView(this);
        mAdView.setAdSize(AdSize.SMART_BANNER);
        mAdView.setAdUnitId(getString(R.string.banner_ad_unit_id));
        AdRequest.Builder adRequest = new AdRequest.Builder();

        String ANDROID_ID = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        String deviceId = md5(ANDROID_ID).toUpperCase();
        Log.e("ANDROID_ID", ""+ANDROID_ID);
        Log.e("deviceId", ""+deviceId);

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
                Log.e("ban_ads", ""+errorCode);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
