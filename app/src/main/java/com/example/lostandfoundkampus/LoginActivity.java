package com.example.lostandfoundkampus;

import android.content.Intent;
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

public class LoginActivity extends AppCompatActivity {

    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Inl3aWZjYmNlb3VlY29nZGJ5dHNhIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzU5NzA4MjIsImV4cCI6MjA5MTU0NjgyMn0.YYjfjJtppbXJjRWVqYLGbiocEpvTSrPTOkUMhMKWoow";
    private SupabaseApi api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Cek jika sudah pernah login, langsung lompat ke MainActivity!
        SharedPreferences prefs = getSharedPreferences("APP_PREFS", MODE_PRIVATE);
        if (prefs.getString("ACCESS_TOKEN", null) != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        api = new Retrofit.Builder()
                .baseUrl("https://ywifcbceouecogdbytsa.supabase.co/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(SupabaseApi.class);

        EditText etEmail = findViewById(R.id.et_login_email);
        EditText etPassword = findViewById(R.id.et_login_password);
        Button btnLogin = findViewById(R.id.btn_login);

        findViewById(R.id.tv_ke_register).setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class))
        );

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            btnLogin.setText("Masuk...");
            btnLogin.setEnabled(false);

            api.loginUser(API_KEY, new AuthRequest(email, password)).enqueue(new Callback<AuthResponse>() {
                @Override
                public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {

                        // Simpan Token & ID ke Brankas HP
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("ACCESS_TOKEN", response.body().access_token);
                        editor.putString("USER_ID", response.body().user.id);
                        editor.apply();

                        // Pindah ke Dashboard
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();

                    } else {
                        Toast.makeText(LoginActivity.this, "Email atau Password salah!", Toast.LENGTH_SHORT).show();
                        btnLogin.setText("Masuk");
                        btnLogin.setEnabled(true);
                    }
                }
                @Override
                public void onFailure(Call<AuthResponse> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, "Error Jaringan", Toast.LENGTH_SHORT).show();
                    btnLogin.setText("Masuk");
                    btnLogin.setEnabled(true);
                }
            });
        });
    }
}