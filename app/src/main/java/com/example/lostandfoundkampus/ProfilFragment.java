package com.example.lostandfoundkampus;

import android.app.AlertDialog; // Tambahan Import untuk Pop-up
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProfilFragment extends Fragment {

    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Inl3aWZjYmNlb3VlY29nZGJ5dHNhIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzU5NzA4MjIsImV4cCI6MjA5MTU0NjgyMn0.YYjfjJtppbXJjRWVqYLGbiocEpvTSrPTOkUMhMKWoow";
    private SupabaseApi api;

    // Variabel Global agar bisa diakses di berbagai method
    private TextView tvNama, tvNim, tvFakultas;
    private String userId, token;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profil, container, false);

        // 1. Kenalkan elemen desain
        tvNama = view.findViewById(R.id.tv_profil_nama);
        tvNim = view.findViewById(R.id.tv_profil_nim);
        tvFakultas = view.findViewById(R.id.tv_profil_fakultas);
        Button btnLogout = view.findViewById(R.id.btn_logout);
        Button btnKeEdit = view.findViewById(R.id.btn_ke_edit);

        // 2. Ambil Kunci & ID dari Brankas
        SharedPreferences prefs = requireActivity().getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE);
        userId = prefs.getString("USER_ID", "");
        token = prefs.getString("ACCESS_TOKEN", "");

        // 3. Siapkan Retrofit
        api = new Retrofit.Builder()
                .baseUrl("https://ywifcbceouecogdbytsa.supabase.co/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(SupabaseApi.class);

        // 4. Perintah Klik Tombol Edit
        btnKeEdit.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfilActivity.class);
            startActivity(intent);
        });

        // 5. Perintah Klik Tombol Keluar dengan Konfirmasi Pop-up
        btnLogout.setOnClickListener(v -> {
            // Membuat dan menampilkan Pop-up Konfirmasi
            new AlertDialog.Builder(requireContext())
                    .setTitle("Konfirmasi Keluar")
                    .setMessage("Apakah kamu yakin ingin keluar dari akun ini?")
                    .setPositiveButton("Ya, Keluar", (dialog, which) -> {
                        // Logika jika tombol "Ya" diklik (Menghapus sesi & pindah ke Login)
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.clear();
                        editor.apply();

                        Intent intent = new Intent(requireActivity(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    })
                    .setNegativeButton("Batal", (dialog, which) -> {
                        // Logika jika tombol "Batal" diklik (Tutup pop-up saja)
                        dialog.dismiss();
                    })
                    .show();
        });

        return view;
    }

    // Menggunakan onResume agar data diperbarui otomatis setelah selesai edit
    @Override
    public void onResume() {
        super.onResume();
        tarikDataProfil();
    }

    private void tarikDataProfil() {
        if (!userId.isEmpty() && !token.isEmpty()) {
            tvNama.setText("Memuat Nama...");

            api.getProfilUser(API_KEY, "Bearer " + token, "eq." + userId).enqueue(new Callback<java.util.List<Profil>>() {
                @Override
                public void onResponse(Call<java.util.List<Profil>> call, Response<java.util.List<Profil>> response) {
                    if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                        Profil profilSaya = response.body().get(0);

                        tvNama.setText(profilSaya.full_name != null ? profilSaya.full_name : "Nama Belum Diisi");
                        tvNim.setText(profilSaya.nim != null ? "NIM : " + profilSaya.nim : "NIM Belum Diisi");
                        tvFakultas.setText(profilSaya.fakultas != null ? profilSaya.fakultas : "Fakultas Belum Diisi");
                    } else {
                        tvNama.setText("Belum ada data");
                        tvNim.setText("Silakan klik Edit untuk melengkapi");
                        tvFakultas.setText("");
                    }
                }

                @Override
                public void onFailure(Call<java.util.List<Profil>> call, Throwable t) {
                    tvNama.setText("Koneksi Error");
                }
            });
        }
    }
}