package com.example.lostandfoundkampus;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LaporFragment extends Fragment {

    // Masukkan Kredensial Supabase kamu di sini
    private static final String SUPABASE_URL = "https://ywifcbceouecogdbytsa.supabase.co/";
    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Inl3aWZjYmNlb3VlY29nZGJ5dHNhIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzU5NzA4MjIsImV4cCI6MjA5MTU0NjgyMn0.YYjfjJtppbXJjRWVqYLGbiocEpvTSrPTOkUMhMKWoow";
    private static final String BEARER_TOKEN = "Bearer " + API_KEY;

    private ImageView imgPreview;
    private EditText etNamaBarang, etLokasi;
    private RadioGroup rgStatus; // Variabel baru untuk menampung pilihan status
    private byte[] imageBytes = null;
    private String namaFileGambar = "";
    private SupabaseApi api;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lapor, container, false);

        // Inisialisasi UI
        CardView btnTambahFoto = view.findViewById(R.id.btn_tambah_foto);
        imgPreview = view.findViewById(R.id.img_preview);
        etNamaBarang = view.findViewById(R.id.et_nama_barang);
        etLokasi = view.findViewById(R.id.et_lokasi);
        rgStatus = view.findViewById(R.id.rg_status); // Menghubungkan ID RadioGroup
        Button btnSubmit = view.findViewById(R.id.btn_submit);

        // Inisialisasi Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SUPABASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(SupabaseApi.class);

        // Aksi ketika kotak foto diklik
        btnTambahFoto.setOnClickListener(v -> bukaGaleri());

        // Aksi ketika tombol Kirim ditekan
        btnSubmit.setOnClickListener(v -> {
            String nama = etNamaBarang.getText().toString().trim();
            String lokasi = etLokasi.getText().toString().trim();

            // Membaca status yang dipilih pengguna
            String statusTerpilih = "Hilang"; // Ini nilai default
            if (rgStatus.getCheckedRadioButtonId() == R.id.rb_temuan) {
                statusTerpilih = "Temuan";
            }

            if (nama.isEmpty() || lokasi.isEmpty()) {
                Toast.makeText(getContext(), "Nama barang dan lokasi wajib diisi!", Toast.LENGTH_SHORT).show();
                return;
            }

            btnSubmit.setText("Mengirim...");
            btnSubmit.setEnabled(false);

            if (imageBytes != null) {
                // Jika ada foto, upload foto dulu (menyertakan statusTerpilih)
                uploadFotoDanKirimData(nama, lokasi, statusTerpilih, btnSubmit);
            } else {
                // Jika tanpa foto, langsung kirim teks (menyertakan statusTerpilih)
                kirimDataKeDatabase(nama, lokasi, statusTerpilih, null, btnSubmit);
            }
        });

        return view;
    }

    // Fitur Buka Galeri Modern di Android
    private final ActivityResultLauncher<Intent> galeriLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    try {
                        InputStream inputStream = getContext().getContentResolver().openInputStream(imageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        imgPreview.setImageBitmap(bitmap);

                        // Ubah gambar jadi byte array agar bisa dikirim ke server
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                        imageBytes = baos.toByteArray();
                        namaFileGambar = "foto_" + System.currentTimeMillis() + ".jpg";

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
    );

    private void bukaGaleri() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galeriLauncher.launch(intent);
    }

    // Parameter diperbarui untuk menerima statusTerpilih
    private void uploadFotoDanKirimData(String nama, String lokasi, String statusTerpilih, Button btnSubmit) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imageBytes);

        api.uploadFoto(API_KEY, BEARER_TOKEN, "image/jpeg", namaFileGambar, requestFile).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    String fotoUrl = SUPABASE_URL + "storage/v1/object/public/foto_barang/" + namaFileGambar;
                    kirimDataKeDatabase(nama, lokasi, statusTerpilih, fotoUrl, btnSubmit);
                } else {
                    Toast.makeText(getContext(), "Gagal upload foto", Toast.LENGTH_SHORT).show();
                    resetTombol(btnSubmit);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), "Error jaringan saat upload foto", Toast.LENGTH_SHORT).show();
                resetTombol(btnSubmit);
            }
        });
    }

    // Parameter diperbarui untuk menerima statusTerpilih
    private void kirimDataKeDatabase(String nama, String lokasi, String statusTerpilih, String fotoUrl, Button btnSubmit) {
        Laporan laporanBaru = new Laporan();
        laporanBaru.nama_barang = nama;
        laporanBaru.kategori = "Lainnya";
        laporanBaru.lokasi = lokasi;
        laporanBaru.status = statusTerpilih; // Memasukkan data dinamis dari RadioButton
        laporanBaru.foto_url = fotoUrl;

        api.kirimLaporanBaru(API_KEY, BEARER_TOKEN, "return=minimal", laporanBaru).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Laporan berhasil dikirim!", Toast.LENGTH_LONG).show();
                    // Kosongkan form kembali seperti semula
                    etNamaBarang.setText("");
                    etLokasi.setText("");
                    rgStatus.check(R.id.rb_hilang); // Kembalikan centang ke "Kehilangan"
                    imgPreview.setImageResource(android.R.drawable.ic_menu_camera);
                    imageBytes = null;
                } else {
                    Toast.makeText(getContext(), "Gagal menyimpan laporan ke database", Toast.LENGTH_SHORT).show();
                }
                resetTombol(btnSubmit);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Error jaringan saat mengirim data", Toast.LENGTH_SHORT).show();
                resetTombol(btnSubmit);
            }
        });
    }

    private void resetTombol(Button btnSubmit) {
        btnSubmit.setText("Kirim Laporan");
        btnSubmit.setEnabled(true);
    }
}