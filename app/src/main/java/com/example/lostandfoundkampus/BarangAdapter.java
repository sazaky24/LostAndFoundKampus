package com.example.lostandfoundkampus;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class BarangAdapter extends RecyclerView.Adapter<BarangAdapter.BarangViewHolder> {

    private final List<Laporan> laporanList;
    // Ganti "uploads" dengan nama bucket yang kamu buat di Supabase Storage
    private final String BASE_URL_STORAGE = "https://ywifcbceouecogdbytsa.supabase.co/storage/v1/object/public/uploads/";

    public BarangAdapter(List<Laporan> laporanList) {
        this.laporanList = laporanList;
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
        Laporan laporan = laporanList.get(position);

        holder.tvNama.setText(laporan.nama_barang);
        holder.tvKategori.setText(laporan.status);
        holder.tvLokasiWaktu.setText(laporan.lokasi + " • " + laporan.created_at);

        // --- PERBAIKAN LOGIKA GAMBAR ---
        String fotoUrl = laporan.foto_url;
        final String fullPath;

        // Cek apakah foto_url sudah berupa link lengkap atau hanya nama file
        if (fotoUrl != null && !fotoUrl.isEmpty()) {
            if (fotoUrl.startsWith("http")) {
                fullPath = fotoUrl;
            } else {
                fullPath = BASE_URL_STORAGE + fotoUrl;
            }

            Glide.with(holder.itemView.getContext())
                    .load(fullPath)
                    .placeholder(android.R.color.darker_gray) // Tampilan saat loading
                    .error(android.R.color.darker_gray)      // Tampilan jika link rusak
                    .centerCrop()
                    .into(holder.imgBarang);
        } else {
            // Jika foto_url null di database, set gambar default
            fullPath = "";
            holder.imgBarang.setImageResource(android.R.color.darker_gray);
        }

        // Icon status (Hilang/Temuan)
        if ("Temuan".equalsIgnoreCase(laporan.status)) {
            holder.imgStatus.setImageResource(android.R.drawable.checkbox_on_background);
        } else {
            holder.imgStatus.setImageResource(android.R.drawable.ic_delete);
        }

        // Intent ke DetailActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), DetailActivity.class);
            intent.putExtra("NAMA", laporan.nama_barang);
            intent.putExtra("LOKASI", laporan.lokasi);
            intent.putExtra("STATUS", laporan.status);
            intent.putExtra("TANGGAL", laporan.created_at);

            // Kirim fullPath agar DetailActivity tidak perlu repot menyusun URL lagi
            intent.putExtra("FOTO", fullPath);

            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return laporanList.size();
    }

    public static class BarangViewHolder extends RecyclerView.ViewHolder {
        TextView tvNama, tvKategori, tvLokasiWaktu;
        ImageView imgStatus;
        ShapeableImageView imgBarang; // Tambahkan ini

        public BarangViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.tv_nama_barang);
            tvKategori = itemView.findViewById(R.id.tv_kategori);
            tvLokasiWaktu = itemView.findViewById(R.id.tv_lokasi_waktu);
            imgStatus = itemView.findViewById(R.id.img_status);
            imgBarang = itemView.findViewById(R.id.img_barang); // Inisialisasi ID dari XML
        }
    }
}