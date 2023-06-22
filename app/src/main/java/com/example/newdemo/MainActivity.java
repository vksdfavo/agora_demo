package com.example.newdemo;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.newdemo.databinding.ActivityMainBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    DatabaseReference reference;
    private String android_id;
    String id, status = "1";
    private List<ModalClass> list;
    String channelId, other_token, channel;
    ForegroundService foregroundService;

    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
            android.Manifest.permission.BLUETOOTH_SCAN,
            android.Manifest.permission.BLUETOOTH_CONNECT,
            android.Manifest.permission.BLUETOOTH_PRIVILEGED
    };
    private static String[] PERMISSIONS_LOCATION = {
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
            android.Manifest.permission.BLUETOOTH_SCAN,
            android.Manifest.permission.BLUETOOTH_CONNECT,
            android.Manifest.permission.BLUETOOTH_PRIVILEGED
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        checkPermissions();


//        Intent intent = getIntent();
//        if (intent != null && intent.getAction() != null && intent.getAction().equals(ConstantsStep.REJECT)) {
//
//            Toast.makeText(this, "reject", Toast.LENGTH_SHORT).show();
//            stopService();
//        }else if (intent != null && intent.getAction() != null && intent.getAction().equals(ConstantsStep.ACCEPT))
//        {
//
//            Toast.makeText(this, "Accept", Toast.LENGTH_SHORT).show();
//
//        }

        list = new ArrayList<>();

        android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        reference = FirebaseDatabase.getInstance().getReference("Call");

        Map map = new HashMap();
        map.put("id", android_id);
        map.put("status", "0");
        reference.child(android_id).setValue(map);

        binding.audioCall1.setOnClickListener(v -> {
            startService();

            String channelId = AgoraChannelIdGenerator.generateChannelId();

        });


        binding.videoCall.setOnClickListener(v -> {

            stopService();

        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();

                for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                    ModalClass user = dataSnapshot1.getValue(ModalClass.class);
                    list.add(user);

                }
                AdapterClass adapterClass = new AdapterClass(MainActivity.this, list, detail -> {
                    channelId = AgoraChannelIdGenerator.generateChannelId();

//                    Map map1 = new HashMap();
//                    map1.put("status", "1");
//                    map1.put("callingFrom", android_id);
//                    map1.put("callingTo", detail.getId());
//                    map1.put("channel", channelId);
//                    Intent intent = new Intent(MainActivity.this, AudioCallActivity.class);
//                    intent.putExtra("otherId", detail.getId());
//                    intent.putExtra("token", channelId);
//                    startActivity(intent);
//                    other_token =detail.getId();

                    startService();


                    // reference.child(detail.getId()).updateChildren(map1);
                });
                binding.recyclerView.setAdapter(adapterClass);
                adapterClass.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
//        binding.videoCall.setOnClickListener(v -> {
//
//            startActivity(new Intent(MainActivity.this, VideoCallActivity.class));
//
//        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null && intent.getAction() != null && intent.getAction().equals(ConstantsStep.REJECT)) {
            String extraValue = intent.getStringExtra("vikas");
            binding.audioCall1.setText(extraValue);
            Toast.makeText(this, "Action Clicked with extra: " + extraValue, Toast.LENGTH_SHORT).show();
        }
    }


    public void startService() {
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        serviceIntent.putExtra("inputExtra", "incoming voice call");
        ContextCompat.startForegroundService(this, serviceIntent);
    }

    public void stopService() {
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        stopService(serviceIntent);
    }

    private void checkPermissions() {
        int permission1 = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission2 = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_SCAN);
        if (permission1 != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, 1);
        } else if (permission2 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_LOCATION, 1
            );
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        reference.child(android_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                id = snapshot.child("status").getValue(String.class);
                channel = snapshot.child("channel").getValue(String.class);
                if (status.equals(id)) {
                    Intent intent = new Intent(MainActivity.this, AudioCallActivity.class);
                    intent.putExtra("otherId", other_token);
                    intent.putExtra("token", channel);
                    startActivity(intent);
                    finish();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}