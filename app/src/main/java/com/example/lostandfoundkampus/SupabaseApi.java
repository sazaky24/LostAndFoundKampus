package com.example.lostandfoundkampus;

import java.util.List;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface SupabaseApi {
    // [KODE LAMA] Mengambil semua laporan
    @GET("rest/v1/laporan?select=*&order=created_at.desc")
    Call<List<Laporan>> getSemuaLaporan(
            @Header("apikey") String apiKey,
            @Header("Authorization") String authToken
    );

    // [KODE BARU] Menyimpan teks ke tabel laporan (Kirim Laporan Baru)
    @POST("rest/v1/laporan")
    Call<Void> kirimLaporanBaru(
            @Header("apikey") String apiKey,
            @Header("Authorization") String authToken,
            @Header("Prefer") String prefer, // Isi dengan "return=minimal"
            @Body Laporan laporanData
    );

    // [KODE BARU] Mengunggah file foto ke Supabase Storage (Bucket: foto_barang)
    @POST("storage/v1/object/foto_barang/{nama_file}")
    Call<ResponseBody> uploadFoto(
            @Header("apikey") String apiKey,
            @Header("Authorization") String authToken,
            @Header("Content-Type") String contentType, // Isi dengan "image/jpeg"
            @Path("nama_file") String namaFile,
            @Body RequestBody imageBytes
    );
}