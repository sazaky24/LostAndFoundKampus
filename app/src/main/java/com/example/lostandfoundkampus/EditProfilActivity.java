package com.example.lostandfoundkampus;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditProfilActivity extends AppCompatActivity {

    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Inl3aWZjYmNlb3VlY29nZGJ5dHNhIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzU5NzA4MjIsImV4cCI6MjA5MTU0NjgyMn0.YYjfjJtppbXJjRWVqYLGbiocEpvTSrPTOkUMhMKWoow";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profil);

        EditText etNama = findViewById(R.id.et_edit_nama);
        EditText etNim = findViewById(R.id.et_edit_nim);
        EditText etFakultas = findViewById(R.id.et_edit_fakultas);
        EditText etHp = findViewById(R.id.et_edit_hp);
        Button btnSimpan = findViewById(R.id.btn_simpan_profil);

        SupabaseApi api = new Retrofit.Builder()
                .baseUrl("https://ywifcbceouecogdbytsa.supabase.co/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(SupabaseApi.class);

        btnSimpan.setOnClickListener(v -> {
            btnSimpan.setText("Menyimpan...");
            btnSimpan.setEnabled(false);

            SharedPreferences prefs = getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE);
            String userId = prefs.getString("USER_ID", "");
            String token = prefs.getString("ACCESS_TOKEN", "");

            if (userId.isEmpty() || token.isEmpty()) {
                Toast.makeText(this, "Sesi tidak valid, silakan Login ulang", Toast.LENGTH_SHORT).show();
                btnSimpan.setText("Simpan Perubahan");
                btnSimpan.setEnabled(true);
                return;
            }

            // Siapkan data editan
            Profil dataUpdate = new Profil();
            dataUpdate.full_name = etNama.getText().toString();
            dataUpdate.nim = etNim.getText().toString();
            dataUpdate.fakultas = etFakultas.getText().toString();
            dataUpdate.phone = etHp.getText().toString();

            // MENGGUNAKAN JALUR UPDATE NORMAL (PATCH)
            api.updateProfil(API_KEY, "Bearer " + token, "eq." + userId, dataUpdate).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(EditProfilActivity.this, "Profil Berhasil Diperbarui!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        try {
                            String pesanError = response.errorBody() != null ? response.errorBody().string() : "Unknown Error";
                            Toast.makeText(EditProfilActivity.this, "Gagal: " + pesanError, Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        btnSimpan.setText("Simpan Perubahan");
                        btnSimpan.setEnabled(true);
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(EditProfilActivity.this, "Koneksi Error", Toast.LENGTH_SHORT).show();
                    btnSimpan.setText("Simpan Perubahan");
                    btnSimpan.setEnabled(true);
                }
            });
        });
    }
}