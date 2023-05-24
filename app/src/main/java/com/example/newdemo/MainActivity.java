package com.example.newdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

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
    String token1 = "007eJxTYDAvfpb8svTmkQts8zl+ye10mN4dGvOxdN/hFaJNLLKMl5wVGJITTc1SzYzNk5OMjU1SDY2TzIzNkizNEy2TLQ0MkxINeztzUhoCGRnSfxuwMDJAIIjPylCWmZ1YzMAAAIkLH+4=";
    String id, status = "1" ;
    private List<ModalClass> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        list = new ArrayList<>();

        android_id =  Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        reference = FirebaseDatabase.getInstance().getReference("Call");

        Map map = new HashMap();
        map.put("id", android_id);
        map.put("status", "0");
        map.put("token", token1);
        reference.child(android_id).setValue(map);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();

                for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                    ModalClass user = dataSnapshot1.getValue(ModalClass.class);
                     list.add(user);

                }
                AdapterClass adapterClass = new AdapterClass(MainActivity.this, list, new AdapterClass.OnItemClickCallback() {
                    @Override
                    public void onItemClick(ModalClass detail) {

                        Map map = new HashMap();
                        map.put("status", "1");
                        Intent intent =  new Intent(MainActivity.this, AudioCallActivity.class);
                        intent.putExtra("otherId", detail.getId());
                        startActivity(intent);

                        reference.child(detail.getId()).updateChildren(map);
                    }
                });
                binding.recyclerView.setAdapter(adapterClass);
                adapterClass.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        binding.videoCall.setOnClickListener(v -> {

            startActivity(new Intent(MainActivity.this, VideoCallActivity.class));

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        reference.child(android_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                 id = snapshot.child("status").getValue(String.class);
                if (status.equals(id))
                {
                 startActivity(new Intent(MainActivity.this, AudioCallActivity.class));
                 finish();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}