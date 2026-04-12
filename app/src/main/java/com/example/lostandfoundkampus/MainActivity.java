package com.example.lostandfoundkampus;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // Menetapkan halaman Home sebagai halaman pertama saat aplikasi dibuka
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment()).commit();
        }

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_profil) {
                selectedFragment = new ProfilFragment();
            } else if (itemId == R.id.nav_lapor) {
                selectedFragment = new LaporFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
                return true;
            }
            return false;
        });
    }

    // Model Class dibiarkan tetap di sini
    public static class Barang {
        public String nama, kategori, lokasi, waktu;
        public boolean isSelesai;

        public Barang(String nama, String kategori, String lokasi, String waktu, boolean isSelesai) {
            this.nama = nama;
            this.kategori = kategori;
            this.lokasi = lokasi;
            this.waktu = waktu;
            this.isSelesai = isSelesai;
        }
    }
}
