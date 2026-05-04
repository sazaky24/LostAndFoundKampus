package com.example.lostandfoundkampus;

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

public class RegisterActivity extends AppCompatActivity {

    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Inl3aWZjYmNlb3VlY29nZGJ5dHNhIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzU5NzA4MjIsImV4cCI6MjA5MTU0NjgyMn0.YYjfjJtppbXJjRWVqYLGbiocEpvTSrPTOkUMhMKWoow";
    private SupabaseApi api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        api = new Retrofit.Builder()
                .baseUrl("https://ywifcbceouecogdbytsa.supabase.co/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(SupabaseApi.class);

        // Hanya butuh Email dan Password
        EditText etEmail = findViewById(R.id.et_reg_email);
        EditText etPassword = findViewById(R.id.et_reg_password);
        Button btnRegister = findViewById(R.id.btn_register);

        findViewById(R.id.tv_ke_login).setOnClickListener(v -> finish()); // Kembali ke Login

        btnRegister.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if(email.isEmpty() || password.length() < 6) {
                Toast.makeText(this, "Email tidak valid atau Password kurang dari 6", Toast.LENGTH_SHORT).show();
                return;
            }

            btnRegister.setText("Memproses...");
            btnRegister.setEnabled(false);

            // Murni hanya mendaftarkan akun (Auth)
            api.registerUser(API_KEY, new AuthRequest(email, password)).enqueue(new Callback<AuthResponse>() {
                @Override
                public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        // Jika berhasil, langsung suruh cek email! (Karena profil sudah diurus otomatis oleh Trigger Supabase)
                        Toast.makeText(RegisterActivity.this, "Berhasil Daftar! CEK EMAIL ANDA untuk verifikasi.", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        // Membaca alasan sebenarnya jika gagal Auth (misal email sudah pernah dipakai)
                        try {
                            String pesanError = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                            Toast.makeText(RegisterActivity.this, "Gagal Daftar: " + pesanError, Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        btnRegister.setText("Daftar Sekarang");
                        btnRegister.setEnabled(true);
                    }
                }

                @Override
                public void onFailure(Call<AuthResponse> call, Throwable t) {
                    Toast.makeText(RegisterActivity.this, "Koneksi terputus saat mendaftar", Toast.LENGTH_SHORT).show();
                    btnRegister.setText("Daftar Sekarang");
                    btnRegister.setEnabled(true);
                }
            });
        });
    }
}