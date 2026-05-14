package com.isteserif.dreamora.data.remote

    // API'ye göndereceğimiz istek formatı (Rüya Metni)
    data class GeminiRequest(
        val contents: List<Content>
    )

    data class Content(
        val parts: List<Part>
    )

    data class Part(
        val text: String
    )

    // API'den bize gelecek cevap formatı (Rüya Analizi)
    data class GeminiResponse(
        val candidates: List<Candidate>?
    )

    data class Candidate(
        val content: Content?
    )