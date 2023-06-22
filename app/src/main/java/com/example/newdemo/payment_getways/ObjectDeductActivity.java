package com.example.newdemo.payment_getways;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.newdemo.databinding.ActivityObjectDeductBinding;

import ai.api.AIDataService;

public class ObjectDeductActivity extends AppCompatActivity {
    ActivityObjectDeductBinding binding;
    private AIDataService aiDataService;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityObjectDeductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }
}