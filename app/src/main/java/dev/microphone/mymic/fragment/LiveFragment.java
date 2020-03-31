package dev.microphone.mymic.fragment;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioDeviceInfo;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import dev.microphone.mymic.R;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.ContentValues.TAG;
import static dev.microphone.mymic.activity.MainActivity.card_live;
import static dev.microphone.mymic.activity.MainActivity.card_record;
import static dev.microphone.mymic.activity.MainActivity.ll_bottom;
import static dev.microphone.mymic.activity.MainActivity.toolbar;
import static dev.microphone.mymic.activity.MainActivity.tv_live;
import static dev.microphone.mymic.activity.MainActivity.tv_record;
import static dev.microphone.mymic.activity.MainActivity.tv_title;

public class LiveFragment extends Fragment {

    TextView tv_connect_bluetooth,tv_blth,tv_speak,tv_recording;
    ImageView iv_mute;
    private static final int PERMISSION_RECORD_AUDIO = 0;
    BluetoothAdapter mBluetoothAdapter;
    private boolean isMuting = false;
    private boolean isRunning = false;
    private Thread m_thread;               /* Thread for running the Loop */
    private AudioRecord recorder = null;
    private AudioTrack track = null;
    int bufferSize = 320;                  /* Buffer for recording data */
    byte buffer[] = new byte[bufferSize];

    String name;
    String address;
    String threadName;

    private String mFileName = null;
    private boolean isRecording = false;
    private MediaRecorder mRecorder;
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;
    private static final String LOG_TAG = "AudioRecording";

    private  BroadcastReceiver mReceiver  ;
    boolean Microphone_Plugged_in = false;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_live, container, false);

        getActivity().setTitle("Live");
        // tv_live.setVisibility(View.GONE);
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String action = intent.getAction();
                int iii=2;
                if (Intent.ACTION_HEADSET_PLUG.equals(action)) {
                    iii=intent.getIntExtra("state", -1);
                    if(Integer.valueOf(iii)==0){
                        Microphone_Plugged_in = false;
                       // Toast.makeText(getActivity(),"microphone not plugged in",Toast.LENGTH_SHORT).show();
                        if (isRunning){
                            LiveAudioLooping();
                        }


                    }if(Integer.valueOf(iii)==1){
                        Microphone_Plugged_in = true;
                       // Toast.makeText(getActivity(),"microphone plugged in",Toast.LENGTH_SHORT).show();
                    }
                }
            }};
        IntentFilter receiverFilter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        getActivity().registerReceiver( mReceiver, receiverFilter );

        toolbar.setBackgroundColor(getResources().getColor(R.color.whiteColor));
        ll_bottom.setBackgroundColor(getResources().getColor(R.color.whiteColor));
        tv_title.setTextColor(getResources().getColor(R.color.blackColor));

        card_record.setCardBackgroundColor(getResources().getColor(R.color.whiteColor));
        card_live.setCardBackgroundColor(getResources().getColor(R.color.blackColor));
        tv_live.setTextColor(getResources().getColor(R.color.whiteColor));
        tv_record.setTextColor(getResources().getColor(R.color.blackColor));

        tv_connect_bluetooth=root.findViewById(R.id.tv_connect_bluetooth);
        tv_blth=root.findViewById(R.id.tv_blth);
        iv_mute=root.findViewById(R.id.iv_mute);
        tv_speak=root.findViewById(R.id.tv_speak);
        tv_recording=root.findViewById(R.id.tv_recording);

        CheckPermissions();//check permission required
        //***********check connected device bluetooth
        //currentBluetooth();
        checkConnected();
        Check_Mute_Unmute();

        tv_connect_bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent settingsIntent = new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
                startActivity(settingsIntent);

            }
        });

        tv_speak.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                if(Microphone_Plugged_in) {
                  //  Toast.makeText(getActivity(), "Headset is plug in", Toast.LENGTH_SHORT).show();
                    LiveAudioLooping();
                }
                else {
                   // Toast.makeText(getActivity(), "Microphone not plugged in", Toast.LENGTH_SHORT).show();
                    OpenDialogPlugin();

                }


            }
        });

        iv_mute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isMuting){
                    isMuting=true;
                    iv_mute.setImageResource(R.drawable.ic_mic_off_black_24dp);
                    //mute audio
                    AudioManager amanager=(AudioManager)getActivity().getSystemService(Context.AUDIO_SERVICE);
                    amanager.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);
                    amanager.setStreamMute(AudioManager.STREAM_ALARM, true);
                    amanager.setStreamMute(AudioManager.STREAM_MUSIC, true);
                    amanager.setStreamMute(AudioManager.STREAM_RING, true);
                    amanager.setStreamMute(AudioManager.STREAM_SYSTEM, true);

                }else {
                    isMuting=false;
                    iv_mute.setImageResource(R.drawable.ic_mic_black_24dp);
                    //unmute audio
                    AudioManager amanager=(AudioManager)getActivity().getSystemService(Context.AUDIO_SERVICE);
                    amanager.setStreamMute(AudioManager.STREAM_NOTIFICATION, false);
                    amanager.setStreamMute(AudioManager.STREAM_ALARM, false);
                    amanager.setStreamMute(AudioManager.STREAM_MUSIC, false);
                    amanager.setStreamMute(AudioManager.STREAM_RING, false);
                    amanager.setStreamMute(AudioManager.STREAM_SYSTEM, false);


                }

            }
        });


        tv_recording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //***check internal storage memory sufficient or not..
                StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
                long bytesAvailable;
                bytesAvailable = stat.getBlockSizeLong() * stat.getAvailableBlocksLong();
                long megAvailable = bytesAvailable / (1024 * 1024);
                Log.e("", "Available MB : " + megAvailable);

                if (isRecording) {
                    tv_recording.setText("Record");
                    isRecording = false;

                    try {
                        mRecorder.stop();
                        mRecorder.release();
                        mRecorder = null;
                    } catch (Exception e) {

                    }


                } else {

                    if (megAvailable > 1) {

                        if (CheckPermissions_record()) {

                            File root = android.os.Environment.getExternalStorageDirectory();
                            File file = new File(root.getAbsolutePath() + "/MyMic_Rec/Audios");
                            if (!file.exists()) {
                                file.mkdirs();
                            }

                            mFileName = root.getAbsolutePath() + "/MyMic_Rec/Audios/" + String.valueOf(System.currentTimeMillis() + ".3gp");
                            Log.e("filename", mFileName);

                            if (!isRecording) {
                                isRecording = true;
                                tv_recording.setText("Stop");

                                mRecorder = new MediaRecorder();
                                mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                                mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                                mRecorder.setOutputFile(mFileName);
                                try {
                                    mRecorder.prepare();
                                } catch (IOException e) {
                                    Log.e(LOG_TAG, "prepare() failed " + e.toString());
                                }
                                mRecorder.start();
                                //  Toast.makeText(getActivity(), "Recording Started", Toast.LENGTH_LONG).show();

                            } else {
                                tv_recording.setText("Record");
                                isRecording = false;

                                try {
                                    mRecorder.stop();
                                    mRecorder.release();
                                    mRecorder = null;
                                } catch (Exception e) {

                                }

                                // Toast.makeText(getActivity(), "Recording Stopped", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            RequestPermissions();
                        }

                    } else {
                        OpenStorageDialog();
                        Toast.makeText(getActivity(), "Insufficient storage, Please delete old files", Toast.LENGTH_SHORT).show();
                    }

                }
            }

        });


        return root;

    }

    private void OpenDialogPlugin() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity())
                .setTitle("Microphone not plugged in")
                .setMessage("Please plugin Microphone");

        dialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {

                dialog.dismiss();

            }


        });
        final AlertDialog alert = dialog.create();
        alert.show();
    }

    private void LiveAudioLooping() {
        try {

            if (isRunning){
                Log.e("real_time1","MyMic1");
                isRunning = false;
                do_loopback(isRunning);
                tv_speak.setText("Speak");

            }else if (!isRunning){
                Log.d(TAG, "======== Start Button Pressed ==========");
                isRunning = true;
                do_loopback(isRunning);
                tv_speak.setText("Speak Stop");

            }else {

                Log.d(TAG, "======== Stop Button Pressed ==========");
                isRunning = false;
                do_loopback(isRunning);
                tv_speak.setText("Speak");

            }


        }catch (Exception e){
            Log.e("mesg_content",e.toString());
        }
    }

    private void Check_Mute_Unmute() {
            iv_mute.setImageResource(R.drawable.ic_mic_black_24dp);
            //unmute audio
            AudioManager amanager=(AudioManager)getActivity().getSystemService(Context.AUDIO_SERVICE);
            amanager.setStreamMute(AudioManager.STREAM_NOTIFICATION, false);
            amanager.setStreamMute(AudioManager.STREAM_ALARM, false);
            amanager.setStreamMute(AudioManager.STREAM_MUSIC, false);
            amanager.setStreamMute(AudioManager.STREAM_RING, false);
            amanager.setStreamMute(AudioManager.STREAM_SYSTEM, false);
    }

    private void do_loopback(final boolean flag)
    {
        m_thread = new Thread(new Runnable() {
            public void run() {
                run_loop(flag);
            }
        });
        m_thread.start();
    }

    private void run_loop(boolean isRunning) {

        /** == If Stop Button is pressed == **/
        if (!isRunning) {
            Log.d(TAG, "=====  Stop Button is pressed ===== ");

            if (AudioRecord.STATE_INITIALIZED == recorder.getState()) {
                recorder.stop();
                recorder.release();
            }
            if (AudioTrack.STATE_INITIALIZED == track.getState()) {
                track.stop();
                track.release();
            }
            return;
        }


        /** ======= Initialize AudioRecord and AudioTrack ======== **/
        recorder = findAudioRecord(recorder);
        if (recorder == null) {
            Log.e(TAG, "======== findAudioRecord : Returned Error! =========== ");
            return;
        }

        track = findAudioTrack(track);
        if (track == null) {
            Log.e(TAG, "======== findAudioTrack : Returned Error! ========== ");
            return;
        }

        if ((AudioRecord.STATE_INITIALIZED == recorder.getState()) &&
                (AudioTrack.STATE_INITIALIZED == track.getState())) {
            recorder.startRecording();
            Log.d(TAG, "========= Recorder Started... =========");
            track.play();
            Log.d(TAG, "========= Track Started... =========");
        } else {
            Log.d(TAG, "==== Initilazation failed for AudioRecord or AudioTrack =====");
            return;
        }

        /** ------------------------------------------------------ **/

        /* Recording and Playing in chunks of 320 bytes */
        bufferSize = 320;

        while (isRunning) {
            /* Read & Write to the Device */
            try {
                recorder.read(buffer, 0, bufferSize);
                track.write(buffer, 0, bufferSize);
            }catch (Exception e){

            }


        }
        Log.i(TAG, "Loopback exit");

    }


    public AudioRecord findAudioRecord (AudioRecord recorder) {
        Log.d(TAG, "===== Initializing AudioRecord API =====");
        int m_bufferSize = AudioRecord.getMinBufferSize(8000,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        if (m_bufferSize != AudioRecord.ERROR_BAD_VALUE) {
            recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, 8000,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, m_bufferSize);

            if (recorder.getState() == AudioRecord.STATE_UNINITIALIZED) {
                Log.e(TAG, "====== AudioRecord UnInitilaised ====== ");
                return null;
            }
        }
        return recorder;
    }

    public AudioTrack findAudioTrack (AudioTrack track)
    {
        Log.d(TAG, "===== Initializing AudioTrack API ====");
        int m_bufferSize = AudioTrack.getMinBufferSize(8000,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        if (m_bufferSize != AudioTrack.ERROR_BAD_VALUE)
        {
            track = new AudioTrack(AudioManager.STREAM_MUSIC, 8000,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, m_bufferSize,
                    AudioTrack.MODE_STREAM);

            if (track.getState() == AudioTrack.STATE_UNINITIALIZED) {
                Log.e(TAG, "===== AudioTrack Uninitialized =====");
                return null;
            }
        }
        return track;
    }


    private void CheckPermissions() {
        if (ContextCompat.checkSelfPermission(getActivity(), RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    RECORD_AUDIO)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{RECORD_AUDIO},
                        PERMISSION_RECORD_AUDIO);
                return;
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{RECORD_AUDIO},
                        1);
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{RECORD_AUDIO},
                        PERMISSION_RECORD_AUDIO);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {

            //method call
        }


    }


public boolean CheckPermissions_record() {
    int result = ContextCompat.checkSelfPermission(getActivity(), WRITE_EXTERNAL_STORAGE);
    int result1 = ContextCompat.checkSelfPermission(getActivity(), RECORD_AUDIO);
    return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
}

    private void RequestPermissions() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{RECORD_AUDIO, WRITE_EXTERNAL_STORAGE}, REQUEST_AUDIO_PERMISSION_CODE);
    }
    private void OpenStorageDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity())
                .setTitle("Alert!")
                .setMessage("Insufficient storage, Please delete old files");


        dialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {

                dialog.dismiss();

            }


        });
        final AlertDialog alert = dialog.create();
        alert.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        getActivity().registerReceiver(mReceiver, filter);
        //currentBluetooth();
        checkConnected();

    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(mReceiver);

        if (isRecording) {
            tv_recording.setText("Record");
            isRecording = false;

            try {
                mRecorder.stop();
                mRecorder.release();
                mRecorder = null;
            } catch (Exception e) {

            }

        }

    }

    @Override
    public void onPause() {
        super.onPause();

        if (isRecording) {
            tv_recording.setText("Record");
            isRecording = false;

            try {
                mRecorder.stop();
                mRecorder.release();
                mRecorder = null;
            } catch (Exception e) {

            }
        }
    }

    public void checkConnected()
    {

        BluetoothAdapter.getDefaultAdapter().getProfileProxy(getActivity(), serviceListener, BluetoothProfile.HEADSET);
    }

    private BluetoothProfile.ServiceListener serviceListener = new BluetoothProfile.ServiceListener()
    {
        @Override
        public void onServiceDisconnected(int profile)
        {
            //Log.e("onServiceDisConnected", ""+profile);
        }

        @Override
        public void onServiceConnected(int profile, BluetoothProfile proxy)
        {
            for (BluetoothDevice device : proxy.getConnectedDevices())
            {
                name = device.getName();
                address = device.getAddress();
                threadName = Thread.currentThread().getName();
                //Toast.makeText(getActivity(), name+" " + address+ threadName,  Toast.LENGTH_SHORT).show();
               // tv_blth.setText("Connected: "+name + "\n Address: " + address);
                tv_blth.setText("Connected: "+name);
                Log.e("onServiceConnected", "|" + device.getName() + " | " + device.getAddress() + " | " + proxy.getConnectionState(device) + "(connected = "
                        + BluetoothProfile.STATE_CONNECTED + ")");
            }

            BluetoothAdapter.getDefaultAdapter().closeProfileProxy(profile, proxy);
        }
    };




}
