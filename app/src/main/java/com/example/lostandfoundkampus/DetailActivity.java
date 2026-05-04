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

        // Mengambil data yang dikirim dari HomeFragment (Adapter)
        String nama = getIntent().getStringExtra("NAMA");
        String lokasi = getIntent().getStringExtra("LOKASI");
        String status = getIntent().getStringExtra("STATUS");
        String tanggal = getIntent().getStringExtra("TANGGAL");
        String fotoUrl = getIntent().getStringExtra("FOTO");

        // Memasukkan data ke layar
        tvNama.setText(nama);
        tvLokasi.setText(lokasi);
        tvStatus.setText(status);

        // Potong jam agar hanya tampil tanggal
        if (tanggal != null && tanggal.length() >= 10) {
            tvTanggal.setText(tanggal.substring(0, 10));
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
                    .into(imgFoto);
        }
    }
}