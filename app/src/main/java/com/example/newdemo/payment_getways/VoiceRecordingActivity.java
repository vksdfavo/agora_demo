package com.example.newdemo.payment_getways;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

import com.coloros.ocs.base.task.OnSuccessListener;
import com.example.newdemo.R;
import com.example.newdemo.databinding.ActivityVoiceRecordingBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class VoiceRecordingActivity extends AppCompatActivity {
    ActivityVoiceRecordingBinding binding;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference audioRef = database.getReference("audio");
    StorageReference reference = FirebaseStorage.getInstance().getReference("audio");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVoiceRecordingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // showSettingsDialog();


        binding.btn.setOnClickListener(v -> {

            binding.voiceRecoderLottie.setVisibility(View.VISIBLE);
            binding.chronometer.setVisibility(View.VISIBLE);
            binding.voiceRecoderLottie.playAnimation();
            binding.chronometer.start();

            startRecording();
            binding.chronometer.setFormat("Time: %s");
            binding.chronometer.setBase(SystemClock.elapsedRealtime());
        });

        binding.stopBtn.setOnClickListener(v -> {

            binding.voiceRecoderLottie.setVisibility(View.INVISIBLE);
            binding.chronometer.setVisibility(View.INVISIBLE);
            binding.voiceRecoderLottie.cancelAnimation();
            binding.chronometer.stop();

            pauseRecording();

        });
    }

    // creating a variable for media recorder object class.
    private MediaRecorder mRecorder;

    // constant for storing audio permission
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;

    // string variable is created for storing a file name
    private static String mFileName = null;

    private void startRecording() {
        if (CheckPermissions()) {


//            mFileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
                mFileName = getExternalCacheDir().getAbsolutePath();
            }
            mFileName += "/AudioRecording.mp3";

            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            mRecorder.setOutputFile(mFileName);
            try {

                mRecorder.prepare();
            } catch (IOException e) {
                Log.e("TAG", "prepare() failed");
            }

            mRecorder.start();
        } else {
            Toast.makeText(VoiceRecordingActivity.this, "Unchecked permission checked", Toast.LENGTH_SHORT).show();

            RequestPermissions();
        }
    }


    public boolean CheckPermissions() {
        // this method is used to check permission
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void RequestPermissions() {
        ActivityCompat.requestPermissions(VoiceRecordingActivity.this, new String[]{RECORD_AUDIO, WRITE_EXTERNAL_STORAGE}, REQUEST_AUDIO_PERMISSION_CODE);
    }


    public void pauseRecording() {

        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;

            SendFile(mFileName);
        }
    }

    private void SendFile(String mFileName) {

        String filePath = mFileName;

        Long tsLong = System.currentTimeMillis() / 1000;
        String ts = tsLong.toString();

        File audioFile = new File(filePath);
        if (audioFile.exists()) {
            Uri audioUri = Uri.fromFile(audioFile);

            String token = audioRef.push().getKey();
            UploadTask uploadTask = reference.child(ts).putFile(audioUri);

            uploadTask.addOnSuccessListener(taskSnapshot -> reference.getDownloadUrl().addOnSuccessListener(new com.google.android.gms.tasks.OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    if (uploadTask.isSuccessful()) {
                        String audioFile1 = uri.toString();
                        audioRef.child(token).setValue(audioFile1);

                    } else {
                        Toast.makeText(VoiceRecordingActivity.this, "file not uploaded", Toast.LENGTH_SHORT).show();

                    }
                }

            })).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(VoiceRecordingActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();


                }
            });
            // Add your code to handle the file here
        } else {
            // File does not exist, display an error message or take alternative action
            Toast.makeText(getApplicationContext(), "File not found", Toast.LENGTH_SHORT).show();
        }


    }


    private void showSettingsDialog() {
        // we are displaying an alert dialog for permissions
        AlertDialog.Builder builder = new AlertDialog.Builder(VoiceRecordingActivity.this);

        // below line is the title for our alert dialog.
        builder.setTitle("Need Permissions");

        // below line is our message for our dialog
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", (dialog, which) -> {
            // this method is called on click on positive button and on clicking shit button
            // we are redirecting our user from our app to the settings page of our app.
            dialog.cancel();
            // below is the intent from which we are redirecting our user.
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivityForResult(intent, 101);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            // this method is called when user click on negative button.
            dialog.cancel();
        });
        // below line is used to display our dialog
        builder.show();

        builder.show().getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(VoiceRecordingActivity.this, R.color.appcolor));
        builder.show().getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(VoiceRecordingActivity.this, R.color.appcolor));
    }

}