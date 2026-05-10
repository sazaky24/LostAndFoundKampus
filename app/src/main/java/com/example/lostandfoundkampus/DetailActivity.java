package com.example.lostandfoundkampus;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ImageView btnBack = findViewById(R.id.btn_back);
        ImageView imgFoto = findViewById(R.id.img_detail_foto);
        TextView tvStatus = findViewById(R.id.tv_detail_status);
        TextView tvNama = findViewById(R.id.tv_detail_nama);
        TextView tvLokasi = findViewById(R.id.tv_detail_lokasi);
        TextView tvTanggal = findViewById(R.id.tv_detail_tanggal);

        // Aksi tombol kembali
        btnBack.setOnClickListener(v -> finish()); // Menutup halaman ini dan kembali ke Home

        String nama = getIntent().getStringExtra("NAMA");
        String lokasi = getIntent().getStringExtra("LOKASI");
        String status = getIntent().getStringExtra("STATUS");
        String tanggal = getIntent().getStringExtra("TANGGAL");
        String fotoUrl = getIntent().getStringExtra("FOTO");

// 2. Set teks (cek null agar tidak crash)
        tvNama.setText(nama != null ? nama : "Tanpa Nama");
        tvLokasi.setText(lokasi != null ? lokasi : "Lokasi tidak diketahui");
        tvStatus.setText(status != null ? status : "Status...");

// 3. PERBAIKAN TANGGAL (Penyebab utama crash)
        if (tanggal != null && tanggal.length() >= 10) {
            try {
                tvTanggal.setText(tanggal.substring(0, 10));
            } catch (Exception e) {
                tvTanggal.setText(tanggal); // Jika gagal potong, tampilkan aslinya saja
            }
        } else {
            tvTanggal.setText(tanggal != null ? tanggal : "Tanggal tidak tersedia");
        }

        // Ubah warna kotak status
        if ("Hilang".equalsIgnoreCase(status)) {
            tvStatus.setBackgroundColor(Color.parseColor("#FFCDD2")); // Latar Merah Muda
            tvStatus.setTextColor(Color.parseColor("#D32F2F")); // Teks Merah
        } else {
            tvStatus.setBackgroundColor(Color.parseColor("#C8E6C9")); // Latar Hijau Muda
            tvStatus.setTextColor(Color.parseColor("#388E3C")); // Teks Hijau
        }

        // Tampilkan foto menggunakan Glide
        if (fotoUrl != null && !fotoUrl.isEmpty()) {
            Glide.with(this)
                    .load(fotoUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_report_image)
                    .into(imgFoto);
        } else {
            // Jika tidak ada foto, tampilkan gambar default agar tidak crash
            imgFoto.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }
}