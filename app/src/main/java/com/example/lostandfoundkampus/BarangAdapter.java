package com.example.lostandfoundkampus;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BarangAdapter extends RecyclerView.Adapter<BarangAdapter.BarangViewHolder> {

    private final List<MainActivity.Barang> barangList;

    public BarangAdapter(List<MainActivity.Barang> barangList) {
        this.barangList = barangList;
    }

    @NonNull
    @Override
    public BarangViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_barang, parent, false);
        return new BarangViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull BarangViewHolder holder, int position) {
        MainActivity.Barang barang = barangList.get(position);

        holder.tvNama.setText(barang.nama);
        holder.tvKategori.setText(barang.kategori);
        holder.tvLokasiWaktu.setText(barang.lokasi + " • " + barang.waktu);

        holder.imgStatus.setColorFilter(null);

        if (barang.isSelesai) {
            holder.imgStatus.setImageResource(android.R.drawable.checkbox_on_background);
            holder.imgStatus.setColorFilter(Color.parseColor("#66BB6A")); // hijau soft (lebih enak dilihat)
        } else {
            holder.imgStatus.setImageResource(android.R.drawable.ic_popup_sync);
            holder.imgStatus.setColorFilter(Color.parseColor("#FFC107")); // kuning soft
        }
    }

    @Override
    public int getItemCount() {
        return barangList.size();
    }

    public void updateData(List<MainActivity.Barang> newList) {
        barangList.clear();
        barangList.addAll(newList);
        notifyDataSetChanged();
    }

    public static class BarangViewHolder extends RecyclerView.ViewHolder {
        TextView tvNama, tvKategori, tvLokasiWaktu;
        ImageView imgStatus;

        public BarangViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.tv_nama_barang);
            tvKategori = itemView.findViewById(R.id.tv_kategori);
            tvLokasiWaktu = itemView.findViewById(R.id.tv_lokasi_waktu);
            imgStatus = itemView.findViewById(R.id.img_status);
        }
    }
}
