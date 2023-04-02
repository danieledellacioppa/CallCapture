package com.example.callcapture;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    TextView dbgConsole;
    Button captureButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        captureButton = findViewById(R.id.captureBtn);
        //give captureButton callReceiverRunnable
        captureButton.setOnClickListener(v -> new MyHandlerThread().execute(new CallReceiverRunnable(this)));

        dbgConsole = findViewById(R.id.dbgConsole);
        dbgConsole.setText("");
        new DebugString("copilotino!", dbgConsole);

    }

    class CallReceiverRunnable implements Runnable
    {
        private AudioManager audioManager;
        private Context context;

        public CallReceiverRunnable(Context context)
        {
            this.context = context;
        }

        @Override
        public void run()
        {
            new DebugString("CallReceiverRunnable running", dbgConsole);
            Toast.makeText(context, "CallReceiverRunnable running", Toast.LENGTH_LONG).show();
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

            // Phone call started
            audioManager.setMode(AudioManager.MODE_IN_CALL);
            audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL), 0);

            MediaRecorder recorder;


//            String mFileName = getExternalCacheDir().getAbsolutePath();
            String fileName ="audiorecordtest.3gp";

            File directory = context.getFilesDir();
            new DebugString(context.getFilesDir().toString(),dbgConsole);
            File outputFile = new File(directory,fileName);

            //show the path of the file
            new DebugString(fileName, dbgConsole);

            int audiopermission = ContextCompat.checkSelfPermission(context,
                    android.Manifest.permission.RECORD_AUDIO);
            List<String> listPermissionsNeeded = new ArrayList<>();

            if (audiopermission != PackageManager.PERMISSION_GRANTED)
            {
                listPermissionsNeeded.add(android.Manifest.permission.RECORD_AUDIO);
            }

            if (!listPermissionsNeeded.isEmpty())
            {
                ActivityCompat.requestPermissions((Activity) context, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 1);
            }
            else
            {

                recorder = new MediaRecorder();
                recorder.reset();
                recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
                recorder.setOutputFile(outputFile);
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                try {
                    recorder.prepare();
                } catch (IOException e) {
                    Log.e("LOG", "prepare() failed");
                }
                recorder.start();
            }






       }

    }



}