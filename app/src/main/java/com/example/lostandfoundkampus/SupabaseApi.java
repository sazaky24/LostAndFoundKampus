package com.example.lostandfoundkampus;

import java.util.List;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SupabaseApi {

    // --- JALUR LAPORAN BARANG ---

    // [KODE LAMA] Mengambil semua laporan
    @GET("rest/v1/laporan?select=*&order=created_at.desc")
    Call<List<Laporan>> getSemuaLaporan(
            @Header("apikey") String apiKey,
            @Header("Authorization") String authToken
    );

    // [KODE LAMA] Menyimpan teks ke tabel laporan (Kirim Laporan Baru)
    @POST("rest/v1/laporan")
    Call<Void> kirimLaporanBaru(
            @Header("apikey") String apiKey,
            @Header("Authorization") String authToken,
            @Header("Prefer") String prefer, // Isi dengan "return=minimal"
            @Body Laporan laporanData
    );

    // [KODE LAMA] Mengunggah file foto ke Supabase Storage (Bucket: foto_barang)
    @POST("storage/v1/object/foto_barang/{nama_file}")
    Call<ResponseBody> uploadFoto(
            @Header("apikey") String apiKey,
            @Header("Authorization") String authToken,
            @Header("Content-Type") String contentType, // Isi dengan "image/jpeg"
            @Path("nama_file") String namaFile,
            @Body RequestBody imageBytes
    );

    // --- JALUR AUTENTIKASI (LOGIN & REGISTER) ---

    // 1. Mendaftar Akun Baru
    @POST("auth/v1/signup")
    Call<AuthResponse> registerUser(
            @Header("apikey") String apiKey,
            @Body AuthRequest authRequest
    );

    // 2. Login Akun
    @POST("auth/v1/token?grant_type=password")
    Call<AuthResponse> loginUser(
            @Header("apikey") String apiKey,
            @Body AuthRequest authRequest
    );

    // --- JALUR PROFIL ---

    // 3. Menyimpan Data Profil Baru
    @POST("rest/v1/profiles")
    Call<Void> buatProfilBaru(
            @Header("apikey") String apiKey,
            @Header("Authorization") String authToken,
            @Header("Prefer") String prefer,
            @Body Profil profilBaru
    );

    // [KODE BARU] Mengambil profil untuk ditampilkan di layar
    @GET("rest/v1/profiles?select=*")
    Call<List<Profil>> getProfilUser(
            @Header("apikey") String apiKey,
            @Header("Authorization") String authToken,
            @Query("profile_id") String profileIdFilter
    );

    // Mengubah/Update Data Profil (Fitur Edit)
    @PATCH("rest/v1/profiles")
    Call<Void> updateProfil(
            @Header("apikey") String apiKey,
            @Header("Authorization") String authToken,
            @Query("profile_id") String profileIdFilter,
            @Body Profil profilUpdate
    );
}