package com.example.newdemo.payment_getways;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;

import com.example.newdemo.OpenCameraActivity;
import com.example.newdemo.SimmerActivity;
import com.example.newdemo.SplashActivity;
import com.example.newdemo.TextToSpeechActivity;
import com.example.newdemo.databinding.ActivityPaymentBinding;

public class PaymentActivity extends AppCompatActivity {
    ActivityPaymentBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.stripeBtn.setOnClickListener(v -> {

            startActivity(new Intent(PaymentActivity.this, StripeActivity.class));
        });

        binding.payBtn.setOnClickListener(v -> {

            Intent intent = new Intent(PaymentActivity.this, PayUActivity.class);
            startActivity(intent);

        });

        binding.recordingBtn.setOnClickListener(v -> {

            Intent intent = new Intent(PaymentActivity.this, VoiceRecordingActivity.class);
            startActivity(intent);

        });
        binding.numberBtn.setOnClickListener(v -> {

            Intent intent = new Intent(PaymentActivity.this, SplashActivity.class);
            startActivity(intent);

        });

        binding.videoBtn.setOnClickListener(v -> {

            Intent intent = new Intent(PaymentActivity.this, VideoPlayerActivity.class);
            startActivity(intent);

        });

        binding.txtToSpeechBtn.setOnClickListener(v -> {

            Intent intent = new Intent(PaymentActivity.this, TextToSpeechActivity.class);
            startActivity(intent);

        });

        binding.txtToOpenCamera.setOnClickListener(v -> {

            Intent intent = new Intent(PaymentActivity.this, OpenCameraActivity.class);
            startActivity(intent);

        });

        binding.aiChat.setOnClickListener(v -> {

            Intent intent = new Intent(PaymentActivity.this, SimmerActivity.class);
            startActivity(intent);

        });
    }
}