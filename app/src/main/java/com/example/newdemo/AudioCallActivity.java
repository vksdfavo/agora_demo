package com.example.newdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.newdemo.databinding.ActivityAudioCallBinding;
import com.example.newdemo.services.StepService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;

public class AudioCallActivity extends AppCompatActivity {
    ActivityAudioCallBinding binding;
    private RtcEngine mRtcEngine;
    private boolean mMuted, mSpeakerOn;
    private PowerManager.WakeLock wakeLock;
    private AudioManager audioManager;
    private SensorManager mSensorManager;
    private Sensor mProximity;
    private String appId = "ca56e637cb334e13b636b97a9c901ba1";
    private String token ="007eJxTYDAvfpb8svTmkQts8zl+ye10mN4dGvOxdN/hFaJNLLKMl5wVGJITTc1SzYzNk5OMjU1SDY2TzIzNkizNEy2TLQ0MkxINeztzUhoCGRnSfxuwMDJAIIjPylCWmZ1YzMAAAIkLH+4=";
    private String channelName = "vikas";
    DatabaseReference reference;
    private String android_id,status, otherId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAudioCallBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        android_id =  Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        reference = FirebaseDatabase.getInstance().getReference("Call");

        otherId = getIntent().getStringExtra("otherId");
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO, 1)) {
            initAgoraEngineAndJoinChannel();
        }


        senseProximity();
    }


    private void senseProximity() {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(0x00000020, getLocalClassName());
        SensorEventListener sens = new SensorEventListener() {
            @SuppressLint("WakelockTimeout")
            @Override
            public void onSensorChanged(SensorEvent event) {

                if (event.values[0] < mProximity.getMaximumRange()) {
                    wakeLock.acquire();
                }

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        mSensorManager.registerListener(sens, mProximity, 2 * 1000 * 1000);

    }

    private void initAgoraEngineAndJoinChannel() {
        initializeAgoraEngine();
        joinChannel();
    }

    public boolean checkSelfPermission(String permission, int requestCode) {

        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
            return false;
        }
        return true;
    }

    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {

        @Override
        public void onUserJoined(int uid, int elapsed) {
            super.onUserJoined(uid, elapsed);
            runOnUiThread(() -> {
//                    startCallimgTime();
            });
        }


        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            super.onJoinChannelSuccess(channel, uid, elapsed);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                }
            });

        }

        @Override
        public void onUserOffline(final int uid, final int reason) {
            runOnUiThread(() -> {
//                    onRemoteUserLeft(uid, reason);
                finish();
            });
        }

        @Override
        public void onUserMuteAudio(final int uid, final boolean muted) {
            runOnUiThread(() -> {
//                    onRemoteUserVoiceMuted(uid, muted);
            });
        }
    };

    private void initializeAgoraEngine() {
        try {

            RtcEngine mRtcEngine = RtcEngine.create(getBaseContext(), appId, mRtcEventHandler);
            mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_COMMUNICATION);
        } catch (Exception e) {
            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }

    private void joinChannel() {
        initializeEngine();
        String callToken = token;

        mRtcEngine.joinChannel(callToken, channelName, "Extra Optional Data", 0);
    }

    private void initializeEngine() {
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), appId, mRtcEventHandler);
        } catch (Exception e) {
            Log.e("agora", Log.getStackTraceString(e));
            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }

    public void onLocalAudioMuteClicked(View view) {
        mMuted = !mMuted;
        mRtcEngine.muteLocalAudioStream(mMuted);
        int res = mMuted ? R.drawable.baseline_volume_mute_24 : R.drawable.baseline_volume_off_24;
        binding.btnMute.setImageResource(res);
    }

    public void endCall(View view) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("End Call");
        builder1.setMessage("Are you sure you want to End Call ?");
        builder1.setIcon(R.drawable.baseline_call_end_24);
        builder1.setCancelable(false);

        builder1.setPositiveButton("End Call", (dialog, id) -> {
            finish();
            mRtcEngine.leaveChannel();
            Map map = new HashMap();
            map.put("status", "0");
            if (!android_id.equals(otherId))
            {
                reference.child(otherId).updateChildren(map);

            }else {
                reference.child(android_id).updateChildren(map);
            }



            dialog.cancel();
        });

        builder1.setNegativeButton("Dismiss", (dialog, id) -> dialog.cancel());

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    @SuppressLint("WrongConstant")
    public void onSpeakerClicked(View view) {
        mSpeakerOn = !mSpeakerOn;
        // Stops/Resumes sending the local audio stream.
        if (!mSpeakerOn) {
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            audioManager.setMode(AudioManager.MODE_IN_CALL);
            audioManager.setSpeakerphoneOn(true);
            binding.btnSpeaker.setImageResource(R.drawable.baseline_speaker_phone_24);
        } else {
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            audioManager.setMode(AudioManager.ROUTE_SPEAKER);
            audioManager.setSpeakerphoneOn(false);
            binding.btnSpeaker.setImageResource(R.drawable.baseline_speaker_phone_24);
        }

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("End Call");
        builder1.setMessage("End call before you exit");
        builder1.setIcon(R.drawable.baseline_warning_24);
        builder1.setCancelable(false);
        builder1.setPositiveButton("End Call", (dialog, id) -> {

            finish();
            mRtcEngine.leaveChannel();
            dialog.cancel();


        });

        builder1.setNegativeButton("Dismiss", (dialog, id) -> dialog.cancel());
        AlertDialog alert11 = builder1.create();
        alert11.show();

    }

    @Override
    protected void onPause() {
        super.onPause();

        mRtcEngine.leaveChannel();
//        finish();
    }
}
