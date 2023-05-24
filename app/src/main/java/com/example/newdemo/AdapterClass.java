package com.example.newdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newdemo.databinding.CallingLayoutBinding;

import java.util.List;

public class AdapterClass extends RecyclerView.Adapter<AdapterClass.ViewHolder> {
    Context context;
    private List<ModalClass> list;
    OnItemClickCallback callback;

    public interface OnItemClickCallback {
        void onItemClick(ModalClass detail);
    }
    public AdapterClass(Context context, List<ModalClass> list, OnItemClickCallback callback) {
        this.context = context;
        this.list = list;
        this.callback = callback;
    }

    @NonNull
    @Override
    public AdapterClass.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(CallingLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull AdapterClass.ViewHolder holder, int position) {

        holder.binding.audioCall1.setText(list.get(position).getId());

        holder.binding.audioCall1.setOnClickListener(v -> {

            callback.onItemClick(list.get(position));
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CallingLayoutBinding binding;

        public ViewHolder(@NonNull CallingLayoutBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
