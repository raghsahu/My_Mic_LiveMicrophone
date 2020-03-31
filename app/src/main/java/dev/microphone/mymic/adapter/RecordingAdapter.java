package dev.microphone.mymic.adapter;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dev.microphone.mymic.R;
import dev.microphone.mymic.model.RecordingList;

public class RecordingAdapter extends RecyclerView.Adapter<RecordingAdapter.ViewHolder> implements
        ActivityCompat.OnRequestPermissionsResultCallback{
    private Context context;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;
    List<RecordingList> recordingLists;
    private MediaPlayer mPlayer;
    private boolean isPlaying = false;
    private int row_index=-1;
    private Dialog Hoadialog;

    public RecordingAdapter(Context context, ArrayList<RecordingList> recordingLists) {
        this.context = context;
        this.recordingLists = recordingLists;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.record_list_item, parent, false);
        ViewHolder viewHolder = new RecordingAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

       holder.tv_rec_name.setText(recordingLists.get(position).getFileName());


       holder.iv_play.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               Log.e("rrrr1","click_uri+"+recordingLists.get(position).getRecordingUri());

               if (isPlaying==false){
                   isPlaying=true;
                holder.iv_play.setImageResource(R.drawable.ic_pause);

                   fetchRecordings(recordingLists.get(position).getRecordingUri(),holder.iv_play);

               }else {
                   isPlaying=false;
                  holder.iv_play.setImageResource(R.drawable.ic_play);

                   mPlayer.release();
                   mPlayer = null;
               }

               row_index=position;
               notifyDataSetChanged();

           }
       });


        if(row_index==position){

           if (isPlaying==true){
                holder.iv_play.setImageResource(R.drawable.ic_pause);
               Log.e("rrrr1","ppp1");

           }else {
                holder.iv_play.setImageResource(R.drawable.ic_play);
               Log.e("rrrr2","ppp2");

           }


        }
        else
        {
            holder.iv_play.setImageResource(R.drawable.ic_play);
            Log.e("rrrr","ppp");
        }


       holder.iv_edit.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               Uri uri= Uri.parse(recordingLists.get(position).getRecordingUri());
               File fdelete = new File(uri.getPath());
               if (fdelete.exists()) {

                   OpenEditDialog(recordingLists.get(position).getFileName(),recordingLists.get(position));

               }
           }
       });


        holder.iv_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uri= Uri.parse(recordingLists.get(position).getRecordingUri());
                File fdelete = new File(uri.getPath());
                if (fdelete.exists()) {
                    if (fdelete.delete()) {
                        recordingLists.remove(recordingLists.get(position));
                        System.out.println("file Deleted :" + uri.getPath());
                        notifyDataSetChanged();
                    } else {
                        System.out.println("file not Deleted :" + uri.getPath());
                    }
                }
            }
        });


    }

    private void OpenEditDialog(final String fileName, final RecordingList recordingList) {


        Hoadialog = new Dialog(context);
        Hoadialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Hoadialog.setCancelable(true);
        Hoadialog.setContentView(R.layout.record_rename_dialog);
        // dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        LinearLayout ll_next=Hoadialog.findViewById(R.id.ll_next);
        final EditText et_filename=Hoadialog.findViewById(R.id.et_filename);
        final TextView tv_save=Hoadialog.findViewById(R.id.tv_save);
        et_filename.setText(fileName);

        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!et_filename.getText().toString().isEmpty()&& et_filename.getText().toString()!=null){

                    File root = android.os.Environment.getExternalStorageDirectory();
                    File file = new File(root.getAbsolutePath() + "/MyMic_Rec/Audios");
                    if (!file.exists()) {
                        file.mkdirs();
                    }

                    if(file.exists()){
                        File from = new File(file,fileName);
                        File to = new File(file,et_filename.getText().toString());
                        if(from.exists())
                            from.renameTo(to);

                        recordingList.setFileName(et_filename.getText().toString());
                        recordingList.setRecordingUri(root.getAbsolutePath() + "/MyMic_Rec/Audios/" + et_filename.getText().toString());
                    notifyDataSetChanged();

                    Log.e("new_uri",recordingList.getRecordingUri());
                    }


                    Hoadialog.dismiss();
                }else {
                    Toast.makeText(context, "Please enter file name", Toast.LENGTH_SHORT).show();
                }

            }
        });


        try {
            if (!((Activity)context).isFinishing()){
                Hoadialog.show();
            }
        }
        catch (WindowManager.BadTokenException e) {
            //use a log message
        }



    }

    private void fetchRecordings(String recordingUri, final ImageView iv_play) {
        mPlayer = new MediaPlayer();


        try {
            mPlayer.setDataSource(recordingUri);
            mPlayer.prepare();
            mPlayer.start();
            // Toast.makeText(getActivity(), "Recording Started Playing", Toast.LENGTH_LONG).show();


            /** once the audio is complete, timer is stopped here**/
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    isPlaying = false;
                    iv_play.setImageResource(R.drawable.ic_play);
                    mPlayer.release();
                    mPlayer = null;

                    //tv_record.setEnabled(true);
                    //tv_re_record.setEnabled(true);
                }
            });


        } catch (IOException e) {
            Log.e("play_faild", "prepare() failed "+e.toString());
        }
    }

    private void checkRunTimePermission(String mobile) {


        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity)context,
                    Manifest.permission.CALL_PHONE)) {

                ActivityCompat.requestPermissions((Activity)context,
                        new String[]{Manifest.permission.CALL_PHONE},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
                // Log.e("perm_if", "perm_if");
            } else {
                ActivityCompat.requestPermissions((Activity)context,
                        new String[]{Manifest.permission.CALL_PHONE},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
                // Log.e("perm_else", "perm_else");
            }
        }else {
            // Log.e("perm_else_try", "perm_else");
            try{
                //redirectSms(mobile);
            }catch(Exception e){
                // Log.e("send_sms_error", e.toString());
            }
        }

    }

    @Override
    public int getItemCount() {
        return recordingLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
       // CircleImageView userimage;
        ImageView iv_play,iv_del,iv_edit;
        TextView tv_rec_name;

        public ViewHolder( View itemView) {
            super(itemView);
            tv_rec_name=itemView.findViewById(R.id.tv_rec_name);
            iv_play=itemView.findViewById(R.id.iv_play);
            iv_del=itemView.findViewById(R.id.iv_del);
            iv_edit=itemView.findViewById(R.id.iv_edit);

        }
    }


    @Override
    public void onRequestPermissionsResult ( int requestCode, String permissions[],
                                             int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                boolean openDialogOnce = true;
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //  redirectSms(mobile_nmbr);

                } else {
                    Toast.makeText(context, "Call faild, please try again.", Toast.LENGTH_LONG).show();

                }
                return;

            }
        }
    }
}
