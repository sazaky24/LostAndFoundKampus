package com.example.lostandfoundkampus;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class BarangAdapter extends RecyclerView.Adapter<BarangAdapter.BarangViewHolder> {

    private final List<Laporan> laporanList;

    public BarangAdapter(List<Laporan> laporanList) {
        this.laporanList = laporanList;
    }

    @NonNull
    @Override
    public BarangViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_barang, parent, false);
        return new BarangViewHolder(view);
    }

    // Menambahkan SuppressLint untuk menghilangkan warning penggabungan teks
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull BarangViewHolder holder, int position) {
        Laporan laporan = laporanList.get(position);

        holder.tvNama.setText(laporan.nama_barang);
        holder.tvKategori.setText(laporan.status);
        holder.tvLokasiWaktu.setText(laporan.lokasi + " • " + laporan.created_at);

        // Menentukan icon silang merah (hilang) atau centang hijau (ketemu)
        // Berdasarkan status dari Supabase
        if ("Temuan".equalsIgnoreCase(laporan.status)) {
            holder.imgStatus.setImageResource(android.R.drawable.checkbox_on_background);
        } else {
            holder.imgStatus.setImageResource(android.R.drawable.ic_delete);
        }
        // Aksi ketika kotak item barang diklik
        holder.itemView.setOnClickListener(v -> {
            // Membuat jembatan (Intent) untuk pindah ke DetailActivity
            android.content.Intent intent = new android.content.Intent(v.getContext(), DetailActivity.class);

            // Membawa data barang ini ke halaman sebelah
            intent.putExtra("NAMA", laporan.nama_barang);
            intent.putExtra("LOKASI", laporan.lokasi);
            intent.putExtra("STATUS", laporan.status);
            intent.putExtra("TANGGAL", laporan.created_at);
            intent.putExtra("FOTO", laporan.foto_url);

            // Berangkat!
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return laporanList.size();
    }

    // Menambahkan 'public' untuk memperbaiki warning visibility scope
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