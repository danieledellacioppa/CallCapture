package com.example.callcapture;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class CallReceiver extends BroadcastReceiver {
    private AudioManager audioManager;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Toast.makeText(context, "I did notice a Call", Toast.LENGTH_LONG).show();
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        if (intent.getAction().equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED))
        {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

            if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK))
            {
                Toast.makeText(context, "Call just started now!", Toast.LENGTH_LONG).show();
                // Phone call started
                audioManager.setMode(AudioManager.MODE_IN_CALL);
                audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL), 0);
            } else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE))
            {
                // Phone call ended
                audioManager.setMode(AudioManager.MODE_NORMAL);
            }
        }
    }
}