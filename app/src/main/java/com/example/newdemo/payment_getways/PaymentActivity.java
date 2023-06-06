package com.example.newdemo.payment_getways;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.example.newdemo.MainActivity;
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
    }
}