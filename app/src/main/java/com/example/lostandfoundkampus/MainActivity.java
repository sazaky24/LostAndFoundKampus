package com.example.lostandfoundkampus;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Memindahkan variabel ke dalam onCreate menyelesaikan warning kuning
        RecyclerView rvBarang = findViewById(R.id.rv_barang);
        rvBarang.setLayoutManager(new LinearLayoutManager(this));

        List<Barang> barangList = new ArrayList<>();
        barangList.add(new Barang("Dompet Kulit Coklat", "Dokumen", "Gedung Fakultas Teknik", "2 jam yang lalu", false));
        barangList.add(new Barang("Smartphone Samsung Galaxy", "Elektronik", "Perpustakaan Pusat", "5 jam yang lalu", false));
        barangList.add(new Barang("Kunci Motor Honda", "Kunci", "Parkiran Gedung B", "1 hari yang lalu", true));
        barangList.add(new Barang("Tas Ransel Hitam", "Lainnya", "Kantin Kampus", "2 hari yang lalu", false));

        BarangAdapter barangAdapter = new BarangAdapter(barangList);
        rvBarang.setAdapter(barangAdapter);
    }

    // Model Class untuk struktur data
    public static class Barang {
        String nama, kategori, lokasi, waktu;
        boolean isSelesai;

        public Barang(String nama, String kategori, String lokasi, String waktu, boolean isSelesai) {
            this.nama = nama;
            this.kategori = kategori;
            this.lokasi = lokasi;
            this.waktu = waktu;
            this.isSelesai = isSelesai;
        }
    }
}