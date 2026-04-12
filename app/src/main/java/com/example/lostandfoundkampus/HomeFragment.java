package com.example.lostandfoundkampus;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment {

    // Kredensial Supabase
    private static final String SUPABASE_URL = "https://ywifcbceouecogdbytsa.supabase.co/";
    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Inl3aWZjYmNlb3VlY29nZGJ5dHNhIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzU5NzA4MjIsImV4cCI6MjA5MTU0NjgyMn0.YYjfjJtppbXJjRWVqYLGbiocEpvTSrPTOkUMhMKWoow";
    private static final String BEARER_TOKEN = "Bearer " + API_KEY;

    private RecyclerView rvBarang;
    private BarangAdapter adapter;

    // DUA LIST UNTUK SISTEM FILTER
    private List<Laporan> listSemuaData = new ArrayList<>(); // Master data
    private List<Laporan> listDitampilkan = new ArrayList<>(); // Data hasil saringan

    // UI Filter
    private LinearLayout btnFilterTotal, btnFilterBerhasil, btnFilterProses;
    private TextView tvAngkaTotal, tvAngkaBerhasil, tvAngkaProses;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Inisialisasi RecyclerView
        rvBarang = view.findViewById(R.id.rv_barang);
        rvBarang.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BarangAdapter(listDitampilkan); // Adapter menggunakan list saringan
        rvBarang.setAdapter(adapter);

        // Inisialisasi UI Filter (Pastikan ID ini ada di fragment_home.xml kamu)
        btnFilterTotal = view.findViewById(R.id.btn_filter_total);
        btnFilterBerhasil = view.findViewById(R.id.btn_filter_berhasil);
        btnFilterProses = view.findViewById(R.id.btn_filter_proses);

        tvAngkaTotal = view.findViewById(R.id.tv_angka_total);
        tvAngkaBerhasil = view.findViewById(R.id.tv_angka_berhasil);
        tvAngkaProses = view.findViewById(R.id.tv_angka_proses);

        // Aksi ketika tombol filter diklik
        btnFilterTotal.setOnClickListener(v -> terapkanFilter("Total"));
        btnFilterBerhasil.setOnClickListener(v -> terapkanFilter("Temuan"));
        btnFilterProses.setOnClickListener(v -> terapkanFilter("Hilang"));

        ambilDataDariSupabase();

        return view;
    }

    private void ambilDataDariSupabase() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SUPABASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SupabaseApi api = retrofit.create(SupabaseApi.class);

        api.getSemuaLaporan(API_KEY, BEARER_TOKEN).enqueue(new Callback<List<Laporan>>() {
            @Override
            public void onResponse(Call<List<Laporan>> call, Response<List<Laporan>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Simpan ke Master Data
                    listSemuaData.clear();
                    listSemuaData.addAll(response.body());

                    // Hitung dan tampilkan angka di atas layar
                    hitungStatistik(listSemuaData);

                    // Tampilkan "Total" secara default saat aplikasi pertama kali dibuka
                    terapkanFilter("Total");
                } else {
                    Toast.makeText(getContext(), "Gagal memuat data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Laporan>> call, Throwable t) {
                Toast.makeText(getContext(), "Error jaringan: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void terapkanFilter(String jenisFilter) {
        listDitampilkan.clear(); // Bersihkan layar

        if (jenisFilter.equals("Total")) {
            // Tampilkan semua data tanpa terkecuali
            listDitampilkan.addAll(listSemuaData);
        } else {
            // Saring data berdasarkan status (Hilang/Temuan)
            for (Laporan l : listSemuaData) {
                if (jenisFilter.equalsIgnoreCase(l.status)) {
                    listDitampilkan.add(l);
                }
            }
        }

        // Beritahu RecyclerView untuk menggambar ulang list dengan data baru
        adapter.notifyDataSetChanged();
    }

    private void hitungStatistik(List<Laporan> semuaData) {
        int total = semuaData.size();
        int hilang = 0;
        int temuan = 0;

        for (Laporan l : semuaData) {
            if ("Hilang".equalsIgnoreCase(l.status)) {
                hilang++;
            } else if ("Temuan".equalsIgnoreCase(l.status)) {
                temuan++;
            }
        }

        // Tampilkan angka ke TextView
        if (tvAngkaTotal != null) tvAngkaTotal.setText(String.valueOf(total));
        if (tvAngkaBerhasil != null) tvAngkaBerhasil.setText(String.valueOf(temuan));
        if (tvAngkaProses != null) tvAngkaProses.setText(String.valueOf(hilang));
    }
}