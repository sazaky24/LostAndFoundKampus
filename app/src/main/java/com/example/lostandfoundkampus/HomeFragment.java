package com.example.lostandfoundkampus;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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

    // LIST UNTUK SISTEM FILTER
    private List<Laporan> listSemuaData = new ArrayList<>();
    private List<Laporan> listDitampilkan = new ArrayList<>();

    // UI Elements
    private LinearLayout btnFilterTotal, btnFilterBerhasil, btnFilterProses, layoutKosong;
    private TextView tvAngkaTotal, tvAngkaBerhasil, tvAngkaProses;
    private EditText etSearch;

    // Status Filter Saat Ini
    private String filterStatusSaatIni = "Total";
    private String keywordPencarian = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Inisialisasi RecyclerView
        rvBarang = view.findViewById(R.id.rv_barang);
        rvBarang.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BarangAdapter(listDitampilkan);
        rvBarang.setAdapter(adapter);

        // Inisialisasi UI
        btnFilterTotal = view.findViewById(R.id.btn_filter_total);
        btnFilterBerhasil = view.findViewById(R.id.btn_filter_berhasil);
        btnFilterProses = view.findViewById(R.id.btn_filter_proses);
        tvAngkaTotal = view.findViewById(R.id.tv_angka_total);
        tvAngkaBerhasil = view.findViewById(R.id.tv_angka_berhasil);
        tvAngkaProses = view.findViewById(R.id.tv_angka_proses);
        etSearch = view.findViewById(R.id.et_search);

        // KODE BARU: Kenalkan Layout Pesan Kosong
        layoutKosong = view.findViewById(R.id.layout_kosong);

        // Aksi ketika tombol filter diklik
        btnFilterTotal.setOnClickListener(v -> terapkanFilterStatus("Total"));
        btnFilterBerhasil.setOnClickListener(v -> terapkanFilterStatus("Temuan"));
        btnFilterProses.setOnClickListener(v -> terapkanFilterStatus("Hilang"));

        // Aksi ketika user mengetik di kolom pencarian
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                keywordPencarian = s.toString().toLowerCase().trim();
                perbaruiTampilanList();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

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
                    listSemuaData.clear();
                    listSemuaData.addAll(response.body());

                    hitungStatistik(listSemuaData);
                    terapkanFilterStatus("Total");
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

    private void terapkanFilterStatus(String jenisFilter) {
        filterStatusSaatIni = jenisFilter;
        perbaruiTampilanList();
    }

    // FUNGSI UTAMA UNTUK MENYARING DATA
    private void perbaruiTampilanList() {
        listDitampilkan.clear();

        for (Laporan l : listSemuaData) {
            boolean cocokStatus = filterStatusSaatIni.equals("Total") || filterStatusSaatIni.equalsIgnoreCase(l.status);

            // Ingat: Sesuaikan "l.nama_barang" dengan nama variabel di model Laporan.java milikmu
            boolean cocokKeyword = keywordPencarian.isEmpty() ||
                    (l.nama_barang != null && l.nama_barang.toLowerCase().contains(keywordPencarian));

            if (cocokStatus && cocokKeyword) {
                listDitampilkan.add(l);
            }
        }

        // KODE BARU: Tampilkan pesan kosong jika tidak ada data yang cocok
        if (listDitampilkan.isEmpty()) {
            rvBarang.setVisibility(View.GONE); // Sembunyikan daftar
            layoutKosong.setVisibility(View.VISIBLE); // Munculkan pesan kosong
        } else {
            rvBarang.setVisibility(View.VISIBLE); // Munculkan daftar
            layoutKosong.setVisibility(View.GONE); // Sembunyikan pesan kosong
        }

        // Beritahu adapter
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

        if (tvAngkaTotal != null) tvAngkaTotal.setText(String.valueOf(total));
        if (tvAngkaBerhasil != null) tvAngkaBerhasil.setText(String.valueOf(temuan));
        if (tvAngkaProses != null) tvAngkaProses.setText(String.valueOf(hilang));
    }
}