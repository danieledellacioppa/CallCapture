# CallCapture

CallCapture is an Android app that allows you to record phone calls on your device by accessing the audio stream that carries the call audio between the two parties. With CallCapture, you can easily capture important conversations, interviews, or meetings for future reference.

## Features

- Record incoming and outgoing phone calls
  * Learn how to access and record audio from the device's microphone and speaker
     + To record phone calls, you'll need to access the audio input and output streams on the device. You can do this using the Android AudioRecord and AudioTrack classes, which allow you to capture audio data from the microphone and play it back through the speaker. You'll need to learn how to use these classes to capture and save audio data to a file.
     + [I'm doing it here](./v10)
- Save recordings in various formats, including AAC, AMR, and WAV
- Rename and delete recordings from within the app
- Share recordings via email, messaging, or other apps
- Choose to automatically record all calls or selectively record certain calls
- Hide the app's icon and prevent it from appearing in the device's app list

## Requirements

- Android 5.0 or higher
- Microphone and speaker access
- Call audio stream access
- File system access

## Installation

1. Download the latest APK file from the releases page.
2. Enable installation of apps from unknown sources in your device settings.
3. Install the APK file on your device.
4. Open the app and follow the on-screen instructions to set up the app.

## Usage

1. Open the app before making or receiving a call.
2. Tap the record button to start recording the call.
3. Tap the stop button to stop recording the call.
4. Find your recordings in the app's recordings list.
5. Tap and hold a recording to rename or delete it.
6. Tap on a recording to listen to it or share it.

## License

This project is licensed under the MIT License - see the LICENSE.md file for details.

## Disclaimer

Recording phone calls without the other person's consent may be illegal in some areas. It is your responsibility to check your local laws and regulations before using this app. The developer assumes no liability for any misuse or legal consequences of using this app.

# CAPTURE_AUDIO_OUTPUT permission
The CAPTURE_AUDIO_OUTPUT permission is a system-level permission that is only granted to system apps. This is because it allows an app to capture audio being played by other apps, which can potentially be used for malicious purposes.

If you need to capture audio output in your app, you may want to consider using the MediaProjection API, which allows your app to capture the device's screen and audio output. However, this requires the user to grant permission for screen recording, which may not be ideal in all situations.

Alternatively, you can prompt the user to manually select the audio source using the AudioManager API, and then record the audio using the MediaRecorder API. This will not allow you to capture audio being played by other apps, but it can be used to record audio from the device's microphone or other input sources.
