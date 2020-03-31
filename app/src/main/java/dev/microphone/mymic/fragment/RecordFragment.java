package dev.microphone.mymic.fragment;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaRecorder;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import dev.microphone.mymic.R;
import dev.microphone.mymic.model.RecordingList;
import dev.microphone.mymic.adapter.AudioItemAdapter;
import dev.microphone.mymic.adapter.RecordingAdapter;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static dev.microphone.mymic.activity.MainActivity.card_live;
import static dev.microphone.mymic.activity.MainActivity.card_record;
import static dev.microphone.mymic.activity.MainActivity.ll_bottom;
import static dev.microphone.mymic.activity.MainActivity.toolbar;
import static dev.microphone.mymic.activity.MainActivity.tv_live;
import static dev.microphone.mymic.activity.MainActivity.tv_record;
import static dev.microphone.mymic.activity.MainActivity.tv_title;

public class RecordFragment extends Fragment {

    TextView tv_recording;
    TextView tv_connect_bluetooth, tv_blth;
    RecyclerView recycler_list;
    private String mFileName = null;
    private boolean isRecording = false;
    private boolean isMuting = false;
    private MediaRecorder mRecorder;
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;
    private static final String LOG_TAG = "AudioRecording";
    ArrayList<RecordingList> recordingLists = new ArrayList<>();
    RecordingAdapter recordingAdapter;
    BluetoothAdapter mBluetoothAdapter;
    ImageView iv_volume_up;

    String name;
    String address;
    String threadName;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //notificationsViewModel =ViewModelProviders.of(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_record, container, false);

        getActivity().setTitle("Help");
        // tv_live.setVisibility(View.GONE);

        toolbar.setBackgroundColor(getResources().getColor(R.color.whiteColor));
        ll_bottom.setBackgroundColor(getResources().getColor(R.color.whiteColor));
        tv_title.setTextColor(getResources().getColor(R.color.blackColor));

        card_live.setCardBackgroundColor(getResources().getColor(R.color.whiteColor));
        card_record.setCardBackgroundColor(getResources().getColor(R.color.blackColor));
        tv_live.setTextColor(getResources().getColor(R.color.blackColor));
        tv_record.setTextColor(getResources().getColor(R.color.whiteColor));

        tv_recording = root.findViewById(R.id.tv_record);
        tv_blth = root.findViewById(R.id.tv_blth);
        recycler_list = root.findViewById(R.id.recycler_list);
        tv_connect_bluetooth = root.findViewById(R.id.tv_connect_bluetooth);
        iv_volume_up = root.findViewById(R.id.iv_volume_up);

        ShowListRecord();
       // currentBluetooth();
        checkConnected();

        Check_Mute_Unmute();

        tv_connect_bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent settingsIntent = new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
                startActivity(settingsIntent);

            }
        });

        iv_volume_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isMuting) {
                    isMuting = true;
                    iv_volume_up.setImageResource(R.drawable.ic_volume_off);
                    //mute audio
                    AudioManager amanager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
                    amanager.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);
                    amanager.setStreamMute(AudioManager.STREAM_ALARM, true);
                    amanager.setStreamMute(AudioManager.STREAM_MUSIC, true);
                    amanager.setStreamMute(AudioManager.STREAM_RING, true);
                    amanager.setStreamMute(AudioManager.STREAM_SYSTEM, true);

                } else {

                    isMuting = false;
                    iv_volume_up.setImageResource(R.drawable.ic_volume);
                    //unmute audio
                    AudioManager amanager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
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
                    ShowListRecord();

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
                                ShowListRecord();
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

    private void Check_Mute_Unmute() {
            iv_volume_up.setImageResource(R.drawable.ic_volume);
            //unmute audio
            AudioManager amanager=(AudioManager)getActivity().getSystemService(Context.AUDIO_SERVICE);
            amanager.setStreamMute(AudioManager.STREAM_NOTIFICATION, false);
            amanager.setStreamMute(AudioManager.STREAM_ALARM, false);
            amanager.setStreamMute(AudioManager.STREAM_MUSIC, false);
            amanager.setStreamMute(AudioManager.STREAM_RING, false);
            amanager.setStreamMute(AudioManager.STREAM_SYSTEM, false);

    }

    private void OpenStorageDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity())
                .setTitle("Alert!")
                .setMessage("Insufficient storage, Please delete old files");

//        dialog.setNegativeButton("no", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });

        dialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {

                dialog.dismiss();

            }


        });
        final AlertDialog alert = dialog.create();
        alert.show();
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
               // Toast.makeText(getActivity(), name+" " + address+ threadName,  Toast.LENGTH_SHORT).show();
               // tv_blth.setText("Connected: "+name + "\n Address: " + address);
                tv_blth.setText("Connected: "+name);
                Log.e("onServiceConnected", "|" + device.getName() + " | " + device.getAddress() + " | " + proxy.getConnectionState(device) + "(connected = "
                        + BluetoothProfile.STATE_CONNECTED + ")");
            }

            BluetoothAdapter.getDefaultAdapter().closeProfileProxy(profile, proxy);
        }
    };

    private void currentBluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            Toast.makeText(getActivity(), "Bluetooth Not Supported", Toast.LENGTH_SHORT).show();
        } else if (!mBluetoothAdapter.isEnabled()) {
            // Bluetooth is not enabled :)
            //startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),1);
            // Toast.makeText(getActivity(),"Bluetooth Not enabled",Toast.LENGTH_SHORT).show();
        } else {
            // Bluetooth is enabled
            // Toast.makeText(getActivity(),"Bluetooth enable",Toast.LENGTH_SHORT).show();
            //set text bluetooth device name

            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                //Toast.makeText(getActivity(), "At least one paired bluetooth device found", Toast.LENGTH_SHORT).show();
                // TODO at this point you'd have to iterate these devices and check if any of them is a wearable (HOW?)
                for (BluetoothDevice device : pairedDevices) {
                    Log.e("connected_bt", "Paired device: " + device.getName() + ", with address: " + device.getAddress());
                    tv_blth.setText("Paired device: " + device.getName());

                }
            } else {
                Toast.makeText(getActivity(), "No paired bluetooth devices found", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void ShowListRecord() {
        File root = android.os.Environment.getExternalStorageDirectory();
        String path = root.getAbsolutePath() + "/MyMic_Rec/Audios";
        Log.e("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        if (files != null) {

            recordingLists.clear();
            for (int i = 0; i < files.length; i++) {

                Log.e("Files", "FileName:" + files[i].getName());
                String fileName = files[i].getName();
                String recordingUri = root.getAbsolutePath() + "/MyMic_Rec/Audios/" + fileName;

                recordingLists.add(0, new RecordingList(recordingUri, fileName));

            }
            // recordingAdapter=new RecordingAdapter(getActivity(),recordingLists);

            AudioItemAdapter recordingAdapter = new AudioItemAdapter(getActivity(), recordingLists);
            RecyclerView.LayoutManager mLayoutManger = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            recycler_list.setLayoutManager(mLayoutManger);
            recycler_list.setItemAnimator(new DefaultItemAnimator());
            recycler_list.setAdapter(recordingAdapter);
            recordingAdapter.notifyDataSetChanged();

        } else {
            Toast.makeText(getActivity(), "No recording found", Toast.LENGTH_SHORT).show();
        }

    }

    //*****************************
    public boolean CheckPermissions_record() {
        int result = ContextCompat.checkSelfPermission(getActivity(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getActivity(), RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void RequestPermissions() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{RECORD_AUDIO, WRITE_EXTERNAL_STORAGE}, REQUEST_AUDIO_PERMISSION_CODE);
    }


    @Override
    public void onResume() {
        super.onResume();
        //currentBluetooth();
        checkConnected();
    }


    @Override
    public void onStop() {
        super.onStop();

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
}
