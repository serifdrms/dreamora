package com.isteserif.dreamora.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    // Gemini API'sinin ana adresi
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    // Ajanımız: Giden ve gelen tüm verileri Android Studio'da (Logcat) bize gösterecek
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // BODY demek: Sadece başlıkları değil, rüya metninin kendisini de göster demek
    }

    // İnternet tarayıcımız (OkHttp) ajanımızla birlikte donatılıyor
    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    // Ve işte arabamız (Retrofit Motoru)
    val geminiApi: GeminiApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient) // Ajanımız (logging) burada devrede olmalı
            .build()
            .create(GeminiApi::class.java)
    }
}