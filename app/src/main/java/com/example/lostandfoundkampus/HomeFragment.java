package com.example.lostandfoundkampus;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Menghubungkan layout fragment_home.xml
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Inisialisasi RecyclerView dari dalam Fragment
        RecyclerView rvBarang = view.findViewById(R.id.rv_barang);
        rvBarang.setLayoutManager(new LinearLayoutManager(getContext()));

        // Menambahkan data dummy
        List<MainActivity.Barang> barangList = new ArrayList<>();
        barangList.add(new MainActivity.Barang("Dompet Kulit Coklat", "Dokumen", "Gedung Fakultas Teknik", "2 jam yang lalu", false));
        barangList.add(new MainActivity.Barang("Smartphone Samsung Galaxy", "Elektronik", "Perpustakaan Pusat", "5 jam yang lalu", false));
        barangList.add(new MainActivity.Barang("Kunci Motor Honda", "Kunci", "Parkiran Gedung B", "1 hari yang lalu", true));
        barangList.add(new MainActivity.Barang("Tas Ransel Hitam", "Lainnya", "Kantin Kampus", "2 hari yang lalu", false));

        BarangAdapter barangAdapter = new BarangAdapter(barangList);
        rvBarang.setAdapter(barangAdapter);

        return view;
    }
}