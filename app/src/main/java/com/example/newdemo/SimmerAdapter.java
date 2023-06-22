package com.example.newdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newdemo.databinding.MessageLayoutBinding;

public class SimmerAdapter extends RecyclerView.Adapter<SimmerAdapter.ViewHolder> {
    private Context context;

    public SimmerAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public SimmerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SimmerAdapter.ViewHolder(MessageLayoutBinding.inflate(LayoutInflater.from(context), parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull SimmerAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        MessageLayoutBinding binding;

        public ViewHolder(@NonNull MessageLayoutBinding itemView) {
            super(itemView.getRoot());

            binding = itemView;
        }
    }
}
