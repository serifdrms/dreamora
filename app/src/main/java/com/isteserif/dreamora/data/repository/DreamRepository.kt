package com.isteserif.dreamora.data.repository

import com.isteserif.dreamora.BuildConfig
import com.isteserif.dreamora.data.remote.ApiClient
import com.isteserif.dreamora.data.remote.Content
import com.isteserif.dreamora.data.remote.GeminiRequest
import com.isteserif.dreamora.data.remote.Part
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DreamRepository {

    // Az önce ürettiğimiz internet motorunu (ApiClient) buraya alıyoruz
    private val api = ApiClient.geminiApi

    // Bu fonksiyon ViewModel tarafından çağrılacak. İçine kullanıcının yazdığı rüyayı alacak.
    suspend fun getDreamAnalysis(dreamText: String): Result<String> {

        // İnternet işlemlerini ana akışı dondurmamak için Arka Planda (IO) yapıyoruz
        return withContext(Dispatchers.IO) {
            try {
                // 1. Önce kullanıcının düz metnini ve Yapay Zeka kişiliğini API'nin istediği formata (GeminiRequest) çeviriyoruz
                val prompt = "Sen gizemli, bilge ve edebi konuşan bir rüya tabircisisin. Kullanıcının sana anlattığı şu rüyayı mistik bir dille, detaylıca analiz et: $dreamText"

                val request = GeminiRequest(
                    contents = listOf(
                        Content(
                            parts = listOf(Part(text = prompt))
                        )
                    )
                )

                // 2. Motoru çalıştırıp şifremizle birlikte rüyayı API'ye yolluyoruz
                val response = api.analyzeDream(
                    apiKey = BuildConfig.GEMINI_API_KEY,
                    request = request
                )

                // 3. Gelen cevabı kontrol ediyoruz
                if (response.isSuccessful) {
                    // Cevabın içindeki o uzun hiyerarşiyi (candidates -> content -> parts -> text) kazıyıp sadece yazıyı alıyoruz
                    val responseText = response.body()?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text

                    if (responseText != null) {
                        Result.success(responseText) // İşlem Başarılı!
                    } else {
                        Result.failure(Exception("API yorum yapamadı, boş cevap döndürdü."))
                    }
                } else {
                    Result.failure(Exception("API Hatası: ${response.code()}"))
                }

            } catch (e: Exception) {
                // İnternet kopması, zaman aşımı gibi çökmeleri yakalıyoruz
                Result.failure(e)
            }
        }
    }
}