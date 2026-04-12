package com.example.lostandfoundkampus;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class LaporFragment extends Fragment {

    private Spinner spKategori;
    private RadioGroup rgStatus;
    private TextView tvLokasiLabel, tvTanggalLabel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_lapor, container, false);

        spKategori = view.findViewById(R.id.sp_kategori);
        rgStatus = view.findViewById(R.id.rg_status);
        tvLokasiLabel = view.findViewById(R.id.tv_lokasi_label);
        tvTanggalLabel = view.findViewById(R.id.tv_tanggal_label);

        // LIST KATEGORI
        String[] kategori = {
                "Pilih kategori",
                "Elektronik",
                "Dokumen",
                "Tas",
                "Kunci",
                "Pakaian",
                "Aksesoris",
                "Lainnya"
        };

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(getContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        kategori);

        spKategori.setAdapter(adapter);

        // STATUS BARANG
        rgStatus.setOnCheckedChangeListener((group, checkedId) -> {

            if (checkedId == R.id.rb_hilang) {

                tvLokasiLabel.setText("Lokasi Terakhir Terlihat *");
                tvTanggalLabel.setText("Tanggal Kehilangan *");

            } else {

                tvLokasiLabel.setText("Lokasi Penemuan *");
                tvTanggalLabel.setText("Tanggal Penemuan *");

            }

        });

        return view;
    }
}