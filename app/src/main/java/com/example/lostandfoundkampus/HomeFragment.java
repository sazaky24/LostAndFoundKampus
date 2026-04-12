package com.example.lostandfoundkampus;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout; // Import ini jika menggunakan LinearLayout sebagai container

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private List<MainActivity.Barang> barangList;
    private BarangAdapter barangAdapter;
    private RecyclerView rvBarang;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // RecyclerView
        rvBarang = view.findViewById(R.id.rv_barang);
        rvBarang.setLayoutManager(new LinearLayoutManager(getContext()));

        // DATA DUMMY
        barangList = new ArrayList<>();
        barangList.add(new MainActivity.Barang("Dompet Kulit Coklat", "Dokumen", "Gedung Fakultas Teknik", "2 jam yang lalu", false));
        barangList.add(new MainActivity.Barang("Smartphone Samsung Galaxy", "Elektronik", "Perpustakaan Pusat", "5 jam yang lalu", false));
        barangList.add(new MainActivity.Barang("Kunci Motor Honda", "Kunci", "Parkiran Gedung B", "1 hari yang lalu", true));
        barangList.add(new MainActivity.Barang("Tas Ransel Hitam", "Lainnya", "Kantin Kampus", "2 hari yang lalu", false));

        barangAdapter = new BarangAdapter(new ArrayList<>(barangList));
        rvBarang.setAdapter(barangAdapter);

        // --- PERBAIKAN FILTER BERDASARKAN GAMBAR 2 ---

        // 1. Filter Total Laporan (Menampilkan Semua)
        View layoutTotal = view.findViewById(R.id.layout_total);
        if (layoutTotal != null) {
            layoutTotal.setOnClickListener(v -> {
                // Tampilkan semua data asli
                barangAdapter.updateData(new ArrayList<>(barangList));
            });
        }

        // 2. Filter Berhasil (isSelesai = true)
        View layoutBerhasil = view.findViewById(R.id.layout_berhasil);
        if (layoutBerhasil != null) {
            layoutBerhasil.setOnClickListener(v -> filterBarang(false)); // false = Selesai/Berhasil
        }

        // 3. Filter Proses (isSelesai = false)
        View layoutProses = view.findViewById(R.id.layout_proses);
        if (layoutProses != null) {
            layoutProses.setOnClickListener(v -> filterBarang(true)); // true = Masih Proses
        }

        return view;
    }

    // METHOD FILTER (Tetap Sama, Tidak Diubah Strukturnya)
    private void filterBarang(boolean proses) {
        List<MainActivity.Barang> filteredList = new ArrayList<>();

        for (MainActivity.Barang b : barangList) {
            if (proses) {
                if (!b.isSelesai) filteredList.add(b); // PROSES
            } else {
                if (b.isSelesai) filteredList.add(b);  // SELESAI/BERHASIL
            }
        }

        // UPDATE DATA KE ADAPTER
        barangAdapter.updateData(filteredList);
    }
}