package com.example.callcapture;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.view.Surface;

import java.io.IOException;
import java.nio.ByteBuffer;

public class ScreenCaptureActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 1;

    private MediaProjectionManager mMediaProjectionManager;
    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;
    private MediaCodec mVideoEncoder;
    private MediaCodec mAudioEncoder;
    private AudioRecord mAudioRecord;
    private boolean mIsRecording;

    private final int AUDIO_SAMPLE_RATE = 44100;
    private final int AUDIO_CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private final int AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_capture);

        mMediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            mMediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data);
            startScreenRecording();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void startScreenRecording() {
        try {
            // Create a virtual display with the size of the device's screen
            mVirtualDisplay = mMediaProjection.createVirtualDisplay("ScreenCapture",
                    getWindowManager().getDefaultDisplay().getWidth(),
                    getWindowManager().getDefaultDisplay().getHeight(),
                    getResources().getDisplayMetrics().densityDpi,
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                    new Surface(mVideoEncoder.createInputSurface()),
                    null,
                    null);

            // Create a video encoder to encode the captured frames
            mVideoEncoder = MediaCodec.createEncoderByType("video/avc");
            MediaFormat videoFormat = MediaFormat.createVideoFormat("video/avc",
                    getWindowManager().getDefaultDisplay().getWidth(),
                    getWindowManager().getDefaultDisplay().getHeight());
            videoFormat.setInteger(MediaFormat.KEY_BIT_RATE, 1000000);
            videoFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 30);
            videoFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
            videoFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 5);
            mVideoEncoder.configure(videoFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            mVideoEncoder.start();

// Create an audio recorder to capture audio from the device's microphone
            int bufferSize = AudioRecord.getMinBufferSize(AUDIO_SAMPLE_RATE, AUDIO_CHANNEL_CONFIG, AUDIO_FORMAT);
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.


                return;
            }
            mAudioRecord = new AudioRecord(AUDIO_SOURCE, AUDIO_SAMPLE_RATE, AUDIO_CHANNEL_CONFIG, AUDIO_FORMAT, bufferSize);

// Create an audio encoder to encode the captured audio
            mAudioEncoder = MediaCodec.createEncoderByType("audio/mp4a-latm");
            MediaFormat audioFormat = MediaFormat.createAudioFormat("audio/mp4a-latm", AUDIO_SAMPLE_RATE, 1);
            audioFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
            audioFormat.setInteger(MediaFormat.KEY_BIT_RATE, 64000);
            mAudioEncoder.configure(audioFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            mAudioEncoder.start();

            mIsRecording = true;
            startRecordingThread();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopScreenRecording() {
        mIsRecording = false;
        try {
            stopRecordingThread();

            mVideoEncoder.stop();
            mVideoEncoder.release();

            mAudioRecord.stop();
            mAudioRecord.release();

            mAudioEncoder.stop();
            mAudioEncoder.release();

            mVirtualDisplay.release();
            mMediaProjection.stop();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void startRecordingThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ByteBuffer[] videoInputBuffers = mVideoEncoder.getInputBuffers();
                    ByteBuffer[] audioInputBuffers = mAudioEncoder.getInputBuffers();

                    MediaCodec.BufferInfo videoBufferInfo = new MediaCodec.BufferInfo();
                    MediaCodec.BufferInfo audioBufferInfo = new MediaCodec.BufferInfo();

                    while (mIsRecording) {
// Capture video frame from the virtual display
                        int videoInputBufferIndex = mVideoEncoder.dequeueInputBuffer(-1);
                        if (videoInputBufferIndex >= 0) {
                            long currentTimeUs = System.nanoTime() / 1000;
                            SurfaceTexture surfaceTexture = mVirtualDisplay.getSurface().getSurfaceTexture();
                            surfaceTexture.updateTexImage();

                            ByteBuffer videoInputBuffer = videoInputBuffers[videoInputBufferIndex];
                            videoInputBuffer.clear();

                            surfaceTexture.getTransformMatrix(videoBufferInfo.presentationTimeUs);
                            surfaceTexture.getTransformMatrix(new float[16]);
                            surfaceTexture.getTransformMatrix(videoBufferInfo.presentationTimeUs);

                            mVideoEncoder.queueInputBuffer(videoInputBufferIndex, 0, videoInputBuffer.capacity(),
                                    currentTimeUs, 0);
                        }

// Capture audio frame from the microphone
                        int audioInputBufferIndex = mAudioEncoder.dequeueInputBuffer(-1);
                        if (audioInputBufferIndex >= 0) {
                            ByteBuffer audioInputBuffer = audioInputBuffers[audioInputBufferIndex];
                            audioInputBuffer.clear();

                            int samplesRead = mAudioRecord.read(audioInputBuffer, audioInputBuffer.capacity());
                            long currentTimeUs = System.nanoTime() / 1000;

                            mAudioEncoder.queueInputBuffer(audioInputBufferIndex, 0, samplesRead * 2,
                                    currentTimeUs, 0);
                        }

// Encode and save the video frame and audio frame
                        int videoOutputBufferIndex = mVideoEncoder.dequeueOutputBuffer(videoBufferInfo, 0);
                        while (videoOutputBufferIndex >= 0) {
                            ByteBuffer videoOutputBuffer = mVideoEncoder.getOutputBuffers()[videoOutputBufferIndex];

                            int audioOutputBufferIndex = mAudioEncoder.dequeueOutputBuffer(audioBufferInfo, 0);
                            while (audioOutputBufferIndex >= 0) {
                                ByteBuffer audioOutputBuffer = mAudioEncoder.getOutputBuffers()[audioOutputBufferIndex];

// Write the encoded audio data to the video frame
                                videoOutputBuffer.position(videoBufferInfo.offset);
                                videoOutputBuffer.limit(videoBufferInfo.offset + videoBufferInfo.size);
                                videoOutputBuffer.put(audioOutputBuffer);

                                mAudioEncoder.releaseOutputBuffer(audioOutputBufferIndex, false);
                                audioOutputBufferIndex = mAudioEncoder.dequeueOutputBuffer(audioBufferInfo, 0);
                            }

// Save the encoded video frame to a file, network stream, etc.
// ...

                            mVideoEncoder.releaseOutputBuffer(videoOutputBufferIndex, false);
                            videoOutputBufferIndex = mVideoEncoder.dequeueOutputBuffer(videoBufferInfo, 0);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void stopRecordingThread() throws InterruptedException {
        mVideoEncoder.signalEndOfInputStream();
        mVideoEncoder.flush();

        int videoOutputBufferIndex = mVideoEncoder.dequeueOutputBuffer(new MediaCodec.BufferInfo(), 0);
        while (videoOutputBufferIndex >= 0) {
            mVideoEncoder.releaseOutputBuffer(videoOutputBufferIndex, false);
            videoOutputBufferIndex = mVideoEncoder.dequeueOutputBuffer(new MediaCodec.BufferInfo(), 0);
        }

        mAudioEncoder.signalEndOfInputStream();
        mAudioEncoder.flush();

        int audioOutputBufferIndex = mAudioEncoder.dequeueOutputBuffer(new MediaCodec.BufferInfo(), 0);
        while (audioOutputBufferIndex >= 0) {
            mAudioEncoder.releaseOutputBuffer(audioOutputBufferIndex, false);
            audioOutputBufferIndex = mAudioEncoder.dequeueOutputBuffer(new MediaCodec.BufferInfo(), 0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopScreenRecording();
    }
}