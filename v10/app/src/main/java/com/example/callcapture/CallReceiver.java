package com.example.callcapture;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import java.io.IOException;

public class CallReceiver extends BroadcastReceiver {
//    private AudioManager audioManager;


    private MediaRecorder recorder;
    private boolean isRecording = false;



//    @Override
//    public void onReceive(Context context, Intent intent)
//    {
//        Toast.makeText(context, "I did notice a Call", Toast.LENGTH_LONG).show();
//        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
//
//        if (intent.getAction().equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED))
//        {
//            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
//
//            if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK))
//            {
//                Toast.makeText(context, "Call just started now!", Toast.LENGTH_LONG).show();
//                // Phone call started
//                audioManager.setMode(AudioManager.MODE_IN_CALL);
//                audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL), 0);
//            } else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE))
//            {
//                // Phone call ended
//                audioManager.setMode(AudioManager.MODE_NORMAL);
//            }
//        }
//    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

        if (state.equals(TelephonyManager.EXTRA_STATE_RINGING))
        {
        // Handle incoming call
        } else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK))
        {
        // Handle outgoing call or call answered
            startRecording();
        } else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE))
        {
        // Handle call ended
            stopRecording();
        }
    }

    private void startRecording()
    {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(getFilePath());
        try {
            recorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        recorder.start();
        isRecording = true;
    }

    private void stopRecording() {
        if (isRecording) {
            recorder.stop();
            recorder.release();
            isRecording = false;
        }
    }

    private String getFilePath() {
// Create a file path for the recorded audio file
        return Environment.getExternalStorageDirectory().getPath()
                + "/recorded_call_" + System.currentTimeMillis() + ".3gp";
    }
}