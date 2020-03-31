package dev.microphone.mymic.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

import dev.microphone.mymic.R;
import dev.microphone.mymic.model.RecordingList;

public class AudioItemAdapter extends RecyclerView.Adapter<AudioItemAdapter.AudioItemsViewHolder> {

    private MediaPlayer mediaPlayer;
    private Context context;
    private List<RecordingList> audioItems;
    private int currentPlayingPosition;
    private SeekBarUpdater seekBarUpdater;
    private AudioItemsViewHolder playingHolder;
    private Dialog Hoadialog;

    public AudioItemAdapter(Context context, List<RecordingList> audioItems) {
        this.audioItems = audioItems;
        this.currentPlayingPosition = -1;
        seekBarUpdater = new SeekBarUpdater();
        this.context = context;
    }

    @Override
    public AudioItemsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AudioItemsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.record_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(AudioItemsViewHolder holder, final int position) {
        holder.tv_rec_name.setText(audioItems.get(position).getFileName());

        if (position == currentPlayingPosition) {
            playingHolder = holder;
            updatePlayingView();
        } else {
            updateNonPlayingView(holder);
        }

        holder.iv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uri = Uri.parse(audioItems.get(position).getRecordingUri());
                File fdelete = new File(uri.getPath());
                if (fdelete.exists()) {

                    OpenEditDialog(audioItems.get(position).getFileName(), audioItems.get(position));

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
        LinearLayout ll_next = Hoadialog.findViewById(R.id.ll_next);
        final EditText et_filename = Hoadialog.findViewById(R.id.et_filename);
        final TextView tv_save = Hoadialog.findViewById(R.id.tv_save);
        et_filename.setText(fileName);

        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!et_filename.getText().toString().isEmpty() && et_filename.getText().toString() != null) {

                    File root = android.os.Environment.getExternalStorageDirectory();
                    File file = new File(root.getAbsolutePath() + "/MyMic_Rec/Audios");
                    if (!file.exists()) {
                        file.mkdirs();
                    }

                    if (file.exists()) {
                        File from = new File(file, fileName);
                        File to = new File(file, et_filename.getText().toString());
                        if (from.exists())
                            from.renameTo(to);

                        recordingList.setFileName(et_filename.getText().toString());
                        recordingList.setRecordingUri(root.getAbsolutePath() + "/MyMic_Rec/Audios/" + et_filename.getText().toString());
                        notifyDataSetChanged();

                        Log.e("new_uri", recordingList.getRecordingUri());
                    }


                    Hoadialog.dismiss();
                } else {
                    Toast.makeText(context, "Please enter file name", Toast.LENGTH_SHORT).show();
                }

            }
        });


        try {
            if (!((Activity) context).isFinishing()) {
                Hoadialog.show();
            }
        } catch (WindowManager.BadTokenException e) {
            //use a log message
        }

    }

    @Override
    public void onViewRecycled(AudioItemsViewHolder holder) {
        super.onViewRecycled(holder);
        if (currentPlayingPosition == holder.getAdapterPosition()) {
            updateNonPlayingView(playingHolder);
            playingHolder = null;
        }
    }

    private void updateNonPlayingView(AudioItemsViewHolder holder) {
        try {
            holder.sbProgress.removeCallbacks(seekBarUpdater);
            holder.sbProgress.setEnabled(false);
            holder.sbProgress.setProgress(0);
            holder.ivPlayPause.setImageResource(R.drawable.ic_play);
        } catch (Exception e) {

        }

    }

    private void updatePlayingView() {
        playingHolder.sbProgress.setMax(mediaPlayer.getDuration());
        playingHolder.sbProgress.setProgress(mediaPlayer.getCurrentPosition());
        playingHolder.sbProgress.setEnabled(true);
        if (mediaPlayer.isPlaying()) {
            playingHolder.sbProgress.postDelayed(seekBarUpdater, 100);
            playingHolder.ivPlayPause.setImageResource(R.drawable.ic_pause);
        } else {
            playingHolder.sbProgress.removeCallbacks(seekBarUpdater);
            playingHolder.ivPlayPause.setImageResource(R.drawable.ic_play);
        }
    }

    void stopPlayer() {
        if (null != mediaPlayer) {
            releaseMediaPlayer();
        }
    }

    private class SeekBarUpdater implements Runnable {
        @Override
        public void run() {
            if (null != playingHolder) {
                playingHolder.sbProgress.setProgress(mediaPlayer.getCurrentPosition());
                playingHolder.sbProgress.postDelayed(this, 100);
            }
        }
    }

    @Override
    public int getItemCount() {
        return audioItems.size();
    }

    class AudioItemsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
        SeekBar sbProgress;
        ImageView ivPlayPause;
        ImageView iv_del, iv_edit;
        TextView tv_rec_name;

        AudioItemsViewHolder(View itemView) {
            super(itemView);
            ivPlayPause = (ImageView) itemView.findViewById(R.id.iv_play);
            sbProgress = (SeekBar) itemView.findViewById(R.id.seekbar);
            tv_rec_name = itemView.findViewById(R.id.tv_rec_name);
            iv_del = itemView.findViewById(R.id.iv_del);
            iv_edit = itemView.findViewById(R.id.iv_edit);

            sbProgress.setOnSeekBarChangeListener(this);
            ivPlayPause.setOnClickListener(this);
            iv_edit.setOnClickListener(this);
            iv_del.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            switch (v.getId()) {

                case R.id.iv_del:
                    int position = getLayoutPosition();
                    Uri uri = Uri.parse(audioItems.get(position).getRecordingUri());
                    File fdelete = new File(uri.getPath());
                    if (fdelete.exists()) {
                        OpenDeleteDialog(uri, fdelete, position);

                    }
                    break;

                case R.id.iv_play:
                    if (getAdapterPosition() == currentPlayingPosition) {
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.pause();
                        } else {
                            mediaPlayer.start();
                        }
                    } else {
                        currentPlayingPosition = getAdapterPosition();
                        if (mediaPlayer != null) {
                            if (null != playingHolder) {
                                updateNonPlayingView(playingHolder);
                            }
                            mediaPlayer.release();
                        }
                        playingHolder = this;
                        startMediaPlayer(audioItems.get(currentPlayingPosition).getRecordingUri());
                    }
                    updatePlayingView();


                    break;
            }

        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                mediaPlayer.seekTo(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    }

    private void OpenDeleteDialog(final Uri uri, final File fdelete, final int position) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context)
                .setTitle(audioItems.get(position).getFileName())
                .setMessage("Are you sure, you want to delete this file");

        dialog.setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {

                if (fdelete.delete()) {
                    audioItems.remove(audioItems.get(position));
                    System.out.println("file Deleted :" + uri.getPath());
                    notifyDataSetChanged();
                } else {
                    System.out.println("file not Deleted :" + uri.getPath());
                }
                dialog.dismiss();

            }


        });
        final AlertDialog alert = dialog.create();
        alert.show();
    }

    private void startMediaPlayer(String audioResId) {
        Uri uri = Uri.parse(audioResId);

        mediaPlayer = MediaPlayer.create(context, uri);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                releaseMediaPlayer();
            }
        });
        mediaPlayer.start();
    }

    private void releaseMediaPlayer() {
        if (null != playingHolder) {
            updateNonPlayingView(playingHolder);
        }
        mediaPlayer.release();
        mediaPlayer = null;
        currentPlayingPosition = -1;
    }
}

