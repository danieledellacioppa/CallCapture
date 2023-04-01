## versione 10
```
java

public class CallReceiver extends BroadcastReceiver {
private AudioManager audioManager;

@Override
public void onReceive(Context context, Intent intent) {
TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

if (intent.getAction().equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
// Phone call started
audioManager.setMode(AudioManager.MODE_IN_CALL);
audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL), 0);
} else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
// Phone call ended
audioManager.setMode(AudioManager.MODE_NORMAL);
}
}
}
}
```

In this code, we are using a `BroadcastReceiver` to listen for changes in the phone call state. When the phone call starts, we set the audio mode to `MODE_IN_CALL` and maximize the volume of the voice call stream. This ensures that the phone call audio is captured at the highest possible volume. When the phone call ends, we set the audio mode back to `MODE_NORMAL`.

Note that this code only captures the phone call audio, but it does not record it. If you want to record the phone call audio, you will need to use a `MediaRecorder` or a similar API to capture and save the audio data.
