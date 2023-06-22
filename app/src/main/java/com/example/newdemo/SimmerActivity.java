package com.example.newdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import com.example.newdemo.databinding.ActivitySimmerBinding;
import com.faltenreich.skeletonlayout.Skeleton;
import com.faltenreich.skeletonlayout.SkeletonLayoutUtils;


public class SimmerActivity extends AppCompatActivity {
    ActivitySimmerBinding binding;
    private Skeleton skeleton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySimmerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        skeleton = findViewById(R.id.skeletonLayout);


        SimmerAdapter adapter = new SimmerAdapter(this);
        binding.recyclerView.setAdapter(adapter);


        skeleton = SkeletonLayoutUtils.applySkeleton(binding.recyclerView, R.layout.message_layout);
        skeleton.showSkeleton();

    }

    private void onDataLoaded() {
        skeleton.showOriginal();
    }
}